package cai288.cs371m.project;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;

/**
 * Created by Cynthia on 10/28/2016.
 */
public class ViewPagerAdapter extends FragmentPagerAdapter{


    private final ArrayList<Fragment> fragmentList;
    private final ArrayList<String> fragmentTitleList;
    Context context;

    public ViewPagerAdapter(FragmentManager fm){
        super(fm);
        fragmentList = new ArrayList<Fragment>();
        fragmentTitleList = new ArrayList<String>();
        this.context = context;
    }

    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }

    public void addFragment(Fragment fragment, String title){
        fragmentList.add(fragment);
        fragmentTitleList.add(title);
    }
    @Override
    public CharSequence getPageTitle(int position){
        return fragmentTitleList.get(position);
    }
}
