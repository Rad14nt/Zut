package com.example.zuut.apis;

import static com.example.zuut.model.HeaderConstants.emailHeader;
import static com.example.zuut.model.HeaderConstants.fetschScootersListHeader;
import static com.example.zuut.model.HeaderConstants.newTHeader;

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

public class BirdApi extends Api {



    private static final String EMAIL_URL = "https://api-auth.prod.birdapp.com/api/v1/auth/email";
    private static final String TOKEN_URL = "https://api-auth.prod.birdapp.com/api/v1/auth/magic-link/use";
    private static final String ACCESS_URL = "https://api-bird.prod.birdapp.com/bird/nearby?radius=1000";
    private static final String REFRESH_URL = "https://api-auth.prod.birdapp.com/api/v1/auth/refresh/token";
    private static final String TAG = "BirdApi";

    public BirdApi() {

    }

    public void registerNewEmail(Context context, String email, ApiSuccessHandler callback) throws JSONException {
        RequestQueue queue = Volley.newRequestQueue(context);

        JSONObject data = new JSONObject();
        data.put(Constants.EMAIL, email);

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                EMAIL_URL,
                data,
                response -> {
                    Log.d(TAG, response.toString());
                    callback.handle(response);
                },
                error -> {
                    Toast.makeText(context, "Please enter a valid E-Mail address", Toast.LENGTH_LONG).show();
                }) {

            @Override
            public Map<String, String> getHeaders() {
                return emailHeader;
            }
        };
        queue.add(request);
    }

    public void getNewTokens(Context context, String refreshkey, Api.ApiSuccessHandler callback) {
        RequestQueue queue = Volley.newRequestQueue(context);

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                REFRESH_URL,
                null,
                response -> {
                    Log.d(TAG, response.toString());
                    callback.handle(response);
                },
                Throwable::printStackTrace) {

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>(newTHeader);
                headers.put("Authorization", "Bearer " + refreshkey);
                return headers;
            }
        };
        queue.add(request);
    }


    public static void sendToken(Context context, String token, ApiSuccessHandler callback) throws JSONException {
        RequestQueue queue = Volley.newRequestQueue(context);

        JSONObject data = new JSONObject();
        data.put(Constants.TOKEN, token);

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                TOKEN_URL,
                data,
                response -> {
                    Log.d(TAG, response.toString());
                    callback.handle(response);
                },
                error -> {
                    error.printStackTrace();
                    Toast.makeText(context, "The key you entered is invalid", Toast.LENGTH_LONG).show();
                }) {

            @Override
            public Map<String, String> getHeaders() {
                return emailHeader;
            }
        };
        queue.add(request);

    }

    @Override
    public void newRetrieveScooters(Context context, String latitude, String longitude, Company company, ParsedScooterHandler callback) {

        if (LocalDateTime.now().minusDays(1).isAfter(LocalDateTime.ofEpochSecond(company.getDatetime(), 0, ZoneOffset.UTC))) {
            getNewTokens(context, company.getRefresh(), result -> {
                CompanyDao companyDao = ZuutDatabase.getInstance(context).getCompanyDao();
                try {
                    company.setAccess(result.getString(Constants.ACCESS));
                    company.setRefresh(result.getString(Constants.REFRESH));
                    company.setDatetime(LocalDateTime.now().toEpochSecond(ZoneOffset.UTC));
                    companyDao.updateCompany(company);
                    newInternalFetchScooters(context, latitude, longitude, company, callback);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            });
        } else {
            newInternalFetchScooters(context, latitude, longitude, company, callback);
        }
    }

    private void newInternalFetchScooters(Context context, String latitude, String longitude, Company company, ParsedScooterHandler callback) {
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = ACCESS_URL + "&latitude=" + latitude + "&longitude=" + longitude;
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    Log.d(TAG, response.toString());
                    try {
                        parseScootersFromJson(response, callback);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                Throwable::printStackTrace) {

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>(fetschScootersListHeader);
                headers.put("Authorization", "Bearer " + company.getAccess());
                headers.put(Constants.LOCATION, "{\"latitude\":" + latitude + ",\"longitude\":" + longitude + ",\"altitude\":500,\"accuracy\":65,\"speed\":-1,\"heading\":-1}");
                return headers;
            }
        };
        queue.add(request);
    }


    public void parseScootersFromJson(JSONObject result, ParsedScooterHandler callback) throws JSONException {
        List<Scooter> scooters = new ArrayList<>();
        JSONArray birds = result.getJSONArray(Constants.BIRDS);
        for (int i = 0; i < birds.length(); i++) {

            JSONObject jsonScooter = birds.getJSONObject(i);

            Scooter scooter = ScooterBuilder.create()
                    .make(Constants.BIRD)
                    .id(jsonScooter.getString(Constants.ID))
                    .estimatedRange(jsonScooter.getDouble(String.valueOf(Constants.ESTIMATED_RANGE)))
                    .battery(jsonScooter.getDouble(Constants.BATTERY_LEVEL))
                    .lat(jsonScooter.getJSONObject(Constants.LOCATION).getDouble(Constants.LATITUDE))
                    .lng(jsonScooter.getJSONObject(Constants.LOCATION).getDouble(Constants.LONGITUDE))
                    .build();
            scooters.add(scooter);
        }
        callback.handle(scooters);
    }
}
