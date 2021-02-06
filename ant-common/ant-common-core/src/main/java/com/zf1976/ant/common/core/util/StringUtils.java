package com.zf1976.ant.common.core.util;

import com.google.common.base.CharMatcher;
import com.power.common.util.StringUtil;

/**
 * @author mac
 * @date 2021/1/23
 **/
public class StringUtils extends StringUtil {

    /**
     * 把字符串数字类型的数字取出来（只取遇到非数字字符前，包括空格）
     * @param str
     * <li>"1-0我5013我24a5c6"    》 1</li>
     * <li>"10  5 013我24a 5c6"  》 10</li>
     * <li>"105013我24a5c6"      》 105013</li>
     * <li>"000"                 》 000</li>
     * <li>"00010123600"         》 00010123600</li>
     * <li>"好20我1a2b"           》  空字符串</li>
     * @return /
     */
    public static String getPrefixNumberText(String str){
        if(isEmpty(str)){
            throw new RuntimeException("参数str不能为空");
        }
        StringBuilder number = new StringBuilder();

        String[] strArray = str.split("");
        for (int i=1; i<strArray.length; i++) {
            if(RegUtils.isNumberText(strArray[i])){
                number.append(strArray[i]);
            }else{
                break;
            }
        }
        return number.toString();
    }


    /**
     * 把字符串数字类型的数字取出来（只取遇到非数字字符前，但不包括空格）
     * @param str
     * <li>"1-0我5013我24a5c6"    》 1</li>
     * <li>"10  5 013我24a 5c6"  》 105013</li>
     * <li>"105013我24a5c6"      》 105013</li>
     * <li>"000"                 》 000</li>
     * <li>"00010123600"         》 00010123600</li>
     * <li>"00010123我600"        》 00010123</li>
     * @return /
     */
    public static String getPrefixNumberTextIgnoreSpace(String str){
        if(isEmpty(str)){
            throw new RuntimeException("参数str不能为空");
        }
        StringBuilder number = new StringBuilder();

        String[] strArray = str.split("");
        for (String string : strArray) {
            if(isNotEmpty(string)){
                if(RegUtils.isNumberText(string)){
                    number.append(string);
                }else{
                    break;
                }
            }
        }
        return number.toString();
    }


    /**
     * 把字符串数字类型的所有数字取出来
     * @param str
     * <li>"1-000我10123我60#0"       》 100010123600</li>
     * <li>"00010-+123我600"         》 00010123600</li>
     * <li>"我是2019我600"            》 2019600</li>
     * <li>"我是20 -19我    600"         》 2019600</li>
     * @return /
     */
    public static String getNumberText(String str){
        if(isEmpty(str)){
            throw new RuntimeException("参数str不能为空");
        }
        StringBuilder number = new StringBuilder();

        String[] strArray = str.split("");
        for (String string : strArray) {
            if(isNotEmpty(str) && RegUtils.isNumberText(string)){
                number.append(string);
            }
        }
        return number.toString();
    }


    /**
     * 把字符串数字类型的数字取出来（只取遇到非数字字符前，不包括空格）转换成数字
     * @param str
     * <li>"1-0我5013我24a5c6"    》 1</li>
     * <li>"10  5 013我24a 5c6"  》 105013</li>
     * <li>"105013我24a5c6"      》 105013</li>
     * <li>"000"                 》 0</li>
     * <li>"00010123600"         》 10123600</li>
     * @return /
     */
    public static long getPrefixNumber(String str){
        String number = getPrefixNumberTextIgnoreSpace(str);
        if(isEmpty(str)){
            return 0;
        }
        return numberText(number);
    }


    /**
     * 把字符串数字类型的数字取出来转换成数字
     * @param str
     * <li>"1-000我10123我60#0"   》 100010123600</li>
     * <li>"00010-+123我600"      》 10123600</li>
     * <li>"我是2019我600"         》 2019600</li>
     * <li>"我是20 -19我    600"     》 2019600</li>
     * @return /
     */
    public static long getNumber(String str){
        String number = getNumberText(str);
        if(isEmpty(str)){
            return 0;
        }
        return numberText(number);
    }

    private static long numberText(String number){
        //去掉前面为0的，如0099变成99
        String[] texts = number.split("");
        StringBuilder numbers = new StringBuilder();
        collectMatches(numbers, texts);
        if(numbers.length() < 1){
            return 0;
        }
        return Long.parseLong(numbers.toString());
    }

    private static void collectMatches(StringBuilder numbers, String[] texts) {
        for (String text : texts) {
            if(numbers.length() < 1){
                if("0".equals(text)){
                    continue;
                }
            }
            numbers.append(text);
        }
    }

}
