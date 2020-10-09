package com.example.koleodemoapp.ui.main

import androidx.lifecycle.ViewModel
import com.example.koleodemoapp.entities.Destination
import com.example.koleodemoapp.repository.IRepository
import javax.inject.Inject

class MainViewModel @Inject constructor(private val repository: IRepository) : ViewModel() {
    fun getDestinationsLists(): List<Destination> {
        return repository.getDestinationsList()
    }
}
