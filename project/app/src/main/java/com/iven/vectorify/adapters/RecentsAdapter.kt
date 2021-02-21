package com.iven.vectorify.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.materialdialogs.MaterialDialog
import com.google.android.material.card.MaterialCardView
import com.iven.vectorify.R
import com.iven.vectorify.models.VectorifyWallpaper
import com.iven.vectorify.toContrastColor
import com.iven.vectorify.utils.Utils
import com.iven.vectorify.vectorifyPreferences
import java.util.*

class RecentsAdapter(
        private val ctx: Context
) :
        RecyclerView.Adapter<RecentsAdapter.RecentSetupsHolder>() {

    var onRecentClick: ((VectorifyWallpaper) -> Unit)? = null
    private var mRecentSetups = if (Utils.isDeviceLand(ctx.resources)) {
        vectorifyPreferences.recentSetupsLand
    } else {
        vectorifyPreferences.recentSetups
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecentSetupsHolder {

        return RecentSetupsHolder(LayoutInflater.from(parent.context).inflate(
                R.layout.recent_option,
                parent,
                false)
        )
    }

    override fun getItemCount(): Int {
        return mRecentSetups?.size!!
    }

    override fun onBindViewHolder(holder: RecentSetupsHolder, position: Int) {
        holder.bindItems(mRecentSetups?.get(position)!!)
    }

    inner class RecentSetupsHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindItems(wallpaper: VectorifyWallpaper) {

            val drawable =
                    Utils.tintDrawable(
                            ctx,
                            wallpaper.resource,
                            wallpaper.vectorColor.toContrastColor(wallpaper.backgroundColor)
                    )

            itemView.run {

                contentDescription = ctx.getString(R.string.content_recent, adapterPosition)

                setOnClickListener {
                    onRecentClick?.invoke(wallpaper)
                }

                setOnLongClickListener {
                    performRecentDeletion(adapterPosition)
                    return@setOnLongClickListener true
                }

                (this as MaterialCardView).setCardBackgroundColor(wallpaper.backgroundColor)

                findViewById<ImageView>(R.id.recent_setups_vector).apply {

                    setImageDrawable(drawable)

                    scaleY = wallpaper.scale
                    scaleX = wallpaper.scale

                    // properly calculate image view gravity to match set wallpaper
                    x = (resources.getDimensionPixelOffset(R.dimen.recent_width) * wallpaper.horizontalOffset) / vectorifyPreferences.savedMetrics.width
                    y = (resources.getDimensionPixelOffset(R.dimen.recent_height) * wallpaper.verticalOffset) / vectorifyPreferences.savedMetrics.height
                }
            }
        }
    }

    fun performRecentDeletion(adapterPosition: Int) {

        val wallpaper = mRecentSetups?.get(adapterPosition)

        MaterialDialog(ctx).show {

            title(R.string.title_recent_setups)
            message(
                    text = ctx.getString(
                            R.string.message_clear_single_recent_setup,
                            adapterPosition.toString()
                    )
            )
            positiveButton {
                //add an empty list to preferences
                try {
                    if (mRecentSetups?.contains(wallpaper)!!) {
                        mRecentSetups?.remove(wallpaper)
                    }
                    notifyDataSetChanged()
                    vectorifyPreferences.recentSetups = mRecentSetups
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            negativeButton { dismiss() }
        }
    }
}
