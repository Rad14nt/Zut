package com.example.zuut.apis;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import com.example.zuut.model.Company;
import com.example.zuut.model.Scooter;

public abstract class Api {

    protected Company company;

    public static Api getInstanceOfApi(Company company) {
        Api api;
        if (company.getType() == ApiType.VOI) {
            api = new VoiApi();
        } else {
            api = new BirdApi();
        }
        api.setCompany(company);
        return api;
    }

    protected void setCompany(Company company) {
        this.company = company;
    }

    public Company getCompany() {
        return company;
    }

    public abstract void newRetrieveScooters(Context context, String latitude, String longitude, Company company, ParsedScooterHandler callback);

    public abstract void registerNewEmail(Context context, String email, ApiSuccessHandler callback) throws JSONException;

    public interface ApiSuccessHandler {
        void handle(JSONObject result);
    }

    public interface ParsedScooterHandler {
        void handle(List<Scooter> scooters);
    }

    public enum ApiType {
        BIRD,
        VOI
    }
}