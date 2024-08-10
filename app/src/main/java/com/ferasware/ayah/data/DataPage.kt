package com.ferasware.ayah.data

data class DataPage(
    val ayahs: List<Ayahs>, val surahs: Map<String, Surahs>
)

data class Ayahs(
    val text: String, val numberInSurah: Int
)

data class Surahs(
    val name: String,
)


data class PageApiResponse(
    val data: DataPage
)