package com.cee.mr_compose_calendar

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.cee.mr_calendar.SimpleCalendar
import com.cee.mr_compose_calendar.ui.theme.MrcalendarTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MrcalendarTheme {
                CustomCalendar(
                    modifier = Modifier.padding()
                )
            }
        }
    }
}

@Composable
fun CustomCalendar(modifier: Modifier = Modifier) {
    Column(modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center) {
        Text(text = "Testing")
        SimpleCalendar(){
            Log.d("sdfsdfsdfsdfsdfsd", "CustomCalendar: $it")
        }
    }
    
}
