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
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import usth.ict.group20.imdb.R
import usth.ict.group20.imdb.adapters.*
import usth.ict.group20.imdb.models.*
import usth.ict.group20.imdb.network.*
import usth.ict.group20.imdb.ui.activity.SearchResultsActivity

class HomeActivity : Fragment() {

    private lateinit var viewPager: ViewPager2
    private val scrollHandler = Handler(Looper.getMainLooper())
    private lateinit var scrollRunnable: Runnable
    private var carouselItemCount = 0

    private val apiService: IMDbApiService by lazy { RetrofitClient.instance }
    private val apiKey = "f23fa4d133a28ce7eb6220b8901707b7"

    // 🌟 Offline Featured Today data
    private val featureTodayList = listOf(
        CarouselItems("Editor's Pick: The Matrix", "file:///android_asset/matrix_poster.jpg", 100001),
        CarouselItems("Classic: Inception", "file:///android_asset/inception_poster.jpg", 100002),
        CarouselItems("New on Netflix: Stranger Things", "file:///android_asset/strangerthings_poster.jpg", 100003),
        CarouselItems("Weekend Pick: Dune Part Two", "file:///android_asset/dune_poster.jpg", 100004)
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.activity_home, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupFollowButtons(view)
        setupSearch(view)

        loadCarousel(view)
        setupTopBoxOffice(view)
    }

    private fun loadCarousel(view: View) {
        val movieCall = apiService.getInTheaters(apiKey)
        val tvCall = apiService.getOnAirTvShows(apiKey)

        movieCall.enqueue(object : Callback<MovieResponse> {
            override fun onResponse(call: Call<MovieResponse>, response: Response<MovieResponse>) {
                val movieList = response.body()?.results ?: emptyList()

                tvCall.enqueue(object : Callback<TvShowResponse> {
                    override fun onResponse(call: Call<TvShowResponse>, response: Response<TvShowResponse>) {
                        val tvList = response.body()?.results ?: emptyList()

                        val movieItems = movieList.map {
                            MediaItem(
                                id = it.id,
                                title = it.title ?: "Untitled",
                                posterPath = it.poster_path,
                                releaseDate = it.release_date,
                                voteAverage = it.voteAverage
                            )
                        }

                        val tvItems = tvList.map {
                            MediaItem(
                                id = it.id,
                                title = it.name ?: "Untitled",
                                posterPath = it.poster_path,
                                releaseDate = it.first_air_date,
                                voteAverage = it.voteAverage
                            )
                        }

                        val combined: List<CarouselItems> = (movieItems + tvItems)
                            .sortedByDescending { it.releaseDate ?: "" }
                            .take(5)
                            .map {
                                CarouselItems(
                                    name = it.title,
                                    imageUrl = it.posterPath?.let { path -> "https://image.tmdb.org/t/p/w500$path" }
                                        ?: "file:///android_asset/placeholder_poster.jpg",
                                    filmId = it.id
                                )
                            }

                        setupFeaturedCarousel(view, combined)

                        setupFeaturedToday(view)
                        loadPopularPeople(view)
                    }

                    override fun onFailure(call: Call<TvShowResponse>, t: Throwable) {
                        showToast("Failed to load TV shows: ${t.message}")
                    }
                })
            }

            override fun onFailure(call: Call<MovieResponse>, t: Throwable) {
                showToast("Failed to load movies: ${t.message}")
            }
        })
    }

    private fun setupFeaturedCarousel(view: View, items: List<CarouselItems>) {
        viewPager = view.findViewById(R.id.featured_carousel_viewpager)
        carouselItemCount = items.size
        viewPager.adapter = CarouselAdapter(items)
        if (carouselItemCount > 1) setupAutoScroll()
    }

    private fun setupAutoScroll() {
        scrollRunnable = Runnable {
            viewPager.currentItem = (viewPager.currentItem + 1) % carouselItemCount
        }
        startAutoScroll()
    }

    private fun startAutoScroll() {
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
        recyclerView.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        recyclerView.adapter = CarouselAdapter(featureTodayList)

        val snapHelper = LinearSnapHelper()
        snapHelper.attachToRecyclerView(recyclerView)
        recyclerView.setHasFixedSize(true)
        recyclerView.isNestedScrollingEnabled = false
    }

    private fun loadPopularPeople(view: View) {
        apiService.getPopularCelebrities(apiKey)
            .enqueue(object : Callback<PersonResponse> {
                override fun onResponse(call: Call<PersonResponse>, response: Response<PersonResponse>) {
                    val celebs = response.body()?.results ?: emptyList()
                    setupHorizontalRecyclerView(
                        view,
                        R.id.popular_people_recyclerview,
                        PersonAdapter(celebs)
                    )
                    loadWhatToWatch(view)
                }

                override fun onFailure(call: Call<PersonResponse>, t: Throwable) {
                    showToast("Failed to load Popular People: ${t.message}")
                    loadWhatToWatch(view)
                }
            })
    }

    private fun loadWhatToWatch(view: View) {
        loadTop10ThisWeek(view)
    }

    private fun loadTop10ThisWeek(view: View) {
        apiService.getTrendingMovies(apiKey)
            .enqueue(object : Callback<MovieResponse> {
                override fun onResponse(call: Call<MovieResponse>, response: Response<MovieResponse>) {
                    val trending = response.body()?.results?.take(10) ?: emptyList()
                    setupHorizontalRecyclerView(view, R.id.top_10_recyclerview, FilmAdapter(trending))
                    loadFanFavorites(view)
                }

                override fun onFailure(call: Call<MovieResponse>, t: Throwable) {
                    showToast("Failed to load Top 10: ${t.message}")
                    loadFanFavorites(view)
                }
            })
    }

    private fun loadFanFavorites(view: View) {
        apiService.getFanFavorites(apiKey)
            .enqueue(object : Callback<MovieResponse> {
                override fun onResponse(call: Call<MovieResponse>, response: Response<MovieResponse>) {
                    val favs = response.body()?.results?.take(10) ?: emptyList()
                    setupHorizontalRecyclerView(view, R.id.top_picks_recyclerview, FilmAdapter(favs))
                    loadInTheaters(view)
                }

                override fun onFailure(call: Call<MovieResponse>, t: Throwable) {
                    showToast("Failed to load Fan Favorites: ${t.message}")
                    loadInTheaters(view)
                }
            })
    }

    private fun loadInTheaters(view: View) {
        apiService.getInTheaters(apiKey)
            .enqueue(object : Callback<MovieResponse> {
                override fun onResponse(call: Call<MovieResponse>, response: Response<MovieResponse>) {
                    val inTheaters = response.body()?.results?.take(10) ?: emptyList()
                    setupHorizontalRecyclerView(
                        view,
                        R.id.in_theaters_recyclerview,
                        FilmAdapter(inTheaters)
                    )
                    loadTopRated(view)
                }

                override fun onFailure(call: Call<MovieResponse>, t: Throwable) {
                    showToast("Failed to load In Theaters: ${t.message}")
                    loadTopRated(view)
                }
            })
    }

    private fun loadTopRated(view: View) {
        val movieCall = apiService.getTopRatedMovies(apiKey)
        val tvCall = apiService.getTopRatedTvShows(apiKey)

        movieCall.enqueue(object : Callback<MovieResponse> {
            override fun onResponse(call: Call<MovieResponse>, response: Response<MovieResponse>) {
                val movieList = response.body()?.results ?: emptyList()

                tvCall.enqueue(object : Callback<TvShowResponse> {
                    override fun onResponse(call: Call<TvShowResponse>, response: Response<TvShowResponse>) {
                        val tvList = response.body()?.results ?: emptyList()

                        val movieItems = movieList.map {
                            MediaItem(
                                id = it.id,
                                title = it.title ?: "Untitled",
                                posterPath = it.poster_path,
                                releaseDate = it.release_date,
                                voteAverage = it.voteAverage
                            )
                        }

                        val tvItems = tvList.map {
                            MediaItem(
                                id = it.id,
                                title = it.name ?: "Untitled",
                                posterPath = it.poster_path,
                                releaseDate = it.first_air_date,
                                voteAverage = it.voteAverage
                            )
                        }

                        val combined: List<CarouselItems> = (movieItems + tvItems)
                            .sortedByDescending { it.voteAverage ?: 0.0 }
                            .take(10)
                            .map {
                                CarouselItems(
                                    name = it.title,
                                    imageUrl = it.posterPath?.let { path ->
                                        "https://image.tmdb.org/t/p/w500$path"
                                    } ?: "file:///android_asset/placeholder_poster.jpg",
                                    filmId = it.id
                                )
                            }

                        setupHorizontalRecyclerView(
                            view,
                            R.id.top_rated_recyclerview,
                            TopRatedAdapter(combined)
                        )

                        loadExploreSection(view)
                    }

                    override fun onFailure(call: Call<TvShowResponse>, t: Throwable) {
                        showToast("Failed to load Top Rated TV: ${t.message}")
                        loadExploreSection(view)
                    }
                })
            }

            override fun onFailure(call: Call<MovieResponse>, t: Throwable) {
                showToast("Failed to load Top Rated Movies: ${t.message}")
                loadExploreSection(view)
            }
        })
    }


    private fun loadExploreSection(view: View) {
        loadStreamingNow(view)
    }

    private fun loadStreamingNow(view: View) {
        apiService.getOnAirTvShows(apiKey)
            .enqueue(object : Callback<TvShowResponse> {
                override fun onResponse(call: Call<TvShowResponse>, response: Response<TvShowResponse>) {
                    val nowStreaming = response.body()?.results?.take(10) ?: emptyList()
                    setupHorizontalRecyclerView(view, R.id.streaming_now_recyclerview, TvShowAdapter(nowStreaming))
                    loadComingSoon(view)
                }

                override fun onFailure(call: Call<TvShowResponse>, t: Throwable) {
                    showToast("Failed to load Streaming Now: ${t.message}")
                    loadComingSoon(view)
                }
            })
    }

    private fun setupTopBoxOffice(view: View) {
        // 1. Create some sample data matching the screenshot
        val sampleMovies = listOf(
            BoxOfficeMovie(1, "Tron: Ares", "$33.2M"),
            BoxOfficeMovie(2, "Roofman", "$8.1M"),
            BoxOfficeMovie(3, "One Battle After Another", "$6.8M"),
            BoxOfficeMovie(4, "Gabby's Dollhouse: The Movie", "$3.5M"),
            BoxOfficeMovie(5, "The Conjuring: Last Rites", "$3.1M"),
            BoxOfficeMovie(6, "Soul on Fire", "$2.8M")
        )

        val recyclerView: RecyclerView = view.findViewById(R.id.top_box_office_recyclerview)
        recyclerView.adapter = BoxOfficeAdapter(sampleMovies)
        recyclerView.isNestedScrollingEnabled = false
    }

    private fun loadComingSoon(view: View) {
        apiService.getComingSoon(apiKey)
            .enqueue(object : Callback<MovieResponse> {
                override fun onResponse(call: Call<MovieResponse>, response: Response<MovieResponse>) {
                    val comingSoon = response.body()?.results?.take(10) ?: emptyList()
                    setupHorizontalRecyclerView(view, R.id.coming_soon_recyclerview, FilmAdapter(comingSoon))
                }

                override fun onFailure(call: Call<MovieResponse>, t: Throwable) {
                    showToast("Failed to load Coming Soon: ${t.message}")
                }
            })
    }

    private fun setupSearch(view: View) {
        val searchView: SearchView = view.findViewById(R.id.home_search_view)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (!query.isNullOrBlank()) {
                    // Launch the new SearchResultsActivity
                    val intent = Intent(requireActivity(), SearchResultsActivity::class.java)
                    intent.putExtra("SEARCH_QUERY", query)
                    startActivity(intent)
                    searchView.clearFocus() // To hide the keyboard
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // We don't want to search on every key press, so we do nothing here.
                return false
            }
        })
    }

    private fun setupFollowButtons(view: View) {
        view.findViewById<ImageButton>(R.id.btn_social_facebook).setOnClickListener { openUrl("https://facebook.com/imdb") }
        view.findViewById<ImageButton>(R.id.btn_social_twitter).setOnClickListener { openUrl("https://twitter.com/imdb") }
        view.findViewById<ImageButton>(R.id.btn_social_instagram).setOnClickListener { openUrl("https://instagram.com/imdb") }
        view.findViewById<ImageButton>(R.id.btn_social_youtube).setOnClickListener { openUrl("https://youtube.com/@imdb") }
        view.findViewById<ImageButton>(R.id.btn_social_tiktok).setOnClickListener { openUrl("https://tiktok.com/@imdb") }
    }

    private fun setupHorizontalRecyclerView(view: View, id: Int, adapter: RecyclerView.Adapter<*>) {
        view.findViewById<RecyclerView>(id).apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            this.adapter = adapter
        }
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
