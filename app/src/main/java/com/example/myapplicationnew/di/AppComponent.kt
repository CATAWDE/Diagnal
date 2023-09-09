package com.example.myapplicationnew.di

import com.example.myapplicationnew.ui.MainActivity
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class])
interface AppComponent {
    fun inject(activity: MainActivity?)

}