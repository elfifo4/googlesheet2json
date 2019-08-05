package com.eladfinish.sheet2json.misc;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextUtils {

    public static String replaceSpace(String str) {
        return str.replaceAll("\\s", "_");
    }

    public static String removeSpace(String str) {
        return str.replaceAll("\\s", "");
    }

    public static String replaceTabWithSpaces(String str) {
        return str.replaceAll("\t", "    ");
    }

    public static String capitalize(String str) {
        String field = removeSpace(str);//.toLowerCase();
        return field.substring(0, 1).toUpperCase()
                + field.substring(1);
    }

    public static String capitalizeEachWord(String str) {
        return replaceAllWithFunction(str, "_.", String::toUpperCase);
    }

    interface Replacer {
        String replace(String str);
    }


    private static String replaceAllWithFunction(String input, String regex, Replacer replacer) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);
        StringBuffer stringBuffer = new StringBuffer();

        while (matcher.find()) {
            String captured = matcher.group();
            matcher.appendReplacement(stringBuffer, replacer.replace(captured));
        }
        matcher.appendTail(stringBuffer);

        return stringBuffer.toString();
    }

}
