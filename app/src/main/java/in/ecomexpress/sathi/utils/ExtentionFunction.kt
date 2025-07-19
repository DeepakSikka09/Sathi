package `in`.ecomexpress.sathi.utils

import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout

fun TabLayout.setupWithViewPager2(viewPager2: ViewPager2) {
    fun updateTabs() {
        removeAllTabs()
        viewPager2.adapter?.let { adapter ->
            repeat(adapter.itemCount) {
                addTab(newTab())
            }
        }
    }

    updateTabs()

    viewPager2.adapter?.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
        override fun onChanged() = updateTabs()
    })

    viewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            getTabAt(position)?.select()
        }
    })

    addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
        override fun onTabSelected(tab: TabLayout.Tab) {
            viewPager2.currentItem = tab.position
        }

        override fun onTabUnselected(tab: TabLayout.Tab) {}

        override fun onTabReselected(tab: TabLayout.Tab) {}
    })
}