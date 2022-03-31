package com.example.paging3_unspalsh.data.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.example.paging3_unspalsh.data.local.UnsplashDatabase
import com.example.paging3_unspalsh.data.remote.UnsplashApi
import com.example.paging3_unspalsh.model.UnsplashImage
import com.example.paging3_unspalsh.model.UnsplashRemoteKeys
import com.example.paging3_unspalsh.util.Constants.ITEMS_PER_PAGE
import java.lang.Exception
import javax.inject.Inject

@OptIn(ExperimentalPagingApi::class)
class UnsplashRemoteMediator @Inject constructor(
    private val unsplashApi: UnsplashApi,
    private val unsplashDatabase: UnsplashDatabase
) : RemoteMediator<Int, UnsplashImage>(){

    private val unsplashImageDao = unsplashDatabase.unsplashImageDao()
    private val unsplashRemoteKeyDao = unsplashDatabase.unsplashRemoteKeysDao()



    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, UnsplashImage>,
    ): MediatorResult {

        return try {
            val currentpage = when(loadType){
                LoadType.REFRESH ->{
                    val remoteKeys = getRemoteKeyClosestToCurrentPosition(state)
                    remoteKeys?.nextPage?.minus(1) ?: 1
                }
                LoadType.PREPEND -> {
                    val remoteKeys = getRemoteKeysForFirstItems(state)
                    val prevPage = remoteKeys?.prevPage?:
                    return MediatorResult.Success(
                        endOfPaginationReached = remoteKeys != null
                     )
                    prevPage
                }
                LoadType.APPEND -> {
                    val remoteKeys = getRemoteKeysForLastItems(state)
                    val nextPage = remoteKeys?.nextPage?:
                    return MediatorResult.Success(
                        endOfPaginationReached = remoteKeys != null
                    )
                    nextPage
                }
            }

            val response = unsplashApi.getAllImages(page = currentpage, per_page = ITEMS_PER_PAGE)
            val endOfPaginationReached = response.isEmpty()

            val prevPage = if (currentpage == 1) null else currentpage-1
            val nextPage = if (endOfPaginationReached)  null else currentpage+1

            unsplashDatabase.withTransaction {
                if (loadType == LoadType.REFRESH){
                    unsplashImageDao.deleteAllImages()
                    unsplashRemoteKeyDao.deleteAllRemoteKeys()
                }
                val keys = response.map { unspalshImage ->
                    UnsplashRemoteKeys(
                        id = unspalshImage.id,
                        prevPage = prevPage,
                        nextPage = nextPage
                    )
                }
                unsplashRemoteKeyDao.addAllRemoteKeys(remoteKeys = keys)
                unsplashImageDao.addImages(images = response)


            }
            MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        }catch (e: Exception){
            return MediatorResult.Error(e)
        }
    }
    private suspend fun getRemoteKeyClosestToCurrentPosition(
        state: PagingState<Int, UnsplashImage>
    ): UnsplashRemoteKeys? {
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.id?.let { id ->
                unsplashRemoteKeyDao.getRemoteKey(id = id)
            }
        }
    }

    private suspend fun getRemoteKeysForFirstItems(
        state: PagingState<Int, UnsplashImage>
    ): UnsplashRemoteKeys? {
        return state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()
            ?.let { unsplashImage ->
                unsplashRemoteKeyDao.getRemoteKey(id = unsplashImage.id)
            }
    }

    private suspend fun getRemoteKeysForLastItems(
        state: PagingState<Int, UnsplashImage>
    ): UnsplashRemoteKeys? {
        return state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()
            ?.let { unsplashImage ->
                unsplashRemoteKeyDao.getRemoteKey(id = unsplashImage.id)
            }
    }

}