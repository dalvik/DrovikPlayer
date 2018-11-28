package com.drovik.player.weather.event;

public class LocationHotCityEvent {

    private String mCity;

    public LocationHotCityEvent(String city) {
        this.mCity = city;
    }

    public String getCity() {
        return mCity;
    }
}
