package com.example.swsearch

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class FavoritesDbHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "favorites.db"
        private const val DATABASE_VERSION = 1
        private const val TABLE_NAME = "favorites"
        private const val COLUMN_RESULT = "result"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTableQuery = "CREATE TABLE $TABLE_NAME ($COLUMN_RESULT TEXT)"
        db?.execSQL(createTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        val dropTableQuery = "DROP TABLE IF EXISTS $TABLE_NAME"
        db?.execSQL(dropTableQuery)
        onCreate(db)
    }

    fun insertFavorite(result: String) {
        val values = ContentValues()
        values.put(COLUMN_RESULT, result)

        val db = writableDatabase
        db.insert(TABLE_NAME, null, values)
        db.close()
    }

    @SuppressLint("Range")
    fun getAllFavorites(): List<String> {
        val favoriteResults = ArrayList<String>()

        val selectQuery = "SELECT * FROM $TABLE_NAME"
        val db = readableDatabase
        val cursor: Cursor? = db.rawQuery(selectQuery, null)

        cursor?.use {
            if (it.moveToFirst()) {
                do {
                    val result = it.getString(it.getColumnIndex(COLUMN_RESULT))
                    favoriteResults.add(result)
                } while (it.moveToNext())
            }
        }

        return favoriteResults
    }
    fun removeFavorite(result: String) {
        val db = writableDatabase
        db.delete(TABLE_NAME, "$COLUMN_RESULT=?", arrayOf(result))
        db.close()
    }

}
