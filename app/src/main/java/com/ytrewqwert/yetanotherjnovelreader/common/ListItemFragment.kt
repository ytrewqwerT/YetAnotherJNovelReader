package com.ytrewqwert.yetanotherjnovelreader.common

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.MergeAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.ytrewqwert.yetanotherjnovelreader.R
import com.ytrewqwert.yetanotherjnovelreader.common.listfooter.ListFooter
import com.ytrewqwert.yetanotherjnovelreader.common.listfooter.ListFooterRecyclerViewAdapter
import com.ytrewqwert.yetanotherjnovelreader.common.listheader.ListHeader
import com.ytrewqwert.yetanotherjnovelreader.common.listheader.ListHeaderRecyclerViewAdapter
import com.ytrewqwert.yetanotherjnovelreader.common.listitem.ListItem
import com.ytrewqwert.yetanotherjnovelreader.common.listitem.ListItemRecyclerViewAdapter
import com.ytrewqwert.yetanotherjnovelreader.data.Repository

/**
 * Displays a combined list of [ListHeader]s, [ListItem]s, and [ListFooter]s provided by an
 * external source.
 *
 * An argument, [ARG_ID], must be provided identifying this [ListItemFragment]'s ID so that it knows
 * which data to retrieve from the [ListItemViewModel].
 */
class ListItemFragment : Fragment(),
    ListItem.InteractionListener, ImageSource, ListFooter.InteractionListener {

    companion object {
        private const val TAG = "ListItemFragment"

        /** An identifier for which data to retrieve from the [ListItemViewModel]. */
        const val ARG_ID = "${TAG}_ID"
    }

    private val viewModel by viewModels<ListItemViewModel>(
        ownerProducer = { parentFragment ?: requireActivity() },
        factoryProducer = { ListItemViewModelFactory(Repository.getInstance(requireContext())) }
    )

    private val uid by lazy { requireArguments().getInt(ARG_ID, 0) }

    private val listHeaderAdapter = ListHeaderRecyclerViewAdapter(this)
    private val listItemAdapter = ListItemRecyclerViewAdapter(this, this)
    private val listFooterAdapter = ListFooterRecyclerViewAdapter(this)
    private val recyclerViewAdapter = MergeAdapter(listHeaderAdapter, listItemAdapter, listFooterAdapter)

    private var swipeRefreshLayout: SwipeRefreshLayout? = null
    private var recyclerView: RecyclerView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.getHeaderList(uid).observe(this) {
            if (it != null) listHeaderAdapter.setItems(it)
        }
        viewModel.getItemList(uid).observe(this) {
            if (it != null) listItemAdapter.setItems(it)
        }
        viewModel.getIsReloading(uid).observe(this) {
            swipeRefreshLayout?.isRefreshing = it
        }
        viewModel.getHasMorePages(uid).observe(this) {
            listFooterAdapter.isVisible = it
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_listitem_list, container, false)
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh)
        recyclerView = view.findViewById(R.id.list)

        swipeRefreshLayout?.setOnRefreshListener { viewModel.reload(uid) }
        recyclerView?.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = recyclerViewAdapter
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        }

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        swipeRefreshLayout = null
        recyclerView = null
    }

    override fun onResume() {
        super.onResume()
        // Force redraw for potentially updated list_item progress values, possible when resuming
        // after the user returns from PartActivity
        recyclerViewAdapter.notifyDataSetChanged()
    }

    override fun onClick(item: ListItem) { viewModel.listItemFragmentViewOnClick(uid, item) }
    override fun onFollowClick(item: ListItem) { viewModel.toggleFollowItem(item) }
    override fun onFooterReached() { viewModel.fetchNextPage(uid) }
    override fun getImage(source: String, callback: (String, Drawable?) -> Unit) {
        viewModel.getImage(source, callback)
    }

}
