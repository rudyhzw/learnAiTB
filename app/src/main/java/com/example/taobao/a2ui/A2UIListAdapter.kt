package com.example.taobao.a2ui

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class A2UIListAdapter(
    private val context: Context,
    private val surface: A2UISurface,
    private val surfaceManager: A2UISurfaceManager,
    private val dataList: List<*>,
    private val templateId: String
) : RecyclerView.Adapter<A2UIListAdapter.ViewHolder>() {

    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = surface.getComponent(templateId)?.let { template ->
            A2UIRenderer(context, surface, surfaceManager).render(template)
        } ?: View(context)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = dataList.getOrNull(position) ?: return
        surface.getComponent(templateId)?.let { template ->
            val renderer = A2UIRenderer(context, surface, surfaceManager)
            val itemView = renderer.renderWithData(template, data, position)
            holder.view.layoutParams = itemView.layoutParams
            if (holder.view is ViewGroup) {
                (holder.view as ViewGroup).removeAllViews()
                holder.view.addView(itemView)
            }
        }
    }

    override fun getItemCount(): Int = dataList.size
}
