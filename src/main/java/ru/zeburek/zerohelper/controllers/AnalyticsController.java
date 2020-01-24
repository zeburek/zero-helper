/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.zeburek.zerohelper.controllers;
import ru.zeburek.zerohelper.utils.LoggingAdapter;

/**
 *
 * @author zeburek
 */
public class AnalyticsController {
    
    public void trackStatistic(String trackedMessage){
        LoggingAdapter.stat(trackedMessage,trackedMessage);
    }
    
    public void trackStatistic(String trackedName, String trackedMessage){
        trackStatistic(trackedName, trackedMessage,trackedName);
    }

    public void trackStatistic(String trackedName, String trackedMessage, String trackedValue){
        LoggingAdapter.stat(trackedName,trackedMessage,trackedValue);
    }
}
