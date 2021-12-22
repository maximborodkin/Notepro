package ru.modemastudio.notepro.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import ru.modemastudio.notepro.model.Feature
import ru.modemastudio.notepro.persistence.database.dao.FeatureDao
import javax.inject.Inject

class FeatureRepositoryImpl @Inject constructor(
    private val featureDao: FeatureDao
) : FeatureRepository {

    override suspend fun getAll(): Flow<List<Feature>> {
        if (featureDao.getAll().first().isEmpty()) {
            featureDao.insert(Feature.getAllFeatures())
        }
        return featureDao.getAll()
    }

    override suspend fun getEnabled(): Flow<List<Feature>> {
        return featureDao.getEnabled()
    }

    override suspend fun enableFeature(featureId: Long) {
        featureDao.enableFeature(featureId)
    }

    override suspend fun disableFeature(featureId: Long) {
        featureDao.disableFeature(featureId)
    }
}