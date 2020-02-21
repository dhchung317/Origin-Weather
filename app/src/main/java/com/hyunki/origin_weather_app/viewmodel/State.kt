package com.hyunki.origin_weather_app.viewmodel

import com.hyunki.origin_weather_app.model.City
import com.hyunki.origin_weather_app.model.Forecast

sealed class State {

    object Loading : State()

    data class Success(
            val any : Any
    ) : State()

    object Error : State()
}