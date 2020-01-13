/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.zeburek.zerohelper.providers;

import ru.zeburek.zerohelper.utils.LoggingAdapter;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import static ru.zeburek.zerohelper.ZeroHelper.AN_SEND;
import static ru.zeburek.zerohelper.controllers.FXMLDocumentController.VENDOR;

/**
 *
 * @author zeburek
 */
public class UpdateProvider {
    private String ver;
    private final String user = "zeburek";
    private final String pass = "Nothingcanfade01";
    private final String USER_AGENT;
    private final String APP_DATA_DIR;
    private final String APP_DIR_APP = System.getProperty("user.dir")
            +File.separator+"app"+File.separator;
    private final String APP_DIR = System.getProperty("user.dir")
            +File.separator;
    private final File APP_DATA_JAR;

    public UpdateProvider(String version, String appDataDir) {
        setCurrentStrings(version);
        USER_AGENT = "ZHelper/"+ver;
        APP_DATA_DIR = appDataDir;
        APP_DATA_JAR = new File(APP_DATA_DIR+"/THUpdate.exe");
    }
    
    public boolean isUpToDate() {
        try {
            byte[] uE = Base64.getEncoder().encode(user.getBytes());
            byte[] pE = Base64.getEncoder().encode(pass.getBytes());
            byte[] vE = Base64.getEncoder().encode(ver.getBytes());
            
            String url = "https://parviz.pw/thelper/update.php";
            URL obj = new URL(url);
            HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
            LoggingAdapter.info("Update","Sending 'POST' request to URL : " + url);

            con.setConnectTimeout(5000);
            //add request header
            con.setRequestMethod("POST");
            con.setRequestProperty("User-Agent", USER_AGENT);
            con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
            
            String urlParameters = "u="+new String(uE)+"&p="+new String(pE)+"&v="+new String(vE);
            
            // Send post request
            con.setDoOutput(true);
            try (DataOutputStream wr = new DataOutputStream(con.getOutputStream())) {
                wr.writeBytes(urlParameters);
                wr.flush();
            }
            
            int responseCode = con.getResponseCode();
            LoggingAdapter.info("Update","Response Code : " + responseCode);
            AN_SEND.trackStatistic("Update","Response Code",""+responseCode);
            return isResponseSuccess(responseCode);
        }   catch (IOException ex) {
            if (ex instanceof SocketTimeoutException){
                AN_SEND.trackStatistic("Update","Error","Connection TimeOut");
            } else {
                Logger.getLogger(UpdateProvider.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return true;
    }
    
    public void getUpdatedSource(){
        new Thread(){
            @Override
            public void run(){
                try {
                    LoggingAdapter.info("Update","Starting to download...");
                    URL website = new URL("https://parviz.pw/thelper/updates/THUpdate.exe");
                    try (InputStream in = website.openStream()) {
                        Files.copy(in, APP_DATA_JAR.toPath(), StandardCopyOption.REPLACE_EXISTING);
                    } catch (IOException ex) {
                        Logger.getLogger(UpdateProvider.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } catch (MalformedURLException ex) {
                    Logger.getLogger(UpdateProvider.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }.start();
        
    }
    
    public void updateIfNeeded() throws IOException, InterruptedException{
        File alreadyUpdated = new File(APP_DATA_DIR+"/THupdated");
        File update = new File(APP_DATA_DIR+"/THUpdate.exe");
        if (update.exists() && !alreadyUpdated.exists()){
            final ProcessBuilder builder = new ProcessBuilder(APP_DATA_DIR+"/THUpdate.exe");
            Process proc = builder.start();
            alreadyUpdated.createNewFile();
            System.exit(0);
        } else if (update.exists() && alreadyUpdated.exists()) {
            update.delete();
            alreadyUpdated.delete();
        }
    }
    
    private void setCurrentStrings(String version){
        ver = version;
        
    }
    
    private boolean isResponseSuccess(int answer){
        return answer == 204;
    }
    
    static {
    final TrustManager[] trustAllCertificates = new TrustManager[] {
        new X509TrustManager() {
            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return null; // Not relevant.
            }
            @Override
            public void checkClientTrusted(X509Certificate[] certs, String authType) {
                // Do nothing. Just allow them all.
            }
            @Override
            public void checkServerTrusted(X509Certificate[] certs, String authType) {
                // Do nothing. Just allow them all.
            }
        }
    };

    try {
        SSLContext sc = SSLContext.getInstance("SSL");
        sc.init(null, trustAllCertificates, new SecureRandom());
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
    } catch (GeneralSecurityException e) {
        throw new ExceptionInInitializerError(e);
    }
}
}
