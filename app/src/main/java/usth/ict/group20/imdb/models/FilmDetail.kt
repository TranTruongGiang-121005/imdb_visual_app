package usth.ict.group20.imdb.models

import com.google.gson.annotations.SerializedName

// --- Main Data Classes ---
data class MovieDetail(
    val id: Int,
    val title: String,
    val overview: String?,
    @SerializedName("poster_path") val posterPath: String?,
    @SerializedName("release_date") val releaseDate: String?,
    val genres: List<Genre>,
    val runtime: Int?,
    @SerializedName("vote_average") val voteAverage: Double,
    val images: ImagesResponse? // For backdrop images
)

data class TvShowDetail(
    val id: Int,
    val name: String,
    val overview: String?,
    @SerializedName("poster_path") val posterPath: String?,
    @SerializedName("first_air_date") val firstAirDate: String?,
    val genres: List<Genre>,
    @SerializedName("episode_run_time") val episodeRunTime: List<Int>?,
    @SerializedName("vote_average") val voteAverage: Double,
    @SerializedName("content_ratings") val contentRatings: ContentRatingsResponse,
    val images: ImagesResponse? // For backdrop images
)

// --- Helper Data Classes ---
data class Genre(val name: String)
data class ImagesResponse(val backdrops: List<Image>)
data class Image(@SerializedName("file_path") val filePath: String)
data class ContentRatingsResponse(val results: List<ContentRating>)
data class ContentRating(val rating: String, @SerializedName("iso_3166_1") val country: String)