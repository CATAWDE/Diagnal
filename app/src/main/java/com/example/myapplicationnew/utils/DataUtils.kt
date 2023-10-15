package com.example.myapplicationnew.utils

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat.getSystemService
import com.example.myapplicationnew.domain.entity.GetMainResponse
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okio.buffer
import okio.source
import java.io.IOException
import java.nio.charset.Charset


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