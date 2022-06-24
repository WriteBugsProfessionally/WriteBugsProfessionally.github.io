package com.WriteBugsProfessionally.Java.Other;

import java.util.stream.IntStream;

public class The520OfOther {
    public static void main(String[] args) {
        IntStream.range(-50, 50).map(y -> -y).forEach(
                y -> IntStream.range(-50, 50).forEach(x ->
                        System.out.print(Math.pow(
                                Math.pow((x * 0.05), 2) + Math.pow((y * 0.1), 2) - 1, 3) - Math.pow(x * 0.05, 2) *
                                Math.pow(y * 0.1, 3) <= 0 ? "love".charAt(
                                Math.abs((y - x) % 4)) : " " + (x == 30 ? "\n" : ""))));
    }
}

