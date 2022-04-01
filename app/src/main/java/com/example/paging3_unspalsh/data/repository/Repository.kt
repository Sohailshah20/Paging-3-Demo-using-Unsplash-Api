package com.example.paging3_unspalsh.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.paging3_unspalsh.data.local.UnsplashDatabase
import com.example.paging3_unspalsh.data.paging.SearchPagingSource
import com.example.paging3_unspalsh.data.paging.UnsplashRemoteMediator
import com.example.paging3_unspalsh.data.remote.UnsplashApi
import com.example.paging3_unspalsh.model.UnsplashImage
import com.example.paging3_unspalsh.util.Constants.ITEMS_PER_PAGE
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@ExperimentalPagingApi
class Repository @Inject constructor(
    private val unsplashApi: UnsplashApi,
    private val unsplashDatabase: UnsplashDatabase
) {

    fun getAllImages(): Flow<PagingData<UnsplashImage>>{
        val pagingSourceFactory = { unsplashDatabase.unsplashImageDao().getAllImages()}
        return Pager(
            config = PagingConfig(pageSize = ITEMS_PER_PAGE),
            remoteMediator = UnsplashRemoteMediator(
                unsplashApi = unsplashApi,
                unsplashDatabase = unsplashDatabase
            ),
            pagingSourceFactory = pagingSourceFactory
        ).flow
    }

    fun searchImages(query: String): Flow<PagingData<UnsplashImage>>{
        return Pager(
            config = PagingConfig(pageSize = ITEMS_PER_PAGE),
            pagingSourceFactory = {
                SearchPagingSource(unsplashApi = unsplashApi, query = query)
            }
        ).flow
    }

}