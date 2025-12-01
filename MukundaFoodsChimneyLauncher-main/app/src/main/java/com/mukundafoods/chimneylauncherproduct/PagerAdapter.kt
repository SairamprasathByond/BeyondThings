package com.mukundafoods.chimneylauncherproduct

import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.mukundafoods.chimneylauncherproduct.ui.chimney.ChimneyControllerFragment
import com.mukundafoods.chimneylauncherproduct.ui.entertainment.EntertainmentFragment
import com.mukundafoods.chimneylauncherproduct.ui.chefconnect.ChefConnectFragment
import com.mukundafoods.chimneylauncherproduct.ui.marketing.MarketingFragment

class PagerAdapter(fm: FragmentManager) :
    FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
    override fun getCount(): Int {
        return if(Variants.IS_MARKETING_APP) 4 else 3;
    }

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> {
                ChimneyControllerFragment()
            }

            1 -> {
                EntertainmentFragment()
            }

            2 -> {
                ChefConnectFragment()
            }

            3 -> {
                MarketingFragment()
            }

            else -> {
                ChimneyControllerFragment()
            }

        }
    }

    override fun getPageTitle(position: Int): CharSequence? {
        when (position) {
            0 -> {
                return ""

            }

            1 -> {
                return ""
            }

            2 -> {
                return ""
            }
            3 -> {
                return ""
            }
        }
        return super.getPageTitle(position)
    }



}