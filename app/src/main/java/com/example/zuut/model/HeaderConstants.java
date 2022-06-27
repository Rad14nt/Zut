package com.example.zuut.model;

import java.util.HashMap;
import java.util.Map;

//Immutable maps uf wish bstellt mit grupperabatt wege Java 8
public class HeaderConstants {
    public static final Map<String, String> emailHeader = new HashMap<>();
    public static final Map<String, String> newTHeader = new HashMap<>();
    public static final Map<String, String> fetschScootersListHeader = new HashMap<>();




    static {
        emailHeader.put("User-Agent", "Bird/4.119.0(co.bird.Ride; build:3; iOS 14.3.0) Alamofire/5.2.2");
        emailHeader.put("Device-Id", "124435a3-9c0a-4967-8e20-ef1f841e568e");
        emailHeader.put("Platform", "ios");
        emailHeader.put("App-Version", "4.119.0");
        emailHeader.put("Content-Type", "application/json");

        newTHeader.put("User-Agent", "Bird/4.119.0(co.bird.Ride; build:3; iOS 14.3.0) Alamofire/5.2.2");
        newTHeader.put("Device-Id", "124435a3-9c0a-4967-8e20-ef1f841e568e");
        newTHeader.put("Platform", "ios");
        newTHeader.put("App-Version", "4.119.0");

        fetschScootersListHeader.put("User-Agent", "Bird/4.119.0(co.bird.Ride; build:3; iOS 14.3.0) Alamofire/5.2.2");
        fetschScootersListHeader.put("legacyrequest", "false");
        fetschScootersListHeader.put("Device-Id", "124435a3-9c0a-4967-8e20-ef1f841e568e");
        fetschScootersListHeader.put("App-Version", "4.119.0");

    }

}
