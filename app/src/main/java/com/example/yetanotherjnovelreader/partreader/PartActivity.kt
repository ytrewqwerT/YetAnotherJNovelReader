package com.example.yetanotherjnovelreader.partreader

import android.animation.AnimatorInflater
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.View
import android.widget.ProgressBar
import android.widget.ScrollView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.animation.addListener
import androidx.lifecycle.observe
import com.example.yetanotherjnovelreader.R
import com.example.yetanotherjnovelreader.data.Repository

class PartActivity : AppCompatActivity() {
    companion object {
        const val EXTRA_PART_ID = "PART_ID"
    }

    private var partId: String = ""
    private var mainTextViewWidth = 0

    private val viewModel by viewModels<PartViewModel> {
        PartViewModelFactory(
            Repository.getInstance(applicationContext), resources, partId, mainTextViewWidth
        )
    }

    private lateinit var loadBar: ProgressBar
    private lateinit var scrollView: ScrollView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_part)

        partId = intent.getStringExtra(EXTRA_PART_ID)

        loadBar = findViewById(R.id.load_bar)
        scrollView = findViewById(R.id.content_scroll_container)
        val textView = findViewById<TextView>(R.id.content_view)

        val mainTextView = findViewById<TextView>(R.id.content_view)
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        mainTextViewWidth =
            displayMetrics.widthPixels - 2 * resources.getDimensionPixelSize(R.dimen.text_margin)
        viewModel.contents.observe(this) { mainTextView.text = it }

        // TODO: Add indicator that part is still loading until below observer is fired
        viewModel.initialPartProgress.observe(this) { percentage ->
            val position = textView.height * percentage
            scrollView.scrollTo(0, position.toInt())
            transitionToContent()
        }

        scrollView.setOnScrollChangeListener { _, _, scrollY, _, _ ->
            val tvHeight = textView.height
            val svHeight = scrollView.height
            viewModel.currentPartProgress = if (svHeight < tvHeight) {
                // tvHeight - svHeight == scrollY when scrolled to bottom of scrollView
                scrollY.toDouble() / (tvHeight - svHeight)
            } else {
                1.0
            }
        }
    }

    private fun transitionToContent() {
        val loadBarAnimator = AnimatorInflater.loadAnimator(this, android.R.animator.fade_out)
        loadBarAnimator.setTarget(loadBar)
        loadBarAnimator.addListener(onEnd = { loadBar.visibility = View.GONE })
        loadBarAnimator.start()

        val scrollViewAnimator = AnimatorInflater.loadAnimator(this, R.animator.slide_from_bottom)
        scrollViewAnimator.setTarget(scrollView)
        scrollViewAnimator.addListener(onStart = { scrollView.visibility = View.VISIBLE })
        scrollViewAnimator.start()
    }
}
