package ru.zeburek.zerohelper.vendors;

import javafx.scene.control.MenuButton;
import javafx.scene.control.SeparatorMenuItem;
import ru.zeburek.zerohelper.utils.LoggingAdapter;
import ru.zeburek.zerohelper.vendors.vendor.Yandex;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by zeburek on 11.06.2017.
 */
public class Vendor {
    private volatile MenuButton mMenuButton;
    private List<DefaultVendor> vendorsList;

    public Vendor(MenuButton menuButton){
        LoggingAdapter.info("Vendors","Checking states of vendors");
        setStatusesForVendors();
        mMenuButton = menuButton;
        menuButton.getItems().add(new SeparatorMenuItem());
        vendorsList = getActivatedVendors();
        putActivatedVendorsToMenu();
        StringBuilder vendorStringBuilder = new StringBuilder();
        for (DefaultVendor vendor:
             vendorsList) {
            vendorStringBuilder.append(vendor.getVendorMenu().getText()+",");
        }
        LoggingAdapter.info("Vendors", vendorStringBuilder.toString());
    }

    private ArrayList<DefaultVendor> getActivatedVendors() {
        ArrayList<DefaultVendor> arrayList = new ArrayList<>();
        arrayList.add(new Yandex());
        return arrayList;
    }

    private void putActivatedVendorsToMenu() {
        for (DefaultVendor vendor:
             vendorsList) {
            if(vendor.getVendorOn()) {
                vendor.getVendorMenu().getItems().addAll(vendor.getVendorMenuItems());
                mMenuButton.getItems().add(vendor.getVendorMenu());
            }
        }
    }

    private void setStatusesForVendors(){
        Yandex.vendorOn = false;
    }
}
