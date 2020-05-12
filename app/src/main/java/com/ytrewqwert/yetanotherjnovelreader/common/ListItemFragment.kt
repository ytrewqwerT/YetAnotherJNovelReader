package com.ytrewqwert.yetanotherjnovelreader.common

import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var recyclerView: RecyclerView

    override fun getImage(source: String, callback: (String, Bitmap?) -> Unit) {
        lifecycleScope.launch { callback(source, viewModel.getImage(source)) }
    }

    override fun onClick(item: ListItem) {
        viewModel.listItemFragmentViewOnClick(uid, item)
    }

    override fun onFollowClick(item: ListItem) {
        viewModel.toggleFollowItem(item)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        uid = requireArguments().getInt(ARG_ID, 0)

        viewModel.getIsReloading(uid).observe(this) { swipeRefreshLayout.isRefreshing = it }
        viewModel.getItemList(uid).observe(this) {
            if (it != null) recyclerViewAdapter.setItems(it)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_listitem_list, container, false)
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh)
        recyclerView = view.findViewById(R.id.list)

        swipeRefreshLayout.setOnRefreshListener {
            viewModel.setIsReloading(uid, true)
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
    }
}
