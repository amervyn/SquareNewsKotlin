package com.amervyn.squarenews.article

import java.util.ArrayList
import java.util.HashMap

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

    private val COUNT = 25

    init {
        // Add some sample items.
        /*for (i in 1..COUNT) {
            addItem(createDummyItem(i))
        }*/
    }

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





    data class ArticleItem(val id: String, val content: String, val details: String) {
        override fun toString(): String = content
    }




}
