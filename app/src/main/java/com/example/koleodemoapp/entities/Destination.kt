package com.example.koleodemoapp.entities

class Destination (
    var id: Int?,
    var name: String?,
    var name_slug: String?,
    var latitude: Float?,
    var longitude: Float?,
    var hits: Int?,
    var ibnr: Int?,
    var city: String?,
    var region: String?,
    var country: String?,
    var localised_name: String?,
    var keywords: List<String>? = null
) {
    override fun toString(): String {
        return name ?: ""
    }
}