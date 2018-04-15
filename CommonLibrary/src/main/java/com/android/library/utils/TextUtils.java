package com.android.library.utils;


import java.util.Collection;
import java.util.regex.Pattern;

public class TextUtils {
    private TextUtils() { /* cannot be instantiated */
    }

    /**
     * 判断非空
     *
     * @param input
     * @return
     */
    public static boolean isNotEmpty(String input) {
        return !isEmpty(input);
    }

    /**
     * 判断给定字符串是否空白串。 空白串是指由空格、制表符、回车符、换行符组成的字符串 若输入字符串为null或空字符串，返回true
     *
     * @param input 要判断的字符串
     * @return boolean 空串：true 非空串：false
     */
    public static boolean isEmpty(CharSequence input) {
        if (android.text.TextUtils.isEmpty(input))
            return true;

        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            if (c != ' ' && c != '\t' && c != '\r' && c != '\n') {
                return false;
            }
        }
        return true;
    }

    /**
     * 判断集合是否为空
     *
     * @param collection
     * @return
     */
    public static <T> boolean isEmpty(Collection<T> collection) {
        if (collection == null || collection.size() == 0) {
            return true;
        }
        return false;
    }

    /**
     * 是否是表情字符
     *
     * @param codePoint
     * @return
     */
    public static boolean isEmojiCharacter(char codePoint) {
        return !((codePoint == 0x0) || (codePoint == 0x9) || (codePoint == 0xA) || (codePoint == 0xD)
                || ((codePoint >= 0x20) && (codePoint <= 0xD7FF)) || ((codePoint >= 0xE000) && (codePoint <= 0xFFFD)) || ((codePoint >= 0x10000) && (codePoint <= 0x10FFFF)));
    }

    /**
     * 过滤emoji 或者 其他非文字类型的字符
     *
     * @param source
     * @return
     */
    public static String filterEmoji(String source) {
        StringBuilder buf = new StringBuilder();
        int len = source.length();
        for (int i = 0; i < len; i++) {
            char codePoint = source.charAt(i);
            if (!isEmojiCharacter(codePoint)) {
                buf.append(codePoint);
            }
        }
        return buf.toString();
    }

    /**
     * 检查是否含有表情
     *
     * @param source
     * @return
     */
    public static boolean containsEmoji(String source) {
        if (isEmpty(source)) {
            return false;
        }
        for (int i = 0; i < source.length(); i++) {
            char c = source.charAt(i);
            if (isEmojiCharacter(c)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isMobile(String mobile) {
        if (isEmpty(mobile)) {
            return false;
        }
        return mobile.matches("^\\d{11}$");
    }
    
    public static boolean mactchMoile(String mobile){
        if (isEmpty(mobile)) {
            return false;
        }
        return Pattern.compile("0?(13[0-9]|15[012356789]|17[0678]|18[0-9]|14[57])[0-9]{8}").matcher(mobile).matches();
    }
    
    public static boolean mactchEmail(String email){
        if (isEmpty(email)) {
            return false;
        }
        //return Pattern.compile("\\w[\\w.-]*@[\\w.]+\\.\\w+").matcher(email).matches();
        return Pattern.compile("([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}").matcher(email).matches();
    }
    
    public static boolean mactchNumberic(String email){
        if (isEmpty(email)) {
            return false;
        }
        return Pattern.compile("\\d+").matcher(email).matches();
    }
}
