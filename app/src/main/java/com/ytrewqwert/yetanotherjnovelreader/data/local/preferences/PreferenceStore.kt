package com.ytrewqwert.yetanotherjnovelreader.data.local.preferences

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Typeface
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.preference.PreferenceManager
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.ytrewqwert.yetanotherjnovelreader.data.local.database.UserData
import com.ytrewqwert.yetanotherjnovelreader.setBoolean
import com.ytrewqwert.yetanotherjnovelreader.setString
import java.time.Instant
import java.time.Period

class PreferenceStore private constructor(private val appContext: Context)
    : SharedPreferences.OnSharedPreferenceChangeListener {

    companion object {
        @Volatile
        private var INSTANCE: PreferenceStore? = null
        fun getInstance(context: Context): PreferenceStore = INSTANCE ?: synchronized(this) {
            INSTANCE ?: PreferenceStore(context.applicationContext).also { INSTANCE = it }
        }
    }

    private val sharedPref = PreferenceManager.getDefaultSharedPreferences(appContext)
    private val encryptedPreferences: SharedPreferences

    init {
        val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
        encryptedPreferences = EncryptedSharedPreferences.create(
            "encrypted_preferences", masterKeyAlias, appContext,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )

        sharedPref.registerOnSharedPreferenceChangeListener(this)
    }

    // User login details
    var email: String?
        get() = sharedPref.getString(PrefKeys.EMAIL, null)
        set(value) = sharedPref.setString(PrefKeys.EMAIL, value)
    var password: String?
        get() = encryptedPreferences.getString(PrefKeys.PASSWORD, null)
        set(value) = encryptedPreferences.setString(PrefKeys.PASSWORD, value)

    // User account details
    var userId: String?
        get() = sharedPref.getString(PrefKeys.USER_ID, null)
        set(value) = sharedPref.setString(PrefKeys.USER_ID, value)
    var authToken: String?
        get() = sharedPref.getString(PrefKeys.AUTH_TOKEN, null)
        set(value) = sharedPref.setString(PrefKeys.AUTH_TOKEN, value)
    var authDate: String?
        get() = sharedPref.getString(PrefKeys.AUTH_DATE, null)
        set(value) = sharedPref.setString(PrefKeys.AUTH_DATE, value)
    var username: String?
        get() = sharedPref.getString(PrefKeys.USERNAME, null)
        set(value) = sharedPref.setString(PrefKeys.USERNAME, value)
    var isMember: Boolean?
        get() = sharedPref.getBoolean(PrefKeys.IS_MEMBER, false)
        set(value) = sharedPref.setBoolean(PrefKeys.IS_MEMBER, value ?: false)

    // Non-reader settings
    var isFilterFollowing: Boolean
        get() = sharedPref.getBoolean(PrefKeys.IS_FOLLOW, false)
        set(value) = sharedPref.setBoolean(PrefKeys.IS_FOLLOW, value)

    // Reader settings
    private val _horizontalReader =
        MutableLiveData(sharedPref.getBoolean(PrefKeys.IS_HORIZONTAL, PrefDefaults.IS_HORIZONTAL))
    private val _fontSize = MutableLiveData(sharedPref.getInt(PrefKeys.FONT_SIZE, PrefDefaults.FONT_SIZE))
    private val _fontStyle = MutableLiveData<Typeface>()
    private val _readerMargin = MutableLiveData(getMargins())
    val horizontalReader: LiveData<Boolean> = _horizontalReader
    val fontSize: LiveData<Int> = _fontSize
    val fontStyle: LiveData<Typeface> = _fontStyle
    val readerMargin: LiveData<Margins> = _readerMargin

    fun authExpired(): Boolean {
        if (authToken == null) return true
        val authInstant = Instant.parse(authDate)
        val cutoffInstant = Instant.now().minus(Period.ofDays(14))
        return authInstant < cutoffInstant
    }

    fun clearUserData() {
        userId = null
        authToken = null
        authDate = null
        username = null
        isMember = false
    }
    fun setUserData(userData: UserData?) {
        userId = userData?.userId
        authToken = userData?.authToken
        authDate = userData?.authDate
        username = userData?.username
        isMember = userData?.isMember
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        with (PrefKeys) {
            when (key) {
                IS_HORIZONTAL -> {
                    _horizontalReader.value =
                        sharedPref.getBoolean(IS_HORIZONTAL, PrefDefaults.IS_HORIZONTAL)
                }
                FONT_SIZE -> {
                    _fontSize.value = sharedPref.getInt(FONT_SIZE, PrefDefaults.FONT_SIZE)
                }
                FONT_STYLE -> {
                    _fontStyle.value = updateTypeface()
                }
                MARGIN_TOP, MARGIN_BOTTOM, MARGIN_LEFT, MARGIN_RIGHT -> {
                    _readerMargin.value = getMargins()
                }
            }
        }
    }

    private fun updateTypeface(): Typeface {
        val styleString = sharedPref.getString(PrefKeys.FONT_STYLE, "default")!!
        return when (styleString) {
            "default" -> Typeface.defaultFromStyle(Typeface.NORMAL)

            // Recreating every time the style is requested is probably a bad idea
            else -> Typeface.createFromAsset(appContext.assets, "fonts/$styleString")
        }
    }

    private fun getMargins(): Margins {
        val top = sharedPref.getInt(PrefKeys.MARGIN_TOP, PrefDefaults.MARGIN)
        val bottom = sharedPref.getInt(PrefKeys.MARGIN_BOTTOM, PrefDefaults.MARGIN)
        val left = sharedPref.getInt(PrefKeys.MARGIN_LEFT, PrefDefaults.MARGIN)
        val right = sharedPref.getInt(PrefKeys.MARGIN_RIGHT, PrefDefaults.MARGIN)
        return Margins(
            top,
            bottom,
            left,
            right
        )
    }

    data class Margins(val top: Int, val bottom: Int, val left: Int, val right: Int)
}