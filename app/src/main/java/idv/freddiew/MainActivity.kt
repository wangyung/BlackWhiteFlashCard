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
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    private lateinit var viewBinding: ActivityMainBinding
    private var mediaPlayer: MediaPlayer? = null
    private val handler: Handler = Handler(Looper.getMainLooper())
    private var randomIndex = Random(System.currentTimeMillis())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        showImage(getRandomFilename())
    }

    private fun playSound() {
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(this, R.raw.sound1).apply {
                isLooping = true
            }
        }

        mediaPlayer?.start()
    }

    private fun showImage(filename: String) {
        lifecycleScope.launch {
            val bitmap = loadBitmapFromAssets(filename)
            viewBinding.flashImage.setImageBitmap(bitmap)
        }
        handler.postDelayed({
            showImage(getRandomFilename())
        }, SWAP_INTERVAL)
    }

    override fun onResume() {
        super.onResume()
        playSound()
    }

    override fun onPause() {
        super.onPause()
        mediaPlayer?.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
        mediaPlayer?.stop()
    }

    private suspend fun loadBitmapFromAssets(filename: String): Bitmap =
        withContext(Dispatchers.IO) {
            assets.open(filename).use {
                BitmapFactory.decodeStream(it)
            }
        }

    private fun getRandomFilename(): String =
        "%03d.jpg".format((randomIndex.nextInt(34) + 1))

    private companion object {
        private val SWAP_INTERVAL = TimeUnit.MINUTES.toMillis(1)
    }
}
