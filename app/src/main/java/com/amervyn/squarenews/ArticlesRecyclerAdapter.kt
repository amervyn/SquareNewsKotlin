package com.amervyn.squarenews

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.AsyncTask
import android.os.Build
import android.support.annotation.RequiresApi
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import com.squareup.picasso.Transformation
import java.net.URL
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*


class ArticlesRecyclerAdapter(context: Context, aresult: ArticleResult) : RecyclerView.Adapter<ArticlesRecyclerAdapter.ViewHolder>() {

    private var aResult : ArticleResult? = null
    private var mContext : Context? = null

    init{
            aResult=aresult
            mContext=context
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater=LayoutInflater.from(mContext)
        val articleView=inflater.inflate(R.layout.article_item,parent,false)
        return ViewHolder(articleView)
    }

    override fun getItemCount(): Int {
        return aResult?.Articles?.count()!!
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val articleItem= aResult?.Articles!![position]
        var hostName=URL(articleItem.Url).host
        hostName=hostName.toLowerCase().replace("www.", "")
        holder.articleSource.text=hostName
        holder.headlineTextView.text = articleItem.Headline

        //val t= Instant.parse(articleItem.PublishedOn)

        val str_date = articleItem.PublishedOn

        val formatter = SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss")
        val date = formatter.parse(str_date) as Date
        val localDate= formatter.parse(LocalDateTime.now().toString())

        var diff=localDate.time-date.time

        val secondsInMilli: Long = 1000
        val minutesInMilli = secondsInMilli * 60
        val hoursInMilli = minutesInMilli * 60

        val elapsedHours = diff / hoursInMilli

        holder.articleAge.text=elapsedHours.toString() + " hours ago"


        //this.DownloadImageTask(holder.imageView).execute(articleItem.ImageUrl)

        if (articleItem.ImageUrl.isNullOrEmpty() || articleItem.ImageUrl.isNullOrBlank())
        {
            holder.pBar.visibility = View.GONE
            Picasso.with(mContext)
                    .load(R.drawable.no_image).transform(CropMiddleFirstPixelTransformation())
                    .into(holder.imageView)
        }
        else
        {
            Picasso.with(mContext)
                    .load(articleItem.ImageUrl).transform(CropMiddleFirstPixelTransformation())
                    .into(holder.imageView, object : Callback {

                        override fun onSuccess() {
                            holder.pBar.visibility = View.GONE
                        }

                        override fun onError() {
                            holder.pBar.visibility = View.GONE
                            Log.d("pBar", "Error")
                            Picasso.with(mContext)
                                    .load(R.drawable.no_image).transform(CropMiddleFirstPixelTransformation())
                                    .into(holder.imageView)

                        }
                    })
        }


        holder.itemView.setOnClickListener {
            //val myWebView: WebView =it.findViewById(R.id.webview)

            //myWebView.loadUrl("http://www.example.com")
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(articleItem.Url))
            mContext?.startActivity(browserIntent)
        }


    }


    inner class CropMiddleFirstPixelTransformation : Transformation {
        private var mWidth: Int = 0
        private var mHeight: Int = 0


        override fun transform(source: Bitmap): Bitmap {
            val width = source.width
            val height = source.height

            val horizontalMiddleArray = IntArray(width)
            source.getPixels(horizontalMiddleArray, 0, width, 0, height / 2, width, 1)

            val verticalMiddleArray = IntArray(height)
            source.getPixels(verticalMiddleArray, 0, 1, width / 2, 0, 1, height)

            val left = getFirstNonWhitePosition(horizontalMiddleArray)
            val right = getLastNonWhitePosition(horizontalMiddleArray)

            val top = getFirstNonWhitePosition(verticalMiddleArray)
            val bottom = getLastNonWhitePosition(verticalMiddleArray)

            mWidth = right - left
            mHeight = bottom - top


            if (!isNegative(left, right, top, bottom)) {
                return source
            }

            val bitmap = Bitmap.createBitmap(source, left, top, mWidth, mHeight)
            source.recycle()
            return bitmap

        }

        private fun isNegative(vararg values: Int): Boolean {
            for (i in values) {
                if (i < 0) {
                    return false
                }
            }
            return true

        }

        private fun getFirstNonWhitePosition(horizontalMiddleArray: IntArray): Int {
            var left = 0
            for (i in horizontalMiddleArray.indices) {
                if (i == 0) {
                    left = horizontalMiddleArray[i]
                }
                if (left != horizontalMiddleArray[i]) {
                    return i
                }
            }
            return -1
        }

        private fun getLastNonWhitePosition(horizontalMiddleArray: IntArray): Int {
            var right = 0
            val length = horizontalMiddleArray.size
            for (i in length - 1 downTo 1) {
                if (i == length - 1) {
                    right = horizontalMiddleArray[i]
                }
                if (right != horizontalMiddleArray[i]) {
                    return i
                }
            }
            return -1
        }


        override fun key(): String {
            return "CropMiddleFirstPixelTransformation(width=$mWidth, height=$mHeight)"
        }
    }




    inner class ViewHolder// We also create a constructor that accepts the entire item row
    // and does the view lookups to find each subview
    (itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

        init{
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            Log.d("RecyclerView", "CLICK")
        }
        // Your holder should contain a member variable
        // for any view that will be set as you render a row

        var headlineTextView: TextView = itemView.findViewById(R.id.article_headline) as TextView

        var imageView: ImageView = itemView.findViewById(R.id.article_image) as ImageView

        var pBar : ProgressBar = itemView.findViewById(R.id.image_progress) as ProgressBar

        var articleSource:TextView=itemView.findViewById(R.id.article_source) as TextView

        var articleAge:TextView=itemView.findViewById(R.id.article_age) as TextView
        // to access the context from any ViewHolder instance.

    }


    //DEPRECATED
    internal inner class DownloadImageTask(var bmImage: ImageView) : AsyncTask<String, Void, Bitmap>() {

        override fun onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute()
            //pd.show()
        }

        override fun doInBackground(vararg urls: String): Bitmap? {
            val urldisplay = urls[0]
            var mIcon11: Bitmap? = null
            try {
                val `in` = java.net.URL(urldisplay).openStream()
                mIcon11 = BitmapFactory.decodeStream(`in`)
            } catch (e: Exception) {
                Log.e("Error", e.message)
                e.printStackTrace()
            }

            return mIcon11
        }

        override fun onPostExecute(result: Bitmap) {
            super.onPostExecute(result)
            //pd.dismiss()
            bmImage.setImageBitmap(result)
        }
    }



}
