package com.theyellowmirror.sightseer;



public class Loc_Rating {
    private String locationName;
    private long xCoord;
    private long yCoord;
    private Rating rating;
    Loc_Rating(){
        locationName = "";
        xCoord = 0;
        yCoord = 0;
        rating = Rating.NO_RATING;
    }

    Loc_Rating(String LocName, long x, long y, Rating rate){
        locationName = LocName;
        xCoord = x;
        yCoord = y;
        rating = rate;
    }

    public long getXCoord(){
        return xCoord;
    }

    public long getYCoord(){
        return yCoord;
    }
    public String getLocationName(){
        return locationName;
    }
    public Rating getRating(){
        return rating;
    }

    public void setLocationName(String name){
        locationName = name;
    }
    public void setxCoord(long x){
        xCoord = x;
    }
    public void setYCoord(long y){
        yCoord = y;
    }
    public void setRating(Rating rate){
        rating = rate;
    }
    public String toString(){
        String rate = "Name" + locationName + "\nRating:" + rating;
        return rate;
    }
}
