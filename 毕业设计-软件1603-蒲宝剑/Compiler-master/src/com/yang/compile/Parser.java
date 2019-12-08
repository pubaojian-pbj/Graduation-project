package com.yang.compile;

import com.yang.util.NumUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by yang on 2017/12/13.
 */
public class Parser {
    public String x;
    private int flag = 0;
    private boolean con = false;
    private Analyzer analyzer;
    String[] names = {"t1", "t2", "t3", "t4", "t5", "t6", "t7"};
    private int nameP = 0;
    private StringBuilder sb = new StringBuilder();
    private StringBuilder ob = new StringBuilder();
    private StringBuilder binaryCode = new StringBuilder();
    private Map<String,String> map = new HashMap<>();
    public  Map<String,String> varNum = new HashMap<>();
    private int statusCode = 1;
    private int address = 0;
    public Map<String,Integer> varAddress = new HashMap<>();

    public Parser(Analyzer analyzer) {
        this.analyzer = analyzer;
        ob.append("DATA:\r\n");
        binaryCode.append("00000\r\n");
    }

    public void start() {
        if (flag == 1) return;
        int sta = analyzer.parser();
        if (con && sta!=Analyzer.CON_END_STATUS) {
            sb.append(analyzer.str);
            start();
            return;
        }
        if (sta == Analyzer.INT_STATUS) {
            sta = analyzer.parser();
            if (sta == Analyzer.VAR_NAME_STATUS) {
                opDef(); //定义过程
            }
        } else if (sta == Analyzer.VAR_NAME_STATUS){
            opProcess(); //运算过程
        } else if (sta == Analyzer.IF_STATUS) {
            sb.append("if");
            start();
        } else if (sta == Analyzer.CON_STATUS) {
            con = true;
            sb.append(" ");
            start();
        }else if (sta == Analyzer.CON_END_STATUS) {
            con = false;
            sb.append(" ");
            sb.append("goto L1;goto L2;L1:");
            start();
        } else if (sta == Analyzer.ELSE_STATUS) {
            sb.append("L2:");
            start();
        } else if (sta == Analyzer.JUMP_STATUS) {
            start();
        } else if (sta == -1) {
            return;
        } else if (sta == Analyzer.RETURN_STATUS) {
            flag = 1;
        } else {
            sb.append("语法错误");
            statusCode = 0;
        }
    }

    private void opDef() {
        String name = analyzer.str;
        ob.append(name);
        ob.append(" DB ");
        varAddress.put(name,address++);
        String newName;
        if (map.get(name) != null) {
            newName = map.get(name);
        }
        else if (name.equals("i")) {
            newName = "i";
        } else if (name.equals("j")) {
            newName = "j";
        }
        else {
            newName = newName();
        }
        int sta = analyzer.parser();
        if (sta == Analyzer.EQUAL_STATUS) {
            sb.append(newName + ":=");
            sta = analyzer.parser();
            if (sta == Analyzer.NUM_STATUS) {
                int num = Integer.valueOf(analyzer.str);
                sb.append(num);
                ob.append(num + "\r\n");
                binaryCode.append(NumUtil.toFourBinaryNum(num) + "\r\n");
                varNum.put("" + num,name);
                map.put(name, newName);
                if (analyzer.parser() == Analyzer.END_STATUS) {
                    sb.append(";");
                    start();
                }
            } else if (sta == Analyzer.VAR_NAME_STATUS) {
                String nm = analyzer.str;
                sb.append(map.get(nm));
                map.put(name, newName);
                sta = analyzer.parser();
                if (sta == Analyzer.OP_STATUS) {
                    sb.append(analyzer.str);
                    if (analyzer.parser() == Analyzer.VAR_NAME_STATUS) {
                        nm = analyzer.str;
                        sb.append(map.get(nm));
                        if (analyzer.parser() == Analyzer.END_STATUS) {
                            sb.append(";");
                            start();
                        }
                    }
                }
            }
        }else if (sta == Analyzer.END_STATUS) {
            map.put(name,newName);
            ob.append("?\r\n");
            binaryCode.append("xxxx" + "\r\n");
            start();
        }
    }

    private void opProcess() {
        String name = analyzer.str;
        String newName;
        if (map.get(name) != null) {
            newName = map.get(name);
        }else {
            newName = newName();
        }
        int sta = analyzer.parser();
        if (sta == Analyzer.EQUAL_STATUS) {
            sb.append(newName + ":=");
            sta = analyzer.parser();
            if (sta == Analyzer.NUM_STATUS) {
                int num = Integer.valueOf(analyzer.str);
                sb.append(num);
                varNum.put("" + num,name);
                map.put(name, newName);
                if (analyzer.parser() == Analyzer.END_STATUS) {
                    sb.append(";");
                    start();
                }
            } else if (sta == Analyzer.VAR_NAME_STATUS) {
                x = name;
                String nm = analyzer.str;
                sb.append(map.get(nm));
                map.put(name, newName);
                sta = analyzer.parser();
                if (sta == Analyzer.OP_STATUS) {
                    sb.append(analyzer.str);
                    if (analyzer.parser() == Analyzer.VAR_NAME_STATUS) {
                        nm = analyzer.str;
                        sb.append(map.get(nm));
                        if (analyzer.parser() == Analyzer.END_STATUS) {
                            sb.append(";");
                            start();
                        }
                    }
                }
            }
        }else if (sta == Analyzer.END_STATUS) {
            map.put(name,newName);
            start();
        }
    }

    private String newName() {
        if (nameP >= names.length) {
            System.out.println("Expression too complex: " + 0);
            System.exit(1);
        }

        String reg = names[nameP];
        nameP++;

        return reg;
    }

    private void freeNames(String s) {
        if (nameP > 0) {
            names[nameP] = s;
            nameP--;
        }
        else {
            System.out.println("(Internal error) Name stack underflow: " + 0);
        }
    }

    public void print() {
        System.out.println(sb);
        System.out.println(ob);
        System.out.println(binaryCode);
    }

    public String retString() {
        return sb.toString();
    }

    public String obString() {
        return ob.toString();
    }

    public String getBinaryCode() {
        return binaryCode.toString();
    }

    public static void main(String[] args) {
        String s = "int a = 6;int b = 5; int c;";
        Analyzer analyzer = new Analyzer(s);
        Parser p = new Parser(analyzer);
        p.start();
        p.print();
    }
}
