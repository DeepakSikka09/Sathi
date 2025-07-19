package `in`.ecomexpress.sathi.ui.drs.mps.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import `in`.ecomexpress.sathi.R
import `in`.ecomexpress.sathi.databinding.RqcImageSliderItemBinding

class   MpsImageSliderAdapter (private val imageUrls: List<String>) : RecyclerView.Adapter<MpsImageSliderAdapter.SliderViewHolder>() {

    class SliderViewHolder(val binding: RqcImageSliderItemBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(imageUrl: String) {
            if (imageUrl.startsWith("http", ignoreCase = true)) {
                Glide.with(binding.imageView.context)
                    .load(imageUrl)
                    .placeholder(R.drawable.rvp_image_placeholder)
                    .error(R.drawable.rvp_image_placeholder)
                    .into(binding.imageView)
            } else {
                binding.imageView.setImageResource(R.drawable.rvp_image_placeholder)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SliderViewHolder {
        val binding = RqcImageSliderItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SliderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SliderViewHolder, position: Int) {
        val imageUrl = imageUrls[position]
        holder.bind(imageUrl)
    }

    override fun getItemCount(): Int = imageUrls.size
}