package ru.zeburek.zerohelper.providers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import ru.zeburek.zerohelper.controllers.FXMLDocumentController;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

/**
 * Created by zeburek on 13.04.2017.
 */
public class CustomizeOutputProvider {
    private final FXMLDocumentController fxml;
    private final int defaultFontSize = 14;
    private final String defaultFontName = "System";
    private final java.awt.Color defaultForegroundStyle = java.awt.Color.WHITE;
    private final java.awt.Color defaultBackgroundStyle = java.awt.Color.decode("#000033");
    private int currentFontSize = 14;
    private String currentFontName = "System";
    private java.awt.Color currentForegroundStyle = java.awt.Color.WHITE;
    private java.awt.Color currentBackgroundStyle = java.awt.Color.decode("#000033");

    public CustomizeOutputProvider(FXMLDocumentController fxmlDocumentController) {
        this.fxml = fxmlDocumentController;
        fxml.customizeOutputColorPicker.setValue(Color.BLACK);
        fxml.customizeTextColorPicker.setValue(Color.WHITE);
        generateListsForFont();
        addListenersForElements();
    }

    public void customizeNewTextArea(JTextPane textArea){
        textArea.setFont(new Font(currentFontName,Font.PLAIN,currentFontSize));
        textArea.setBackground(currentBackgroundStyle);
        textArea.setForeground(currentForegroundStyle);
    }

    private void generateListsForFont() {
        generateListsForFontFamilies();
        generateListsForFontSize();
    }

    private void generateListsForFontFamilies() {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        String[] names = ge.getAvailableFontFamilyNames();
        java.util.List<String> namesList = Arrays.asList(names);
        ObservableList observableList = FXCollections.observableList(namesList);
        SortedList sortedList = new SortedList(observableList);
        fxml.customizeFontFamilyComboBox.setItems(sortedList);
    }

    private void generateListsForFontSize() {
        java.util.List<String> namesList = Arrays.asList("8","9","10","12","14","16","20","24","28","36","42","48","64");
        ObservableList observableList = FXCollections.observableList(namesList);
        fxml.customizeTextSizeComboBox.setItems(observableList);
    }

    private void addListenersForElements(){
        fxml.customizeFontFamilyComboBox.setOnAction(customizeFontFamilyComboBoxActionEventEventHandler());
        fxml.customizeTextSizeComboBox.setOnAction(customizeTextSizeComboBoxActionEventEventHandler());
        fxml.customizeTextColorPicker.setOnAction(customizeTextColorPickerActionEventEventHandler());
        fxml.customizeOutputColorPicker.setOnAction(customizeOutputColorPickerActionEventEventHandler());
        fxml.customizeResetToDefaultButton.setOnAction(customizeResetToDefaultButtonEventEventHandler());
    }

    private EventHandler<ActionEvent> customizeFontFamilyComboBoxActionEventEventHandler(){
        return event -> {
            setOutputFontFamily(fxml.customizeFontFamilyComboBox.getSelectionModel().getSelectedItem().toString());
        };
    }

    private EventHandler<ActionEvent> customizeTextSizeComboBoxActionEventEventHandler(){
        return event -> {
            setOutputFontSize(fxml.customizeTextSizeComboBox.getSelectionModel().getSelectedItem().toString());
        };
    }

    private EventHandler<ActionEvent> customizeTextColorPickerActionEventEventHandler(){
        return event -> {
            setOutputTextColor(toAwtColor(fxml.customizeTextColorPicker.getValue()));
        };
    }

    private EventHandler<ActionEvent> customizeOutputColorPickerActionEventEventHandler(){
        return event -> {
            setOutputBackgroundColor(toAwtColor(fxml.customizeOutputColorPicker.getValue()));
        };
    }

    private EventHandler<ActionEvent> customizeResetToDefaultButtonEventEventHandler(){
        return event -> {
            resetAllStylesToDefault();
        };
    }

    public void setOutputFontFamily(String fontFamily){
        int currentSize = fxml.getCurrentTabTextArea().getFont().getSize();
        Font font = new Font(fontFamily,Font.PLAIN,currentSize);
        currentFontName = fontFamily;
        JTextPane[] currentAreas = fxml.getAllTabsTextAreas();
        for (JTextPane textArea:currentAreas) {
            textArea.setFont(font);
        }
    }

    public void setOutputFontSize(String fontSize){
        String currentFamily = fxml.getCurrentTabTextArea().getFont().getFamily();
        int fontSized = Integer.parseInt(fontSize);
        Font font = new Font(currentFamily,Font.PLAIN,fontSized);
        currentFontSize = fontSized;
        JTextPane[] currentAreas = fxml.getAllTabsTextAreas();
        for (JTextPane textArea:currentAreas) {
            textArea.setFont(font);
        }
    }

    public void setOutputFont(Font font){
        currentFontName = font.getFamily();
        currentFontSize = font.getSize();
        JTextPane[] currentAreas = fxml.getAllTabsTextAreas();
        for (JTextPane textArea:currentAreas) {
            textArea.setFont(font);
        }
    }

    public void setOutputBackgroundColor(java.awt.Color color){
        currentBackgroundStyle = color;
        JTextPane[] currentAreas = fxml.getAllTabsTextAreas();
        for (JTextPane textArea:currentAreas) {
            textArea.setBackground(color);
        }
    }

    public void setOutputTextColor(java.awt.Color color){
        currentForegroundStyle = color;
        JTextPane[] currentAreas = fxml.getAllTabsTextAreas();
        for (JTextPane textArea:currentAreas) {
            textArea.setForeground(color);
        }
    }

    private static java.awt.Color toAwtColor(Color fx)
    {
        return new java.awt.Color((float) fx.getRed(),
                (float) fx.getGreen(),
                (float) fx.getBlue(),
                (float) fx.getOpacity());
    }

    private void resetAllStylesToDefault() {
        JTextPane[] currentAreas = fxml.getAllTabsTextAreas();
        for (JTextPane textArea:currentAreas) {
            textArea.setForeground(defaultForegroundStyle);
            textArea.setBackground(defaultBackgroundStyle);
            textArea.setFont(new Font(defaultFontName,Font.PLAIN,defaultFontSize));
        }
        currentFontName = defaultFontName;
        currentFontSize = defaultFontSize;
        currentBackgroundStyle = defaultBackgroundStyle;
        currentForegroundStyle = defaultForegroundStyle;
        fxml.customizeFontFamilyComboBox.getSelectionModel().clearSelection();
        fxml.customizeTextSizeComboBox.getSelectionModel().clearSelection();
        fxml.customizeOutputColorPicker.setValue(Color.BLACK);
        fxml.customizeTextColorPicker.setValue(Color.WHITE);

    }
}
