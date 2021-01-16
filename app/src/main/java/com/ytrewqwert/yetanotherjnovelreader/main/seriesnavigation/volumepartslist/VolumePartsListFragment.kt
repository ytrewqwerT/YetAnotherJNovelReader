package com.ytrewqwert.yetanotherjnovelreader.main.seriesnavigation.volumepartslist

import android.content.Intent
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.ytrewqwert.yetanotherjnovelreader.common.swipeablelist.SwipeableListFragment
import com.ytrewqwert.yetanotherjnovelreader.data.Repository
import com.ytrewqwert.yetanotherjnovelreader.data.local.database.part.PartFull
import com.ytrewqwert.yetanotherjnovelreader.main.MainViewModel
import com.ytrewqwert.yetanotherjnovelreader.partreader.PartActivity

/** SwipeableListFragment for showing the parts in a volume. */
class VolumePartsListFragment : SwipeableListFragment<PartFull>(), VolumePartsListAdapter.Listener {

    private val volumeId by lazy { arguments?.getString(ARG_VOLUME_ID) ?: "" }

    override val listContentsAdapter by lazy { VolumePartsListAdapter(this) }

    override val viewModel by viewModels<VolumePartsListViewModel> {
        VolumePartsListViewModelFactory(Repository.getInstance(requireContext()), volumeId)
    }
    private val mainViewModel by activityViewModels<MainViewModel>()

    override fun onPartClick(part: PartFull) {
        val intent = Intent(context, PartActivity::class.java)
        intent.putExtra(PartActivity.EXTRA_PART_ID, part.part.id)
        startActivity(intent)
    }

    override fun onFollowClick() {
        val serieId = viewModel.items.value?.firstOrNull()?.part?.serieId ?: return
        mainViewModel.toggleFollow(serieId)
    }

    companion object {
        const val ARG_VOLUME_ID = "VOLUME_ID"
    }
}