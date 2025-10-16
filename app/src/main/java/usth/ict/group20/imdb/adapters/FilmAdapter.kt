package usth.ict.group20.imdb.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import usth.ict.group20.imdb.R
import usth.ict.group20.imdb.models.Movie

class FilmAdapter(private val movies: List<Movie>) :
    RecyclerView.Adapter<FilmAdapter.FilmViewHolder>() {

    class FilmViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val poster: ImageView = itemView.findViewById(R.id.film_poster_image)
        val title: TextView = itemView.findViewById(R.id.film_title_text)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilmViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_film_poster, parent, false)
        return FilmViewHolder(view)
    }

    override fun onBindViewHolder(holder: FilmViewHolder, position: Int) {
        val movie = movies[position]
        holder.poster.load(movie.posterUrl)
        holder.title.text = movie.title
    }

    override fun getItemCount() = movies.size
}
