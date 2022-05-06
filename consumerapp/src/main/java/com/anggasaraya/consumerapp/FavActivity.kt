package com.anggasaraya.consumerapp

import android.bluetooth.BluetoothAdapter.EXTRA_STATE
import android.content.Intent
import android.database.ContentObserver
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.anggasaraya.consumerapp.adapter.ListUserFavAdapter
import com.anggasaraya.consumerapp.db.DatabaseContract.UserFavColumns.Companion.CONTENT_URI
import com.anggasaraya.consumerapp.entity.UserFav
import com.anggasaraya.consumerapp.helper.MappingHelper
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_fav.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class FavActivity : AppCompatActivity() {
    companion object{
        const val EXTRA_USERFAV = "extra_userfav"
        const val EXTRA_POSITION = "extra_position"
        const val REQUEST_ADD = 100
        const val RESULT_ADD = 101
        const val REQUEST_UPDATE = 200
        const val RESULT_DELETE = 301
    }

    private lateinit var adapter: ListUserFavAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fav)
        title = resources.getString(R.string.favorite)

        rv_user.layoutManager = LinearLayoutManager(this)
        rv_user.setHasFixedSize(true)
        adapter = ListUserFavAdapter(this)
        rv_user.adapter = adapter

        val handlerThread = HandlerThread("DataObserver")
        handlerThread.start()
        val handler = Handler(handlerThread.looper)

        val myObserver = object : ContentObserver(handler) {
            override fun onChange(self: Boolean) {
                loadNotesAsync()
            }
        }
        contentResolver.registerContentObserver(CONTENT_URI, true, myObserver)

        if (savedInstanceState == null) {
            loadNotesAsync()
        } else {
            val list = savedInstanceState.getParcelableArrayList<UserFav>(EXTRA_STATE)
            if (list != null) {
                adapter.list = list
            }
        }
    }

    private fun loadNotesAsync() {
        GlobalScope.launch(Dispatchers.Main) {
            progressBar.visibility = View.VISIBLE
            val deferredUserFav = async(Dispatchers.IO) {
                val cursor = contentResolver.query(CONTENT_URI, null, null, null, null)
                MappingHelper.mapCursorToArrayList(cursor)
            }
            progressBar.visibility = View.INVISIBLE
            val userFavs = deferredUserFav.await()
            if (userFavs.size > 0) {
                adapter.list = userFavs
            } else {
                adapter.list = ArrayList()
                showSnackbarMessage(resources.getString(R.string.nul_data_fav_info))
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelableArrayList(EXTRA_STATE, adapter.list)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (data != null) {
            when (requestCode) {
                REQUEST_ADD -> if (resultCode == RESULT_ADD) {
                    val note = data.getParcelableExtra<UserFav>(EXTRA_USERFAV) as UserFav

                    adapter.addItem(note)
                    rv_user.smoothScrollToPosition(adapter.itemCount - 1)
                }
                REQUEST_UPDATE ->
                    when (resultCode) {

                        RESULT_DELETE -> {
                            val position = data.getIntExtra(EXTRA_POSITION, 0)

                            adapter.removeItem(position)
                        }
                    }
            }
        }
    }

    private fun showSnackbarMessage(message: String) {
        Snackbar.make(rv_user, message, Snackbar.LENGTH_SHORT).show()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_favorite, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.setting) {
            val mIntent = Intent(this@FavActivity, SettingActivity::class.java)
            startActivity(mIntent)
        }
        return super.onOptionsItemSelected(item)
    }
}