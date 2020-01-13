/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.zeburek.zerohelper.providers;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.application.HostServices;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.scene.paint.Paint;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import static ru.zeburek.zerohelper.ZeroHelper.VER_ID;

/**
 *
 * @author zeburek
 */
public class LayoutProvider {
    public void setGraphicToElement(Button obj, FontAwesomeIcon icon){
        obj.setGraphic(new FontAwesomeIconView(icon));
    }
    
    public void setGraphicToElement(SplitMenuButton obj, FontAwesomeIcon icon){
        obj.setGraphic(new FontAwesomeIconView(icon));
    }
    
    public void setGraphicToElement(MenuButton obj, FontAwesomeIcon icon){
        obj.setGraphic(new FontAwesomeIconView(icon));
    }
    
    public void setGraphicToElement(MenuItem obj, FontAwesomeIcon icon){
        obj.setGraphic(new FontAwesomeIconView(icon));
    }

    public void setGraphicToElement(Hyperlink obj, FontAwesomeIcon icon){
        obj.setGraphic(new FontAwesomeIconView(icon));
    }
    
    public void setGraphicToElement(Label obj, FontAwesomeIcon icon){
        obj.setGraphic(new FontAwesomeIconView(icon));
    }
    
    public void setGraphicAndClearTextToElement(Button obj, FontAwesomeIcon icon){
        obj.setText("");
        obj.setGraphic(new FontAwesomeIconView(icon));
    }
    
    public void setGraphicAndClearTextToElement(SplitMenuButton obj, FontAwesomeIcon icon){
        obj.setText("");
        obj.setGraphic(new FontAwesomeIconView(icon));
    }
    
    public void setGraphicAndClearTextToElement(MenuButton obj, FontAwesomeIcon icon){
        obj.setText("");
        obj.setGraphic(new FontAwesomeIconView(icon));
    }
    
    public void setGraphicAndClearTextToElement(MenuItem obj, FontAwesomeIcon icon){
        obj.setText("");
        obj.setGraphic(new FontAwesomeIconView(icon));
    }
    
    public void setGraphicAndClearTextToElement(Label obj, FontAwesomeIcon icon){
        obj.setText("");
        obj.setGraphic(new FontAwesomeIconView(icon));
    }
    
    public void setGraphicToElementWithColor(Button obj, FontAwesomeIcon icon, String color){
        FontAwesomeIconView ic = new FontAwesomeIconView(icon);
        ic.setFill(Paint.valueOf(color));
        obj.setGraphic(ic);
    }
    
    public void setGraphicToElementWithColor(SplitMenuButton obj, FontAwesomeIcon icon, String color){
        FontAwesomeIconView ic = new FontAwesomeIconView(icon);
        ic.setFill(Paint.valueOf(color));
        obj.setGraphic(ic);
    }
    
    public void setGraphicToElementWithColor(MenuButton obj, FontAwesomeIcon icon, String color){
        FontAwesomeIconView ic = new FontAwesomeIconView(icon);
        ic.setFill(Paint.valueOf(color));
        obj.setGraphic(ic);
    }
    
    public void setGraphicToElementWithColor(MenuItem obj, FontAwesomeIcon icon, String color){
        FontAwesomeIconView ic = new FontAwesomeIconView(icon);
        ic.setFill(Paint.valueOf(color));
        obj.setGraphic(ic);
    }
    
    public void setGraphicToElementWithColor(Label obj, FontAwesomeIcon icon, String color){
        FontAwesomeIconView ic = new FontAwesomeIconView(icon);
        ic.setFill(Paint.valueOf(color));
        obj.setGraphic(ic);
    }

    public TextFlow getCustomAboutTextFlow(HostServices hostServices){
        TextFlow textFlow = new TextFlow();

        /**
         * "<html><p>Название: <b>Testing Helper</b><br>"
         + "Автор: Parviz Khavari<br>"
         + "Сайт: <a href='https://parviz.pw' target='_blank'>https://parviz.pw</a><br>"
         + "Версия: <b>"+VER_ID+"</b>"
         + "</p></html>"
         *
         */
        Text first = new Text("Product name: ");
        Text second = new Text("Testing Helper\n");
        second.setStyle("-fx-font-weight: bold");
        Text third = new Text("Author: Parviz Khavari\n");
        Text fouth = new Text("Site: ");
        Hyperlink fith = new Hyperlink("https://parviz.pw");
        fith.setOnAction(t -> hostServices.showDocument(fith.getText()));
        Text six = new Text("\nVersion: ");
        Text seven = new Text(VER_ID);
        seven.setStyle("-fx-font-weight: bold");
        Text eight = new Text("\nApp dir: ");
        Text nine = new Text(System.getProperty("user.dir"));
        textFlow.getChildren().addAll(first,second,third,fouth,fith,six,seven,eight,nine);
        return textFlow;
    }
}
