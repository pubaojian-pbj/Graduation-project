package com.yang.util;

import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

public class FileUtils {
    public static String assFileName="E:\\ass.txt";
    public static  String binaryFileName="E:\\binary.txt";

    public static void FileWriteCode(String txt,String name) throws Exception {
        try {
            FileWriter fileWriter = new FileWriter(name);
            fileWriter.write(txt);
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
    }
}
