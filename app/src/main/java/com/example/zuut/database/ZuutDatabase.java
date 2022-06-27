package com.example.zuut.database;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.zuut.R;

import java.util.concurrent.Executors;

import com.example.zuut.apis.Api;
import com.example.zuut.model.Company;

@Database(entities = {Company.class}, version = 1)
public abstract class ZuutDatabase extends RoomDatabase {

    public abstract com.example.zuut.database.CompanyDao getCompanyDao();

    private static ZuutDatabase zuutDb;

    public static ZuutDatabase getInstance(Context context) {

        if (null == zuutDb) {
            zuutDb = buildDatabaseInstance(context);
        }
        return zuutDb;
    }


    private static ZuutDatabase buildDatabaseInstance(Context context) {
        return Room.databaseBuilder(context, ZuutDatabase.class, "zuut_db")
                .allowMainThreadQueries()
                .addCallback(new Callback() {
                    @Override
                    public void onCreate(@NonNull SupportSQLiteDatabase db) {
                        super.onCreate(db);
                        Executors.newSingleThreadExecutor().execute(() -> {

                            Company[] companies = new Company[]{
                                    new Company("Bird", R.drawable.bird_logo, Api.ApiType.BIRD),
                                    new Company("Voi", R.drawable.voi_logo, Api.ApiType.VOI)
                            };


                            getInstance(context).getCompanyDao().insertCompanies(companies);
                        });
                    }
                })
                .build();

    }
}
