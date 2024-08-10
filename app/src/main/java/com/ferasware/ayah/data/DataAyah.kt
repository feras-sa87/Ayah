package com.ferasware.ayah.data

data class DataAyah(
    val text: String, val surah: Surah, val numberInSurah: Int
)

data class Surah(val name: String)

data class AyahApiResponse(
    val data: DataAyah,
)