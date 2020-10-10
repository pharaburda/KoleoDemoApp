package com.example.koleodemoapp.services

import com.example.koleodemoapp.entities.Destination
import com.example.koleodemoapp.entities.Keyword
import io.reactivex.rxjava3.core.Maybe
import retrofit2.http.GET

interface KoleoService {
    @GET("stations")
    fun getListOfStations(): Maybe<List<Destination>>

    @GET("station_keywords")
    fun getListOfStationKeywords(): Maybe<List<Keyword>>
}