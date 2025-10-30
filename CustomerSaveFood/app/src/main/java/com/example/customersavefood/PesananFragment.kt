package com.example.customersavefood

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout


class PesananFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_pesanan, container, false)

        val tabLayout: TabLayout = view.findViewById(R.id.tab_layout)
        val viewPager: ViewPager = view.findViewById(R.id.view_pager)

        // Setup ViewPager with an adapter
        val pagerAdapter = PagerAdapter(childFragmentManager)
        viewPager.adapter = pagerAdapter

        // Connect TabLayout with ViewPager
        tabLayout.setupWithViewPager(viewPager)

        return view
    }

    // PagerAdapter for ViewPager
    private class PagerAdapter(fm: androidx.fragment.app.FragmentManager) : androidx.fragment.app.FragmentPagerAdapter(fm) {

        override fun getItem(position: Int): Fragment {
            // Return your Fragments based on position
            return if (position == 0) {
                DalamProsesFragment() // Create a Fragment for "In Process" tab
            } else {
                SelesaiFragment() // Create a Fragment for "Completed" tab
            }
        }

        override fun getCount(): Int {
            // Return the number of tabs
            return 2
        }

        override fun getPageTitle(position: Int): CharSequence? {
            // Set tab titles
            return if (position == 0) {
                "In Process"
            } else {
                "Completed"
            }
        }
    }
}