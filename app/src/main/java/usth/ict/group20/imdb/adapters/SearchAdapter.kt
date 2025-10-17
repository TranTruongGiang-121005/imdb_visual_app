package usth.ict.group20.imdb.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.content.Intent
import androidx.recyclerview.widget.RecyclerView
import coil.load
import usth.ict.group20.imdb.R
import usth.ict.group20.imdb.models.SearchResult
import usth.ict.group20.imdb.ui.activity.FilmActivity


class SearchAdapter(private var results: List<SearchResult>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val VIEW_TYPE_MOVIE_TV = 1
        private const val VIEW_TYPE_PERSON = 2
    }

    override fun getItemViewType(position: Int): Int {
        return when (results[position].mediaType) {
            "movie", "tv" -> VIEW_TYPE_MOVIE_TV
            "person" -> VIEW_TYPE_PERSON
            else -> throw IllegalArgumentException("Invalid media type at position $position")
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_MOVIE_TV -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_film_poster, parent, false)
                MovieTvViewHolder(view)
            }
            VIEW_TYPE_PERSON -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_person_portrait, parent, false)
                PersonViewHolder(view)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        // Get the data for the current item
        val element = results[position]

        // Check the type of the ViewHolder and bind the data
        when (holder) {
            is MovieTvViewHolder -> {
                holder.bind(element)
                holder.itemView.setOnClickListener {
                    val intent = Intent(holder.itemView.context, FilmActivity::class.java).apply {
                        putExtra("FILM_ID", element.id)
                        putExtra("MEDIA_TYPE", element.mediaType)
                    }
                    holder.itemView.context.startActivity(intent)
                }
            }
            is PersonViewHolder -> {
                holder.bind(element)
                // You can add a click listener for people here later
                // For example, to open a PersonDetailActivity
                holder.itemView.setOnClickListener {
                    // TODO: Create and launch PersonDetailActivity
                }
            }
        }
    }

    override fun getItemCount() = results.size

    fun updateData(newResults: List<SearchResult>) {
        results = newResults
        notifyDataSetChanged()
    }

    // ViewHolder for Movie/TV, using IDs from item_film_poster.xml
    class MovieTvViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // NOTE: Please double-check these IDs match your item_film_poster.xml file!
        private val poster: ImageView = itemView.findViewById(R.id.film_poster_image)
        private val title: TextView? = itemView.findViewById(R.id.film_title_text)

        fun bind(result: SearchResult) {
            val fullPosterPath = "https://image.tmdb.org/t/p/w500${result.posterPath}"
            poster.load(fullPosterPath) {
                placeholder(R.drawable.ic_search_placeholder)
                error(R.drawable.ic_search_placeholder)
            }
            title?.text = result.title ?: result.name
        }
    }

    // ViewHolder for Person, now merged with your item_person_portrait.xml
    class PersonViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // ✅ **These IDs now match your provided XML file**
        private val profile: ImageView = itemView.findViewById(R.id.person_portrait_image)
        private val name: TextView? = itemView.findViewById(R.id.person_name_text)

        fun bind(result: SearchResult) {
            val fullProfilePath = "https://image.tmdb.org/t/p/w500${result.profilePath}"
            profile.load(fullProfilePath) {
                placeholder(R.drawable.ic_search_placeholder)
                error(R.drawable.ic_search_placeholder)
            }
            name?.text = result.name
        }
    }
}