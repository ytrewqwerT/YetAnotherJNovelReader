package com.ytrewqwert.yetanotherjnovelreader.partreader.pagedreader

import android.annotation.SuppressLint
import android.content.res.Resources
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import com.ytrewqwert.yetanotherjnovelreader.R
import com.ytrewqwert.yetanotherjnovelreader.Utils
import com.ytrewqwert.yetanotherjnovelreader.databinding.FragmentPagedReaderPageBinding
import com.ytrewqwert.yetanotherjnovelreader.partreader.PartViewModel
import com.ytrewqwert.yetanotherjnovelreader.partreader.TapListener

/** An individual page in the [PagedReaderFragment]. */
class PagedReaderPageFragment : Fragment() {
    companion object {
        const val ARG_PAGE_NUM = "PAGED_READER_PAGE_NUM"
    }

    // Delayed lifecycle that only triggers LiveData after the fragment has RESUMED.
    private val delayedLifecycleOwner = object : LifecycleOwner {
        val lifecycle = LifecycleRegistry(this)
        override fun getLifecycle(): Lifecycle = lifecycle
    }

    private val partViewModel by activityViewModels<PartViewModel>()
    private val pagedReaderViewModel by viewModels<PagedReaderViewModel>(
        ownerProducer = { requireParentFragment() }
    )

    private var binding: FragmentPagedReaderPageBinding? = null
    private var textView: TextView? = null

    override fun onResume() {
        super.onResume()
        delayedLifecycleOwner.lifecycle.currentState = Lifecycle.State.RESUMED
    }

    override fun onPause() {
        delayedLifecycleOwner.lifecycle.currentState = Lifecycle.State.CREATED
        super.onPause()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Receive via a delayed lifecycle to ensure that the posted code will be executed
        pagedReaderViewModel.fullContent.observe(delayedLifecycleOwner) {
            textView?.post {
                val marginsDp = partViewModel.marginsDp.value
                val vMargin = if (marginsDp != null) {
                    Utils.dpToPx(marginsDp.bottom + marginsDp.top, resources.displayMetrics)
                } else 0
                val viewHeight = view?.height ?: 0
                val pageHeight = (viewHeight - vMargin).coerceAtLeast(0)

                val pages = Paginator.paginate(textView, it, pageHeight)
                pagedReaderViewModel.setPages(pages)
            } ?: throw IllegalStateException("No view set")
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_paged_reader_page, container, false
        )
        binding?.lifecycleOwner = viewLifecycleOwner
        binding?.viewModel = partViewModel

        val view = binding?.root
        textView = view?.findViewById(R.id.page_contents)

        // Detect Screen taps and send the tap location to be processed.
        textView?.setOnTouchListener(TapListener(requireContext()) { event ->
            val screenHeight = Resources.getSystem().displayMetrics.heightPixels
            val screenWidth = Resources.getSystem().displayMetrics.widthPixels
            // The values sometimes go slightly over 1.0, so coerce to the expected range.
            val x = ((event?.rawX ?: 0f) / screenWidth).coerceIn(0f, 1f)
            val y = ((event?.rawY ?: 0f) / screenHeight).coerceIn(0f, 1f)
            partViewModel.processScreenTap(x, y)
            true
        })

        val pageNum = requireArguments().getInt(ARG_PAGE_NUM, 0)
        pagedReaderViewModel.getPageContent(pageNum).observe(viewLifecycleOwner) {
            textView?.text = it
        }

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        textView = null
    }
}
