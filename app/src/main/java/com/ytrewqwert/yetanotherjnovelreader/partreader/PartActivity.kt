package com.ytrewqwert.yetanotherjnovelreader.partreader

import android.animation.AnimatorInflater
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.animation.addListener
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainerView
import androidx.lifecycle.observe
import com.ytrewqwert.yetanotherjnovelreader.R
import com.ytrewqwert.yetanotherjnovelreader.data.Repository
import com.ytrewqwert.yetanotherjnovelreader.databinding.ActivityPartBinding
import com.ytrewqwert.yetanotherjnovelreader.partreader.pagedreader.PagedReaderFragment
import com.ytrewqwert.yetanotherjnovelreader.settings.SettingsActivity

class PartActivity : AppCompatActivity() {
    companion object {
        const val EXTRA_PART_ID = "PART_ID"
    }

    private var partId: String = ""
    private var statusBarHeight = 0

    private lateinit var binding: ActivityPartBinding
    private val viewModel by viewModels<PartViewModel> {
        PartViewModelFactory(
            Repository.getInstance(applicationContext), resources, partId
        )
    }

    private lateinit var layoutRoot: View
    private lateinit var statusBackground: View
    private lateinit var loadBar: ProgressBar
    private lateinit var toolbar: Toolbar
    private lateinit var readerContainer: FragmentContainerView
    private lateinit var readerFragment: Fragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_part)
        binding.lifecycleOwner = this

        partId = intent.getStringExtra(EXTRA_PART_ID)
        binding.viewModel = viewModel

        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        layoutRoot = findViewById(R.id.layout_root)
        statusBackground = findViewById(R.id.status_bar_background)
        loadBar = findViewById(R.id.load_bar)
        readerContainer = findViewById(R.id.reader_container)

        readerFragment = PagedReaderFragment()
        with (supportFragmentManager.beginTransaction()) {
            replace(R.id.reader_container, readerFragment)
            commit()
        }

        // Indirectly notify the ReaderFragment to refresh its position after rendering has
        // completed since it may have responded to the value before rendering finished, resulting
        // in possible mis-positioning.
        layoutRoot.post {
            viewModel.currentProgress.value = viewModel.currentProgress.value
        }

        initialiseStatusBarHeight()
        initialiseObserversListeners()

        setStatusBarTextColor(resources.getBoolean(R.bool.isLightMode))

        if (viewModel.partReady.value == true) transitionToContent()
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

    private fun initialiseObserversListeners() {
        viewModel.partReady.observe(this) {
            if (it) transitionToContent()
        }
        viewModel.errorEvent.observe(this) { error ->
            Toast.makeText(this, error, Toast.LENGTH_LONG).show()
            finish()
        }
        viewModel.showAppBar.observe(this) {
            setAppBarVisibility(it)
        }
    }

    private fun initialiseStatusBarHeight() {
        layoutRoot.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE

        statusBarHeight = resources.getDimensionPixelSize(R.dimen.default_status_bar_height)
        val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
        if (resourceId > 0) statusBarHeight = resources.getDimensionPixelSize(resourceId)
        statusBackground.layoutParams.height = statusBarHeight
        toolbar.setPadding(0, statusBarHeight, 0, 0)
        toolbar.layoutParams.height += statusBarHeight
    }

    private fun setStatusBarTextColor(isLight: Boolean) {
        var uiVisibility = window.decorView.systemUiVisibility
        var mask = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        if (isLight) {
            uiVisibility = uiVisibility or mask
        } else {
            mask = mask.inv()
            uiVisibility = uiVisibility and mask
        }
        window.decorView.systemUiVisibility = uiVisibility
    }

    private fun setAppBarVisibility(visible: Boolean) {
        when (visible) {
            true -> {
                setStatusBarTextColor(false)
                val animator = AnimatorInflater.loadAnimator(this, R.animator.show_top_app_bar)
                animator.setTarget(toolbar)
                animator.addListener(onStart = { toolbar.visibility = View.VISIBLE })
                animator.start()
            }
            else -> {
                setStatusBarTextColor(resources.getBoolean(R.bool.isLightMode))
                val animator = AnimatorInflater.loadAnimator(this, R.animator.hide_top_app_bar)
                animator.setTarget(toolbar)
                animator.addListener(onEnd = { toolbar.visibility = View.GONE })
                animator.start()
            }
        }
    }

    private fun transitionToContent() {
        viewModel.gotoProgressEvent.value = true

        val loadBarAnimator = AnimatorInflater.loadAnimator(this, android.R.animator.fade_out)
        loadBarAnimator.setTarget(loadBar)
        loadBarAnimator.addListener(onEnd = { loadBar.visibility = View.GONE })
        loadBarAnimator.start()

        val contentAnimator = AnimatorInflater.loadAnimator(this, R.animator.slide_from_bottom)
        contentAnimator.setTarget(readerContainer)
        contentAnimator.addListener(onStart = { readerContainer.visibility = View.VISIBLE })
        contentAnimator.start()
    }
}
