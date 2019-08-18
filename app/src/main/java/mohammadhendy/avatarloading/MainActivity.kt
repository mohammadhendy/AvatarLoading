package mohammadhendy.avatarloading

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import mohammadhendy.avatarloading.avatar.Avatar

class MainActivity : AppCompatActivity() {

    private var currentImageId = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        loadNextImage(currentImageId++)
        next_button.setOnClickListener {
            loadNextImage(currentImageId++)
        }
    }

    private fun loadNextImage(imageId: Int) {
        Avatar.load("https://picsum.photos/id/$imageId/1024/1024/")
            .placeholder(R.drawable.ic_person_black_24dp)
            .errorImage(R.drawable.ic_error)
            .into(avatar_image_view)
    }
}
