package com.example.yetanotherjnovelreader.activitypart

import android.os.Bundle
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.observe
import com.example.yetanotherjnovelreader.R
import com.example.yetanotherjnovelreader.data.Repository

class PartActivity : AppCompatActivity() {
    companion object {
        const val EXTRA_PART_ID = "PART_ID"
    }

    var partId: String = ""
    private val viewModel by viewModels<PartViewModel> {
        PartViewModel.PartViewModelFactory(Repository.getInstance(applicationContext), resources, partId)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_part)

        partId = intent.getStringExtra(EXTRA_PART_ID)

        val mainTextView = findViewById<TextView>(R.id.content_view)
        viewModel.contents.observe(this) { mainTextView.text = it }
    }
}
