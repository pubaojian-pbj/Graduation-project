package com.yang.util;

/**
 * Created by yang on 2018/5/21.
 */
public class NumUtil {
    public static String toFourBinaryNum(int num) {
        String str = Integer.toBinaryString(num);
        int n = 4 - str.length();
        while (n > 0) {
            str = "0" + str;
            n--;
        }
        return str;
    }

    public static String toFiveBinaryNum(int num) {
        String str = Integer.toBinaryString(num);
        int n = 5 - str.length();
        while (n > 0) {
            str = "0" + str;
            n--;
        }
        return str;
    }

    public static String toSixBinaryNum(int num) {
        String str = Integer.toBinaryString(num);
        int n = 6 - str.length();
        while (n > 0) {
            str = "0" + str;
            n--;
        }
        return str;
    }

    public static String toTenBinaryNum(int num) {
        String str = Integer.toBinaryString(num);
        int n = 10 - str.length();
        while (n > 0) {
            str = "0" + str;
            n--;
        }
        return str;
    }
}
