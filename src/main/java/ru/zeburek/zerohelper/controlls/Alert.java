package ru.zeburek.zerohelper.controlls;

import javafx.scene.control.ButtonType;

/**
 * Created by zeburek on 13.05.2017.
 */
public class Alert extends javafx.scene.control.Alert {
    public Alert(AlertType alertType, String contentText, ButtonType... buttons) {
        super(alertType, contentText, buttons);
    }

    public Alert(AlertType alertType, String titleText, String headerText, String contentText, ButtonType... buttons){
        super(alertType, contentText, buttons);
        setTitle(titleText);
        setHeaderText(headerText);
    }
}
