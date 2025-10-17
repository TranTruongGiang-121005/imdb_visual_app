package usth.ict.group20.imdb.ui.fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import usth.ict.group20.imdb.R
import usth.ict.group20.imdb.adapters.*
import usth.ict.group20.imdb.listeners.OnFilmClickListener
import usth.ict.group20.imdb.models.*
import usth.ict.group20.imdb.network.IMDbApiService
import usth.ict.group20.imdb.network.RetrofitClient
import usth.ict.group20.imdb.ui.activity.FilmActivity
import usth.ict.group20.imdb.ui.activity.SearchResultsActivity

private data class MediaItem(
    val id: Int, val mediaType: String, val title: String?,
    val posterPath: String?, val releaseDate: String?, val voteAverage: Double?
)

class HomeActivity : Fragment(), OnFilmClickListener {

    private lateinit var viewPager: ViewPager2
    private val scrollHandler = Handler(Looper.getMainLooper())
    private lateinit var scrollRunnable: Runnable
    private var carouselItemCount = 0

    private val apiService: IMDbApiService by lazy { RetrofitClient.instance }
    private val apiKey = "f23fa4d133a28ce7eb6220b8901707b7"

    private val featureTodayList = listOf(
        CarouselItems("Editor's Pick: The Matrix", "file:///android_asset/matrix_poster.jpg", 603, "movie", 8.7),
        CarouselItems("Classic: Inception", "file:///android_asset/inception_poster.jpg", 27205, "movie", 8.8)
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.activity_home, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupFollowButtons(view)
        setupSearch(view)
        loadCarousel(view)
    }

    override fun onFilmClick(filmId: Int, mediaType: String) {
        val intent = Intent(requireActivity(), FilmActivity::class.java).apply {
            putExtra("FILM_ID", filmId)
            putExtra("MEDIA_TYPE", mediaType)
        }
        startActivity(intent)
    }

    private fun loadCarousel(view: View) {
        val listener: OnFilmClickListener = this
        apiService.getInTheaters(apiKey).enqueue(object : Callback<MovieResponse> {
            override fun onResponse(call: Call<MovieResponse>, response: Response<MovieResponse>) {
                val movieList = response.body()?.results ?: emptyList()
                apiService.getOnAirTvShows(apiKey).enqueue(object : Callback<TvShowResponse> {
                    override fun onResponse(call: Call<TvShowResponse>, response: Response<TvShowResponse>) {
                        val tvList = response.body()?.results ?: emptyList()
                        val movieItems = movieList.map { MediaItem(it.id, "movie", it.title, it.poster_path, it.release_date, it.voteAverage) }
                        val tvItems = tvList.map { MediaItem(it.id, "tv", it.name, it.poster_path, it.first_air_date, it.voteAverage) }
                        val combined = (movieItems + tvItems).sortedByDescending { it.releaseDate ?: "" }.take(5)
                            .map { CarouselItems(it.title ?: "Untitled", it.posterPath?.let { p -> "https://image.tmdb.org/t/p/w500$p" } ?: "", it.id, it.mediaType, it.voteAverage) }
                        setupFeaturedCarousel(view, combined)
                        setupFeaturedToday(view)
                        loadPopularPeople(view)
                    }
                    override fun onFailure(call: Call<TvShowResponse>, t: Throwable) { showToast("Failed to load TV shows") }
                })
            }
            override fun onFailure(call: Call<MovieResponse>, t: Throwable) { showToast("Failed to load movies") }
        })
    }

    private fun setupFeaturedCarousel(view: View, items: List<CarouselItems>) {
        viewPager = view.findViewById(R.id.featured_carousel_viewpager)
        carouselItemCount = items.size
        viewPager.adapter = CarouselAdapter(items, this)
        if (carouselItemCount > 1) setupAutoScroll()
    }

    private fun setupAutoScroll() {
        scrollRunnable = Runnable { viewPager.currentItem = (viewPager.currentItem + 1) % carouselItemCount }
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                scrollHandler.removeCallbacks(scrollRunnable)
                scrollHandler.postDelayed(scrollRunnable, 3500)
            }
        })
        scrollHandler.postDelayed(scrollRunnable, 3500)
    }

    private fun setupFeaturedToday(view: View) {
        val recyclerView = view.findViewById<RecyclerView>(R.id.featured_today_recyclerview)
        recyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        recyclerView.adapter = CarouselAdapter(featureTodayList, this)
    }

    private fun loadPopularPeople(view: View) {
        apiService.getPopularCelebrities(apiKey).enqueue(object : Callback<PersonResponse> {
            override fun onResponse(call: Call<PersonResponse>, response: Response<PersonResponse>) {
                setupHorizontalRecyclerView(view, R.id.popular_people_recyclerview, PersonAdapter(response.body()?.results ?: emptyList()))
                loadTop10ThisWeek(view)
            }
            override fun onFailure(call: Call<PersonResponse>, t: Throwable) { loadTop10ThisWeek(view) }
        })
    }

    private fun loadTop10ThisWeek(view: View) {
        val listener: OnFilmClickListener = this
        apiService.getTrendingMovies(apiKey).enqueue(object : Callback<MovieResponse> {
            override fun onResponse(call: Call<MovieResponse>, response: Response<MovieResponse>) {
                setupHorizontalRecyclerView(view, R.id.top_10_recyclerview, FilmAdapter(response.body()?.results?.take(10) ?: emptyList(), listener))
                loadFanFavorites(view)
            }
            override fun onFailure(call: Call<MovieResponse>, t: Throwable) { /* Handle error */ }
        })
    }

    private fun loadFanFavorites(view: View) {
        val listener: OnFilmClickListener = this
        apiService.getFanFavorites(apiKey).enqueue(object : Callback<MovieResponse> {
            override fun onResponse(call: Call<MovieResponse>, response: Response<MovieResponse>) {
                setupHorizontalRecyclerView(view, R.id.top_picks_recyclerview, FilmAdapter(response.body()?.results?.take(10) ?: emptyList(), listener))
                loadInTheaters(view)
            }
            override fun onFailure(call: Call<MovieResponse>, t: Throwable) { /* Handle error */ }
        })
    }

    private fun loadInTheaters(view: View) {
        val listener: OnFilmClickListener = this
        apiService.getInTheaters(apiKey).enqueue(object : Callback<MovieResponse> {
            override fun onResponse(call: Call<MovieResponse>, response: Response<MovieResponse>) {
                setupHorizontalRecyclerView(view, R.id.in_theaters_recyclerview, FilmAdapter(response.body()?.results?.take(10) ?: emptyList(), listener))
                loadTopRated(view)
            }
            override fun onFailure(call: Call<MovieResponse>, t: Throwable) { /* Handle error */ }
        })
    }

    private fun loadTopRated(view: View) {
        val listener: OnFilmClickListener = this
        apiService.getTopRatedMovies(apiKey).enqueue(object : Callback<MovieResponse> {
            override fun onResponse(call: Call<MovieResponse>, response: Response<MovieResponse>) {
                val movieList = response.body()?.results ?: emptyList()
                apiService.getTopRatedTvShows(apiKey).enqueue(object : Callback<TvShowResponse> {
                    override fun onResponse(call: Call<TvShowResponse>, response: Response<TvShowResponse>) {
                        val tvList = response.body()?.results ?: emptyList()
                        val movieItems = movieList.map { MediaItem(it.id, "movie", it.title, it.poster_path, it.release_date, it.voteAverage) }
                        val tvItems = tvList.map { MediaItem(it.id, "tv", it.name, it.poster_path, it.first_air_date, it.voteAverage) }

                        // ✅ This now passes the voteAverage into the CarouselItems
                        val combined = (movieItems + tvItems).sortedByDescending { it.voteAverage ?: 0.0 }.take(10)
                            .map { CarouselItems(it.title ?: "Untitled", it.posterPath?.let { p -> "https://image.tmdb.org/t/p/w500$p" } ?: "", it.id, it.mediaType, it.voteAverage) }

                        setupHorizontalRecyclerView(view, R.id.top_rated_recyclerview, TopRatedAdapter(combined, listener))
                        loadExploreSection(view)
                    }
                    override fun onFailure(call: Call<TvShowResponse>, t: Throwable) { /* Handle error */ }
                })
            }
            override fun onFailure(call: Call<MovieResponse>, t: Throwable) { /* Handle error */ }
        })
    }

    private fun loadExploreSection(view: View) {
        loadStreamingNow(view)
    }

    private fun loadStreamingNow(view: View) {
        val listener: OnFilmClickListener = this
        apiService.getOnAirTvShows(apiKey).enqueue(object : Callback<TvShowResponse> {
            override fun onResponse(call: Call<TvShowResponse>, response: Response<TvShowResponse>) {
                setupHorizontalRecyclerView(view, R.id.streaming_now_recyclerview, TvShowAdapter(response.body()?.results?.take(10) ?: emptyList(), listener))
                loadTopBoxOffice(view)
            }
            override fun onFailure(call: Call<TvShowResponse>, t: Throwable) { loadTopBoxOffice(view) }
        })
    }

    private fun loadTopBoxOffice(view: View) {
        val listener: OnFilmClickListener = this
        apiService.getTrendingMovies(apiKey).enqueue(object : Callback<MovieResponse> {
            override fun onResponse(call: Call<MovieResponse>, response: Response<MovieResponse>) {
                val boxOfficeMovies = response.body()?.results?.take(6)?.mapIndexed { index, movie ->
                    // Add the 'earnings' parameter here
                    BoxOfficeMovie(
                        id = movie.id,
                        rank = index + 1,
                        title = movie.title ?: "Untitled",
                        earnings = "$0M" // Or a real value if you have one from your API
                    )
                } ?: emptyList()
                val recyclerView: RecyclerView = view.findViewById(R.id.top_box_office_recyclerview)
                recyclerView.layoutManager = LinearLayoutManager(context)
                recyclerView.adapter = BoxOfficeAdapter(boxOfficeMovies, listener)
                recyclerView.isNestedScrollingEnabled = false
                loadComingSoon(view)
            }
            override fun onFailure(call: Call<MovieResponse>, t: Throwable) { loadComingSoon(view) }
        })
    }

    private fun loadComingSoon(view: View) {
        val listener: OnFilmClickListener = this
        apiService.getComingSoon(apiKey).enqueue(object : Callback<MovieResponse> {
            override fun onResponse(call: Call<MovieResponse>, response: Response<MovieResponse>) {
                setupHorizontalRecyclerView(view, R.id.coming_soon_recyclerview, FilmAdapter(response.body()?.results?.take(10) ?: emptyList(), listener))
            }
            override fun onFailure(call: Call<MovieResponse>, t: Throwable) { /* Handle error */ }
        })
    }

    private fun setupHorizontalRecyclerView(view: View, id: Int, adapter: RecyclerView.Adapter<*>) {
        view.findViewById<RecyclerView>(id)?.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            this.adapter = adapter
        }
    }

    private fun setupSearch(view: View) {
        val searchView: SearchView = view.findViewById(R.id.home_search_view)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                performSearch(query)
                return true
            }
            override fun onQueryTextChange(newText: String?) = false
        })
        val searchIcon = searchView.findViewById<ImageView>(androidx.appcompat.R.id.search_mag_icon)
        searchIcon?.setOnClickListener { performSearch(searchView.query.toString()) }
    }

    private fun performSearch(query: String?) {
        if (!query.isNullOrBlank()) {
            val intent = Intent(requireActivity(), SearchResultsActivity::class.java)
            intent.putExtra("SEARCH_QUERY", query)
            startActivity(intent)
            view?.findFocus()?.clearFocus()
        }
    }

    private fun setupFollowButtons(view: View) {
        view.findViewById<ImageButton>(R.id.btn_social_facebook).setOnClickListener { openUrl("https://facebook.com/imdb") }
        view.findViewById<ImageButton>(R.id.btn_social_twitter).setOnClickListener { openUrl("https://twitter.com/imdb") }
        view.findViewById<ImageButton>(R.id.btn_social_instagram).setOnClickListener { openUrl("https://instagram.com/imdb") }
        view.findViewById<ImageButton>(R.id.btn_social_youtube).setOnClickListener { openUrl("https://youtube.com/@imdb") }
        view.findViewById<ImageButton>(R.id.btn_social_tiktok).setOnClickListener { openUrl("https://tiktok.com/@imdb") }
    }

    private fun openUrl(url: String) {
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
    }

    private fun showToast(msg: String) {
        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
    }

    override fun onPause() {
        super.onPause()
        if (::scrollRunnable.isInitialized) scrollHandler.removeCallbacks(scrollRunnable)
    }

    override fun onResume() {
        super.onResume()
        if (::scrollRunnable.isInitialized) scrollHandler.postDelayed(scrollRunnable, 3500)
    }
}