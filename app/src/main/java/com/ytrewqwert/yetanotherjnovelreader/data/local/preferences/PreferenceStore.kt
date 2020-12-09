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
}