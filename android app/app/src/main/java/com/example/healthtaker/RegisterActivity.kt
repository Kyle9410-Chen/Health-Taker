package com.example.healthtaker

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.healthtaker.ui.theme.HealthTakerTheme
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.healthtaker.ui.theme.Brown
import com.example.healthtaker.ui.theme.Yellow
import com.example.healthtaker.ui.theme.YellowBrown
import org.json.JSONObject
import kotlin.concurrent.thread


class RegisterActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HealthTakerTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    RegisterScreen()
                }
            }
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun RegisterScreen(registerViewModel: RegisterViewModel = viewModel()) {

    val context = LocalContext.current
    val registerState by registerViewModel.registerState
    val errorContent by registerViewModel.errorContent
    var inputUsername: String by remember { mutableStateOf("") }
    var inputPassword: String by remember { mutableStateOf("") }
    var inputConfirmPassword: String by remember { mutableStateOf("") }
    var inputIdentityNumber: String by remember { mutableStateOf("") }
    var inputIsCaregiver: Boolean by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(containerColor = Yellow)
        ) {
            Column(
                modifier = Modifier
                    .background(YellowBrown)
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Create Account",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )

                Spacer(modifier = Modifier.height(8.dp))

                // 輸入框: 用戶名
                OutlinedTextField(
                    value = inputUsername,
                    onValueChange = { inputUsername = it },
                    label = { Text("Username", color = Yellow) },
                    colors = TextFieldDefaults.textFieldColors(
                        focusedIndicatorColor = Yellow,
                        unfocusedIndicatorColor = Yellow,
                        cursorColor = Yellow,
                        containerColor = YellowBrown,
                        textColor = Color.White
                    ),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                // 輸入框: 身分證號
                OutlinedTextField(
                    value = inputIdentityNumber,
                    onValueChange = { inputIdentityNumber = it },
                    label = { Text("IdentityNumber", color = Yellow) },
                    colors = TextFieldDefaults.textFieldColors(
                        focusedIndicatorColor = Yellow,
                        unfocusedIndicatorColor = Yellow,
                        cursorColor = Yellow,
                        containerColor = YellowBrown,
                        textColor = Color.White
                    ),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                // 輸入框: 密碼
                OutlinedTextField(
                    value = inputPassword,
                    onValueChange = { inputPassword = it },
                    label = { Text("Password", color = Yellow) },
                    colors = TextFieldDefaults.textFieldColors(
                        focusedIndicatorColor = Yellow,
                        unfocusedIndicatorColor = Yellow,
                        cursorColor = Yellow,
                        containerColor = YellowBrown,
                        textColor = Color.White
                    ),
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                // 輸入框：確認密碼
                OutlinedTextField(
                    value = inputConfirmPassword,
                    onValueChange = { inputConfirmPassword = it },
                    label = { Text("Confirm Password", color = Yellow) },
                    colors = TextFieldDefaults.textFieldColors(
                        focusedIndicatorColor = Yellow,
                        unfocusedIndicatorColor = Yellow,
                        cursorColor = Yellow,
                        containerColor = YellowBrown,
                        textColor = Color.White
                    ),
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth()
                )

                // 是否為照護者的勾選框
                Row(
                    modifier = Modifier
                        .clickable(onClick = { inputIsCaregiver = !inputIsCaregiver })
                        .fillMaxWidth()
                ) {
                    Checkbox(
                        checked = inputIsCaregiver,
                        onCheckedChange = { inputIsCaregiver = it },
                        colors = CheckboxDefaults.colors(
                            checkedColor = Yellow,
                            uncheckedColor = Yellow,
                            checkmarkColor = YellowBrown
                        ),
                        modifier = Modifier.align(Alignment.CenterVertically)
                    )
                    Text(
                        text = "is caregiver?",
                        color = Yellow,
                        modifier = Modifier.align(Alignment.CenterVertically)
                    )
                }

                when(registerState) {
                    State.PENDING -> {
                        // 顯示加載中的指示器
                        CircularProgressIndicator(
                            color = Yellow,
                            modifier = Modifier
                        )
                    }
                    State.SUCCESS -> {
                        Text("Registered successful", color = Color.Green)
                        // 導航到主頁面
                        context.startActivity(Intent(context, LoginActivity::class.java))
                        (context as Activity).finish()
                    }
                    State.ERROR -> {
                        // 顯示錯誤訊息
                        Text(errorContent, color = Color.Red)
                    }
                    else -> Unit
                }

                Spacer(modifier = Modifier.height(24.dp))

                // 註冊按鈕
                Button(
                    onClick = {
                        registerViewModel.register(inputUsername, inputPassword, inputConfirmPassword, inputIdentityNumber, inputIsCaregiver)
                    },
                    enabled = inputUsername.isNotBlank() && inputPassword.isNotBlank() && inputConfirmPassword.isNotBlank(),
                    modifier = Modifier
                        .height(48.dp)
                        .align(Alignment.CenterHorizontally),
                    colors = ButtonDefaults.buttonColors(containerColor = Yellow),
                    shape = RoundedCornerShape(15.dp)
                ) {
                    Text(
                        text = "Register",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = YellowBrown,
                        modifier = Modifier
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // 登入連結
                ClickableText(
                    text = AnnotatedString("Already have an account? Sign in"),
                    onClick = {
                        context.startActivity(Intent(context, LoginActivity::class.java))
                        (context as Activity).finish()
                    },
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    style = TextStyle(
                        color = Color.White,
                        textDecoration = TextDecoration.Underline
                    )
                )
            }
        }
    }
}

class RegisterViewModel: ViewModel() {
    
    // 註冊狀態
    val registerState = mutableStateOf(State.IDLE)
    // 錯誤訊息
    val errorContent = mutableStateOf("")

    // 註冊函數
    fun register(username: String, password: String, confirmPassword: String, identityNumber: String, isCaregiver: Boolean) {
        registerState.value = State.PENDING // 註冊中
        viewModelScope.launch {
            thread {
                val registerPosts = JSONObject()
                registerPosts.put("username", username)
                registerPosts.put("password", password)
                registerPosts.put("passwordConfirm", confirmPassword)
                registerPosts.put("identityNumber", identityNumber)
                registerPosts.put("isCaregiver", isCaregiver)

                // 嘗試登入並取得登入結果
                val registerReturn = JSONObject(API.post("create", registerPosts.toString()))

                if (registerReturn.get("Status") as Boolean) {
                    registerState.value = State.SUCCESS
                } else {
                    registerState.value = State.ERROR
                    errorContent.value = registerReturn.get("Content") as String
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RegisterScreenPreview() {
    HealthTakerTheme {
    }
}