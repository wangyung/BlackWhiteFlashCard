package idv.freddiew

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import idv.freddiew.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    private lateinit var viewBinding: ActivityMainBinding
    private lateinit var mediaPlayer: MediaPlayer
    private val handler: Handler = Handler(Looper.getMainLooper())
    private var index = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        showImage(getFilenameFromIndex(index))
        playSound()
    }

    private fun playSound() {
        mediaPlayer = MediaPlayer.create(this, R.raw.sound1).apply {
            isLooping = true
        }

        mediaPlayer.start()
    }

    private fun showImage(filename: String) {
        lifecycleScope.launch {
            val bitmap = loadBitmapFromAssets(filename)
            viewBinding.flashImage.setImageBitmap(bitmap)
        }
        handler.postDelayed({
            showImage(getFilenameFromIndex(index))
        }, SWAP_INTERVAL)
        index++
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
        mediaPlayer.stop()
    }

    private suspend fun loadBitmapFromAssets(filename: String): Bitmap =
        withContext(Dispatchers.IO) {
            assets.open(filename).use {
                BitmapFactory.decodeStream(it)
            }
        }

    private fun getFilenameFromIndex(index: Int): String =
        "%03d.jpg".format((index % 33) + 1)

    private companion object {
        private val SWAP_INTERVAL = TimeUnit.MINUTES.toMillis(2)
    }
}
