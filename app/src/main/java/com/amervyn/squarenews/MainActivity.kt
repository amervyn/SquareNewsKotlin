package com.amervyn.squarenews

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.FragmentActivity
import android.widget.Toast
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.*
import org.json.JSONObject
import java.io.IOException
import android.R.attr.duration
import android.R.attr.layout
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import java.time.LocalDateTime
import com.google.gson.JsonElement
import com.google.gson.JsonParser
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.LinearLayout


class ArticleResult(val TotalResults : Int, val Articles : List<ArticleItem>) {
    override fun toString(): String = TotalResults.toString()

}

class ArticleItem(val ArticleId : Int, val SourceId: Int, val NewsApiSourceId: String,
                       val Headline : String, val Description : String, val ImageUrl : String,
                       val Url : String, val IsAvailable : Boolean, val CreatedOn : String,
                       val PublishedOn : String, val Country : String, val ViewCount : Int) {
    override fun toString(): String = Headline
}


class MainActivity : AppCompatActivity() {

    val url="http://amervyn.duckdns.org/api/Articles"
    private var aResult: ArticleResult? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //callApi()
    }


    override fun onResume() {
        super.onResume()
        val toast=Toast.makeText(applicationContext, "calling api...", Toast.LENGTH_SHORT)
        toast.show()
        callApi()

        /*val recyclerAdapter=ArticlesRecyclerAdapter(this, this!!.aResult!!)
        val recyclerView=findViewById<View>(R.id.rvArticles) as RecyclerView

        recyclerView.adapter=recyclerAdapter
        recyclerView.layoutManager=LinearLayoutManager(this)*/
    }


    private fun callApi() {

        println("calling api")

        //val proxyTest = Proxy(Proxy.Type.HTTP, InetSocketAddress("192.168.51.99", 3128))

        //val builder= OkHttpClient.Builder().proxy(proxyTest)
        val client=OkHttpClient()

        val request = Request.Builder()
                .url(url)
                .build()

        // Get a handler that can be used to post to the main thread
        client.newCall(request).enqueue(object: Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                runOnUiThread {
                    val toast=Toast.makeText(applicationContext, e.message, Toast.LENGTH_LONG)
                    toast.show()
                }
            }
            @Throws(IOException::class)
            override fun onResponse(call: Call, response: Response) {

                val responseData= response.body()?.string()
                println(responseData)

                /*val json = JSONObject(responseData)
                val totalResults=json.getString("TotalResults")
                Log.d("Debug", totalResults.toString())
                println(totalResults.toString())*/

                val parser = JsonParser()
                val mJson = parser.parse(responseData)
                val gson=GsonBuilder().create()
                val articleFeed=gson.fromJson(mJson, ArticleResult::class.java)

                if (!response.isSuccessful)
                {
                    runOnUiThread {
                        val toast=Toast.makeText(applicationContext, "api call failed", Toast.LENGTH_SHORT)
                        toast.show()
                    }
                    throw IOException("Unexpected code $response")
                }
                else
                {
                    if(articleFeed!=null)
                    {
                        Log.d("Debug", "Total Results: " + articleFeed.TotalResults.toString())

                        runOnUiThread {
                            val toast=Toast.makeText(applicationContext, articleFeed.TotalResults.toString(), Toast.LENGTH_SHORT)
                            toast.show()

                            aResult=articleFeed
                        }
                    }


                }
            }
        })
    }
}
