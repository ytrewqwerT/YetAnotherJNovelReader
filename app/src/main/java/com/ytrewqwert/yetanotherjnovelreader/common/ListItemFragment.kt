package com.ytrewqwert.yetanotherjnovelreader.common

import android.animation.AnimatorInflater
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.core.animation.addListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ytrewqwert.yetanotherjnovelreader.R

class ListItemFragment : Fragment(),
    ListItem.InteractionListener {
    companion object {
        private const val TAG = "ListItemFragment"
        const val ARG_ID = "${TAG}_ID"
    }

    private val viewModel by lazy {
        // If a parent fragment exists, let it manage this fragment's contents instead of activity.
        ViewModelProvider(parentFragment ?: requireActivity())[ListItemViewModel::class.java]
    }
    private var uid = 0
    private var recyclerViewAdapter =
        CustomRecyclerViewAdapter(
            this
        )

    private lateinit var loadBar: ProgressBar
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        uid = requireArguments().getInt(ARG_ID, 0)
        viewModel.getItemList(uid).observe(this) {
            recyclerViewAdapter.setItems(it)
            transitionToContent()
        }
    }

    override fun onResume() {
        super.onResume()
        // Force redraw for potentially updated recycler_item progress values
        recyclerViewAdapter.notifyDataSetChanged()

        if (viewModel.getItemList(uid).value?.isNotEmpty() == true) {
            loadBar.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_listitem_list, container, false)
        loadBar = view.findViewById(R.id.load_bar)
        recyclerView = view.findViewById(R.id.list)
        with (recyclerView) {
            layoutManager = LinearLayoutManager(context)
            adapter = recyclerViewAdapter
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        }
        return view
    }

    override fun onClick(item: ListItem) {
        viewModel.listItemFragmentViewOnClick(uid, item)
    }

    private fun transitionToContent() {
        val loadBarAnimator = AnimatorInflater.loadAnimator(context, android.R.animator.fade_out)
        loadBarAnimator.setTarget(loadBar)
        loadBarAnimator.addListener(onEnd = { loadBar.visibility = View.GONE })
        loadBarAnimator.start()

        val scrollViewAnimator = AnimatorInflater.loadAnimator(context, R.animator.slide_from_right)
        scrollViewAnimator.setTarget(recyclerView)
        scrollViewAnimator.addListener(onStart = { recyclerView.visibility = View.VISIBLE })
        scrollViewAnimator.start()
    }
}