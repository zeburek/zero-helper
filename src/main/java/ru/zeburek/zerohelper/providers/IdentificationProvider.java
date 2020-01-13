/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.zeburek.zerohelper.providers;

import ru.zeburek.zerohelper.utils.LoggingAdapter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Properties;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author zeburek
 */
public class IdentificationProvider {
    public final Properties prop = new Properties();
    public FileInputStream fisProp;
    private String path;
    
    public IdentificationProvider(String appDataPath){
        path = appDataPath+"/thid";
        try {
            File fProp = new File(path);
            if(!fProp.exists()){fProp.createNewFile();}
            fisProp = new FileInputStream(path);
            prop.load(fisProp);
            setIdentificatorIfNotExist();
        } catch (Exception ex) {
            LoggingAdapter.info("Update","THID alredy exists.");
        }
    }
    
    public String getIdentificator(){
        LoggingAdapter.info("Update","UUID: "+prop.getProperty("id"));
        return prop.getProperty("id");
    }
    
    public void saveUUID(){
        OutputStream output = null;
        try {
            output = new FileOutputStream(path);
            prop.store(output, "UUID");
        } catch (Exception ex) {
            Logger.getLogger(IdentificationProvider.class.getName()).log(Level.SEVERE, null, ex);
        } 
    }

    private void setIdentificatorIfNotExist() {
        if(!checkIdentifierExist()){
            String id = generateIdentifier();
            prop.setProperty("id", id);
        }
    }

    private boolean checkIdentifierExist() {
        return prop.containsKey("id");
    }
    
    private String generateIdentifier() {
        final String uuid = UUID.randomUUID().toString();
        return uuid;
    }
    
}
