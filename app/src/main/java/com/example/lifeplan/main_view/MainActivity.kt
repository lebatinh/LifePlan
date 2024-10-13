package com.example.lifeplan.main_view

import android.Manifest
import android.annotation.SuppressLint
import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.provider.Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.lifeplan.R
import com.example.lifeplan.custom.item.BottomNavItem
import com.example.lifeplan.ui.theme.LifePlanTheme
import com.example.lifeplan.viewModel.ScheduleViewModel
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            LifePlanTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    val pagerState = rememberPagerState(initialPage = 0, pageCount = { 3 })

                    val scheduleViewModel: ScheduleViewModel = viewModel(
                        factory = ViewModelProvider.AndroidViewModelFactory(
                            LocalContext.current.applicationContext as Application
                        )
                    )
                    Scaffold(
                        bottomBar = {
                            BottomNavigationBar(
                                navController = navController,
                                pagerState = pagerState
                            )
                        },
                        containerColor = MaterialTheme.colorScheme.background,
                        contentColor = MaterialTheme.colorScheme.onBackground
                    ) { paddingValue ->
                        HorizontalPagerScreen(
                            navController = navController,
                            pagerState = pagerState,
                            modifier = Modifier.padding(paddingValue),
                            viewModel = scheduleViewModel,
                            context = LocalContext.current
                        )
                    }
                }
            }
        }

        checkPostNotificationPermission()

        createNotificationChannel()

        checkAndRequestBatteryOptimization()
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            "ALARM_CHANNEL",
            "Alarm Notifications",
            NotificationManager.IMPORTANCE_HIGH
        )
        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(channel)
    }

    private fun checkPostNotificationPermission() {
        // Kiểm tra nếu quyền thông báo chưa được cấp, yêu cầu người dùng cấp quyền
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                1
            )
        }
    }

    @SuppressLint("BatteryLife")
    private fun checkAndRequestBatteryOptimization() {
        val powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager

        // Kiểm tra nếu ứng dụng của bạn đang được tối ưu hóa pin
        val packageName = packageName
        if (!powerManager.isIgnoringBatteryOptimizations(packageName)) {
            // Yêu cầu người dùng tắt tối ưu hóa pin
            val intent = Intent(ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS)
                .setData(Uri.parse("package:$packageName"))
            startActivity(intent)
        } else {
            Toast.makeText(this, "Ứng dụng đã tắt tối ưu hóa pin.", Toast.LENGTH_SHORT).show()
        }
    }
}

@Composable
fun HorizontalPagerScreen(
    navController: NavHostController,
    pagerState: PagerState,
    modifier: Modifier,
    viewModel: ScheduleViewModel,
    context: Context
) {
    HorizontalPager(
        beyondViewportPageCount = 3,
        state = pagerState,
        modifier = modifier
    ) { page ->
        when (page) {
            0 -> HomeScreen(modifier = Modifier)
            1 -> {
                ScheduleScreen(modifier = Modifier, viewModel = viewModel, context = context)
            }

            2 -> ExpenditureScreen(modifier = Modifier)
        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavHostController, pagerState: PagerState) {
    val home = BottomNavItem.Home.apply {
        title = LocalContext.current.getString(R.string.home)
    }
    val schedule = BottomNavItem.Schedule.apply {
        title = LocalContext.current.getString(R.string.schedule)
    }
    val expenditure = BottomNavItem.Expenditure.apply {
        title = LocalContext.current.getString(R.string.expenditure)
    }

    val items = listOf(
        home,
        schedule,
        expenditure
    )

    val coroutineScope = rememberCoroutineScope()
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.secondary,
        contentColor = MaterialTheme.colorScheme.onSecondary
    ) {
        val currentRoute = pagerState.currentPage

        items.forEachIndexed { index, item ->
            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = item.title) },
                label = { Text(item.title, style = MaterialTheme.typography.labelLarge) },
                selected = currentRoute == index,
                onClick = {
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(index)
                    }
                }
            )
        }
    }
}