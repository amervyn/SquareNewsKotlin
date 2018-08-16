package com.amervyn.squarenews

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.Snackbar
import kotlinx.android.synthetic.main.activity_scrolling.*
import okhttp3.*
import org.json.JSONObject
import java.io.IOException
import java.net.InetSocketAddress
import java.net.Proxy

class MainActivity : AppCompatActivity() {

    val url="http://amervyn.duckdns.org/api/Articles"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }
        CallApi()
    }


    private fun CallApi() {
        println("calling api")

        val proxyTest = Proxy(Proxy.Type.HTTP, InetSocketAddress("192.168.51.99", 3128))

        val builder= OkHttpClient.Builder().proxy(proxyTest)
        val client=builder.build()

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
                val json = JSONObject(responseData)

                if (!response.isSuccessful)
                {
                    throw IOException("Unexpected code $response")
                }
            }
        })
    }
}
