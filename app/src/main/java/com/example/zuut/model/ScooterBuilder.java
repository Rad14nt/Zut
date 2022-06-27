package com.example.zuut.model;

public class ScooterBuilder {
    private Scooter scooter = new Scooter();


    public static ScooterBuilder create() {
        return new ScooterBuilder();
    }

    public ScooterBuilder id(String id) {
        scooter.setId(id);
        return this;
    }

    public ScooterBuilder lat(double lat) {
        scooter.setLat(lat);
        return this;
    }

    public ScooterBuilder lng(double lng) {
        scooter.setLong(lng);
        return this;
    }

    public ScooterBuilder make(String make) {
        scooter.setMake(make);
        return this;
    }

    public ScooterBuilder battery(double batteryLevel) {
        scooter.setBatteryLevel(batteryLevel);
        return this;
    }

    public ScooterBuilder estimatedRange(double estimatedRange) {
        scooter.setEstimatedRange(estimatedRange);
        return this;
    }

    public Scooter build() {
        return scooter;
    }
}
