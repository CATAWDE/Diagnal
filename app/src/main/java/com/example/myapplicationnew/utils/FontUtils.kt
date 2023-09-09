package com.example.myapplicationnew.utils

import android.content.Context
import android.graphics.Typeface
import android.widget.TextView

class FontUtils {
    companion object {
        private var fontUtils: FontUtils? = null
        private var customTypeface: Typeface? = null

        init {
            if (fontUtils == null) fontUtils = FontUtils()
        }

        fun setTypeface(view: TextView, fontString: String) {
            if (customTypeface != null) customTypeface = null
            customTypeface = when (fontString) {
                AppConstant.FONT_TYPE_SEMIBOLD -> {
                    Typeface.createFromAsset(view.context.assets, "fonts/TitilliumWeb-SemiBold.ttf")
                }
                AppConstant.FONT_TYPE_LIGHT ->{
                    Typeface.createFromAsset(view.context.assets, "fonts/TitilliumWeb-Light.ttf")
                }
                else -> {
                    Typeface.createFromAsset(view.context.assets, "fonts/TitilliumWeb-Black.ttf")
                }
            }
            view.setTypeface(customTypeface)
        }

        fun convertPxToDp(px: Int, context: Context): Int {
            return (px / (context.resources.displayMetrics.density)).toInt()
        }

    }
}