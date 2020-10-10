package com.example.koleodemoapp.repository

import com.example.koleodemoapp.entities.Destination
import com.example.koleodemoapp.services.KoleoService
import io.reactivex.rxjava3.core.Maybe
import javax.inject.Inject

class RemoteRepository @Inject constructor(private val service: KoleoService) : IRepository {
    override fun getDestinationsList(): Maybe<List<Destination>> {
        return service.getListOfStations()
    }
}