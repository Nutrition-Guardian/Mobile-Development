package com.capstone.project.nutritionguardian.view.main

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.capstone.project.nutritionguardian.R
import com.capstone.project.nutritionguardian.databinding.ActivityMainBinding
import com.capstone.project.nutritionguardian.notification.AlarmReceiver
import com.capstone.project.nutritionguardian.view.ViewModelFactory
import com.capstone.project.nutritionguardian.view.add.AddActivity
import com.capstone.project.nutritionguardian.view.home.HomeFragment
import com.capstone.project.nutritionguardian.view.profile.ProfileFragment
import com.capstone.project.nutritionguardian.view.welcome.WelcomeActivity
import com.google.firebase.auth.FirebaseAuth
import java.util.Calendar

@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {
    private val viewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(this, FirebaseAuth.getInstance())
    }
    private lateinit var binding: ActivityMainBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        replaceFragment(HomeFragment())

        setSupportActionBar(binding.toolbarHome)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        viewModel.getSession().observe(this) { user ->
            if (!user.isLogin) {
                startActivity(Intent(this, WelcomeActivity::class.java))
                finish()
            }
        }

        binding.bottomNavView.setOnItemSelectedListener{
            when(it.itemId) {

                R.id.bottom_home -> replaceFragment(HomeFragment())
                R.id.bottom_profile -> replaceFragment(ProfileFragment())

                else -> {
                }
            }
            true
        }

        binding.fab.setOnClickListener{
            startActivity(Intent(this@MainActivity, AddActivity::class.java))
        }
        setupView()

        setNotification(7, 0)
        setNotification(12, 0)
        setNotification(18, 0)
    }

    private fun setupView() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_container, fragment)
        fragmentTransaction.commit()
    }

    private fun setNotification(hour: Int, minute: Int) {
        val calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
        }

        val intent = Intent(this, AlarmReceiver::class.java)
        intent.putExtra("notification_time", calendar.timeInMillis)

        val pendingIntent = PendingIntent.getBroadcast(
            this,
            (hour * 60 + minute).hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            pendingIntent
        )
    }


}
