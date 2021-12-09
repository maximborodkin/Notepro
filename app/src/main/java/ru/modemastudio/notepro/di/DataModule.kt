package ru.modemastudio.notepro.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.Reusable
import ru.modemastudio.notepro.persistence.database.NoteproDatabase
import ru.modemastudio.notepro.persistence.database.dao.CategoryDao
import ru.modemastudio.notepro.persistence.database.dao.NoteDao
import javax.inject.Singleton

@Module
object DataModule {

    @Singleton
    @Provides
    fun provideDatabase(applicationContext: Context): NoteproDatabase {
        return Room.databaseBuilder(
            applicationContext,
            NoteproDatabase::class.java,
            NoteproDatabase.databaseName
        ).build()
    }

    @Reusable
    @Provides
    fun provideNoteDao(noteproDatabase: NoteproDatabase): NoteDao {
        return noteproDatabase.noteDao()
    }

    @Reusable
    @Provides
    fun provideCategoryDao(noteproDatabase: NoteproDatabase): CategoryDao {
        return noteproDatabase.categoryDao()
    }
}