package com.example.myapplicationnew.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.myapplicationnew.viewmodel.MediaViewModel
import javax.inject.Inject

class MainViewModelFactory @Inject constructor() :ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MediaViewModel() as T
    }
}