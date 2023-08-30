package com.example.myapplicationnew.utils

import android.content.Context
import android.graphics.Typeface
import android.widget.ActionMenuView
import android.widget.TextView
import com.example.myapplicationnew.domain.entity.GetMainResponse
import com.example.myapplicationnew.domain.entity.MediaItem
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okio.buffer
import okio.source
import java.io.IOException
import java.lang.reflect.Type
import java.nio.charset.Charset
import android.util.Log

class DataUtils {
    companion object {
        private var dataUtils: DataUtils? = null

        init {
            if (dataUtils == null) dataUtils = DataUtils()
        }

        fun getPageDataFromJson(page: String, context: Context): GetMainResponse? {
            var response:GetMainResponse? =null
            val filePath=
                when(page){
                    "1"->{ "CONTENTLISTINGPAGE-PAGE1.json"}
                    "2" ->{"CONTENTLISTINGPAGE-PAGE2.json"}
                    "3" ->{"CONTENTLISTINGPAGE-PAGE3.json"}
                    else -> {"CONTENTLISTINGPAGE-PAGE1.json"}
                }
            val data= readJsonFromAssets(filePath,context)
            data?.let { string ->
                 response= Gson().fromJson(string, object: TypeToken<GetMainResponse>(){}.type)
                Log.d("***" ,"" + response?.page?.content_items?.content?.size)
            }
            return response
        }

        fun readJsonFromAssets(filePath: String,context: Context): String? {
            try {
                val source = context.assets?.open(filePath)?.source()?.buffer()
                return source?.readByteString()?.string(Charset.forName("utf-8"))

            } catch (e: IOException) {
                e.printStackTrace()
            }
            return null
        }

    }
}