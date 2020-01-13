package ru.zeburek.zerohelper.vendors;

import javafx.event.EventHandler;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;

/**
 * Created by zeburek on 09.06.2017.
 */
public interface DefaultVendor{

    public void init();

    public void setVendorOn(boolean VendorOn);
    public boolean getVendorOn();

    public void setVendorMenu(Menu vendorMenu);
    public Menu getVendorMenu();

    public void setVendorMenuItems(MenuItem... vendorMenuItem);
    public MenuItem[] getVendorMenuItems();

    public MenuItem getNewMenuItem(String name, EventHandler commandToExecute);

    public String getVendorUpdateCheckString();
    public String getVendorUpdateFileString();

    public String toString();


}
