package edu.vanderbilt.vm.guide.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

public class Navigator extends Activity {
    
    
    
    
    
    
    public static void open(Context ctx) {
        
        Intent i = new Intent(ctx, Navigator.class);
        ctx.startActivity(i);
        
    }
    
}
