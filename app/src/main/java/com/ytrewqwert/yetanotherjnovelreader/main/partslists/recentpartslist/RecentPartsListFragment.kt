package com.ytrewqwert.yetanotherjnovelreader.main.partslists.recentpartslist

import android.content.Intent
import androidx.fragment.app.viewModels
import com.ytrewqwert.yetanotherjnovelreader.common.RepositoriedViewModelFactory
import com.ytrewqwert.yetanotherjnovelreader.common.swipeablelist.SwipeableListFragment
import com.ytrewqwert.yetanotherjnovelreader.data.Repository
import com.ytrewqwert.yetanotherjnovelreader.data.local.database.part.PartFull
import com.ytrewqwert.yetanotherjnovelreader.main.partslists.PartsListAdapter
import com.ytrewqwert.yetanotherjnovelreader.partreader.PartActivity

/** SwipeableListFragment for showing recently released parts. */
class RecentPartsListFragment : SwipeableListFragment<PartFull>(), PartsListAdapter.Listener {

    override val listContentsAdapter by lazy {
        PartsListAdapter(this, this)
    }

    override val viewModel by viewModels<RecentPartsListViewModel> {
        RepositoriedViewModelFactory(Repository.getInstance(requireContext()))
    }

    override fun onPartClick(part: PartFull) {
        val intent = Intent(context, PartActivity::class.java)
        intent.putExtra(PartActivity.EXTRA_PART_ID, part.part.id)
        startActivity(intent)
    }

    override fun onPartFollowClick(part: PartFull) {
        viewModel.toggleFollow(part)
    }
}