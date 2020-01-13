package ru.zeburek.zerohelper.utils;

import org.apache.commons.lang.SystemUtils;

/**
 * Created by zeburek on 16.06.17.
 */
public class SystemController {
    public enum System {
        LINUX,
        WINDOWS,
        MACOS,
        UNKNOWN
    }

    public static System getCurrentOs(){
        if (SystemUtils.IS_OS_MAC){
            return System.MACOS;
        } else if (SystemUtils.IS_OS_LINUX){
            return System.LINUX;
        } else if (SystemUtils.IS_OS_WINDOWS){
            return System.WINDOWS;
        } else {
            return System.UNKNOWN;
        }
    }

    public static String getCurrentOsString(){
        if (SystemUtils.IS_OS_MAC){
            return "MacOS";
        } else if (SystemUtils.IS_OS_LINUX){
            return "Linux";
        } else if (SystemUtils.IS_OS_WINDOWS){
            return "Windows";
        } else {
            return "Unknown";
        }
    }
}
