package com.example.chatter.feature.auth.signup

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.chatter.R
import com.example.chatter.feature.auth.signin.SignInState

@Composable
fun SignUpScreen(navController: NavController){

    val viewModel :SignUpViewModel = hiltViewModel()
    val uiState = viewModel.state.collectAsState()
    var email by remember {
        mutableStateOf("")
    }
    var name by remember {
        mutableStateOf("")
    }
    var confirm by remember {
        mutableStateOf("")
    }
    var password by remember {
        mutableStateOf("")
    }
    val context = LocalContext.current

    LaunchedEffect(key1 = uiState.value) {
        when(uiState.value) {
            is SignUpState.Success -> {
                navController.navigate("home")
            }
            is SignUpState.Error -> {
                Toast.makeText(context, "Password is not matching Confirm Password!", Toast.LENGTH_SHORT).show()
            }
            else -> {}
        }
    }



    Scaffold(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier
            .padding(it)
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Image(painter = painterResource(id = R.drawable.chatlogo), contentDescription =null,
                modifier = Modifier
                    .size(120.dp)
                    .background(Color.White)
            )

            Spacer(modifier = Modifier.size(16.dp))

            OutlinedTextField(value = name, onValueChange ={ name=it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                //       placeholder = { Text(text = "Email")},
                label = { Text(text = "Full Name") }
            )

            Spacer(modifier = Modifier.size(16.dp))

            OutlinedTextField(value = email, onValueChange ={ email=it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                //       placeholder = { Text(text = "Email")},
                label = { Text(text = "Email") }
            )
            Spacer(modifier = Modifier.size(16.dp))
            OutlinedTextField(value = password, onValueChange ={ password=it } ,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                //    placeholder = { Text(text = "Password")},
                label = { Text(text = "Password") },
                visualTransformation = PasswordVisualTransformation()
            )
            Spacer(modifier = Modifier.size(16.dp))
            OutlinedTextField(value = confirm, onValueChange ={ confirm=it } ,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                //    placeholder = { Text(text = "Password")},
                label = { Text(text = "Confirm Password") },
                isError = password.isNotEmpty()&&confirm.isNotEmpty()&&password!=confirm,
                visualTransformation = PasswordVisualTransformation()
            )

            Spacer(modifier = Modifier.size(16.dp))

            if (uiState.value == SignUpState.Loading) {
                CircularProgressIndicator()
            }
            else{
                Button(onClick = {
                    viewModel.signUp(name,email,password)
                }
                    ,enabled = password.isNotEmpty()&&confirm.isNotEmpty()&&password==confirm) {
                    Text(text = "Sign Up")

                }

                TextButton(onClick = { viewModel.signUp(name,email, password) }) {
                    Text(text = "Already have an account? Sign In")

                }
            }


        }

    }

}

@Preview(showBackground=true)
@Composable
fun SignUpScreenPreview(){
    SignUpScreen(navController = rememberNavController())
}