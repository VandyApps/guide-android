package edu.vanderbilt.vm.guide;

/**
 * @author Athran
 * Origin: GuideMain
 * Desc: A home page for interaction with Place
 * NavigateTo: WebMap
 */

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import edu.vanderbilt.vm.guide.util.GlobalState;
import edu.vanderbilt.vm.guide.util.JsonUtils;
import edu.vanderbilt.vm.guide.util.Place;

public class PlaceDetailActivity extends Activity implements OnClickListener {
	
	TextView PlaceName;
	ImageView PlaceImage;
	TextView PlaceDescription;
	Button BMap;
	Bitmap Image;
	String url;
	
	//Build a prototype place for testing
	private static final Place DUMMY_PLACE;
	static {
		Place.Builder pb = new Place.Builder();
		DUMMY_PLACE = pb.setLatitude(36.144371)
				.setLongitude(-86.802442)
				.setName("Featheringhill Hall")
				.setDescription("The path of the righteous man is beset " +
						"on all sides by the iniquities of the selfish and " +
						"the tyranny of evil men. Blessed is he who, in " +
						"the name of charity and good will, shepherds " +
						"the weak through the valley of darkness, for " +
						"he is truly his brother's keeper and the " +
						"finder of lost children. And I will strike " +
						"down upon thee with great vengeance and " +
						"furious anger those who would attempt to " +
						"poison and destroy My brothers. And you " +
						"will know My name is the Lord when " +
						"I lay My vengeance upon \n thee. \n" +
						"Now that we know who you are, I know who " +
						"I am. I'm not a mistake! It all makes " +
						"sense! In a comic, you know how you can " +
						"tell who the arch-villain's going to be? " +
						"He's the exact opposite of the hero. And " +
						"most times they're friends, like you and " +
						"me! I should've known way back when... " +
						"You know why, David? Because of the kids. " +
						"They called me Mr Glass.") // http://slipsum.com/ LOL
				.setHours("7:00 - 22:00")
				.setCategory("Academic")
				.setUniqueId(1)
				.build();
	}
	
	@Override
	public void onCreate(Bundle SavedInstanceState){
		super.onCreate(SavedInstanceState);
		setContentView(R.layout.activity_place_detail);
		
		/**
		 * Sets the content of the page based on data from Place
		 */
		Place place = GlobalState.getPlaceById(1);
		
		PlaceName = (TextView)findViewById(R.id.PlaceName);
		if (place != null){
			PlaceName.setText(place.getName());
		} else {
			PlaceName.setText(DUMMY_PLACE.getName());
		}
		
		PlaceImage = (ImageView)findViewById(R.id.PlaceImage);
		PlaceImage.setImageResource(R.drawable.ic_launcher);
		
		PlaceDescription = (TextView)findViewById(R.id.PlaceDescription);
		PlaceDescription.setText(DUMMY_PLACE.getDescription());
		
		/**
		 * Set Behaviour
		 */
		Thread downloadImage = new Thread() {
		    @Override
		    public void run() {
		        try {
    	            InputStream is = (InputStream) new URL(url).getContent();
    	            Log.d(getClass().getSimpleName(), "Download succeeded");
    	            Image = BitmapFactory.decodeStream(is);
    	        } catch (Exception e) {
    	            Log.d(getClass().getSimpleName(), "Download failed");
    	            Image = null;
    	        }
		    }
		};
		downloadImage.start();
        try {
            downloadImage.join();
            PlaceImage.setImageBitmap(Image);
        } catch (InterruptedException e) {
            Log.d(getClass().getSimpleName(), "Download failed", e);
            //Error Handle
        }
		BMap = (Button)findViewById(R.id.BMap);
		BMap.setOnClickListener(this);
		
	}
	
	public void onClick(View view){
		Intent i = new Intent(this,WebMap.class);
		i.putExtra("Lat",Double.toString(DUMMY_PLACE.getLatitude()));
		i.putExtra("Long",Double.toString(DUMMY_PLACE.getLongitude()));
		startActivity(i);
	}
	
	
}

