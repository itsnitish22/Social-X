package com.example.socialx.news

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.socialx.api.RetrofitInstance
import com.example.socialx.api.models.News
import kotlinx.coroutines.launch

class NewsViewModel : ViewModel() {
    //to update the progress dialog
    private val _receivedNews: MutableLiveData<News> = MutableLiveData()
    val receivedNews: LiveData<News>
        get() = _receivedNews

    //data received will be updated here
    private val _jobCompleted = MutableLiveData(false)
    val jobCompleted: LiveData<Boolean>
        get() = _jobCompleted

    //function call to GET data from API
    fun getNews() {
        viewModelScope.launch {
            val fetchedNews = RetrofitInstance.api.getNews()
            _receivedNews.value = fetchedNews
            _jobCompleted.value = true
            Log.i("NewsViewModel", fetchedNews.toString())
        }
    }
}