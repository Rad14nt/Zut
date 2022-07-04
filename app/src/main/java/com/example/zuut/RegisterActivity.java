package com.example.zuut;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.zuut.database.ZuutDatabase;

import org.json.JSONException;

import com.example.zuut.apis.Api;
import com.example.zuut.database.CompanyDao;
import com.example.zuut.database.ZuutDatabase;
import com.example.zuut.model.Company;

public class RegisterActivity extends AppCompatActivity {

    private static final int VOI_ACTIVITY_ID = 2;
    public static final String COMPANY_ID_KEY = "companyId";

    Button register_btn;
    EditText et_email;
    ListView lv_list;
    ZuutDatabase database;
    ImageView company_logo;

    @Override
    protected void onPostResume() {
        super.onPostResume();
        database = ZuutDatabase.getInstance(RegisterActivity.this);
        int companyId = getIntent().getIntExtra(FilterActivity.COMPANY_ID_KEY, VOI_ACTIVITY_ID);

        Company company = database.getCompanyDao().findById(companyId);
        switch (company.getName()){
            case Constants.BIRD: company_logo.setImageResource(R.drawable.bird_logo);
            break;
            case Constants.VOI: company_logo.setImageResource(R.drawable.voi_logo);
            break;
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_register);

        register_btn = findViewById(R.id.btn_register);
        lv_list = findViewById(R.id.lv_list);
        et_email = findViewById(R.id.et_email);
        company_logo = findViewById(R.id.register_company_Logo);
        ImageView upButton = findViewById(R.id.upButton);
        TextView title = findViewById(R.id.toolbarTitle);
        title.setText(R.string.register);



        upButton.setOnClickListener(v -> finish());

        database = ZuutDatabase.getInstance(RegisterActivity.this);
        int companyId = getIntent().getIntExtra(FilterActivity.COMPANY_ID_KEY, VOI_ACTIVITY_ID);

        Company company = database.getCompanyDao().findById(companyId);
        CompanyDao companyDao = ZuutDatabase.getInstance(getApplicationContext()).getCompanyDao();
        Api api = Api.getInstanceOfApi(company);


        switch (company.getName()){
            case Constants.BIRD: company_logo.setImageResource(R.drawable.bird_logo);
                break;
            case Constants.VOI: company_logo.setImageResource(R.drawable.voi_logo);
                break;
        }


        upButton.setOnClickListener(v ->
        {
            company.setActivated(false);
            database.getCompanyDao().updateCompany(company);
            finish();
        });

        register_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    api.registerNewEmail(getApplicationContext(), et_email.getText().toString(), result -> {

                        if (company.getId() != 2) {
                            Intent intent = new Intent(RegisterActivity.this, com.example.zuut.TokenActivity.class);
                            intent.putExtra(COMPANY_ID_KEY, company.getId());
                            startActivity(intent);
                        }
                        else {
                            Intent intent = new Intent(RegisterActivity.this, MapsActivity.class);
                            intent.putExtra(COMPANY_ID_KEY, company.getId());
                            startActivity(intent);
                        }
                    });


                    //todo ignore error 422
                } catch (JSONException e) {
                    Log.e(Constants.REGISTER_ACTIVITY, Constants.REGISTER_FAILED, e);
                }
            }
        });
    }
}