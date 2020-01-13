/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.zeburek.zerohelper.utils;

import static ru.zeburek.zerohelper.ZeroHelper.ENABLE_DEBUG;

/**
 *
 * @author zeburek
 */
public class LoggingAdapter {

    protected static final String COLOR_RESET   = "\u001b[0m";
    protected static final String COLOR_BOLD   = "\u001b[1m";
    protected static final String COLOR_BLINK   = "\u001b[5m";
    protected static final String COLOR_RED   = "\u001b[31m";
    protected static final String COLOR_GREEN   = "\u001b[32m";
    protected static final String COLOR_BLUE   = "\u001b[34m";


    public static void info(String name, String... message){
        System.out.println(COLOR_BOLD+"[INFO]"+COLOR_RESET+"["+name+"]: "+ mytoString(message,", "));
    }

    public static void debug(String name, String... message){
        if (!ENABLE_DEBUG) return;
        System.out.println(COLOR_BOLD+COLOR_BLUE+"[DEBUG]"+COLOR_RESET+
                "["+name+"]: "+ mytoString(message,", "));
    }

    public static void stat(String name, String... message){
        if (!ENABLE_DEBUG) return;
        System.out.println(COLOR_BOLD+COLOR_GREEN+"[STATS]"+COLOR_RESET+
                "["+name+"]: "+ mytoString(message,", "));
    }

    public static void error(String name, String... message){
        System.out.println(COLOR_BOLD+COLOR_RED+COLOR_BLINK+"[ERROR]"+COLOR_RESET+COLOR_RED+
                "["+name+"]: "+ mytoString(message,", ")+COLOR_RESET);
    }

    private static String mytoString(String[] theAray, String delimiter) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < theAray.length; i++) {
            if (i > 0) {
                sb.append(delimiter);
            }
            String item = theAray[i];
            sb.append(item);
        }
        return sb.toString();
    }
}
