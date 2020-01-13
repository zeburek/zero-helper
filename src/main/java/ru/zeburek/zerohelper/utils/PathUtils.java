package ru.zeburek.zerohelper.utils;

/**
 * Created by zeburek on 11.06.2017.
 */
public class PathUtils {
    public static String replaceEmptyFields(String path){
        return path.replace(" ","\\ ");
    }

    public static void replaceEmptyFieldsOnString(String path){
        path.replace(" ","\\ ");
    }
}
