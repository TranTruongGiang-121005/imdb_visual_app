package usth.ict.group20.imdb.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import usth.ict.group20.imdb.R
import usth.ict.group20.imdb.listeners.OnFilmClickListener
import usth.ict.group20.imdb.models.TvShow

class TvShowAdapter(
    private val shows: List<TvShow>,
    private val listener: OnFilmClickListener
) : RecyclerView.Adapter<TvShowAdapter.TvShowViewHolder>() {

    class TvShowViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val poster: ImageView = itemView.findViewById(R.id.film_poster_image)
        val title: TextView = itemView.findViewById(R.id.film_title_text)
        // ✅ Add references to the rating views
        val ratingText: TextView = itemView.findViewById(R.id.film_rating_text)
        val ratingContainer: View = itemView.findViewById(R.id.rating_container)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TvShowViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_film_poster, parent, false)
        return TvShowViewHolder(view)
    }

    override fun onBindViewHolder(holder: TvShowViewHolder, position: Int) {
        val show = shows[position]
        holder.poster.load("https://image.tmdb.org/t/p/w500${show.poster_path}")
        holder.title.text = show.name

        // ✅ Add the logic to display the rating
        if (show.voteAverage != null && show.voteAverage > 0) {
            holder.ratingText.text = String.format("%.1f", show.voteAverage)
            holder.ratingContainer.visibility = View.VISIBLE
        } else {
            holder.ratingContainer.visibility = View.GONE
        }

        holder.itemView.setOnClickListener {
            listener.onFilmClick(show.id, "tv")
        }
    }

    override fun getItemCount() = shows.size
}