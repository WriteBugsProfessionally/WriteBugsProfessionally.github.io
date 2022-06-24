package com.WriteBugsProfessionally.Java.Tools;

import java.util.Scanner;

public class ComputerLowGradeOfTools {
    public static void main(String[] args)
    {

        Scanner scanner = new Scanner(System.in);

        String isNext = "Yes";
        System.out.println("这是一个简易的加减乘除计算器");
        if (scanner.hasNextLine()) {
            while (isNext.equals("Yes")) {
                System.out.println("请输出您要进行的操作：");

                String str = scanner.next();
                switch (str) {
                    case "+":
                        System.out.println("请输入您要计算的两位数字：");
                        double a = scanner.nextDouble();
                        double b = scanner.nextDouble();
                        add(a, b);
                        break;

                    case "-":
                        System.out.println("请输入您要计算的两位数字：");
                        double c = scanner.nextDouble();
                        double d = scanner.nextDouble();
                        subtract(c, d);
                        break;

                    case "*":
                        System.out.println("请输入您要计算的两位数字：");
                        double e = scanner.nextDouble();
                        double f = scanner.nextDouble();
                        multiply(e, f);
                        break;

                    case "/":
                        System.out.println("请输入您要计算的两位数字：");
                        double g = scanner.nextDouble();
                        double h = scanner.nextDouble();
                        divide(g, h);
                        break;

                    default:
                        System.out.println("请合理输入！");
                }

                System.out.println("是否继续计算?(输入Yes继续，其他任意键结束)：");
                isNext = scanner.next();

            }
            scanner.close();
        }


    }

    // 加
    public static void add(double num1, double num2) {

        double result;

        result = num1 + num2;

        System.out.println("您输入的计算结果为：" + result);

        // return result;
    }

    // 减
    public static void subtract(double num1, double num2) {

        double result;

        result = num1 - num2;

        System.out.println("您输入的计算结果为：" + result);

        // return result;
    }

    // 乘
    public static void multiply(double num1, double num2) {

        double result;

        result = num1 * num2;

        System.out.println("您输入的计算结果为：" + result);

        // return result;
    }

    // 除
    public static void divide(double num1, double num2) {

        double result;

        if (num2 == 0) {
            System.out.println("错误");
            return;
        }

        result = num1 / num2;

        System.out.println("您输入的计算结果为：" + result);

        // return result;
    }

}


