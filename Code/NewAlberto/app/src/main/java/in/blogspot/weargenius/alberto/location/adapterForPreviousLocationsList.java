package in.blogspot.weargenius.alberto.location;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import in.blogspot.weargenius.alberto.R;
import in.blogspot.weargenius.alberto.databases.MyLocationsDB;

public class adapterForPreviousLocationsList extends RecyclerView.Adapter<adapterForPreviousLocationsList.ViewHolder> {
    private final TypedValue mTypedValue = new TypedValue();
    private ArrayList<String> locationLatData;
    private ArrayList<String> locationLongData;
    private ArrayList<String> locationNameData;
    private ArrayList<String> locationDateData;
    private ArrayList<String> locationTimeData;
    private ArrayList<String> locationVelocityData;
    private ArrayList<Integer> locationIDData;
    private int mBackground;
    private Context locationListContext;

    // Provide a suitable constructor (depends on the kind of dataset)
    public adapterForPreviousLocationsList(Context con, ArrayList<Integer> locationId, ArrayList<String> locationLat, ArrayList<String> locationLong, ArrayList<String> locationDate, ArrayList<String> locationTime, ArrayList<String> locationVelocity) {
        locationLatData = locationLat;
        locationLongData = locationLong;
        locationNameData = locationLong;
        locationDateData = locationDate;
        locationTimeData = locationTime;
        locationVelocityData = locationVelocity;
        locationIDData = locationId;
        locationListContext = con;
        con.getTheme().resolveAttribute(R.attr.selectableItemBackground, mTypedValue, true);
        mBackground = mTypedValue.resourceId;

    }

    public void remove(String item) {
        int position = locationLatData.indexOf(item);
        locationLatData.remove(position);
        locationLongData.remove(position);
        locationNameData.remove(position);
        locationDateData.remove(position);
        locationTimeData.remove(position);
        locationVelocityData.remove(position);
        locationIDData.remove(position);
        notifyItemRemoved(position);
        notifyDataSetChanged();
        MyLocationsDB locationDB = new MyLocationsDB(locationListContext);
        locationDB.open();
        locationDB.delete(locationIDData.get(position));
        locationDB.close();
    }

    // Create new views (invoked by the layout manager)
    @Override
    public adapterForPreviousLocationsList.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                                         int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.previous_locations_list_item, parent, false);
        // set the view's size, margins, paddings and layout parameters
        v.setBackgroundResource(mBackground);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.locationData.setText(locationLatData.get(position) + " / " + locationLongData.get(position));
        holder.locationName.setText(locationNameData.get(position));
        holder.locationDate.setText(locationDateData.get(position));
        holder.locationTime.setText(locationTimeData.get(position));
        holder.locationVelocity.setText(locationVelocityData.get(position));
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //
                final AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setTitle("Delete / Show");
                builder.setMessage(
                        "Body : " + locationLatData.get(position) + " (" + locationDateData.get(position) + " " + locationTimeData.get(position) + " )");

                builder.setPositiveButton("View", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //
                    }
                });
                builder.setNeutralButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //View Notes
                        remove(locationLatData.get(position));
                    }
                });
                builder.setNegativeButton("Cancel", null);
                builder.show();

            }
        });

        Glide.with(holder.locationImage.getContext())
                .load(R.drawable.location)
                .fitCenter()
                .into(holder.locationImage);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return locationLatData.size();
    }

    // Provide a reference to the views for each data item
// Complex data items may need more than one view per item, and
// you provide access to all the views for a data item in a view holder
    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public final View mView;
        private TextView locationData;
        private TextView locationName;
        private TextView locationDate;
        private TextView locationTime;
        private TextView locationVelocity;
        private ImageView locationImage;

        public ViewHolder(View v) {
            super(v);
            mView = v;
            locationData = (TextView) v.findViewById(R.id.location_data);
            locationName = (TextView) v.findViewById(R.id.location_name);
            locationDate = (TextView) v.findViewById(R.id.location_date);
            locationTime = (TextView) v.findViewById(R.id.location_time);
            locationVelocity = (TextView) v.findViewById(R.id.location_velocity);
            locationImage = (ImageView) v.findViewById(R.id.locationImage);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + locationData.getText();
        }
    }

}