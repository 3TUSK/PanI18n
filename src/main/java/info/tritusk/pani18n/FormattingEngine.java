package info.tritusk.pani18n;

import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.IntUnaryOperator;

final class FormattingEngine {

    private static final String COLOR_CODE = "123456789abcdef";
    private static final String FORMATTING_CODE = "klmno";
    
    /**
     *
     * @param str String
     * @param wrapWidth maximum length of one line, based on char width
     * @param charWidthGetter an int to int function that returns width of character
     * @param currentLocale target locale
     * @return A list of String, each occupies one line
     */
    public static List<String> wrapStringToWidth(final String str, final int wrapWidth, final IntUnaryOperator charWidthGetter, final Locale currentLocale) {
        BreakIterator lineBreakEngine = BreakIterator.getLineInstance(currentLocale);
        lineBreakEngine.setText(str);
        ArrayList<String> lines = new ArrayList<>(8);
        String cachedFormat = "";
        char color = '0', format = 'r'; // 0 is format code for black-colored-text; r is format code to reset format to default
        int start = 0;
        int width = 0;
        for (int index = 0; index < str.length(); index++) {
            char c = str.charAt(index);

            if (c == '\n') { // Unconditionally cut string when there is new line
                lines.add(cachedFormat + str.substring(start, index));
                // Set start to appropriate position before next String::substring call
                start = index + 1;
                // Clear width counter
                width = 0;
                if (format != 'r' && format != 'R') {
                    cachedFormat = new String(new char[] {'\u00A7', color, '\u00A7', format});
                } else {
                    cachedFormat = new String(new char[] {'\u00A7', color});
                }
                continue;
            } else if (c == '\u00A7') { // a.k.a. '§'. Used by Minecraft to denote special format, don't count it
                index++;
                char f = Character.toLowerCase(str.charAt(index));
                if (f == 'r' || f == 'R') {
                    color = '0';
                    format = 'r';
                } else if (FORMATTING_CODE.indexOf(f) != -1) {
                    format = f;
                } else if (COLOR_CODE.indexOf(f) != -1) {
                    color = f;
                    format = 'r'; // Reset format when new color code appears
                } else {
                    // TODO is this correct?
                    width += charWidthGetter.applyAsInt('\u00A7');
                    width += charWidthGetter.applyAsInt(str.charAt(index));
                }
            } else {
                // Regular content, add its width to the tracker
                width += charWidthGetter.applyAsInt(c);
            }

            if (width > wrapWidth) {
                int end = lineBreakEngine.preceding(index);
                if (end <= start) {
                    String result = cachedFormat + str.substring(start, index);
                    lines.add(result);
                    start = index;
                } else {
                    String result = cachedFormat + str.substring(start, end);
                    lines.add(result);
                    start = end;
                    index = start;
                }
                width = 0;
                if (format != 'r' && format != 'R') {
                    cachedFormat = new String(new char[] {'\u00A7', color, '\u00A7', format});
                } else {
                    cachedFormat = new String(new char[] {'\u00A7', color});
                }
            }
        }
        String lastPiece = str.substring(start);
        if (!lastPiece.isEmpty()) {
            lines.add(cachedFormat + str.substring(start)); // Add the last piece, if exists
        }
        lines.trimToSize();
        return lines;
    }
}
