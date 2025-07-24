package com.example.movieapp

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class TvResponse(
    val page: Int,
    val results: List<TvShowResult>
) : Parcelable

@Parcelize
data class TvShowResult(
    val id: Int,
    val name: String?,
    val original_name: String?,
    val overview: String?,
    val poster_path: String?,
    val backdrop_path: String?,
    val vote_average: Double?,
    val vote_count: Int?,
    val first_air_date: String?,
    val genre_ids: List<Int>
) : Parcelable

@Parcelize
data class TvDetailResponse(
    val id: Int,
    val name: String?,
    val number_of_seasons: Int?,
    val seasons: List<SeasonInfo>?
) : Parcelable

@Parcelize
data class SeasonInfo(
    val id: Int,
    val name: String?,
    val season_number: Int?,
    val episode_count: Int?,
    val poster_path: String?,
    val air_date: String?
) : Parcelable

@Parcelize
data class SeasonEpisodesResponse(
    val id: Int,
    val name: String?,
    val season_number: Int?,
    val episodes: List<EpisodeInfo>?
) : Parcelable

@Parcelize
data class EpisodeInfo(
    val id: Int,
    val name: String?,
    val episode_number: Int?,
    val overview: String?,
    val air_date: String?,
    val still_path: String?
) : Parcelable 