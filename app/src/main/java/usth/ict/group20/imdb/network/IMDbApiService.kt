package usth.ict.group20.imdb.network

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import usth.ict.group20.imdb.models.*

interface IMDbApiService {

    @GET("movie/top_rated")
    fun getTopRatedMovies(
        @Query("api_key") apiKey: String,
        @Query("page") page: Int = 1
    ): Call<MovieResponse>

    @GET("movie/popular")
    fun getFanFavorites(
        @Query("api_key") apiKey: String,
        @Query("page") page: Int = 1
    ): Call<MovieResponse>

    @GET("movie/now_playing")
    fun getInTheaters(
        @Query("api_key") apiKey: String,
        @Query("page") page: Int = 1
    ): Call<MovieResponse>

    @GET("trending/movie/week")
    fun getTrendingMovies(
        @Query("api_key") apiKey: String
    ): Call<MovieResponse>

    @GET("movie/upcoming")
    fun getComingSoon(
        @Query("api_key") apiKey: String,
        @Query("page") page: Int = 1
    ): Call<MovieResponse>

    @GET("tv/on_the_air")
    fun getOnAirTvShows(
        @Query("api_key") apiKey: String,
        @Query("page") page: Int = 1
    ): Call<TvShowResponse>

    @GET("tv/top_rated")
    fun getTopRatedTvShows(
        @Query("api_key") apiKey: String,
        @Query("page") page: Int = 1
    ): Call<TvShowResponse>

    @GET("person/popular")
    fun getPopularCelebrities(
        @Query("api_key") apiKey: String,
        @Query("page") page: Int = 1
    ): Call<PersonResponse>

    @GET("search/multi")
    fun searchMulti(
        @Query("api_key") apiKey: String,
        @Query("query") query: String
    ): Call<MultiSearchResponse>

    @GET("movie/{movie_id}")
    fun getMovieDetails(
        @Path("movie_id") movieId: Int,
        @Query("api_key") apiKey: String,
        @Query("append_to_response") appendToResponse: String = "images"
    ): Call<MovieDetail>

    @GET("tv/{tv_id}")
    fun getTvShowDetails(
        @Path("tv_id") tvId: Int,
        @Query("api_key") apiKey: String,
        @Query("append_to_response") appendToResponse: String = "images,content_ratings"
    ): Call<TvShowDetail>
}

