package usth.ict.group20.imdb.models

import com.google.gson.annotations.SerializedName
data class TvShow(
    val id: Int,
    val name: String,
    val overview: String?,
    val poster_path: String?,
    val first_air_date: String?,
    @SerializedName("vote_average")
    val voteAverage: Double?
) {
    val posterUrl: String
        get() = "https://image.tmdb.org/t/p/w500${poster_path}"
}

data class TvShowResponse(
    val results: List<TvShow>
)