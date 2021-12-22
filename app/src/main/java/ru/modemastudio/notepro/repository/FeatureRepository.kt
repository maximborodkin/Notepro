package ru.modemastudio.notepro.repository

import kotlinx.coroutines.flow.Flow
import ru.modemastudio.notepro.model.Feature

interface FeatureRepository {
    suspend fun getAll(): Flow<List<Feature>>
    suspend fun getEnabled(): Flow<List<Feature>>
    suspend fun enableFeature(featureId: Long)
    suspend fun disableFeature(featureId: Long)
}