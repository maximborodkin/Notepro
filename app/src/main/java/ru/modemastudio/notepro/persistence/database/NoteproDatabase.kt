package ru.modemastudio.notepro.persistence.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ru.modemastudio.notepro.model.Note
import ru.modemastudio.notepro.model.Category
import ru.modemastudio.notepro.model.Feature
import ru.modemastudio.notepro.persistence.database.NoteproDatabase.Companion.databaseVersion
import ru.modemastudio.notepro.persistence.database.converters.*
import ru.modemastudio.notepro.persistence.database.dao.*

@TypeConverters(DateConverter::class, PriorityConverter::class)
@Database(entities = [Note::class, Category::class, Feature::class], version = databaseVersion)
abstract class NoteproDatabase : RoomDatabase(){

    abstract fun noteDao(): NoteDao
    abstract fun categoryDao(): CategoryDao
    abstract fun featureDao(): FeatureDao

    companion object {
        const val databaseVersion = 1
        const val databaseName = "notepro_database"
    }
}