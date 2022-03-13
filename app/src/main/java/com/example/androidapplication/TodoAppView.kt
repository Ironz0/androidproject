package com.example.androidapplication

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.VerticalAlignmentLine
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


const val HOME_ROUTE = "home"
const val NOTE_ROUTE = "note"

@Composable
fun MainView() {
    val userVM = viewModel<UserViewModel>()

    Firebase.auth.addAuthStateListener {
        if (it.currentUser != null) {
            userVM.username.value = it.currentUser?.email.toString()
            userVM.getUserInfo(it.currentUser!!.uid);
        } else {
            userVM.username.value = ""
        }
    }

    if(userVM.username.value.isEmpty()){
        LoginView(userVM)
    }else {
        MainScaffoldView(userVM)
    }
}

@Composable
fun MainScaffoldView(userVM: UserViewModel) {

    val navController = rememberNavController()

    Scaffold(
        topBar = { TopBarView() },
        bottomBar = { BottomBarView(navController) },
        content = { MainContentView(navController, userVM) }
    )
}

@Composable
fun MainContentView(navController: NavHostController, userVM: UserViewModel) {
    val noteVM = viewModel<NoteViewModel>()

    NavHost(navController = navController, startDestination = HOME_ROUTE ){
        composable( route = HOME_ROUTE ){ HomeView(userVM) }
        composable( route = NOTE_ROUTE){ NoteView(noteVM) }
    }
}

@Composable
fun HomeView(userVM: UserViewModel) {

    Column(modifier = Modifier
        .fillMaxSize()
        .background(Color(0xFFBCECEC)),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center) {
        Text("Welcome")
        Text("${userVM.firstName.value}")
        Text("${userVM.lastName.value}")
    }
}

@Composable
fun NoteView(noteVM: NoteViewModel) {

    var noteText by remember { mutableStateOf("") }

    Column(modifier = Modifier
        .fillMaxSize()
        .background(Color(0xFFBCECEC))
        .padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        OutlinedTextField(
            value = noteText ,
            onValueChange = { noteText = it },
            label = { Text(text = "News") })

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedButton(
            onClick = { noteVM.addNote( Note(noteText) ) }
        ) {
            Text(text = "Add News")
        }

        Spacer(modifier = Modifier.height(10.dp))

        noteVM.notes.value?.forEach {
            Divider(thickness = 2.dp)
            Text(text = it.message)
        }
        Divider(thickness = 2.dp)
    }
}

@Composable
fun BottomBarView(navController: NavHostController) {
    Row(modifier = Modifier
        .fillMaxWidth()
        .background(Color(0xFF4963E6)),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ){
        Icon(
            painter = painterResource(id = R.drawable.ic_home),
            contentDescription = "home",
            modifier = Modifier.clickable {  navController.navigate(HOME_ROUTE)  })
        Icon(
            painter = painterResource(id = R.drawable.ic_note),
            contentDescription = "note",
            modifier = Modifier.clickable {  navController.navigate(NOTE_ROUTE)  })
    }
}

@Composable
fun TopBarView() {
    val userVM = viewModel<UserViewModel>()

    Row(modifier = Modifier
        .fillMaxWidth()
        .background(Color(0xFF4963E6))
        .padding(10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = userVM.username.value)
        OutlinedButton(onClick = { userVM.logoutUser() }) {
            Text(text = "Log out")
        }
    }
}


@Composable
fun LoginView(userVM: UserViewModel) {
    var email by remember { mutableStateOf("") }
    var pw by remember { mutableStateOf("") }
    
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            value = email , 
            onValueChange = { email = it },
            label = { Text(text = "Email") })
        OutlinedTextField(
            value = pw ,
            onValueChange = { pw = it },
            label = { Text(text = "Password") },
            visualTransformation = PasswordVisualTransformation())
        OutlinedButton(onClick = { userVM.loginUser(email,pw) }) {
            Text(text = "Login")
        }
    }
}