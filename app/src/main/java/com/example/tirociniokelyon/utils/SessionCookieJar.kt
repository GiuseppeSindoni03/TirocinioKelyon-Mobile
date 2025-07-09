package com.example.tirociniokelyon.com.example.tirociniokelyon.utils

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class PersistentCookieJar @Inject constructor(
    @ApplicationContext private val context: Context
) : CookieJar {

    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("cookie_prefs", Context.MODE_PRIVATE)
    private val gson = Gson()
    private val cookieStore = mutableMapOf<String, List<Cookie>>()

    companion object {
        private const val COOKIE_KEY = "cookies"
    }

    init {
        // Carica i cookie salvati al momento dell'inizializzazione
        loadCookiesFromPreferences()
    }

    override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
        // Salva i cookie in memoria
        cookieStore[url.host] = cookies

        // Salva i cookie in SharedPreferences per la persistenza
        saveCookiesToPreferences()
    }

    override fun loadForRequest(url: HttpUrl): List<Cookie> {
        // Filtra i cookie scaduti prima di restituirli
        val validCookies = cookieStore[url.host]?.filter { cookie ->
            !cookie.expiresAt.let { it != 0L && it < System.currentTimeMillis() }
        } ?: emptyList()

        // Aggiorna la mappa rimuovendo i cookie scaduti
        if (validCookies.size != cookieStore[url.host]?.size) {
            cookieStore[url.host] = validCookies
            saveCookiesToPreferences()
        }

        return validCookies
    }

    private fun saveCookiesToPreferences() {
        val cookiesMap = mutableMapOf<String, List<SerializableCookie>>()

        cookieStore.forEach { (host, cookies) ->
            cookiesMap[host] = cookies.map { SerializableCookie.fromCookie(it) }
        }

        val json = gson.toJson(cookiesMap)
        sharedPreferences.edit().putString(COOKIE_KEY, json).apply()
    }

    private fun loadCookiesFromPreferences() {
        val json = sharedPreferences.getString(COOKIE_KEY, null)
        if (json != null) {
            try {
                val type = object : TypeToken<Map<String, List<SerializableCookie>>>() {}.type
                val cookiesMap: Map<String, List<SerializableCookie>> = gson.fromJson(json, type)

                cookiesMap.forEach { (host, serializableCookies) ->
                    val cookies = serializableCookies.mapNotNull { it.toCookie() }
                    cookieStore[host] = cookies
                }
            } catch (e: Exception) {
                // Se c'è un errore nel parsing, pulisci le preferenze
                clearCookies()
            }
        }
    }

    fun clearCookies() {
        cookieStore.clear()
        sharedPreferences.edit().clear().apply()
    }

    // Classe per serializzare/deserializzare i cookie
    private data class SerializableCookie(
        val name: String,
        val value: String,
        val expiresAt: Long,
        val domain: String,
        val path: String,
        val secure: Boolean,
        val httpOnly: Boolean,
        val hostOnly: Boolean
    ) {
        companion object {
            fun fromCookie(cookie: Cookie): SerializableCookie {
                return SerializableCookie(
                    name = cookie.name,
                    value = cookie.value,
                    expiresAt = cookie.expiresAt,
                    domain = cookie.domain,
                    path = cookie.path,
                    secure = cookie.secure,
                    httpOnly = cookie.httpOnly,
                    hostOnly = cookie.hostOnly
                )
            }
        }

        fun toCookie(): Cookie? {
            return try {
                Cookie.Builder()
                    .name(name)
                    .value(value)
                    .expiresAt(expiresAt)
                    .domain(domain)
                    .path(path)
                    .apply {
                        if (secure) secure()
                        if (httpOnly) httpOnly()
                        if (hostOnly) hostOnlyDomain(domain)
                    }
                    .build()
            } catch (e: Exception) {
                null
            }
        }
    }
}