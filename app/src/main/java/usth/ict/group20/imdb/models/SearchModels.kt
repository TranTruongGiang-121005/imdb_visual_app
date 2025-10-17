package usth.ict.group20.imdb.models

import com.google.gson.annotations.SerializedName

// The overall response from the API
data class MultiSearchResponse(
    val results: List<SearchResult>
)

// A single result, which can be a movie, TV show, or person
data class SearchResult(
    val id: Int,
    @SerializedName("media_type")
    val mediaType: String, // "movie", "tv", or "person"

    // Movie/TV specific fields (nullable)
    val title: String?,
    val name: String?, // 'name' for TV shows and people
    @SerializedName("poster_path")
    val posterPath: String?,
    @SerializedName("release_date")
    val releaseDate: String?,
    @SerializedName("first_air_date")
    val firstAirDate: String?,
    @SerializedName("vote_average")
    val voteAverage: Double?,

    // Person specific fields (nullable)
    @SerializedName("profile_path")
    val profilePath: String?
)