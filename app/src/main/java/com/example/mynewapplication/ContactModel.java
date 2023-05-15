package com.example.mynewapplication;
import android.net.Uri;
import android.annotation.SuppressLint;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ContactModel {
    public Uri img;
    public String Username;
    public String number;
    public Date date = new Date();
    @SuppressLint("SimpleDateFormat")
    SimpleDateFormat formatTime;
    String timeStamp;

    public int remove_img;
    public int update_img;


    @SuppressLint("SimpleDateFormat")
    public ContactModel(Uri img,int u_img,int r_img, String number, String name) {
        this.Username = name;
        this.img = img;
        this.number = number;
        this.remove_img=r_img;
        this.update_img=u_img;
        date = new Date();
        formatTime = new SimpleDateFormat("hh:mm aa");
    }

    public void SetTimeStamp(String t) {
        this.timeStamp = t;
    }

    @SuppressLint("SimpleDateFormat")
    public String getCurrentTime() {
        return timeStamp = formatTime.format(date);
    }

}
