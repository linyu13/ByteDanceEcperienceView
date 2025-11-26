package com.example.bytedanceexperienceview.data.model

data class ExperienceItem(
    val imageUrl: String,
    val title: String,
    val userName: String,
    val userAvatarUrl: String,
    var likesCount: Int,
    var isLiked: Boolean
)