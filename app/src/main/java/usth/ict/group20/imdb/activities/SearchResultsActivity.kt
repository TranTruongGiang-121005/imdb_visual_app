package usth.ict.group20.imdb.ui.activity

import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import usth.ict.group20.imdb.R
import usth.ict.group20.imdb.adapters.SearchAdapter
import usth.ict.group20.imdb.models.MultiSearchResponse
import usth.ict.group20.imdb.network.IMDbApiService
import usth.ict.group20.imdb.network.RetrofitClient
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager

class SearchResultsActivity : AppCompatActivity() {

    private lateinit var searchAdapter: SearchAdapter
    private val apiService: IMDbApiService by lazy { RetrofitClient.instance }
    private val apiKey = "f23fa4d133a28ce7eb6220b8901707b7"

    private lateinit var progressBar: ProgressBar
    private lateinit var noResultsView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_results)

        val query = intent.getStringExtra("SEARCH_QUERY")

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Results for: '$query'"

        progressBar = findViewById(R.id.progress_bar)
        noResultsView = findViewById(R.id.tv_no_results)

        setupRecyclerView()

        if (query != null && query.isNotEmpty()) {
            performSearch(query)
        } else {
            Toast.makeText(this, "No search query provided", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupRecyclerView() {
        val recyclerView: RecyclerView = findViewById(R.id.search_results_recyclerview)
        val flexboxLayoutManager = FlexboxLayoutManager(this).apply {
            flexDirection = FlexDirection.ROW
            flexWrap = FlexWrap.WRAP
        }

        // Set the new layout manager
        recyclerView.layoutManager = flexboxLayoutManager

        // The adapter remains the same
        searchAdapter = SearchAdapter(emptyList())
        recyclerView.adapter = searchAdapter
    }

    private fun performSearch(query: String) {
        progressBar.visibility = View.VISIBLE
        noResultsView.visibility = View.GONE

        apiService.searchMulti(apiKey, query).enqueue(object : Callback<MultiSearchResponse> {
            override fun onResponse(call: Call<MultiSearchResponse>, response: Response<MultiSearchResponse>) {
                progressBar.visibility = View.GONE
                if (response.isSuccessful) {
                    val results = response.body()?.results?.filter {
                        // Filter out results that aren't movies, TV, or people with images
                        (it.mediaType == "movie" && it.posterPath != null) ||
                                (it.mediaType == "tv" && it.posterPath != null) ||
                                (it.mediaType == "person" && it.profilePath != null)
                    } ?: emptyList()

                    if (results.isEmpty()) {
                        noResultsView.visibility = View.VISIBLE
                    } else {
                        searchAdapter.updateData(results)
                    }
                } else {
                    noResultsView.visibility = View.VISIBLE
                }
            }

            override fun onFailure(call: Call<MultiSearchResponse>, t: Throwable) {
                progressBar.visibility = View.GONE
                noResultsView.visibility = View.VISIBLE
                Toast.makeText(this@SearchResultsActivity, "Search failed: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}