package in.blogspot.weargenius.alberto.foods;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import in.blogspot.weargenius.alberto.MainActivity;
import in.blogspot.weargenius.alberto.R;
import in.blogspot.weargenius.alberto.databases.FoodDetailsDB;
import in.blogspot.weargenius.alberto.databases.FoodHadTodayDB;

public class FoodMenu extends AppCompatActivity {

    private String foodType;
    private adapterForFoodList mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_menu);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
                Log.d("Food_Details", "home selected");
            }
        });

        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.food_list);
        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        Log.d("Foods_All", "Food Menu Starting");
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            foodType = extras.getString("TYPE");
        } else
            foodType = ",";

        FoodDetailsDB foodDB = new FoodDetailsDB(getApplicationContext());
        foodDB.open();
        Cursor allFoods = foodDB.getAllFoods(foodType);
        int i = 0;
        ArrayList<String> foodName = new ArrayList<>(allFoods.getCount());
        ArrayList<String> foodType = new ArrayList<>(allFoods.getCount());
        ArrayList<String> foodCalorie = new ArrayList<>(allFoods.getCount());
        ArrayList<String> foodDate = new ArrayList<>(allFoods.getCount());
        ArrayList<Integer> foodId = new ArrayList<>(allFoods.getCount());
        ArrayList<Integer> foodImage = new ArrayList<>(allFoods.getCount());
        if (allFoods.getCount() > 0)
            for (allFoods.moveToFirst(); !allFoods.isLast(); allFoods.moveToNext()) {

                foodName.add(i, allFoods.getString(allFoods.getColumnIndex("foodName")));
                foodType.add(i, allFoods.getString(allFoods.getColumnIndex("foodType")));
                foodCalorie.add(i, allFoods.getString(allFoods.getColumnIndex("foodCalorie")));
                foodDate.add(i, allFoods.getString(allFoods.getColumnIndex("date")));
                foodId.add(i, allFoods.getInt(allFoods.getColumnIndex("Sl_No")));

                try {
                    foodImage.add(getResources().getIdentifier(allFoods.getString(allFoods.getColumnIndex("foodImage")), "drawable", getApplicationContext().getPackageName()));
                } catch (Exception e) {
                    foodImage.add(R.drawable.call_received);
                }

                Log.d("Foods_All", allFoods.getString(allFoods.getColumnIndex("foodName")));
                i++;
            }
        // specify an adapter (see also next example)
        mAdapter = new adapterForFoodList(this, foodId, foodImage, foodName, foodCalorie, foodType, foodDate);
        mRecyclerView.setAdapter(mAdapter);
        foodDB.close();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_food_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.save) {
            new Thread() {
                public void run() {
                    try {
                        Date dat = new Date();
                        SimpleDateFormat sdfDate = new SimpleDateFormat("dd/MMM/yyyy");
                        FoodHadTodayDB addFood = new FoodHadTodayDB(FoodMenu.this);
                        addFood.open();
                        long x = addFood.insertData(sdfDate.format(dat), mAdapter.getAddedFoods().substring(1), foodType);
                        addFood.close();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), foodType + "  Added", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } catch (Exception e) {
                        Log.d("E :", e.getMessage());
                    }
                }
            }.start();
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {

        super.onBackPressed();

    }
}
