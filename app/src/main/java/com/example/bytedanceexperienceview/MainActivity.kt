package com.example.bytedanceexperienceview

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.bytedanceexperienceview.ui.experience.adapter.ExperienceCardAdapter
import com.example.bytedanceexperienceview.ui.experience.controller.ExperienceController

class MainActivity : AppCompatActivity() {

    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var recyclerView: RecyclerView
    private lateinit var controller: ExperienceController
    private lateinit var adapter: ExperienceCardAdapter

    private var isLoading = false // 防止上拉重复加载

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 使用 activity_main.xml 布局
        setContentView(R.layout.activity_main)

        controller = ExperienceController()

        // 查找视图
        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout)
        recyclerView = findViewById(R.id.experience_recycler_view)

        // 1. 配置瀑布流布局管理器
        val layoutManager = StaggeredGridLayoutManager(
            2, // 核心：双列
            StaggeredGridLayoutManager.VERTICAL
        )
        // 优化瀑布流闪烁问题，但不推荐在所有情况下都使用 NONE
        layoutManager.gapStrategy = StaggeredGridLayoutManager.GAP_HANDLING_NONE
        recyclerView.layoutManager = layoutManager

        // 2. 初始化 Adapter
        adapter = ExperienceCardAdapter(
            onLikeClick = { item ->
                controller.toggleLikeStatus(item) { updatedList ->
                    // 接收 Controller 更新后的完整列表并提交给 Adapter
                    adapter.submitList(updatedList)
                }
            }
        )
        recyclerView.adapter = adapter

        // 3. 配置下拉刷新和上拉加载
        setupRefreshListener()
        setupLoadMoreListener(layoutManager)

        // 初始加载数据
        loadInitialData()
    }

    private fun loadInitialData() {
        // 首次加载时显示刷新动画
        swipeRefreshLayout.isRefreshing = true
        controller.refreshData { initialData ->
            adapter.submitList(initialData)
            swipeRefreshLayout.isRefreshing = false
        }
    }

    private fun setupRefreshListener() {
        swipeRefreshLayout.setOnRefreshListener {
            controller.refreshData { refreshedData ->
                adapter.submitList(refreshedData)
                swipeRefreshLayout.isRefreshing = false
            }
        }
    }

    private fun setupLoadMoreListener(layoutManager: StaggeredGridLayoutManager) {
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                // dy > 0 表示正在向上滚动（滑向列表底部）
                if (dy <= 0 || isLoading) return

                val lastVisibleItemPositions = layoutManager.findLastVisibleItemPositions(null)
                val totalItemCount = layoutManager.itemCount

                // 检查最后可见的项是否接近列表底部
                val shouldLoad = lastVisibleItemPositions.any { it >= totalItemCount - 5 }

                if (shouldLoad) {
                    isLoading = true
                    controller.loadMoreData { newItems ->
                        // 提交新的完整列表，触发 DiffUtil
                        adapter.submitList(newItems)
                        isLoading = false
                    }
                }
            }
        })
    }
}