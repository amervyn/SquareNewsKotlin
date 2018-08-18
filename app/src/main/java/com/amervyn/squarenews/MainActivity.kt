package com.amervyn.squarenews

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.amervyn.squarenews.article.ArticleContent
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

class MainActivity : AppCompatActivity() {

    val url="http://amervyn.duckdns.org/api/Articles"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        callApi()
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
            }
            @Throws(IOException::class)
            override fun onResponse(call: Call, response: Response) {

                val responseData= response.body()?.string()
                println(responseData)
                //val json = JSONObject(responseData)
                val gson=GsonBuilder().create()

                val articleFeed=gson.fromJson(responseData, ArticleContent.ArticleItem::class.java)

                if (!response.isSuccessful)
                {
                    throw IOException("Unexpected code $response")
                }
            }
        })
    }
}
