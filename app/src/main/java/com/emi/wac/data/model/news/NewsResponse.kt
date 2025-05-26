package com.emi.wac.data.model.news

/**
 * Represents the response from a news API.
 *
 * @property status The status of the API response (e.g., "ok", "error").
 * @property totalResults The total number of results returned by the API.
 * @property articles A list of articles included in the response.
 */
data class NewsResponse(
    val status: String,
    val totalResults: Int,
    val articles: List<Article>
)

/**
 * Represents a news article.
 *
 * @property source The source of the article.
 * @property author The author of the article, if available.
 * @property title The title of the article.
 * @property description A brief description of the article.
 * @property url The URL to the full article.
 * @property urlToImage The URL to the article's image, if available.
 * @property publishedAt The publication date of the article.
 * @property content The content of the article, if available.
 */
data class Article(
    val source: Source,
    val author: String?,
    val title: String,
    val description: String?,
    val url: String,
    val urlToImage: String?,
    val publishedAt: String,
    val content: String?
)

/**
 * Represents the source of a news article.
 *
 * @property id The unique identifier of the source, if available.
 * @property name The name of the source.
 */
data class Source(
    val id: String?,
    val name: String
)