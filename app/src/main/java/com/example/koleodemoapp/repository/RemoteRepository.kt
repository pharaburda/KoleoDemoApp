package com.example.koleodemoapp.repository

import android.location.Location
import com.example.koleodemoapp.entities.Destination

class RemoteRepository : IRepository {
    override fun getDestinationsList(): List<Destination> {
        return listOf(
            Destination("Warsaw", Location("provider")),
            Destination("Siedlce", Location("provider")),
            Destination("Crackow", Location("provider")),
            Destination("Gdansk", Location("provider"))
        )
    }
}