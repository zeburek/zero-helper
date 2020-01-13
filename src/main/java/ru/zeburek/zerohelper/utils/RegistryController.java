package ru.zeburek.zerohelper.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import static ru.zeburek.zerohelper.ZeroHelper.APP_DATA_DIR;

/**
 * Created by zeburek on 04.07.2017.
 */
public class RegistryController {
    private final String registryFilePathString = APP_DATA_DIR + File.separator+"install.reg";
    private volatile String userDir;
    private volatile String registryCodeString = "Windows Registry Editor Version 5.00\n" +
            "\n" +
            "[HKEY_CLASSES_ROOT\\.apk]\n" +
            "\n" +
            "[HKEY_CLASSES_ROOT\\.apk\\DefaultIcon]\n" +
            "@=\"C:\\\\Program Files\\\\THelper\\\\THelper.exe\"\n" +
            "\n" +
            "[HKEY_CLASSES_ROOT\\.apk\\Shell]\n" +
            "\n" +
            "[HKEY_CLASSES_ROOT\\.apk\\Shell\\Open]\n" +
            "\"Icon\"=\"\\\"C:\\\\Program Files\\\\THelper\\\\THelper.exe\\\"\"\n" +
            "\n" +
            "[HKEY_CLASSES_ROOT\\.apk\\Shell\\Open\\Command]\n" +
            "@=\"C:\\\\Program Files\\\\THelper\\\\THelper.exe \\\"%1\\\"\"\n" +
            "\n" +
            "[HKEY_CURRENT_USER\\Software\\Classes\\.apk]\n" +
            "@=\"apk_auto_file\"" +
            "\n" +
            "[HKEY_CURRENT_USER\\Software\\Classes\\apk_auto_file]\n" +
            "\n" +
            "[HKEY_CURRENT_USER\\Software\\Classes\\apk_auto_file\\shell]\n" +
            "\n" +
            "[HKEY_CURRENT_USER\\Software\\Classes\\apk_auto_file\\shell\\open]\n" +
            "\n" +
            "[HKEY_CURRENT_USER\\Software\\Classes\\apk_auto_file\\shell\\open\\command]\n" +
            "@=\"C:\\\\Program Files\\\\THelper\\\\THelper.exe \\\"%1\\\"\"";

    public RegistryController(){
        if(SystemController.getCurrentOs().equals(SystemController.System.WINDOWS)) {
            if (System.getProperty("user.dir").endsWith("app")) {
                userDir = System.getProperty("user.dir").replace("\\app", "");
            }
            setRegistryCodeString();
            LoggingAdapter.debug("Registry", userDir);
            if (!isRegistryFileExist()) {
                saveStringToFile();
                executeRegistryFile();
            }
        }
    }

    private void setRegistryCodeString(){
        if (userDir != null){
            userDir = userDir.replace("\\","\\\\");
            registryCodeString = "Windows Registry Editor Version 5.00\n" +
                    "\n" +
                    "[HKEY_CLASSES_ROOT\\.apk]\n" +
                    "\n" +
                    "[HKEY_CLASSES_ROOT\\.apk\\DefaultIcon]\n" +
                    "@=\""+userDir+"\\\\THelper.exe\"\n" +
                    "\n" +
                    "[HKEY_CLASSES_ROOT\\.apk\\Shell]\n" +
                    "\n" +
                    "[HKEY_CLASSES_ROOT\\.apk\\Shell\\Open]\n" +
                    "\"Icon\"=\"\\\""+userDir+"\\\\THelper.exe\\\"\"\n" +
                    "\n" +
                    "[HKEY_CLASSES_ROOT\\.apk\\Shell\\Open\\Command]\n" +
                    "@=\""+userDir+"\\\\THelper.exe \\\"%1\\\"\"\n" +
                    "\n" +
                    "[HKEY_CURRENT_USER\\Software\\Classes\\.apk]\n" +
                    "@=\"apk_auto_file\"" +
                    "\n" +
                    "[HKEY_CURRENT_USER\\Software\\Classes\\apk_auto_file]\n" +
                    "\n" +
                    "[HKEY_CURRENT_USER\\Software\\Classes\\apk_auto_file\\shell]\n" +
                    "\n" +
                    "[HKEY_CURRENT_USER\\Software\\Classes\\apk_auto_file\\shell\\open]\n" +
                    "\n" +
                    "[HKEY_CURRENT_USER\\Software\\Classes\\apk_auto_file\\shell\\open\\command]\n" +
                    "@=\""+userDir+"\\\\THelper.exe \\\"%1\\\"\"";
        }
    }

    private void saveStringToFile(){
        try ( FileWriter fw = new FileWriter(registryFilePathString) ) {
            fw.write(registryCodeString);
            fw.close();
        }
        catch ( IOException e ) {
            System.out.println("Всё погибло! \n"+e);
        }
    }

    private boolean isRegistryFileExist(){
        File registryFile = new File(registryFilePathString);
        return registryFile.exists();
    }

    private void executeRegistryFile(){
        String execString = "cmd /c regedit /s "+registryFilePathString;
        Runtime rn=Runtime.getRuntime();
        try {
            Process pr=rn.exec(execString);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
