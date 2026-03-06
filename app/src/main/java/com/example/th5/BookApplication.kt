package com.example.th5

import android.app.Application
import com.example.th5.data.AppContainer
import com.example.th5.data.DefaultAppContainer

class BookApplication : Application() {
    lateinit var container: AppContainer
    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer(this)
    }
}
