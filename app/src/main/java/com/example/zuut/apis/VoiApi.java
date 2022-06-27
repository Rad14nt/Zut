package com.example.zuut.apis;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.zuut.Constants;
import com.example.zuut.database.CompanyDao;
import com.example.zuut.database.ZuutDatabase;
import com.example.zuut.model.Company;
import com.example.zuut.model.Scooter;
import com.example.zuut.model.ScooterBuilder;

public class VoiApi extends Api {

    private static final String Tag = "VoiApi";
    private static final String BASE_URL = "https://api.voiapp.io/v1";
    private static final String PHONE_URL = BASE_URL + "/auth/verify/phone";
    private static final String CODE_URL = BASE_URL + "/auth/verify/code";
    private static final String EMAIL_URL = BASE_URL + "/auth/verify/presence";
    private static final String AUTH_URL = BASE_URL + "/auth/session";
    private static final String ZONE_URL = BASE_URL + "/zones?";
    private static final String ACCESS_URL = "https://api.voiapp.io/v2/rides/vehicles?zone_id=";
    private static final int COMPANY_ID = 2;

    public VoiApi() {
    }


    public static void getOtp(Context context, String phoneNumber, String countryCode, ApiSuccessHandler callback) throws JSONException {
        RequestQueue queue = Volley.newRequestQueue(context);
        JSONObject data = new JSONObject();
        data.put(Constants.COUNTRY_CODE, countryCode);
        data.put(Constants.PHONE_NUMBER, phoneNumber);


        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                PHONE_URL,
                data,
                response -> {
                    Log.d(Tag, response.toString());
                    parseOtpToken(context, response);
                    callback.handle(response);

                },
                error -> {
                    error.printStackTrace();
                    Toast.makeText(context, "Please enter a valid Phone-Number without Zeros at the start", Toast.LENGTH_LONG).show();
                }) {
        };
        queue.add(request);
    }


    public static void parseOtpToken(Context context, JSONObject result) {
        ZuutDatabase database;
        database = ZuutDatabase.getInstance(context);
        Company company = database.getCompanyDao().findById(COMPANY_ID);

        try {
            String token = result.getString(Constants.TOKEN);
            company.setToken(token);
            database.getCompanyDao().updateCompany(company);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void verifyOtp(Context context, String code, ApiSuccessHandler callback) throws JSONException {

        RequestQueue queue = Volley.newRequestQueue(context);

        ZuutDatabase database;
        database = ZuutDatabase.getInstance(context);
        Company company = database.getCompanyDao().findById(COMPANY_ID);

        JSONObject data = new JSONObject();
        data.put(Constants.CODE, code);
        data.put(Constants.TOKEN, company.getToken());


        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                CODE_URL,
                data,
                response -> {
                    Log.d(Tag, response.toString());
                    callback.handle(response);
                },
                error -> {
                    error.printStackTrace();
                    Toast.makeText(context, "The key you entered is invalid", Toast.LENGTH_LONG).show();
                }) {

        };
        queue.add(request);
    }


    public void authenticate(Context context, ApiSuccessHandler callback) throws JSONException {
        RequestQueue queue = Volley.newRequestQueue(context);

        ZuutDatabase database;
        database = ZuutDatabase.getInstance(context);
        Company company = database.getCompanyDao().findById(COMPANY_ID);

        JSONObject data = new JSONObject();
        data.put(Constants.AUTHENTICATION_TOKEN, company.getAuthenticationToken());

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                AUTH_URL,
                data,
                response -> {
                    Log.d(Tag, response.toString());
                    callback.handle(response);
                    try {
                        String accessToken = response.getString(Constants.ACCESS_TOKEN);
                        String authenticationToken = response.getString(Constants.AUTHENTICATION_TOKEN);
                        company.setAuthenticationToken(authenticationToken);
                        System.out.println(response + "LOLOL");
                        company.setAccess(accessToken);
                        database.getCompanyDao().updateCompany(company);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(context, "Parsing failed", Toast.LENGTH_LONG).show();
                    }
                },
                error -> {
                    error.printStackTrace();
                    Toast.makeText(context, "Opening Api Session failed", Toast.LENGTH_LONG).show();
                }) {
        };
        queue.add(request);
    }


    @Override
    public void registerNewEmail(Context context, String email, ApiSuccessHandler callback) throws JSONException {
        RequestQueue queue = Volley.newRequestQueue(context);
        ZuutDatabase database;
        database = ZuutDatabase.getInstance(context);
        Company company = database.getCompanyDao().findById(COMPANY_ID);;

        JSONObject data = new JSONObject();
        data.put(Constants.EMAIL, email);
        data.put(Constants.TOKEN, company.getToken());
        CompanyDao companyDao = ZuutDatabase.getInstance(context).getCompanyDao();

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                EMAIL_URL,
                data,
                response -> {
                    Log.d(Tag, response.toString());
                    try {
                        String authenticationToken = response.getString("authToken");
                        company.setAuthenticationToken(authenticationToken);
                        company.setDatetime(LocalDateTime.now().toEpochSecond(ZoneOffset.UTC));
                        companyDao.updateCompany(company);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    callback.handle(response);
                },
                error -> {
                    error.printStackTrace();
                    System.out.println("error here");
                    Toast.makeText(context, "Please enter a valid E-Mail address", Toast.LENGTH_LONG).show();
                }) {

        };
        queue.add(request);
    }

    public List<Scooter> parseScootersFromJson(JSONObject result, ParsedScooterHandler callback) throws JSONException {
        List<Scooter> scooters = new ArrayList<>();
        JSONArray vehicleGroups = result.getJSONObject(Constants.DATA).getJSONArray(Constants.VEHICLE_GROUPS);
        JSONObject objects = vehicleGroups.getJSONObject(0);
        JSONArray vehicles = objects.getJSONArray(Constants.VEHICLES);
        for (int i = 0; i < vehicles.length(); i++) {
            JSONObject jsonScooter = vehicles.getJSONObject(i);
            Scooter scooter = ScooterBuilder
                    .create()
                    .id(jsonScooter.getString(Constants.ID))
                    .make(Constants.VOI)
                    .battery(jsonScooter.getDouble(Constants.BATTERY))
                    .lat(jsonScooter.getJSONObject(Constants.LOCATION).getDouble(Constants.LAT))
                    .lng(jsonScooter.getJSONObject(Constants.LOCATION).getDouble(Constants.LNG))
                    .build();
            scooters.add(scooter);
        }
        callback.handle(scooters);
        return scooters;
    }

    public void getScooterList(Context context, String zone, ParsedScooterHandler callback) throws JSONException {
        RequestQueue queue = Volley.newRequestQueue(context);
        JSONObject data = new JSONObject();
        data.put(Constants.ZONE, zone);
        ZuutDatabase database;
        database = ZuutDatabase.getInstance(context);
        Company company = database.getCompanyDao().findById(COMPANY_ID);

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                ACCESS_URL  + zone,
                data,
                response -> {
                    Log.d(Tag, response.toString());
                    try {
                        callback.handle((parseScootersFromJson(response, callback)));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                Throwable::printStackTrace) {

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put(Constants.X_ACCESS_TOKEN, company.getAccess());
                return headers;
            }
        };
        queue.add(request);
    }


    @Override
    public void newRetrieveScooters(Context context, String latitude, String longitude, Company company, ParsedScooterHandler callback) {

        if (LocalDateTime.now().minusMinutes(5).isAfter(LocalDateTime.ofEpochSecond(company.getDatetime(), 0, ZoneOffset.UTC))) {
            try {
                authenticate(context, result -> {
                    CompanyDao companyDao = ZuutDatabase.getInstance(context).getCompanyDao();
                    company.setDatetime(LocalDateTime.now().toEpochSecond(ZoneOffset.UTC));
                    companyDao.updateCompany(company);
                    Log.d(Tag, result.toString());
                    newInternalFetchScooters(context, latitude, longitude, company, callback);
                });
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            newInternalFetchScooters(context, latitude, longitude, company, callback);
        }
    }

    private void newInternalFetchScooters(Context context, String latitude, String longitude, Company company, Api.ParsedScooterHandler callback) {
        RequestQueue queue = Volley.newRequestQueue(context);

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                ZONE_URL + "lat=" + latitude + "&lng=" + longitude,
                null,
                response -> {

                    JSONArray cities;
                    try {
                        cities = response.getJSONArray(Constants.ZONES);
                        JSONObject city = cities.getJSONObject(0);
                        String zone = city.getString(Constants.ZONE_ID);
                        Log.d(Tag, response.toString());
                        getScooterList(context, zone, callback);

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(context, "Failed to retrieve VOI scooters.", Toast.LENGTH_LONG).show();
                    }
                },
                error -> {
                    error.printStackTrace();
                    CompanyDao companyDao = ZuutDatabase.getInstance(context).getCompanyDao();
                    company.setActivated(false);
                    companyDao.updateCompany(company);
                    Toast.makeText(context, "Deactivating VOI due to error, please reset manually in Settings.", Toast.LENGTH_LONG).show();
                }) {


            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put(Constants.X_ACCESS_TOKEN, company.getAccess());
                return headers;
            }
        };
        queue.add(request);
    }
}
