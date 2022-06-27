package com.example.zuut.model;

import com.google.android.gms.maps.model.Marker;

import java.util.Objects;

public class Scooter {

    private String id;
    private double lat;
    private double log;
    private String make;
    private double battery_level;
    private double estimated_range;
    private Marker marker;

    public Scooter() {}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMake() {
        return make;
    }

    public void setMake(String make) {
        this.make = make;
    }

    public double getBatteryLevel() {
        return battery_level;
    }

    public void setBatteryLevel(double battery_level) {
        this.battery_level = battery_level;
    }

    public double getEstimatedRange() {
        return estimated_range;
    }

    public void setEstimatedRange(double estimated_range) {
        this.estimated_range = estimated_range;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLong() {
        return log;
    }

    public void setLong(double log) {
        this.log = log;
    }

    public Marker getMarker() {
        return marker;
    }

    public void setMarker(Marker marker) {
        this.marker = marker;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Scooter scooter = (Scooter) o;
        return Objects.equals(id, scooter.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
