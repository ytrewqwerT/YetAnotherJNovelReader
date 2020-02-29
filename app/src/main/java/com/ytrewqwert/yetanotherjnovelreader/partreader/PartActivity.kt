package com.ytrewqwert.yetanotherjnovelreader.partreader

import android.animation.AnimatorInflater
import android.content.Intent
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ProgressBar
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.animation.addListener
import androidx.lifecycle.observe
import com.ytrewqwert.yetanotherjnovelreader.R
import com.ytrewqwert.yetanotherjnovelreader.data.Repository
import com.ytrewqwert.yetanotherjnovelreader.settings.SettingsActivity

class PartActivity : AppCompatActivity() {
    companion object {
        const val EXTRA_PART_ID = "PART_ID"
    }

    private var partId: String = ""
    private var mainTextViewWidth = 0
    private var statusBarHeight = 0

    private val viewModel by viewModels<PartViewModel> {
        PartViewModelFactory(
            Repository.getInstance(applicationContext), resources, partId, mainTextViewWidth
        )
    }

    private lateinit var statusBackground: View
    private lateinit var loadBar: ProgressBar
    private lateinit var scrollView: ScrollView
    private lateinit var textView: TextView
    private lateinit var toolbar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_part)

        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        partId = intent.getStringExtra(EXTRA_PART_ID)

        statusBackground = findViewById(R.id.status_bar_background)
        loadBar = findViewById(R.id.load_bar)
        scrollView = findViewById(R.id.content_scroll_container)
        textView = findViewById<TextView>(R.id.content_view)

        initialiseStatusBarHeight()
        determineMainTextViewWidth()
        initialiseObserversListeners()

        if (viewModel.initialPartProgress.value != null) transitionToContent()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_reader, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean  = when (item?.itemId) {
        R.id.settings_button -> {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    override fun onPause() {
        super.onPause()
        viewModel.uploadProgressNow()
    }
    override fun onResume() {
        super.onResume()
        val textSize = viewModel.fontSize.toFloat()
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize)
        textView.typeface = viewModel.fontStyle
    }

    private fun determineMainTextViewWidth() {
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        mainTextViewWidth =
            displayMetrics.widthPixels - 2 * resources.getDimensionPixelSize(R.dimen.text_margin)
    }

    private fun initialiseObserversListeners() {
        viewModel.contents.observe(this) { textView.text = it }
        viewModel.initialPartProgress.observe(this) {
            transitionToContent()
        }
        viewModel.errorEvent.observe(this) { error ->
            Toast.makeText(this, error, Toast.LENGTH_LONG).show()
            finish()
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
        // Attaching to scrollView would have been preferred, but it doesn't seem to want to work...
        textView.setOnClickListener {
            toggleTopAppBarVisibility()
        }
    }

    private fun initialiseStatusBarHeight() {
        statusBarHeight = resources.getDimensionPixelSize(R.dimen.default_status_bar_height)
        val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
        if (resourceId > 0) statusBarHeight = resources.getDimensionPixelSize(resourceId)
        statusBackground.layoutParams.height = statusBarHeight
        toolbar.setPadding(0, statusBarHeight, 0, 0)
        toolbar.layoutParams.height += statusBarHeight
    }


    private fun toggleTopAppBarVisibility() {
        when (toolbar.visibility) {
            View.VISIBLE -> {
                val animator = AnimatorInflater.loadAnimator(this, R.animator.hide_top_app_bar)
                animator.setTarget(toolbar)
                animator.addListener(onEnd = { toolbar.visibility = View.GONE })
                animator.start()
            }
            else -> {
                val animator = AnimatorInflater.loadAnimator(this, R.animator.show_top_app_bar)
                animator.setTarget(toolbar)
                animator.addListener(onStart = { toolbar.visibility = View.VISIBLE })
                animator.start()
            }
        }
    }

    private fun transitionToContent() {
        // Set scrollview position before transitioning
        val percentage = viewModel.initialPartProgress.value ?: 0.0
        val position = textView.height * percentage
        scrollView.scrollTo(0, position.toInt())

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
