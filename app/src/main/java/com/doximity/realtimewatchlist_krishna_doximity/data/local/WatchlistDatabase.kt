package com.doximity.realtimewatchlist_krishna_doximity.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.doximity.realtimewatchlist_krishna_doximity.data.local.entity.WatchlistEntity

@Database(
    entities = [WatchlistEntity::class],
    version = 1,
    exportSchema = false,
)
abstract class WatchlistDatabase : RoomDatabase() {
    abstract fun watchlistDao(): WatchlistDao
}
