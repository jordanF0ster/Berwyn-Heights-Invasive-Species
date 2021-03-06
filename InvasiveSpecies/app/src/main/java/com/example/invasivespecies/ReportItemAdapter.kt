package com.example.invasivespecies

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage


class ReportItemAdapter(private val mContext: Context) :
    RecyclerView.Adapter<ReportItemAdapter.ViewHolder>() {

    private val mItems = ArrayList<Report>()
    private var database: FirebaseDatabase = FirebaseDatabase.getInstance()

    fun add(item: Report) {
        mItems.add(item)
        notifyItemChanged(mItems.size)
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
        var mCheckBox: CheckBox? = null
        var mCreatorTextView: TextView? = null
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
        viewHolder.mCheckBox = viewHolder.mItemLayout.findViewById<CheckBox>(R.id.plant_item_checkBox)
        viewHolder.mCreatorTextView = viewHolder.mItemLayout.findViewById<TextView>(R.id.plant_item_creator)

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
            viewHolder.mCheckBox!!.isChecked = reportItem.status == Report.Status.DONE
            viewHolder.mCreatorTextView!!.text = "Created by: " + reportItem.creator

            val ONE_MEGABYTE: Long = 1024 * 1024
            val imageRef = Firebase.storage.reference.child(reportItem.id.toString() + ".jpg")
            imageRef.getBytes(ONE_MEGABYTE).addOnSuccessListener {
                // Data for "images/island.jpg" is returned, use this as needed
                val bmp = BitmapFactory.decodeByteArray(it, 0, it.size)
                viewHolder.mImageView!!.setImageBitmap(
                    Bitmap.createScaledBitmap(
                        bmp,
                        viewHolder.mImageView!!.width,
                        viewHolder.mImageView!!.height,
                        false
                    )
                )
            }.addOnFailureListener {
                Log.i(TAG, "Failed to download image: " + it.message)
            }


            viewHolder.mCheckBox!!.setOnCheckedChangeListener{ _, isChecked ->
                if(isChecked) {
                    reportItem.status = Report.Status.DONE
                    mItems.removeAt(viewHolder.adapterPosition)
                    database.getReference("reports").child(reportItem.plantname!!)
                        .child(reportItem.id!!).removeValue()
                    notifyItemRemoved(viewHolder.adapterPosition)
                    notifyItemRangeChanged(viewHolder.adapterPosition,mItems.size)
                } else {
                    reportItem.status = Report.Status.NOTDONE
                }
            }
        }
    }

    companion object {
        private const val TAG = "Report-Item-Adapter"
        private const val VIEW_REPORTS_TYPE = R.layout.activity_view_reports
        private const val REPORT_ITEM_VIEW_TYPE = R.layout.view_reports_item
    }

}