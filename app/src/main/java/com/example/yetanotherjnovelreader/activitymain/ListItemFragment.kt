package com.example.yetanotherjnovelreader.activitymain

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.observe
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.yetanotherjnovelreader.R
import com.example.yetanotherjnovelreader.common.CustomRecyclerViewAdapter
import com.example.yetanotherjnovelreader.common.ListItem

class ListItemFragment : Fragment(), ListItem.InteractionListener {
    companion object {
        private const val TAG = "ListItemFragment"
        const val ARG_ID = "${TAG}_ID"
    }

    private val viewModel by activityViewModels<ListItemViewModel>()
    private var uid = 0
    private var recyclerViewAdapter = CustomRecyclerViewAdapter(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        uid = arguments?.getInt(ARG_ID, 0) ?: 0
        viewModel.getItemList(uid).observe(this) {
            recyclerViewAdapter.setItems(it)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_listitem_list, container, false)

        if (view is RecyclerView) with (view) {
            layoutManager = LinearLayoutManager(context)
            adapter = recyclerViewAdapter
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        }
        return view
    }

    override fun onClick(item: ListItem) {
        viewModel.listItemFragmentViewOnClick(uid, item)
    }
}
