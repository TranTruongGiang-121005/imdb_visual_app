package usth.ict.group20.imdb.models

import com.google.gson.annotations.SerializedName
data class Movie(
    val id: Int,
    val title: String,
    val overview: String?,
    val poster_path: String?,
    val release_date: String?,
    @SerializedName("vote_average")
    val voteAverage: Double?
) {
    val posterUrl: String
        get() = "https://image.tmdb.org/t/p/w500${poster_path}"
}

data class MovieResponse(
    val results: List<Movie>
)