package com.ytrewqwert.yetanotherjnovelreader.data.local

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Typeface
import androidx.preference.PreferenceManager
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.ytrewqwert.yetanotherjnovelreader.setBoolean
import com.ytrewqwert.yetanotherjnovelreader.setString
import org.json.JSONObject
import java.time.Instant
import java.time.Period


class PreferenceStore private constructor(private val appContext: Context) {

    companion object {
        private const val EMAIL_KEY = "EMAIL"
        private const val PASSWORD_KEY = "PASSWORD"

        private const val USER_ID_KEY = "USER_ID"
        private const val AUTH_TOKEN_KEY = "AUTHENTICATION_TOKEN"
        private const val AUTH_DATE_KEY = "AUTHENTICATION_DATE"
        private const val USERNAME_KEY = "USERNAME"
        private const val IS_MEMBER_KEY = "IS_MEMBER"

        private const val FONT_STYLE_KEY = "FONT_STYLE"
        private const val FONT_SIZE_KEY = "FONT_SIZE" // TODO: Coordinate key with R.xml.preferences
        private const val FONT_SIZE_DEFAULT = 15 // TODO: Maybe move value to resource file?
        private const val READER_MARGIN_KEY = "READER_MARGIN"
        private const val READER_MARGIN_DEFAULT = 16

        @Volatile
        private var INSTANCE: PreferenceStore? = null

        fun getInstance(context: Context): PreferenceStore =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: PreferenceStore(context.applicationContext).also {
                    INSTANCE = it
                }
            }
    }

    private val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(appContext)
    private val encryptedPreferences: SharedPreferences

    init {
        val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
        encryptedPreferences = EncryptedSharedPreferences.create(
            "encrypted_preferences", masterKeyAlias, appContext,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    var email: String?
        get() = sharedPreferences.getString(EMAIL_KEY, null)
        set(value) = sharedPreferences.setString(EMAIL_KEY, value)
    var password: String?
        get() = encryptedPreferences.getString(PASSWORD_KEY, null)
        set(value) = encryptedPreferences.setString(PASSWORD_KEY, value)

    var userId: String?
        get() = sharedPreferences.getString(USER_ID_KEY, null)
        set(value) = sharedPreferences.setString(USER_ID_KEY, value)
    var authToken: String?
        get() = sharedPreferences.getString(AUTH_TOKEN_KEY, null)
        set(value) = sharedPreferences.setString(AUTH_TOKEN_KEY, value)
    var authDate: String?
        get() = sharedPreferences.getString(AUTH_DATE_KEY, null)
        set(value) = sharedPreferences.setString(AUTH_DATE_KEY, value)
    var username: String?
        get() = sharedPreferences.getString(USERNAME_KEY, null)
        set(value) = sharedPreferences.setString(USERNAME_KEY, value)
    var isMember: Boolean?
        get() = sharedPreferences.getBoolean(IS_MEMBER_KEY, false)
        set(value) = sharedPreferences.setBoolean(IS_MEMBER_KEY, value ?: false)

    val fontSize: Int
        get() = sharedPreferences.getInt(FONT_SIZE_KEY, FONT_SIZE_DEFAULT)
    val fontStyle: Typeface
        get() {
            val styleString = sharedPreferences.getString(FONT_STYLE_KEY, "default")
            return when (styleString) {
                "default" -> Typeface.defaultFromStyle(Typeface.NORMAL)

                // Recreating every time the style is requested is probably a bad idea
                else -> Typeface.createFromAsset(appContext.assets, "fonts/$styleString")
            }
        }
    val readerMargin: Int
        get() = sharedPreferences.getInt(READER_MARGIN_KEY, READER_MARGIN_DEFAULT)

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
}