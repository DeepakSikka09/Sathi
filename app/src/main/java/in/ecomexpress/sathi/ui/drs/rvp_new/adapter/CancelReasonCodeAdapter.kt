package `in`.ecomexpress.sathi.ui.drs.rvp_new.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.radiobutton.MaterialRadioButton
import com.google.android.material.textview.MaterialTextView
import `in`.ecomexpress.sathi.R
import `in`.ecomexpress.sathi.repo.local.data.rvp.RadioButtonItem

class CancelReasonCodeAdapter(private var itemList: MutableList<RadioButtonItem>,private val onReasonSelected: (String) -> Unit) : RecyclerView.Adapter<CancelReasonCodeAdapter.ViewHolder>() {

    private var selectedPosition = -1 // Keep track of the selected radio button


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_cancel_reason_code, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = itemList[position]
        holder.textView.text = item.text
        holder.radioButton.isChecked = position == selectedPosition

        // Handle radio button selection
        // Trigger the callback when an item is selected
        holder.radioButton.setOnClickListener {
            updateSelection(holder.adapterPosition)
            onReasonSelected(item.text) // Call the lambda function with selected reason
        }

        holder.itemView.setOnClickListener {
            updateSelection(holder.adapterPosition)
            onReasonSelected(item.text,) // Call the lambda function with selected reason
        }
    }
    private fun updateSelection(position: Int) {
        if (selectedPosition != position) {
            val oldPosition = selectedPosition
            selectedPosition = position
            notifyItemChanged(oldPosition) // Notify old item to update
            notifyItemChanged(selectedPosition) // Notify new item to update
        }
    }
    // New method to get selected reason text
    fun getSelectedReason(): String? {
        return if (selectedPosition != -1 && selectedPosition < itemList.size) {
            itemList[selectedPosition].text
        } else {
            null
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(newReasonList: List<RadioButtonItem>) {
        //itemList.clear()
        itemList.addAll(newReasonList)
        notifyDataSetChanged() // Notify adapter to refresh the list
    }
    fun getSelectedPosition(): Int {
        return selectedPosition
    }

    override fun getItemCount(): Int = itemList.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val radioButton: MaterialRadioButton = itemView.findViewById(R.id.icon_check)
        val textView: MaterialTextView = itemView.findViewById(R.id.txt_reason_particular)
    }
}
