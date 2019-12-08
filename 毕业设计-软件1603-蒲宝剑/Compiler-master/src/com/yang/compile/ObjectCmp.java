package com.yang.compile;

import com.yang.util.NumUtil;

public class ObjectCmp {
        /*
        if a=b goto L1;
        goto L2;
        L1:t1:=2;
        t2:=1;
        t3:=t1 + t2;
        L2:t1:=2;
        t2:=1;
        t3:=t1 - t2;
         */
    private StringBuilder assemblyCode = new StringBuilder();
    private StringBuilder binaryCode = new StringBuilder();
    private StringBuilder sb = new StringBuilder();
    private String[] exp;
    private String str;
    private int pt = 0;
    private boolean flag = false;
    private boolean galf = false;
    private boolean hasIf = false;
    private Parser parser;

    public ObjectCmp(String str,Parser parser) {
        this.str = str;
        this.parser = parser;
        assemblyCode.append("CODE:\r\n");
        binaryCode.append(NumUtil.toFiveBinaryNum(16));
        binaryCode.append("\r\n");
    }
    public String getAssemblyCode() {
        if (!str.contains("if")) {
            assemblyCode.append("MOV AH,01;\r\n");
            assemblyCode.append("INT 21H;");
            binaryCode.append("1111111111111111");
            binaryCode.append("\r\n");
        }
        return parser.obString() + String.valueOf(assemblyCode);
    }

    public String getBinaryCode() {
        return parser.getBinaryCode() + String.valueOf(binaryCode);
    }

    public void start() {
        exp = str.split(";");
        while (pt < exp.length) {
            assemblyCode.append(cmp(exp[pt]));
            pt++;
        }
    }

    public String cmp(String one) {
        sb.setLength(0);
        if (one.startsWith("t1:=")) {
            String num = one.substring(4,one.length());
            String var = parser.varNum.get(num);
            sb.append("MOV AL,");
            sb.append(var);
            sb.append(":\r\n");
            binaryCode.append(NumUtil.toSixBinaryNum(0) + NumUtil.toFiveBinaryNum(0)
            + NumUtil.toFiveBinaryNum(parser.varAddress.get(var)));
            binaryCode.append("\r\n");
        } else if (one.startsWith("L1:")){
            sb.append("L1;\r\n");
            binaryCode.append(NumUtil.toFiveBinaryNum(20));
            binaryCode.append("\r\n");
        }  else if (one.startsWith("L2:")){
            sb.append("L2;\r\n");
            binaryCode.append(NumUtil.toFiveBinaryNum(24));
            binaryCode.append("\r\n");
            galf = true;
        }
        else if (one.startsWith("t2:=")) {
            String num = one.substring(4,one.length());
            String var = parser.varNum.get(num);
            sb.append("MOV AH,");
            sb.append(var);
            binaryCode.append(NumUtil.toSixBinaryNum(0) + NumUtil.toFiveBinaryNum(1)
                    + NumUtil.toFiveBinaryNum(parser.varAddress.get(var)));
            binaryCode.append("\r\n");
            sb.append(":\r\n");
        }
        else if (one.startsWith("i:=")) {
            sb.append("MOV BL,i");
            sb.append(":\r\n");
            binaryCode.append(NumUtil.toSixBinaryNum(0) + NumUtil.toFiveBinaryNum(2)
                    + NumUtil.toFiveBinaryNum(parser.varAddress.get("i")));
            binaryCode.append("\r\n");
        } else if (one.startsWith("j:=")) {
            sb.append("MOV BH,j");
            sb.append(":\r\n");
            binaryCode.append(NumUtil.toSixBinaryNum(0) + NumUtil.toFiveBinaryNum(3)
                    + NumUtil.toFiveBinaryNum(parser.varAddress.get("j")));
            binaryCode.append("\r\n");
        } else if (one.startsWith("if")){
            flag = true;
            hasIf = true;
            sb.append("CMP BL,BH;\r\n");
            binaryCode.append(NumUtil.toSixBinaryNum(2) + NumUtil.toFiveBinaryNum(2)
                    + NumUtil.toFiveBinaryNum(3));
            binaryCode.append("\r\n");
            if (one.contains("i>j")) {
                sb.append("JNG L2;\r\n");
                binaryCode.append(NumUtil.toSixBinaryNum(10) + NumUtil.toTenBinaryNum(24));
                binaryCode.append("\r\n");
            } else if (one.contains("i<j")) {
                sb.append("JNL L2;\r\n");
                binaryCode.append(NumUtil.toSixBinaryNum(9) + NumUtil.toTenBinaryNum(24));
                binaryCode.append("\r\n");
            } else if (one.contains("i=j")) {
                sb.append("JNE L2;\r\n");
                binaryCode.append(NumUtil.toSixBinaryNum(8) + NumUtil.toTenBinaryNum(24));
                binaryCode.append("\r\n");
            }
        } else  {

        }
        if (one.contains("t3:=")) {
            if (one.contains("+")) {
                sb.append("ADD AL,AH");
                binaryCode.append(NumUtil.toSixBinaryNum(3) + NumUtil.toFiveBinaryNum(0)
                        + NumUtil.toFiveBinaryNum(1));
                binaryCode.append("\r\n");
            } else if (one.contains("-")) {
                sb.append("SUB AL,AH");
                binaryCode.append(NumUtil.toSixBinaryNum(4) + NumUtil.toFiveBinaryNum(0)
                        + NumUtil.toFiveBinaryNum(1));
                binaryCode.append("\r\n");
            } else if (one.contains("&")) {
                sb.append("AND AL,AH");
                binaryCode.append(NumUtil.toSixBinaryNum(6) + NumUtil.toFiveBinaryNum(0)
                        + NumUtil.toFiveBinaryNum(1));
                binaryCode.append("\r\n");
            } else if (one.contains("|")) {
                sb.append("OR AL,AH");
                binaryCode.append(NumUtil.toSixBinaryNum(5) + NumUtil.toFiveBinaryNum(0)
                        + NumUtil.toFiveBinaryNum(1));
                binaryCode.append("\r\n");
            }
            sb.append(";\r\n");
            sb.append("MOV " + parser.x +",AL\r\n");
            binaryCode.append(NumUtil.toSixBinaryNum(1) + NumUtil.toFiveBinaryNum(0)
                    + NumUtil.toFiveBinaryNum(parser.varAddress.get("c")));
            binaryCode.append("\r\n");
            if (flag) {
                sb.append("JMP NEXT;\r\n");
                flag = false;
                binaryCode.append(NumUtil.toSixBinaryNum(7) + NumUtil.toTenBinaryNum(28));
                binaryCode.append("\r\n");
            }
            if (galf) {
                if (hasIf) {
                    sb.append("NEXT;\r\n");
                    binaryCode.append(NumUtil.toFiveBinaryNum(28));
                    binaryCode.append("\r\n");
                }
                sb.append("MOV AH,01;\r\n");
                sb.append("INT 21H;");
                binaryCode.append("1111111111111111");
                binaryCode.append("\r\n");
                galf = false;
            }
        }
        return sb.toString();
    }

    public static void main(String[] args) {
//        ObjectCmp oc = new ObjectCmp("if a < b goto L2;L1:t1:=2;t2:=1;t3:=t1+t2;L2:t1:=2;t2:=1;t3:=t1-t2;");

        String s = "int a = 6;int b = 5; int c; c = a + b;";
        Analyzer analyzer = new Analyzer(s);
        Parser p = new Parser(analyzer);
        p.start();
        ObjectCmp oc = new ObjectCmp(p.retString(),p);
        oc.start();
        System.out.println(oc.getAssemblyCode());
    }
}