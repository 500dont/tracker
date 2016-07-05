package com.app.madym.tracker;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PointsListActivity extends AppCompatActivity implements View.OnClickListener,
        ValueEventListener {
    private static final String TAG = "pizza";

    public static final String FIREBASE_URL = "https://points-1354.firebaseio.com/tracker/mady";

    private Firebase mFirebaseRoot;

    private EditText mEnterEntry;
    private ListAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.points_list_activity);

        // set up firebase
        Firebase.setAndroidContext(this);
        mFirebaseRoot = new Firebase(FIREBASE_URL);
        mFirebaseRoot.addValueEventListener(this /* value event listener */);

        // set up UI
        mEnterEntry = (EditText) findViewById(R.id.enter_entry);
        TextView addEntry = (TextView) findViewById(R.id.add_entry);
        addEntry.setOnClickListener(this);

        mAdapter = new ListAdapter(this /* context */);
        ListView list = (ListView) findViewById(R.id.entry_list);
        list.setAdapter(mAdapter);
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        ArrayList<PointsEntry> entries = new ArrayList<>();
        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
            PointsEntry entry = new PointsEntry(snapshot.getKey(), (long) snapshot.getValue());
            entries.add(entry);
        }
        // TODO whenever anything changes *everything* is returned in the dataSnapshot and we
        // repopulate the list -- is there a way to get an update *only* for the changed item?
        // If we can then we could use RV and it would make sense.
        mAdapter.setList(entries);
    }

    @Override
    public void onCancelled(FirebaseError firebaseError) {
        // post error + fix UI?
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.add_entry) {
            final String text = mEnterEntry.getText().toString();
            if (!TextUtils.isEmpty(text)) {
                final PointsEntry entry = new PointsEntry(0, text);
                // update ui immediately in case firebase is slow af
                mAdapter.addToStart(entry);

                // update firebase
                Map<String, Object> updates = new HashMap<>();
                updates.put(text, 0);
                mFirebaseRoot.updateChildren(updates);
            }
        }
    }

    public class ListAdapter extends BaseAdapter implements View.OnClickListener,
            View.OnLongClickListener {
        private LayoutInflater mInflater;
        private ArrayList<PointsEntry> mList;
        private boolean mAdding = true; // favour adding points instead of subtracting

        public ListAdapter(Context context) {
            mInflater = LayoutInflater.from(context);
        }

        public void setList(ArrayList<PointsEntry> list) {
            if (list == null) {
                return;
            }
            mList = list;
            notifyDataSetChanged();
        }

        public void addToStart(PointsEntry entry) {
            if (mList == null) {
                return;
            }
            mList.add(0, entry);
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            if (mList == null) {
                return 0;
            }
            return mList.size();
        }

        @Override
        public PointsEntry getItem(int position) {
            if (mList == null) {
                return null;
            }
            return mList.get(position);
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = convertView;
            if (view == null) {
                view = mInflater.inflate(R.layout.points_row, null);
            }
            // TODO make viewholder
            updateView(view, position);
            view.setTag(position);
            view.setOnClickListener(this);
            view.setOnLongClickListener(this);
            return view;
        }

        @Override
        public void onClick(View view) {
            final int position = (int) view.getTag();
            final PointsEntry entry = getItem(position);
            entry.count += mAdding ? 1 : -1;
            mFirebaseRoot.child(entry.text).setValue(entry.count);
            updateView(view, position);
        }

        @Override
        public boolean onLongClick(View view) {
            mAdding = !mAdding;
            updateView(view, (int) view.getTag());
            return true;
        }

        private void updateView(View view, int position) {
            final TextView text = (TextView) view.findViewById(R.id.points_text);
            if (text != null) {
                // TODO proper layouts for each string
                final String delta = mAdding ? "++" : "--";
                final PointsEntry entry = getItem(position);
                final String string = Integer.toString(entry.count) + "   " + entry.text + "   " + delta;
                text.setText(string);
            }
        }
    }
}
