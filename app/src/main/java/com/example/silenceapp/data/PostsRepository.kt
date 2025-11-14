// PostsRepository.kt
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import com.example.silenceapp.data.Post

class PostsRepository {
    fun postsStream(pageSize: Int = 10): Flow<PagingData<Post>> {
        return Pager(
            config = PagingConfig(pageSize = pageSize, enablePlaceholders = false),
            pagingSourceFactory = { PostsPagingSource() }
        ).flow
    }
}
