package com.yang.ui;

import com.yang.compile.Analyzer;
import com.yang.compile.ObjectCmp;
import com.yang.compile.Parser;
import com.yang.util.FileUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Created by yang on 2017/12/2.
 */
public class Ui extends JFrame{
    private Button cmp1,save1,cmp2,save2;
    private JLabel text1, text2, text3;
    private TextArea input, outputAss, outputBinary;
    private String cCode;
    private Analyzer analyzer;
    private Parser parser;
    private ObjectCmp cmp;
    private static String match = "#include<stdio.h>\r\nint main().*return 0;\r\n}";

    public Ui(String name) {
        super(name);
        generate();
        setLayout(null);
        setSize(1250,600);
        setLocation(100,100);
        addWindowListener(new WindowAdapter(){
            @Override
            public void windowClosing(WindowEvent arg0) {
                System.exit(0);
            }
        });
    }

    private void generate() {
        text1 = new JLabel("input C code: ");
        text1.setBounds(0,0,300,50);
        text2 = new JLabel("output Ass code: ");
        text2.setBounds(450,0,300,50);
        text3 = new JLabel("output Binary code: ");
        text3.setBounds(900,0,300,50);
        input = new TextArea();
        input.setBounds(0,50,300,500);
        outputAss = new TextArea();
//        outputAss.setHorizontalAlignment(SwingConstants.LEFT);
//        outputAss.setVerticalAlignment(SwingConstants.TOP);
//        outputAss.setBorder(BorderFactory.createLineBorder(Color.red));
        outputAss.setBounds(450,50,300,500);
        outputBinary = new TextArea();
//        outputBinary.setBorder(BorderFactory.createLineBorder(Color.red));
        outputBinary.setBounds(900,50,300,500);
//        outputBinary.setHorizontalAlignment(SwingConstants.LEFT);
//        outputBinary.setVerticalAlignment(SwingConstants.TOP);
        cmp1 = new Button("Compile");
        cmp1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cCode = input.getText();
                String str = cCode.substring(31, cCode.length() - 6);
                String begin = cCode.substring(0,31);
                String end = cCode.substring(cCode.length() - 12,cCode.length());
                if (begin.equals("#include<stdio.h>\r\nint main() {") && end.equals("return 0;\r\n}")) {
                    analyzer = new Analyzer(str);
                    parser = new Parser(analyzer);
                    parser.start();
                    parser.print();
                    cmp = new ObjectCmp(parser.retString(),parser);
                    System.out.println("中间代码：");
                    System.out.println(parser.retString());
                    cmp.start();
                    outputAss.setText(cmp.getAssemblyCode());
                } else {
                    outputAss.setText("语法错误");
                }
            }
        });
        cmp1.setBounds(320,200,100,30);
        save1 = new Button("Save");
        save1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
					FileUtils.FileWriteCode(cmp.getAssemblyCode(),FileUtils.assFileName);
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
            }
        });
        save1.setBounds(320,300,100,30);
        cmp2 = new Button("ToBinary");
        cmp2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                outputBinary.setText(cmp.getBinaryCode());
            }
        });
        cmp2.setBounds(780,200,100,30);
        save2 = new Button("Save");
        save2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
					FileUtils.FileWriteCode(cmp.getBinaryCode(),FileUtils.binaryFileName);
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
            }
        });
        save2.setBounds(780,300,100,30);
        add(text1);
        add(text2);
        add(text3);
        add(input);
        add(outputAss);
        add(outputBinary);
        add(cmp1);
        add(save1);
        add(cmp2);
        add(save2);
    }


    public static void main(String[] args) {
        Ui ui = new Ui("Compile");
        ui.setVisible(true);
    }
}
