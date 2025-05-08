package com.emi.wac.data.network


import com.emi.wac.common.Constants.DOMAIN
import com.emi.wac.common.Constants.LANGUAGE
import com.emi.wac.common.Constants.NEWS_API_KEY
import com.emi.wac.common.Constants.SORT
import com.emi.wac.data.model.news.NewsResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsAPIService {
    @GET("v2/everything")
    suspend fun getNews(
        @Query("domains") domains: String = DOMAIN,
        @Query("sortBy") sortBy: String = SORT,
        @Query("language") language: String = LANGUAGE,
        @Query("apiKey") apiKey: String = NEWS_API_KEY
    ): NewsResponse
}