package ru.zeburek.zerohelper.vendors.vendor;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import ru.zeburek.zerohelper.utils.LoggingAdapter;
import ru.zeburek.zerohelper.vendors.DefaultVendor;

import static ru.zeburek.zerohelper.ZeroHelper.AN_SEND;
import static ru.zeburek.zerohelper.controllers.FXMLDocumentController.thrp;

/**
 * Created by zeburek on 09.06.2017.
 */
public class Yandex implements DefaultVendor {
    public static volatile boolean vendorOn = false;
    public static volatile String vendorUpdateCheckString = "update_yand.php";
    public static volatile String vendorUpdateFileString = "THUpdate_yand.exe";


    private Menu vendorMenu = null;
    private MenuItem[] vendorMenuItems = null;
    private String VendorName = "Yandex";

    public Yandex(){
        init();
    }

    @Override
    public void init() {
        Menu mainMenu = new Menu(VendorName);
        setVendorMenu(mainMenu);
        setVendorMenuItems(getNewMenuItem("Create enable_logging file",handleEnabledLoggingMenuItem()),
                getNewMenuItem("Set permission for MoreLocale 2",handleMoreLocaleMenuItem()),
                getNewMenuItem("Clear browser secret data", handleBrowserSettingsMenuItem()));
    }

    @Override
    public void setVendorOn(boolean VendorOn) {
        vendorOn = VendorOn;
    }

    @Override
    public boolean getVendorOn() {
        return vendorOn;
    }

    @Override
    public void setVendorMenu(Menu vendorMenu) {
        this.vendorMenu = vendorMenu;
    }

    @Override
    public Menu getVendorMenu() {
        return vendorMenu;
    }

    @Override
    public void setVendorMenuItems(MenuItem... vendorMenuItem) {
        this.vendorMenuItems = vendorMenuItem;
    }

    @Override
    public MenuItem[] getVendorMenuItems() {
        return vendorMenuItems;
    }

    @Override
    public MenuItem getNewMenuItem(String name, EventHandler commandToExecute) {
        LoggingAdapter.debug("Vendor","Putting new item: ",name);
        MenuItem menuItem = new MenuItem(name);
        menuItem.setOnAction(commandToExecute);
        return menuItem;
    }

    @Override
    public String getVendorUpdateCheckString() {
        return vendorUpdateCheckString;
    }

    @Override
    public String getVendorUpdateFileString() {
        return vendorUpdateFileString;
    }

    private EventHandler<ActionEvent> handleEnabledLoggingMenuItem(){
        return event1 -> {
            thrp.runSilentCommand(thrp.reloadStrings("enableLoggingFileString"));
            AN_SEND.trackStatistic("Vendor",VendorName,"EnableLoggingFile");
        };
    }

    private EventHandler<ActionEvent> handleMoreLocaleMenuItem(){
        return event1 -> {
            thrp.runSilentCommand(thrp.reloadStrings("moreLocaleAccessString"));
            AN_SEND.trackStatistic("Vendor",VendorName,"MoreLocaleAccess");
        };
    }

    private EventHandler<ActionEvent> handleBrowserSettingsMenuItem(){
        return event1 -> {
            thrp.runSilentCommand(thrp.reloadStrings("browserResetSettingsSpecialString"));
            AN_SEND.trackStatistic("Vendor",VendorName,"BrowserResetSettingsSpecial");
        };
    }
}
