
package edu.vanderbilt.vm.guide.ui;

import java.util.LinkedList;
import java.util.List;

import edu.vanderbilt.vm.guide.R;
import edu.vanderbilt.vm.guide.util.GuideConstants.PlaceCategories;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * <p>
 * A dialog that presents to the user three options:
 * </p>
 * <code> 
 *  {   NameSnippet,
 *      Categories,
 *      Distance        }
 * </code>
 * <p>
 * as search criteria. User can choose which one is relevant and give
 * appropriate values, and press "Search", upon which the dialog fragment will
 * pass the selections to the calling Activity which implements
 * <code>SearchConfigReceiver</code> and promptly dissapear. The calling
 * Activity should update its content to reflect the user's choice based on the
 * information passed in <code>SearchConfig</code>.
 * </p>
 * 
 * @author athran
 */
public class SearchDialog extends DialogFragment {

    /**
     * Create a new instance of SearchDialog. The caller should implement
     * <code>SearchConfigReceiver</code>, or have another object implement it,
     * so that it can receive the user's choice the finishing button is pressed.
     * 
     * @param receiver
     * @return
     */
    public static SearchDialog newInstance(SearchConfigReceiver receiver) {
        if (receiver == null) {
            throw new IllegalArgumentException("Must provide a receiver");
        }
        return newInstance(receiver, null);
    }

    /**
     * Same as <code>newInstance(SearchConfigReceiver receiver)</code>, but with
     * the option to set an initial selection. Use this if there is an option
     * for the user to repeat and refine the search based on the previous
     * search. Just pass in the <code>SearchConfig</code> instance passed in
     * from the previous search.
     * 
     * @param receiver
     * @param config
     * @return
     */
    public static SearchDialog newInstance(SearchConfigReceiver receiver, SearchConfig config) {
        SearchDialog frag = new SearchDialog();
        frag.mReceiver = receiver;
        frag.mInitialSearchConfig = config;
        return frag;
    }

    public interface SearchConfigReceiver {

        void receiveSearchConfig(SearchConfig config);

    }

    public interface SearchConfig {

        /**
         * The user might not remember exactly what the building's name is.
         * 
         * @return
         */
        String getNameSnippet();

        /**
         * <code>PlaceCategories</code> is a enum of the set of Categories found
         * in the database. An empty list is equivalent to "Any".
         * 
         * @return
         */
        List<PlaceCategories> getCategories();

        /**
         * How far from the current location should be included, in feet.
         * <code>Double.MAX_VALUE</code> means do not consider distance.
         * 
         * @return
         */
        Double getSearchRadius();

    }

    // ---------- END public interface ---------- //

    private SearchConfig mInitialSearchConfig;

    private SearchConfigReceiver mReceiver;

    private View mRoot;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRoot = inflater.inflate(edu.vanderbilt.vm.guide.R.layout.search_dialog, container, false);
        return mRoot;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        this.getDialog().setTitle("Search Criteria");

        
        ((TextView)mRoot.findViewById(R.id.search_btn1)).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                final String snippet = ((TextView) mRoot.findViewById(R.id.search_et1)).getText().toString();
                
                String distanceStr = ((TextView) mRoot.findViewById(R.id.search_et2)).getText().toString();
                final Double distance;
                if (distanceStr == null || distanceStr.equals("")) {
                    distance = Double.MAX_VALUE;
                } else {
                    distance = Double.valueOf(distanceStr);
                }
                
                final List<PlaceCategories> list = new LinkedList<PlaceCategories>();
                LinearLayout ll = (LinearLayout) mRoot.findViewById(R.id.search_category_list);
                CheckBox cb;
                for (int i = 0; i < ll.getChildCount(); i++) {
                    cb = (CheckBox) ll.getChildAt(i);
                    
                    if (cb.isChecked()) {
                        list.add(PlaceCategories.values()[i]);  // I forgot how to work with enum
                                                                // has it really been two years since
                                                                // I last do that?
                    }
                }
                
                mReceiver.receiveSearchConfig(new SearchConfig() {

                    @Override
                    public String getNameSnippet() {
                        return snippet;
                    }

                    @Override
                    public List<PlaceCategories> getCategories() {
                        return list;
                    }

                    @Override
                    public Double getSearchRadius() {
                        return distance;
                    }
                    
                });
                
                
                dismiss();
            }
            
        });
        
        
        ((TextView)mRoot.findViewById(R.id.search_btn2)).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                ((TextView) mRoot.findViewById(R.id.search_et1)).setText(null);
                ((TextView) mRoot.findViewById(R.id.search_et2)).setText(null);
                
                LinearLayout ll = (LinearLayout) mRoot.findViewById(R.id.search_category_list);
                for (int i = 0; i < ll.getChildCount(); i++) {
                    
                    ((CheckBox) ll.getChildAt(i)).setChecked(false);
                    
                }
                ll.setVisibility(View.GONE);
                
                ((TextView) mRoot.findViewById(R.id.search_tv3)).setText("Any");
            }
            
        });
        
        
        OnClickListener categoryListener = new OnClickListener() {

            @Override
            public void onClick(View v) {
                LinearLayout ll = (LinearLayout) mRoot.findViewById(R.id.search_category_list);
                
                if (ll.getVisibility() == View.GONE) {
                    ll.setVisibility(View.VISIBLE);
                    
                } else {
                    
                    ll.setVisibility(View.GONE);
                    StringBuilder builder = new StringBuilder();
                    CheckBox cb;
                    for (int i = 0; i < ll.getChildCount() && builder.length() < 20; i++) {
                        cb = (CheckBox) ll.getChildAt(i);
                        if (cb.isChecked()) {
                            builder.append(cb.getText());
                            builder.append(", ");
                        }
                    }
                    
                    ((TextView) mRoot.findViewById(R.id.search_tv3)).setText(builder.toString());
                    
                }
            }
            
        };
        
        mRoot.findViewById(R.id.search_tv2).setOnClickListener(categoryListener);
        mRoot.findViewById(R.id.search_tv3).setOnClickListener(categoryListener);
        
        
    }

    @Override
    public void onResume() {
        super.onResume();

        if (mInitialSearchConfig != null) {
            ((TextView) mRoot.findViewById(R.id.search_et1)).setText(mInitialSearchConfig.getNameSnippet());
            
            ((TextView) mRoot.findViewById(R.id.search_et2)).setText(Double.toString(mInitialSearchConfig.getSearchRadius()));
            
            
            
        }

    }

}

























