package com.example.zuut;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.zuut.database.ZuutDatabase;
import org.json.JSONException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import com.example.zuut.apis.BirdApi;
import com.example.zuut.model.Company;

public class TokenActivity extends AppCompatActivity {

    Button btn_send;
    EditText et_token;
    ListView lv_list;
    ZuutDatabase database;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_token);
        et_token = findViewById(R.id.et_token);
        btn_send = findViewById(R.id.btn_send);
        lv_list = findViewById(R.id.lv_list);
        database = ZuutDatabase.getInstance(TokenActivity.this);
        int companyId = getIntent().getIntExtra(FilterActivity.COMPANY_ID_KEY, -1);
        Company company = database.getCompanyDao().findById(companyId);
        btn_send.setOnClickListener(v -> {
            Log.d(Constants.TEST, et_token.getText().toString());
            try {
                company.setToken(et_token.getText().toString());
                BirdApi.sendToken(getApplicationContext(), et_token.getText().toString(), result -> {
                    try {
                        company.setAccess(result.getString(Constants.ACCESS));
                        company.setRefresh(result.getString(Constants.REFRESH));
                        company.setDatetime(LocalDateTime.now().toEpochSecond(ZoneOffset.UTC));
                        database.getCompanyDao().updateCompany(company);
                        openMapsActivity();
                    } catch (JSONException e) {
                        Log.e(Constants.TOKEN_ACTIVITY, Constants.JSON_PARSING_FAILED, e);
                    }
                });
            } catch (JSONException e) {
                Log.e(Constants.REGISTER_ACTIVITY, Constants.REGISTER_FAILED, e);
            }
        });
    }
    public void openMapsActivity() {
        Intent intent = new Intent(this, MapsActivity.class);
        startActivity(intent);
    }
}