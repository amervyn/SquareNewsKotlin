package com.amervyn.squarenews

import android.app.DialogFragment
import android.content.ClipData
import android.view.WindowManager
import android.os.Bundle
import android.support.annotation.Nullable
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import okhttp3.*
import java.io.IOException
import java.util.*


interface ItemClickListener{
    fun onItemClick(countryCode: String, countryName: String)
}


class CountrySelectDialogFragment : DialogFragment() {

    private var mTitleText: TextView? = null
    private var countryCode : String = ""
    private var countryName : String = ""
    private var lv: ListView? = null

    var countryUrl = "http://amervyn.duckdns.org/api/NewsSources"
    var oldCountryParam: String = "country=GB"
    private val client = OkHttpClient()
    val spinnerMap = HashMap<Int, String>()

    var mListener: ItemClickListener? = null


    fun setOnListItemSelectedListener(listener: ItemClickListener) {
        this.mListener = listener
    }


    fun newInstance(): CountrySelectDialogFragment {
        val frag = CountrySelectDialogFragment()
        //val args = Bundle()
        //args.putString("title", title)
        //frag.arguments = args
        return frag
    }


    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val rootView=inflater!!.inflate(R.layout.fragment_select_country, container)

        lv = rootView.findViewById(R.id.country_list) as ListView

        getCountries()

        return rootView
    }

    override fun onResume() {
        // Get existing layout params for the window
        val params = dialog.window!!.attributes
        // Assign window properties to fill the parent
        params.width = WindowManager.LayoutParams.MATCH_PARENT
        params.height = WindowManager.LayoutParams.MATCH_PARENT
        dialog.window!!.attributes = params as android.view.WindowManager.LayoutParams
        // Call super onResume after sizing
        super.onResume()
    }

    override fun onViewCreated(view: View?, @Nullable savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Get field from view
        mTitleText = view!!.findViewById(R.id.title_select_country)
        // Fetch arguments from bundle and set title
        //val title = arguments.getString("title", "Enter Name")
        //dialog.setTitle(title)
        // Show soft keyboard automatically and request focus to field
        //mEditText!!.requestFocus()

        dialog.window!!.setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
    }


    private fun GetCountryName(countryCode: String): String? {
        val loc = Locale("", countryCode)
        if(countryCode=="ZH")
            return "Chinese"

        return loc.displayCountry
    }


    private fun GetCountryCode(countryName: String): String? {
        val loc = Locale("", countryName)
        return loc.isO3Country
    }


    private fun getCountries() {
        val request = Request.Builder()
                .url(countryUrl)
                .build()

        // Get a handler that can be used to post to the main thread
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call?, e: IOException?) {

            }

            override fun onResponse(call: Call?, response: Response?) {
                if(response!!.isSuccessful){
                    val responseData = response.body()?.string()
                    val parser = JsonParser()
                    val mJson = parser.parse(responseData)
                    val gson = GsonBuilder().create()
                    val countries = gson.fromJson(mJson, Array<String>::class.java)
                    val spinnerArray= arrayOfNulls<String>(countries.size)

                    for (i in 0 until countries.size) {
                        spinnerMap[i] = countries[i]
                        spinnerArray[i] = GetCountryName(countries[i])
                    }

                    lv?.onItemClickListener= AdapterView.OnItemClickListener { p0, _, p2, _ ->

                        val item=p0?.getItemAtPosition(p2)
                        countryName=item.toString()

                        val code=spinnerMap[p2]

                        countryCode=code.toString()

                        mListener?.onItemClick(countryCode,countryName)

                        dismiss()
                    }

                    if(isAdded)
                    {
                        activity.runOnUiThread {
                            val adapter: ArrayAdapter<String> = ArrayAdapter<String>(
                                    activity,
                                    android.R.layout.simple_list_item_1,
                                    spinnerArray
                            )

                            /*adapter.sort { lhs, rhs ->
                                lhs.compareTo(rhs)   //or whatever your sorting algorithm
                            }*/

                            //arraylist Append
                            lv!!.adapter = adapter
                        }
                    }



                    /*val spinner = findViewById<Spinner>(R.id.country_spinner)
                    //spinner.onItemSelectedListener =

                    spinner?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
                        override fun onNothingSelected(parent: AdapterView<*>?) {

                        }

                        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                            val item = spinnerMap[parent?.selectedItemPosition]

                            url = url.replace(oldCountryParam, "country=" + item.toString())

                            oldCountryParam = "country=" + item.toString()

                            Log.d("ItemSelected", "New URL: $url")

                            callApi()

                        }*/

                    //}

                    // Create an ArrayAdapter using the string array and a default spinner layout

                    //runOnUiThread {
                        //val adapter = ArrayAdapter<String>(applicationContext, android.R.layout.simple_spinner_item, spinnerArray)
                        //adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        //spinner.adapter = adapter
                    //}



                }
            }
        })
    }

}
