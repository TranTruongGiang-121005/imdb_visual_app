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

class CarouselAdapter(private val items: List<CarouselItems>) :
    RecyclerView.Adapter<CarouselAdapter.CarouselViewHolder>() {

    class CarouselViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image: ImageView = itemView.findViewById(R.id.carousel_image)
        val title: TextView = itemView.findViewById(R.id.carousel_title)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarouselViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_carousel, parent, false)
        return CarouselViewHolder(view)
    }

    override fun onBindViewHolder(holder: CarouselViewHolder, position: Int) {
        val item = items[position]
        holder.image.load(item.imageUrl)
        holder.title.text = item.name
    }

    override fun getItemCount() = items.size
}
