package edu.vanderbilt.vm.guide.ui.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;


/**
 * A subclass of view that knows how to modify itself according to the object passed into itself
 *
 * @author athran
 */
abstract public class ItemView extends LinearLayout {

    public ItemView(Context ctx) {
        super(ctx);
    }

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
    abstract public void setView(Object obj, int position, View convertView, ViewGroup parent, CursorIndexerFactory.CursorIndexer indexer);

    /**
     * Abstract factory for creating new instances of ItemView.
     *
     * @author athran
     */
    public interface ItemViewFactory {

        ItemView getItemView(Context ctx);

    }

}