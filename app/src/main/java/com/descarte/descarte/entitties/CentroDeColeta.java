package com.descarte.descarte.entitties;

public class CentroDeColeta {

    public String id;
    public String name;
    public String address;
    public double lat;
    public double lng;

    public CentroDeColeta(String id, String name, String address, double lat, double lng) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.lat = lat;
        this.lng = lng;
    }
}
