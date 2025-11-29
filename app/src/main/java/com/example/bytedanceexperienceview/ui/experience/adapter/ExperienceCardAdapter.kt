package com.example.bytedanceexperienceview.ui.experience.adapter

import ExperienceItem
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.CircleCropTransformation
import com.example.bytedanceexperienceview.R

class ExperienceCardAdapter(
    // 定义点赞点击事件的回调
    private val onLikeClick: (item: ExperienceItem) -> Unit
) : ListAdapter<ExperienceItem, ExperienceCardAdapter.ExperienceCardViewHolder>(
    ExperienceItemDiffCallback()
) {

    // 1. ViewHolder
    inner class ExperienceCardViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val imageView: ImageView = view.findViewById(R.id.experience_image)
        private val titleView: TextView = view.findViewById(R.id.experience_title)
        private val avatarView: ImageView = view.findViewById(R.id.user_avatar)
        private val userNameView: TextView = view.findViewById(R.id.user_name)
        private val likeButton: ImageView = view.findViewById(R.id.like_button)
        private val likesCountView: TextView = view.findViewById(R.id.tv_likes_count)

        init {
            // 设置点赞点击监听器
            likeButton.setOnClickListener {
                if (bindingAdapterPosition != RecyclerView.NO_POSITION) {
                    val item = getItem(bindingAdapterPosition)
                    onLikeClick(item)
                }
            }
        }

        fun bind(item: ExperienceItem) {
            // 使用 Coil 加载主图片，实现图片缓存和预加载
            imageView.load(item.imageUrl) {
                crossfade(true)
                placeholder(R.drawable.ic_launcher_foreground)
            }

            // 使用 Coil 加载头像并裁剪为圆形
            avatarView.load(item.userAvatarUrl) {
                transformations(CircleCropTransformation())
                placeholder(R.drawable.ic_experience_avatar)
            }

            titleView.text = item.title
            userNameView.text = item.userName
            likesCountView.text = item.likesCount.toString()

            // 更新点赞图标状态和颜色
            val likeColor = if (item.isLiked) Color.RED else Color.DKGRAY
            likeButton.setColorFilter(likeColor)
        }
    }

    // 2. DiffUtil
    class ExperienceItemDiffCallback : DiffUtil.ItemCallback<ExperienceItem>() {
        override fun areItemsTheSame(oldItem: ExperienceItem, newItem: ExperienceItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ExperienceItem, newItem: ExperienceItem): Boolean {
            // 仅比较可能变化的内容
            return oldItem.likesCount == newItem.likesCount &&
                    oldItem.isLiked == newItem.isLiked &&
                    oldItem.title == newItem.title
        }
    }

    // 3. Adapter 重载方法
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExperienceCardViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.experience_card,
            parent,
            false
        )
        return ExperienceCardViewHolder(view)
    }

    override fun onBindViewHolder(holder: ExperienceCardViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}
