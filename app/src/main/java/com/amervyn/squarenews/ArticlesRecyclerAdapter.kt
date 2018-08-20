package com.amervyn.squarenews

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView


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

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val articleItem= aResult?.Articles!![position]

    }

    inner class ViewHolder// We also create a constructor that accepts the entire item row
    // and does the view lookups to find each subview
    (itemView: View) : RecyclerView.ViewHolder(itemView) {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row

        var nameTextView: ImageView = itemView.findViewById(R.id.article_image) as ImageView

        // to access the context from any ViewHolder instance.
    }


}