/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.zeburek.zerohelper.controllers;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import javafx.application.Application;
import javafx.application.HostServices;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingNode;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.TextFlow;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import ru.zeburek.zerohelper.providers.CustomizeOutputProvider;
import ru.zeburek.zerohelper.providers.LayoutProvider;
import ru.zeburek.zerohelper.providers.RunsProvider;
import ru.zeburek.zerohelper.providers.SettingsProvider;
import ru.zeburek.zerohelper.utils.LoggingAdapter;
import ru.zeburek.zerohelper.utils.SystemController;
import ru.zeburek.zerohelper.vendors.Vendor;

import javax.swing.*;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import static ru.zeburek.zerohelper.ZeroHelper.AN_SEND;
import static ru.zeburek.zerohelper.ZeroHelper.HOME_DIR;
import static ru.zeburek.zerohelper.ZeroHelper.TRAY_ICON;
import static ru.zeburek.zerohelper.controllers.ResourcesController.ADB_EXE;

/**
 *
 * @author zeburek
 */
public class FXMLDocumentController implements Initializable {
    private static final Logger LOG = Logger.getLogger(String.valueOf(FXMLDocumentController.class));

    //List of my Variables
    public volatile static RunsProvider thrp = new RunsProvider();
    public static final Clipboard clipboard = Clipboard.getSystemClipboard();
    private LayoutProvider thlp = new LayoutProvider();
    public CustomizeOutputProvider thcop;
    public SettingsProvider thsp;
    private BorderPaneController thbpc;
    public DeviceController thdc = new DeviceController(thrp);
    public static Vendor VENDOR;
    private SearchController searchController = new SearchController();

    public HostServices mainHostServices;
    public Stage mainStage;
    public FileChooser dialog = new FileChooser();
    private File lastDirectory = new File(System.getProperty("user.home"));
    private boolean isVideoRecording = false;
    private long currentTimeStamp;
    //End
    @FXML
    public Parent root;
    @FXML
    private SplitMenuButton logcatStartMenuItem;
    @FXML
    private MenuItem logcatStopMenuItem;
    @FXML
    private MenuItem logcatClearMenuItem;
    @FXML
    private MenuItem devicesUnAuthMenuItem;
    @FXML
    private MenuItem appClearMenuItem;
    @FXML
    private MenuItem appUninstallMenuItem;
    @FXML
    private MenuItem appStopMenuItem;
    @FXML
    private ComboBox<?> deviceIdComboBox;
    @FXML
    private ComboBox<?> packageNameComboBox;
    @FXML
    private Button reloadListsButton;
    @FXML
    private MenuItem appVersionMenuItem;
    @FXML
    private TabPane outputTabbedPane;
    @FXML
    private Button tabsAddTubButton;
    @FXML
    private SplitMenuButton devicesListSplitButton;
    @FXML
    private SplitMenuButton appInstallSplitButton;
    @FXML
    private SplitMenuButton screenVideoRecordSplitButton;
    @FXML
    private MenuItem screenTakeScreenshotMenuItem;
    @FXML
    private MenuButton menuBarMenuButton;
    @FXML
    public TextField filterTextField;
    @FXML
    public TextField searchTextField;
    @FXML
    private ToolBar topControlsToolbar;
    @FXML
    private CheckBox outputClearCheckBox;
    @FXML
    private ProgressBar progressBar;
    @FXML
    private ProgressIndicator progressSpinner;
    @FXML
    private Label progressActivityLabel;
    @FXML
    private ToolBar bottomControlsToolbar;
    @FXML
    private CheckMenuItem rApkInstallCheckbox;
    @FXML
    private CheckMenuItem dApkInstallCheckbox;
    @FXML
    public ComboBox customizeFontFamilyComboBox;
    @FXML
    public ComboBox customizeTextSizeComboBox;
    @FXML
    public ColorPicker customizeTextColorPicker;
    @FXML
    public ColorPicker customizeOutputColorPicker;
    @FXML
    public Button customizeResetToDefaultButton;
    @FXML
    public Button additionalPanelCommandButton;
    @FXML
    public TabPane borderTabPane;
    @FXML
    public MenuItem saveOutputMainMenuItem;
    @FXML
    public MenuItem reloadListsMainMenuItem;
    @FXML
    public MenuItem appInstallMainMenuItem;
    @FXML
    public MenuItem takeScreenshotMainMenuItem;
    @FXML
    public MenuItem aboutInfoMainMenuItem;
    @FXML
    public Label selectedDeviceLabel;
    @FXML
    public Label selectedAppLabel;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        sendPCStats();
        Tab firstTab = getSelectedTabInTabPane();
        Process firstProcess = null;
        thrp.tabAndProcessMap.put(firstTab, firstProcess);
        setGraphicsToNeededElements();
        thcop = new CustomizeOutputProvider(this);
        thsp = new SettingsProvider(this);
        VENDOR = new Vendor(menuBarMenuButton);
    }

    public void handleOnWindowShown(HostServices hostServices, Stage stage, Application.Parameters parameters){
        startAdbServer();
        mainHostServices = hostServices;
        mainStage = stage;
        thsp.loadSettings();
        thrp.setAllStyledTextAreaMaps(getCurrentTabTextArea());
        thbpc = new BorderPaneController(borderTabPane,additionalPanelCommandButton);
        tabsAddTubButton.fire();
        getSelectedTabInTabPane().setClosable(false);

        if (!parameters.getRaw().isEmpty() && parameters.getRaw().get(0).endsWith(".apk")){
            LoggingAdapter.debug("Parameters",parameters.getRaw().get(0));
            reloadListsButton.fire();
            new Thread(() -> {
                try {
                    TimeUnit.SECONDS.sleep(4);
                    installApkFromArgument(parameters.getRaw().get(0));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();

        } else {
            LoggingAdapter.debug("Parameters","Empty");
        }
    }

    @FXML
    private void handleLogcatStartSplitButton(ActionEvent event){
        AN_SEND.trackStatistic("Logcat","simple");
        runsExtendedToTA(thrp.reloadStrings("logcatMainString"),false,"");
    }

    @FXML
    private void handleLogcatClearMenuItem(ActionEvent event){
        AN_SEND.trackStatistic("Logcat","cleared");
        runSimpleCommand(thrp.reloadStrings("clearLogcatString"),
                "Clearing logs on device...");
    }

    @FXML
    private void handleLogcatStopMenuItem(ActionEvent event){
        AN_SEND.trackStatistic("Logcat","stoped");
        thrp.cancelLogcat(thrp.getAssociatedTabProcess(getSelectedTabInTabPane()));
    }
    
    @FXML
    private void handleLogcatFilteredTextField(ActionEvent event){
        AN_SEND.trackStatistic("Logcat","filtered",filterTextField.getText());
        runsExtendedToTA(thrp.reloadStrings("logcatMainString"),true,filterTextField.getText());
    }

    @FXML
    public void handleSearchTextField(ActionEvent event) {
        searchController.startSearch(getCurrentTabTextArea(),searchTextField);
    }

    @FXML
    private void handleDevicesListSplitButton(ActionEvent event){
        AN_SEND.trackStatistic("Devices","list");
        runsExtendedToTA(thrp.reloadStrings("devicesListString",true),false,"");
    }

    @FXML
    private void handleDevicesUnAuthMenuItem(ActionEvent event){
        AN_SEND.trackStatistic("Devices","unauth");
        runSimpleCommand(thrp.reloadStrings("devicesReconnectString"),
                "Reconnecting device...");
    }
    
    @FXML
    private void handleAppInstallSplitButton(ActionEvent event){
        if (thrp.reloadStrings("")!=null){
            ExtensionFilter filter = new ExtensionFilter("Android Packages","*.apk");
            dialog.getExtensionFilters().add(filter);
            dialog.setTitle("Select apk for installation");
            dialog.setInitialDirectory(new File(lastDirectory.getParent()));
            File result = dialog.showOpenDialog(null);
            if (result != null){
                lastDirectory = result;
                StringBuffer params = new StringBuffer();
                if(rApkInstallCheckbox.isSelected()){params.append("-r ");}
                if(dApkInstallCheckbox.isSelected()){params.append("-d ");}
                AN_SEND.trackStatistic("App","install",result.getName());
                String path;
                if (SystemController.getCurrentOs().equals(SystemController.System.WINDOWS)){
                    path = "\""+result.getAbsolutePath()+"\"";
                } else {
                    path = result.getAbsolutePath().replace(" "," ");
                }
                LoggingAdapter.debug("App install","Path to file: "+path);
                String input=(ADB_EXE.toString()+" -s "+thrp.selectedDevice+" install "+params.toString());
                ArrayList<String> array = new ArrayList<>(Arrays.asList(input.split(" ")));
                array.add(path);
                try {
                    thrp.runAppInstall(array.toArray(new String[array.size()]),progressBar, progressActivityLabel);
                    } catch (IOException | InterruptedException ex) {
                    Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
                }  
            }
        }
    }

    @FXML
    private void handleAppClearMenuItem(ActionEvent event){
        AN_SEND.trackStatistic("App","clear",thrp.packageName);
        runSimpleCommand(thrp.reloadStrings("clearDataOfAppString"),
                "Clearing app data...");
    }

    @FXML
    private void handleAppUninstallMenuItem(ActionEvent event){
        AN_SEND.trackStatistic("App","uninstall", thrp.packageName);
        runSimpleCommand(thrp.reloadStrings("uninstallAppString"),
                "Uninstalling app...");
    }

    @FXML
    private void handleAppStopMenuItem(ActionEvent event){
        AN_SEND.trackStatistic("App","force-stop", thrp.packageName);
        runSimpleCommand(thrp.reloadStrings("forceStopString"),
                "Force-stopping app...");
    }

    @FXML
    private void handleAppVersionMenuItem(ActionEvent event){
        AN_SEND.trackStatistic("App","version", thrp.packageName);
        runsExtendedToTA(thrp.reloadStrings("versionCodeString"),false,"");
        runSimpleCommand(thrp.reloadStrings("versionCodeString"),
                "Getting app version...");
    }

    @FXML
    private void handleScreenVideoRecordSplitButton(ActionEvent event){
        if (thrp.reloadStrings("")!=null){
            if(!isVideoRecording){
                isVideoRecording = true;
                thrp.videoName = getVideoFileName();
                LoggingAdapter.info("Exec","Starting video recording...");
                AN_SEND.trackStatistic("Screen","video");

                try {
                    screenVideoRecordSplitButton.setText("Stop record");
                    thlp.setGraphicToElement(screenVideoRecordSplitButton,FontAwesomeIcon.SQUARE);
                    screenVideoRecordSplitButton.getChildrenUnmodifiable().get(0).
                            setStyle("-fx-background-color: linear-gradient(rgba(255,0,0,0.3),rgba(255,0,0,0.6),rgba(255,0,0,0.8))");
                    screenVideoRecordSplitButton.
                            setStyle("-fx-background-color: linear-gradient(rgba(255,0,0,0.3),rgba(255,0,0,0.6),rgba(255,0,0,0.8))");
                    thrp.videoRecord(thrp.reloadStrings("recordVideoString"),progressBar);
                } catch (Exception ex) {
                    Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else if (isVideoRecording){
                isVideoRecording = false;
                try {
                    screenVideoRecordSplitButton.setText("Record screen");
                    thlp.setGraphicToElementWithColor(screenVideoRecordSplitButton,
                            FontAwesomeIcon.CIRCLE,
                            "linear-gradient(rgba(255,0,0,0.3),rgba(255,0,0,0.6),rgba(255,0,0,0.8))");
                    screenVideoRecordSplitButton.getChildrenUnmodifiable().get(0).
                            setStyle("");
                    screenVideoRecordSplitButton.
                            setStyle("");
                    thrp.video.destroy();
                    TimeUnit.SECONDS.sleep(2);
                    LoggingAdapter.info("Exec","Transferring video to your PC...");
                    thrp.videoRecord(thrp.reloadStrings("recordVideoCopyString"),progressBar,2);
                    Thread.sleep(500);
                } catch (Exception ex) {
                    Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    @FXML
    private void handleScreenTakeScreenshotMenuItem(ActionEvent event){
        if(thrp.reloadStrings("")!=null){
            try {
                AN_SEND.trackStatistic("Screen","screenshot");
                currentTimeStamp = System.currentTimeMillis();
                String query = ADB_EXE.toString()+" -s "+thrp.selectedDevice+" shell screencap "
                        + "/data/local/tmp/Screenshot_TH.png";
                runSimpleCommand(query.split(" "),"Making screenshot...");
                LoggingAdapter.info("Exec","Making screen shot.");
                TimeUnit.SECONDS.sleep(2);
                LoggingAdapter.info("Exec","Screenshot name is: Screenshot_"+currentTimeStamp);
                query = ADB_EXE.toString()+" -s "+thrp.selectedDevice+" pull -p /data/local/tmp/Screenshot_TH.png "
                        +HOME_DIR+"/Pictures/Android/Screenshot_"+currentTimeStamp+".png";
                runSimpleCommand(query.split(" "),"Copying screenshot...");
                LoggingAdapter.info("Exec","Screenshot copied.");
            } catch (InterruptedException ex) {
                Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    @FXML
    private void handleTabsAddTubButton(ActionEvent event){
        AN_SEND.trackStatistic("Tab","added");
        Tab addingTab = getNewTabForTabPane();
        addingTab.setOnClosed((Event event2) -> {
            AN_SEND.trackStatistic("Tab","removed");
            if(thrp.isTimerTaskByStyledTextArea(getSpecificTabTextArea(addingTab))){
                thrp.getTimerTaskByStyledTextArea(getSpecificTabTextArea(addingTab)).cancel();
            }
            thrp.tabAndProcessMap.remove(addingTab);
            thrp.removeOutputTimerTimerTaskMap(
                    thrp.getTimerByStyledTextArea(
                            getSpecificTabTextArea(addingTab)
                    )
            );
            thrp.removeStyledTextAreaOutputTimerMap(getSpecificTabTextArea(addingTab));
            thrp.removeStyledTextAreaStringBufferMap(getSpecificTabTextArea(addingTab));
        });
        outputTabbedPane.getTabs().add(addingTab);
        setSelectedTabInTabPane();
        appendNewProcessByTab(addingTab);
        LoggingAdapter.info("Tabs","Current last key availabled: "+
                thrp.tabAndProcessMap.toString());
    }
    
    @FXML
    private void handleReloadListsButton(ActionEvent event){
        AN_SEND.trackStatistic("ReloadLists","Used");
        deviceIdComboBox.getItems().clear();
        packageNameComboBox.getItems().clear();
        thrp.selectedDevice = null;
        thrp.packageName = null;
        selectedDeviceLabel.setText("");
        selectedAppLabel.setText("");
        thdc.getDevicesList(deviceIdComboBox);
    }
    @FXML
    private void handleAdditionalPanelCommandButton(ActionEvent event){
        thbpc.setCurrentWidthOfBorderPane();
    }
    
    @FXML
    private void handleDeviceIdComboBoxPropertyChanged(ActionEvent event){
        String deviceId =
                thdc.deviceNameSerialNoMap.get(deviceIdComboBox.getSelectionModel().getSelectedItem().toString());
        thrp.selectedDevice = deviceId;
        LoggingAdapter.debug("DeviceController:SelectedDevice",thrp.selectedDevice);
        thdc.getPackagesList(packageNameComboBox);
        selectedDeviceLabel.setText(deviceIdComboBox.getSelectionModel().getSelectedItem().toString()
                +"("+deviceId+")");
    }
    
    @FXML
    private void handlePackageNameComboBoxPropertyChanged(ActionEvent event){
        thrp.packageName = packageNameComboBox.getSelectionModel().getSelectedItem().toString();
        LoggingAdapter.debug("DeviceController:SelectedPackage",thrp.packageName);
        String appVer = thdc.appPackageVersionMap.get(packageNameComboBox.getSelectionModel().getSelectedItem().toString());
        selectedAppLabel.setText("app: "+packageNameComboBox.getSelectionModel().getSelectedItem().toString()+
            " | ver: "+appVer);
    }

    @FXML
    public void handleSaveOutputMainMenuItem(ActionEvent event) {
        AN_SEND.trackStatistic("Menu","SaveToFile");
        thrp.saveToFile(getCurrentTabTextArea().getText());
    }

    @FXML
    public void handleReloadListsMainMenuItem(ActionEvent event) {
        handleReloadListsButton(event);
    }

    @FXML
    public void handleAppInstallMainMenuItem(ActionEvent event) {
        handleAppInstallSplitButton(event);
    }

    @FXML
    public void handleTakeScreenshotMainMenuItem(ActionEvent event) {
        handleScreenTakeScreenshotMenuItem(event);
    }

    @FXML
    public void handleAboutInfoMainMenuItem(ActionEvent event) {
        AN_SEND.trackStatistic("Menu","About");
        TextFlow textFlow = thlp.getCustomAboutTextFlow(mainHostServices);
        Alert aboutDialog = new Alert(Alert.AlertType.INFORMATION);
        aboutDialog.setTitle("About");
        aboutDialog.getDialogPane().setContent(textFlow);

        aboutDialog.show();
    }

    public JTextPane getCurrentTabTextArea() {
        ObservableList tabsFromTabbedPane = outputTabbedPane.getTabs();
        if(tabsFromTabbedPane.isEmpty()){return null;}
        Tab selectedTab = (Tab) tabsFromTabbedPane.
                get(outputTabbedPane.getSelectionModel().getSelectedIndex());
        ObservableList childsOfOnePane = ((Parent) selectedTab.getContent())
                .getChildrenUnmodifiable();
        ScrollPane selectedScrollPane = (ScrollPane) childsOfOnePane.get(0);
        SwingNode selectedSwingNode = (SwingNode)
                selectedScrollPane.getContent();
        JScrollPane scrollPane1 = (JScrollPane) selectedSwingNode.getContent();
        JTextPane textArea = (JTextPane) scrollPane1.getViewport().getView();

        return textArea;
    }

    public JTextPane getSpecificTabTextArea(Tab specificTab) {
        Tab selectedTab = specificTab;
        ObservableList childsOfOnePane = ((Parent) selectedTab.getContent())
                .getChildrenUnmodifiable();
        ScrollPane selectedScrollPane = (ScrollPane) childsOfOnePane.get(0);
        SwingNode selectedSwingNode = (SwingNode)
                selectedScrollPane.getContent();
        JScrollPane scrollPane1 = (JScrollPane) selectedSwingNode.getContent();
        JTextPane textArea = (JTextPane) scrollPane1.getViewport().getView();
        return textArea;
    }

    public JTextPane[] getAllTabsTextAreas(){
        ObservableList tabsFromTabbedPane = outputTabbedPane.getTabs();
        int tabsCount = outputTabbedPane.getTabs().size()-1;
        int tabsSize = outputTabbedPane.getTabs().size();
        JTextPane[] allTabsTextAreas = new JTextPane[tabsSize];
        for(int i = 0; i<= tabsCount; i++){
            Tab tab = (Tab) tabsFromTabbedPane.
                    get(i);
            ObservableList childsOfOnePane = ((Parent) tab.getContent())
                    .getChildrenUnmodifiable();
            ScrollPane scrollPane = (ScrollPane) childsOfOnePane.get(0);
            SwingNode selectedSwingNode = (SwingNode)
                    scrollPane.getContent();
            JScrollPane scrollPane1 = (JScrollPane) selectedSwingNode.getContent();
            JTextPane textArea = (JTextPane) scrollPane1.getViewport().getView();
            allTabsTextAreas[i] = textArea;
        }
        StringBuilder stringBuilder = new StringBuilder();
        for(int i = 0; i <= allTabsTextAreas.length-1; i++){
            stringBuilder.append(allTabsTextAreas[i].toString());
        }
        LoggingAdapter.debug("Tabs","Count: "+allTabsTextAreas.length);
        return allTabsTextAreas;
    }
    
    private Tab getNewTabForTabPane(){
        JTextPane newTextPane = new JTextPane();
        thcop.customizeNewTextArea(newTextPane);
        DefaultCaret caret = (DefaultCaret)newTextPane.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        JScrollPane scrollPane = new JScrollPane(newTextPane);
        scrollPane.setAutoscrolls(true);
        SwingNode swingNode = new SwingNode();
        swingNode.setContent(scrollPane);
        int currentTabsNumber = outputTabbedPane.getTabs().size() + 1;
        thrp.setAllStyledTextAreaMaps(newTextPane);
        ScrollPane newScrollPane = new ScrollPane(swingNode);
        newScrollPane.setFitToHeight(true);
        newScrollPane.setFitToWidth(true);
        AnchorPane newAnchorPane = new AnchorPane(newScrollPane);
        AnchorPane.setTopAnchor(newScrollPane, 0d);
        AnchorPane.setBottomAnchor(newScrollPane, 0d);
        AnchorPane.setLeftAnchor(newScrollPane, 0d);
        AnchorPane.setRightAnchor(newScrollPane, 0d);
        Tab newTab = new Tab("Log Tab "+currentTabsNumber++, newAnchorPane);
        return newTab;
    }
    
    private void setSelectedTabInTabPane(){
        outputTabbedPane.getSelectionModel().selectLast();
    }
    
    private int getSelectedTabIndexInTabPane(){
        return outputTabbedPane.getSelectionModel().getSelectedIndex();
    }
    private Tab getSelectedTabInTabPane(){
        return outputTabbedPane.getSelectionModel().getSelectedItem();
    }
    private Process getAssociatedTabProcess(){
        return thrp.tabAndProcessMap.get(getSelectedTabInTabPane());
    }

    private void appendNewProcessByTab(Tab addingTab) {
        Process newProcess = null;
        thrp.tabAndProcessMap.put(addingTab, newProcess);
    }
    
    private void setGraphicsToNeededElements(){
        thlp.setGraphicAndClearTextToElement(tabsAddTubButton, FontAwesomeIcon.PLUS);
        thlp.setGraphicAndClearTextToElement(reloadListsButton, FontAwesomeIcon.REFRESH);
        thlp.setGraphicToElement(logcatStartMenuItem, FontAwesomeIcon.FILE_TEXT);
        thlp.setGraphicToElement(devicesListSplitButton, FontAwesomeIcon.MOBILE_PHONE);
        thlp.setGraphicToElement(menuBarMenuButton, FontAwesomeIcon.BARS);
        thlp.setGraphicToElement(appInstallSplitButton, FontAwesomeIcon.TASKS);
        thlp.setGraphicToElementWithColor(screenVideoRecordSplitButton, 
                FontAwesomeIcon.CIRCLE,
                "linear-gradient(rgba(255,0,0,0.3),rgba(255,0,0,0.6),rgba(255,0,0,0.8))");
        thlp.setGraphicToElement(screenTakeScreenshotMenuItem, FontAwesomeIcon.CAMERA);
        thlp.setGraphicAndClearTextToElement(additionalPanelCommandButton,FontAwesomeIcon.ALIGN_LEFT);

        additionalPanelCommandButton.setPadding(new Insets(2,5,2,5l));
    }
    
    public void runsExtendedToTA (String[] str, boolean FiltEn, String filter){
        thrp.runLogcat(str,FiltEn,filter,getCurrentTabTextArea(), outputClearCheckBox,getSelectedTabInTabPane());
    }

    public void runSimpleCommand(String[] str, String name){
        thrp.runSimpleCommand(str,progressActivityLabel,progressSpinner,name);
    }

    public String getVideoFileName(){
        return "UnNamedVideoFile_"+System.currentTimeMillis();
    }

    public void startAdbServer(){
        LoggingAdapter.debug("System", "Starting ADB server");
        String query = ADB_EXE.toString()+" start-server";
        thrp.runSilentCommand(query.split(" "));
    }

    public void killAdbServer(){
        String query = ADB_EXE.toString()+" kill-server";
        thrp.runSilentCommand(query.split(" "));
    }

    @FXML
    public void handleDeviceLableDoubleClick(MouseEvent mouseEvent) {
        if(mouseEvent.getButton().equals(MouseButton.PRIMARY)){
            if(mouseEvent.getClickCount() == 2){
                AN_SEND.trackStatistic("Other","Double click","Device Label");
                ClipboardContent content = new ClipboardContent();
                content.putString(thrp.selectedDevice);
                clipboard.setContent(content);
                TRAY_ICON.displayPopup("Device Serial No copied",
                        "Serial: "+thrp.selectedDevice,
                        TrayIcon.MessageType.INFO);
            }
        }
    }

    @FXML
    public void handleAppLableDoubleClick(MouseEvent mouseEvent) {
        if(mouseEvent.getButton().equals(MouseButton.PRIMARY)){
            if(mouseEvent.getClickCount() == 2){
                AN_SEND.trackStatistic("Other","Double click","App Label");
                ClipboardContent content = new ClipboardContent();
                content.putString(thrp.packageName);
                clipboard.setContent(content);
                TRAY_ICON.displayPopup("App Package Name copied",
                        "Package: "+thrp.packageName,
                        TrayIcon.MessageType.INFO);
            }
        }
    }



    private void sendPCStats() {
        AN_SEND.trackStatistic("Launch","OS",System.getProperty("os.name"));
        AN_SEND.trackStatistic("Launch","OS arch",System.getProperty("os.arch"));
        AN_SEND.trackStatistic("Launch","OS ver",System.getProperty("os.version"));
        AN_SEND.trackStatistic("Launch","Java ver",System.getProperty("java.version"));
        GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        int width = gd.getDisplayMode().getWidth();
        int height = gd.getDisplayMode().getHeight();
        AN_SEND.trackStatistic("Launch","Screen",width+"x"+height);
        AN_SEND.trackStatistic("Launch","User-Name",System.getProperty("user.name"));
    }

    public void installApkFromArgument(String message) {
        Path result = FileSystems.getDefault().getPath(message);
        StringBuffer params = new StringBuffer();
        if(rApkInstallCheckbox.isSelected()){params.append("-r ");}
        if(dApkInstallCheckbox.isSelected()){params.append("-d ");}
        AN_SEND.trackStatistic("App","install",result.getFileName().toString());
        String path;
        if (SystemController.getCurrentOs().equals(SystemController.System.WINDOWS)){
            path = "\""+result.toString()+"\"";
        } else {
            path = result.toString().replace(" "," ");
        }
        LoggingAdapter.debug("App install","Path to file: "+path);
        String input=(ADB_EXE.toString()+" -s "+thrp.selectedDevice+" install "+params.toString());
        ArrayList<String> array = new ArrayList<>(Arrays.asList(input.split(" ")));
        array.add(path);
        try {
            thrp.runAppInstall(array.toArray(new String[array.size()]),progressBar, progressActivityLabel);
        } catch (IOException | InterruptedException ex) {
            Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
