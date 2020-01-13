package ru.zeburek.zerohelper.tray;

import javafx.application.Platform;
import javafx.stage.Stage;
import ru.zeburek.zerohelper.ZeroHelper;
import ru.zeburek.zerohelper.controllers.FXMLDocumentController;
import ru.zeburek.zerohelper.utils.SystemController;

import java.awt.*;

import static ru.zeburek.zerohelper.ZeroHelper.AN_SEND;

/**
 * Created by zeburek on 07.06.2017.
 */
public class TrayIconProvider {
    private final Stage stage;
    private final FXMLDocumentController controller;
    private SystemTray tray;
    private TrayIcon trayIcon;
    private boolean IS_SUPPORTED;
    public TrayIconProvider(Stage stage, FXMLDocumentController controller) throws AWTException {
        this.stage = stage;
        this.controller = controller;
        if (SystemController.getCurrentOs().equals(SystemController.System.WINDOWS)) {
            IS_SUPPORTED = true;
            displayTray();
            AN_SEND.trackStatistic("TrayIcon","Success","System tray icon shown");
        } else {
            IS_SUPPORTED = false;
            AN_SEND.trackStatistic("TrayIcon","Failure","System tray icon unsupported");
        }

        displayPopup("Testing Helper","Application started",TrayIcon.MessageType.NONE);
    }

    private void displayTray() throws AWTException {
        //Obtain only one instance of the SystemTray object
        tray = SystemTray.getSystemTray();
        //If the icon is a file
        Image image = Toolkit.getDefaultToolkit().createImage(ZeroHelper.class.getResource("Logo_ZH.png"));
        trayIcon = new TrayIcon(image, "Testing Helper");
        //Let the system resizes the image if needed
        trayIcon.setImageAutoSize(true);
        //Set tooltip text for the tray icon
        trayIcon.setToolTip("Testing Helper");
        tray.add(trayIcon);
        setupTrayMenu();
    }

    private void setupTrayMenu(){
        PopupMenu popupMenu = new PopupMenu();
        MenuItem open = new MenuItem("Open");
        MenuItem install = new MenuItem("Install app");
        MenuItem exit = new MenuItem("Exit");
        open.addActionListener(e -> {
            Platform.runLater(() -> {
                stage.show();
                stage.toFront();
            });
        });
        install.addActionListener(e -> {
            Platform.runLater(()->{
                stage.toFront();
                controller.appInstallMainMenuItem.fire();
            });
        });
        exit.addActionListener(e -> {
            Platform.runLater(stage::close);
        });
        popupMenu.add(open);
        popupMenu.add(install);
        popupMenu.add(exit);
        trayIcon.setPopupMenu(popupMenu);
    }

    public void displayPopup(String header, String text, TrayIcon.MessageType type){
        if (!IS_SUPPORTED){
            return;
        }
        trayIcon.displayMessage(header, text, type);
    }
}
