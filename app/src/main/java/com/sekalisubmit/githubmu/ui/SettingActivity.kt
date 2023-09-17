package com.sekalisubmit.githubmu.ui

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import com.sekalisubmit.githubmu.R
import com.sekalisubmit.githubmu.databinding.ActivitySettingBinding

class SettingActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val btnHome: ImageButton = findViewById(R.id.btn_home)
        btnHome.setOnClickListener(){
            val moveIntent = Intent(this@SettingActivity, MainActivity::class.java)
            startActivity(moveIntent)
        }


    }
}