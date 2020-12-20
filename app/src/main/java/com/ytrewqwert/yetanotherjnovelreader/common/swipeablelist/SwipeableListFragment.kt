package com.ytrewqwert.yetanotherjnovelreader.common.swipeablelist

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.ytrewqwert.yetanotherjnovelreader.R
import com.ytrewqwert.yetanotherjnovelreader.common.ImageSource

/** A RecyclerView container fragment that allows for swipe-refreshing. */
abstract class SwipeableListFragment : Fragment(), ImageSource {

    /** The adapter that manages each item in the recycler view */
    protected abstract val recyclerViewAdapter: RecyclerView.Adapter<out RecyclerView.ViewHolder>
    /** Manages refreshes and image retrieval. */
    protected abstract val viewModel: SwipeableListViewModel

    private var swipeRefreshLayout: SwipeRefreshLayout? = null
    private var recyclerView: RecyclerView? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_swipable_list, container, false)
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh)
        recyclerView = view.findViewById(R.id.list)

        swipeRefreshLayout?.setOnRefreshListener { viewModel.refresh() }
        recyclerView?.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = recyclerViewAdapter
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        }

        viewModel.refreshing.observe(viewLifecycleOwner) {
            swipeRefreshLayout?.isRefreshing = it
        }

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        swipeRefreshLayout = null
        recyclerView = null
    }

    final override fun getImage(source: String, callback: (source: String, image: Drawable?) -> Unit) {
        viewModel.getImage(source, callback)
    }
}