package com.ahmet.barberbooking.Model;

import java.util.Map;

public class FCMSendData {

    private String to;
    private Map<String, String> mMapData;

    public FCMSendData() {}

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public Map<String, String> getmMapData() {
        return mMapData;
    }

    public void setmMapData(Map<String, String> mMapData) {
        this.mMapData = mMapData;
    }
}
