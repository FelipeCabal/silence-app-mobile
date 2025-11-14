// PostsPagingSource.kt
import androidx.paging.PagingSource
import androidx.paging.PagingState
import kotlinx.coroutines.delay
import com.example.silenceapp.data.Post

class PostsPagingSource : PagingSource<Int, Post>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Post> {
        val page = params.key ?: 0
        val pageSize = params.loadSize.takeIf { it > 0 } ?: 10

        return try {
            // Simular latencia/llamada a API
            delay(800)

            val items = (0 until pageSize).map { i ->
                val id = page * pageSize + i.toLong()
                Post(
                    id = id,
                    author = if (i % 2 == 0) "Natalia" else "Paola",
                    avatarUrl = "https://i.pravatar.cc/150?img=${(id % 70) + 1}",
                    timeAgo = when (i % 5) {
                        0 -> "2m"
                        1 -> "21h"
                        else -> "${(i % 12) + 1}h"
                    },
                    text = if (i % 4 == 0)
                        "Mi gato y yo tenemos un pacto: él no se mete a la bañera y yo tampoco. Él se lame, yo me perfumo. Somos dos almas limpias a nuestra manera 🐈‍⬛✨"
                    else
                        "Beautiful!\n#niceday\n#goodforsoul",
                    imageUrl = if (i % 3 == 0) "https://picsum.photos/seed/${id}/800/400" else null
                )
            }

            LoadResult.Page(
                data = items,
                prevKey = if (page == 0) null else page - 1,
                nextKey = page + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Post>): Int? {
        return state.anchorPosition?.let { anchor ->
            val page = state.closestPageToPosition(anchor)
            page?.prevKey?.plus(1) ?: page?.nextKey?.minus(1)
        }
    }
}
