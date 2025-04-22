package com.example.persian_date_picker

import android.util.Log
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.core.text.isDigitsOnly
import ir.huri.jcal.JalaliCalendar
import kotlinx.coroutines.launch
import java.time.Year


val blue0 = Color(0xffe5f1ff)
val blue1 = Color(0xff0776f7)
val blue_2F52E0 = Color(0xff2F52E0)
val red1 = Color(0xfffd504f)
val white = Color(0xFFFFFFFF)

@Composable
fun PersianRangeDatePicker(
    onDismiss: (Boolean) -> Unit,
    onClear: () -> Unit,
    setDate: (List<Map<String, String>>) -> Unit,
) {
    val today = JalaliCalendar().day
    val month = JalaliCalendar().month - 1
    val year = JalaliCalendar().year - 1
    var selectedPart by remember {
        mutableStateOf("main")
    }
    var startDate by remember {
        mutableStateOf(listOf(year, month, today))
    }
    var endDate by remember {
        mutableStateOf(listOf(year, month, today))
    }

    var enableButton by remember {
        mutableStateOf(false)
    }

    val width = LocalConfiguration.current.screenWidthDp
    CompositionLocalProvider(
        LocalLayoutDirection provides LayoutDirection.Rtl, LocalDensity provides Density(
            LocalDensity.current.density,
            1f // - we set here default font scale instead of system one
        )
    ) {
        Card(
            modifier = Modifier
                .size(width = width.dp, height = 530.dp),
            shape = RoundedCornerShape(10.dp),
            backgroundColor = MaterialTheme.colors.background
        ) {
            Column(
                Modifier
                    .fillMaxSize()
                    .padding(horizontal = 0.dp),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                MainContent(
                    startDate,
                    endDate,
                    selectedPart,
                    setStartDate = { startDate = it },
                    setEndDate = { endDate = it },
                    setSelected = { selectedPart = it },
                    setEnableButton = {
                        enableButton = it
                    }
                )

                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.End
                ) {

                    Text(
                       
                        text = "حذف انتخاب ها",
                        color = red1,
                        fontSize = 16.sp,
                        modifier = Modifier
                            .clickable {
                                onClear()
                                setDate(
                                    listOf(
                                        mapOf(
                                            "day" to today.toString(),
                                            "month" to month.toString(),
                                            "year" to year.toString()
                                        ),
                                        mapOf(
                                            "day" to today.toString(),
                                            "month" to month.toString(),
                                            "year" to year.toString()
                                        )
                                    )
                                )
                            }
                            .padding(horizontal = 10.dp)
                            .weight(1f)
                            .fillMaxWidth()
                    )
                    Button(
                        onClick = {
                            onDismiss(true)
                            setDate(
                                listOf(
                                    mapOf(
                                        "day" to startDate[2].toString(),
                                        "month" to startDate[1].toString(),
                                        "year" to startDate[0].toString()
                                    ),
                                    mapOf(
                                        "day" to endDate[2].toString(),
                                        "month" to endDate[1].toString(),
                                        "year" to endDate[0].toString()
                                    )
                                )
                            )
                        },
                        colors = androidx.compose.material.ButtonDefaults.buttonColors(
                            Color(0xff2F52E0)
                        ),
                        enabled = enableButton,
                        modifier = Modifier
                            .padding(horizontal = 15.dp)
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .weight(2f)
                            .height(48.dp)
                    ) {
                        Text(
                           
                            text = "تایید",
                            color = white,
                            fontSize = 16.sp,
                        )
                    }


                }
            }
        }
    }
}

@Composable
private fun MainContent(
    startDate: List<Int>,
    endDate: List<Int>,
    selectedPart: String,
    setStartDate: (List<Int>) -> Unit,
    setEndDate: (List<Int>) -> Unit,
    setSelected: (String) -> Unit,
    setEnableButton: (Boolean) -> Unit,
) {


    val width = LocalConfiguration.current.screenWidthDp
    val persianWeekDays =
        listOf("شنبه", "یکشنبه", "دوشنبه", "سه شنبه", "چهارشنبه", "پنجشنبه", "جمعه")
    val monthsList = listOf(
        "فروردین",
        "اردیبهشت",
        "خرداد",
        "تیر",
        "مرداد",
        "شهریور",
        "مهر",
        "آبان",
        "آذر",
        "دی",
        "بهمن",
        "اسفند",
    )
    val weekDay = JalaliCalendar(startDate[0], startDate[1], startDate[2]).dayOfWeek

    var end by remember {
        mutableStateOf(false)
    }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(blue_2F52E0)
                    .padding(vertical = 10.dp, horizontal = 25.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    if (!end) {
                        Text(
                            "انتخاب تاریخ",
                            fontSize = 16.sp,
                            color = MaterialTheme.colors.onPrimary,
                            modifier = Modifier.padding(horizontal = 5.dp)
                        )

                    } else if (startDate == endDate) {
                        Text(
                            
                            text = startDate[2].toString(),
                            fontSize = 16.sp,
                            color = MaterialTheme.colors.onPrimary
                        )
                        Text(
                            text = monthsList[startDate[1]],
                            fontSize = 16.sp,
                            color = MaterialTheme.colors.onPrimary,
                            modifier = Modifier
                                .padding(horizontal = 5.dp)
                        )
                    } else {
                        Text(
                            text = startDate[2].toString(),
                            fontSize = 16.sp,
                            color = MaterialTheme.colors.onPrimary
                        )
                        Text(
                            text = monthsList[startDate[1]],
                            fontSize = 16.sp,
                            color = MaterialTheme.colors.onPrimary,
                            modifier = Modifier
                                .padding(horizontal = 5.dp)
                        )
                        Text(
                           text = "تا",
                            fontSize = 16.sp,
                            color = MaterialTheme.colors.onPrimary,
                            modifier = Modifier.padding(horizontal = 5.dp)
                        )
                        Text(
                            
                            text = endDate[2].toString(),
                            fontSize = 16.sp,
                            color = MaterialTheme.colors.onPrimary
                        )
                        Text(
                            
                            text = monthsList[endDate[1]],
                            fontSize = 16.sp,
                            color = MaterialTheme.colors.onPrimary,
                            modifier = Modifier
                                .padding(horizontal = 5.dp)
                        )
                    }


                }
            }

            Days(
                startDate,
                endDate,
                setStartDate = { setStartDate(it) },
                setEndDay = {
                    end = true
                    setEndDate(it)
                    setEnableButton(true)
                }
            )

        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun Days(
    startDate: List<Int>,
    endDate: List<Int>,
    setStartDate: (List<Int>) -> Unit,
    setEndDay: (List<Int>) -> Unit,
) {

    val monthsList = listOf(
        "فروردین",
        "اردیبهشت",
        "خرداد",
        "تیر",
        "مرداد",
        "شهریور",
        "مهر",
        "آبان",
        "آذر",
        "دی",
        "بهمن",
        "اسفند",
    )
    val weekDays = listOf("شنبه", "یکشنبه", "دوشنبه", "سه شنبه", "چهارشنبه", "پنجشنبه", "جمعه")

    var start by remember {
        mutableStateOf(false)
    }

    var end by remember {
        mutableStateOf(false)
    }

    var today = JalaliCalendar().day
    val thisMonth = JalaliCalendar().month
    val thisYear = JalaliCalendar().year


    val monthRange = getMonthRange(thisMonth, thisYear)

    if (startDate == endDate) {

    }

    var gridState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    LaunchedEffect(key1 = 1) {
        scope.launch {
            gridState.scrollToItem(monthRange.indexOf(MonthYear(thisMonth - 1, thisYear)))
        }
    }

    Column(
        Modifier
            .fillMaxWidth()
            .padding(top = 10.dp)
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(vertical = 2.dp, horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            weekDays.forEach {
                Text(
                   
                    text = it,
                    color = blue_2F52E0,
                    style = MaterialTheme.typography.subtitle2
                )
            }
        }

        //val years = listOf(sYear.toInt() - 1, sYear.toInt(), sYear.toInt() + 1)
        CompositionLocalProvider(LocalOverscrollConfiguration provides null) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(380.dp),
                state = gridState
            ) {
                items(monthRange) { month ->

                    val daysList = daysOfMonth(listOf(month.year, month.month), startDate, endDate)

                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 19.dp, vertical = 0.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.End
                    ) {
                        Text(
                            
                            text = monthsList[month.month],
                            fontSize = 16.sp,
                            color = MaterialTheme.colors.onBackground.copy(.5f)
                        )
                    }
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(7),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(280.dp),
                        contentPadding = PaddingValues(horizontal = 15.dp, vertical = 0.dp),
                        verticalArrangement = Arrangement.Top,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        items(daysList) {
                            Surface(
                                modifier = Modifier
                                    .padding(vertical = 2.dp)
                                    .size(42.dp)
                                    .clip(
                                        decideDayShape(
                                            it,
                                            month,
                                            startDate,
                                            endDate,
                                        )
                                    )
                                    .clickable {

                                        setStartEndDates(
                                            it,
                                            month,
                                            startDate,
                                            endDate,
                                            start,
                                            setStartDate = { v ->
                                                setStartDate(v)
                                            },
                                            setEndDate = { v ->
//                                                if (end) {
//                                                    end = false
//                                                    setStartDate(v)
//
//                                                } else {
                                                setEndDay(v)
//                                                }
                                            },
                                            setEndBool = { v -> end = v },
                                            setStartBool = { v ->
                                                start = v
                                            }
                                        )
                                    },
                                shape = decideDayShape(it, month, startDate, endDate),
                                color =decideDayColor(it, startDate, endDate, end, month,start),
                                border = BorderStroke(
                                    1.dp,
                                    color = if (it == today.toString() && startDate[1] == thisMonth) red1 else Color.Transparent
                                )
                            ) {
                                Row(
                                    Modifier.fillMaxSize(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Text(
                                        text = it,
                                        fontSize = 16.sp,
                                        color = decideDayText(it, startDate, endDate, month,end)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun setStartEndDates(
    day: String,
    monthYear: MonthYear,
    startDate: List<Int>,
    endDate: List<Int>,
    start: Boolean,
    setStartDate: (List<Int>) -> Unit,
    setEndDate: (List<Int>) -> Unit,
    setEndBool: (Boolean) -> Unit,
    setStartBool: (Boolean) -> Unit
) {
    if (day != " ") {
        val newDate = listOf(monthYear.year, monthYear.month, day.toInt())

        if (!start) {
            // کلیک اول: فقط تاریخ شروع تنظیم می‌شه
            setStartDate(newDate)
            setStartBool(true)
            setEndBool(false) // مطمئن می‌شیم که پایان هنوز انتخاب نشده
        } else if (newDate == startDate) {
            // کلیک روی تاریخ شروع: ریست کردن
            setStartBool(false)
            setEndBool(false)
            setStartDate(newDate)
        } else {
            // کلیک دوم یا بعدی: تنظیم تاریخ پایان یا جابه‌جایی
            val newJalali = JalaliCalendar(newDate[0], newDate[1] + 1, newDate[2])
            val startJalali = JalaliCalendar(startDate[0], startDate[1] + 1, startDate[2])

            if (newJalali.toInt() < startJalali.toInt()) {
                // تاریخ جدید قبل از شروع: جابه‌جایی
                setEndDate(startDate)
                setStartDate(newDate)
                setEndBool(true)
            } else {
                // تاریخ جدید بعد یا برابر با شروع: تنظیم به‌عنوان پایان
                setEndDate(newDate)
                setEndBool(true)
            }
        }
    }
}

fun JalaliCalendar.toInt(): Int {

    return (year * 365) + (month * 30) + day
}

private fun calculateFirstDayOffset(date: List<Int>): Int {
    return JalaliCalendar(date[0].toInt(), date[1] + 1, 1).dayOfWeek
}

private fun daysOfMonth(date: List<Int>, startDate: List<Int>, endDate: List<Int>): List<String> {
    var daysList = mutableListOf<String>()

    val weekDay = calculateFirstDayOffset(date)
    if (weekDay != 7) {
        for (i in 1..weekDay) {
            daysList.add(" ")
        }
    }
    var dayCount: Int = 0

    dayCount = if (date[1] < 6) {
        31
    } else {
        30
    }

    for (i in 1..dayCount) {
        daysList.add(i.toString())
    }

    return daysList
}


private fun decideDayColor(
    day: String,
    startDate: List<Int>,
    endDate: List<Int>,
    end: Boolean,
    monthYear: MonthYear,
    start: Boolean
): Color {
    if (day != " ") {
        if (day == startDate[2].toString() && monthYear.month == startDate[1] && monthYear.year == startDate[0]) {
            // روز شروع
            return blue1
        } else if (end && day == endDate[2].toString() && monthYear.month == endDate[1] && monthYear.year == endDate[0]) {
            // روز پایان (فقط وقتی end درست باشه)
            return blue1
        } else if (end && isBetweenStartEnd(day, startDate, endDate, monthYear)) {
            // روزهای بین شروع و پایان (فقط وقتی end درست باشه)
            return blue0
        }
        // روزهای عادی
        return Color.Transparent
    }
    // فضای خالی
    return Color.Transparent
}

@Composable
private fun decideDayText(
    day: String,
    startDate: List<Int>,
    endDate: List<Int>,
    monthYear: MonthYear,
    end: Boolean = false
): Color {
    if (day != " ") {
        val isStart = day == startDate[2].toString() && monthYear.month == startDate[1] && monthYear.year == startDate[0]
        val isEnd = day == endDate[2].toString() && monthYear.month == endDate[1] && monthYear.year == endDate[0]

        if (isStart || (isEnd && end)) {
            // رنگ سفید برای روز شروع یا روز پایان وقتی بازه کامل انتخاب شده
            return MaterialTheme.colors.onPrimary
        } else {
            // رنگ معمولی برای بقیه روزها
            return MaterialTheme.colors.onBackground.copy(.7f)
        }
    }
    return Color.Transparent
}

private fun decideDayShape(
    day: String,
    monthYear: MonthYear,
    startDate: List<Int>,
    endDate: List<Int>
): RoundedCornerShape {

    val cornerRadius = 13.dp
    if (day != " ") {
        if (day.toInt() == startDate[2] && monthYear.month == startDate[1] && monthYear.year == startDate[0]) {
            return RoundedCornerShape(topStart = cornerRadius, bottomStart = cornerRadius)
        } else if (day.toInt() == endDate[2] && monthYear.month == endDate[1] && monthYear.year == endDate[0]) {
            return RoundedCornerShape(topEnd = cornerRadius, bottomEnd = cornerRadius)
        } else if (isBetweenStartEnd(day, startDate, endDate, monthYear)) {
            return RoundedCornerShape(0.dp)
        }
        return RoundedCornerShape(0.dp)
    }
    return RoundedCornerShape(0.dp)
}

private fun isBetweenStartEnd(
    value: String,
    startDate: List<Int>,
    endDate: List<Int>,
    monthYear: MonthYear
): Boolean {
    if (value != " ") {
        if (monthYear.month == startDate[1] && monthYear.year == startDate[0] && monthYear.month == endDate[1] && monthYear.year == endDate[0]) {
            if (value.toInt() > startDate[2] && value.toInt() < endDate[2]) {
                return true
            }
        } else if (monthYear.month == startDate[1]) {
            if (value.toInt() > startDate[2]) {
                return true
            }
        } else if (monthYear.month < endDate[1] && monthYear.month > startDate[1]) {
            return true
        } else if (monthYear.month <= endDate[1] && monthYear.month > startDate[1]) {
            if (value.toInt() < endDate[2]) {
                return true
            }
        }
        return false
    }
    return false
}

data class MonthYear(val month: Int, val year: Int)

fun getMonthRange(thisMonth: Int, thisYear: Int): List<MonthYear> {
    return (-3..1).map { offset ->
        var tempMonth = thisMonth + offset
        var tempYear = thisYear

        if (tempMonth < 0) {
            tempMonth += 12
            tempYear -= 1
        } else if (tempMonth > 11) {
            tempMonth -= 12
            tempYear += 1
        }
        MonthYear(tempMonth, tempYear)
    }
}