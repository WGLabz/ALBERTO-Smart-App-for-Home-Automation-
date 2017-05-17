package com.oksbwn.dailyFoods;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.oksbwn.R;
import com.oksbwn.allDatabaseSetups.addFoodToDatabase;
import com.oksbwn.allSettings.settingsMenuFragment;

public class foodMenu extends Activity {
    addFoodToDatabase addFood=null;
    SimpleDateFormat sdfDate = new SimpleDateFormat("dd/MMM/yyyy");
    private AlphaAnimation buttonClick = new AlphaAnimation(1F, 0.8F);
    // Array of strings storing country names
    String[] foodName = new String[] {
            "Alu Bharta",
            "Alu Beans",
            "Alu Chips",
            "Alu Govi",
            "ALu Kadali Fry",
            "Alu Potala",
            "Aluchup",
            "Baingan Bharta ",
            "Biscuits",
            "Chilly Chicken",
            "Chicken Curry",
            "Chilly Paneer",
            "Chowmin",
            "Chuda Chini",
            "Cofee",
            "Cold Drink",
            "Dahi",
            "Dahi Baingan",
            "Dahi Salad",
            "Dal",
            "Dalma",
            "Dosa",
            "Egg Bhujia",
            "Egg Curry",
            "Fruits",
            "Gaja Muga",
            "Ghanta Curry Masala",
            "Ghanta Curry Fry",
            "Guguni",
            "Mixture",
            "Mudhi",
            "Omlet",
            "Paneer Masala",
            "Paratha",
            "Puri",
            "Rice",
            "Roti",
            "Rusk",
            "Saga",
            "Salad",
            "Samosa",
            "Tea",
            "Tomatto Khatta",
            "Vada"
    };

    // Array of integers points to images stored in /res/drawable-ldpi/
    int[] foodImage = new int[]{
            R.drawable.aloo_bharta,
            R.drawable.alu_beans,
            R.drawable.alu_chips,
            R.drawable.alu_govi,
            R.drawable.alu_kadali_fry,
            R.drawable.alu_potal,
            R.drawable.aluchup,
            R.drawable.baigan_bharta,
            R.drawable.biscuit,
            R.drawable.chicken_chilly,
            R.drawable.chicken_curry,
            R.drawable.chilly_paneer,
            R.drawable.chowmin,
            R.drawable.chuda_chini,
            R.drawable.cofee,
            R.drawable.cold_drink,
            R.drawable.dahi,
            R.drawable.dahi_baigan,
            R.drawable.dahi_salad,
            R.drawable.dal,
            R.drawable.dalma,
            R.drawable.dosa,
            R.drawable.egg_bhujia,
            R.drawable.egg_curry,
            R.drawable.fruits,
            R.drawable.gaja_muga,
            R.drawable.ghanta_curry_masala,
            R.drawable.ghnta_curry_fry,
            R.drawable.guguni,
            R.drawable.mixture,
            R.drawable.mudhi,
            R.drawable.omlet,
            R.drawable.paneer_masala,
            R.drawable.paratha,
            R.drawable.puri,
            R.drawable.rice,
            R.drawable.roti,
            R.drawable.rusk,
            R.drawable.saga,
            R.drawable.salad,
            R.drawable.samosa,
            R.drawable.tea,
            R.drawable.tomatto_khatta,
            R.drawable.vada
    };
    String what;
    // Array of strings to store currencies
    String[] foodDetail = new String[]{
            "Indian Rupee",
            "Pakistani Rupee",
            "Sri Lankan Rupee",
            "Renminbi",
            "Bangladeshi Taka",
            "Nepalese Rupee",
            "Afghani",
            "North Korean Won",
            "South Korean Won",
            "Japanese Yen",
            "Japanese Yen",
            "Indian Rupee",
            "Pakistani Rupee",
            "Sri Lankan Rupee",
            "Renminbi",
            "Bangladeshi Taka",
            "Nepalese Rupee",
            "Afghani",
            "North Korean Won",
            "South Korean Won",
            "Japanese Yen",
            "Japanese Yen",
            "Indian Rupee",
            "Pakistani Rupee",
            "Sri Lankan Rupee",
            "Renminbi",
            "Bangladeshi Taka",
            "Nepalese Rupee",
            "Afghani",
            "North Korean Won",
            "South Korean Won",
            "Japanese Yen",
            "Japanese Yen",
            "Indian Rupee",
            "Pakistani Rupee",
            "Sri Lankan Rupee",
            "Renminbi",
            "Bangladeshi Taka",
            "Nepalese Rupee",
            "Afghani",
            "North Korean Won",
            "South Korean Won",
            "Japanese Yen",
            "Japanese Yen"
    };
String foods="";
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_menu);

        ActionBar ab = getActionBar();
        ab.setDisplayHomeAsUpEnabled(true);


        final Bundle bundle = getIntent().getExtras();
        what=bundle.getString("type");
         Log.d("What food",what);
        // Each row in the list stores country name, currency and flag
        List<HashMap<String,String>> aList = new ArrayList<HashMap<String,String>>();

        for(int i=0;i<foodDetail.length;i++){
            HashMap<String, String> hm = new HashMap<String,String>();
            hm.put("txt", "  " + foodName[i]);
            hm.put("cur"," " + foodDetail[i]);
            hm.put("flag", Integer.toString(foodImage[i]) );
            aList.add(hm);
        }
        // Keys used in Hashmap
        String[] from = { "flag","txt","cur" };

        // Ids of views in listview_layout
        int[] to = { R.id.flag,R.id.txt,R.id.cur};

        // Instantiating an adapter to store each items
        // R.layout.listview_layout defines the layout of each item
        SimpleAdapter adapter = new SimpleAdapter(getBaseContext(), aList, R.layout.listview_layout, from, to);

        // Getting a reference to listview of main.xml layout file
        ListView listView = ( ListView ) findViewById(R.id.foodList);

        // Setting the adapter to the listView
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                //Toast.makeText(getApplicationContext(),"Selected is  "+position,Toast.LENGTH_LONG).show();
                foods=foods+", "+foodName[position];
                Toast.makeText(getApplicationContext(),foods.substring(1,foods.length()),Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.food_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Thread th=null;
        switch (item.getItemId()) {
            // action with ID action_refresh was selected
            case R.id.saveFood:
                th= new Thread() {
                    public void run() {
                        try{
                            Date dat= new Date();
                            addFood= new addFoodToDatabase(foodMenu.this);
                            addFood.open();
                            long x=addFood.insertData(sdfDate.format(dat),foods.substring(1,foods.length()),what);
                            addFood.close();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getApplicationContext(), "Food Taken Added", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }catch(Exception e){
                            Log.d("E :",e.getMessage());
                        }
                    }
                };
                th.start();
                finish();
                break;
            // action with ID action_settings was selected
            case R.id.action_settings:
                th=new Thread() {
                    public void run() {
                        try {
                            Intent i = new Intent(getApplicationContext(),settingsMenuFragment.class);
                            startActivity(i);
                            finish();
                        }catch(Exception e){
                            Log.d("Exception :",e.getMessage());
                        }
                    }
                };
                th.start();
                break;
            case android.R.id.home:
                try {
                    Intent i = new Intent(getApplicationContext(),com.oksbwn.main.mainActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                    finish();
                }catch(Exception e){
                    Log.d("Exception :",e.getMessage());
                }
                return true;
            default:
                break;
        }

        return true;
    }

}