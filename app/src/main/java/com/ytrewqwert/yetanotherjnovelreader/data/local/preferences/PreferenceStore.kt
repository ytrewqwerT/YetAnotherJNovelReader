package com.ytrewqwert.yetanotherjnovelreader.data.local.preferences

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Typeface
import androidx.preference.PreferenceManager
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.ytrewqwert.yetanotherjnovelreader.data.local.database.UserData
import com.ytrewqwert.yetanotherjnovelreader.setBoolean
import com.ytrewqwert.yetanotherjnovelreader.setString
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.channelFlow
import java.time.Instant
import java.time.Period

/** Exposes the values for the user's preferences. */
@Suppress("EXPERIMENTAL_API_USAGE")
class PreferenceStore private constructor(private val appContext: Context) {

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
    }

    // User login details
    var email: String?
        get() = encryptedPreferences.getString(PrefKeys.EMAIL, null)
        set(value) = encryptedPreferences.setString(PrefKeys.EMAIL, value)
    var password: String?
        get() = encryptedPreferences.getString(PrefKeys.PASSWORD, null)
        set(value) = encryptedPreferences.setString(PrefKeys.PASSWORD, value)
    var authToken: String?
        get() = encryptedPreferences.getString(PrefKeys.AUTH_TOKEN, null)
        set(value) = encryptedPreferences.setString(PrefKeys.AUTH_TOKEN, value)

    // User account details
    var userId: String?
        get() = sharedPref.getString(PrefKeys.USER_ID, null)
        set(value) = sharedPref.setString(PrefKeys.USER_ID, value)
    private var authDate: String?
        get() = sharedPref.getString(PrefKeys.AUTH_DATE, null)
        set(value) = sharedPref.setString(PrefKeys.AUTH_DATE, value)
    var username: String?
        get() = sharedPref.getString(PrefKeys.USERNAME, null)
        set(value) = sharedPref.setString(PrefKeys.USERNAME, value)
    var isMember: Boolean?
        get() = sharedPref.getBoolean(PrefKeys.IS_MEMBER, false)
        set(value) = sharedPref.setBoolean(PrefKeys.IS_MEMBER, value ?: false)

    // Non-reader settings
    val isFilterFollowing = channelFlow {
        offer(sharedPref.getBoolean(PrefKeys.IS_FOLLOW, false))
        val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
            if (key == PrefKeys.IS_FOLLOW) offer(sharedPref.getBoolean(PrefKeys.IS_FOLLOW, false))
        }
        sharedPref.registerOnSharedPreferenceChangeListener(listener)
        awaitClose { sharedPref.unregisterOnSharedPreferenceChangeListener(listener) }
    }
    fun setIsFilterFollowing(value: Boolean) { sharedPref.setBoolean(PrefKeys.IS_FOLLOW, value) }

    // Reader settings
    private var paginated: Boolean = sharedPref.getBoolean(PrefKeys.IS_HORIZONTAL, PrefDefaults.IS_HORIZONTAL)
    private var fontSize = sharedPref.getInt(PrefKeys.FONT_SIZE, PrefDefaults.FONT_SIZE)
    private var fontStyle = getTypeface()
    private var readerMargins = getMargins()

    /** Aggregates a number of preferences for styling the app's part reader. */
    val readerSettings = channelFlow {
        offer(ReaderPreferences(paginated, fontSize, fontStyle, readerMargins))
        val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, changedKey ->
            with (PrefKeys) {
                when (changedKey) {
                    IS_HORIZONTAL -> {
                        paginated = sharedPref.getBoolean(IS_HORIZONTAL, PrefDefaults.IS_HORIZONTAL)
                    }
                    FONT_SIZE -> {
                        fontSize = sharedPref.getInt(FONT_SIZE, PrefDefaults.FONT_SIZE)
                    }
                    FONT_STYLE -> {
                        fontStyle = getTypeface()
                    }
                    MARGIN_TOP, MARGIN_BOTTOM, MARGIN_LEFT, MARGIN_RIGHT -> {
                        readerMargins = getMargins()
                    }
                    else -> return@OnSharedPreferenceChangeListener
                }
            }
            offer(ReaderPreferences(paginated, fontSize, fontStyle, readerMargins))
        }
        sharedPref.registerOnSharedPreferenceChangeListener(listener)
        awaitClose { sharedPref.unregisterOnSharedPreferenceChangeListener(listener) }
    }

    fun isAuthExpired(): Boolean {
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

    private fun getTypeface(): Typeface {
        val fontStr = sharedPref.getString(PrefKeys.FONT_STYLE, "default")!!
        val fontResId = FontResIds.getFontResourceId(fontStr) ?: return Typeface.DEFAULT
        return appContext.resources.getFont(fontResId)
    }

    private fun getMargins(): Margins {
        val top = sharedPref.getInt(PrefKeys.MARGIN_TOP, PrefDefaults.MARGIN)
        val bottom = sharedPref.getInt(PrefKeys.MARGIN_BOTTOM, PrefDefaults.MARGIN)
        val left = sharedPref.getInt(PrefKeys.MARGIN_LEFT, PrefDefaults.MARGIN)
        val right = sharedPref.getInt(PrefKeys.MARGIN_RIGHT, PrefDefaults.MARGIN)
        return Margins(top, bottom, left, right)
    }

    /** A collection of values defining how large the margins around each edge should be. */
    data class Margins(val top: Int, val bottom: Int, val left: Int, val right: Int)

    /** Aggregates a number of preferences relating to the app's part reader. */
    data class ReaderPreferences(
        val isHorizontal: Boolean,
        val fontSize: Int,
        val fontStyle: Typeface,
        val margin: Margins
    )
}