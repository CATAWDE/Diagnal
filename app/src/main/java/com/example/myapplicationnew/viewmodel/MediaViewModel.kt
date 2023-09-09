package com.example.myapplicationnew.viewmodel

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplicationnew.domain.entity.GetMainResponse
import com.example.myapplicationnew.utils.DataUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MediaViewModel:ViewModel() {

    var mediaResponseLiveData = MutableLiveData<GetMainResponse?>()

    fun getPageResponseFromPageNumber(pageNo:String,context:Context){
        viewModelScope.launch(Dispatchers.IO) {
            val response = DataUtils.getPageDataFromJson(pageNo, context)
            response?.let {
                mediaResponseLiveData.postValue(it)
            }
        }
    }
}