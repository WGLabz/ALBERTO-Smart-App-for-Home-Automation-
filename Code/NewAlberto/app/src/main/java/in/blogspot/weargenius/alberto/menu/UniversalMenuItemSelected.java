package in.blogspot.weargenius.alberto.menu;

import android.content.Context;
import android.content.Intent;
import android.view.MenuItem;
import android.widget.Toast;

import in.blogspot.weargenius.alberto.MainActivity;
import in.blogspot.weargenius.alberto.R;
import in.blogspot.weargenius.alberto.callInterception.CallDetails;
import in.blogspot.weargenius.alberto.foods.FoodMenu;

/**
 * Created by oksbwn on 12/31/2015.
 */
public class UniversalMenuItemSelected {
    public UniversalMenuItemSelected(MenuItem item, Context con) {
        int id = item.getItemId();

        if (id == R.id.home) {
            Intent i = new Intent(con, MainActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            con.startActivity(i);

        } else if (id == R.id.call_details) {
            Intent i = new Intent(con, CallDetails.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            con.startActivity(i);

        } else if (id == R.id.show_food_menu) {
            Intent i = new Intent(con, FoodMenu.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            con.startActivity(i);

        } else if (id == R.id.settings) {
            Toast.makeText(con, "Nav " +
                    "selected", Toast.LENGTH_LONG).show();
        } else if (id == R.id.nav_share) {
            Toast.makeText(con, "Shr selected", Toast.LENGTH_LONG).show();
        } else if (id == R.id.nav_send) {
            Toast.makeText(con, "Nav selected", Toast.LENGTH_LONG).show();
        }

    }
}
