package com.example.koleodemoapp.repository

import com.example.koleodemoapp.entities.Destination

interface IRepository {
    fun getDestinationsList() : List<Destination>
}