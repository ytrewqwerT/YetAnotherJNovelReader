package com.example.yetanotherjnovelreader.common


import android.os.Bundle
import android.text.Spanned
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.example.yetanotherjnovelreader.R
import com.example.yetanotherjnovelreader.data.RemoteRepository

class PartFragment : Fragment() {

    private val viewModel by activityViewModels<PartViewModel> {
        PartViewModel.PartViewModelFactory(RemoteRepository.getInstance(requireActivity().applicationContext), resources)
    }

    private var contentView: TextView? = null
    private val contentObserver by lazy {
        Observer<Spanned> {
            contentView?.text = it
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_part, container, false)
        contentView = view.findViewById(R.id.content_view)
        viewModel.getContents().observe(this, contentObserver)
        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.getContents().removeObserver(contentObserver)
    }
}