package com.example.yetanotherjnovelreader.common

import android.text.Spanned
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class PartViewModel : ViewModel() {
    val contents = MutableLiveData<Spanned>()
}