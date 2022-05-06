package com.anggasaraya.consumerapp

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.anggasaraya.consumerapp.adapter.ListUserAdapter
import com.anggasaraya.consumerapp.entity.UserFav
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import cz.msebera.android.httpclient.Header
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject
import java.util.*

class MainActivity : AppCompatActivity() {
    companion object{
        private var searchSaved: String? = null
        private var searchView: SearchView? = null
        private val TAG: String = MainActivity::class.simpleName!!
        private const val STATE_SEARCH: String = "state_search"
        private const val DEFAULT_USERNAME: String = "sidiqpermana"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        title = "[Consumer] Github User"
        getUserListFromAPI(DEFAULT_USERNAME)

        if (savedInstanceState != null) {
            searchSaved = savedInstanceState.getString(STATE_SEARCH)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_main, menu)
        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val searchMenuItem = menu.findItem(R.id.search)
        searchView = searchMenuItem.actionView as SearchView
        searchView?.setSearchableInfo(searchManager.getSearchableInfo(componentName))
        searchView?.queryHint = resources.getString(R.string.search)
        if (searchSaved != null && searchSaved?.isNotEmpty() == true) {
            searchMenuItem.expandActionView()
            searchView?.setQuery(searchSaved, true)
            getUserListFromAPI(searchSaved.orEmpty())
            searchView?.clearFocus()
        }
        searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                if(newText != "") getUserListFromAPI(newText) else getUserListFromAPI(DEFAULT_USERNAME)
                return true
            }
        })
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.setting){

            val mIntent = Intent(this@MainActivity, SettingActivity::class.java)
            startActivity(mIntent)
        } else if (item.itemId == R.id.favorite){
            val mIntent = Intent(this@MainActivity, FavActivity::class.java)
            startActivity(mIntent)
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        searchSaved = searchView?.query.toString()
        outState.putString(STATE_SEARCH, searchSaved)
    }

    private fun getUserListFromAPI(username: String){
        progressBar.visibility = View.VISIBLE
        val client = AsyncHttpClient()
        client.addHeader("Authorization", "token 5169e0158b48541936e82727e7439f007caa4450")
        client.addHeader("User-Agent", "request")
        client["https://api.github.com/search/users?q=$username", object : AsyncHttpResponseHandler() {
            override fun onSuccess(statusCode: Int, headers: Array<Header>, responseBody: ByteArray) {
                progressBar?.visibility = View.INVISIBLE
                val list = ArrayList<UserFav>()
                val result = String(responseBody)
                Log.d(TAG, result)
                try {
                    val jsonObject = JSONObject(result)
                    val items = jsonObject.getJSONArray("items")
                    for (i in 0 until items.length()) {
                        val item = items.getJSONObject(i)
                        val user = UserFav()
                        user.username = item.getString("login")
                        user.avatarURL = item.getString("avatar_url")
                        list.add(user)
                    }
                    rv_user.layoutManager = LinearLayoutManager(applicationContext)
                    val listUserAdapter = ListUserAdapter(list)
                    rv_user.adapter = listUserAdapter
                    listUserAdapter.setOnItemClickCallback(object: ListUserAdapter.OnItemClickCallback{
                        override fun onItemClicked(data: UserFav?) {
                            val detailIntent = Intent(this@MainActivity, DetailActivity::class.java)
                            detailIntent.putExtra(DetailActivity.EXTRA_USER, data)
                            startActivity(detailIntent)
                        }
                    })
                } catch (e: Exception) {
                    Toast.makeText(this@MainActivity, e.message, Toast.LENGTH_SHORT).show()
                    e.printStackTrace()
                }
            }

            override fun onFailure(statusCode: Int, headers: Array<Header>, responseBody: ByteArray, error: Throwable) {
                progressBar?.visibility = View.INVISIBLE
                val errorMessage: String = when (statusCode) {
                    401 -> "$statusCode : Bad Request"
                    403 -> "$statusCode : Forbidden"
                    404 -> "$statusCode : Not Found"
                    else -> statusCode.toString() + " : " + error.message
                }
                Toast.makeText(this@MainActivity, errorMessage, Toast.LENGTH_SHORT).show()
            }
        }]
    }
}