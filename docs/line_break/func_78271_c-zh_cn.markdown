# `func_78271_c` (`FontRenderer::listFormattedStringToWidth`) 背后的故事

`func_78271_c` 是 `FontRenderer` 类下的一个方法，用于将字符串切割成小于一定宽度的若干字符串。它主要用于处理书与笔、工具提示等场景下的长文本换行问题。本文将讨论此方法的实现细节——它具体的行为、它的局限、以及改进的突破口。

## 动机
为什么会有这样一个方法？因为长文本。想想这篇文章：如果不是换行，所有的文本都将会在几行内显示完毕，每一行都是一大段文本，行尾有一个换行符 `\n`。用户体验极差。更何况 Minecraft 里没有多少显示文本的空间。正常的游戏内手册的大小和原版成书相差无几。
此外，Minecraft 中文本换行和字宽也有关。为此，必须要有一个专用的方法来正确处理换行。

## 实现
所以就有了 `func_78271_c`。在原版的底层代码中，它的行为**大约**是这样的：

```lua
function func_78271_c(str, wrapWidth)
    return insertNewLineBasedOnWrapWidth(str, wrapWidth).split("\n")
end

function insertNewLineBasedOnWrapWidth(str, wrapWidth)
    var nextNewLinePos = findWrapPosition(str, wrapWidth)
    if nextNewLinePos > str.length() then
        return str
    else
        var part = str.substring(0, nextNewLinePos)
        var format = getFormatFrom(part)
        var remain = str.substring(nextNewLinePos)
        return part + "\n" + format + insertNewLineBasedOnWrapWidth(remain) // recursive call
    end
end

function findWrapPosition(str, wrapWidth)
    var current = 0
    var lastSpace = -1
    var widthCount = 0
    var boldMode = 0
    for (; current < str.length(); current++) do
        var c = str.charAt(current)
        if c is newLine then
            lastSpace = current
            break
        else if c is whitespace then
            lastSpace = current
        else if c is '§' then
            current++
            var f = str.charAt(current)
            if isBoldFormat(f) then
                boldMode = true
            else if isResetFormat(f) or isColorFormat(f) then
                boldMode = false
            end
        else
            widthCount += getCharWidth(c)
            if boldMode then
                widthCount++
            end
        end

        if widthCount > wrapWidth then
            break
        end
    end

    if str.length() != current and lastSpace != -1 and lastSpace < current then
        return lastSpace
    else
        return current
    end
end
```

基本上，它的逻辑如下：

  1. 找到下一个可看作是空白的字符；
  2. 如果当前累计的字符串宽度没超过给定宽度，那么空白所在的地方就会插入换行符；
  3. 否则，直接在距离上一个换行符最远的地方插入新的换行符，使得两个换行符之间的字符串长度不超过给定宽度；
  4. 将上一个字符串的样式拼接到剩余未处理的字符串的开头；
  5. 在字符串处理完毕后，按换行符分割字符串。

## 限制
这个实现基本是合理的。但它有一个假设：要分割的字符串需要使用空格分割单词（即使用空格作为分字符），在上文伪代码描述的具体实现中可以看到，它会追踪最近一个空白（具体来说，只有 `\u0020`）及换行符的位置，以确定是否插入新的 `\n`。尽管世界上大多数书写系统使用空格作为分字符，但这并不代表所有的文字都是如此。比如中文只使用汉字和标点符号，没有分字符的概念。对目前的 `func_78271_c` 实现来说，它可以处理纯中文文本，因为它会将目标文本看作是“supercalifragilisticexpialidocious”这样的“超长单词”处理。但当出现混合文本的时候，问题就会浮现：

![林业模组的“林务员手册”，第一页](sample-1.png)

注意“Binnie”后出现的空白。在中文中，几乎所有时候，汉字之后都允许换行。然而在这里，尽管“Binnie”之后有足够的空间容纳若干汉字，它之后仍然出现了换行。原因也十分简单——`func_78271_c` 不认为那些汉字之后可以换行。

简单来说，`func_78271_c` 当前的实现只能处理空格分割的文本。它现在的实现甚至不能在连字符（`-`）之后另起一行。

## 改进
当然，[换行规则实际上很复杂][ref-1]。然而，Java 标准库已经内建了相关的类库——`java.text.BreakIterator`。`BreakIterator` 类可提供对字词、行、句子等的边界分析（Boundary Analysis）。使用它可以在不依赖其他库的情况下轻松处理不同语言环境下的换行问题。
笔者已经实现了一个改进版本的 `func_78271_c`，使用 `BreakIterator` 来判断换行位置。相关代码目前位于 [3TUSK/PanI18n][ref-2]。其逻辑通过替换 `field_71466_p`（`Minecraft.fontRenderer`）字段的值注入，该字段中持有一全 Minecraft 共享的 `FontRenderer` 实例。
该实现的效果图：

![林业模组的“林务员手册”第一页正文，以原版书与笔为载体，使用改进后的逻辑](sample-2.png)

对比原版：

![林业模组的“林务员手册”第一页正文，以原版书与笔为载体，使用原版 FontRenderer 逻辑](sample-3.png)

## 结语
国际化一直都是软件开发中的难题，Minecraft 自然也不得不面对一样的问题。相关问题的解决需要全社区的共同努力。在此，笔者衷心希望本文能帮助到未来的开发者。  
此外，截至Minecraft 1.13 Release，此问题仍然存在。

![林业模组的“林务员手册”第一页正文，以原版书与笔为载体，使用 Minecraft 1.13 Release](sample-4.png)

[ref-1]: http://www.unicode.org/reports/tr14/
[ref-2]: https://github.com/3TUSK/PanI18n/blob/bleeding/src/main/java/info/tritusk/pani18n/FormattingEngine.java
