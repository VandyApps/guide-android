package edu.vanderbilt.vm.guide;

/**
 * @author Athran
 * Contains a WebView to display the map overview of a Place
 * from http://maps.google.com/
 * Perhaps easier than trying to figure out the GoogleMap API licensing
 * 
 * Update: is deprecated. Subject to removal soon?
 */

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;

public class WebMap extends Activity{
	WebView browser;
	private String LAT;
	private String LONG;
	
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.webmap);
		
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
		    LAT = extras.getString("Lat");
		    LONG = extras.getString("Long");
		}
		String Url = "http://maps.google.com/maps?z=18&t=k&q=loc:" + LAT + "+" + LONG;
		
		browser = (WebView)findViewById(R.id.webview);
		browser.getSettings().setJavaScriptEnabled(true); //So that it can load the website
		/**
		 * Loading the map for this Place
		 * 
		 * About GoogleMap format: 
		 * http://www.seomoz.org/ugc/everything-you-never-wanted-to-know-about-google-maps-parameters
		 * 		z=12		zoom level 1-20
		 * 		t=m		type m=map k=satellite h=hybrid p=terrain e=GEarth
		 * 		q=loc:##+##	query for a location defined by coordinates
		 */
		browser.loadUrl(Url);
		
	}
	
	
	
}
