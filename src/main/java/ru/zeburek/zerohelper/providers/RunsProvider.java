/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.zeburek.zerohelper.providers;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.scene.control.*;
import javafx.scene.paint.Paint;
import javafx.stage.FileChooser;
import ru.zeburek.zerohelper.ZeroHelper;
import ru.zeburek.zerohelper.utils.LoggingAdapter;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;
import java.io.*;
import java.util.*;
import java.util.Timer;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import static ru.zeburek.zerohelper.ZeroHelper.HOME_DIR;
import static ru.zeburek.zerohelper.controllers.ResourcesController.ADB_EXE;

/**
 *
 * @author zeburek
 */
public class RunsProvider {

    public String[] reloadStrings(String str){
        return reloadStrings(str,false);
    }

    public String[] reloadStrings(String str,boolean skipCheck){
        if (selectedDevice!=null || skipCheck){
            switch (str){
                case "devicesListString":
                    String[] devicesListString = {ADB_EXE.toString(),"devices"};
                    return devicesListString;
                case "devicesReconnectString":
                    String[] devicesReconnectString = {ADB_EXE.toString(),"reconnect"};
                    return devicesReconnectString;
                case "logcatMainString":
                    String[] logcatMainString={ADB_EXE.toString(),"-s",selectedDevice,"logcat","-v","brief"};
                    return logcatMainString;
                case "recordVideoString":
                    String[] recordVideoString={ADB_EXE.toString(),"-s",selectedDevice,"shell","screenrecord","/data/local/tmp/"+videoName+".mp4"};
                    return recordVideoString;
                case "recordVideoCopyString":
                    String[] recordVideoCopyString={ADB_EXE.toString(),"-s",selectedDevice,"pull","-p","/data/local/tmp/"+videoName+".mp4",HOME_DIR+"/Videos/"};
                    return recordVideoCopyString;
                case "recordVideoRemoveString":
                    String[] recordVideoRemoveString={ADB_EXE.toString(),"-s",selectedDevice,"shell","rm","/data/local/tmp/"+videoName+".mp4"};
                    return recordVideoRemoveString;
                case "enableLoggingFileString":
                    String[] enableLoggingFileString={ADB_EXE.toString(),"-s",selectedDevice,"shell","cd /sdcard && 1 > enable_logging"};
                    return enableLoggingFileString;
                case "clearLogcatString":
                    String[] clearLogcatString={ADB_EXE.toString(),"-s",selectedDevice,"logcat","-c"};
                    return clearLogcatString;
                case "filterFieldString":
                    String[] filterFieldString = {ADB_EXE.toString(),"-s",selectedDevice,"logcat","-v","brief"};
                    return filterFieldString;
                case "forceStopString":
                    String[] forceStopString={ADB_EXE.toString(),"-s",selectedDevice,"shell","am","force-stop",packageName};
                    return forceStopString;
                case "moreLocaleAccessString":
                    String[] moreLocaleAccessString={ADB_EXE.toString(),"-s",selectedDevice,"shell","pm","grant","jp.co.c_lis.ccl.morelocale","android.permission.CHANGE_CONFIGURATION"};
                    return moreLocaleAccessString;
                case "versionCodeString":
                    String[] versionCodeString={ADB_EXE.toString(),"-s",selectedDevice,"shell","dumpsys","package",packageName};
                    return versionCodeString;
                case "uninstallAppString":
                    String[] uninstallAppString={ADB_EXE.toString(),"-s",selectedDevice,"shell","pm","uninstall",packageName};
                    return uninstallAppString;
                case "clearDataOfAppString":
                    String[] clearDataOfAppString={ADB_EXE.toString(),"-s",selectedDevice,"shell","pm","clear",packageName};
                    return clearDataOfAppString;
                case "browserResetSettingsSpecialString":
                    String[] browserResetSettingsSpecialString={ADB_EXE.toString(),"-s",selectedDevice,"shell","am","broadcast","-a",packageName+".RESET_SETTINGS","-n",packageName+"/"+packageName+".ResetSettingsReceiver"};
                    return browserResetSettingsSpecialString;
                case "createScreenshot":
                    String[] createScreenshotString={ADB_EXE.toString(),"-s",selectedDevice,"shell","screencap","/sdcard/Screenshot_TH.png"};
                    return createScreenshotString;
                case "copyScreenshot":
                    String[] copyScreenshotString={ADB_EXE.toString(),"-s",selectedDevice,"pull","-p","/sdcard/Screenshot_TH.png",HOME_DIR+"/Pictures/Android/"};
                    return copyScreenshotString;
                default:
                    String[] strN = {""};
                    return strN;
            }
        }else{
            Alert alert = 
                    new Alert(
                            Alert.AlertType.INFORMATION, 
                            "Please select device first!", 
                            ButtonType.OK);
            alert.setHeaderText("No device selected!");
            alert.setTitle("No device selected");
            alert.showAndWait();
        }
        return null;
    }
    
    public Timer bufferMainTimer = new Timer();
    public TimerTask bufferMainTimerTask;
    public Timer highlighterMainTimer = new Timer();
    public TimerTask highlighterMainTimerTask;
    public long currentTimeStamp;
    public StringBuffer mainOutputBufferString = new StringBuffer("");
    public StringBuffer testOutputBufferString = new StringBuffer("");
    public boolean globalFilterEnabled = false;
    public boolean haveEverStartedTimer = false;
    public String packageName;
    public String selectedDevice;
    public String globalFilter;
    public String videoName;

    public FileChooser dialogSaveToFile = new FileChooser();
    private File lastDirectorySaveToFile = new File(System.getProperty("user.home"));

    final ObservableList<String> appObservableList  = FXCollections.observableArrayList();
    final SortedList<String>     appSortedList = new SortedList<>( appObservableList );
    
    public Process prDev = null;
    public Process prPack = null;
    public Process video = null;
    public Process prOther = null;
    public Process prAppInstall = null;
    public Thread pbThread = null;
    
    public Map<Tab,Process> tabAndProcessMap = new HashMap<>();
    public Map<JTextPane,Timer> styledTextAreaOutputTimerMap = new HashMap<>();
    public Map<JTextPane,StringBuffer> styledTextAreaStringBufferMap = new HashMap<>();
    public Map<Timer,TimerTask> outputTimerTimerTaskMap = new HashMap<>();

    public void setAllStyledTextAreaMaps(JTextPane textArea){
        LoggingAdapter.debug("System", "Styling text areas");
        if (textArea == null) {return;}
        StringBuffer stringBuffer = new StringBuffer("");
        setStyledTextAreaStringBufferMap(textArea,stringBuffer);
    }

    private void setStyledTextAreaOutputTimerMap(JTextPane textArea, Timer timer){
        styledTextAreaOutputTimerMap.put(textArea,timer);
    }

    private void setStyledTextAreaStringBufferMap(JTextPane textArea, StringBuffer stringBuffer){
        styledTextAreaStringBufferMap.put(textArea,stringBuffer);
    }

    private void setOutputTimerTimerTaskMap(Timer timer, TimerTask timerTask){
        outputTimerTimerTaskMap.put(timer,timerTask);
    }

    public Timer getTimerByStyledTextArea(JTextPane textArea){
        return styledTextAreaOutputTimerMap.get(textArea);
    }

    private StringBuffer getStringBufferByStyledTextArea(JTextPane textArea){
        return styledTextAreaStringBufferMap.get(textArea);
    }

    public TimerTask getTimerTaskByStyledTextArea(JTextPane textArea){
        return outputTimerTimerTaskMap.get(styledTextAreaOutputTimerMap.get(textArea));
    }

    public boolean isTimerTaskByStyledTextArea(JTextPane textArea){
        return outputTimerTimerTaskMap.containsKey(styledTextAreaOutputTimerMap.get(textArea));
    }

    public void removeStyledTextAreaOutputTimerMap(JTextPane textArea){
        styledTextAreaOutputTimerMap.remove(textArea);
    }

    public void removeStyledTextAreaStringBufferMap(JTextPane textArea){
        styledTextAreaStringBufferMap.remove(textArea);
    }

    public void removeOutputTimerTimerTaskMap(Timer timer){
        outputTimerTimerTaskMap.remove(timer);
    }
    
    public void runLogcat(String[] str, boolean FiltEn, String filter, JTextPane tp, CheckBox c, Tab tab) {
        try {
            LoggingAdapter.debug("Exec command",Arrays.toString(str));
            clearSelect(c, tp);
            cancelLogcat(getAssociatedTabProcess(tab));
            //Timer Block begin
            if(isTimerTaskByStyledTextArea(tp)){getTimerTaskByStyledTextArea(tp).cancel();}
            startOutputTimer(tp);
            //Timer Block ends
            Process pr = initNewProcessBuilderOnProcess(tab, str);
            if (pr == null){
                LoggingAdapter.info("Exec command","Stopping, nothing to execute");
                return;
            }
            new Thread() {
                @Override
                public void run() {
                    BufferedReader in = new BufferedReader(new InputStreamReader(pr.getInputStream()));
                    String line;
                    try {
                        while ((line = in.readLine()) != null) {
                            if (FiltEn == false) {
                                if(!line.equals(""))
                                {
                                    getStringBufferByStyledTextArea(tp).append(line + "\n");
                                }
                            } else {
                                String[] filters = filter.split("\\|");
                                for (String str:
                                     filters) {
                                    if (line.toLowerCase().contains(str.toLowerCase())) {
                                        getStringBufferByStyledTextArea(tp).append(line + "\n");
                                    }
                                }
                            }
                        }
                        //pr.waitFor();
                    } catch (IOException ex) {
                        Logger.getLogger(RunsProvider.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    LoggingAdapter.info("Exec","Finished");
                }
            }.start();
        } catch (IOException ex) {
            Logger.getLogger(RunsProvider.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    public void runSimpleCommand(String[] str, Label label, ProgressIndicator pa, String name){
        try {
            LoggingAdapter.debug("Exec command",Arrays.toString(str));
            System.out.println(Arrays.toString(str));
            ProcessBuilder ps = new ProcessBuilder(str);
            ps.redirectErrorStream(true);
            cancelLogcat(prOther);
            prOther = ps.start();
            label.setText(name);
            pa.setVisible(true);
            new Thread() {
                @Override
                public void run() {
                    BufferedReader in = new BufferedReader(new InputStreamReader(prOther.getInputStream()));
                    String line;
                    try {
                        while ((line = in.readLine()) != null) {
                            if(!line.toLowerCase().contains("success"))
                            {
                                //empty
                            }
                        }

                        LoggingAdapter.info("Exec","Finished");
                        Platform.runLater(() -> {
                            label.setText("Success");
                            label.setTextFill(Paint.valueOf("green"));
                            pa.setVisible(false);
                        });
                        Thread.sleep((long) 3000);
                        Platform.runLater(() -> {
                            label.setText("");
                            label.setTextFill(Paint.valueOf("black"));
                        });                        
                    } catch (IOException | InterruptedException ex) {
                        Logger.getLogger(RunsProvider.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }.start();
        } catch (IOException ex) {
            Logger.getLogger(RunsProvider.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void runSilentCommand(String[] str){
        try {
            LoggingAdapter.debug("Exec command",Arrays.toString(str));
            ProcessBuilder ps = new ProcessBuilder(str);
            ps.redirectErrorStream(true);
            cancelLogcat(prOther);
            prOther = ps.start();
        } catch (IOException ex) {
            Logger.getLogger(RunsProvider.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void runAppInstall(String[] str, ProgressBar ta, Label label) throws IOException, InterruptedException {
        LoggingAdapter.debug("Exec command",Arrays.toString(str));
        ProcessBuilder ps = new ProcessBuilder(str);
        ps.redirectErrorStream(true);
        cancelLogcat(prAppInstall);
        prAppInstall = ps.start();
        new Thread() {
            @Override
            public void run() {
                try {
                    for (int i = 0; i <= 30; i++) {
                        ta.setProgress(0.01*i);
                        Thread.sleep(100);
                        Platform.runLater(() -> {
                            label.setText("Copying...");
                        });
                    }
                    BufferedReader in = new BufferedReader(new InputStreamReader(prAppInstall.getInputStream()));
                    String line;
                    StringBuilder stringBuilder = new StringBuilder();
                    while ((line = in.readLine()) != null) {
                        stringBuilder.append(line+"\n");
                        if (line.toLowerCase().contains("pkg")){
                            for (int i = 31; i <= 85; i++) {
                                ta.setProgress(0.01*i);
                                Thread.sleep(100);
                                Platform.runLater(() -> {
                                    label.setText("Installing...");
                                });
                            }
                        }
                        if (line.toLowerCase().contains("success")){
                            for (int i = 86; i <= 100; i++) {
                                ta.setProgress(0.01*i);
                                Thread.sleep(30);
                                Platform.runLater(() -> {
                                    label.setText("Success");
                                    label.setTextFill(Paint.valueOf("green"));
                                });
                            }
                            Thread.sleep((long) 3000);
                            Platform.runLater(() -> {
                                label.setText("");
                                label.setTextFill(Paint.valueOf("black"));
                            });
                        }
                        if (line.toLowerCase().contains("failure") || line.toLowerCase().contains("error")){
                            ta.setProgress(0.01*100);
                            String error = line;
                            Platform.runLater(() -> {
                                Alert alert = 
                                        new Alert(
                                                Alert.AlertType.ERROR, 
                                                "It seems that we have an error while installing:\n"+error+"",
                                                ButtonType.OK);
                                alert.setHeaderText("Installation error!");
                                alert.setTitle("Installation error");
                                alert.showAndWait();
                                label.setText("Failure!");
                            });
                            Thread.sleep((long) 3000);
                            Platform.runLater(() -> {
                                label.setText("");
                            });
                        }
                    }
                    LoggingAdapter.debug("Exec command",stringBuilder.toString());
                    LoggingAdapter.info("Exec","Finished");
                } catch (IOException | InterruptedException ex) {
                    Logger.getLogger(ZeroHelper.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }.start();
    }
    
    public void videoRecord(String[] str, ProgressBar ta) {
        videoRecord(str,ta,1);
    }

    public void videoRecord(String[] str, ProgressBar ta, int step) {
        try {
            LoggingAdapter.debug("Exec command",Arrays.toString(str));
            ProcessBuilder ps = new ProcessBuilder(str);
            ps.redirectErrorStream(true);
            cancelLogcat(video);
            video = ps.start();
            Thread t = new Thread() {
                @Override
                public void run() {
                    BufferedReader on = new BufferedReader(new InputStreamReader(video.getInputStream()));
                    String line;
                    try {
                        while ((line = on.readLine()) != null) {
                            if (step==1){
                                Platform.runLater(() -> {
                                    ta.setProgress(ProgressBar.INDETERMINATE_PROGRESS);
                                });
                            }
                            else if (step==2){
                                makeProgressBarBeSmilie(ta);
                            }
                        }
                        if (step == 2){
                            videoRecord(reloadStrings("recordVideoRemoveString"),ta,3);
                        }
                        video.waitFor();
                    } catch (IOException | InterruptedException ex) {
                        Logger.getLogger(RunsProvider.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    LoggingAdapter.info("Exec","Finished");
                    Platform.runLater(() -> {
                        ta.setProgress(100);
                    });
                }
            };
            t.start();
        } catch (IOException ex) {
            Logger.getLogger(RunsProvider.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void cancelLogcat(Process pr) {
        testOutputBufferString.setLength(0);
        if (pr != null) {
            pr.destroyForcibly();
            pr = null;
        }
    }
    
    public void clearSelect(CheckBox c, JTextPane ta) {
        if (c.isSelected()) {
            ta.setText("");
        }
    }
    
    private void makeProgressBarBeSmilie(ProgressBar pb){
        if(pbThread!=null){pbThread.interrupt();}
        pbThread = new Thread(() -> {
            for (int i = 0; i <= 100; i = i+2) {
                int z = i;
                Platform.runLater(() -> {
                    pb.setProgress(z);
                });
                try {
                    TimeUnit.MILLISECONDS.sleep(200);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });
        pbThread.start();
    }

    public Process getAssociatedTabProcess(Tab tab){
        return tabAndProcessMap.get(tab);
    }
    
    public Process initNewProcessBuilderOnProcess(Tab tab, String[] str) throws IOException {
        if (str == null){
            LoggingAdapter.error("Exec command","Command is empty, may be no devices selected");
            return null;
        }
        ProcessBuilder vr = new ProcessBuilder(str);
        vr.directory(new File(System.getProperty("user.dir")));
        vr.redirectErrorStream(true);
        Process pr = vr.start();
        tabAndProcessMap.put(tab, pr);
        return pr;
    }

    private void startOutputTimer(JTextPane ta) {
        removeOutputTimerTimerTaskMap(getTimerByStyledTextArea(ta));
        removeStyledTextAreaOutputTimerMap(ta);
        StringBuffer control = new StringBuffer("");
        control.setLength(0);
        Timer timer = new Timer();
        TimerTask timerTask =  new TimerTask() {
            @Override
            public void run() {
                StringBuffer stringBuffer = getStringBufferByStyledTextArea(ta);
                if (!stringBuffer.toString().equals(control.toString()))
                {
                    Platform.runLater(() -> {
                        try {
                            appendToJTPane(getStringBufferByStyledTextArea(ta).toString(),ta);
                        } catch (BadLocationException e) {
                            e.printStackTrace();
                        }
                        getStringBufferByStyledTextArea(ta).setLength(0);
                    });
                }
            }
        };

        timer.schedule(timerTask, 100, 100);
        setOutputTimerTimerTaskMap(getTimerByStyledTextArea(ta),timerTask);
        setStyledTextAreaOutputTimerMap(ta,timer);
    }

    public void appendToJTPane(String str, JTextPane tp) throws BadLocationException
    {
        StyledDocument document = (StyledDocument) tp.getDocument();
        document.insertString(document.getLength(), str, null);
    }

    public void saveToFile(String text) {
        FileChooser.ExtensionFilter filter =
                new FileChooser.ExtensionFilter("Text files","*.txt");
        dialogSaveToFile.getExtensionFilters().add(filter);
        dialogSaveToFile.setTitle("Save output");
        dialogSaveToFile.setInitialDirectory(new File(lastDirectorySaveToFile.getParent()));
        File result = dialogSaveToFile.showSaveDialog(null);
        if (result != null){
            if(!result.getName().contains(".txt") || !result.getName().contains(".log")){
                result = new File(result.getPath().concat(".txt"));
            }
            lastDirectorySaveToFile = result;
            try ( FileWriter fw = new FileWriter(result) ) {
                fw.write(text);
            }
            catch ( IOException e ) {
                System.out.println("Всё погибло! \n"+e);
            }
        }
    }
    
}
