package ru.zeburek.zerohelper.controllers;

import javafx.scene.control.Button;
import javafx.scene.control.TabPane;

/**
 * Created by zeburek on 08.05.2017.
 */
public class BorderPaneController {
    private static TabPane borderTabPane;
    private static Button borderTabPaneControllButton;
    private boolean isBorderPaneVisible;

    public BorderPaneController(TabPane tb, Button btn){
        borderTabPane = tb;
        borderTabPaneControllButton = btn;
        borderTabPane.setPrefWidth(32d);
        isBorderPaneVisible = false;
    }

    public void setCurrentWidthOfBorderPane(){
        if(!isBorderPaneVisible){
            borderTabPane.setPrefWidth(200d);
            isBorderPaneVisible = true;
        }else if(isBorderPaneVisible){
            borderTabPane.setPrefWidth(32d);
            isBorderPaneVisible = false;
        }
    }
}
