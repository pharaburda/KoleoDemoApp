package com.example.koleodemoapp.repository

import com.example.koleodemoapp.entities.Destination
import com.example.koleodemoapp.entities.Keyword
import io.reactivex.rxjava3.core.Maybe

interface IRepository {
    fun getDestinationsList() : Maybe<List<Destination>>
}