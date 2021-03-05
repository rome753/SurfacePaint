package com.example.opengles3camera

import android.app.Application

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        app = this
    }

    companion object {
        var app : App? = null
    }
}