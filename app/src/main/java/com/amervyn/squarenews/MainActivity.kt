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
import android.support.design.widget.NavigationView
import android.support.v4.app.DialogFragment
import android.support.v4.view.GravityCompat
import android.support.v4.view.MenuItemCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBar
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
import java.util.*
import android.widget.ArrayAdapter






class ArticleResult(val TotalResults: Int, val Articles: List<ArticleItem>) {
    override fun toString(): String = TotalResults.toString()

}

class ArticleItem(val ArticleId: Int, val SourceId: Int, val NewsApiSourceId: String,
                  val Headline: String, val Description: String, val ImageUrl: String,
                  val Url: String, val IsAvailable: Boolean, val CreatedOn: String,
                  val PublishedOn: String, val Country: String, val ViewCount: Int) {
    override fun toString(): String = Headline
}



class MainActivity : AppCompatActivity() {

    var url = "http://amervyn.duckdns.org/api/Articles/?country=GB&pageSize=50"
    var countryUrl = "http://amervyn.duckdns.org/api/NewsSources"
    private var aResult: ArticleResult? = null
    var oldCountryParam: String = "country=GB"
    private val client = OkHttpClient()
    val spinnerMap = HashMap<Int, String>()
    private lateinit var mDrawerLayout: DrawerLayout


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mDrawerLayout = findViewById(R.id.drawer_layout)

        val navigationView: NavigationView = findViewById(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener { menuItem ->
            // set item as selected to persist highlight
            menuItem.isChecked = true
            // close drawer when item is tapped
            mDrawerLayout.closeDrawers()

            // Add code here to update the UI based on the item selected
            // For example, swap UI fragments here

            true
        }



        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        //toolbar.setLogo(R.drawable.ic_fishnews_home)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        //supportActionBar?.setDisplayShowHomeEnabled(true)
        //supportActionBar?.setDisplayUseLogoEnabled(true)

        val actionbar: ActionBar? = supportActionBar
        actionbar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_left_menu)
        }

        val spinner = findViewById<Spinner>(R.id.bottom_dialog_show_items)

        // Create an ArrayAdapter using the string array and a default spinner layout
        val adapter = ArrayAdapter.createFromResource(this,
                R.array.countries_array, android.R.layout.simple_spinner_item)

        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        // Apply the adapter to the spinner
        spinner.adapter = adapter

        callApi()

        getCountries()
    }


    fun showBottomSheetDialogFragment(view:View?) {

        val bottomSheetFragment = BottomSheetFragment()
        bottomSheetFragment.show(supportFragmentManager, bottomSheetFragment.tag)
    }


    private fun getCountries() {

    }


    override fun onBackPressed() {
        if (this.mDrawerLayout.isDrawerVisible(GravityCompat.START)) {
            this.mDrawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    fun refreshData(item: View) {
        callApi()
    }


    fun openCountrySelection(item: View)
    {

        val fm = this@MainActivity.fragmentManager
        val countrySelectDialogFragment = CountrySelectDialogFragment().newInstance()
        countrySelectDialogFragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.Dialog_FullScreen)

        countrySelectDialogFragment.setOnListItemSelectedListener(object : ItemClickListener{
            override fun onItemClick(countryCode: String, countryName: String) {
                url = url.replace(oldCountryParam, "country=$countryCode")
                oldCountryParam = "country=$countryCode"
                var countryActionLabel=this@MainActivity.findViewById(R.id.action_selected_country) as TextView
                countryActionLabel?.text=countryName
                callApi()
            }

        })

        countrySelectDialogFragment.show(fm, "fragment_select_country")

    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                mDrawerLayout.openDrawer(GravityCompat.START)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        //return super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }


    private fun callApi() {

        println("loading articles...")

        //val proxyTest = Proxy(Proxy.Type.HTTP, InetSocketAddress("192.168.51.99", 3128))

        //val builder= OkHttpClient.Builder().proxy(proxyTest)

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
