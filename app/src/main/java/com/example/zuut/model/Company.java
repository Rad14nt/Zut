package com.example.zuut.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.example.zuut.apis.Api;

@Entity(tableName = "company")
public class Company {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String name;
    private int logo;
    private boolean activated = false;
    private String token;
    private String authenticationToken;
    private String access;
    private String refresh;
    private long datetime;
    private Api.ApiType type;

    public Company() {
    }

    public Company(String name, int logo, Api.ApiType type) {
        this.name = name;
        this.logo = logo;
        this.type = type;
    }

    public long getDatetime() {
        return datetime;
    }

    public void setDatetime(long datetime) {
        this.datetime = datetime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getLogo() {
        return logo;
    }

    public void setLogo(int logo) {
        this.logo = logo;
    }

    public boolean isActivated() {
        return activated;
    }

    public void setActivated(boolean activated) {
        this.activated = activated;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getAuthenticationToken() {
        return authenticationToken;
    }

    public void setAuthenticationToken(String authenticationToken) {
        this.authenticationToken = authenticationToken;
    }

    public String getAccess() {
        return access;
    }

    public void setAccess(String access) {
        this.access = access;
    }

    public String getRefresh() {
        return refresh;
    }

    public void setRefresh(String refresh) {
        this.refresh = refresh;
    }

    public Api.ApiType getType() {
        return type;
    }

    public void setType(Api.ApiType type) {
        this.type = type;
    }
}
