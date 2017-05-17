package in.blogspot.weargenius.alberto.synchronizations;

import android.content.Context;
import android.content.Intent;

import in.blogspot.weargenius.alberto.databases.MyExpensesDB;
import in.blogspot.weargenius.alberto.mobileMessages.ReadInboxMessages;

/**
 * Created by oksbwn on 1/4/2016.
 */
public class dailySyncs {
    public dailySyncs(Context con) {
        MyExpensesDB expenseDB = new MyExpensesDB(con);
        expenseDB.open();
        expenseDB.putDataToServer();
        expenseDB.close();

        con.startService(new Intent(con, ReadInboxMessages.class));
    }
}
