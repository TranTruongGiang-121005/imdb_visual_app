package usth.ict.group20.imdb.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import usth.ict.group20.imdb.R
import usth.ict.group20.imdb.models.CarouselItems

class TopRatedAdapter(private val items: List<CarouselItems>) :
    RecyclerView.Adapter<TopRatedAdapter.TopRatedViewHolder>() {

    inner class TopRatedViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val poster: ImageView = view.findViewById(R.id.film_poster_image)
        val title: TextView = view.findViewById(R.id.film_title_text)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TopRatedViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_film_poster, parent, false)
        return TopRatedViewHolder(view)
    }

    override fun onBindViewHolder(holder: TopRatedViewHolder, position: Int) {
        val item = items[position]
        holder.title.text = item.name
        holder.poster.load(item.imageUrl)
    }

    override fun getItemCount(): Int = items.size
}
