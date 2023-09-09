package com.example.myapplicationnew.di

import android.app.Application

class MainApplication : Application() {
    var mComponent: AppComponent? = null

    override fun onCreate() {
        super.onCreate()
        mComponent = DaggerAppComponent.builder()
            .appModule(AppModule(this))
            .build()
    }
}