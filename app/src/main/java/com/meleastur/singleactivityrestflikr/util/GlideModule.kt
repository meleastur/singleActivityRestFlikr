package com.meleastur.singleactivityrestflikr.util

import android.content.Context
import android.util.Log.VERBOSE
import com.bumptech.glide.GlideBuilder
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.bitmap_recycle.LruBitmapPool
import com.bumptech.glide.load.engine.cache.LruResourceCache
import com.bumptech.glide.load.engine.cache.MemorySizeCalculator
import com.bumptech.glide.module.AppGlideModule
import com.bumptech.glide.request.RequestOptions
import com.meleastur.singleactivityrestflikr.BuildConfig
import com.meleastur.singleactivityrestflikr.R


@GlideModule
class GlideModule : AppGlideModule() {
    companion object {
        val optionsGlide = RequestOptions()
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .error(R.drawable.ic_report_problem)
    }

    override fun applyOptions(context: Context, builder: GlideBuilder) {
        val calculator = MemorySizeCalculator.Builder(context)
            .setMemoryCacheScreens(3f)
            .setBitmapPoolScreens(3f)
            .build()
        builder.setMemoryCache(LruResourceCache(calculator.memoryCacheSize.toLong()))
            .setBitmapPool(LruBitmapPool(calculator.bitmapPoolSize.toLong()))
            .setLogLevel(VERBOSE)
            .setLogRequestOrigins(BuildConfig.DEBUG)
    }
}