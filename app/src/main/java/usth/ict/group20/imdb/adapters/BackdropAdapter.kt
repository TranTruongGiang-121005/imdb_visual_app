package usth.ict.group20.imdb.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import usth.ict.group20.imdb.R
import usth.ict.group20.imdb.models.Image

class BackdropAdapter(private val images: List<Image>) :
    RecyclerView.Adapter<BackdropAdapter.BackdropViewHolder>() {

    class BackdropViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.backdrop_image)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BackdropViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_backdrop, parent, false)
        return BackdropViewHolder(view)
    }

    override fun onBindViewHolder(holder: BackdropViewHolder, position: Int) {
        val imageUrl = "https://image.tmdb.org/t/p/w780${images[position].filePath}"
        holder.imageView.load(imageUrl)
    }

    override fun getItemCount() = images.size
}