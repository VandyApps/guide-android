package edu.vanderbilt.vm.guide;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.app.SherlockFragmentActivity;

public class GuideMain extends SherlockFragmentActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide_main);
        setupActionBar();
    }
    
    private void setupActionBar() {
    	ActionBar ab = getSupportActionBar();
    	ab.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
    	ab.setDisplayShowTitleEnabled(false);
    	
    	Tab tab = ab.newTab()
    			.setText("Agenda")
    			.setTabListener(new TabListener<AgendaFragment>(
    					this, "agenda", AgendaFragment.class));
    	ab.addTab(tab);
    	
    	
    }

    
    /**
     * TabListener static inner class.  TabListeners handle callbacks resulting
     * from a tab click.  We will be using this to swap in and out fragments
     * as a user interacts with the UI.  This code was borrowed from the Android
     * API guides at http://developer.android.com/guide/topics/ui/actionbar.html#Tabs.
     * @author nicholasking
     *
     * @param <T> The fragment's class
     */
    @TargetApi(11)
	public static class TabListener<T extends Fragment> implements ActionBar.TabListener {
        private Fragment mFragment;
        private final Activity mActivity;
        private final String mTag;
        private final Class<T> mClass;

        /** Constructor used each time a new tab is created.
          * @param activity  The host Activity, used to instantiate the fragment
          * @param tag  The identifier tag for the fragment
          * @param clz  The fragment's Class, used to instantiate the fragment
          */
        public TabListener(Activity activity, String tag, Class<T> clz) {
            mActivity = activity;
            mTag = tag;
            mClass = clz;
        }

        /* The following are each of the ActionBar.TabListener callbacks */

        public void onTabSelected(Tab tab, FragmentTransaction ft) {
            // Check if the fragment is already initialized
            if (mFragment == null) {
                // If not, instantiate and add it to the activity
                mFragment = Fragment.instantiate(mActivity, mClass.getName());
                ft.add(android.R.id.content, mFragment, mTag);
            } else {
                // If it exists, simply attach it in order to show it
                ft.attach(mFragment);
            }
        }

        public void onTabUnselected(Tab tab, FragmentTransaction ft) {
            if (mFragment != null) {
                // Detach the fragment, because another one is being attached
                ft.detach(mFragment);
            }
        }

        public void onTabReselected(Tab tab, FragmentTransaction ft) {
            // User selected the already selected tab. Usually do nothing.
        }
    }
    
}
