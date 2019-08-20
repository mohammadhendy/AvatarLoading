package mohammadhendy.avatarloading

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import mohammadhendy.avatarloading.avatar.Avatar

class MainActivity : AppCompatActivity() {

    private var currentImageId = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        lifecycle.addObserver(Avatar)
        loadNextImage(currentImageId++)
        next_button.setOnClickListener {
            loadNextImage(currentImageId++)
        }

        show_grid_button.setOnClickListener {
            startActivity(Intent(this, GridActivity::class.java))
        }
    }

    private fun loadNextImage(imageId: Int) {
        Avatar.load("https://picsum.photos/id/$imageId/1024/1024/")
            .placeholder(R.drawable.ic_person_black_24dp)
            .errorImage(R.drawable.ic_error)
            .showProgress(true)
            .memoryCache(false)
            .diskCache(true)
            .into(avatar_image_view)
    }

    override fun onDestroy() {
        super.onDestroy()
        lifecycle.removeObserver(Avatar)
    }
}
