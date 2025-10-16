package usth.ict.group20.imdb.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import usth.ict.group20.imdb.R
import usth.ict.group20.imdb.models.TvShow

class TvShowAdapter(private val tvShows: List<TvShow>) :
    RecyclerView.Adapter<TvShowAdapter.TvShowViewHolder>() {

    inner class TvShowViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val poster: ImageView = view.findViewById(R.id.film_poster_image)
        val title: TextView = view.findViewById(R.id.film_title_text)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TvShowViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_film_poster, parent, false)
        return TvShowViewHolder(view)
    }

    override fun onBindViewHolder(holder: TvShowViewHolder, position: Int) {
        val tvShow = tvShows[position]
        holder.title.text = tvShow.name
        holder.poster.load(tvShow.posterUrl)
    }

    override fun getItemCount(): Int = tvShows.size
}
