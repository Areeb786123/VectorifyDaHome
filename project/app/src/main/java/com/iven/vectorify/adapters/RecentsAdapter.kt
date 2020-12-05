package com.iven.vectorify.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.materialdialogs.MaterialDialog
import com.iven.vectorify.R
import com.iven.vectorify.VectorifyWallpaper
import com.iven.vectorify.toContrastColor
import com.iven.vectorify.utils.Utils
import com.iven.vectorify.vectorifyPreferences

class RecentsAdapter(
    private val context: Context
) :
    RecyclerView.Adapter<RecentsAdapter.RecentSetupsHolder>() {

    var onRecentClick: ((VectorifyWallpaper) -> Unit)? = null
    private var mRecentSetups = vectorifyPreferences.vectorifyWallpaperSetups

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecentSetupsHolder {
        val recentSetupLayout = LayoutInflater.from(parent.context).inflate(
            R.layout.recent_option,
            parent,
            false
        )
        return RecentSetupsHolder(
            recentSetupLayout
        )
    }

    override fun getItemCount(): Int {
        return mRecentSetups?.size!!
    }

    override fun onBindViewHolder(holder: RecentSetupsHolder, position: Int) {
        holder.bindItems(mRecentSetups?.get(position)!!)
    }

    inner class RecentSetupsHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindItems(vectorifyWallpaper: VectorifyWallpaper) {

            val drawable =
                Utils.tintDrawable(
                    context,
                    vectorifyWallpaper.resource,
                    vectorifyWallpaper.vectorColor.toContrastColor(vectorifyWallpaper.backgroundColor)
                )

            itemView.findViewById<ImageButton>(R.id.recent_setups_vector).apply {
                setBackgroundColor(vectorifyWallpaper.backgroundColor)
                setImageDrawable(drawable)
                setOnClickListener {
                    onRecentClick?.invoke(vectorifyWallpaper)
                }
                setOnLongClickListener {

                    MaterialDialog(context).show {

                        title(R.string.title_recent_setups)
                        message(
                            text = context.getString(
                                R.string.message_clear_single_recent_setup,
                                adapterPosition.toString()
                            )
                        )
                        positiveButton {
                            //add an empty list to preferences
                            try {
                                if (mRecentSetups?.contains(vectorifyWallpaper)!!) {
                                    mRecentSetups?.remove(
                                        vectorifyWallpaper
                                    )
                                }
                                notifyDataSetChanged()
                                vectorifyPreferences.vectorifyWallpaperSetups = mRecentSetups
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                        negativeButton { dismiss() }
                    }

                    return@setOnLongClickListener true
                }
            }
        }
    }
}
