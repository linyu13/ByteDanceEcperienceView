package com.example.bytedanceexperienceview.data.datasource

import ExperienceItem
import kotlinx.coroutines.delay
import kotlin.random.Random

class ExperienceDataSource {

    private val mockImageURL = listOf(
        "https://picsum.photos/id/10/300/350",  // 3:3.5
        "https://picsum.photos/id/12/300/550",  // 3:5.5
        "https://picsum.photos/id/15/300/400",  // 3:4
        "https://picsum.photos/id/20/300/500",  // 3:5
        "https://picsum.photos/id/24/300/450",  // 3:4.5
        "https://picsum.photos/id/27/300/600",  // 3:6
        "https://picsum.photos/id/30/300/380",  // 3:3.8
        "https://picsum.photos/id/33/300/420"   // 3:4.2
    )

    private val mockAvatarURL = "https://picsum.photos/id/64/100/100"

    suspend fun getMockData(page: Int, pageSize: Int): List<ExperienceItem> {
        delay(1000L)
        return List(pageSize) { index ->
            val ID = "page${page}_item${index}"

            ExperienceItem(
                id = ID,
                imageUrl = mockImageURL[Random.nextInt(mockImageURL.size)],
                title = "标题",
                userName = "用户",
                userAvatarUrl = mockAvatarURL,
                likesCount = Random.nextInt(100, 5000),
                isLiked = Random.nextBoolean()
            )

        }

    }
}