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
public class audioSelection extends Fragment {

    static ArrayList<Integer> audioProfileIdData;
    static ArrayList<String> audioInDeviceData;
    static ArrayList<String> audioOutDeviceData;
    static ArrayList<Integer> audioProfileImageData;
    static ArrayList<String> audioProfileDate;
    static ArrayList<String> audioProfileName;
    static ArrayList<Integer> audioProfileValData;

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
            PostCallToServer pc = new PostCallToServer("http://192.168.0.1/smart_home/API/android/getAudioProfiles.php",
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

            audioProfileIdData = new ArrayList<>(jsonData.length());
            audioInDeviceData = new ArrayList<>(jsonData.length());
            audioOutDeviceData = new ArrayList<>(jsonData.length());
            audioProfileImageData = new ArrayList<>(jsonData.length());
            audioProfileDate = new ArrayList<>(jsonData.length());
            audioProfileValData = new ArrayList<>(jsonData.length());
            audioProfileName = new ArrayList<>(jsonData.length());

            for (int count = 0; count < jsonData.length(); count++) {
                JSONObject obj = (JSONObject) jsonData.get(count);
                Log.d("JSON_Audio", obj.toString());
                try {
                    audioProfileImageData.add(getResources().getIdentifier(obj.get("image").toString().toLowerCase(), "drawable", getActivity().getPackageName()));
                } catch (Exception e) {
                    audioProfileImageData.add(getResources().getIdentifier("cam", "drawable", getActivity().getPackageName()));
                }
                audioProfileName.add(obj.get("input").toString() + " : " + obj.get("output").toString());
                audioInDeviceData.add(obj.get("input").toString());
                audioOutDeviceData.add(obj.get("output").toString());
                audioProfileDate.add(obj.get("date").toString());
                audioProfileValData.add(obj.getInt("profile_id"));
                audioProfileIdData.add(obj.getInt("id"));

            }

        } catch (Exception e) {
            Log.d("Server_Error", "Application Error");
        }
        rv.setAdapter(new SimpleStringRecyclerViewAdapter(getActivity(), audioProfileName));
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
                    .inflate(R.layout.appliance_profile_control_list_item, parent, false);
            view.setBackgroundResource(mBackground);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            holder.mBoundString = mValues.get(position);

            holder.profileName.setText(audioProfileName.get(position));
            holder.profileAddedDate.setText(audioProfileDate.get(position));
            holder.profileOnDevices.setText(audioInDeviceData.get(position));
            holder.profileOffDevices.setText(audioOutDeviceData.get(position));

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //
                    final AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                    builder.setTitle("Audio Profile");
                    builder.setMessage("IN : " + audioInDeviceData.get(position) + " OUT :" + audioOutDeviceData.get(position) + " " + audioProfileValData.get(position));

                    builder.setPositiveButton("Change", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //

                            PostCallToServer pc = new PostCallToServer("http://192.168.0.1/API/fromLappyServer/myProfiles/changeAudio.php",
                                    new String[]{"STAT"},
                                    new String[]{"" + audioProfileValData.get(position)});
                            Toast.makeText(builder.getContext(), "Profile Changed", Toast.LENGTH_SHORT).show();
                        }
                    });
                    builder.setNegativeButton("Cancel", null);
                    builder.show();
                }
            });
            Glide.with(holder.profileImage.getContext())
                    .load(audioProfileImageData.get(position))
                    .fitCenter()
                    .into(holder.profileImage);
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        public static class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public final ImageView profileImage;
            public final TextView profileName;
            public final TextView profileAddedDate;
            public final TextView profileOnDevices;
            public final TextView profileOffDevices;
            public String mBoundString;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                profileImage = (ImageView) view.findViewById(R.id.profile_image);
                profileName = (TextView) view.findViewById(R.id.profile_name);
                profileAddedDate = (TextView) view.findViewById(R.id.date);
                profileOnDevices = (TextView) view.findViewById(R.id.devices_on);
                profileOffDevices = (TextView) view.findViewById(R.id.devices_off);
            }

            @Override
            public String toString() {
                return super.toString() + " '" + profileName.getText();
            }
        }
    }
}
