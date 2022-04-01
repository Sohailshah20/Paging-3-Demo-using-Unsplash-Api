package com.example.paging3_unspalsh.screens

import androidx.lifecycle.ViewModel
import androidx.paging.ExperimentalPagingApi
import com.example.paging3_unspalsh.data.repository.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@ExperimentalPagingApi
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: Repository
) : ViewModel(){

    val getAllImages = repository.getAllImages()

}