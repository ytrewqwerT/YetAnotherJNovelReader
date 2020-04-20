package com.ytrewqwert.yetanotherjnovelreader.data.local

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Typeface
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.preference.PreferenceManager
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.ytrewqwert.yetanotherjnovelreader.setBoolean
import com.ytrewqwert.yetanotherjnovelreader.setString
import org.json.JSONObject
import java.time.Instant
import java.time.Period


class PreferenceStore private constructor(private val appContext: Context)
    : SharedPreferences.OnSharedPreferenceChangeListener {

    companion object {
        private const val EMAIL_KEY = "EMAIL"
        private const val PASSWORD_KEY = "PASSWORD"

        private const val USER_ID_KEY = "USER_ID"
        private const val AUTH_TOKEN_KEY = "AUTHENTICATION_TOKEN"
        private const val AUTH_DATE_KEY = "AUTHENTICATION_DATE"
        private const val USERNAME_KEY = "USERNAME"
        private const val IS_MEMBER_KEY = "IS_MEMBER"

        private const val IS_HORIZONTAL_KEY = "HORIZONTAL_READER"
        private const val IS_HORIZONTAL_DEFAULT = false
        private const val FONT_STYLE_KEY = "FONT_STYLE"
        private const val FONT_SIZE_KEY = "FONT_SIZE" // TODO: Coordinate key with R.xml.preferences
        private const val FONT_SIZE_DEFAULT = 15 // TODO: Maybe move value to resource file?
        private const val MARGIN_KEY = "READER_MARGIN"
        private const val MARGIN_DEFAULT = 16

        @Volatile
        private var INSTANCE: PreferenceStore? = null

        fun getInstance(context: Context): PreferenceStore =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: PreferenceStore(context.applicationContext).also {
                    INSTANCE = it
                }
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

    var email: String?
        get() = sharedPref.getString(EMAIL_KEY, null)
        set(value) = sharedPref.setString(EMAIL_KEY, value)
    var password: String?
        get() = encryptedPreferences.getString(PASSWORD_KEY, null)
        set(value) = encryptedPreferences.setString(PASSWORD_KEY, value)

    var userId: String?
        get() = sharedPref.getString(USER_ID_KEY, null)
        set(value) = sharedPref.setString(USER_ID_KEY, value)
    var authToken: String?
        get() = sharedPref.getString(AUTH_TOKEN_KEY, null)
        set(value) = sharedPref.setString(AUTH_TOKEN_KEY, value)
    var authDate: String?
        get() = sharedPref.getString(AUTH_DATE_KEY, null)
        set(value) = sharedPref.setString(AUTH_DATE_KEY, value)
    var username: String?
        get() = sharedPref.getString(USERNAME_KEY, null)
        set(value) = sharedPref.setString(USERNAME_KEY, value)
    var isMember: Boolean?
        get() = sharedPref.getBoolean(IS_MEMBER_KEY, false)
        set(value) = sharedPref.setBoolean(IS_MEMBER_KEY, value ?: false)

    private val _horizontalReader =
        MutableLiveData(sharedPref.getBoolean(IS_HORIZONTAL_KEY, IS_HORIZONTAL_DEFAULT))
    private val _fontSize = MutableLiveData(sharedPref.getInt(FONT_SIZE_KEY, FONT_SIZE_DEFAULT))
    private val _fontStyle = MutableLiveData<Typeface>()
    private val _readerMargin = MutableLiveData(sharedPref.getInt(MARGIN_KEY, MARGIN_DEFAULT))
    val horizontalReader: LiveData<Boolean> = _horizontalReader
    val fontSize: LiveData<Int> = _fontSize
    val fontStyle: LiveData<Typeface> = _fontStyle
    val readerMargin: LiveData<Int> = _readerMargin

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
    fun setUserData(data: JSONObject?) {
        userId = data?.getString("userId")
        authToken = data?.getString("id")
        authDate = data?.getString("created")
        val user = data?.getJSONObject("user")
        username = user?.getString("username")
        val curSub = user?.getJSONObject("currentSubscription")
        isMember = curSub?.getString("status") == "active"
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        when (key) {
            IS_HORIZONTAL_KEY -> {
                _horizontalReader.value =
                    sharedPref.getBoolean(IS_HORIZONTAL_KEY, IS_HORIZONTAL_DEFAULT)
            }
            FONT_SIZE_KEY -> {
                _fontSize.value = sharedPref.getInt(FONT_SIZE_KEY, FONT_SIZE_DEFAULT)
            }
            FONT_STYLE_KEY -> {
                _fontStyle.value = updateTypeface()
            }
            MARGIN_KEY -> {
                _readerMargin.value = sharedPref.getInt(MARGIN_KEY, MARGIN_DEFAULT)
            }
        }
    }

    private fun updateTypeface(): Typeface {
        val styleString = sharedPref.getString(FONT_STYLE_KEY, "default")!!
        return when (styleString) {
            "default" -> Typeface.defaultFromStyle(Typeface.NORMAL)

            // Recreating every time the style is requested is probably a bad idea
            else -> Typeface.createFromAsset(appContext.assets, "fonts/$styleString")
        }
    }
}