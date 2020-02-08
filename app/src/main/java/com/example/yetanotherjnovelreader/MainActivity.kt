package com.example.yetanotherjnovelreader

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.yetanotherjnovelreader.data.RemoteRepository
import com.example.yetanotherjnovelreader.data.Series

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        val recyclerViewAdapter = CustomRecyclerViewAdapter<Series>()
        recyclerView.adapter = recyclerViewAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        val repository = RemoteRepository.getInstance(applicationContext)
        repository.getSeries { recyclerViewAdapter.setItems(it) }
    }
}