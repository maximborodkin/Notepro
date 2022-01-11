package ru.modemastudio.notepro.repository

import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext
import ru.modemastudio.notepro.model.Feature
import ru.modemastudio.notepro.persistence.database.dao.FeatureDao
import javax.inject.Inject

class FeatureRepositoryImpl @Inject constructor(
    private val featureDao: FeatureDao
) : FeatureRepository {

    override suspend fun getAll(): Flow<List<Feature>> {
        return featureDao.getAll()
    }

    override suspend fun getEnabled(): Flow<List<Feature>> {
        if (featureDao.getEnabled().firstOrNull().isNullOrEmpty()) initialFill()
        return featureDao.getEnabled()
    }

    override suspend fun enableFeature(featureId: Long) {
        featureDao.enableFeature(featureId)
    }

    override suspend fun disableFeature(featureId: Long) {
        featureDao.disableFeature(featureId)
    }

    private suspend fun initialFill() = withContext(IO) {
        val features = listOf(
            Feature.H1,
            Feature.H2,
            Feature.H3,
            Feature.H4,
            Feature.H5,
            Feature.H6
        )
        featureDao.insert(features)
    }
}