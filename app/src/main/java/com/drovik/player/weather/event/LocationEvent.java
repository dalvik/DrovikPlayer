package com.drovik.player.weather.event;

public class LocationEvent {

    private String mCity;

    public LocationEvent(String city) {
        this.mCity = city;
    }

    public String getCity() {
        return mCity;
    }
}
