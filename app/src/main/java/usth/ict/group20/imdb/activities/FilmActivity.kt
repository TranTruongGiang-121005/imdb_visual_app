package usth.ict.group20.imdb.ui.activity

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import coil.load
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import usth.ict.group20.imdb.R
import usth.ict.group20.imdb.adapters.BackdropAdapter
import usth.ict.group20.imdb.models.*
import usth.ict.group20.imdb.network.*

class FilmActivity : AppCompatActivity() {

    private val apiService: IMDbApiService by lazy { RetrofitClient.instance }
    private val apiKey = "f23fa4d133a28ce7eb6220b8901707b7"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_film)

        val filmId = intent.getIntExtra("FILM_ID", -1)
        val mediaType = intent.getStringExtra("MEDIA_TYPE")

        if (filmId != -1 && mediaType != null) {
            when (mediaType) {
                "movie" -> fetchMovieDetails(filmId)
                "tv" -> fetchTvShowDetails(filmId)
            }
        } else {
            finish() // Exit if data is invalid
        }
    }

    private fun fetchMovieDetails(movieId: Int) {
        apiService.getMovieDetails(movieId, apiKey).enqueue(object : Callback<MovieDetail> {
            override fun onResponse(call: Call<MovieDetail>, response: Response<MovieDetail>) {
                response.body()?.let { bindMovieData(it) }
            }
            override fun onFailure(call: Call<MovieDetail>, t: Throwable) { /* Handle error */ }
        })
    }

    private fun fetchTvShowDetails(tvId: Int) {
        apiService.getTvShowDetails(tvId, apiKey).enqueue(object : Callback<TvShowDetail> {
            override fun onResponse(call: Call<TvShowDetail>, response: Response<TvShowDetail>) {
                response.body()?.let { bindTvShowData(it) }
            }
            override fun onFailure(call: Call<TvShowDetail>, t: Throwable) { /* Handle error */ }
        })
    }

    private fun bindMovieData(movie: MovieDetail) {
        findViewById<TextView>(R.id.tv_film_title).text = movie.title
        findViewById<TextView>(R.id.tv_media_type).text = "Movie"
        findViewById<TextView>(R.id.tv_year).text = movie.releaseDate?.substring(0, 4)
        findViewById<TextView>(R.id.tv_summary).text = movie.overview
        findViewById<TextView>(R.id.tv_rating_value).text = String.format("%.1f / 10", movie.voteAverage)
        movie.runtime?.let {
            findViewById<TextView>(R.id.tv_duration).text = "${it / 60}h ${it % 60}m"
        }

        findViewById<ImageView>(R.id.iv_poster).load("https://image.tmdb.org/t/p/w500${movie.posterPath}")

        val genreGroup: ChipGroup = findViewById(R.id.chip_group_genres)
        movie.genres.forEach {
            genreGroup.addView(Chip(this).apply { text = it.name })
        }

        movie.images?.backdrops?.let {
            val backdropViewPager: ViewPager2 = findViewById(R.id.backdrop_viewpager)
            backdropViewPager.adapter = BackdropAdapter(it)
        }
    }

    private fun bindTvShowData(tvShow: TvShowDetail) {
        findViewById<TextView>(R.id.tv_film_title).text = tvShow.name
        findViewById<TextView>(R.id.tv_media_type).text = "TV Show"
        findViewById<TextView>(R.id.tv_year).text = tvShow.firstAirDate?.substring(0, 4)
        // ... Bind other TV-specific views
    }
}