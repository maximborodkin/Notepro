package ru.modemastudio.notepro.persistence.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import ru.modemastudio.notepro.model.Feature
import ru.modemastudio.notepro.model.Feature.Contract.Columns

@Dao
interface FeatureDao {

    @Query("SELECT * FROM ${Feature.tableName}")
    fun getAll(): Flow<List<Feature>>

    @Query("SELECT * FROM ${Feature.tableName} WHERE ${Columns.isEnabled}=1")
    fun getEnabled(): Flow<List<Feature>>

    @Query("UPDATE ${Feature.tableName} SET ${Columns.isEnabled}=1 WHERE ${Columns.featureId}=:featureId")
    fun enableFeature(featureId: Long)

    @Query("UPDATE ${Feature.tableName} SET ${Columns.isEnabled}=0 WHERE ${Columns.featureId}=:featureId")
    fun disableFeature(featureId: Long)

    @Insert
    suspend fun insert(features: List<Feature>)
}