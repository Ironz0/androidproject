package com.example.androidapplication

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class UserViewModel: ViewModel() {
    var username = mutableStateOf("")
    var firstName = mutableStateOf("")
    var lastName = mutableStateOf("")
    private var db = Firebase.firestore;

    fun loginUser( email: String, pw: String ){
        Firebase.auth
            .signInWithEmailAndPassword(email, pw)
            .addOnSuccessListener {
                username.value = email
            }
    }

    fun logoutUser(){
        Firebase.auth.signOut()
        username.value = ""
    }

    fun getUserInfo(uid: String) {
        val docRef = db.document("users/$uid");
        docRef.get().addOnSuccessListener {
            firstName.value = it.data!!["firstname"] as String
            lastName.value = it.data!!["lastname"] as String
            println(it.data!!["firstname"] as String)
            println(it.data!!["lastname"] as String)
        }
    }
}