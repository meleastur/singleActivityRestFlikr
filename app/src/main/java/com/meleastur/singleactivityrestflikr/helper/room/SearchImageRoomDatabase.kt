package com.meleastur.singleactivityrestflikr.helper.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.meleastur.singleactivityrestflikr.common.constants.Constants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [SearchImage::class], version = 1, exportSchema = false)
abstract class SearchImageRoomDatabase : RoomDatabase() {

    abstract fun searchImageDao(): SearchImageDao

    companion object {
        @Volatile
        private var INSTANCE: SearchImageRoomDatabase? = null

        fun getDatabase(
            context: Context,
            scope: CoroutineScope
        ): SearchImageRoomDatabase {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SearchImageRoomDatabase::class.java,
                    Constants.DATABASE
                )
                    .addCallback(SearchImageDatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                // return instance
                instance
            }
        }

        private class SearchImageDatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            /**
             * Override the onOpen method to populate the database.
             * For this sample, we clear the database every time it is created or opened.
             */
            override fun onOpen(db: SupportSQLiteDatabase) {
                super.onOpen(db)
                // If you want to keep the data through app restarts,
                // comment out the following line.
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateDatabase(database.searchImageDao())
                    }
                }
            }
        }

        /**
         * Populate the database in a new coroutine.
         * If you want to start with more imageSearch, just add them.
         */
        suspend fun populateDatabase(searchImageDao: SearchImageDao) {
            // Start the app with a clean database every time.
            // Not needed if you only populate on creation.
            searchImageDao.deleteAll()

            var searchImage = SearchImage()
            searchImageDao.insert(searchImage)
            searchImage = SearchImage()
            searchImageDao.insert(searchImage)
        }
    }

}
