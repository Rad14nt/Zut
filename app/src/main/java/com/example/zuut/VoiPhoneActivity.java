package com.example.zuut;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import com.example.zuut.database.ZuutDatabase;
import org.json.JSONException;
import com.example.zuut.apis.VoiApi;
import com.example.zuut.model.Company;

public class VoiPhoneActivity extends AppCompatActivity {

    private String country_code;
    Button send_btn;
    EditText et_phone;
    ZuutDatabase zuutDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voi_phone);
        send_btn = findViewById(R.id.btn_phoneSend);
        et_phone = findViewById(R.id.et_phoneNumber);
        TelephonyManager tm = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        country_code = tm.getNetworkCountryIso().toUpperCase();

        ImageView upButton = findViewById(R.id.upButton);
        TextView title = findViewById(R.id.toolbarTitle);
        title.setText(R.string.voi_phone_title);
        upButton.setOnClickListener(v -> finish());



        zuutDatabase = ZuutDatabase.getInstance(VoiPhoneActivity.this);

        upButton.setOnClickListener(v -> {
            Company currentCompany = zuutDatabase.getCompanyDao().findById(2);
            currentCompany.setActivated(false);
            zuutDatabase.getCompanyDao().updateCompany(currentCompany);
            finish();
        });

        send_btn.setOnClickListener(v -> {
            try {
                VoiApi.getOtp(getApplicationContext(), et_phone.getText().toString(), country_code, result -> {
                    Intent intent = new Intent(this, VoiVerifyPhoneActivity.class);
                    startActivity(intent);
                });
            } catch (JSONException e) {
                Log.e(Constants.VOI_PHONE_ACTIVITY, Constants.REGISTER_FAILED, e);
            }
        });
    }

}