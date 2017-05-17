package in.blogspot.weargenius.alberto.foods;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import in.blogspot.weargenius.alberto.R;

public class adapterForFoodList extends RecyclerView.Adapter<adapterForFoodList.ViewHolder> {
    private final TypedValue mTypedValue = new TypedValue();
    private ArrayList<String> foodAddedDate;
    private ArrayList<String> foodTypeData;
    private ArrayList<String> foodCalorieContent;
    private ArrayList<String> foodNameData;
    private ArrayList<Integer> foodImageData;
    private ArrayList<Integer> foodIdData;
    private int mBackground;
    private Context foodDetailsContext;
    private String foodsAdded = "";

    // Provide a suitable constructor (depends on the kind of dataset)
    public adapterForFoodList(Context con, ArrayList<Integer> foodId, ArrayList<Integer> foodImage, ArrayList<String> foodName, ArrayList<String> foodCalorie, ArrayList<String> foodType, ArrayList<String> foodDate) {
        foodAddedDate = foodDate;
        foodTypeData = foodType;
        foodCalorieContent = foodCalorie;
        foodNameData = foodName;
        foodImageData = foodImage;
        foodIdData = foodId;
        foodDetailsContext = con;

        con.getTheme().resolveAttribute(R.attr.selectableItemBackground, mTypedValue, true);
        mBackground = mTypedValue.resourceId;

    }

    public void remove(String item) {
        int position = foodNameData.indexOf(item);
        foodTypeData.remove(position);
        foodCalorieContent.remove(position);
        foodNameData.remove(position);
        foodImageData.remove(position);
        foodIdData.remove(position);
        foodAddedDate.remove(position);
        notifyItemRemoved(position);
        notifyDataSetChanged();
    }

    // Create new views (invoked by the layout manager)
    @Override
    public adapterForFoodList.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                            int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.food_list_list_item, parent, false);
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
        holder.foodNameView.setText(foodNameData.get(position));
        holder.foodTypeView.setText(foodTypeData.get(position).substring(1));
        holder.foodDateView.setText(foodAddedDate.get(position));
        holder.foodCalorieView.setText(foodCalorieContent.get(position));

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                foodsAdded = foodsAdded + ", " + foodNameData.get(position);
                Toast.makeText(foodDetailsContext, foodsAdded.substring(1), Toast.LENGTH_SHORT).show();
            }
        });

        Glide.with(holder.foodImageView.getContext())
                .load(foodImageData.get(position))
                .fitCenter()
                .into(holder.foodImageView);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return foodNameData.size();
    }

    public String getAddedFoods() {
        return this.foodsAdded;
    }

    // Provide a reference to the views for each data item
// Complex data items may need more than one view per item, and
// you provide access to all the views for a data item in a view holder
    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public final View mView;

        private TextView foodDateView;
        private TextView foodNameView;
        private TextView foodTypeView;
        private TextView foodCalorieView;
        private ImageView foodImageView;

        public ViewHolder(View v) {
            super(v);
            mView = v;
            foodNameView = (TextView) v.findViewById(R.id.food_name);
            foodTypeView = (TextView) v.findViewById(R.id.food_type);
            foodDateView = (TextView) v.findViewById(R.id.food_date);
            foodCalorieView = (TextView) v.findViewById(R.id.food_calorie);
            foodImageView = (ImageView) v.findViewById(R.id.food_image);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + foodNameView.getText();
        }
    }
}