package com.example.koleodemoapp.ui.main


import androidx.lifecycle.ViewModel
import com.example.koleodemoapp.entities.Destination
import com.example.koleodemoapp.repository.IRepository
import io.reactivex.rxjava3.core.Maybe
import javax.inject.Inject

class MainViewModel @Inject constructor(private val repository: IRepository) : ViewModel() {

    fun getDestinationsLists(): Maybe<List<Destination>> {
        return repository.getDestinationsList()
    }
}
