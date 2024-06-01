package com.example.healthtaker

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.healthtaker.ui.theme.Brown
import com.example.healthtaker.ui.theme.HealthTakerTheme
import com.example.healthtaker.ui.theme.Yellow
import com.example.healthtaker.ui.theme.YellowBrown
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import kotlin.concurrent.thread

class LoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if(checkIsLogged(this))
            finish()
        setContent {
            HealthTakerTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    LoginScreen()
                }
            }
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun LoginScreen(loginViewModel: LoginViewModel = LoginViewModel(LocalContext.current as LoginActivity)) {

    val context = LocalContext.current
    val loginState: State by loginViewModel.loginState
    val errorContent: String by loginViewModel.errorContent
    var inputUsername: String by remember { mutableStateOf("") }
    var inputPassword: String by remember { mutableStateOf("") }
    var inputIsCaregiver: Boolean by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Brown)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // 登入表單的卡片
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(containerColor = YellowBrown)
        ) {
            Column(
                modifier = Modifier
                    .background(YellowBrown)
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Sign In",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )

                Spacer(modifier = Modifier.height(8.dp))

                // 輸入框: 用戶名
                OutlinedTextField(
                    value = inputUsername,
                    onValueChange = {inputUsername = it},
                    label = { Text("Account", color = Yellow) },
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
                    onValueChange = {inputPassword = it},
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
                    keyboardActions = KeyboardActions(),
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
                        modifier = Modifier.align(CenterVertically)
                    )
                    Text(
                        text = "is caregiver?",
                        color = Yellow,
                        modifier = Modifier.align(CenterVertically)
                    )
                }

                // 登入狀態
                when(loginState) {
                    State.PENDING -> {
                        // 顯示加載中的指示器
                        CircularProgressIndicator(
                            color = Yellow,
                            modifier = Modifier
                        )
                    }
                    State.SUCCESS -> {
                        if(inputIsCaregiver) {
                            // 導航到選擇被照護者
                            context.startActivity(Intent(context, SelectUserActivity::class.java))
                            (context as Activity).finish()
                        } else {
                            // 導航到主頁面
                            context.startActivity(Intent(context, MainActivity::class.java))
                            (context as Activity).finish()
                        }
                    }
                    State.ERROR -> {
                        // 顯示錯誤訊息
                        Text(errorContent, color = Color.Red)
                    }
                    else -> Unit
                }

                Spacer(modifier = Modifier.height(24.dp))

                // 登入按鈕
                Button(
                    onClick = { loginViewModel.login(inputUsername, inputPassword, inputIsCaregiver) },
                    enabled = inputUsername.isNotBlank(),
                    modifier = Modifier
                        .height(48.dp)
                        .align(Alignment.CenterHorizontally),
                    colors = ButtonDefaults.buttonColors(containerColor = Yellow),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "Sign in",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = YellowBrown,
                        modifier = Modifier
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // 註冊鏈接
                ClickableText(
                    text = AnnotatedString("Create Account"),
                    onClick = {
                        context.startActivity(Intent(context, RegisterActivity::class.java))
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

class LoginViewModel(val context: LoginActivity): ViewModel() {

    // 登入狀態
    val loginState: MutableState<State> = mutableStateOf(State.IDLE)
    // 錯誤訊息
    val errorContent: MutableState<String> = mutableStateOf("")

    // 登入函數
    fun login(username: String, password: String, isCaregiver: Boolean) {
        viewModelScope.launch {
            loginState.value = State.PENDING // 登入中
            thread {
                try {
                    //將資料寫入 JSONObject
                    val loginPosts = JSONObject()
                    loginPosts.put("username", username)
                    loginPosts.put("password", password)
                    loginPosts.put("isCaregiver", isCaregiver)

                    // 使用 webAPI post 登入資料，並取得登入結果
                    val loginReturn = JSONObject(
                        API.post("login", loginPosts.toString())
                    )

                    if (loginReturn.getBoolean("Status")) { // 成功登入
                        if(isCaregiver) {
                            User.caregiverID.value = loginReturn.getString("ID") // 取得 userId
                            getCareRecipients(loginReturn.getString("ID")) // 取得 user 其他資料

                            // 使用 SharedPreferences 儲存登入資料
                            context.runOnUiThread {
                                PreferencesManager(context).
                                    saveDataBoolean("Status", true)
                                PreferencesManager(context).
                                    saveDataString(
                                        "ID",
                                        loginReturn.getString("ID")
                                    )
                                PreferencesManager(context).
                                    saveDataBoolean("isCaregiver", true)
                            }
                            loginState.value = State.SUCCESS
                        } else {
                            User.userID.value = loginReturn.getString("ID")
                            context.runOnUiThread {
                                PreferencesManager(context).saveDataBoolean("Status", true)
                                PreferencesManager(context).saveDataString("ID",
                                    loginReturn.getString("ID"))
                                PreferencesManager(context).saveDataBoolean("isCaregiver", false)
                            }
                            loginState.value = State.SUCCESS
                        }
                    } else {
                        loginState.value = State.ERROR
                        errorContent.value = loginReturn.getString("Content")
                    }
                }  catch (e: Exception) {
                    loginState.value = State.ERROR
                    errorContent.value = "Application unexpected error occurred"
                    Log.i("", e.toString())
                }
            }
        }
    }
}

private fun checkIsLogged(context: Context): Boolean { // 在開啟 app 時調用判斷是否登入過
    if (PreferencesManager(context).getDataBoolean("Status", false)) { // 判斷 SharedPreferences 內是否有資料
        if (PreferencesManager(context).getDataBoolean("isCaregiver", false)) {
            User.userID.value = PreferencesManager(context).getDataString("ID", "")
            thread {
                getCareRecipients(User.userID.value)

                // 跳轉至主畫面
                val intent = Intent(context, SelectUserActivity::class.java)
                context.startActivity(intent)
            }
        } else {
            User.caregiverID.value = PreferencesManager(context).getDataString("ID", "")

            // 跳轉至主畫面
            val intent = Intent(context, MainActivity::class.java)
            context.startActivity(intent)
        }
        return true
    }
    return false
}

// 取得被照護者資料
fun getCareRecipients(ID: String) {
    User.IDs.clear()
    User.names.clear()

    val careRecipient = JSONArray(API.get("getCareRecipient/${ID}")).let { x ->
        Array(x.length()) { x.getJSONObject(it) }.toCollection(ArrayList<JSONObject>())
    }

    for (item in careRecipient) {
        User.IDs.add(item.getString("ID"))
        User.names.add(item.getString("Username"))
    }
}

class PreferencesManager(context: Context) {
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("Health Taker", Context.MODE_PRIVATE)

    fun saveDataString(key: String, value: String) {
        sharedPreferences.edit()
            .putString(key, value)
            .apply()
    }

    fun saveDataBoolean(key: String, value: Boolean) {
        sharedPreferences.edit()
            .putBoolean(key, value)
            .apply()
    }

    fun getDataString(key: String, defaultValue: String): String{
        return sharedPreferences.getString(key, defaultValue) ?: defaultValue
    }


    fun getDataBoolean(key: String, defaultValue: Boolean): Boolean{
        return sharedPreferences.getBoolean(key, defaultValue)
    }
}

enum class State {
    IDLE, PENDING, SUCCESS, ERROR
}

class User {
    companion object {
        val IDs: SnapshotStateList<String> = mutableStateListOf()
        val names: SnapshotStateList<String> = mutableStateListOf()
        val userID: MutableState<String> = mutableStateOf("")
        val username: MutableState<String> = mutableStateOf("")
        val caregiverID: MutableState<String> = mutableStateOf("")
    }
}



@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    HealthTakerTheme {
        LoginScreen()
    }
}