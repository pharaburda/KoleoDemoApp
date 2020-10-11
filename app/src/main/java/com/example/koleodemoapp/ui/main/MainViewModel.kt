package com.example.koleodemoapp.ui.main


import android.location.Location
import androidx.lifecycle.ViewModel
import com.example.koleodemoapp.entities.Destination
import com.example.koleodemoapp.repository.IRepository
import io.reactivex.rxjava3.core.Maybe
import timber.log.Timber
import javax.inject.Inject

class MainViewModel @Inject constructor(private val repository: IRepository) : ViewModel() {

    fun getDestinationsLists(): Maybe<List<Destination>> = repository.getDestinationsList()

    fun calculateDistanceBetweenDestinations(
        firstLat: Float?,
        firstLong: Float?,
        secondLat: Float?,
        secondLong: Float?
    ): Int {
        if (firstLat == null || firstLong == null || secondLat == null || secondLong == null) {
            return 0
        } else {
            var results: FloatArray? = FloatArray(10)
            Location.distanceBetween(
                firstLat.toDouble(),
                firstLong.toDouble(),
                secondLat.toDouble(),
                secondLong.toDouble(),
                results
            )
            return if (results == null) {
                0
            } else {
                (results.first() / 1000).toInt()
            }
        }
    }
}
