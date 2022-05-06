package com.anggasaraya.githubuserfinal

import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.anggasaraya.githubuserfinal.FavActivity.Companion.EXTRA_POSITION
import com.anggasaraya.githubuserfinal.FavActivity.Companion.EXTRA_USERFAV
import com.anggasaraya.githubuserfinal.adapter.SectionsPagerAdapter
import com.anggasaraya.githubuserfinal.db.DatabaseContract
import com.anggasaraya.githubuserfinal.db.DatabaseContract.UserFavColumns.Companion.CONTENT_URI
import com.anggasaraya.githubuserfinal.entity.UserFav
import com.anggasaraya.githubuserfinal.helper.MappingHelper
import com.bumptech.glide.Glide
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import cz.msebera.android.httpclient.Header
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.json.JSONObject

class DetailActivity : AppCompatActivity(), View.OnClickListener {
    private val TAG = DetailActivity::class.java.simpleName
    private var isFav = false
    private var position: Int = 0
    private var userFav: UserFav? = null
    private lateinit var uriWithId: Uri

    companion object{
        const val EXTRA_USER = "extra_user"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        title = resources.getString(R.string.detail_title)

        userFav = intent.getParcelableExtra<UserFav>(EXTRA_USERFAV) as UserFav?
        if (userFav != null){
            isFav = true
            fab_fav.setImageDrawable(
                ContextCompat.getDrawable(applicationContext, R.drawable.ic_baseline_favorite_red_24))
            position = intent.getIntExtra(EXTRA_POSITION, 0)
            getUserFavDetail()
        } else{
            userFav = intent.getParcelableExtra<UserFav>(EXTRA_USER) as UserFav?
            checkUserInFav(userFav?.username)
            Glide.with(applicationContext)
                .load(userFav?.avatarURL)
                .into(img_avatar)
            getUserDetailFromAPI(userFav?.username)
        }

        tabs.setupWithViewPager(view_pager)
        fab_fav.setOnClickListener(this)
    }

    fun checkUserInFav(username: String?){
        GlobalScope.launch(Dispatchers.Main) {
            progressBar.visibility = View.VISIBLE
            val deferredUserFav = async(Dispatchers.IO) {
                val cursor = contentResolver.query(CONTENT_URI, null, null, null, null)
                MappingHelper.mapCursorToArrayList(cursor)
            }
            progressBar.visibility = View.INVISIBLE
            val userFavs = deferredUserFav.await()
            for (user: UserFav in userFavs) {
                if (user.username == username){
                    isFav = true
                    fab_fav.setImageDrawable(
                        ContextCompat.getDrawable(applicationContext, R.drawable.ic_baseline_favorite_red_24))
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_detail, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.share){
            val sendIntent = Intent()
            sendIntent.action = Intent.ACTION_SEND
            sendIntent.putExtra(Intent.EXTRA_TEXT,
                resources.getString(R.string.username) + " : " + tv_username.text + "\n" +
                        resources.getString(R.string.name) + " : " + tv_name.text + "\n" +
                        resources.getString(R.string.location) + " : " + tv_location.text + "\n" +
                        tv_repository.text + "\n" + tv_company.text + "\n" +
                        resources.getString(R.string.follower) + " : " + tv_follower_numb.text + "\n" +
                        resources.getString(R.string.following) + " : " + tv_following_numb.text)
            sendIntent.type = "text/plain"
            val shareIntent = Intent.createChooser(sendIntent, null)
            startActivity(shareIntent)
        }
        return super.onOptionsItemSelected(item)
    }

    private fun getUserFavDetail(){
        fab_fav.visibility = View.INVISIBLE
        progressBar.visibility = View.VISIBLE
        fab_fav.setImageDrawable(
            ContextCompat.getDrawable(applicationContext, R.drawable.ic_baseline_favorite_red_24))
        userFav = intent.getParcelableExtra<UserFav>(EXTRA_USERFAV) as UserFav?
        tv_username.text = userFav?.username
        tv_name.text = userFav?.name
        tv_location.text = userFav?.location
        tv_company.text = userFav?.company
        tv_repository.text = userFav?.repositroy
        tv_follower_numb.text = userFav?.follower
        tv_following_numb.text = userFav?.following
        Glide.with(applicationContext)
            .load(userFav?.avatarURL)
            .into(img_avatar)

        setupSectionPager(userFav?.username)
        progressBar.visibility = View.INVISIBLE
        fab_fav?.visibility = View.VISIBLE
    }

    private fun getUserDetailFromAPI(username: String?) {
        fab_fav.visibility = View.INVISIBLE
        progressBar?.visibility = View.VISIBLE
        val client = AsyncHttpClient()
        client.addHeader("Authorization", "token 5169e0158b48541936e82727e7439f007caa4450")
        client.addHeader("User-Agent", "request")
        client["https://api.github.com/users/$username", object : AsyncHttpResponseHandler() {
            override fun onSuccess(statusCode: Int, headers: Array<Header>, responseBody: ByteArray) {
                progressBar?.visibility = View.INVISIBLE
                fab_fav?.visibility = View.VISIBLE
                val result = String(responseBody)
                Log.d(TAG, result)
                try {
                    val userDetail = JSONObject(result)
                    tv_username.text = userDetail.getString("login")
                    tv_name.text = userDetail.getString("name")
                    tv_location.text = userDetail.getString("location")
                    tv_company.text = resources.getString(R.string.company, userDetail.getString("company"))
                    tv_repository.text = resources.getString(R.string.repository, userDetail.getString("public_repos"))
                    tv_follower_numb.text = userDetail.getInt("followers").toString()
                    tv_following_numb.text = userDetail.getInt("following").toString()
                    setupSectionPager(userDetail.getString("login"))
                } catch (e: Exception) {
                    Toast.makeText(this@DetailActivity, e.message, Toast.LENGTH_SHORT).show()
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
                Toast.makeText(this@DetailActivity, errorMessage, Toast.LENGTH_SHORT).show()
            }
        }]
    }

    private fun setupSectionPager(data: String?){
        val sectionsPagerAdapter = SectionsPagerAdapter(this, supportFragmentManager)
        sectionsPagerAdapter.username = data
        view_pager.adapter = sectionsPagerAdapter
        tabs.setupWithViewPager(view_pager)
        supportActionBar?.elevation = 0f
    }

    override fun onClick(view: View?) {
        if (view?.id == R.id.fab_fav){
            if (isFav){
                isFav = false
                fab_fav.setImageDrawable(
                    ContextCompat.getDrawable(applicationContext, R.drawable.ic_baseline_favorite_border_white_24))
                uriWithId = Uri.parse(CONTENT_URI.toString() + "/" + userFav?.id)
                contentResolver.delete(uriWithId, null, null).toLong()
                Toast.makeText(this@DetailActivity, resources.getString(R.string.remove_from_fav_succes_info), Toast.LENGTH_LONG).show()
            } else{
                isFav = true
                fab_fav.setImageDrawable(
                    ContextCompat.getDrawable(applicationContext, R.drawable.ic_baseline_favorite_red_24))

                val values = ContentValues()
                values.put(DatabaseContract.UserFavColumns.COLUMN_NAME_USERNAME, tv_username.text.toString().trim())
                values.put(DatabaseContract.UserFavColumns.COLUMN_NAME_NAME, tv_name.text.toString().trim())
                values.put(DatabaseContract.UserFavColumns.COLUMN_NAME_AVATAR_URL, userFav?.avatarURL.toString().trim())
                values.put(DatabaseContract.UserFavColumns.COLUMN_NAME_LOCATION, tv_location.text.toString().trim())
                values.put(DatabaseContract.UserFavColumns.COLUMN_NAME_COMPANY, tv_company.text.toString().trim())
                values.put(DatabaseContract.UserFavColumns.COLUMN_NAME_REPOSITORY, tv_repository.text.toString().trim())
                values.put(DatabaseContract.UserFavColumns.COLUMN_NAME_FOLLOWER, tv_follower_numb.text.toString().trim())
                values.put(DatabaseContract.UserFavColumns.COLUMN_NAME_FOLLOWING, tv_following_numb.text.toString().trim())

                contentResolver.insert(CONTENT_URI, values)
                Toast.makeText(this@DetailActivity, resources.getString(R.string.add_to_fav_succes_info), Toast.LENGTH_LONG).show()
            }
        }
    }
}