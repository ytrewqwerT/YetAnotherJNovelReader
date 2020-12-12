package com.ytrewqwert.yetanotherjnovelreader.partreader

import android.animation.AnimatorInflater
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.animation.doOnEnd
import androidx.core.animation.doOnStart
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainerView
import androidx.fragment.app.commit
import androidx.lifecycle.observe
import com.ytrewqwert.yetanotherjnovelreader.R
import com.ytrewqwert.yetanotherjnovelreader.Utils
import com.ytrewqwert.yetanotherjnovelreader.data.Repository
import com.ytrewqwert.yetanotherjnovelreader.databinding.ActivityPartBinding
import com.ytrewqwert.yetanotherjnovelreader.partreader.pagedreader.PagedReaderFragment
import com.ytrewqwert.yetanotherjnovelreader.partreader.scrollreader.ScrollReaderFragment
import com.ytrewqwert.yetanotherjnovelreader.settings.SettingsActivity

/** Displays the contents of a part for the user to read. */
class PartActivity : AppCompatActivity() {
    companion object {
        const val EXTRA_PART_ID = "PART_ID"
    }

    private val partId: String by lazy { intent.getStringExtra(EXTRA_PART_ID) ?: "" }
    private val statusBarHeight by lazy {
        with(resources) {
            val resId = getIdentifier("status_bar_height", "dimen", "android")
            if (resId > 0) getDimensionPixelSize(resId)
            else getDimensionPixelSize(R.dimen.default_status_bar_height)
        }
    }

    private lateinit var binding: ActivityPartBinding
    private val viewModel by viewModels<PartViewModel> {
        PartViewModelFactory(Repository.getInstance(applicationContext), partId)
    }

    private lateinit var layoutRoot: View
    private lateinit var statusBackground: View
    private lateinit var loadBar: ProgressBar
    private lateinit var toolbar: Toolbar
    private lateinit var readerContainer: FragmentContainerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_part)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        layoutRoot = findViewById(R.id.layout_root)
        statusBackground = findViewById(R.id.status_bar_background)
        loadBar = findViewById(R.id.load_bar)
        readerContainer = findViewById(R.id.reader_container)

        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        initialiseSystemBars()
        initialiseObserversListeners()
        setStatusBarTextColor(resources.getBoolean(R.bool.isLightMode))
        if (viewModel.partReady.value == true) transitionToContent()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_reader, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean  = when (item.itemId) {
        android.R.id.home -> {
            finish()
            true
        }
        R.id.settings_button -> {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    override fun onPause() {
        super.onPause()
        viewModel.saveProgress()
    }

    override fun onResume() {
        super.onResume()
        setNavigationBarVisibility(viewModel.showAppBar.value ?: false)
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
            setNavigationBarVisibility(it)
        }
        viewModel.margin.observe(this) {
            updatePageDimens()
        }
        viewModel.horizontalReader.observe(this) { isHorizontal ->
            when (isHorizontal) {
                true ->  setReaderFragment(PagedReaderFragment::class.java, "PagedReader")
                false -> setReaderFragment(ScrollReaderFragment::class.java, "ScrollReader")
            }
        }

        // Notify viewModel once the reader's dimensions are known
        readerContainer.post {
            updatePageDimens()
        }
    }

    private fun initialiseSystemBars() {
        // Make the activity fullscreen
        layoutRoot.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION)
        // Prevent content from being shown under the status bar via statusBackground
        statusBackground.layoutParams.height = statusBarHeight
        // Extend the app bar such that it's colour extends to under the status bar when shown
        toolbar.setPadding(0, statusBarHeight, 0, 0)
        toolbar.layoutParams.height += statusBarHeight
    }

    private fun setNavigationBarVisibility(visible: Boolean) {
        val visFlags = View.SYSTEM_UI_FLAG_IMMERSIVE or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        window.decorView.systemUiVisibility = if (visible) {
            (window.decorView.systemUiVisibility and visFlags.inv())
        } else {
            (window.decorView.systemUiVisibility or visFlags)
        }
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
                animator.doOnStart { toolbar.visibility = View.VISIBLE }
                animator.start()
            }
            else -> {
                setStatusBarTextColor(resources.getBoolean(R.bool.isLightMode))
                val animator = AnimatorInflater.loadAnimator(this, R.animator.hide_top_app_bar)
                animator.setTarget(toolbar)
                animator.doOnEnd { toolbar.visibility = View.GONE }
                animator.start()
            }
        }
    }

    private fun transitionToContent() {
        val loadBarAnimator = AnimatorInflater.loadAnimator(this, android.R.animator.fade_out)
        loadBarAnimator.setTarget(loadBar)
        loadBarAnimator.doOnEnd { loadBar.visibility = View.GONE }
        loadBarAnimator.start()

        val contentAnimator = AnimatorInflater.loadAnimator(this, R.animator.slide_from_bottom)
        contentAnimator.setTarget(readerContainer)
        contentAnimator.doOnStart { readerContainer.visibility = View.VISIBLE }
        contentAnimator.start()
    }

    private fun <T : Fragment> setReaderFragment(fragClass: Class<T>, tag: String) {
        val frag =
            supportFragmentManager.findFragmentByTag(tag) ?: fragClass.newInstance()
        supportFragmentManager.commit { replace(R.id.reader_container, frag, tag) }
    }

    private fun updatePageDimens() {
        var pageWidth = readerContainer.width
        var pageHeight = readerContainer.height

        val margins = viewModel.margin.value ?: return
        pageHeight -= Utils.dpToPx(margins.top, resources.displayMetrics)
        pageHeight -= Utils.dpToPx(margins.bottom, resources.displayMetrics)
        pageWidth -= Utils.dpToPx(margins.left, resources.displayMetrics)
        pageWidth -= Utils.dpToPx(margins.right, resources.displayMetrics)

        viewModel.setPageDimens(pageWidth, pageHeight)
    }
}
