
package edu.vanderbilt.vm.guide.ui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

public class About extends SherlockActivity {

    Logger logger = LoggerFactory.getLogger(SherlockActivity.class);
    
    private static String MAP_TIPS = "map_tips";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getIntent().hasExtra(MAP_TIPS)) {
            @SuppressWarnings("unused")
            LinearLayout layout = new LinearLayout(this);
        } else {
            TextView tvAbout = new TextView(this);
            tvAbout.setText("Source code is available on "
                    + "https://github.com/VandyMobile/guide-android");
            tvAbout.setTextSize(18f);
            tvAbout.setGravity(Gravity.CENTER);
            this.setContentView(tvAbout);

        }
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        boolean isDebuggable =  ( 0 != ( getApplicationInfo().flags &= ApplicationInfo.FLAG_DEBUGGABLE ) );
        if (isDebuggable) {
            menu.add(Menu.NONE, 1, Menu.NONE, "Open Graph Debug Activity");
            return true;
        }
        return false;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == 1) {
            Intent intent = new Intent().setClass(this, GraphUtilsDebugActivity.class);
            startActivity(intent);
            return true;
        } else {
            logger.error("Invalid options item selected");
            return false;
        }
    }

    public static void open(Context ctx) {
        Intent i = new Intent(ctx, About.class);
        ctx.startActivity(i);
    }

    public static void openMapTips(Context ctx) {
        Intent i = new Intent(ctx, About.class);
        i.putExtra(MAP_TIPS, "");
    }
}
