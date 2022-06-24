package com.WriteBugsProfessionally.Java.Tools;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class ComputerAdvancedOfTools {

        TextField tfResult;
        Button btNum[], btOper[], btPoint, btEqual, btBack, btSqrt, btPingfang, btLifang;
        int num1, num2;// 操作数
        char oper;// 操作符
        boolean isNum;// 代表数字标志位

        public void init() {
            Frame f;

            tfResult = new TextField();

            btNum = new Button[10];// 数组对象
            for (int i = 0; i <= 9; i++) {
                btNum[i] = new Button(i + "");
            }
            btOper = new Button[4];
            btOper[0] = new Button("+");
            btOper[1] = new Button("-");
            btOper[2] = new Button("*");
            btOper[3] = new Button("/");

            btPoint = new Button(".");
            btEqual = new Button("=");
            btBack = new Button("Backspace");
            btSqrt = new Button("Sqrt");
            btPingfang = new Button("2");
            btLifang = new Button("3");
            f = new Frame("计算器");

            f.setSize(300, 300);

            // 放置组件
            f.add(tfResult, "North");
            Panel panel = new Panel();
            f.add(panel, "Center");
            GridLayout gl = new GridLayout(5, 4);
            panel.setLayout(gl);
            panel.add(btNum[1]);
            panel.add(btNum[2]);
            panel.add(btNum[3]);
            panel.add(btOper[0]);

            panel.add(btNum[4]);
            panel.add(btNum[5]);
            panel.add(btNum[6]);
            panel.add(btOper[1]);

            panel.add(btNum[7]);
            panel.add(btNum[8]);
            panel.add(btNum[9]);
            panel.add(btOper[2]);

            panel.add(btNum[0]);
            panel.add(btPoint);
            panel.add(btEqual);
            panel.add(btOper[3]);

            panel.add(btBack);
            panel.add(btSqrt);
            panel.add(btPingfang);
            panel.add(btLifang);
            f.setVisible(true);

            // 注册监听
            f.addWindowListener(new MyWinLis());
            // 数字按钮注册监听
            MyNumLis mnl = new MyNumLis();
            for (int i = 0; i <= 9; i++)
                btNum[i].addActionListener(mnl);

            // 操作符按钮注册监听
            MyOperLis mol = new MyOperLis();
            for (int j = 0; j <= 3; j++)
                btOper[j].addActionListener(mol);
            // 等号按钮注册监听
            MyEqualLis mel = new MyEqualLis();
            btEqual.addActionListener(mel);
            // 删除按钮注册监听
            MyBackLis mbl = new MyBackLis();
            btBack.addActionListener(mbl);
            // 开根号按钮注册监听
            MySqrtLis msl = new MySqrtLis();
            btSqrt.addActionListener(msl);
            // 平方按钮注册监听
            MyPingfangLis mpl = new MyPingfangLis();
            btPingfang.addActionListener(mpl);
            // 百分号注册监听
            MyLifangLis mll = new MyLifangLis();
            btLifang.addActionListener(mll);
        }

        // 定义监听器类
        class MyNumLis implements ActionListener {
            public void actionPerformed(ActionEvent e) {
                String t = e.getActionCommand();
                String s = tfResult.getText();
                if (isNum == false)
                    tfResult.setText(t);
                else
                    tfResult.setText(s + t);
                isNum = true;
            }
        }

        class MySqrtLis implements ActionListener {
            public void actionPerformed(ActionEvent e) {
                double t = Double.parseDouble(tfResult.getText());
                tfResult.setText(Math.sqrt(t) + "");
                isNum = false;
            }
        }

        class MyPingfangLis implements ActionListener {
            public void actionPerformed(ActionEvent e) {
                num1 = Integer.parseInt(tfResult.getText());
                tfResult.setText(num1 * num1 + "");
            }
        }

        class MyLifangLis implements ActionListener {
            public void actionPerformed(ActionEvent e) {
                num1 = Integer.parseInt(tfResult.getText());
                tfResult.setText(num1 * num1 * num1 + "");
            }
        }

        class MyOperLis implements ActionListener {
            public void actionPerformed(ActionEvent e) {
                num1 = Integer.parseInt(tfResult.getText());// 转化为INT
                oper = e.getActionCommand().charAt(0);
                isNum = false;
            }
        }

        class MyEqualLis implements ActionListener {
            public void actionPerformed(ActionEvent e) {
                int result = 0;
                isNum = false;
                num2 = Integer.parseInt(tfResult.getText());
                switch (oper) {
                    case '+':
                        result = num1 + num2;
                        break;
                    case '-':
                        result = num1 - num2;
                        break;
                    case '*':
                        result = num1 * num2;
                        break;
                    case '/':
                        result = num1 / num2;
                        break;
                }
                tfResult.setText(result + "");
            }
        }

        class MyBackLis implements ActionListener {
            public void actionPerformed(ActionEvent e) {
                tfResult.setText("");
                isNum = false;
            }
        }

        class MyWinLis extends WindowAdapter {
            public void windowClosing(WindowEvent e) {
                e.getWindow().setVisible(false);
                e.getWindow().dispose();
                System.exit(0);
            }
        }

        public static void main(String[] args) {
            // TODO Auto-generated method stub
            new ComputerAdvancedOfTools().init();
        }

    }

