package com.example.fromscratch;

import com.google.android.gms.common.internal.StringResourceValueReader;

import java.util.Date;

public class MarchandHelperClass {

    private String password;
    private Boolean blockage;
    private int signalement;
    private String telephone;
    private String mail;
    private String adress;
    private String username;
    private double latitude;
    private double longitude;

    public String getAdress() {
        return adress;
    }

    public void setAdress(String adress) {
        this.adress = adress;
    }

    public MarchandHelperClass() {

    }

    public MarchandHelperClass(String password,String adress, Boolean blockage, int signalement, String telephone, String mail, String username, double latitude, double longitude) {
        this.password = password;
        this.blockage = blockage;
        this.signalement = signalement;
        this.telephone = telephone;
        this.mail = mail;
        this.username = username;
        this.latitude = latitude;
        this.longitude = longitude;
        this.adress = adress;
    }
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Boolean getBlockage() {
        return blockage;
    }

    public void setBlockage(Boolean blockage) {
        this.blockage = blockage;
    }

    public int getSignalement() {
        return signalement;
    }

    public void setSignalement(int signalement) {
        this.signalement = signalement;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }
}