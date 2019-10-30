package com.meleastur.singleactivityrestflikr.ui.search_images

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.meleastur.singleactivityrestflikr.model.SearchImage
import java.net.URL


class SearchImagesAdapter(
    private val context: Context,
    var searchImageList: ArrayList<SearchImage>, fragment: Fragment
) : RecyclerView.Adapter<SearchImagesAdapter.ListViewHolder>() {

    private val listener: SearchImagesAdapter.onItemClickListener
    private val fragment: Fragment

    init {
        this.listener = fragment as SearchImagesAdapter.onItemClickListener
        this.fragment = fragment
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        var searchImage = searchImageList[position]
        listener.itemPositionChange(searchImage.page, searchImageList.size - 1, position + 1)

        holder.title.text = searchImage.title
        holder.author.text = searchImage.author

        // TODO
        //  Checkear searchImage.thumbnailURL en el parseo del Presenter
        try {
            val url = URL(searchImage.thumbnailURL)
            Glide.with(fragment)
                .load(url)
                .centerCrop()
                .into(holder.image)
        } catch (e: Exception) {
            Log.e("onBindViewHolder", "SearchImages - Glide: " + e.localizedMessage.toString())
            Glide.with(fragment)
                .load(com.meleastur.singleactivityrestflikr.R.drawable.ic_photo)
                .centerCrop()
                .into(holder.image)
        }

        holder.layout!!.setOnClickListener {
            listener.itemDetail(searchImage)
        }

        if (position == searchImageList.size - 1) {
            listener.itemBottomReached()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val itemView = LayoutInflater.from(context)
            .inflate(com.meleastur.singleactivityrestflikr.R.layout.item_search_images, parent, false)
        val holder = SearchImagesAdapter.ListViewHolder(itemView)
        holder.setIsRecyclable(false)
        return holder
    }

    override fun getItemCount(): Int {
        return searchImageList.size
    }

    class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var layout =
            itemView.findViewById<RelativeLayout>(com.meleastur.singleactivityrestflikr.R.id.item_parent)
        val image =
            itemView.findViewById<ImageView>(com.meleastur.singleactivityrestflikr.R.id.image_thumbnail)
        val title =
            itemView.findViewById<TextView>(com.meleastur.singleactivityrestflikr.R.id.image_title)
        val author =
            itemView.findViewById<TextView>(com.meleastur.singleactivityrestflikr.R.id.image_author)
    }

    interface onItemClickListener {
        fun itemDetail(searchImage: SearchImage)

        fun itemPositionChange(page: Int, perPage: Int, position: Int)

        fun itemBottomReached()
    }
}