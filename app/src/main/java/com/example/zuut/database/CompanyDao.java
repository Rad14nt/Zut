package com.example.zuut.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import com.example.zuut.model.Company;

@Dao
public interface CompanyDao {
    @Query("SELECT * FROM company")
    List<Company> findAllCompanies();

    @Query("SELECT * FROM company where activated == 1")
    List<Company> findAllActivatedCompanies();

    @Query("SELECT * FROM company where id = :companyId")
    Company findById(int companyId);


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertCompanies(Company... companies);

    @Update
    void updateCompany(Company... companies);

}
