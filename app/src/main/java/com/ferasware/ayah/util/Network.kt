package com.ferasware.ayah.util

import com.ferasware.ayah.data.AyahApiResponse
import com.ferasware.ayah.data.DataAyah
import com.ferasware.ayah.data.DataPage
import com.ferasware.ayah.data.PageApiResponse
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.suspendCancellableCoroutine
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.random.Random
import kotlin.random.nextInt

interface RequestApi {
    @GET("/ayah/{ayahNumber}")
    fun getAyah(@Path("ayahNumber") ayahNumber: String): Call<AyahApiResponse>

    @GET("/page/{pageNumber}/quran-uthmani-quran-academy")
    fun getPage(@Path("pageNumber") pageNumber: String): Call<PageApiResponse>
}

val retrofit: Retrofit =
    Retrofit.Builder().baseUrl("https://api.alquran.cloud/v1/").addConverterFactory(
        GsonConverterFactory.create()
    ).build()

val apiRequest: RequestApi = retrofit.create(RequestApi::class.java)

private suspend fun fetchAyah(ayahNumber: Int): DataAyah? {
    return suspendCancellableCoroutine { continuation ->
        val callBack = object : Callback<AyahApiResponse> {
            override fun onResponse(
                call: Call<AyahApiResponse>, response: Response<AyahApiResponse>
            ) {
                if (response.isSuccessful) {
                    continuation.resume(response.body()?.data)
                } else {
                    continuation.resumeWithException(RuntimeException("Failed to get response"))
                }
            }

            override fun onFailure(call: Call<AyahApiResponse>, t: Throwable) {
                continuation.resumeWithException(t)
            }
        }

        apiRequest.getAyah(ayahNumber.toString()).enqueue(callBack)
    }
}


private suspend fun fetchPage(pageNumber: Int): DataPage? {
    return suspendCancellableCoroutine { continuation ->
        val callBack = object : Callback<PageApiResponse> {
            override fun onResponse(
                call: Call<PageApiResponse>, response: Response<PageApiResponse>
            ) {
                if (response.isSuccessful) {
                    continuation.resume(response.body()?.data)
                } else {
                    continuation.resumeWithException(RuntimeException("Failed to get response"))
                }
            }

            override fun onFailure(call: Call<PageApiResponse>, t: Throwable) {
                continuation.resumeWithException(t)
            }
        }

        apiRequest.getPage(pageNumber.toString()).enqueue(callBack)
    }
}

suspend fun myGetPage(pageNumber: Int): Map<String, String>? {
    try {
        val page = coroutineScope {
            fetchPage(pageNumber)
        }

        return if (page != null) {
            val ayahs = StringBuilder()

            page.ayahs.forEach { ayah ->
                ayahs.append("${ayah.text.removeSuffix("\n")} ${ayah.numberInSurah} ")
            }

            val surahs = page.surahs.values.toList().map {
                it.name.removePrefix("سُورَةُ ")
            }

            mapOf(
                "ayahs" to ayahs.toString(),
                "surahs" to surahs.joinToString(
                    separator = "، ", prefix = if (surahs.size > 1) "سُوَرُ " else "سُورَةُ "
                )
            )
        } else return null

    } catch (e: Exception) {
        return null
    }
}


suspend fun myGetAyah(ayahNumber: Int = Random.nextInt(1..6236)): Map<String, String>? {

    try {
        val ayah = coroutineScope {
            fetchAyah(ayahNumber)
        }

        return if (ayah != null) {
            mapOf(
                "surah" to ayah.surah.name,
                "ayahNumber" to ayah.numberInSurah.toString(),
                "ayah" to ayah.text
            )
        } else {
            return null
        }
    } catch (e: Exception) {
        return null
    }
}