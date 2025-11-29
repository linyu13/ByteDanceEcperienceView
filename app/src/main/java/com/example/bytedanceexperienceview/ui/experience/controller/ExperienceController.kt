package com.example.bytedanceexperienceview.ui.experience.controller

import ExperienceItem
import android.util.Log
import com.example.bytedanceexperienceview.data.datasource.ExperienceDataSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// 业务逻辑与数据管理
class ExperienceController {
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val dataSource = ExperienceDataSource()

    // 数据分页
    private var currentPage = 1
    private val pageSize = 15

    // 存储当前显示的所有数据
    private val dataList = mutableListOf<ExperienceItem>()


    // 初始化
    fun refreshData(onResult: (List<ExperienceItem>) -> Unit) {
        scope.launch {
            currentPage = 1
            val newData = dataSource.getMockData(currentPage, pageSize)

            withContext(Dispatchers.Main) {
                dataList.clear()
                dataList.addAll(newData)
                onResult(dataList.toList())
            }
        }
    }

    // 上拉加载
    fun loadMoreData(onResult: (List<ExperienceItem>) -> Unit) {
        scope.launch {
            currentPage++
            val newData = dataSource.getMockData(currentPage, pageSize)

            withContext(Dispatchers.Main) {
                dataList.addAll(newData)
                onResult(dataList.toList())
            }
        }
    }

    private val TAG = "Controller调试"
    fun toggleLikeStatus(item: ExperienceItem, onUpdate: (List<ExperienceItem>, Int) -> Unit) {
        val index = dataList.indexOfFirst { it.id == item.id }
        Log.d(TAG, "状态切换: 接收ID: ${item.id}, 在 dataList 中找到的索引: $index")
        if (index != -1) {
            val currentItem = dataList[index]

            if (currentItem.isLiked) {
                currentItem.isLiked = false
                currentItem.likesCount = (currentItem.likesCount - 1).coerceAtLeast(0)
            } else {
                currentItem.isLiked = true
                currentItem.likesCount++
            }

            onUpdate(dataList.toList(), index)
        }
    }
}