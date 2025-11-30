package com.example.bytedanceexperienceview

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.bytedanceexperienceview.ui.experience.adapter.ExperienceCardAdapter
import com.example.bytedanceexperienceview.ui.experience.controller.ExperienceController
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {

    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var recyclerView: RecyclerView
    private lateinit var fabToggleLayout: FloatingActionButton


    private lateinit var controller: ExperienceController
    private lateinit var adapter: ExperienceCardAdapter

    // 防止上拉重复加载
    private var isLoading = false

    // 初始化瀑布流默认列数
    private var currentSpanCount = 2
    private lateinit var layoutManager: StaggeredGridLayoutManager // 提升作用域

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 使用 activity_main 布局
        setContentView(R.layout.activity_main)

        controller = ExperienceController()

        // 查找视图
        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout)
        recyclerView = findViewById(R.id.recycler_view)
        fabToggleLayout = findViewById(R.id.fab_toggle_layout)

        // 初始化瀑布流额
        layoutManager = StaggeredGridLayoutManager(
            currentSpanCount,
            StaggeredGridLayoutManager.VERTICAL
        )
        // 优化瀑布流闪烁问题
        layoutManager.gapStrategy = StaggeredGridLayoutManager.GAP_HANDLING_NONE
        recyclerView.layoutManager = layoutManager


        val TAG = "Activity调试"
        adapter = ExperienceCardAdapter(
            onLikeClick = { item ->
                // 传入 item
                controller.toggleLikeStatus(item) { updatedList, position ->
                    // 提交新的列表
                    Log.d(TAG, "回调接收: 收到位置 $position 的更新请求。")
                    adapter.submitList(updatedList) {
                        // 回调完成后，强制刷新 item
                        Log.d(TAG, "回调完成: submitList 已完成。 $position 变化。")
                        adapter.notifyItemChanged(position)
                    }
                }
            }
        )
        recyclerView.adapter = adapter

        setupRefreshListener()
        setupLoadMoreListener(layoutManager)

        // 悬浮按钮点击事件
        fabToggleLayout.setOnClickListener {
            layoutChange()
        }
        // 初始加载数据
        loadInitialData()
    }

    // 切换瀑布流列数
    private fun layoutChange() {
        currentSpanCount = if (currentSpanCount == 2) 1 else 2

        // 重新布局
        layoutManager.spanCount = currentSpanCount

        Log.d("LayoutToggle", "已切换到 $currentSpanCount 列")
    }

    private fun loadInitialData() {
        // 首次加载时显示刷新动画
        swipeRefreshLayout.isRefreshing = true
        controller.refreshData { initialData ->
            adapter.submitList(initialData)
            swipeRefreshLayout.isRefreshing = false
        }
    }

    // 刷新监听
    private fun setupRefreshListener() {
        swipeRefreshLayout.setOnRefreshListener {
            controller.refreshData { refreshedData ->
                adapter.submitList(refreshedData)
                swipeRefreshLayout.isRefreshing = false
            }
        }
    }

    // 滑动监听，计算滑动位置
    private fun setupLoadMoreListener(layoutManager: StaggeredGridLayoutManager) {
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                // dy > 0 表示正在向上滚动
                if (dy <= 0 || isLoading) return

                val lastVisibleItemPositions = layoutManager.findLastVisibleItemPositions(null)
                val totalItemCount = layoutManager.itemCount

                // 检查最后可见的项是否接近列表底部
                val shouldLoad = lastVisibleItemPositions.any { it >= totalItemCount - 5 }

                if (shouldLoad) {
                    isLoading = true
                    controller.loadMoreData { newItems ->
                        // 提交新的完整列表
                        adapter.submitList(newItems)
                        isLoading = false
                    }
                }
            }
        })
    }
}