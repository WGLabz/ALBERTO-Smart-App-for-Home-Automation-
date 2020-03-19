package in.blogspot.weargenius.alberto.smarthome;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import in.blogspot.weargenius.alberto.R;
import in.blogspot.weargenius.alberto.utilities.PostCallToServer;

/**
 * Created by oksbwn on 1/15/2016.
 */
public class individualAppliances extends Fragment {

    static ArrayList<Integer> applianceImageServer;
    static ArrayList<String> applianceNameServer;
    static ArrayList<String> dateServer;
    static ArrayList<String> applianceLocationServer;
    static ArrayList<String> applianceDetailsServer;
    static ArrayList<Integer> applianceIdServer;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        RecyclerView rv = (RecyclerView) inflater.inflate(
                R.layout.fragment_all_calls, container, false);
        rv.setLayoutManager(new LinearLayoutManager(rv.getContext()));
        refreshContents(rv);
        return rv;
    }

    private void refreshContents(RecyclerView rv) {
        try {
            Log.d("JSON", "Quering Server");
            PostCallToServer pc = new PostCallToServer("http://192.168.0.1/smart_home/API/android/getLoads.php",
                    new String[]{},
                    new String[]{});
            Log.d("JSON", "Null Data");
            int timer = 0;
            while (pc.getResponse() == null || timer < 5000) {
                timer++;
                Log.d("JSON", "Null Data");
            }
            String SetServerString = pc.getResponse();
            Log.d("JSON", SetServerString);

            JSONArray jsonData = new JSONArray(SetServerString);

            applianceImageServer = new ArrayList<>(jsonData.length());
            applianceNameServer = new ArrayList<>(jsonData.length());
            dateServer = new ArrayList<>(jsonData.length());
            applianceLocationServer = new ArrayList<>(jsonData.length());
            applianceIdServer = new ArrayList<>(jsonData.length());
            applianceDetailsServer = new ArrayList<>(jsonData.length());

            for (int count = 0; count < jsonData.length(); count++) {
                JSONObject obj = (JSONObject) jsonData.get(count);
                Log.d("JSON", obj.toString());
                String image = obj.get("image").toString();
                applianceNameServer.add(image);
                applianceLocationServer.add(obj.get("location").toString());
                applianceDetailsServer.add(obj.get("details").toString());
                applianceIdServer.add(obj.getInt("id"));
                if (obj.get("status").toString().contains("F"))
                    applianceImageServer.add(getResources().getIdentifier(image + "_off", "drawable", getActivity().getPackageName()));
                else
                    applianceImageServer.add(getResources().getIdentifier(image + "_on", "drawable", getActivity().getPackageName()));
                dateServer.add(obj.get("date").toString());
            }

        } catch (Exception e) {
            Log.d("Server_Error", "Application Error");
        }
        rv.setAdapter(new SimpleStringRecyclerViewAdapter(getActivity(), applianceNameServer));
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
                    .inflate(R.layout.appliance_control_list_item, parent, false);
            view.setBackgroundResource(mBackground);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            holder.mBoundString = mValues.get(position);

            holder.applianceName.setText(applianceNameServer.get(position));
            holder.dateInstalled.setText(dateServer.get(position));
            holder.applianceLocation.setText(applianceLocationServer.get(position));
            holder.applianceDetails.setText(applianceDetailsServer.get(position));

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //
                    final AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                    builder.setTitle("Turn ON/OFF");
                    builder.setMessage(applianceNameServer.get(position) + " (" + applianceLocationServer.get(position) + ")");

                    builder.setPositiveButton("Toggle", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //
                            PostCallToServer pc = new PostCallToServer("http://192.168.0.1/smart_home/API/android/change_load_status.php",
                                    new String[]{"NO"},
                                    new String[]{"" + applianceIdServer.get(position)});
                            while (pc.getResponse() == null) {
                            }
                            if (pc.getResponse().contains("ok")) {//rawImageName
                                if (applianceImageServer.get(position) == builder.getContext().getResources().getIdentifier(applianceNameServer.get(position) + "_off", "drawable", builder.getContext().getPackageName()))
                                    applianceImageServer.set(position, builder.getContext().getResources().getIdentifier(applianceNameServer.get(position) + "_on", "drawable", builder.getContext().getPackageName()));
                                else
                                    applianceImageServer.set(position, builder.getContext().getResources().getIdentifier(applianceNameServer.get(position) + "_off", "drawable", builder.getContext().getPackageName()));

                                Toast.makeText(builder.getContext(), applianceNameServer.get(position) + " been toggled", Toast.LENGTH_SHORT).show();
                                notifyDataSetChanged();
                            } else {
                                Toast.makeText(builder.getContext(), applianceNameServer.get(position) + " been toggled", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    builder.setNegativeButton("Cancel", null);
                    builder.show();
                }
            });
            Glide.with(holder.applianceImage.getContext())
                    .load(applianceImageServer.get(position))
                    .fitCenter()
                    .into(holder.applianceImage);
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        public static class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public final ImageView applianceImage;
            public final TextView applianceName;
            public final TextView dateInstalled;
            public final TextView applianceLocation;
            public final TextView applianceDetails;
            public String mBoundString;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                applianceImage = (ImageView) view.findViewById(R.id.appliance_image);
                applianceLocation = (TextView) view.findViewById(R.id.appliance_location);
                dateInstalled = (TextView) view.findViewById(R.id.date_installed);
                applianceName = (TextView) view.findViewById(R.id.appliance_name);
                applianceDetails = (TextView) view.findViewById(R.id.appliance_details);
            }

            @Override
            public String toString() {
                return super.toString() + " '" + applianceName.getText();
            }
        }
    }
}
