package edu.vanderbilt.vm.guide;

/**
 * @author Athran
 * This Fragment is the content of the second tab that I added to the main page
 */
import android.annotation.TargetApi;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

@TargetApi(11)
public class PlaceMainFragment extends Fragment implements View.OnClickListener{
	Button btn;
	
	/**
	 * This is an empty constructor
	 * The Dev page says that this is necessary
	 * but the program seems to run fine without it
	 */
	public PlaceMainFragment(){}
	
	/**
	 * I'm not too sure about this initialization
	 * Is this the right way to do it?
	 * Fragment is still confusing for me
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return(inflater.inflate(R.layout.fragment_place,container,false));

	}
	
	public void onActivityCreated(Bundle savedInstanceState){
		super.onActivityCreated(savedInstanceState);
		
		btn = (Button)getActivity().findViewById(R.id.B_PlaceDetail2);
		btn.setOnClickListener(this);
		
	}
	
	/**
	 * The button sends an Intent when clicked
	 * A new Activity is called: "Place Detail"
	 */
	public void onClick(View view){
		startActivity(new Intent(getActivity(), PlaceDetailActivity.class));
	}
	
	
}
