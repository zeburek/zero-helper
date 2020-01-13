package ru.zeburek.zerohelper.providers;

import javafx.application.Platform;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Window;
import ru.zeburek.zerohelper.controllers.FXMLDocumentController;
import ru.zeburek.zerohelper.utils.LoggingAdapter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Optional;
import java.util.Properties;

import static ru.zeburek.zerohelper.ZeroHelper.HOME_DIR;
import static ru.zeburek.zerohelper.ZeroHelper.SETTINGS_FILE;

/**
 * Created by zeburek on 16.04.2017.
 */
public class SettingsProvider {
    private final FXMLDocumentController fxml;
    private Parent root;
    private Scene rootScene;
    private Window rootWindow;
    private final Properties props = new Properties();
    private CheckBox rememberChk = new CheckBox("Don't ask again");

    public boolean isFirstStart = true;
    public String saveSettingsStr = null;

    public SettingsProvider(FXMLDocumentController fxmlDocumentController){
        this.fxml = fxmlDocumentController;
    }

    public void loadSettings() {
        LoggingAdapter.debug("System", "Starting to load settings");
        this.rootWindow = fxml.filterTextField.getScene().getWindow();
        try {
            FileInputStream inputSettings = new FileInputStream(SETTINGS_FILE);
            Throwable localThrowable3 = null;
            try {
                this.props.load(inputSettings);
            } catch (Throwable localThrowable1) {
                localThrowable3 = localThrowable1;
                throw localThrowable1;
            } finally {
                if (inputSettings != null) {
                    if (localThrowable3 != null) {
                        try {
                            inputSettings.close();
                        } catch (Throwable localThrowable2) {
                            localThrowable3.addSuppressed(localThrowable2);
                        }
                    } else {
                        inputSettings.close();
                    }
                }
            }
        } catch (Exception localException) {
        }
        int isFirst;
        double savedX;
        double savedY;
        double savedHeight;
        double savedWidth;
        boolean savedIsWindowsMaximised;
        String savedFilter;
        String savedHighlighter;
        String savedForeground;
        String savedBackground;
        String savedFontFamily;
        int savedFontSize;
        try {
            isFirst = Integer.parseInt(this.props.getProperty("isFirstStart", "1"));
            savedX = Double.parseDouble(this.props.getProperty("xPos", "" + rootWindow.getX()));
            savedY = Double.parseDouble(this.props.getProperty("yPos", "" + rootWindow.getY()));
            savedHeight = Double.parseDouble(this.props.getProperty("fHeight", "600"));
            savedWidth = Double.parseDouble(this.props.getProperty("fWidth", "1170"));
            savedIsWindowsMaximised = Boolean.getBoolean(this.props.getProperty("isWindowsMaximised","false"));
            savedFilter = this.props.getProperty("filter", "");
            savedHighlighter = this.props.getProperty("highlighter", "");
            savedForeground = this.props.getProperty("foreground", "#ffffff");
            savedBackground = this.props.getProperty("background", "#000033");
            savedFontFamily = this.props.getProperty("fontFamily", "Monospaced");
            savedFontSize = Integer.parseInt(this.props.getProperty("fontSize", "12"));
            if (this.props.containsKey("saveSettings")) {
                this.saveSettingsStr = this.props.getProperty("saveSettings");
                LoggingAdapter.debug("SettingsProvider:SaveSettings", this.saveSettingsStr);
            }
        } catch (NumberFormatException e) {
            isFirst = 0;
            savedX = rootWindow.getX();
            savedY = rootWindow.getY();
            savedHeight = 600d;
            savedWidth = 1170d;
            savedIsWindowsMaximised = false;
            savedFilter = "";
            savedHighlighter = "";
            savedForeground = "#ffffff";
            savedBackground = "#000033";
            savedFontFamily = "Monospaced";
            savedFontSize = 12;
        }
        if (isFirst != 1) {
            this.isFirstStart = false;
        }
        double finalSavedX = savedX;
        double finalSavedY = savedY;
        double finalSavedHeight = savedHeight;
        double finalSavedWidth = savedWidth;
        boolean finalSavedIsWindowsMaximised = savedIsWindowsMaximised;
        String finalSavedFilter = savedFilter;
        String finalSavedHighlighter = savedHighlighter;
        String finalSavedForeground = savedForeground;
        String finalSavedBackground = savedBackground;
        String finalSavedFontFamily = savedFontFamily;
        int finalSavedFontSize = savedFontSize;
        Platform.runLater(()->{
            rootWindow.setX(finalSavedX);
            rootWindow.setY(finalSavedY);
            rootWindow.setHeight(finalSavedHeight);
            rootWindow.setWidth(finalSavedWidth);
            fxml.mainStage.setMaximized(finalSavedIsWindowsMaximised);
            fxml.filterTextField.setText(finalSavedFilter);
            fxml.searchTextField.setText(finalSavedHighlighter);
            fxml.thcop.setOutputTextColor(java.awt.Color.decode(finalSavedForeground));
            fxml.thcop.setOutputBackgroundColor(java.awt.Color.decode(finalSavedBackground));
            fxml.thcop.setOutputFont(new java.awt.Font(finalSavedFontFamily, java.awt.Font.PLAIN,finalSavedFontSize));

        });
    }

    public void saveSettings()
    {
        if (this.saveSettingsStr == null)
        {
            int closing = showApproveClosePopup();
            System.out.println("Result of popup: "+closing);
            this.props.setProperty("isFirstStart", "0");
            if ((closing == 2) || (closing == -1)) {
                return;
            }
            if ((closing == 1) && (this.rememberChk.isSelected()))
            {
                removeSavedSettings();
                this.props.setProperty("saveSettings", "false");
                storeSettingsToFile();
            }
            if ((closing == 1) && (!this.rememberChk.isSelected()))
            {
                removeSavedSettings();
                storeSettingsToFile();
            }
            if ((closing == 0) && (this.rememberChk.isSelected()))
            {
                this.props.setProperty("saveSettings", "true");
                setPropertiesForSettings();
                storeSettingsToFile();
            }
            if ((closing == 0) && (!this.rememberChk.isSelected()))
            {
                setPropertiesForSettings();
                storeSettingsToFile();
            }
        }
        else if (!this.saveSettingsStr.equals("false"))
        {
            if (this.saveSettingsStr.equals("true"))
            {
                setPropertiesForSettings();
                storeSettingsToFile();
            }
        }
    }

    public void storeSettingsToFile()
    {
        try
        {
            FileOutputStream outputSettings = new FileOutputStream(SETTINGS_FILE);Throwable localThrowable3 = null;
            try
            {
                this.props.store(outputSettings, "Testing Helper Preferences \nIf something goes wrong - delete this file.");
            }
            catch (Throwable localThrowable1)
            {
                localThrowable3 = localThrowable1;throw localThrowable1;
            }
            finally
            {
                if (outputSettings != null) {
                    if (localThrowable3 != null) {
                        try
                        {
                            outputSettings.close();
                        }
                        catch (Throwable localThrowable2)
                        {
                            localThrowable3.addSuppressed(localThrowable2);
                        }
                    } else {
                        outputSettings.close();
                    }
                }
            }
        }
        catch (Exception ignore)
        {
            System.out.println(ignore);
        }
    }

    public void setPropertiesForSettings()
    {
        this.props.setProperty("xPos", "" + rootWindow.getX());
        this.props.setProperty("yPos", "" + rootWindow.getY());
        this.props.setProperty("fHeight", "" + rootWindow.getHeight());
        this.props.setProperty("fWidth", "" + rootWindow.getWidth());
        this.props.setProperty("isWindowsMaximised", ""+fxml.mainStage.isMaximized());
        this.props.setProperty("filter", fxml.filterTextField.getText());
        this.props.setProperty("highlighter", fxml.searchTextField.getText());
        this.props.setProperty("foreground", "#" + Integer.toHexString(fxml.getCurrentTabTextArea().getForeground().getRGB()).substring(2));
        this.props.setProperty("background", "#" + Integer.toHexString(fxml.getCurrentTabTextArea().getBackground().getRGB()).substring(2));
        this.props.setProperty("fontFamily", fxml.getCurrentTabTextArea().getFont().getFamily());
        this.props.setProperty("fontSize", "" + fxml.getCurrentTabTextArea().getFont().getSize());
    }

    public void makeMainAppDirs()
    {
        if (this.isFirstStart) {
            new File(HOME_DIR + "/AppData/Local/THelper").mkdirs();
        }
    }

    public void getDisabledItems()
    {

    }

    private int showApproveClosePopup()
    {
        String msg = "Save settings on close?";
        DialogPane dialogPane = new DialogPane();
        dialogPane.setContent(rememberChk);
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setContentText(msg);
        alert.setTitle("Save settings");
        alert.getDialogPane().contentProperty().set(dialogPane);
        ButtonType okButton = new ButtonType("Yes", ButtonBar.ButtonData.YES);
        ButtonType noButton = new ButtonType("No", ButtonBar.ButtonData.NO);
        alert.getButtonTypes().setAll(okButton, noButton);
        Optional<ButtonType> result = alert.showAndWait();
        System.out.println(result);
        if (result.get() == okButton) {
            return 0;
        } else if (result.get() == noButton) {
            return 1;
        } else {
            return 2;
        }
    }

    private void removeSavedSettings()
    {
        this.props.remove("xPos");
        this.props.remove("yPos");
        this.props.remove("fHeight");
        this.props.remove("fWidth");
        this.props.remove("state");
        this.props.remove("videoName");
        this.props.remove("filter");
        this.props.remove("highlighter");
        this.props.remove("foreground");
        this.props.remove("background");
        this.props.remove("fontFamily");
        this.props.remove("fontSize");
    }
}
