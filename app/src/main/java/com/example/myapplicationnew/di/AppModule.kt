package com.example.myapplicationnew.di

import android.app.Application
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
internal class AppModule(mApplication: Application) {
    private val mApplication: Application

    init {
        this.mApplication = mApplication
    }

    @Provides
    @Singleton
    fun provideApplication(): Application {
        return mApplication
    }
}