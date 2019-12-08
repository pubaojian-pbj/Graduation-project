package com.yang.compile;

import java.util.Scanner;

public class Analyzer {
    private String[] keyWords = new String[5];
    private char[] arr;
    private int code;
    private int ptr = 0;
    private char ch;
    private int syn;
    public String str;
    public static int INT_STATUS = 3;
    public static int IF_STATUS = 4;
    public static int ELSE_STATUS = 5;
    public static int VAR_NAME_STATUS = 6;
    public static int NUM_STATUS = 7;
    public static int EQUAL_STATUS = 8;
    public static int OP_STATUS = 9;
    public static int END_STATUS = 12;
    public static int CON_STATUS = 13;
    public static int CON_END_STATUS = 14;
    public static int JUMP_STATUS = 20;
    public static int RETURN_STATUS = 2;

    public Analyzer(String str) {
        arr = str.toCharArray();
        init();
    }

    private void init() {
        keyWords[0] = "include";
        keyWords[1] = "return";
        keyWords[2] = "int";
        keyWords[3] = "if";
        keyWords[4] = "else";
    }

    public int reserve(String s) {
        int is = 0;
        for (int i = 0; i < 5; i ++) {
            if (s.equals(keyWords[i])) {
                is = i + 1;
                break;
            }
        }
        return is;
    }

    public String start() {
        String s = new String("");
        while(ptr < arr.length) {
            test();
            if (syn != 17 && syn != 20) {
                Type t = new Type();
                System.out.println("(" + str + "," + syn + ")");
                s += str + " " + syn + "\n";
            } else if(syn == 20) {

            }
            else {
                System.out.println("未检测出。");
            }
        }
        return s;
    }

    public int parser() {
        if (ptr < arr.length) {
            return test();
        } else {
            return -1;
        }
    }

    public int test() {
        str = "";
        ch = arr[ptr++];
        while (ch == ' ') {
            ch = arr[ptr];
            ptr++;
        }

        if (Character.isLetter(ch)) {
            while (Character.isLetter(ch) || Character.isDigit(ch)) {
                str += ch;
                if (ptr == arr.length) {
                    ptr++;
                    break;
                }
                ch = arr[ptr++];
            }
            ptr--;
            code = reserve(str);
            if (code != 0) {
                syn = code;
            } else {
                syn = 6;
            }
        } else if (Character.isDigit(ch)) {
            while(Character.isDigit(ch)) {
                str += ch;
                if (ptr == arr.length) {
                    ptr++;
                    break;
                }
                ch = arr[ptr++];
            }
            ptr--;
            syn = 7;
        } else if (ch == '=') {
            str += ch;
            syn = 8;
        } else if (ch == '+' || ch == '-' || ch == '&' || ch == '|') {
            str += ch;
            syn = 9;
        } else if (ch == '*') {
            str += ch;
            syn = 10;
            if (ptr != arr.length) {
                ch = arr[ptr++];
                if (ch == '*') {
                    str += ch;
                    syn = 11;
                } else
                    ptr--;
            }
        } else if (ch == ';') {
            str += ch;
            syn = 12;
        } else if (ch == '(') {
            str += ch;
            syn = 13;
        } else if (ch == ')') {
            str += ch;
            syn = 14;
        } else if (ch == '{') {
            str += ch;
            syn = 20;
        } else if (ch == '}') {
            str += ch;
            syn = 20;
        } else if (ch == '<') {
            str += ch;
            syn = 17;
        } else if (ch == '>') {
            str += ch;
            syn = 18;
        } else if (ch == '\r' || ch == '\n' ||  ch == '\t') {
            syn = 20;
        } else {
            syn = 19;
        }
        return syn;
    }

    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        String s = in.nextLine();
        Analyzer ana = new Analyzer(s);
        ana.start();
    }
}