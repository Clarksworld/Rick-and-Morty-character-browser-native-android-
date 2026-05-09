package com.mobile.rickydemo.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.mobile.rickydemo.data.local.dao.CharacterDao
import com.mobile.rickydemo.data.local.dao.RemoteKeysDao
import com.mobile.rickydemo.data.local.entity.CharacterEntity
import com.mobile.rickydemo.data.local.entity.RemoteKeys

@Database(
    entities = [CharacterEntity::class, RemoteKeys::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class RickAndMortyDatabase : RoomDatabase() {
    abstract fun characterDao(): CharacterDao
    abstract fun remoteKeysDao(): RemoteKeysDao

    companion object {
        const val DATABASE_NAME = "rick_and_morty_db"
    }
}
