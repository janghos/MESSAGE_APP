package com.jangho.mesagedev

import MainFragment
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
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
    lateinit var sharedPreference : SharedPreferences

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
        sharedPreference = getSharedPreferences("message_pref", 0)

        Handler(Looper.getMainLooper()).postDelayed({
            if (getSaveString("id") != "") {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainer, MainFragment())
                    .commit()
                false

                binding.bottomNavigationView.visibility = View.VISIBLE
            } else {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainer, LoginFragment())
                    .addToBackStack(null) // 백 스택에 추가하여 뒤로가기를 지원
                    .commit()
                false

                binding.bottomNavigationView.visibility = View.GONE
            }
            binding.tvSplashTitle.visibility = View.GONE
            binding.progressbar.visibility = View.GONE
        },2000)




        binding.bottomNavigationView.setOnItemSelectedListener {
                when(it.itemId){
                    R.id.navigation_message -> {
                       replaceFragment(SendMessageFragment(),null)
                        false
                    }
                    R.id.navigation_result -> {
                        val phoneNumber = "tel:02-546-7113"
                        val intent = Intent(Intent.ACTION_DIAL)
                        intent.data = Uri.parse(phoneNumber)
                        startActivity(intent)
                        false
                    }
                R.id.navigation_book -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainer, MainFragment())
                        .commit()

                    false
                }
                R.id.navigation_logout -> {
                    saveString("id", "")
                    saveString("pw", "")
                    replaceFragment(LoginFragment(), null)
                    binding.bottomNavigationView.visibility = View.GONE
                    false
                }
                else -> {


                    false
                }
            }
        }
        val dailyPreferenceManager = DailyPrefManager(this)

        if(dailyPreferenceManager.resetDailyValue() || getSaveString("app_first") == null){
            saveString("app_first", "true")
            saveString("count", "10000")
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

    fun saveString(key : String, value : String){
        sharedPreference.edit().putString(key,value).apply()
    }

    fun getSaveString(key: String) : String? {
        return sharedPreference.getString(key, "").toString()
    }

    fun saveStringList(key: String, valueList: List<String>) {
        val editor: SharedPreferences.Editor = sharedPreference.edit()
        val valueString = valueList.joinToString("\n")
        editor.putString(key, valueString)
        editor.apply()
    }

    fun getStringList(key: String): List<String>? {
        val valueString = sharedPreference.getString(key, null)

        if (valueString == null || valueString == "NODATA") {
            return null
        }
        return valueString.split("\n").map { it.trim() }
    }

    fun bottomNavigation() : View{
        return binding.bottomNavigationView
    }

}