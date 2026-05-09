package com.mobile.rickydemo.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "characters")
data class CharacterEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val status: String,
    val species: String,
    val type: String,
    val gender: String,
    val originName: String,
    val locationName: String,
    val image: String,
    val episodeUrls: List<String>,
    val page: Int // Added for pagination support
)
