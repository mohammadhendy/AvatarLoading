package mohammadhendy.avatarloading

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
            .placeholder(R.drawable.ic_person_black_24dp)
            .errorImage(R.drawable.ic_error)
            .showProgress(true)
            .memoryCache(false)
            .diskCache(true)
            .into(holder.itemView.avatar_image_view)
    }

    override fun getItemCount() = images.size
}