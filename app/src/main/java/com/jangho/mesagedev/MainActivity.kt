package com.jangho.mesagedev

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.google.firebase.database.FirebaseDatabase
import com.jangho.mesagedev.databinding.ActivityMainBinding



class MainActivity : AppCompatActivity() {


    private var doubleBackToExitPressedOnce = false
    private lateinit var binding : ActivityMainBinding
    private var database: FirebaseDatabase ?= null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)
        setContentView(binding.root)
        try {
            database = FirebaseDatabase.getInstance()
        } catch (e: Exception) {
            // Log the exception for debugging
            Log.e("Firebase", "Firebase initialization error", e)
        }

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, LoginFragment())
            .addToBackStack(null) // 백 스택에 추가하여 뒤로가기를 지원
            .commit()
        false

        binding.bottomNavigationView.visibility = View.GONE

        binding.bottomNavigationView.setOnItemSelectedListener {
                when(it.itemId){
                    R.id.navigation_message -> {
                        supportFragmentManager.beginTransaction()
                            .replace(R.id.fragmentContainer, LoginFragment())
                            .addToBackStack(null) // 백 스택에 추가하여 뒤로가기를 지원
                            .commit()
                        false
                    }
                    R.id.navigation_result -> {
                        false
                    }
                R.id.navigation_book -> {
                    false
                }
                R.id.navigation_logout -> {
                    false
                }
                else -> {
                    false
                }

            }
        }




    }
    override fun onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed()
            // 앱 종료 로직 추가
            finish()
            return
        }

        this.doubleBackToExitPressedOnce = true
        Toast.makeText(this, "한 번 더 뒤로가기를\n 누르면 앱이 종료됩니다.", Toast.LENGTH_SHORT).show()

        Handler(Looper.getMainLooper()).postDelayed({
            doubleBackToExitPressedOnce = false
        }, 1000)
    }

    fun replaceFragment(fragment: Fragment, bundle: Bundle?) {
        bundle?.let {
            fragment.arguments = it
        }
       supportFragmentManager.beginTransaction()
       .replace(R.id.fragmentContainer, fragment)
       .addToBackStack(null) // 백 스택에 추가하여 뒤로가기를 지원
       .commit()
    }

    fun visibleNavigation(){
        binding.bottomNavigationView.visibility = View.VISIBLE
    }

    fun hideKeyboard(context: Context) {
        val inputMethodManager = ContextCompat.getSystemService(
            context,
            InputMethodManager::class.java
        ) as InputMethodManager
        inputMethodManager.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)
    }

    fun returnFirebase() : FirebaseDatabase {
        return database!!
    }

}