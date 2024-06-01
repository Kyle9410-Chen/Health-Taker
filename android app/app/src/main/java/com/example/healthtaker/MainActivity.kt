package com.example.healthtaker

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.concurrent.thread

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HealthTakerTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen()
                }
            }
        }
    }
}

@Composable
fun MainScreen() {

    val context = LocalContext.current
    var screenState by remember { mutableStateOf(MainState.DEFAULT) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Brown)
            .padding(16.dp),
    ) {
        // 上方的行，包含登出按鈕和用戶名
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(45.dp),
            verticalAlignment = CenterVertically
        ) {
            // 登出按鈕
            Button(
                onClick = { logout(context) },
                modifier = Modifier.height(45.dp),
                colors = ButtonDefaults.buttonColors(containerColor = YellowBrown),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "Sign Out",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Yellow
                )
            }

            Spacer(modifier = Modifier.weight(1f)) // 使用 Spacer 推動 username 到右邊

            // 用戶名
            Text(
                text = User.username.value,
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                color = Yellow,
                modifier = Modifier
                    .align(CenterVertically)
            )

            Spacer(modifier = Modifier.width(8.dp)) // 圖標和文字之間的空白

            Icon(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = null,
                tint = Yellow,
                modifier = Modifier
                    .size(45.dp)
                    .align(CenterVertically)
            )
        }

        when(screenState) {
            MainState.DEFAULT -> {
                DefaultScreen { newState -> screenState = newState }
            }
            MainState.BLOOD_PRESSURE -> {
                BloodPressureScreen { newState -> screenState = newState }
            }
            MainState.ADD_BLOOD_PRESSURE -> {
                AddBloodPressureScreen { newState -> screenState = newState }
            }
            MainState.HEART_RATE -> {
                HeartRateScreen { newState -> screenState = newState }
            }
            MainState.OXYGEN -> {
                OxygenScreen { newState -> screenState = newState }
            }
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun DefaultScreen(
    getWeatherViewModel: GetWeatherViewModel = viewModel(),
    getBloodPressureViewModel: GetBloodPressureViewModel = viewModel(),
    getHeartRateViewModel: GetHeartRateViewModel = viewModel(),
    getOxygenViewModel: GetOxygenViewModel = viewModel(),
    onStateChange: (MainState) -> Unit
) {

    val getWeatherErrorContent: String by getWeatherViewModel.errorContent
    val getWeatherState: State by getWeatherViewModel.getWeatherState
    val currentTemperature: String by getWeatherViewModel.getTemperature
    val currentHumidity: String by getWeatherViewModel.getHumidity

    val getBloodPressureErrorContent: String by getBloodPressureViewModel.errorContent
    val getBloodPressureState: State by getBloodPressureViewModel.getBloodPressureState
    val getBloodPressure: List<JSONObject> by getBloodPressureViewModel.getBloodPressure
    var currentBloodPressure = "N/A"
    var bloodPressureUpdate = "N/A"

    val getHeartRateErrorContent: String by getHeartRateViewModel.errorContent
    val getHeartRateState: State by getHeartRateViewModel.getHeartRateState
    val getHeartRate: List<JSONObject> by getHeartRateViewModel.getHeartRate
    var currentHeartRate = "N/A"
    var heartRateUpdate = "N/A"

    val getOxygenErrorContent: String by getOxygenViewModel.errorContent
    val getOxygenState: State by getOxygenViewModel.getOxygenState
    val getOxygen: List<JSONObject> by getOxygenViewModel.getOxygen
    var currentOxygen = "N/A"
    var oxygenUpdate = "N/A"

    var currentDate: String by remember { mutableStateOf("") }
    var currentTime: String by remember { mutableStateOf("") }
    val dateFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd")
    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")

    // 使用 LaunchedEffect 來定期更新時間和日期
    LaunchedEffect(key1 = Unit) {
        while (true) {
            currentDate = LocalDateTime.now()
                .format(dateFormatter)
            currentTime = LocalDateTime.now()
                .format(timeFormatter)
            delay(1000L) // 每秒更新一次
        }
    }

    // 使用 LaunchedEffect 來定期更新溫度和濕度
    LaunchedEffect(key1 = Unit) {
        while (true) {
            getWeatherViewModel.getWeather()
            getBloodPressureViewModel.getBloodPressure()

            getHeartRateViewModel.getHeartRate()
            getOxygenViewModel.getOxygen()
            delay(60000L) // 每分鐘更新一次
        }
    }


    Spacer(modifier = Modifier.height(24.dp))

    Text(
        text = "Care\nRecipient",
        fontSize = 46.sp,
        fontWeight = FontWeight.Bold,
        fontFamily = FontFamily.SansSerif,
        color = Color.White,
        lineHeight = 60.sp
    )

    Spacer(modifier = Modifier.height(24.dp))

    // 主內容
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
    ) {
        // 時間和天氣的卡片
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(containerColor = YellowBrown)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                // 日期
                Text(
                    text = currentDate,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(8.dp))

                // 時間
                Text(
                    text = currentTime,
                    fontSize = 42.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.padding(start = 36.dp)
                )

                Column(modifier = Modifier.align(Alignment.End)) {
                    // 溫度
                    Text(
                        text = "temp: ${currentTemperature}℃",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.align(Alignment.End)
                    )

                    // 濕度
                    Text(
                        text = "humidity: ${currentHumidity}%",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.align(Alignment.End)
                    )
                }
            }
        }

        when(getWeatherState) {
            State.ERROR -> {
                // 顯示錯誤訊息
                Text(getWeatherErrorContent, color = Color.Red)
            }
            else -> Unit
        }

        Spacer(modifier = Modifier.height(8.dp))

        if(getBloodPressureState == State.SUCCESS && getBloodPressure.isNotEmpty()) {
            currentBloodPressure =
                "${getBloodPressure[0].getString("Systolic")}/${getBloodPressure[0].getString("Diastolic")}"
            bloodPressureUpdate = getBloodPressure[0].getString("DateTime")
        }

        // 血壓的卡片
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            onClick = { if(getBloodPressure.isNotEmpty()) onStateChange(MainState.BLOOD_PRESSURE) },
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(containerColor = YellowBrown)
        ) {
            Column(
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Blood Pressure: ",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Yellow
                )

                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = currentBloodPressure,
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(start = 32.dp)
                    )

                    Text(
                        text = "(Update: ${bloodPressureUpdate})",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Yellow,
                        modifier = Modifier.padding(start = 32.dp)
                    )
                }
            }
        }

        when(getBloodPressureState) {
            State.ERROR -> {
                // 顯示錯誤訊息
                Text(getBloodPressureErrorContent, color = Color.Red)
            }
            else -> Unit
        }

        Spacer(modifier = Modifier.height(8.dp))

        if(getHeartRateState == State.SUCCESS && getHeartRate.isNotEmpty()) {
            currentHeartRate = getHeartRate[0].getString("HeartRate")
            heartRateUpdate = getHeartRate[0].getString("DateTime")
        }

        // 心率的卡片
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            onClick = { if(getHeartRate.isNotEmpty()) onStateChange(MainState.HEART_RATE) },
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(containerColor = YellowBrown)
        ) {
            Column(
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = "HeartRate: ",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Yellow
                )

                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = currentHeartRate,
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(start = 32.dp)
                    )

                    Text(
                        text = "(Update: ${heartRateUpdate})",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Yellow,
                        modifier = Modifier.padding(start = 32.dp)
                    )
                }
            }
        }

        when(getHeartRateState) {
            State.ERROR -> {
                // 顯示錯誤訊息
                Text(getHeartRateErrorContent, color = Color.Red)
            }
            else -> Unit
        }

        Spacer(modifier = Modifier.height(8.dp))

        if(getOxygenState == State.SUCCESS && getOxygen.isNotEmpty()) {
            currentOxygen = getOxygen[0].getString("BloodOxygen")
            oxygenUpdate = getOxygen[0].getString("DateTime")
        }

        // 血氧的卡片
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            onClick = { if(getOxygen.isNotEmpty()) onStateChange(MainState.OXYGEN) },
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(containerColor = YellowBrown)
        ) {
            Column(
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = "SpO2: ",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Yellow
                )

                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {

                    Text(
                        text = "${currentOxygen}%",
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(start = 32.dp)
                    )

                    Text(
                        text = "(Update: ${oxygenUpdate})",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Yellow,
                        modifier = Modifier.padding(start = 32.dp)
                    )
                }
            }
        }

        when(getOxygenState) {
            State.ERROR -> {
                // 顯示錯誤訊息
                Text(getOxygenErrorContent, color = Color.Red)
            }
            else -> Unit
        }
    }
}

@Composable
fun BloodPressureScreen(getBloodPressureViewModel: GetBloodPressureViewModel = viewModel(), onStateChange: (MainState) -> Unit) {

    val getBloodErrorContent: String by getBloodPressureViewModel.errorContent
    val getBloodPressureState: State by getBloodPressureViewModel.getBloodPressureState
    val getBloodPressure: List<JSONObject> by getBloodPressureViewModel.getBloodPressure

    getBloodPressureViewModel.getBloodPressure()

    Spacer(modifier = Modifier.height(16.dp))

    Box(modifier = Modifier.clickable { onStateChange(MainState.DEFAULT) }) {
        Text(
            text = "Blood Pressure",
            fontSize = 42.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.SansSerif,
            color = Color.White
        )
    }

    Spacer(modifier = Modifier.height(4.dp))

    // 主內容
    Column(
        modifier = Modifier
            .fillMaxSize(),
    ) {
        Card(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 16.dp),
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(containerColor = YellowBrown)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = null,
                        tint = Yellow,
                        modifier = Modifier
                            .clickable { onStateChange(MainState.DEFAULT) }
                            .size(40.dp)
                            .align(CenterVertically)
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        tint = Yellow,
                        modifier = Modifier
                            .clickable { onStateChange(MainState.ADD_BLOOD_PRESSURE) }
                            .size(40.dp)
                            .align(CenterVertically)
                    )
                }

                when(getBloodPressureState) {
                    State.SUCCESS -> {
                        DefaultLazyColumn(getBloodPressure, "Systolic", "Diastolic", "")
                    }

                    State.ERROR -> {
                        // 顯示錯誤訊息
                        Text(getBloodErrorContent, color = Color.Red)
                    }

                    else -> Unit
                }
            }
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun AddBloodPressureScreen(addBloodPressureViewModel: AddBloodPressureViewModel = viewModel(), onStateChange: (MainState) -> Unit) {

    val addBloodErrorContent: String by addBloodPressureViewModel.errorContent
    val addBloodPressureState: State by addBloodPressureViewModel.addBloodPressureState
    var inputSystolic: String by remember { mutableStateOf("") }
    var inputDiastolic: String by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Brown)
            .padding(16.dp)
            .clickable { onStateChange(MainState.BLOOD_PRESSURE) },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(containerColor = YellowBrown)
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = null,
                tint = Yellow,
                modifier = Modifier
                    .clickable { onStateChange(MainState.BLOOD_PRESSURE) }
                    .size(40.dp)
            )

            Column(
                modifier = Modifier
                    .background(YellowBrown)
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Add BloodPressure",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )

                Spacer(modifier = Modifier.height(8.dp))

                // 輸入框: 收縮壓
                OutlinedTextField(
                    value = inputSystolic,
                    onValueChange = {inputSystolic = it},
                    label = { Text("Systolic", color = Yellow) },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal
                    ),
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

                // 輸入框: 舒張壓
                OutlinedTextField(
                    value = inputDiastolic,
                    onValueChange = {inputDiastolic = it},
                    label = { Text("Diastolic", color = Yellow) },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal
                    ),
                    colors = TextFieldDefaults.textFieldColors(
                        focusedIndicatorColor = Yellow,
                        unfocusedIndicatorColor = Yellow,
                        cursorColor = Yellow,
                        containerColor = YellowBrown,
                        textColor = Color.White
                    ),
                    singleLine = true,
                    keyboardActions = KeyboardActions(),
                    modifier = Modifier.fillMaxWidth()
                )

                // 新增狀態
                when(addBloodPressureState) {
                    State.PENDING -> {
                        // 顯示加載中的指示器
                        CircularProgressIndicator(
                            color = Yellow,
                            modifier = Modifier
                        )
                    }
                    State.SUCCESS -> {
                        onStateChange(MainState.BLOOD_PRESSURE)
                    }
                    State.ERROR -> {
                        // 顯示錯誤訊息
                        Text(addBloodErrorContent, color = Color.Red)
                    }
                    else -> Unit
                }

                Spacer(modifier = Modifier.height(24.dp))

                // 送出按鈕
                Button(
                    onClick = { addBloodPressureViewModel.addBloodPressure(inputSystolic, inputDiastolic) },
                    enabled = inputSystolic.isNotBlank() && inputDiastolic.isNotBlank(),
                    modifier = Modifier
                        .height(48.dp)
                        .align(Alignment.CenterHorizontally),
                    colors = ButtonDefaults.buttonColors(containerColor = Yellow),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "Add",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = YellowBrown,
                        modifier = Modifier
                    )
                }
            }
        }
    }
}

@Composable
fun HeartRateScreen(getHeartRateViewModel: GetHeartRateViewModel = viewModel(), onStateChange: (MainState) -> Unit) {

    val getHeartRateErrorContent: String by getHeartRateViewModel.errorContent
    val getHeartRateState: State by getHeartRateViewModel.getHeartRateState
    val getHeartRate: List<JSONObject> by getHeartRateViewModel.getHeartRate

    getHeartRateViewModel.getHeartRate()

    Spacer(modifier = Modifier.height(16.dp))

    Box(modifier = Modifier.clickable { onStateChange(MainState.DEFAULT) }) {
        Text(
            text = "HeartRate",
            fontSize = 42.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.SansSerif,
            color = Color.White
        )
    }

    Spacer(modifier = Modifier.height(4.dp))

    // 主內容
    Column(
        modifier = Modifier
            .fillMaxSize(),
    ) {
        Card(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 16.dp),
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(containerColor = YellowBrown)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = null,
                        tint = Yellow,
                        modifier = Modifier
                            .clickable { onStateChange(MainState.DEFAULT) }
                            .size(40.dp)
                            .align(CenterVertically)
                    )
                }

                when(getHeartRateState) {
                    State.SUCCESS -> {
                        DefaultLazyColumn(getHeartRate, "HeartRate", "", "")
                    }

                    State.ERROR -> {
                        // 顯示錯誤訊息
                        Text(getHeartRateErrorContent, color = Color.Red)
                    }

                    else -> Unit
                }
            }
        }
    }
}

@Composable
fun OxygenScreen(getOxygenViewModel: GetOxygenViewModel = viewModel(), onStateChange: (MainState) -> Unit) {

    val getOxygenErrorContent: String by getOxygenViewModel.errorContent
    val getOxygenState: State by getOxygenViewModel.getOxygenState
    val getOxygen: List<JSONObject> by getOxygenViewModel.getOxygen

    getOxygenViewModel.getOxygen()

    Spacer(modifier = Modifier.height(16.dp))

    Box(modifier = Modifier.clickable { onStateChange(MainState.DEFAULT) }) {
        Text(
            text = "SpO2",
            fontSize = 42.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.SansSerif,
            color = Color.White
        )
    }

    Spacer(modifier = Modifier.height(4.dp))

    // 主內容
    Column(
        modifier = Modifier
            .fillMaxSize(),
    ) {
        Card(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 16.dp),
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(containerColor = YellowBrown)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = null,
                        tint = Yellow,
                        modifier = Modifier
                            .clickable { onStateChange(MainState.DEFAULT) }
                            .size(40.dp)
                            .align(CenterVertically)
                    )
                }

                when(getOxygenState) {
                    State.SUCCESS -> {
                        DefaultLazyColumn(getOxygen, "BloodOxygen", "", "%")
                    }

                    State.ERROR -> {
                        // 顯示錯誤訊息
                        Text(getOxygenErrorContent, color = Color.Red)
                    }

                    else -> Unit
                }
            }
        }
    }
}

@Composable
fun DefaultLazyColumn(items: List<JSONObject>, firstItemName: String, secondItemName: String, suffix: String) {
    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        items(items = items, itemContent = { item ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = (if(secondItemName == "") item.getString(firstItemName)
                                else "${item.getString(firstItemName)}/${item.getString(secondItemName)}")
                                + suffix,
                        fontSize = 36.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )

                    Text(
                        text = item.getString("DateTime"),
                        fontSize = 18.sp,
                        color = Yellow
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Divider(
                    color = Yellow,
                    thickness = 1.dp,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))
            }
        })
    }
}

class GetWeatherViewModel: ViewModel() {

    // 取得狀態
    val getWeatherState: MutableState<State> = mutableStateOf(State.IDLE)
    // 錯誤訊息
    val errorContent: MutableState<String> = mutableStateOf("")
    // 取得的溫度
    val getTemperature: MutableState<String> = mutableStateOf("N/A")
    // 取得的濕度
    val getHumidity: MutableState<String> = mutableStateOf("N/A")

    fun getWeather() {
        viewModelScope.launch {
            thread {
                try {
                    val temperatureData = JSONArray(API.get("getTemperature/${User.userID.value}")).let{ x ->
                        Array(x.length()) {x.getJSONObject(it)}.toCollection(ArrayList<JSONObject>())
                    }
                    getTemperature.value = if(temperatureData.size == 0) "N/A" else temperatureData[0].getString("Temperature")
                    val humidityData = JSONArray(API.get("getHumidity/${User.userID.value}")).let { x ->
                        Array(x.length()) { x.getJSONObject(it) }.toCollection(ArrayList<JSONObject>())
                    }
                    getHumidity.value = if(humidityData.size == 0) "N/A" else humidityData[0].getString("Humidity")
                    getWeatherState.value = State.SUCCESS
                } catch (e: Exception) {
                    getWeatherState.value = State.ERROR
                    errorContent.value = "Getting weather unexpected error occurred"
                    Log.i("", e.toString())
                }
            }
        }
    }
}

class GetBloodPressureViewModel: ViewModel() {

    // 取得狀態
    val getBloodPressureState: MutableState<State> = mutableStateOf(State.IDLE)
    // 錯誤訊息
    val errorContent: MutableState<String> = mutableStateOf("")
    // 取得的血壓
    val getBloodPressure: MutableState<List<JSONObject>> = mutableStateOf(listOf())

    fun getBloodPressure() {
        viewModelScope.launch {
            thread {
                try {
                    getBloodPressure.value = (
                            JSONArray(API.get(
                                "getBloodPressure/${User.userID.value}"
                            )).let{
                            x -> Array(x.length()) {
                                x.getJSONObject(it)
                            }.toCollection(ArrayList<JSONObject>())
                    })
                    getBloodPressureState.value = State.SUCCESS
                } catch (e: Exception) {
                    getBloodPressureState.value = State.ERROR
                    errorContent.value = "Getting blood pressure unexpected error occurred"
                    Log.i("", e.toString())
                }
            }
        }
    }
}

class GetHeartRateViewModel: ViewModel() {
    // 取得狀態
    val getHeartRateState: MutableState<State> = mutableStateOf(State.IDLE)
    // 錯誤訊息
    val errorContent: MutableState<String> = mutableStateOf("")
    // 取得的心率
    val getHeartRate: MutableState<List<JSONObject>> = mutableStateOf(listOf())

    fun getHeartRate() {
        viewModelScope.launch {
            thread {
                try {
                    getHeartRate.value = (JSONArray(API.get("getHeartRate/${User.userID.value}")).let{x ->
                        Array(x.length()) {x.getJSONObject(it)}.toCollection(ArrayList<JSONObject>())})
                    getHeartRateState.value = State.SUCCESS
                } catch (e: Exception) {
                    getHeartRateState.value = State.ERROR
                    errorContent.value = "Getting heart rate unexpected error occurred"
                    Log.i("", e.toString())
                }
            }
        }
    }
}

class GetOxygenViewModel: ViewModel() {
    // 取得狀態
    val getOxygenState: MutableState<State> = mutableStateOf(State.IDLE)
    // 錯誤訊息
    val errorContent: MutableState<String> = mutableStateOf("")
    // 取得的血氧
    val getOxygen: MutableState<List<JSONObject>> = mutableStateOf(listOf())

    fun getOxygen() {
        viewModelScope.launch {
            thread {
                try {
                    getOxygen.value = (JSONArray(API.get("getBloodOxygen/${User.userID.value}")).let{x ->
                        Array(x.length()) {x.getJSONObject(it)}.toCollection(ArrayList<JSONObject>())})
                    getOxygenState.value = State.SUCCESS
                } catch (e: Exception) {
                    getOxygenState.value = State.ERROR
                    errorContent.value = "Getting SpO2 unexpected error occurred"
                    Log.i("", e.toString())
                }
            }
        }
    }
}

class AddBloodPressureViewModel: ViewModel() {

    // 新增狀態
    val addBloodPressureState: MutableState<State> = mutableStateOf(State.IDLE)
    // 錯誤訊息
    val errorContent: MutableState<String> = mutableStateOf("")

    fun addBloodPressure(systolic: String, diastolic: String) {
        viewModelScope.launch {
            thread {
                try {
                    val bloodPressurePosts = JSONObject()
                    bloodPressurePosts.put("id", User.userID.value)
                    bloodPressurePosts.put("systolic", systolic.toFloat())
                    bloodPressurePosts.put("diastolic", diastolic.toFloat())

                    val addBloodPressureReturn = JSONObject(API.post("addBloodPressure", bloodPressurePosts.toString()))

                    if(addBloodPressureReturn.getBoolean("Status")) {
                        addBloodPressureState.value = State.SUCCESS
                    } else {
                        addBloodPressureState.value = State.ERROR
                        errorContent.value = addBloodPressureReturn.getString("Content")
                    }
                }
                catch (e: Exception) {
                    addBloodPressureState.value = State.ERROR
                    errorContent.value = "Post blood pressure unexpected error occurred"
                    Log.i("", e.toString())
                }
            }
        }
    }
}

private fun logout(context: Context) {
    context.startActivity(
        Intent(context, LoginActivity::class.java)
            .also { it.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP) }
    )

    // 清除使用者保持登入快取
    PreferencesManager(context).saveDataBoolean("Status", false)
    PreferencesManager(context).saveDataString("ID", "")
    PreferencesManager(context).saveDataBoolean("isCaregiver", false)

    // 清除使用者登入資料
    User.IDs.clear()
    User.names.clear()
    User.userID.value = ""
    User.username.value = ""
    User.caregiverID.value = ""

    if(DataCheckService.instance != null)
        DataCheckService.instance?.stopSelf()

    (context as Activity).finishAffinity()
}

enum class MainState {
    DEFAULT, BLOOD_PRESSURE, ADD_BLOOD_PRESSURE, HEART_RATE, OXYGEN
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    HealthTakerTheme {
        MainScreen()
    }
}