package usth.ict.group20.imdb.listeners

fun interface OnFilmClickListener {
    fun onFilmClick(filmId: Int, mediaType: String)
}