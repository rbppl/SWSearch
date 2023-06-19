package com.example.swsearch

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class FavoritesActivity : AppCompatActivity() {

    private lateinit var backButton: Button
    private lateinit var listView: ListView
    private lateinit var listAdapter: ArrayAdapter<String>
    private lateinit var resultList: ArrayList<String>
    private lateinit var dbHelper: FavoritesDbHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favorites)

        listView = findViewById(R.id.listViewFavorites)
        listView.setOnItemLongClickListener { _, _, position, _ ->
            showRemoveDialog(position)
            true
        }

        resultList = ArrayList()

        dbHelper = FavoritesDbHelper(this)
        val favoriteResults = dbHelper.getAllFavorites()
        if (favoriteResults.isNotEmpty()) {
            resultList.addAll(favoriteResults)
        }

        listAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, resultList)
        listView.adapter = listAdapter

        backButton = findViewById(R.id.backBtn)
        backButton.setOnClickListener {
            finish()
        }
    }
    private fun showRemoveDialog(position: Int) {
        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.setTitle("Удалить из избранного?")
        alertDialogBuilder.setMessage("Отмена | Да")
        alertDialogBuilder.setCancelable(true)
        alertDialogBuilder.setPositiveButton("Да") { _, _ ->
            removeFavorite(position)
        }
        alertDialogBuilder.setNegativeButton("Отмена") { dialog, _ ->
            dialog.cancel()
        }
        alertDialogBuilder.create().show()
    }

    private fun removeFavorite(position: Int) {
        val favorite = resultList[position]
        resultList.removeAt(position)
        listAdapter.notifyDataSetChanged()

        val favoritesDbHelper = FavoritesDbHelper(this)
        favoritesDbHelper.removeFavorite(favorite)
        favoritesDbHelper.close()
    }

}
