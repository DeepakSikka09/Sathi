package `in`.ecomexpress.sathi.ui.drs.rvp_new.adapter

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import `in`.ecomexpress.sathi.R
import `in`.ecomexpress.sathi.ui.drs.rvp_new.activity.CancelPickupRemarksActivity


class UndeliveredCalenderRVPAdapter(
    private val itemList: List<CancelPickupRemarksActivity.DateItem>,
    private val onDateSelected: (CancelPickupRemarksActivity.DateItem) -> Unit
   // private val onItemClick: (CancelPickupRemarksActivity.DateItem) -> Unit
) : RecyclerView.Adapter<UndeliveredCalenderRVPAdapter.ViewHolder>() {

    private var selectedPosition = -1 // Track the selected position
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.undelivered_calender_grid, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, @SuppressLint("RecyclerView") position: Int) {
        val item = itemList[position]

        // Set date and day values
        holder.dateTextView.text = item.date
        holder.dayTextView.text = item.dayName

        if (position == selectedPosition) {
            holder.itemView.setBackgroundResource(R.drawable.item_selected)
            holder.dateTextView.setTextColor(Color.BLACK)
            holder.dayTextView.setTextColor(Color.BLACK)
        } else {
            holder.itemView.setBackgroundResource(R.drawable.item_default)
            holder.dateTextView.setTextColor(Color.BLACK)
            holder.dayTextView.setTextColor(Color.BLACK)
        }

        // Handle item clicks
        holder.itemView.setOnClickListener {
            val previousPosition = selectedPosition
            selectedPosition = position
            notifyItemChanged(previousPosition)
            notifyItemChanged(selectedPosition)
            onDateSelected(item)
        }
    }

    override fun getItemCount(): Int = itemList.size

    // ViewHolder class
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val dateTextView: TextView = itemView.findViewById(R.id.tv_date)
        val dayTextView: TextView = itemView.findViewById(R.id.tv_weekday)
    }
}