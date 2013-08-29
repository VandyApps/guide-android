
package edu.vanderbilt.vm.guide.ui.adapter;

import java.util.ArrayList;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.app.SherlockFragmentActivity;

import edu.vanderbilt.vm.guide.ui.AgendaFragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;

public class SwipingTabsAdapter extends FragmentPagerAdapter implements ActionBar.TabListener,
        ViewPager.OnPageChangeListener {
    private final SherlockFragmentActivity mActivity;

    private final ActionBar mActionBar;

    private final ViewPager mViewPager;

    private final ArrayList<TabInfo> mTabs = new ArrayList<TabInfo>();

    private AgendaFragment mAgendaFragment;

    static final class TabInfo {
        private final Class<?> clss;

        private final Bundle args;

        TabInfo(Class<?> _class, Bundle _args) {
            clss = _class;
            args = _args;
        }
    }

    public SwipingTabsAdapter(SherlockFragmentActivity activity, ViewPager pager) {
        super(activity.getSupportFragmentManager());
        mActivity = activity;
        mActionBar = activity.getSupportActionBar();
        mViewPager = pager;
        mViewPager.setAdapter(this);
        mViewPager.setOnPageChangeListener(this);
    }

    public void addTab(ActionBar.Tab tab, Class<?> clss, Bundle args) {
        TabInfo info = new TabInfo(clss, args);
        tab.setTag(info);
        tab.setTabListener(this);
        mTabs.add(info);
        mActionBar.addTab(tab);
        notifyDataSetChanged();
    }

    @Override
    public void onPageScrollStateChanged(int arg0) {
        updateAgendaFragment();
    }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {
        updateAgendaFragment();
    }

    private void updateAgendaFragment() {
        if (mAgendaFragment != null) {
            //mAgendaFragment.onReselect();
        }
    }

    @Override
    public void onPageSelected(int position) {
        updateAgendaFragment();
        mActionBar.setSelectedNavigationItem(position);
    }

    @Override
    public void onTabReselected(Tab tab, FragmentTransaction ft) {
        updateAgendaFragment();
    }

    @Override
    public void onTabSelected(Tab tab, FragmentTransaction ft) {
        TabInfo tag = (TabInfo)tab.getTag();
        for (int i = 0; i < mTabs.size(); i++) {
            if (mTabs.get(i) == tag) {
                mViewPager.setCurrentItem(i);
                if (tag.clss.equals(AgendaFragment.class) && mAgendaFragment != null) {
                    //mAgendaFragment.onReselect();
                }
            }
        }
    }

    @Override
    public void onTabUnselected(Tab tab, FragmentTransaction ft) {
    }

    @Override
    public SherlockFragment getItem(int position) {
        TabInfo info = mTabs.get(position);
        SherlockFragment fragment = (SherlockFragment) Fragment.instantiate(mActivity, info.clss.getName(), info.args);
        if (info.clss.equals(AgendaFragment.class)) {
            //mAgendaFragment = (AgendaFragment) fragment;
        }
        return fragment;
    }

    @Override
    public int getCount() {
        return mTabs.size();
    }
}
