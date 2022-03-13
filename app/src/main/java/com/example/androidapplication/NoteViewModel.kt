package com.example.androidapplication

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class NoteViewModel: ViewModel() {
    val notes: MutableLiveData<List<Note>> by lazy {
        MutableLiveData<List<Note>>().also {
            getAllNotes()
        }
    }
    var db = Firebase.firestore;

    fun addNote(note: Note){
        var newNotes = notes.value?.toMutableList()
        addNoteToFirebase(note.message)
        newNotes?.add(note)
        notes.value = newNotes
    }

    fun getNotes(): LiveData<List<Note>> {
        return notes
    }

    private fun addNoteToFirebase(message: String) {
        db.collection("newsfeed")
            .add(mapOf("message" to message))
    }

    private fun getAllNotes() {
        val notesRef = db.collection("newsfeed")
        notesRef.get().addOnSuccessListener {
            val listOfNews = mutableListOf<Note>()
            for (note in it.documents)
            {
                val message = note.data!!["message"] as String
                val newNote = Note(message)
                listOfNews.add(newNote)
            }
            notes.value = listOfNews
        }
    }

}