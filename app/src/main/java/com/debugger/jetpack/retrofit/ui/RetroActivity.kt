package com.debugger.jetpack.retrofit.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.debugger.jetpack.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RetroActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_retro)
    }
}