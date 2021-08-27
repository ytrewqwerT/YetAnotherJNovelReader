package com.ytrewqwert.yetanotherjnovelreader.main.seriesnavigation

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.ytrewqwert.yetanotherjnovelreader.R
import com.ytrewqwert.yetanotherjnovelreader.data.local.database.serie.SerieFull
import com.ytrewqwert.yetanotherjnovelreader.data.local.database.volume.VolumeFull
import com.ytrewqwert.yetanotherjnovelreader.main.seriesnavigation.serieslist.SeriesListFragment
import com.ytrewqwert.yetanotherjnovelreader.main.seriesnavigation.serievolumeslist.SerieVolumesListFragment
import com.ytrewqwert.yetanotherjnovelreader.main.seriesnavigation.volumepartslist.VolumePartsListFragment

/** Displays lists of series, volumes and parts allowing for exploration of available content. */
class ExplorerFragment : Fragment() {
    companion object {
        private const val TAG = "NavigationFragment"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setChildFragment(SeriesListFragment::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_explorer, container, false)
    }

    override fun onResume() {
        super.onResume()
        // Propagate to child fragment
        val curFragment = childFragmentManager.findFragmentById(R.id.fragment_container)
        curFragment?.onResume()
    }

    fun onSeriesListItemInteraction(serie: SerieFull) {
        Log.d(TAG, "Series clicked: ${serie.serie.title}")
        onSeriesListItemInteraction(serie.serie.id)
    }
    fun onSeriesListItemInteraction(serieId: String) {
        setChildFragment(
            SerieVolumesListFragment::class.java,
            bundleOf(SerieVolumesListFragment.ARG_SERIE_ID to serieId)
        )
    }

    fun onVolumesListItemInteraction(volume: VolumeFull) {
        Log.d(TAG, "Volume clicked: ${volume.volume.title}")
        onVolumesListItemInteraction(volume.volume.id)
    }
    fun onVolumesListItemInteraction(volumeId: String) {
        setChildFragment(
            VolumePartsListFragment::class.java,
            bundleOf(VolumePartsListFragment.ARG_VOLUME_ID to volumeId)
        )
    }

    private fun setChildFragment(fragmentClass: Class<out Fragment>, args: Bundle? = null) {
        childFragmentManager.commit {
            setCustomAnimations(
                R.animator.slide_from_right, R.animator.slide_to_left,
                R.animator.slide_from_left, R.animator.slide_to_right
            )
            replace(R.id.fragment_container, fragmentClass, args)
            if (childFragmentManager.findFragmentById(R.id.fragment_container) != null) {
                addToBackStack(null)
            }
        }
    }
}
