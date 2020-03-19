package in.blogspot.weargenius.alberto.callInterception;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import in.blogspot.weargenius.alberto.R;
import in.blogspot.weargenius.alberto.databases.CallDetailsDB;

public class MissedCalls extends Fragment {

    static ArrayList<Integer> imageCallType;
    CallDetailsDB callDetailsDB;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        RecyclerView rv = (RecyclerView) inflater.inflate(
                R.layout.fragment_all_calls, container, false);
        callDetailsDB = new CallDetailsDB(getActivity());
        setupRecyclerView(rv);
        return rv;
    }


    private void setupRecyclerView(RecyclerView recyclerView) {
        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
        recyclerView.setAdapter(new SimpleStringRecyclerViewAdapter(getActivity(),
                getRandomSublist()));
    }

    private List<String> getRandomSublist() {
        callDetailsDB.open();
        Cursor allData = callDetailsDB.getCallDetails("200");

        ArrayList<String> listPhoneNo = new ArrayList<>(allData.getCount());
        imageCallType = new ArrayList<>(allData.getCount());
        for (allData.moveToFirst(); !allData.isAfterLast(); allData.moveToNext()) {
            String noPhone = allData.getString(allData.getColumnIndex("mobileNo"));
            if (!noPhone.isEmpty() && allData.getString(allData.getColumnIndex("type")).contains("Missed")) {
                listPhoneNo.add(allData.getString(allData.getColumnIndex("mobileNo")));
                imageCallType.add(R.drawable.call_missed);
            }
        }
        callDetailsDB.close();
        return listPhoneNo;
    }

    public static class SimpleStringRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleStringRecyclerViewAdapter.ViewHolder> {

        private final TypedValue mTypedValue = new TypedValue();
        private int mBackground;
        private List<String> mValues;

        public SimpleStringRecyclerViewAdapter(Context context, List<String> items) {
            context.getTheme().resolveAttribute(R.attr.selectableItemBackground, mTypedValue, true);
            mBackground = mTypedValue.resourceId;
            mValues = items;
        }

        public String getValueAt(int position) {
            return mValues.get(position);
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.call_details_list_item, parent, false);
            view.setBackgroundResource(mBackground);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            holder.mBoundString = mValues.get(position);
            holder.mTextView.setText(mValues.get(position));

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //
                }
            });
            Glide.with(holder.mImageView.getContext())
                    .load(imageCallType.get(position))
                    .fitCenter()
                    .into(holder.mImageView);
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        public static class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public final ImageView mImageView;
            public final TextView mTextView;
            public String mBoundString;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                mImageView = (ImageView) view.findViewById(R.id.avatar);
                mTextView = (TextView) view.findViewById(R.id.phone_no);
            }

            @Override
            public String toString() {
                return super.toString() + " '" + mTextView.getText();
            }
        }
    }
}
