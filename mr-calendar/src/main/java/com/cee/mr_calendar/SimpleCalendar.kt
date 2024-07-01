package com.cee.mr_calendar

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.time.MonthDay
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun SimpleCalendar(modifier: Modifier = Modifier,selectedColor:Color = Color(0xFF98C84F),selectedDate:(String?)->Unit) {
    var currentMonth by remember { mutableStateOf(Calendar.getInstance().get(Calendar.MONTH)) }
    var currentYear by remember { mutableStateOf(Calendar.getInstance().get(Calendar.YEAR)) }
    var currentDay by remember { mutableStateOf(Calendar.getInstance().get(Calendar.DAY_OF_MONTH)) }
    var today by remember { mutableStateOf(Date()) }

    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val dayFormat = SimpleDateFormat("dd", Locale.getDefault())
    val calendar = Calendar.getInstance()
    val daysInMonth = getDaysInMonth(currentMonth, currentYear)
    val daysOfWeekLabels = listOf("S", "M", "T", "W", "T", "F", "S")
    var selectedDay by remember { mutableStateOf<Date?>(null) }
    calendar.set(currentYear, currentMonth, 1)
    val firstDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1

    var daysToShowFromNextMonth = 7 - (daysInMonth.size + firstDayOfWeek) % 7
    if (daysToShowFromNextMonth == 7) {
        daysToShowFromNextMonth = 0
    }

    Column(modifier = modifier
        .wrapContentSize()
        .padding(start = 15.dp, end = 15.dp)) {
        Month(
            monthName = monthName(currentMonth),
            onPreviousMonth = {
                currentMonth--
                if (currentMonth < 0) {
                    currentMonth = 11
                    currentYear--
                }
            },
            onNextMonth = {
                currentMonth++
                if (currentMonth > 11) {
                    currentMonth = 0
                    currentYear++
                }
            }
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 20.dp, top = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            for (label in daysOfWeekLabels) {
                Text(
                    text = label,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp
                )
            }
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            val prevMonthDaysToShow = firstDayOfWeek
            val totalDaysToShow = daysInMonth.size + prevMonthDaysToShow + daysToShowFromNextMonth

            items(totalDaysToShow) { index ->
                if (index < prevMonthDaysToShow) {
                    val prevMonthDay = getDaysInPreviousMonth(currentMonth, currentYear, firstDayOfWeek)[index]
                    DayItem(selectedColor = selectedColor,day = prevMonthDay, dayIsInCurrentMont = false ,isDayNextMont = true)
                } else if (index < daysInMonth.size + prevMonthDaysToShow) {
                    DayItem(selectedColor = selectedColor,day = daysInMonth[index - prevMonthDaysToShow], currentDay = currentDay ,today = today,selectedDay = selectedDay){
                        calendar.set(Calendar.DAY_OF_MONTH,it)
                        selectedDay = calendar.time
                        currentDay = dayFormat.format(calendar.time).toInt()
                        selectedDate(dateFormat.format(calendar.time))
                    }
                } else {
                    val nextMonthDay = index - daysInMonth.size - prevMonthDaysToShow + 1
                    DayItem(selectedColor = selectedColor,day = nextMonthDay, dayIsInCurrentMont = false ,isDayNextMont = true)
                }
            }
        }
    }
}

internal fun getDaysInPreviousMonth(month: Int, year: Int, firstDayOfWeek: Int):List<Int> {
    val calendar = Calendar.getInstance()
    calendar.set(year, month - 1, 1)
    val daysInPreviousMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
    val startDay = daysInPreviousMonth - firstDayOfWeek + 1
    val daysToDisplay = mutableListOf<Int>()
    for (day in startDay..daysInPreviousMonth) {daysToDisplay.add(day)
    }
    return daysToDisplay
}

@Composable
internal fun DayItem(
    selectedColor: Color,
    day: Int = 10,
    selectedDay:Date? = null,
    today:Date? = null,
    currentDay:Int = 0,
    dayIsInCurrentMont:Boolean = true,
    isDayNextMont:Boolean = false,
    daySelected:(Int) -> Unit = {}) {
    val dateFormat = SimpleDateFormat("dd", Locale.getDefault())

    val selected = selectedDay?.let { dateFormat.format(it)}

    var enable by remember { mutableStateOf(true) }
    Box(
        modifier = Modifier
            .wrapContentSize()
            .clip(CircleShape)
            .clickable(enabled = enable) {
                if(!dayIsInCurrentMont){
                    return@clickable
                }

                if (selectedDay != null) {
                    enable = today?.before(selectedDay) == true
                }

                daySelected(day)
            },
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier  = Modifier
                .size(30.dp)
                .clip(CircleShape)
                .background(
                    color = if (day == selected?.toInt() || day == currentDay) selectedColor else Color.Transparent,
                    shape = CircleShape
                )
        ){
            Text(
                text = day.toString(),
                color = if (day == selected?.toInt() || day == currentDay) Color.White else if (isDayNextMont) Color.LightGray else Color.Black,
                fontWeight = FontWeight.W500,
                textAlign = TextAlign.Center,
                fontSize = 20.sp,
                modifier = Modifier
                    .align(Alignment.Center)
            )
        }
    }
}

internal fun getDaysInMonth(month: Int, year: Int): List<Int> {
    val calendar = Calendar.getInstance()
    calendar.set(year, month, 1)
    val daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
    return (1..daysInMonth).toList()
}

internal fun monthName(month: Int): String {
    return SimpleDateFormat("MMMM", Locale.getDefault()).format(
        Calendar.getInstance().apply { set(Calendar.MONTH, month) }.time
    )
}

@Composable
internal fun Month(monthName:String, onPreviousMonth: () -> Unit, onNextMonth: () -> Unit){
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        IconButton(
            modifier = Modifier
                .height(22.dp)
                .width(22.dp),
            colors = IconButtonDefaults.iconButtonColors(containerColor = Color(0xFFFF6C96)),
            onClick = { onPreviousMonth() }
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                contentDescription = "arrow",
                tint = Color.White)
        }

        Text(
            text = monthName,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        IconButton(
            modifier = Modifier
                .height(22.dp)
                .width(22.dp),
            colors = IconButtonDefaults.iconButtonColors(containerColor = Color(0xFFFF6C96)),
            onClick = { onNextMonth() }
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = "arrow",
                tint = Color.White)
        }
    }
}
