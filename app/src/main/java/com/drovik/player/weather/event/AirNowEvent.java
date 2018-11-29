package com.drovik.player.weather.event;

import com.drovik.player.weather.IWeatherResponse;

public class AirNowEvent {

    private IWeatherResponse mIWeatherResponse;
    public AirNowEvent(IWeatherResponse iWeatherResponse) {
        this.mIWeatherResponse = iWeatherResponse;
    }

    public IWeatherResponse getIWeatherResponse() {
        return this.mIWeatherResponse;
    }
}
