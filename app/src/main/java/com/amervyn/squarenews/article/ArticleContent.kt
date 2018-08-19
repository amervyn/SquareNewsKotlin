package com.amervyn.squarenews.article

import java.time.LocalDateTime
import java.util.*

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 *
 * TODO: Replace all uses of this class before publishing your app.
 */
object ArticleContent {

    /**
     * An array of sample (dummy) items.
     */
    val ITEMS: MutableList<ArticleItem> = ArrayList()

    /**
     * A map of sample (dummy) items, by ID.
     */
    val ITEM_MAP: MutableMap<String, ArticleItem> = HashMap()

    private val COUNT = ITEMS.count()

    //init {
        // Add some sample items.
        /*for (i in 1..COUNT) {
            addItem(createDummyItem(i))
        }*/
    //}

    /*private fun addItem(item: ArticleItem) {
        ITEMS.add(item)
        ITEM_MAP.put(item.id, item)
    }

    private fun createDummyItem(position: Int): ArticleItem {
        return ArticleItem(position.toString(), "Item " + position, makeDetails(position))
    }

    private fun makeDetails(position: Int): String {
        val builder = StringBuilder()
        builder.append("Details about Item: ").append(position)
        for (i in 0..position - 1) {
            builder.append("\nMore details information here.")
        }
        return builder.toString()
    }*/


/*

    Articles:
    ArticleId	149116
    SourceId	1
    NewsApiSourceId	"independent"
    Headline	"London workers are queuing up for free food because they can't afford to eat"
    Description	"Growing number of people forced to choose between rent or food as cost of living continues to soar"
    ImageUrl	"https://static.independent.co.uk/s3fs-public/thumbnails/image/2018/08/16/14/food-handour.hpg-.jpg"
    Url	"https://www.independent.co.uk/news/uk/home-news/workers-jobs-food-banks-money-afford-cost-living-a8494311.html"
    IsVisible	true
    CreatedOn	"2018-08-19T13:25:00"
    PublishedOn	"2018-08-19T13:08:00"
    Country	"gb"
    ViewCount	0
*/



    data class ArticleItem(val ArticleId : Int, val SourceId: Int, val NewsApiSourceId: String,
                           val Headline : String, val Description : String, val ImageUrl : String,
                           val Url : String, val IsAvailable : Boolean, val CreatedOn : LocalDateTime,
                           val PublishedOn : LocalDateTime, val Country : String, val ViewCount : Int) {
        override fun toString(): String = Headline
    }




}
