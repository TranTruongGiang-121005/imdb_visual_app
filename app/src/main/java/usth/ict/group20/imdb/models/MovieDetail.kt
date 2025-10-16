package usth.ict.group20.imdb.models

data class MovieDetail(
    val id: Int,
    val title: String?,
    val overview: String?,
    val poster_path: String?,
    val backdrop_path: String?,
    val release_date: String?,
    val runtime: Int?,
    val vote_average: Double?,
    val genres: List<Genre>?
) {
    val posterUrl: String?
        get() = poster_path?.let { "https://image.tmdb.org/t/p/w500$it" }

    val backdropUrl: String?
        get() = backdrop_path?.let { "https://image.tmdb.org/t/p/w780$it" }
}

data class Genre(
    val id: Int,
    val name: String
)
