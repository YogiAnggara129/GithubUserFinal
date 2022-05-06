package com.anggasaraya.githubuserfinal

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.anggasaraya.githubuserfinal.adapter.ListUserAdapter
import com.anggasaraya.githubuserfinal.entity.UserFav
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import cz.msebera.android.httpclient.Header
import kotlinx.android.synthetic.main.fragment_follower.*
import org.json.JSONArray
import java.util.*

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class FollowerFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_follower, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val username = arguments?.getString(ARG_USERNAME)
        getFollowerListFromAPI(username)
    }

    private fun getFollowerListFromAPI(username: String?){
        progressBar.visibility = View.VISIBLE
        val client = AsyncHttpClient()
        client.addHeader("Authorization", "token 5169e0158b48541936e82727e7439f007caa4450")
        client.addHeader("User-Agent", "request")
        client["https://api.github.com/users/$username/followers", object : AsyncHttpResponseHandler() {
            override fun onSuccess(statusCode: Int, headers: Array<Header>, responseBody: ByteArray) {
                progressBar.visibility = View.INVISIBLE
                val list = ArrayList<UserFav>()
                val result = String(responseBody)
                Log.d(TAG, result)
                try {
                    val jsonArray = JSONArray(result)
                    for (i in 0 until jsonArray.length()) {
                        val foll = jsonArray.getJSONObject(i)
                        val follower = UserFav()
                        follower.username = foll.getString("login")
                        follower.avatarURL = foll.getString("avatar_url")
                        list.add(follower)
                    }
                    rv_user.layoutManager = LinearLayoutManager(activity)
                    val listUserAdapter = ListUserAdapter(list)
                    rv_user.adapter = listUserAdapter
                    listUserAdapter.setOnItemClickCallback(object: ListUserAdapter.OnItemClickCallback{
                        override fun onItemClicked(data: UserFav?) {
                            val detailIntent = Intent(activity, DetailActivity::class.java)
                            detailIntent.putExtra(DetailActivity.EXTRA_USER, data)
                            startActivity(detailIntent)
                        }
                    })
                } catch (e: Exception) {
                    Toast.makeText(activity, e.message, Toast.LENGTH_SHORT).show()
                    e.printStackTrace()
                }
            }

            override fun onFailure(statusCode: Int, headers: Array<Header>, responseBody: ByteArray, error: Throwable) {
                val errorMessage: String = when (statusCode) {
                    401 -> "$statusCode : Bad Request"
                    403 -> "$statusCode : Forbidden"
                    404 -> "$statusCode : Not Found"
                    else -> statusCode.toString() + " : " + error.message
                }
                Toast.makeText(activity, errorMessage, Toast.LENGTH_SHORT).show()
            }
        }]
    }

    companion object {
        private const val ARG_USERNAME = "username"
        private val TAG = FollowerFragment::class.java.simpleName

        fun newInstance(username: String?): FollowerFragment{
            val fragment = FollowerFragment()
            val bundle = Bundle()
            bundle.putString(ARG_USERNAME, username)
            fragment.arguments = bundle
            return fragment
        }
    }
}