package usth.ict.group20.imdb.ui.activity

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
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
import usth.ict.group20.imdb.network.IMDbApiService
import usth.ict.group20.imdb.network.RetrofitClient

class FilmActivity : AppCompatActivity() {

    private val apiService: IMDbApiService by lazy { RetrofitClient.instance }
    private val apiKey = "f23fa4d133a28ce7eb6220b8901707b7"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_film)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Details"

        val filmId = intent.getIntExtra("FILM_ID", -1)
        val mediaType = intent.getStringExtra("MEDIA_TYPE")

        if (filmId != -1 && mediaType != null) {
            when (mediaType) {
                "movie" -> fetchMovieDetails(filmId)
                "tv" -> fetchTvShowDetails(filmId)
            }
        } else {
            Toast.makeText(this, "Error: Film not found", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }

    private fun fetchMovieDetails(movieId: Int) {
        apiService.getMovieDetails(movieId, apiKey).enqueue(object : Callback<MovieDetail> {
            override fun onResponse(call: Call<MovieDetail>, response: Response<MovieDetail>) {
                if (response.isSuccessful) {
                    response.body()?.let { bindMovieData(it) }
                } else {
                    showErrorToast()
                }
            }
            override fun onFailure(call: Call<MovieDetail>, t: Throwable) {
                showErrorToast()
            }
        })
    }

    private fun fetchTvShowDetails(tvId: Int) {
        apiService.getTvShowDetails(tvId, apiKey).enqueue(object : Callback<TvShowDetail> {
            override fun onResponse(call: Call<TvShowDetail>, response: Response<TvShowDetail>) {
                if (response.isSuccessful) {
                    response.body()?.let { bindTvShowData(it) }
                } else {
                    showErrorToast()
                }
            }
            override fun onFailure(call: Call<TvShowDetail>, t: Throwable) {
                showErrorToast()
            }
        })
    }

    private fun bindMovieData(movie: MovieDetail) {
        findViewById<TextView>(R.id.tv_film_title).text = movie.title
        findViewById<TextView>(R.id.tv_media_type).text = "Movie"
        findViewById<TextView>(R.id.tv_year).text = movie.releaseDate?.take(4)
        findViewById<TextView>(R.id.tv_summary).text = movie.overview
        findViewById<TextView>(R.id.tv_rating_value).text = String.format("%.1f / 10", movie.voteAverage)

        movie.runtime?.let {
            if (it > 0) {
                val hours = it / 60
                val minutes = it % 60
                findViewById<TextView>(R.id.tv_duration).text = "${hours}h ${minutes}m"
            }
        }

        findViewById<ImageView>(R.id.iv_poster).load("https://image.tmdb.org/t/p/w500${movie.posterPath}")

        val genreGroup: ChipGroup = findViewById(R.id.chip_group_genres)
        movie.genres.forEach { genre ->
            genreGroup.addView(Chip(this).apply { text = genre.name })
        }

        // ✅ Re-added backdrop slideshow logic
        val backdropViewPager: ViewPager2 = findViewById(R.id.backdrop_viewpager)
        val backdrops = movie.images?.backdrops
        if (!backdrops.isNullOrEmpty()) {
            backdropViewPager.adapter = BackdropAdapter(backdrops)
            backdropViewPager.visibility = View.VISIBLE
        } else {
            backdropViewPager.visibility = View.GONE // Hide if no images
        }
    }

    private fun bindTvShowData(tvShow: TvShowDetail) {
        findViewById<TextView>(R.id.tv_film_title).text = tvShow.name
        findViewById<TextView>(R.id.tv_media_type).text = "TV Show"
        findViewById<TextView>(R.id.tv_year).text = tvShow.firstAirDate?.take(4)
        findViewById<TextView>(R.id.tv_summary).text = tvShow.overview
        findViewById<TextView>(R.id.tv_rating_value).text = String.format("%.1f / 10", tvShow.voteAverage)

        tvShow.episodeRunTime?.firstOrNull()?.let {
            if (it > 0) {
                val hours = it / 60
                val minutes = it % 60
                findViewById<TextView>(R.id.tv_duration).text = if (hours > 0) "${hours}h ${minutes}m" else "${minutes}m"
            }
        }

        val usRating = tvShow.contentRatings.results.find { it.country == "US" }?.rating
        if (!usRating.isNullOrEmpty()) {
            findViewById<TextView>(R.id.tv_content_rating).apply {
                text = usRating
                visibility = View.VISIBLE
            }
        }

        findViewById<ImageView>(R.id.iv_poster).load("https://image.tmdb.org/t/p/w500${tvShow.posterPath}")

        val genreGroup: ChipGroup = findViewById(R.id.chip_group_genres)
        tvShow.genres.forEach { genre ->
            genreGroup.addView(Chip(this).apply { text = genre.name })
        }

        // ✅ Re-added backdrop slideshow logic
        val backdropViewPager: ViewPager2 = findViewById(R.id.backdrop_viewpager)
        val backdrops = tvShow.images?.backdrops
        if (!backdrops.isNullOrEmpty()) {
            backdropViewPager.adapter = BackdropAdapter(backdrops)
            backdropViewPager.visibility = View.VISIBLE
        } else {
            backdropViewPager.visibility = View.GONE // Hide if no images
        }
    }

    private fun showErrorToast() {
        Toast.makeText(this, "Failed to load film details", Toast.LENGTH_SHORT).show()
    }
}