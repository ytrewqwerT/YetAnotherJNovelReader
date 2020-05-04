package com.ytrewqwert.yetanotherjnovelreader.common

import android.animation.AnimatorInflater
import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.core.animation.addListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.observe
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.ytrewqwert.yetanotherjnovelreader.R
import com.ytrewqwert.yetanotherjnovelreader.data.Repository
import kotlinx.coroutines.launch

class ListItemFragment : Fragment(), ListItem.InteractionListener,
    ImageSource {
    companion object {
        private const val TAG = "ListItemFragment"
        const val ARG_ID = "${TAG}_ID"
    }

    private val viewModel by viewModels<ListItemViewModel>(
        ownerProducer = { parentFragment ?: requireActivity() },
        factoryProducer = { ListItemViewModelFactory(Repository.getInstance(requireContext())) }
    )

    private var uid = 0
    private var recyclerViewAdapter = CustomRecyclerViewAdapter(this, this)

    private lateinit var loadBar: RelativeLayout
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var recyclerView: RecyclerView

    private var isLoading = true

    override fun getImage(source: String, callback: (Bitmap?) -> Unit) {
        lifecycleScope.launch { callback(viewModel.getImage(source)) }
    }

    override fun onClick(item: ListItem) {
        viewModel.listItemFragmentViewOnClick(uid, item)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        uid = requireArguments().getInt(ARG_ID, 0)

        viewModel.getItemList(uid).observe(this) {
            when (it) {
                null -> transitionToLoading()
                else -> {
                    recyclerViewAdapter.setItems(it)
                    transitionToContent()
                }
            }

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_listitem_list, container, false)
        loadBar = view.findViewById(R.id.load_bar)
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh)
        recyclerView = view.findViewById(R.id.list)

        swipeRefreshLayout.setOnRefreshListener {
            swipeRefreshLayout.isRefreshing = false
            // Content manager should observe itemList and update its value if null.
            viewModel.setItemList(uid, null)
        }

        with (recyclerView) {
            layoutManager = LinearLayoutManager(context)
            adapter = recyclerViewAdapter
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        }

        return view
    }

    override fun onResume() {
        super.onResume()
        // Force redraw for potentially updated recycler_item progress values
        recyclerViewAdapter.notifyDataSetChanged()
        // Pausing switches back to the initial loading view for some reason, so switch it back
        if (viewModel.getItemList(uid).value?.isNotEmpty() == true) {
            loadBar.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
        }
    }

    private fun transitionToContent() {
        if (!isLoading) return
        isLoading = false
        val scrollViewAnimator =
            AnimatorInflater.loadAnimator(context, R.animator.slide_from_right)
        scrollViewAnimator.setTarget(recyclerView)
        scrollViewAnimator.addListener(onStart = {
            recyclerView.visibility = View.VISIBLE
        })
        scrollViewAnimator.start()

        val loadBarAnimator =
            AnimatorInflater.loadAnimator(context, android.R.animator.fade_out)
        loadBarAnimator.setTarget(loadBar)
        loadBarAnimator.addListener(onEnd = {
            loadBar.visibility = View.GONE
        })
        loadBarAnimator.start()
    }

    private fun transitionToLoading() {
        if (isLoading) return
        isLoading = true
        val loadBarAnimator =
            AnimatorInflater.loadAnimator(context, R.animator.slide_from_top)
        loadBarAnimator.setTarget(loadBar)
        loadBarAnimator.addListener(onStart = { loadBar.visibility = View.VISIBLE })
        loadBarAnimator.start()

        val scrollViewAnimator =
            AnimatorInflater.loadAnimator(context, R.animator.slide_to_bottom)
        scrollViewAnimator.setTarget(recyclerView)
        scrollViewAnimator.addListener(onEnd = {
            recyclerView.visibility = View.GONE
        })
        scrollViewAnimator.start()
    }
}
