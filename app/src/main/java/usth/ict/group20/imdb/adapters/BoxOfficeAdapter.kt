package usth.ict.group20.imdb.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import usth.ict.group20.imdb.R
import usth.ict.group20.imdb.listeners.OnFilmClickListener
import usth.ict.group20.imdb.models.BoxOfficeMovie

class BoxOfficeAdapter(
    private val movies: List<BoxOfficeMovie>,
    private val listener: OnFilmClickListener // Accepts the click listener from the fragment
) : RecyclerView.Adapter<BoxOfficeAdapter.MovieViewHolder>() {

    class MovieViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val rank: TextView = itemView.findViewById(R.id.tv_rank)
        val title: TextView = itemView.findViewById(R.id.tv_movie_title)
        val earnings: TextView = itemView.findViewById(R.id.tv_movie_earnings)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_box_office_movie, parent, false)
        return MovieViewHolder(view)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        val movie = movies[position]
        holder.rank.text = movie.rank.toString()
        holder.title.text = movie.title
        // Hide earnings for now as the API doesn't provide it easily
        holder.earnings.visibility = View.GONE

        // Set the click listener on the entire item
        holder.itemView.setOnClickListener {
            // When clicked, use the listener to tell HomeActivity which film was selected
            listener.onFilmClick(movie.id, "movie")
        }
    }

    override fun getItemCount() = movies.size
}