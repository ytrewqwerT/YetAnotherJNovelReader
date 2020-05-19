package com.ytrewqwert.yetanotherjnovelreader.common

import android.graphics.Bitmap
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
import com.ytrewqwert.yetanotherjnovelreader.common.listheader.ListHeaderRecyclerViewAdapter
import com.ytrewqwert.yetanotherjnovelreader.common.listitem.ListItem
import com.ytrewqwert.yetanotherjnovelreader.common.listitem.ListItemRecyclerViewAdapter
import com.ytrewqwert.yetanotherjnovelreader.data.Repository

class ListItemFragment : Fragment(), ListItem.InteractionListener, ImageSource {
    companion object {
        private const val TAG = "ListItemFragment"
        const val ARG_ID = "${TAG}_ID"
    }

    private val viewModel by viewModels<ListItemViewModel>(
        ownerProducer = { parentFragment ?: requireActivity() },
        factoryProducer = { ListItemViewModelFactory(Repository.getInstance(requireContext())) }
    )

    private val uid by lazy { requireArguments().getInt(ARG_ID, 0) }

    private val listHeaderAdapter = ListHeaderRecyclerViewAdapter(this)
    private val listItemAdapter = ListItemRecyclerViewAdapter(this, this)
    private val recyclerViewAdapter = MergeAdapter(listHeaderAdapter, listItemAdapter)

    private var swipeRefreshLayout: SwipeRefreshLayout? = null
    private var recyclerView: RecyclerView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.getIsReloading(uid).observe(this) { swipeRefreshLayout?.isRefreshing = it }
        viewModel.getHeaderList(uid).observe(this) {
            if (it != null) listHeaderAdapter.setItems(it)
        }
        viewModel.getItemList(uid).observe(this) {
            if (it != null) listItemAdapter.setItems(it)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_listitem_list, container, false)
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh)
        recyclerView = view.findViewById(R.id.list)

        swipeRefreshLayout?.setOnRefreshListener { viewModel.setIsReloading(uid, true) }

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
        // Force redraw for potentially updated list_item progress values
        recyclerViewAdapter.notifyDataSetChanged()
    }


    override fun onClick(item: ListItem) {
        viewModel.listItemFragmentViewOnClick(uid, item)
    }

    override fun onFollowClick(item: ListItem) {
        viewModel.toggleFollowItem(item)
    }


    override fun getImage(source: String, callback: (String, Bitmap?) -> Unit) {
        viewModel.getImage(source, callback)
    }
}
