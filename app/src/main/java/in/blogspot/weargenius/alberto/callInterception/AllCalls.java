package in.blogspot.weargenius.alberto.callInterception;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
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
import in.blogspot.weargenius.alberto.databases.LocalContactsDB;

public class AllCalls extends Fragment {

    static ArrayList<Integer> imageCallType;
    static ArrayList<String> callDate;
    static ArrayList<String> callTime;
    static ArrayList<String> listPhoneNo;
    static ArrayList<String> listPersonName;
    CallDetailsDB callDetailsDB;
    LocalContactsDB localContacts;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        RecyclerView rv = (RecyclerView) inflater.inflate(
                R.layout.fragment_all_calls, container, false);
        callDetailsDB = new CallDetailsDB(getActivity());
        localContacts = new LocalContactsDB(getActivity());
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
        localContacts.open();
        Cursor allData = callDetailsDB.getCallDetails("200");
        listPersonName = new ArrayList<>(allData.getCount());
        imageCallType = new ArrayList<>(allData.getCount());
        listPhoneNo = new ArrayList<>(allData.getCount());
        callDate = new ArrayList<>(allData.getCount());
        callTime = new ArrayList<>(allData.getCount());
        for (allData.moveToFirst(); !allData.isAfterLast(); allData.moveToNext()) {
            String noPhone = allData.getString(allData.getColumnIndex("mobileNo"));
            if (!noPhone.isEmpty()) {
                // Log.d("Call_Manager", rc.getContactName(allData.getString(allData.getColumnIndex("mobileNo"))) + " " + allData.getString(allData.getColumnIndex("mobileNo")));
                listPersonName.add(localContacts.getPersonName(noPhone));
                listPhoneNo.add(noPhone);
                callDate.add(allData.getString(allData.getColumnIndex("currentDate")).replaceAll("/", " "));
                callTime.add(allData.getString(allData.getColumnIndex("time")));
                if (allData.getString(allData.getColumnIndex("type")).contains("Incoming"))
                    imageCallType.add(R.drawable.call_received);
                else if (allData.getString(allData.getColumnIndex("type")).contains("Missed"))
                    imageCallType.add(R.drawable.call_missed);
                else
                    imageCallType.add(R.drawable.call);
            }
        }
        callDetailsDB.close();
        localContacts.close();
        return listPersonName;
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
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            holder.mBoundString = mValues.get(position);
            holder.mTextView.setText(mValues.get(position));
            holder.mDateView.setText(callDate.get(position));
            holder.mTimeView.setText(callTime.get(position));

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //
                    final AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                    builder.setTitle("Call / View Notes");
                    builder.setMessage("Choose to call or view Notes." +
                            "Mobile no : " + listPhoneNo.get(position).trim() + " (" + listPersonName.get(position) + " )");

                    builder.setPositiveButton("CALL", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String uri = "tel:" + listPhoneNo.get(position).trim();
                            Intent intent = new Intent(Intent.ACTION_DIAL);
                            intent.setData(Uri.parse(uri));
                            builder.getContext().startActivity(intent);
                        }
                    });
                    builder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //View Notes
                        }
                    });
                    builder.setNegativeButton("Notes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //View Notes
                        }
                    });
                    builder.show();

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
            public final TextView mDateView;
            public final TextView mTimeView;
            public final TextView mTextView;
            public String mBoundString;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                mImageView = (ImageView) view.findViewById(R.id.avatar);
                mTextView = (TextView) view.findViewById(R.id.phone_no);
                mDateView = (TextView) view.findViewById(R.id.call_date);
                mTimeView = (TextView) view.findViewById(R.id.call_time);
            }

            @Override
            public String toString() {
                return super.toString() + " '" + mTextView.getText();
            }
        }
    }
}
