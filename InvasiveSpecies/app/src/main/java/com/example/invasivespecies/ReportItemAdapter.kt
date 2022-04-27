package com.example.invasivespecies

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.RecyclerView

class ReportItemAdapter(private val mContext: Context) :
    RecyclerView.Adapter<ReportItemAdapter.ViewHolder>() {

    private val mItems = ArrayList<Report>()

    fun add(item: Report) {
        mItems.add(item)
//        notifyItemChanged(mItems.size)
    }

    // Clears the list adapter of all items.
    fun clear() {
        mItems.clear()
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return mItems.size
    }

    class ViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var mItemLayout: View = itemView
        var mImageView: ImageView? = null
        var mNameTextView: TextView? = null
        var mColorTextView: TextView? = null
        var mAmountTextView: TextView? = null
        var mNotesTextView: TextView? = null
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.view_reports_item, parent, false)
        val viewHolder = ViewHolder(v)

        viewHolder.mItemLayout = v as RelativeLayout
        viewHolder.mImageView = viewHolder.mItemLayout.findViewById<ImageView>(R.id.plant_item_image)
        viewHolder.mNameTextView = viewHolder.mItemLayout.findViewById<TextView>(R.id.plant_item_name)
        viewHolder.mColorTextView = viewHolder.mItemLayout.findViewById<TextView>(R.id.plant_item_color)
        viewHolder.mAmountTextView = viewHolder.mItemLayout.findViewById<TextView>(R.id.plant_name_amount)
        viewHolder.mNotesTextView = viewHolder.mItemLayout.findViewById<TextView>(R.id.plant_item_notes)

        return viewHolder
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

        if (position < mItems.size) {
            val reportItem = mItems[position]

//            viewHolder.mImageView
            viewHolder.mNameTextView!!.text = reportItem.plantname
            viewHolder.mColorTextView!!.text = reportItem.color
            viewHolder.mAmountTextView!!.text = reportItem.amount
            viewHolder.mNotesTextView!!.text = reportItem.notes
        }
    }

    companion object {
        private const val TAG = "Report-Item-Adapter"
        private const val VIEW_REPORTS_TYPE = R.layout.activity_view_reports
        private const val REPORT_ITEM_VIEW_TYPE = R.layout.view_reports_item
    }

}