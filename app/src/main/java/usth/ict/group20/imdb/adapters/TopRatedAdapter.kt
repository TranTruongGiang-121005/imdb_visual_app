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
import usth.ict.group20.imdb.models.CarouselItems

class TopRatedAdapter(
    private val items: List<CarouselItems>,
    private val listener: OnFilmClickListener
) : RecyclerView.Adapter<TopRatedAdapter.TopRatedViewHolder>() {

    class TopRatedViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val poster: ImageView = itemView.findViewById(R.id.film_poster_image)
        val title: TextView = itemView.findViewById(R.id.film_title_text)
        val ratingText: TextView = itemView.findViewById(R.id.film_rating_text)
        val ratingContainer: View = itemView.findViewById(R.id.rating_container)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TopRatedViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_film_poster, parent, false)
        return TopRatedViewHolder(view)
    }

    override fun onBindViewHolder(holder: TopRatedViewHolder, position: Int) {
        val item = items[position]
        holder.poster.load(item.imageUrl)
        holder.title.text = item.name

        if (item.voteAverage != null && item.voteAverage > 0) {
            holder.ratingText.text = String.format("%.1f", item.voteAverage)
            holder.ratingContainer.visibility = View.VISIBLE
        } else {
            holder.ratingContainer.visibility = View.GONE
        }

        holder.itemView.setOnClickListener {
            listener.onFilmClick(item.filmId, item.mediaType)
        }
    }

    override fun getItemCount() = items.size
}