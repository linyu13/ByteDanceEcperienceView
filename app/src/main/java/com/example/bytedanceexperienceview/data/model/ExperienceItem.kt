package com.example.bytedanceexperienceview.data.model

data class ExperienceItem(
    val id: String, // 唯一 ID
    val imageUrl: String, // 图片URL
    val title: String, // 标题
    val userName: String, // 用户名
    val userAvatarUrl: String, // 用户头像URL
    var likesCount: Int, // 点赞数量
    var isLiked: Boolean // 是否点赞
)