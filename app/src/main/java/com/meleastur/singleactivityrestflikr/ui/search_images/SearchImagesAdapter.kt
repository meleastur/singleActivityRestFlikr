package com.meleastur.singleactivityrestflikr.ui.search_images

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Build
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
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.meleastur.singleactivityrestflikr.model.SearchImage
import java.net.URL
import com.google.android.material.snackbar.Snackbar
import com.meleastur.singleactivityrestflikr.R
import com.meleastur.singleactivityrestflikr.util.Constants


class SearchImagesAdapter(
    private val context: Context,
    var searchImageList: ArrayList<SearchImage>, fragment: Fragment
) : RecyclerView.Adapter<SearchImagesAdapter.ListViewHolder>() {

    private val listener: onItemClickListener
    private val fragment: Fragment

    init {
        this.listener = fragment as onItemClickListener
        this.fragment = fragment
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val searchImage = searchImageList[position]

        holder.title.text = searchImage.title
        holder.author.text = searchImage.author

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            holder.image.transitionName = "transition_name_" + position
        }

        val url = URL(searchImage.thumbnailURL)
        Glide.with(fragment)
            .load(url)
            .apply(Constants.optionsGlide)
            .transition(withCrossFade())
            .centerCrop()
            .into(holder.image)

        holder.layout.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                listener.itemDetail(searchImage, holder.image.transitionName)
            }else{
                listener.itemDetail(searchImage, "")
            }
        }

        listener.itemPositionChange(searchImage.page, searchImageList.size - 1, position + 1)

        if (position == searchImageList.size - 1) {
            listener.itemBottomReached()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val itemView = LayoutInflater.from(context)
            .inflate(
                R.layout.item_search_images,
                parent,
                false
            )
        val holder = ListViewHolder(itemView)
        holder.setIsRecyclable(false)
        return holder
    }

    override fun getItemCount(): Int {
        return searchImageList.size
    }

    fun showSnackRestartGlide(imageView: ImageView, urlGlide: URL) {
        Snackbar
            .make(imageView, "Error en la descarga", Snackbar.LENGTH_LONG)
            .setAction("Reintentar") {
                Glide.with(fragment)
                    .load(urlGlide)
                    .placeholder(R.drawable.ic_photo)
                    .transition(withCrossFade())
                    .apply(Constants.optionsGlide)
                    .transition(withCrossFade())
                    .into(imageView)
            }.show()
    }

    class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var layout: RelativeLayout =
            itemView.findViewById(R.id.item_parent)
        val image: ImageView =
            itemView.findViewById(R.id.image_thumbnail)
        val title: TextView =
            itemView.findViewById(R.id.image_title)
        val author: TextView =
            itemView.findViewById(R.id.image_author)
    }

    interface onItemClickListener {
        fun itemDetail(searchImage: SearchImage, transactionName: String)

        fun itemPositionChange(page: Int, perPage: Int, position: Int)

        fun itemBottomReached()
    }
}