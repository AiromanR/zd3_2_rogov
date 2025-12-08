package com.example.zd3_1_rogov

data class Movie(
    val Title: String,
    val Poster: String,
    val Genre: List<String>,
    val Plot: String,
    val Year: String,
    val Runtime: String,
    val imdbID: String
)