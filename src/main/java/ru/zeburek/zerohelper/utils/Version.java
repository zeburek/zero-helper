package ru.zeburek.zerohelper.utils;

import ru.zeburek.zerohelper.ZeroHelper;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by zeburek on 03.07.2017.
 */
public class Version {
    public volatile static String VERSION;
    public Version(){
        VERSION = getVersionFromFile();
    }

    public String getVersionFromFile() {
        try (InputStream stream = ZeroHelper.class.getResourceAsStream("version.properties")) {
            Properties verProp = new Properties();
            verProp.load(stream);
            String major = verProp.getProperty("VERSION_MAJOR");
            String minor = verProp.getProperty("VERSION_MINOR");
            String subminor = verProp.getProperty("VERSION_PATCH");
            return major + "." + minor + "." + subminor;
        } catch (IOException e) {
            e.printStackTrace();
            return "1.0";
        }
    }
}
