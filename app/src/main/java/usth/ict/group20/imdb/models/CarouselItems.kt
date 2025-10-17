package usth.ict.group20.imdb.models

data class CarouselItems(
    val name: String,
    val imageUrl: String?,
    val filmId: Int,
    val mediaType: String,
    val voteAverage: Double?
)
