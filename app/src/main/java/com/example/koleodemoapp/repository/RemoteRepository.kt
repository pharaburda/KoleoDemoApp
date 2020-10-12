package com.example.koleodemoapp.repository

import com.example.koleodemoapp.entities.Destination
import com.example.koleodemoapp.services.KoleoService
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.kotlin.zipWith
import timber.log.Timber
import javax.inject.Inject

class RemoteRepository @Inject constructor(private val service: KoleoService) : IRepository {
    override fun getDestinationsList(): Maybe<List<Destination>> {
        return service.getListOfStations()
            .zipWith(service.getListOfStationKeywords()) { stations, keywords ->
                stations.forEach { station ->
                    val matchingKeywords: List<String> =
                        keywords.filter { keyword -> station.id == keyword.station_id }
                            .map { it.keyword }
                    station.apply { this.keywords = matchingKeywords }
                }
                return@zipWith stations
            }
    }

}