package com.example.yetanotherjnovelreader.activitypart

import android.os.Bundle
import android.util.DisplayMetrics
import android.widget.ScrollView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
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
        PartViewModel.PartViewModelFactory(
            Repository.getInstance(applicationContext), resources, partId, mainTextViewWidth
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_part)

        partId = intent.getStringExtra(EXTRA_PART_ID)

        val scrollView = findViewById<ScrollView>(R.id.content_scroll_container)
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
            scrollView.smoothScrollTo(0, position.toInt())
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
}
