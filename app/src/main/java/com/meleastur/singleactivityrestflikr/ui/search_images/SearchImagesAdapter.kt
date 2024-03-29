package com.meleastur.singleactivityrestflikr.ui.search_images

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade
import com.google.android.material.snackbar.Snackbar
import com.meleastur.singleactivityrestflikr.R
import com.meleastur.singleactivityrestflikr.common.glide.GlideApp
import com.meleastur.singleactivityrestflikr.common.glide.GlideAppModule
import com.meleastur.singleactivityrestflikr.helper.room.SearchImage
import java.net.URL


class SearchImagesAdapter internal constructor(
    private val context: Context,
    var searchImageList: ArrayList<SearchImage>, fragment: Fragment
) : RecyclerView.Adapter<SearchImagesAdapter.ListViewHolder>() {

    private val listener: ItemClickListener
    private val fragment: Fragment
    private val height = 200
    private val width = 200

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var searchImages = emptyList<SearchImage>() // Cached copy of words

    init {
        this.listener = fragment as ItemClickListener
        this.fragment = fragment
    }

    inner class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var layout: RelativeLayout =
            itemView.findViewById(R.id.item_parent)
        val image: AppCompatImageView =
            itemView.findViewById(R.id.image_thumbnail)
        val title: TextView =
            itemView.findViewById(R.id.image_title)
        val author: TextView =
            itemView.findViewById(R.id.image_author)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val itemView = inflater.inflate(R.layout.item_search_images, parent, false)
        return ListViewHolder(itemView)
    }

    internal fun setSearchImages(words: List<SearchImage>) {
        this.searchImages = words
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val searchImage = searchImageList[position]
        var positionAux = position

        holder.title.text = searchImage.title
        holder.author.text = context.getString(R.string.item_author, searchImage.author)

        try {
            val url = URL(searchImage.thumbnailURL)

            GlideApp.with(fragment)
                .load(url)
                .apply(GlideAppModule.optionsGlide)
                .centerCrop()
                .override(width, height)
                .transition(withCrossFade())
                .into(holder.image)
        } catch (e: Exception) {
            Log.e(
                "SearchImages", "thumbnailURL " + searchImage.thumbnailURL
                        + " - onBindViewHolder error " + e.message
            )
            GlideApp.with(fragment)
                .load(R.drawable.ic_photo)
                .apply(GlideAppModule.optionsGlide)
                .centerCrop()
                .override(width, height)
                .transition(withCrossFade())
                .into(holder.image)
        }

        holder.layout.setOnClickListener {
            listener.itemDetail(searchImage)
        }

        if(positionAux == 0){
            positionAux = 1
        }
        listener.itemPositionChange(searchImage.page, searchImageList.size - 1, positionAux)

        if (position == searchImageList.size - 1) {
            listener.itemBottomReached()
        }
    }

    override fun getItemCount(): Int {
        return searchImageList.size
    }

    fun showSnackRestartGlide(imageView: ImageView, urlGlide: URL) {
        Snackbar
            .make(imageView, "Error en la descarga", Snackbar.LENGTH_LONG)
            .setAction("Reintentar") {
                GlideApp.with(fragment)
                    .load(urlGlide)
                    .placeholder(R.drawable.ic_photo)
                    .transition(withCrossFade())
                    .apply(GlideAppModule.optionsGlide)
                    .transition(withCrossFade())
                    .into(imageView)
            }.show()
    }

   

    interface ItemClickListener {
        fun itemDetail(searchImage: SearchImage)

        fun itemPositionChange(page: Int, perPage: Int, position: Int)

        fun itemBottomReached()
    }
}