package com.example.myapplicationnew.domain.entity

import com.google.gson.annotations.SerializedName

data class GetMainResponse(
    val page: Page?
)

data class Page(
    val title: String,
    @SerializedName("total-content-items")
    val total_content_items: String,
    @SerializedName("page-num")
    val page_num: String,
    @SerializedName("page-size")
    val page_size: String,
    @SerializedName("content-items")
    val content_items: ContentItemsResponse
)

data class ContentItemsResponse(
    val content: List<Content> = listOf()
)

data class Content(
    val name: String,
    @SerializedName("poster-image")
    val poster_image: String
)
