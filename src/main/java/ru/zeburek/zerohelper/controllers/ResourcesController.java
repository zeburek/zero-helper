package ru.zeburek.zerohelper.controllers;

import javafx.application.Platform;
import javafx.scene.control.ButtonType;
import org.apache.commons.lang.SystemUtils;
import ru.zeburek.zerohelper.ZeroHelper;
import ru.zeburek.zerohelper.controlls.Alert;
import ru.zeburek.zerohelper.utils.LoggingAdapter;
import ru.zeburek.zerohelper.utils.SystemController;

import java.io.*;
import java.nio.file.*;

import static ru.zeburek.zerohelper.ZeroHelper.*;

/**
 * Created by zeburek on 12.05.2017.
 */
public class ResourcesController {

    public static Path BASE_PATH = FileSystems.getDefault().getPath(ZeroHelper.APP_DATA_DIR+"/resources");

    public static Path ADB_EXE;
    public static Path ADBWINAPI_DLL;
    public static Path ADBWINUSBAPI_DLL;

    public static boolean IS_ADB_UNPACKED;

    public ResourcesController(){
        LoggingAdapter.debug("System",SystemController.getCurrentOsString());
        try {
            createDataPathsIfNotExist();
            if(SystemController.getCurrentOs().equals(SystemController.System.WINDOWS)) {
                initResourcesForWindows();
            } else if (SystemController.getCurrentOs().equals(SystemController.System.LINUX)){
                initResourcesForLinux();
            } else if (SystemController.getCurrentOs().equals(SystemController.System.MACOS)){
                initResourcesForMac();
            } else {
                AN_SEND.trackStatistic("ERROR","Unknown OS", SystemUtils.OS_NAME);
                Alert alert =
                        new Alert(
                                javafx.scene.control.Alert.AlertType.INFORMATION,
                                "Error",
                                "Something went wrong",
                                "We couldn't identify your OS, contact us via https://parviz.pw site!",
                                ButtonType.OK);
                alert.showAndWait();
                Platform.exit();
                System.exit(0);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createDataPathsIfNotExist() {
        File homeDir = new File(HOME_DIR);
        makeDir(homeDir);
        File appDataDir = new File(HOME_DIR + File.separator + "AppData");
        makeDir(appDataDir);
        File localAppDAtaDir = new File(HOME_DIR + File.separator + "AppData" + File.separator + "Local");
        makeDir(localAppDAtaDir);
        File thelperAppDataDir = new File(APP_DATA_DIR);
        makeDir(thelperAppDataDir);
        File baseDir = new File(BASE_PATH.toString());
        makeDir(baseDir);
        File videoDir = new File(HOME_DIR + File.separator + "Videos");
        makeDir(videoDir);
        File screenShotsDir = new File(HOME_DIR + File.separator + "Pictures");
        makeDir(screenShotsDir);
        File androidScreenShotsDir =
                new File(HOME_DIR + File.separator + "Pictures"+ File.separator +"Android");
        makeDir(androidScreenShotsDir);
    }

    private void makeDir(File homeDir) {
        if (!homeDir.exists()){
            homeDir.mkdir();
        }
    }

    private void initResourcesForWindows() throws IOException {
        String actualAdb = whichAdb();
        if (actualAdb.equals("")){
            IS_ADB_UNPACKED = true;
            File baseDir = new File(BASE_PATH.toString());
            if(!isDirEmpty(BASE_PATH)){
                removeNonEmptyDirectory(baseDir);
            }
            ADB_EXE = FileSystems.getDefault().getPath(BASE_PATH.toString() + File.separator + "adb" + ".exe");
            ADBWINAPI_DLL = FileSystems.getDefault().getPath(BASE_PATH.toString() + File.separator + "AdbWinApi" + ".dll");
            ADBWINUSBAPI_DLL = FileSystems.getDefault().getPath(BASE_PATH.toString() + File.separator + "AdbWinUsbApi" + ".dll");

            extractRecourceByPath(ADB_EXE, "adb.exe");
            extractRecourceByPath(ADBWINAPI_DLL, "AdbWinApi.dll");
            extractRecourceByPath(ADBWINUSBAPI_DLL, "AdbWinUsbApi.dll");
        } else {
            IS_ADB_UNPACKED = false;
            ADB_EXE = FileSystems.getDefault().getPath(actualAdb);
        }

        checkIfAdbInstallationExist(ADB_EXE);
    }

    private void initResourcesForLinux() {
        try {
            ADB_EXE = FileSystems.getDefault().getPath(whichAdb());
        } catch (IOException e) {
            e.printStackTrace();
        }
        checkIfAdbInstallationExist(ADB_EXE);
    }

    private void initResourcesForMac() {
        try {
            ADB_EXE = FileSystems.getDefault().getPath(whichAdb());
        } catch (IOException e) {
            e.printStackTrace();
        }
        checkIfAdbInstallationExist(ADB_EXE);
    }

    public void extractRecourceByPath(Path outputPath,String nameOfExistingFile){
        try (InputStream stream = ZeroHelper.class.getResourceAsStream(nameOfExistingFile)) {
            Files.copy(stream, outputPath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean removeAllFilesExists(){
        if (SystemController.getCurrentOs().equals(SystemController.System.WINDOWS) && IS_ADB_UNPACKED){
            LoggingAdapter.debug("Removing unpacked adb files");
            boolean first = removeFilesExists(ADB_EXE);
            boolean second = removeFilesExists(ADBWINAPI_DLL);
            boolean third = removeFilesExists(ADBWINUSBAPI_DLL);
            return first && second && third;
        } else {
            return true;
        }
    }

    boolean removeFilesExists(Path pathToFile){
        try {
            return Files.deleteIfExists(pathToFile);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private static boolean isDirEmpty(final Path directory) throws IOException {
        try(DirectoryStream<Path> dirStream = Files.newDirectoryStream(directory)) {
            return !dirStream.iterator().hasNext();
        }
    }

    private void removeNonEmptyDirectory(File index){
        String[]entries = index.list();
        for(String s: entries){
            File currentFile = new File(index.getPath(),s);
            currentFile.delete();
        }
    }

    private String whichAdb() throws IOException {
        ProcessBuilder builder;
        if(SystemController.getCurrentOs().equals(SystemController.System.WINDOWS)) {
            builder = new ProcessBuilder("cmd.exe", "/c", "where adb");
        } else {
            builder = new ProcessBuilder("bash", "-c", "which adb");
        }

        builder.redirectErrorStream(true);
        Process p = builder.start();
        BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
        StringBuilder buffer = new StringBuilder();
        String line;
        while (((line = r.readLine()) != null) || p.isAlive()) {
            if (line != null) {
                buffer.append(line);
            }
        }
        int exit_val;
        try {
            exit_val = p.exitValue();
        } catch (IllegalThreadStateException e) {
            LoggingAdapter.error("System", "The process is not exited yet. Killing. Stack: " + e);
            exit_val = 404;
        }

        LoggingAdapter.info("System","Is there any ADB installed: " + buffer +
                " Status: " + exit_val);
        if (exit_val == 0) {
            return new String(buffer).trim();
        } else {
            return "";
        }
    }

    private void checkIfAdbInstallationExist(Path adbExe) {
        File adbFilePath = adbExe.toFile();
        if (!adbFilePath.exists() || !adbFilePath.canExecute()){
            Alert alert = new Alert(javafx.scene.control.Alert.AlertType.WARNING,
                    "Resources checker",
                    "Something wrong with ADB installation!" +
                            "\n Zero Helper won't work correct!",
                    "We couldn't find your ADB installation, may be you forget to install it? \n" +
                            "Your ADB file should be declared in PATH, or you should link it to /usr/bin/adb \n" +
                            "On Linux easiest way: sudo apt-get install android-sdk",
                    ButtonType.OK);
            alert.setHeight(500d);
            alert.setWidth(1000d);
            alert.showAndWait();
        }
    }


}
