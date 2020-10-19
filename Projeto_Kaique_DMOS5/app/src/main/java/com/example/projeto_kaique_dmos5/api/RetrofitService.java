package com.example.projeto_kaique_dmos5.api;


import com.example.projeto_kaique_dmos5.model.weatherapi.WeatherResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface RetrofitService {

    @GET("weather?units=metric&lang=pt")
    Call<WeatherResponse>getWeatherConditions(
            @Query("q") String city,
            @Query("appid")  String key);

   // weather?id=524901&appid=2f78457799fa9a9f8713bc8c3ae350f7&lang=sp&units=metric
   //http://api.openweathermap.org/data/2.5/weather?q=London&appid=2f78457799fa9a9f8713bc8c3ae350f7

}
