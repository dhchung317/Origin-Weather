package com.hyunki.origin_weather_app.viewmodel

import com.hyunki.origin_weather_app.model.City
import com.hyunki.origin_weather_app.model.Forecast

sealed class State {

    object Loading : State()

//    data class Success(
//            val any : Any
//    ) : State()

    object Error : State()

    sealed class Success : State() {

        data class OnCitiesLoaded(
                val cities: ArrayList<City>
        ) : Success()

        data class OnForecastsLoaded(
                val forecasts: ArrayList<Forecast>
        ) : Success()

        data class OnCityByIdLoaded(
                val city: City
        ) : Success()

        data class OnForecastsByIdLoaded(
                val forecasts: ArrayList<Forecast>
        ) : Success()

        data class OnDefaultLocationLoaded(
                val cityString: String
        ) : Success()
    }
}
