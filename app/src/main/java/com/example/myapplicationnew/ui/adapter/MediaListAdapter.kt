package com.example.myapplicationnew.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplicationnew.databinding.ItemMediaBinding
import com.example.myapplicationnew.domain.entity.Content


class MediaListAdapter(private var mediaList: ArrayList<Content>):RecyclerView.Adapter<MediaListAdapter.MediaListViewHolder>() {

    inner class MediaListViewHolder(private val itemBinding: ItemMediaBinding): RecyclerView.ViewHolder(itemBinding.root) {
        fun bind(item: Content) {
            itemBinding.apply {
                mediaItem = item
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MediaListViewHolder {
        return MediaListViewHolder(ItemMediaBinding.inflate(
            LayoutInflater.from(parent.context),
            parent, false))
    }

    override fun onBindViewHolder(holder: MediaListViewHolder, position: Int) {
        val item = mediaList[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int = mediaList.size

    fun setData(list: ArrayList<Content>) {
        mediaList=list
        notifyItemRangeChanged(0,list.size-1)
    }

}