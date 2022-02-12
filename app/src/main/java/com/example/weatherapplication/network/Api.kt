package com.example.weatherapplication.network

import com.example.weatherapplication.model.CurrentConditions
import com.example.weatherapplication.model.Forecast
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface Api {
    @GET("weather")
    fun getCurrentConditions(
        @Query("zip") zip: String,
        @Query("units") units: String,
        @Query("appId") appId: String
    ): Call<CurrentConditions>

    @GET("forecast/daily")
    fun getForecast(
        @Query("zip") zip: String,
        @Query("units") units: String,
        @Query("appId") appId: String
    ): Call<Forecast>
}