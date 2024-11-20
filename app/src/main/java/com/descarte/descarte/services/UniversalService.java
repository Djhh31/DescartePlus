package com.descarte.descarte.services;

import com.descarte.descarte.entitties.Data;

import java.util.List;

public abstract class UniversalService {
    double lat;
    double lng;
    int distance;

    public UniversalService(double Latitude, double Longitute){
        this.lat=Latitude;
        this.lng=Longitute;
    }

    public UniversalService(double Latitude, double Longitute, int distance){
        this(Latitude, Longitute);
        this.distance = distance;
    }

    public abstract String ApiUrlBuild();
    public abstract void GetDataAsync(OnDataReceivedListener listener);

    public interface OnDataReceivedListener {
        void onDataReceived(List<Data> results);
    }

}
