package com.example.myapplicationnew.binder

import android.content.Context
import android.util.TypedValue
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.example.myapplicationnew.R
import com.example.myapplicationnew.utils.AppConstant
import com.example.myapplicationnew.utils.FontUtils


@BindingAdapter("setPosterTitle")
fun setPosterTitle(view: AppCompatTextView,title: String?) {
    FontUtils.setTypeface(view, AppConstant.FONT_TYPE_LIGHT)
    view.setTextSize(TypedValue.COMPLEX_UNIT_PX, 36f)
    view.text = if (!title.isNullOrEmpty()) {
        title
    }else{
        ""
    }
    var arr= title?.split(" ")
    if(arr?.size!! > 2){
        view.startAnimation(
            AnimationUtils.loadAnimation(
                view.context,
                 R.anim.slide_in_right
            ) as Animation
        )

    }
}

@BindingAdapter("setPosterImage")
fun setPosterImage(view: AppCompatImageView,imgName: String?) {
       if (!imgName.isNullOrEmpty()) {
            Glide.with(view.context)
                .load(getImage(view.context,imgName))
                .placeholder(R.drawable.placeholder_for_missing_posters)
                .error(R.drawable.placeholder_for_missing_posters)
                .into(view)
        }else{
            view.setImageResource(R.drawable.placeholder_for_missing_posters)
        }
}

fun getImage(context:Context, imgName: String?): Int {
    val imgNameCustom = imgName?.dropLast(4)
    return context.getResources().getIdentifier(imgNameCustom, "drawable", context.getPackageName())
}

