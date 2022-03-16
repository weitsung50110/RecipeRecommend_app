package com.example.reciperecommend

import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter

class SamplePagerAdapter(private val pageList : MutableList<PageView>) : PagerAdapter() {

    override fun getCount(): Int {
        return pageList.size
    }

    override fun isViewFromObject(view: View, o: Any): Boolean {
        return o === view
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        container.addView(pageList[position])
        return pageList[position]
    }

    override fun destroyItem(container: ViewGroup, position: Int, o: Any) {
        container.removeView(o as View)
    }

}