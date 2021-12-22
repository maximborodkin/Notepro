package ru.modemastudio.notepro.ui.note_details

import android.app.Application
import androidx.annotation.StringRes
import androidx.lifecycle.*
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import ru.modemastudio.notepro.R
import ru.modemastudio.notepro.model.Feature
import ru.modemastudio.notepro.model.Note
import ru.modemastudio.notepro.repository.FeatureRepository
import ru.modemastudio.notepro.repository.NoteRepository
//import ru.modemastudio.notepro.ui.note_details.NoteDetailsViewModel.NoteDetailsState.*
import javax.inject.Inject

class NoteDetailsViewModel @Inject constructor(
    application: Application,
    private val noteRepository: NoteRepository,
    private val featureRepository: FeatureRepository,
    private val noteId: Long
) : AndroidViewModel(application) {

    private val _note = MutableLiveData<Note>()
    val note: LiveData<Note> = _note

    private val _features = MutableLiveData<List<Feature>>()
    val features: LiveData<List<Feature>> = _features

    val isEditorVisible = MutableLiveData(false)

    init {
        viewModelScope.launch {
            _note.postValue(noteRepository.getById(noteId))
            _features.postValue(featureRepository.getEnabled().first())
        }
    }

    fun save() {
        viewModelScope.launch {
            _note.value?.let { noteRepository.save(it) }
        }
    }

    class NoteDetailsViewModelFactory @AssistedInject constructor(
        private val application: Application,
        private val noteRepository: NoteRepository,
        private val featureRepository: FeatureRepository,
        @Assisted("noteId") private val noteId: Long
    ) : ViewModelProvider.AndroidViewModelFactory(application) {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(NoteDetailsViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return NoteDetailsViewModel(application, noteRepository, featureRepository, noteId) as T
            }
            throw IllegalArgumentException("Inappropriate ViewModel class ${modelClass.simpleName}")
        }

        @AssistedFactory
        interface Factory {
            fun create(@Assisted("noteId") noteId: Long): NoteDetailsViewModelFactory
        }
    }
}