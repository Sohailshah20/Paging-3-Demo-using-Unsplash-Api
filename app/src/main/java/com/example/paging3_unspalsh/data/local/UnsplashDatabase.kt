package com.example.paging3_unspalsh.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.paging3_unspalsh.data.local.dao.UnsplashImageDao
import com.example.paging3_unspalsh.data.local.dao.UnsplashRemoteKeysDao
import com.example.paging3_unspalsh.model.UnsplashRemoteKeys
import com.example.paging3_unspalsh.model.UnsplashImage

@Database(entities = [UnsplashImage::class, UnsplashRemoteKeys::class], version = 1)
abstract class UnsplashDatabase : RoomDatabase(){

    abstract fun unsplashImageDao(): UnsplashImageDao
    abstract fun unsplashRemoteKeysDao(): UnsplashRemoteKeysDao

}