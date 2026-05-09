package com.mobile.rickydemo.di

import android.content.Context
import androidx.room.Room
import com.mobile.rickydemo.data.local.RickAndMortyDatabase
import com.mobile.rickydemo.data.local.dao.CharacterDao
import com.mobile.rickydemo.data.local.dao.RemoteKeysDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideRickAndMortyDatabase(@ApplicationContext context: Context): RickAndMortyDatabase {
        return Room.databaseBuilder(
            context,
            RickAndMortyDatabase::class.java,
            RickAndMortyDatabase.DATABASE_NAME
        ).build()
    }

    @Provides
    @Singleton
    fun provideCharacterDao(database: RickAndMortyDatabase): CharacterDao {
        return database.characterDao()
    }

    @Provides
    @Singleton
    fun provideRemoteKeysDao(database: RickAndMortyDatabase): RemoteKeysDao {
        return database.remoteKeysDao()
    }
}
