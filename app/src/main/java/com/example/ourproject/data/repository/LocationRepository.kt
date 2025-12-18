package com.example.ourproject.data.repository

import com.example.ourproject.data.dao.LocationDao
import com.example.ourproject.data.entity.LocationEntity
import kotlinx.coroutines.flow.Flow

class LocationRepository(private val locationDao: LocationDao) {
    fun getAllLocations(): Flow<List<LocationEntity>> = locationDao.getAllLocations()

    suspend fun getLocationById(id: Long): LocationEntity? = locationDao.getLocationById(id)

    suspend fun getLocationByName(name: String): LocationEntity? = locationDao.getLocationByName(name)

    suspend fun insertLocation(location: LocationEntity): Long = locationDao.insertLocation(location)

    suspend fun insertAllLocations(locations: List<LocationEntity>) = locationDao.insertAllLocations(locations)

    suspend fun updateLocation(location: LocationEntity) = locationDao.updateLocation(location)

    suspend fun deleteLocation(location: LocationEntity) = locationDao.deleteLocation(location)

    suspend fun deleteAllLocations() = locationDao.deleteAllLocations()
}

