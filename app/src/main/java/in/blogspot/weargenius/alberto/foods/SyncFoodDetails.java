package in.blogspot.weargenius.alberto.foods;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import in.blogspot.weargenius.alberto.databases.FoodDetailsDB;
import in.blogspot.weargenius.alberto.utilities.PostCallToServer;

/**
 * Created by oksbwn on 1/16/2016.
 */
public class SyncFoodDetails {
    public SyncFoodDetails(Context con) {
        try {
            Log.d("JSONFood", "Quering Server");
            PostCallToServer pc = new PostCallToServer("http://192.168.0.1/smart_home/API/android/getFoods.php",
                    new String[]{},
                    new String[]{});
            Log.d("JSONFood", "Null Data");
            int timer = 0;
            while (pc.getResponse() == null || timer < 5000) {
                timer++;
                Log.d("JSON", "Null Data");
            }
            String SetServerString = pc.getResponse();
            Log.d("JSONFood", SetServerString);

            JSONArray jsonData = new JSONArray(SetServerString);
            FoodDetailsDB foodDb = new FoodDetailsDB(con);
            foodDb.open();
            for (int count = 0; count < jsonData.length(); count++) {
                JSONObject obj = (JSONObject) jsonData.get(count);
                Log.d("JSON", obj.toString());
                String image = obj.get("image").toString();
                String name = obj.get("name").toString();
                String calorie = obj.get("calorie").toString();
                String date = obj.get("date").toString();
                String type = obj.get("type").toString();
                foodDb.addNewFood(image, name, calorie, type, date);
                Log.d("JSONFood", "Food added " + image + " " + name + " " + calorie + " " + type + " " + date);
            }
            foodDb.close();
        } catch (Exception e) {
            Log.d("Server_Error", "Application Error");
        }
    }
}
