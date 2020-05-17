package com.ytrewqwert.yetanotherjnovelreader.partreader

import android.content.res.Resources
import android.graphics.Typeface
import android.graphics.drawable.BitmapDrawable
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ImageSpan
import androidx.lifecycle.*
import com.ytrewqwert.yetanotherjnovelreader.SingleLiveEvent
import com.ytrewqwert.yetanotherjnovelreader.Utils
import com.ytrewqwert.yetanotherjnovelreader.data.Repository
import com.ytrewqwert.yetanotherjnovelreader.data.local.preferences.PreferenceStore
import com.ytrewqwert.yetanotherjnovelreader.scaleToWidth
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class PartViewModel(
    private val repository: Repository,
    private val resources: Resources,
    private val partId: String
) : ViewModel() {
    var pageWidthPx: Int private set
    var pageHeightPx: Int private set
    val fontSizePx: Int

    val errorEvent = SingleLiveEvent<String>()
    val partReady = SingleLiveEvent<Boolean>()
    val gotoProgressEvent = SingleLiveEvent<Boolean>()
    val showAppBar = SingleLiveEvent<Boolean>()

    private var contentsNoImages: Spanned? = null
    private val _contents = MutableLiveData<Spanned>()
    val contents: LiveData<Spanned> get() = _contents

    private val _horizontalReader = MutableLiveData<Boolean>()
    private val _fontSize = MutableLiveData<Int>()
    private val _fontStyle = MutableLiveData<Typeface>()
    private val _margin = MutableLiveData<PreferenceStore.Margins>()
    val horizontalReader: LiveData<Boolean> = _horizontalReader
    val fontSize: LiveData<Int> = _fontSize
    val fontStyle: LiveData<Typeface> = _fontStyle
    val margin: LiveData<PreferenceStore.Margins> = _margin

    private var savedProgress = 0.0
    val currentProgress = MutableLiveData(0.0)
    val progressText: LiveData<String> = Transformations.map(currentProgress) {
        "${(100*it).toInt()}%"
    }

    init {
        pageWidthPx = 0
        pageHeightPx = 0

        val displayMetrics = resources.displayMetrics
        val fontSizeSp = fontSize.value ?: 15
        fontSizePx = Utils.spToPx(fontSizeSp, displayMetrics)

        viewModelScope.launch {
            getPartData()
            savedProgress = repository.getParts(partId).getOrNull(0)?.progress?.progress ?: 0.0
            currentProgress.value = savedProgress
            partReady.value = true
        }

        viewModelScope.launch {
            repository.getReaderSettingsFlow().collect {
                _horizontalReader.value = it.isHorizontal
                _fontSize.value = it.fontSize
                _fontStyle.value = it.fontStyle
                _margin.value = it.margin
            }
        }
    }

    fun toggleAppBarVisibility() {
        showAppBar.value = !(showAppBar.value ?: false)
    }

    fun setPageDimens(widthPx: Int, heightPx: Int) {
        pageWidthPx = widthPx
        pageHeightPx = heightPx
        // Update contents to have correctly sized images
        viewModelScope.launch {
            _contents.value = replaceTempImages(contentsNoImages ?: return@launch)
        }
    }

    fun uploadProgressNow() {
        // Only upload if the value has changed since last upload
        val progress = currentProgress.value ?: 0.0
        if (progress != savedProgress) {
            viewModelScope.launch { repository.setPartProgress(partId, progress) }
        }
    }

    private suspend fun getPartData() {
        contentsNoImages = repository.getPartContent(partId)
        if (contentsNoImages != null) {
            _contents.value = replaceTempImages(contentsNoImages ?: return)
        } else errorEvent.value = "Failed to get part data"
    }

    private suspend fun replaceTempImages(spanned: Spanned): Spanned {
        val spanBuilder = SpannableStringBuilder(spanned)
        coroutineScope {
            for (img in spanBuilder.getSpans(0, spanned.length, ImageSpan::class.java)) {
                launch { replaceTempImage(img, spanBuilder) }
            }
        }
        return spanBuilder
    }

    private suspend fun replaceTempImage(img: ImageSpan, spanBuilder: SpannableStringBuilder) {
        val bitmap = repository.getImage(img.source ?: return)
        val drawable = BitmapDrawable(resources, bitmap)
        drawable.scaleToWidth(pageWidthPx)
        val newImg = ImageSpan(drawable)

        synchronized(spanBuilder) {
            val start = spanBuilder.getSpanStart(img)
            val end = spanBuilder.getSpanEnd(img)
            spanBuilder.removeSpan(img)
            spanBuilder.setSpan(newImg, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
    }
}
