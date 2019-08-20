package mohammadhendy.avatarloading

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import kotlinx.android.synthetic.main.activity_grid.*
import kotlinx.android.synthetic.main.activity_main.*
import mohammadhendy.avatarloading.avatar.Avatar

class GridActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_grid)
        lifecycle.addObserver(Avatar)

        val images: ArrayList<String> = arrayListOf<String>()
        for (i in 0..100) {
            images.add("https://picsum.photos/id/$i/512/512/")
        }
        images_recycler_view.apply {
            adapter = MyAdapter(images.toTypedArray())
            addItemDecoration(GridItemDecoration(10, 3))
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        lifecycle.removeObserver(Avatar)
    }
}
