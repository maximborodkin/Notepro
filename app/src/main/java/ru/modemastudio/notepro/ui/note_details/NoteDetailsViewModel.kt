package ru.modemastudio.notepro.ui.note_details

import android.app.Application
import androidx.lifecycle.*
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ru.modemastudio.notepro.model.Category
import ru.modemastudio.notepro.model.Feature
import ru.modemastudio.notepro.model.Note
import ru.modemastudio.notepro.repository.CategoryRepository
import ru.modemastudio.notepro.repository.FeatureRepository
import ru.modemastudio.notepro.repository.NoteRepository
import javax.inject.Inject

class NoteDetailsViewModel @Inject constructor(
    application: Application,
    private val noteRepository: NoteRepository,
    private val featureRepository: FeatureRepository,
    private val categoryRepository: CategoryRepository,
    private val noteId: Long
) : AndroidViewModel(application) {

    private val _note = MutableLiveData<Note>()
    val note: LiveData<Note> = _note

    val isEditorVisible = MutableLiveData(false)

    init {
        viewModelScope.launch {
            val note = noteRepository.getById(noteId).first()
            note?.let {
                _note.postValue(it)
                isEditorVisible.postValue(it.body.isBlank())
            }
        }
    }

    fun saveNote() = viewModelScope.launch {
        _note.value?.let { noteRepository.save(it) }
    }

    suspend fun getEnabledFeatures() = featureRepository.getEnabled()

    suspend fun getAllCategories() = categoryRepository.getAllCategories()

    class NoteDetailsViewModelFactory @AssistedInject constructor(
        private val application: Application,
        private val noteRepository: NoteRepository,
        private val featureRepository: FeatureRepository,
        private val categoryRepository: CategoryRepository,
        @Assisted("noteId") private val noteId: Long
    ) : ViewModelProvider.AndroidViewModelFactory(application) {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(NoteDetailsViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return NoteDetailsViewModel(
                    application,
                    noteRepository,
                    featureRepository,
                    categoryRepository,
                    noteId
                ) as T
            }
            throw IllegalArgumentException("Inappropriate ViewModel class ${modelClass.simpleName}")
        }

        @AssistedFactory
        interface Factory {
            fun create(@Assisted("noteId") noteId: Long): NoteDetailsViewModelFactory
        }
    }
}