package com.amervyn.squarenews

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import android.support.v4.content.ContextCompat.startActivity
import android.content.Intent
import android.net.Uri


class ArticlesRecyclerAdapter(context: Context, aresult: ArticleResult) : RecyclerView.Adapter<ArticlesRecyclerAdapter.ViewHolder>() {

    private var aResult : ArticleResult? = null
    private var mContext : Context? = null
    private lateinit var clickListener : View.OnClickListener

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

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val articleItem= aResult?.Articles!![position]
        holder.headlineTextView.text = articleItem.Headline
        //this.DownloadImageTask(holder.imageView).execute(articleItem.ImageUrl)


        holder.itemView.setOnClickListener {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(articleItem.Url))
            mContext?.startActivity(browserIntent)
        }

        Picasso.with(mContext)
                .load(articleItem.ImageUrl)
                .into(holder.imageView, object : Callback {
                    override fun onSuccess() {
                        holder.pBar.visibility = View.GONE
                    }

                    override fun onError() {
                        Log.d("pBar", "Error")
                        val errorUrl="https://dummyimage.com/600x400/ffffff/ff0000&text=Image+Unavailable"

                        Picasso.with(mContext)
                                .load(errorUrl)
                                .into(holder.imageView)

                    }
                })

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
        // to access the context from any ViewHolder instance.

    }


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
