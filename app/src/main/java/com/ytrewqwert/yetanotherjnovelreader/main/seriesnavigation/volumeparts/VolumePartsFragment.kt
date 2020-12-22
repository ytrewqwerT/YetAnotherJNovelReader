package com.ytrewqwert.yetanotherjnovelreader.main.seriesnavigation.volumeparts

import android.content.Intent
import androidx.fragment.app.viewModels
import com.ytrewqwert.yetanotherjnovelreader.common.swipeablelist.SwipeableListFragment
import com.ytrewqwert.yetanotherjnovelreader.data.Repository
import com.ytrewqwert.yetanotherjnovelreader.data.local.database.part.PartFull
import com.ytrewqwert.yetanotherjnovelreader.partreader.PartActivity

class VolumePartsFragment : SwipeableListFragment<PartFull>(), VolumePartsAdapter.Listener {

    private val volumeId by lazy { arguments?.getString(ARG_VOLUME_ID) ?: "" }

    override val listContentsAdapter by lazy { VolumePartsAdapter(this) }

    override val viewModel by viewModels<VolumePartsViewModel> {
        VolumePartsViewModelFactory(Repository.getInstance(requireContext()), volumeId)
    }

    override fun onPartClick(part: PartFull) {
        val intent = Intent(context, PartActivity::class.java)
        intent.putExtra(PartActivity.EXTRA_PART_ID, part.part.id)
        startActivity(intent)
    }

    override fun onFollowClick() {
        viewModel.toggleFollow()
    }

    companion object {
        const val ARG_VOLUME_ID = "VOLUME_ID"
    }
}