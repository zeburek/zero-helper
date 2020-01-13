/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.zeburek.zerohelper.controllers;

import com.brsanthu.googleanalytics.GoogleAnalytics;
import ru.zeburek.zerohelper.utils.LoggingAdapter;

import static ru.zeburek.zerohelper.ZeroHelper.ENABLE_DEBUG;
import static ru.zeburek.zerohelper.ZeroHelper.VER_ID;

/**
 *
 * @author zeburek
 */
public class AnalyticsController {
    private final static GoogleAnalytics G_A = GoogleAnalytics
            .builder()
            .withAppName("Zero Helper")
            .withAppVersion(VER_ID)
            .withTrackingId("UA-93471795-1")
            .build();

    
    public AnalyticsController(){
        G_A.screenView("Zero Helper", "Started").sendAsync();
        G_A.getConfig().setGatherStats(true);
        LoggingAdapter.stat("Analytics","Initiated","Stats: "+G_A.getConfig().isGatherStats());
    }
    
    public void trackStatistic(String trackedMessage){
        LoggingAdapter.stat(trackedMessage,trackedMessage);
        if (ENABLE_DEBUG) return;
        G_A.event().eventCategory(trackedMessage).eventAction(trackedMessage).sendAsync();
    }
    
    public void trackStatistic(String trackedName, String trackedMessage){
        trackStatistic(trackedName, trackedMessage,trackedName);
    }

    public void trackStatistic(String trackedName, String trackedMessage, String trackedValue){
        LoggingAdapter.stat(trackedName,trackedMessage,trackedValue);
        if (ENABLE_DEBUG) return;
        G_A.event()
                .eventCategory(trackedName)
                .eventAction(trackedMessage)
                .eventLabel(trackedValue)
                .eventValue(1)
                .sendAsync();
    }
}
