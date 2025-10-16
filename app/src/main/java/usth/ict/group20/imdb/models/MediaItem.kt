package usth.ict.group20.imdb.models

data class MediaItem(
    val id: Int,
    val title: String,
    val posterPath: String?,
    val releaseDate: String?,
    val voteAverage: Double?
)
