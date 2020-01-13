package ru.zeburek.zerohelper.controllers;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.scene.control.ComboBox;
import ru.zeburek.zerohelper.providers.RunsProvider;
import ru.zeburek.zerohelper.utils.LoggingAdapter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import static ru.zeburek.zerohelper.controllers.ResourcesController.ADB_EXE;

/**
 * Created by zeburek on 13.05.2017.
 */
public class DeviceController {

    public final RunsProvider runsProvider;

    public Process prDev = null;
    public Process prPack = null;

    final ObservableList<String> appObservableList  = FXCollections.observableArrayList();
    final SortedList<String> appSortedList = new SortedList<>( appObservableList );

    Map<String,String> deviceNameSerialNoMap = new LinkedHashMap<>();
    Map<String,String> appPackageVersionMap = new LinkedHashMap<>();

    public DeviceController(RunsProvider runsProvider){
        this.runsProvider = runsProvider;
    }

    public void getDevicesList(ComboBox list) {
        try {
            deviceNameSerialNoMap.clear();
            ProcessBuilder ps = new ProcessBuilder(ADB_EXE.toString(),"devices");
            ps.redirectErrorStream(true);
            runsProvider.cancelLogcat(prDev);
            prDev = ps.start();
            list.getItems().clear();
            new Thread() {
                @Override
                public void run() {
                    try {
                        BufferedReader in = new BufferedReader(new InputStreamReader(prDev.getInputStream()));
                        String line;
                        while ((line = in.readLine()) != null) {
                            if (!line.contains("devices")){
                                line = line.replace("device", "");
                                line = line.replaceAll("\\s+","");
                                if (!"".equals(line)){
                                    String device = addDeviceBySerialNo(line);
                                    list.getItems().add(device);
                                    System.out.print(line+",");
                                }
                                setSelectionInComboBox(list);
                            }
                        }
                        LoggingAdapter.info("Exec","Finished");
                    } catch (IOException ex) {
                        Logger.getLogger(RunsProvider.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }.start();
        } catch (IOException ex) {
            Logger.getLogger(RunsProvider.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void getPackagesList(ComboBox list) {
        try {
            ProcessBuilder ps = new ProcessBuilder(ADB_EXE.toString(),"-s",runsProvider.selectedDevice,"shell","pm","list","packages","-3");
            ps.redirectErrorStream(true);
            runsProvider.cancelLogcat(prPack);
            prPack = ps.start();
            appObservableList.clear();
            appPackageVersionMap.clear();
            list.getItems().clear();
            new Thread() {
                @Override
                public void run() {
                    try {
                        BufferedReader in = new BufferedReader(new InputStreamReader(prPack.getInputStream()));
                        String line;
                        while ((line = in.readLine()) != null) {
                            line = line.replaceAll("package:","");
                            if (!"".equals(line)){
                                appObservableList.add(line);
                                System.out.print(line+",");
                            }
                        }
                        LoggingAdapter.info("Exec","Finished");
                    } catch (IOException ex) {
                        Logger.getLogger(RunsProvider.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    appSortedList.setComparator(Comparator.<String>naturalOrder());
                    list.getItems().addAll(appSortedList);
                    setSelectionInComboBox(list);
                    for (String appName:
                         appSortedList) {
                        appPackageVersionMap.put(appName,
                                getApplicationVersionNumber(runsProvider.selectedDevice,appName));
                    }
                }
            }.start();
        } catch (IOException ex) {
            Logger.getLogger(RunsProvider.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void setSelectionInComboBox(ComboBox list) {
        Platform.runLater(() -> {
            list.getSelectionModel().selectFirst();
        });
    }

    private String addDeviceBySerialNo(String deviceSerialNo){
        String manufacturer = getPropertyByName(deviceSerialNo,"ro.product.manufacturer");
        String model = getPropertyByName(deviceSerialNo,"ro.product.model");
        String resultDevice = manufacturer + " " + model;
        deviceNameSerialNoMap.put(resultDevice, deviceSerialNo);
        return resultDevice;
    }

    private String getPropertyByName(String deviceSerialNo, String propertyName){
        StringBuffer propertyBuffer = new StringBuffer("");
        try {
            ProcessBuilder ps = new ProcessBuilder(ADB_EXE.toString(),"-s",deviceSerialNo,"shell","getprop",propertyName);
            ps.redirectErrorStream(true);
            Process prProperty = ps.start();
            BufferedReader in = new BufferedReader(new InputStreamReader(prProperty.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                propertyBuffer.append(line);
                System.out.println(line);
            }
            LoggingAdapter.info("Exec","Finished");
        } catch (IOException ex) {
            Logger.getLogger(RunsProvider.class.getName()).log(Level.SEVERE, null, ex);
        }
        return propertyBuffer.toString();
    }

    private String getApplicationVersionNumber(String deviceSerialNo, String packageName){
        StringBuffer propertyBuffer = new StringBuffer("");
        try {
            ProcessBuilder ps = new ProcessBuilder(ADB_EXE.toString(),"-s",deviceSerialNo,"shell","dumpsys","package",packageName);
            ps.redirectErrorStream(true);
            Process prProperty = ps.start();
            BufferedReader in = new BufferedReader(new InputStreamReader(prProperty.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                if (line.toLowerCase().contains("versionName".toLowerCase())) {
                    propertyBuffer.append(line);
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(RunsProvider.class.getName()).log(Level.SEVERE, null, ex);
        }
        String result = propertyBuffer.toString();
        result = result.replace("versionName=","").trim();
        //LoggingAdapter.debug("DeviceController:AppVersion",packageName+"::"+result);
        return result;
    }
}
