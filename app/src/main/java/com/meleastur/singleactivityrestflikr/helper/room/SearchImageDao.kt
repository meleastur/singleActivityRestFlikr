package com.meleastur.singleactivityrestflikr.helper.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface SearchImageDao {

    // LiveData is a data holder class that can be observed within a given lifecycle.
    // Always holds/caches latest version of data. Notifies its active observers when the
    // data has changed. Since we are getting all the contents of the database,
    // we are notified whenever any of the database contents have changed.
    @Query("SELECT * from search_images ORDER BY id ASC")
    fun getAllSearchImages(): LiveData<List<SearchImage>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(searchImage: SearchImage)

    @Query("DELETE FROM search_images")
    suspend fun deleteAll()
}