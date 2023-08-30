package com.example.myapplicationnew.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.example.myapplicationnew.domain.entity.GetMainResponse
import com.example.myapplicationnew.utils.DataUtils

class MediaViewModel() {
    var mediaResponseLiveData = MutableLiveData<GetMainResponse>()

    fun getPageResponseFromPageNumber(pageNo:String,context:Context){
        val response= DataUtils.getPageDataFromJson(pageNo,context)
        mediaResponseLiveData.postValue(response)
    }
}