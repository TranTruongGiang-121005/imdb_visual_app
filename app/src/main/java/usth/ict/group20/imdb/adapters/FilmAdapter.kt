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
import usth.ict.group20.imdb.models.Movie

// ✅ 1. Add the listener to the constructor
class FilmAdapter(
    private val films: List<Movie>,
    private val listener: OnFilmClickListener
) : RecyclerView.Adapter<FilmAdapter.FilmViewHolder>() {

    class FilmViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val poster: ImageView = itemView.findViewById(R.id.film_poster_image)
        val title: TextView? = itemView.findViewById(R.id.film_title_text)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilmViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_film_poster, parent, false)
        return FilmViewHolder(view)
    }

    override fun onBindViewHolder(holder: FilmViewHolder, position: Int) {
        val film = films[position]
        holder.poster.load("https://image.tmdb.org/t/p/w500${film.poster_path}")
        holder.title?.text = film.title

        // ✅ 2. Set the click listener on the item
        holder.itemView.setOnClickListener {
            listener.onFilmClick(film.id, "movie") // All films here are movies
        }
    }

    override fun getItemCount() = films.size
}
