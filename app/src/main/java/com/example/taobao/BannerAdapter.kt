package com.example.taobao

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class BannerAdapter(
    private val banners: List<Map<String, Any>>
) : RecyclerView.Adapter<BannerAdapter.BannerViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BannerViewHolder {
        val view = View(parent.context)
        view.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        return BannerViewHolder(view)
    }

    override fun onBindViewHolder(holder: BannerViewHolder, position: Int) {
        holder.bind(banners[position])
    }

    override fun getItemCount(): Int = banners.size

    class BannerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(banner: Map<String, Any>) {
            val color = banner["color"]?.toString() ?: "#FF6600"
            itemView.setBackgroundColor(Color.parseColor(color))
        }
    }
}
