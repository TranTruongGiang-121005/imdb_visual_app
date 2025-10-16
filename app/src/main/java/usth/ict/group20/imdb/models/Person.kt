package usth.ict.group20.imdb.models

data class Person(
    val id: Int,
    val name: String,
    val profile_path: String?
) {
    val photoUrl: String
        get() = "https://image.tmdb.org/t/p/w500${profile_path}"
}

data class PersonResponse(
    val results: List<Person>
)