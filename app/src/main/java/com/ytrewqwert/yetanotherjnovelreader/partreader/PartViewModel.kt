package com.ytrewqwert.yetanotherjnovelreader.partreader

import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ImageSpan
import androidx.lifecycle.*
import com.ytrewqwert.yetanotherjnovelreader.SingleLiveEvent
import com.ytrewqwert.yetanotherjnovelreader.data.Repository
import com.ytrewqwert.yetanotherjnovelreader.data.htmlparser.PartHtmlParser
import com.ytrewqwert.yetanotherjnovelreader.data.local.preferences.ReaderPreferenceStore
import com.ytrewqwert.yetanotherjnovelreader.scaleToSize
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

/**
 * Exposes data for the [PartActivity].
 *
 * @property[partId] The ID of the part to be shown in the [PartActivity].
 */
class PartViewModel(
    private val repository: Repository,
    private val partId: String
) : ViewModel() {
    /** The width of the page in which the part's text can be drawn. */
    private var pageWidthPx: Int
    /** The height of the page in which the part's text can be drawn. */
    private var pageHeightPx: Int

    /** Notifies an observer of when something goes wrong, with a message of what went wrong. */
    val errorEvent = SingleLiveEvent<String>()
    /** Notifies an observer of whether the part's content is ready to be displayed to the user. */
    val partReady = SingleLiveEvent<Boolean>()
    /** Notifies an observer of whether the top app bar should be shown. */
    val showAppBar = SingleLiveEvent<Boolean>()
    /** Notifies an observer to go forward/back a page. */
    val pageTurn = SingleLiveEvent<PageTurn>()

    /** Enum for the different page turn events possible. */
    enum class PageTurn { TURN_FORWARD, TURN_BACKWARD }

    private var contentsHtml: String? = null
    private var contentsNoImages: Spanned? = null
    private val _contents = MutableLiveData<Spanned>()
    /** The content of the part, to be displayed to the user. */
    val contents: LiveData<Spanned> get() = _contents

    private val _horizontalReader = MutableLiveData<Boolean>()
    private val _fontSize = MutableLiveData<Int>()
    private val _fontStyle = MutableLiveData<Typeface>()
    private val _marginsDp = MutableLiveData<ReaderPreferenceStore.Margins>()
    private val _lineSpacing = MutableLiveData<Float>()
    /** Whether the activity should use a paginated, horizontally scrolling reader. */
    val horizontalReader: LiveData<Boolean> = _horizontalReader
    /** The size to make the reader's normal text, in sp. */
    val fontSize: LiveData<Int> = _fontSize
    /** The typeface to use in drawing the text in the reader. */
    val fontStyle: LiveData<Typeface> = _fontStyle
    /** How much space to leave around each edge of the reader. */
    val marginsDp: LiveData<ReaderPreferenceStore.Margins> = _marginsDp
    /** A multiplier for the spacing to place between lines. */
    val lineSpacing: LiveData<Float> = _lineSpacing

    private var savedProgress = 0.0
    /** The user's current reading progress through the part. */
    val currentProgress = MutableLiveData(0.0)
    /** A String representation of [currentProgress] (for databinding). */
    val progressText: LiveData<String> = Transformations.map(currentProgress) {
        "${(100*it).toInt()}%"
    }

    init {
        pageWidthPx = 0
        pageHeightPx = 0

        viewModelScope.launch {
            getPartData()
            savedProgress = repository.getParts(partId).getOrNull(0)?.progress?.progress ?: 0.0
            currentProgress.value = savedProgress
            partReady.value = true
        }

        viewModelScope.launch {
            var skipped = false
            repository.getReaderSettingsFlow().collect {
                _horizontalReader.value = it.isHorizontal
                _fontSize.value = it.fontSize
                _fontStyle.value = it.fontStyle
                _marginsDp.value = it.marginsDp
                _lineSpacing.value = it.lineSpacing

                if (!skipped) skipped = true // Skip first since it's called by getPartData() above.
                else processContentsHtml()
            }
        }
    }

    fun toggleAppBarVisibility() {
        showAppBar.value = !(showAppBar.value ?: false)
    }

    /** Sets the dimensions of the drawable region for a single page. */
    fun setPageDimens(widthPx: Int, heightPx: Int) {
        pageWidthPx = widthPx
        pageHeightPx = heightPx
        // Update contents to have correctly sized images
        viewModelScope.launch {
            _contents.value = replaceTempImages(contentsNoImages ?: return@launch)
        }
    }

    /** Saves the current value of [currentProgress] to the database. */
    fun saveProgress() {
        // Only upload if the value has changed since last save
        val progress = currentProgress.value ?: 0.0
        if (progress != savedProgress) {
            viewModelScope.launch { repository.setPartProgress(partId, progress) }
            savedProgress = progress
        }
    }

    /**
     * Executes an action based on where a screen tap occurred, with the [xPos] and [yPos] being
     * proportions along the screen from the top left corner indicating the tap location.
     */
    fun processScreenTap(xPos: Float, yPos: Float) {
        // TODO: Utilise yPos?
        val pageTurnAreasPc = repository.getReaderSettings().pageTurnAreasPc
        val leftBoundPx = pageTurnAreasPc.left.toFloat() / 100
        val rightBoundPx = (100 - pageTurnAreasPc.right).toFloat() / 100
        when {
            xPos < leftBoundPx  -> pageTurn.value = PageTurn.TURN_BACKWARD
            xPos > rightBoundPx -> pageTurn.value = PageTurn.TURN_FORWARD
            else -> toggleAppBarVisibility()
        }
    }

    private suspend fun getPartData() {
        contentsHtml = repository.getPartContent(partId)
        processContentsHtml()
    }

    private suspend fun processContentsHtml() {
        val curContents = contentsHtml
        if (curContents != null) {
            contentsNoImages = PartHtmlParser.parse(curContents, partId)
            if (contentsNoImages != null) {
                _contents.value = replaceTempImages(contentsNoImages ?: return)
            } else errorEvent.value = "Failed to process part data"
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
        val drawable = repository.getImage(img.source ?: return) ?:return
        drawable.scaleToSize(pageWidthPx, pageHeightPx)
        val newImg = ImageSpan(drawable)

        synchronized(spanBuilder) {
            val start = spanBuilder.getSpanStart(img)
            val end = spanBuilder.getSpanEnd(img)
            spanBuilder.removeSpan(img)
            spanBuilder.setSpan(newImg, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
    }
}
