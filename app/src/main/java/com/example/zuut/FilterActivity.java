package com.example.zuut;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import com.example.zuut.adapters.CompanyListAdapter;
import com.example.zuut.apis.Api;
import com.example.zuut.database.ZuutDatabase;
import com.example.zuut.model.Company;

public class FilterActivity extends AppCompatActivity {

    private CompanyListAdapter adapter;
    private ZuutDatabase database;
    public static final String COMPANY_ID_KEY = "companyId";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        database = ZuutDatabase.getInstance(FilterActivity.this);

        setContentView(R.layout.activity_filter);
        ImageView upButton = findViewById(R.id.upButton);
        TextView title = findViewById(R.id.toolbarTitle);
        title.setText(R.string.settingsTranslation);
        upButton.setOnClickListener(v -> finish());
    }

    @Override
    protected void onStart() {
        super.onStart();

        ArrayList<Company> companies = (ArrayList<Company>) database.getCompanyDao().findAllCompanies();

        adapter = new CompanyListAdapter(companies, (position, isChecked) -> {

            Company currentCompany = adapter.getCompanyAtPos(position);
            currentCompany.setActivated(isChecked);

            if (currentCompany.getType() == Api.ApiType.VOI && currentCompany.getAccess() == null && currentCompany.isActivated()) {
                Intent intent;
                if (currentCompany.getToken() == null) {
                    intent = new Intent(this, com.example.zuut.VoiPhoneActivity.class);
                } else {
                    intent = new Intent(this, com.example.zuut.RegisterActivity.class);
                    intent.putExtra(COMPANY_ID_KEY, currentCompany.getId());
                }
                startActivity(intent);
            } else if (currentCompany.isActivated() && currentCompany.getAccess() == null) {
                Intent intent = new Intent(this, com.example.zuut.RegisterActivity.class);
                intent.putExtra(COMPANY_ID_KEY, currentCompany.getId());
                startActivity(intent);
            }

            database.getCompanyDao().updateCompany(currentCompany);
        });

        RecyclerView view = findViewById(R.id.filterList);
        view.setLayoutManager(new LinearLayoutManager(this));
        view.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        ArrayList<Company> companies = (ArrayList<Company>) database.getCompanyDao().findAllCompanies();
        adapter.setLocalDataSet(companies);
    }
}