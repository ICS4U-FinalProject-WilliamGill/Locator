package com.williamgill.com.locator;

/**
 * Created by 610 on 5/15/2015.
 */
public class PersonalLocation {
   private double longitude;
   private double latitude;

    PersonalLocation(double longitude, double latitude){

    this.longitude = longitude;
    this.latitude = latitude;


    }

    PersonalLocation()
    {

    }

    public double getLongitude(){
       return longitude;
    }


    public double getLatitude(){

        return latitude;
    }

    public void setLatitude(double latitude){
        this.latitude = latitude;
    }
    public void setLongitude(double longitude){
        this.longitude = longitude;
    }




}
