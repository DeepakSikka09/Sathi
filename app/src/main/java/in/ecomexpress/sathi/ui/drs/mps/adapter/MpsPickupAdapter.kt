package `in`.ecomexpress.sathi.ui.drs.mps.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayout
import `in`.ecomexpress.sathi.R
import `in`.ecomexpress.sathi.databinding.MpsPickupItemsBinding
import `in`.ecomexpress.sathi.repo.remote.model.mps.MpsPickupItem

class MpsPickupAdapter(private val itemList: List<MpsPickupItem>, private val setupIndicator: (ViewPager2, TabLayout) -> Unit) : RecyclerView.Adapter<MpsPickupAdapter.PickupViewHolder>() {

    class PickupViewHolder(val binding: MpsPickupItemsBinding) : RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(item: MpsPickupItem, setupIndicator: (ViewPager2, TabLayout) -> Unit) {
            binding.subProductNumbering.text = "Product #${adapterPosition + 1}"
            binding.itemDescription.text = item.itemDescription

            val imageList = if (item.imageUrls.size == 1 && item.imageUrls[0].contains(",")) {
                item.imageUrls[0].split(",").map { it.trim() }
            } else {
                item.imageUrls
            }

            val validImageUrls = imageList.filter { it.startsWith("http", ignoreCase = true) }
            val finalImageList = validImageUrls.ifEmpty { listOf("Without_Urls") }
            val imageSliderAdapter = MpsImageSliderAdapter(finalImageList)
            binding.qcImageView.adapter = imageSliderAdapter

            if (finalImageList.contains("Without_Urls")) {
                binding.qcImageIndicator.visibility = ViewGroup.GONE
            } else {
                binding.qcImageIndicator.visibility = ViewGroup.VISIBLE
                setupIndicator(binding.qcImageView, binding.qcImageIndicator)
            }

            if (validImageUrls.isNotEmpty()) {
                Glide.with(binding.qcImageView.context)
                    .load(validImageUrls.first())
                    .placeholder(R.drawable.rvp_image_placeholder)
                    .error(R.drawable.rvp_image_placeholder)
                    .into(binding.noQCImageView)
            } else {
                binding.noQCImageView.setImageResource(R.drawable.rvp_image_placeholder)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PickupViewHolder {
        val binding = MpsPickupItemsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PickupViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PickupViewHolder, position: Int) {
        holder.bind(itemList[position], setupIndicator)
    }

    override fun getItemCount(): Int = itemList.size
}