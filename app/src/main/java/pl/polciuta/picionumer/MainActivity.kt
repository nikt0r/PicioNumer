package pl.polciuta.picionumer

import android.content.Context
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import android.graphics.BitmapFactory
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.*
import java.net.URL


class MainActivity : AppCompatActivity() {

    private lateinit var job: Job

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnGenerate.setOnClickListener {
            generateNumber()
        }
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        savedInstanceState.run {
            editMaxNumber.setText(getCharSequence("editMaxNumber"), TextView.BufferType.EDITABLE)
            textResult.text = getCharSequence("textResult")
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.run {
            putCharSequence("editMaxNumber", editMaxNumber.text)
            putCharSequence("textResult", textResult.text)
        }
    }

    override fun onPause() {
        super.onPause()
        cancelJob()
    }

    override fun onDestroy() {
        super.onDestroy()
        cancelJob()
    }

    private fun cancelJob() {
        if (this::job.isInitialized) job.cancel()
    }

    private fun generateNumber() {
        editMaxNumber.text.toString().toIntOrNull()?.let { maxNumber ->
            if (maxNumber > 0) {
                val random = Random()
                val rand = random.nextInt(maxNumber) + 1
                textResult.text = rand.toString()

                hideKeyboard()

                if (!this::job.isInitialized || !job.isActive) {
                    retrieveImage()
                }
            }
        }
    }

    private fun retrieveImage() {
        val urls = listOf(
            "https://picsum.photos/640/360/?random",
            "https://loremflickr.com/640/360"
        )

        val urldisplay = urls[Random().nextInt(2)]

        job = lifecycleScope.launch {
            try {
                val bitmap = withContext(Dispatchers.IO) {
                    URL(urldisplay).openStream().use {
                        BitmapFactory.decodeStream(it)
                    }
                }
                imageSpace.setImageBitmap(bitmap)
            } catch (e: CancellationException) {
                // ignore
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    showToast(getString(R.string.toast_no_image))
                }
            }
        }
    }

    private fun hideKeyboard() {
        val imm = this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(editMaxNumber.windowToken, 0)
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
}
