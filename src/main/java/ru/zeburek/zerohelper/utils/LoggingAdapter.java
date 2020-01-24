/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.zeburek.zerohelper.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author zeburek
 */
public class LoggingAdapter {
    private static Logger logger = LoggerFactory.getLogger("zerohelper");


    public static void info(String name, String... message){
        logger.info("["+name+"]: "+ mytoString(message,", "));
    }

    public static void debug(String name, String... message){
        logger.debug("["+name+"]: "+ mytoString(message,", "));
    }

    public static void stat(String name, String... message){
        logger.debug("["+name+"]: "+ mytoString(message,", "));
    }

    public static void error(String name, String... message){
        logger.error("["+name+"]: "+ mytoString(message,", "));
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
