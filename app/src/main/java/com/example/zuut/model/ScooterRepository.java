package com.example.zuut.model;

import java.util.ArrayList;
import java.util.List;

public class ScooterRepository {
    private static final ScooterRepository instance = new ScooterRepository();
    private final List<com.example.zuut.model.Scooter> scooters = new ArrayList<>();

    private ScooterRepository() {}

    public static synchronized ScooterRepository getInstance() {
        return instance;
    }

    public List<com.example.zuut.model.Scooter> getScooters() {
        return scooters;
    }

    public void addScooters(List<com.example.zuut.model.Scooter> scooter) {
        scooters.addAll(scooter);
    }

    public void clear() {
        scooters.clear();
    }



}
