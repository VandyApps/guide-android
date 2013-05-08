package edu.vanderbilt.vm.guide.ui.adapter;

import java.util.ArrayList;

import edu.vanderbilt.vm.guide.util.Geomancer;
import edu.vanderbilt.vm.guide.util.GuideConstants.PlaceCategories;

import android.content.Context;
import android.database.Cursor;
import android.location.Location;

public class CursorIndexerHelper {
    
    public interface CursorIndexer {
        
        boolean isHeader(int position);
        
        int getDBRow(int position);
        
        String getHeaderTitle(int position);
        
        int categoriesCount();
        
    }
    
    
    public static CursorIndexer getAlphabeticalIndexer(Context ctx, Cursor cursor, int nameColIx) {
        return new AlphabeticalIndexer(ctx, cursor, nameColIx);
    }
    
    public static CursorIndexer getDistanceIndexer(Context ctx, Cursor cursor, int latColIx, int lngColIx) {
        return new DistanceIndexer(ctx, cursor, latColIx, lngColIx);
    }
    
    public static CursorIndexer getCategoricalIndexer(Context ctx, Cursor cursor, int catColIx) {
        return new CategoricalIndexer(ctx, cursor, catColIx);
    }
    
    
    // ========== END public interface ========== //
    
    private static class AlphabeticalIndexer implements CursorIndexer {

        private ArrayList<Integer> mEnigma;
        private ArrayList<HeaderRecord> mRecords;
        
        private int mCategories = 26;
        
        private static class HeaderRecord {
            
            int mPosition;
            final String mTitle;
            final ArrayList<Integer> mChild;

            public HeaderRecord(String s) {
                mPosition = 0;
                mTitle = s;
                mChild = new ArrayList<Integer>();
            }

        }
        
        public AlphabeticalIndexer(Context ctx, Cursor cursor, int nameColIx) {
            
            // initialize records
            mRecords = new ArrayList<HeaderRecord>();
            char c = 'A';
            for (int i = 0; i < mCategories; i++) {
                mRecords.add(new HeaderRecord(String.valueOf(c)));
                c++;
            }

            HeaderRecord rec = new HeaderRecord("0-9");
            mRecords.add(rec);
            
            
            // iterates through the database and make an index
            if (cursor.moveToFirst()) {
                
                
                String initial;
                boolean isChar;
                
                do {
        
                    initial = cursor.getString(nameColIx).substring(0, 1);
                    isChar = false;
                    
                    for (int i = 0; i < mRecords.size() - 1; i++) {
                        
                        if (initial.equalsIgnoreCase(mRecords.get(i).mTitle)) {
                            mRecords.get(i).mChild.add(cursor.getPosition());
                            isChar = true;
                            break;
                        }
                        
                    }
        
                    if (!isChar) { // add to final category
                        mRecords.get(mRecords.size() - 1).mChild.add(cursor.getPosition());
                    }
                    
                } while (cursor.moveToNext());
            
            
                // Build HashMap based of the information stored in mRecord
                int listPosition = 0;
                mEnigma = new ArrayList<Integer>();
                
                for (int i = 0; i < mRecords.size(); i++) {

                    if (mRecords.get(i).mChild.size() == 0) {
                        mCategories--;
                        
                    } else {
                        mRecords.get(i).mPosition = listPosition;
                        mEnigma.add(listPosition, -(i + 1));
                        listPosition++;
            
                        for (Integer child : mRecords.get(i).mChild) {
                            mEnigma.add(listPosition, child);
                            listPosition++;
                        }
                    }
                    
                }
                
            }
        }
        
        @Override
        public boolean isHeader(int position) {
            return mEnigma.get(position) < 0;
        }

        @Override
        public int getDBRow(int position) {
            return mEnigma.get(position);
        }

        @Override
        public String getHeaderTitle(int position) {
            if (isHeader(position)) {
                return mRecords.get(-(mEnigma.get(position)) - 1).mTitle;
            } else {
                throw new IllegalStateException("Is not a header.");
            }
        }

        @Override
        public int categoriesCount() {
            return mCategories;
        }

    }
    
    
    
    private static class DistanceIndexer implements CursorIndexer {

        private ArrayList<Integer> mEnigma;
        private ArrayList<HeaderRecord> mRecords;
        
        private int mCategories = 26;
        
        private static class HeaderRecord {
            
            int mPosition;
            final double mDist;     // in meters
            final String mTitle;
            final ArrayList<Integer> mChild;
            
            public HeaderRecord(String s, double d) {
                mPosition = 0;
                mTitle = s;
                mDist = d;
                mChild = new ArrayList<Integer>();
            }

        }
        
        public DistanceIndexer(Context ctx, Cursor cursor, int latColIx, int lngColIx) {
            

            // Initializing the Header records
            mRecords = new ArrayList<HeaderRecord>();
            mRecords.add(new HeaderRecord("100 ft", 30.5));
            mRecords.add(new HeaderRecord("200 ft", 61));
            mRecords.add(new HeaderRecord("400 ft", 122));
            mRecords.add(new HeaderRecord("800 ft", 244));
            mRecords.add(new HeaderRecord("1000 ft", 304.8));
            mRecords.add(new HeaderRecord("0.3 mi", 483));
            mRecords.add(new HeaderRecord("0.6 mi", 965.6));
            mRecords.add(new HeaderRecord("1.2 mi", 1931));
            mRecords.add(new HeaderRecord("2.4 mi", 3862));
            mRecords.add(new HeaderRecord("In a galaxy far far away", 10000000));
            mCategories = mRecords.size();

            
            
            // Scanning the database to index
            if (cursor.moveToFirst()) {
            

                Location current = Geomancer.getDeviceLocation();
                Location tmp = new Location("Temp");
                
                do {
                    
                    tmp.setLatitude(Double.parseDouble(cursor.getString(latColIx)));
                    tmp.setLongitude(Double.parseDouble(cursor.getString(lngColIx)));
                    
                    for (int i = 0; i < mRecords.size();i++) {
                        if (current.distanceTo(tmp) < mRecords.get(i).mDist) {
                            mRecords.get(i).mChild.add(cursor.getPosition());
                            break;
                        }
                    }
                    
                } while (cursor.moveToNext());
        
                
                
                // Build HashMap based of the information stored in mRecord
                int listPosition = 0;
                mEnigma = new ArrayList<Integer>();
                
                for (int i = 0; i < mRecords.size(); i++) {
                    
                    if (mRecords.get(i).mChild.size() == 0) {
                        mCategories--;
        
                    } else {
                        mRecords.get(i).mPosition = listPosition;
                        mEnigma.add(listPosition, -(i + 1));
                        listPosition++;
                        
                        for (Integer child : mRecords.get(i).mChild) {
                            mEnigma.add(listPosition, child);
                            listPosition++;
                        }
                    }
                    
                }
            }
            
        }
        
        
        @Override
        public boolean isHeader(int position) {
            return mEnigma.get(position) < 0;
        }

        @Override
        public int getDBRow(int position) {
            return mEnigma.get(position);
        }

        @Override
        public String getHeaderTitle(int position) {
            if (isHeader(position)) {
                return mRecords.get(-(mEnigma.get(position)) - 1).mTitle;
            } else {
                throw new IllegalStateException("Is not a header.");
            }
        }

        @Override
        public int categoriesCount() {
            return mCategories;
        }
        
    }
    
    private static class CategoricalIndexer implements CursorIndexer {

        private ArrayList<Integer> mEnigma;
        private ArrayList<HeaderRecord> mRecords;
        
        private int mCategories = 26;
        
        public static class HeaderRecord {

            int mPosition;
            final String mTitle;
            final PlaceCategories mCat;
            final ArrayList<Integer> mChild;

            public HeaderRecord(PlaceCategories d) {
                mPosition = 0;
                mCat = d;
                mTitle = d.text();
                mChild = new ArrayList<Integer>();
            }

        }
        
        public CategoricalIndexer(Context ctx, Cursor cursor, int catColIx) {
            
            
            // Initializing the Header records
            mRecords = new ArrayList<HeaderRecord>();

            for (PlaceCategories c : PlaceCategories.values()) {
                mRecords.add(new HeaderRecord(c));
            }

            mCategories = mRecords.size();
            
            
            // iterates through the database and make an index
            if (cursor.moveToFirst()) {

                String catStr;
                boolean isCat;
                
                do {
                    
                    catStr = cursor.getString(catColIx);
                    isCat = false;
                    //logger.info("Category String from database: " + catStr);
                    for (int i = 0; i < mRecords.size() - 1; i++) {
                        
                        if (catStr.equalsIgnoreCase(mRecords.get(i).mCat.text())) {
                            mRecords.get(i).mChild.add(cursor.getPosition());
                            isCat = true;
                            break;
                        }
                        
                    }
                    
                    if (!isCat) { // add to final category
                        mRecords.get(mRecords.size() - 1).mChild.add(cursor.getPosition());
                    }
                    
                } while (cursor.moveToNext());
            }
            
            
            // Build HashMap based of the information stored in mRecord
            int listPosition = 0;
            mEnigma = new ArrayList<Integer>();
            
            for (int i = 0; i < mRecords.size(); i++) {

                if (mRecords.get(i).mChild.size() == 0) {
                    //logger.info("Size of Records' child: " + mRecord.get(i).mChild.size());
                    mCategories--;

                } else {
                    mRecords.get(i).mPosition = listPosition;
                    mEnigma.add(listPosition, -(i + 1));
                    listPosition++;
        
                    for (Integer child : mRecords.get(i).mChild) {
                        mEnigma.add(listPosition, child);
                        listPosition++;
                    }
                }
                
            }
            
        }
        
        @Override
        public boolean isHeader(int position) {
            return mEnigma.get(position) < 0;
        }

        @Override
        public int getDBRow(int position) {
            return mEnigma.get(position);
        }

        @Override
        public String getHeaderTitle(int position) {
            if (isHeader(position)) {
                return mRecords.get(-(mEnigma.get(position)) - 1).mTitle;
            } else {
                throw new IllegalStateException("Is not a header.");
            }
        }

        @Override
        public int categoriesCount() {
            return mCategories;
        }
        
    }
    
}























