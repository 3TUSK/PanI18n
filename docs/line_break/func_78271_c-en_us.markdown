# Myths behind `func_78271_c` (`FontRenderer::listFormattedStringToWidth`)

`func_78271_c` is a method of `FontRenderer`, which divides a string to pieces within a certain width. This method is used for wrapping long text into several lines, for example in a Book and Quill, or a tooltip HUD. This article will discuss its implementation details - what it is doing, what limitations it has, and what possible improvements we may have.

## Motivation
Why this method is a thing? Because text can be very long. Think about this article: without proper wrapping, this article will be displayed in only a couple of lines, each line contains a very long sentence, ending with a `\n`. It's definitely not good UX, right? Even more than that, spaces for displaying text in Minecraft is usually limited - a typical in-game manual occupies a bit more space than vanilla written book.  
Furthermore, wrapping text in Minecraft is depending on character width. There must be some sort of helper methods to make sure text wrapping is correctly handled.

## Implementation
So here it is, `func_78271_c`. It *roughly* does the following:

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

It essentially does the follow:

  1. Find the next position of character that counts as whitespace;
  2. If the position is not exceeding the maximum wrapping width, then insert new line character at that position;
  3. Otherwise, insert new line character at the position, where the width of text between previous new line character and this new line character is smaller than the given wrapping width;
  4. Preserve special format from previous "line", by appending them after new line character;
  5. When the entire string is processed, split the string on new line character.

## Limitations
This implementation is basically valid. However, it makes one assumption: the text to be split relies on whitespace to separate words (use whitespace as word divider), because it tracks position of whitespace (specifically, `\u0020`) and new line to determine where is next valid position to insert `\n`. Although most of writing systems on the world do use whitespace as word divider, this is not true for all scripts. For example, Chinese script does not use any word divider, only Chinese characters and a set of punctuations. It may not be a severe issue if there is only Chinese script, as current implementation of `func_78271_c` will treat it as "a very, very long single word", something like "supercalifragilisticexpialidocious". However, the true issue will reveal when there is mixed script:

![Front page of "Forester's Almanac", from Forestry Mod](sample-1.png)

Notice the space behind "Binnie". In Chinese script, under almost all cases, it is possible to start a new line after any Chinese character. However, a new line is inserted after "Binnie", even there is still enough space to let several Chinese character fit in. The reason is simple and dry: `func_78271_c` does not recognize those Chinese characters as valid line breaking positions.  

In short, current implementation of `func_78271_c` can only handle space-separated text. It cannot even handle the situation where there should be a line break after hyphen (`-`).

## Improvements
Surely, [line breaking rules are, in fact, complex][ref-1]. However, Java Standard Library does have related facilities to handle this - `java.text.BreakIterator`. The `BreakIterator` is a class that provides supports of boundary analysis of words, lines, sentences and others. Using this can easily handle line breaking on a per-locale basis without extra dependencies.  
I have implemented a such version of `func_78271_c` which utilizes `BreakIterator` to determine new line position. It is now available at: [3TUSK/PanI18n][ref-2]. The logic is injected by swapping out the value of `field_71466_p` (`Minecraft.fontRenderer`), which is a public field that holds the only instance of `FontRenderer` used by Minecraft.  
The new implementation is demonstrated below:

![Contents of front page of "Forester's Almanac", in vanilla Book and Quill, using new logic](sample-2.png)

Comparing with vanilla implementation:

![Contents of front page of "Forester's Almanac", in vanilla Book and Quill, using vanilla FontRenderer](sample-3.png)

## Final words
Internationalization is always a challenging topic in software development, and Minecraft surely is facing the same issue right now. Tackling down the issue requires joint effort from the whole community. I sincerely hope this can help further developers.  
Also, as of Minecraft 1.13 Release, this is still a valid issue.

![Contents of front page of "Forester's Almanac", in vanilla Book and Quill, using Minecraft 1.13 Release](sample-4.png)

[ref-1]: http://www.unicode.org/reports/tr14/
[ref-2]: https://github.com/3TUSK/PanI18n/blob/bleeding/src/main/java/info/tritusk/pani18n/FormattingEngine.java
