package com.example.healthtaker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.healthtaker.ui.theme.Brown
import com.example.healthtaker.ui.theme.HealthTakerTheme
import com.example.healthtaker.ui.theme.Yellow
import com.example.healthtaker.ui.theme.YellowBrown
import kotlinx.coroutines.launch

class SelectUserActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        try {
            if(User.caregiverID.value != "") {
                val notificationChannel = NotificationChannel(
                    "fall_dataCheck",
                    "fall notification",
                    NotificationManager.IMPORTANCE_HIGH
                )
                val notificationManager =
                    getSystemService(NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.createNotificationChannel(notificationChannel)

                val it = Intent(this, DataCheckService::class.java)
                it.putExtra("ID", User.caregiverID.value)
                startForegroundService(it)
            }
        } catch (e: Exception) {
            Log.i("", e.toString())
        }

        super.onCreate(savedInstanceState)
        setContent {
            HealthTakerTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    SelectUserScreen()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectUserScreen(selectUserViewModel: SelectUserViewModel = viewModel()) {

    val context = LocalContext.current
    val selectUserState: State by selectUserViewModel.selectUserState
    val errorContent: String by selectUserViewModel.errorContent

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Brown)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Care Recipient",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(24.dp))

        LazyColumn(
            modifier = Modifier,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(items = User.names, itemContent = { username ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { selectUserViewModel.selectUser(username) },
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = YellowBrown),
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccountBox,
                            contentDescription = null,
                            tint = Yellow,
                            modifier = Modifier
                                .size(60.dp)
                                .align(CenterVertically)
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Text(
                            text = username,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Yellow,
                            modifier = Modifier.align(CenterVertically)
                        )
                    }
                }
            })
        }

        // 選擇狀態
        when(selectUserState) {
            State.PENDING -> {
                // 顯示加載中的指示器
                CircularProgressIndicator(
                    color = Yellow,
                    modifier = Modifier
                )
            }
            State.SUCCESS -> {
                // 導航到主頁面
                context.startActivity(
                    Intent(context, MainActivity::class.java)
                    .also { it.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP) }
                )
            }
            State.ERROR -> {
                // 顯示錯誤訊息
                Text(errorContent, color = Color.Red)
            }
            else -> Unit
        }
    }
}

class SelectUserViewModel: ViewModel() {
    // 選擇狀態
    val selectUserState: MutableState<State> = mutableStateOf(State.IDLE)
    // 錯誤訊息
    val errorContent: MutableState<String> = mutableStateOf("")

    fun selectUser(name: String) {
        viewModelScope.launch {
            selectUserState.value = State.PENDING   // 載入中
            try {
                val id: String = User.IDs[User.names.indexOf(name)]
                User.userID.value = id
                User.username.value = name
                selectUserState.value = State.SUCCESS
            } catch (e: Exception) {
                selectUserState.value = State.ERROR
                errorContent.value = "Application unexpected error occurred"
                Log.i("", e.toString())
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SelectUserPreview() {
    HealthTakerTheme {
        SelectUserScreen()
    }
}