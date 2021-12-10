package ru.modemastudio.notepro.di

import android.content.Context
import androidx.room.Room
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.Reusable
import ru.modemastudio.notepro.persistence.database.NoteproDatabase
import ru.modemastudio.notepro.persistence.database.dao.CategoryDao
import ru.modemastudio.notepro.persistence.database.dao.NoteDao
import ru.modemastudio.notepro.repository.NoteRepository
import ru.modemastudio.notepro.repository.NoteRepositoryImpl
import javax.inject.Singleton

@Module(includes = [DataModuleBinds::class])
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

@Module
interface DataModuleBinds {
    @Binds
    fun bindNoteRepository(noteRepositoryImpl: NoteRepositoryImpl): NoteRepository
}