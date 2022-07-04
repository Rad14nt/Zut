package com.example.zuut;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;

import com.example.zuut.apis.VoiApi;
import com.example.zuut.database.ZuutDatabase;

public class VoiVerifyPhoneActivity extends AppCompatActivity {

    Button send_btn;
    EditText et_phoneVerification;
    ZuutDatabase zuutDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voi_verify_phone);
        send_btn = findViewById(R.id.btn_verify_phone);
        et_phoneVerification = findViewById(R.id.et_phoneNumberAuth);

        ImageView upButton = findViewById(R.id.upButton);
        TextView title = findViewById(R.id.toolbarTitle);
        title.setText(R.string.voi_phone_title);
        upButton.setOnClickListener(v -> finish());

        zuutDatabase = ZuutDatabase.getInstance(VoiVerifyPhoneActivity.this);
        send_btn.setOnClickListener(v -> {
                try {
                    VoiApi.verifyOtp(getApplicationContext(), et_phoneVerification.getText().toString(), result -> {
                        Intent intent = new Intent(VoiVerifyPhoneActivity.this, com.example.zuut.RegisterActivity.class);
                        startActivity(intent);
                    });
                } catch (JSONException e) {
                    Log.e(Constants.VOI_VERIFY_PHONE_ACTIVITY, Constants.REGISTER_FAILED, e);
                }
        });
    }
}