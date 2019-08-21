package mohammadhendy.avatarloading

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_image.view.avatar_image_view
import mohammadhendy.avatarloading.avatar.Avatar

class MyAdapter(private val images: Array<String>) :
    RecyclerView.Adapter<MyAdapter.MyViewHolder>() {

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): MyAdapter.MyViewHolder {
        return MyViewHolder(LayoutInflater.from(parent.context)
            .inflate(R.layout.item_image, parent, false))
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        Avatar.load(images[position])
            .placeholder(R.drawable.ic_placeholder)
            .errorImage(R.drawable.ic_error)
            .showProgress(true)
            .memoryCache(true)
            .diskCache(true)
            .into(holder.itemView.avatar_image_view)
    }

    override fun getItemCount() = images.size
}