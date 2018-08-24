package com.amervyn.squarenews

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.FragmentActivity
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.*
import org.json.JSONObject
import java.io.IOException
import android.R.attr.duration
import android.R.attr.layout
import android.support.v4.view.MenuItemCompat
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import java.time.LocalDateTime
import com.google.gson.JsonElement
import com.google.gson.JsonParser
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import android.widget.Spinner


class ArticleResult(val TotalResults: Int, val Articles: List<ArticleItem>) {
    override fun toString(): String = TotalResults.toString()

}

class ArticleItem(val ArticleId: Int, val SourceId: Int, val NewsApiSourceId: String,
                  val Headline: String, val Description: String, val ImageUrl: String,
                  val Url: String, val IsAvailable: Boolean, val CreatedOn: String,
                  val PublishedOn: String, val Country: String, val ViewCount: Int) {
    override fun toString(): String = Headline
}


class MainActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    var url = "http://amervyn.duckdns.org/api/Articles/?country=GB&pageSize=50"
    private var aResult: ArticleResult? = null
    var oldCountryParam: String = "country=GB"


    override fun onNothingSelected(parent: AdapterView<*>?) {

    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val item = parent?.getItemAtPosition(position)

        url = url.replace(oldCountryParam, "country=" + item.toString())

        oldCountryParam = "country=" + item.toString()

        Log.d("ItemSelected", "New URL: $url")

        callApi()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        //toolbar.setLogo(R.drawable.ic_fishnews_home)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        //supportActionBar?.setDisplayShowHomeEnabled(true)
        //supportActionBar?.setDisplayUseLogoEnabled(true)

        //callApi()
    }


    fun refreshData(item: MenuItem) {
        callApi()
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        //return super.onCreateOptionsMenu(menu)

        menuInflater.inflate(R.menu.menu_main, menu)

        val spinner = findViewById<Spinner>(R.id.country_spinner)
        spinner.onItemSelectedListener = this
        // Create an ArrayAdapter using the string array and a default spinner layout
        val adapter = ArrayAdapter.createFromResource(this,
                R.array.countries_array, android.R.layout.simple_spinner_item)
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        // Apply the adapter to the spinner
        spinner.adapter = adapter

        return true
    }


    /*override fun onResume() {
        super.onResume()
        val toast = Toast.makeText(applicationContext, "calling api...", Toast.LENGTH_SHORT)
        toast.show()
        callApi()
    }*/


    private fun callApi() {

        println("loading articles...")

        //val proxyTest = Proxy(Proxy.Type.HTTP, InetSocketAddress("192.168.51.99", 3128))

        //val builder= OkHttpClient.Builder().proxy(proxyTest)
        val client = OkHttpClient()

        val request = Request.Builder()
                .url(url)
                .build()

        // Get a handler that can be used to post to the main thread
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                runOnUiThread {
                    val toast = Toast.makeText(applicationContext, e.message, Toast.LENGTH_LONG)
                    toast.show()
                }
            }

            @Throws(IOException::class)
            override fun onResponse(call: Call, response: Response) {

                val responseData = response.body()?.string()
                println(responseData)

                /*val json = JSONObject(responseData)
                val totalResults=json.getString("TotalResults")
                Log.d("Debug", totalResults.toString())
                println(totalResults.toString())*/

                val parser = JsonParser()
                val mJson = parser.parse(responseData)
                val gson = GsonBuilder().create()
                val articleFeed = gson.fromJson(mJson, ArticleResult::class.java)

                if (!response.isSuccessful) {
                    runOnUiThread {
                        val toast = Toast.makeText(applicationContext, "api call failed", Toast.LENGTH_SHORT)
                        toast.show()
                    }
                    throw IOException("Unexpected code $response")
                } else {
                    if (articleFeed != null) {
                        Log.d("Debug", "Finished loading: " + articleFeed.TotalResults.toString() + " articles")

                        runOnUiThread {
                            val toast = Toast.makeText(applicationContext, "Finished loading: " + articleFeed.TotalResults.toString() + " articles", Toast.LENGTH_SHORT)
                            toast.show()

                            aResult = articleFeed

                            val recyclerAdapter = ArticlesRecyclerAdapter(applicationContext, articleFeed)
                            val recyclerView = findViewById<View>(R.id.rvArticles) as RecyclerView

                            recyclerView.adapter = recyclerAdapter
                            recyclerView.layoutManager = LinearLayoutManager(applicationContext)

                        }
                    }


                }
            }
        })
    }
}
