package com.legyver.utils.cli.internal;

import java.util.List;
import java.util.StringJoiner;
import java.util.StringTokenizer;
import java.util.stream.Collectors;

/**
 * Force line wraps for menu text
 */
public class WindowWrapFactory {
    private static final String TAB = "\t";
    private final int width;
    private final String lineWrapCharacters;

    /**
     * Construct a WindowWrapFactory to force a specified width with the given line wrap characters
     * @param width the width of the text at which to force wrapping
     * @param lineWrapCharacters the carriage/return characters to apply at line ending
     */
    public WindowWrapFactory(int width, String lineWrapCharacters) {
        this.width = width;
        this.lineWrapCharacters = lineWrapCharacters;
    }

    /**
     * Force text to display within the specified width
     * @param textToWrap the text to format
     * @return the formatted text
     */
    public String wrap(String textToWrap) {
        StringJoiner stringJoiner = new StringJoiner(lineWrapCharacters);
        List<String> lines = textToWrap.lines().collect(Collectors.toList());

        StringJoiner currentLine = new StringJoiner(" ");
        StringBuilder wrappedLinePrefix = new StringBuilder();
        for (String line: lines) {
            while (line.startsWith(TAB)) {
                currentLine.add(TAB);
                wrappedLinePrefix.append(TAB);
                line = line.length() == 1 ? "" : line.substring(1);//seek pass leading tab
            }
            String[] tabbed = line.split(TAB);

            for (int i = 0; i < tabbed.length; i++) {
                String tab = tabbed[i];
                StringTokenizer tabTokenizer = new StringTokenizer(tab, " ");
                while (tabTokenizer.hasMoreElements()) {
                    String token = tabTokenizer.nextToken();
                    int lengthWithToken = currentLine.length() + token.length() + 1;//+1 for separating space
                    if (lengthWithToken > width) {
                        String finalizedLine = currentLine.toString().replaceAll("\t ", TAB);//workaround for stringjoiner " "
                        stringJoiner.add(finalizedLine);
                        currentLine = new StringJoiner(" ");
                        if (wrappedLinePrefix.length() > 0) {
                            currentLine.add(wrappedLinePrefix);
                        }
                    }
                    currentLine.add(token);
                }
                if (i < tabbed.length - 1) {
                    currentLine.add(TAB);
                }
            }
            String finalizedLine = currentLine.toString().replaceAll("\t ", TAB);//workaround for stringjoiner " "
            stringJoiner.add(finalizedLine);
            currentLine = new StringJoiner(" ");
            if (wrappedLinePrefix.length() > 0) {
                wrappedLinePrefix = new StringBuilder();
            }
        }
        return stringJoiner.toString();
    }
}
