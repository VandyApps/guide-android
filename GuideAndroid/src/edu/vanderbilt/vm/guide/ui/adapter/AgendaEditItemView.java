package edu.vanderbilt.vm.guide.ui.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import edu.vanderbilt.vm.guide.container.Place;

import java.util.HashSet;
import java.util.Set;

/**
 * Date: 6/5/13
 * Time: 7:42 PM
 */
public class AgendaEditItemView extends ItemView {

    public AgendaEditItemView(Context ctx) {
        super(ctx);

        mCheck = new CheckBox(ctx);
        this.addView(mCheck);
        mPosition = -1;
    }

    public AgendaEditItemView(Context ctx, Set<Integer> set) {
        this(ctx);
        mCheckedSet = set;
        setTag(mCheckedSet);

        mCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mCheckedSet.add(mPosition);

                } else {
                    mCheckedSet.remove(mPosition);
                }

            }
        });

    }

    private CheckBox mCheck;

    private int mPosition;
    private Set<Integer> mCheckedSet;

    /**
     * <p>
     * Instruct this object to morph itself according to a data object passed into it. This method's signature is made
     * to match the setView() of BaseAdapter, but only <code>obj</code> is important. View recycling should be done
     * in the adapter that uses this ItemView, so <code>convertView</code> is useless.
     * </p>
     * <p>
     * <code>indexer</code> is used if the ListView will be indexed. Otherwise, use null.
     * </p>
     *
     * @param obj         Data object to be detailed
     * @param position    This item's position in the list. Usually unused.
     * @param convertView view for recycling. leave nulled.
     * @param parent      the ViewGroup which contains this view. leave nulled.
     * @param indexer
     */
    @SuppressWarnings("unchecked")
    @Override
    public void setView(Object obj, int position, View convertView, ViewGroup parent, CursorIndexerFactory.CursorIndexer indexer) {
        mPosition = position;
        if (mCheckedSet.contains(position)) {
            mCheck.setChecked(true); // Not very Zen. Just wanted to make it as obvious as possible.

        } else {
            mCheck.setChecked(false);
        }

        mCheck.setText(((Place) obj).getName());
    }

    public static ItemViewFactory getFactory() {
        return new AgendaItemViewFactory();
    }

    private static class AgendaItemViewFactory implements ItemViewFactory{

        private Set<Integer> mCheckedSet;

        AgendaItemViewFactory() {
            mCheckedSet = new HashSet<Integer>();
        }

        @Override
        public ItemView getItemView(Context ctx) {
            return new AgendaEditItemView(ctx, mCheckedSet);
        }
    }

}
