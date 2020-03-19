package in.blogspot.weargenius.alberto.databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import in.blogspot.weargenius.alberto.utilities.GetDateTime;
import in.blogspot.weargenius.alberto.utilities.PostCallToServer;

public class MyExpensesDB {
    public static final String KEY_ROWID = "Sl_No";
    public static final String KEY_DATE = "currentDate";
    public static final String KEY_ITEM = "item";
    public static final String KEY_COST = "cost";
    public static final String KEY_SYNCEDTOPC = "syncPC";

    private static final String DATABASE_NAME = "AlbertoExpenses";
    private static final String DATABASE_TABLE = "myExpenses";
    private static final int DATABASE_VERSION = 1;
    private final Context ourContext;
    private DbHelper ourHelper;
    private SQLiteDatabase ourDatabase;

    public MyExpensesDB(Context c) {
        ourContext = c;
    }

    public MyExpensesDB open() {
        ourHelper = new DbHelper(ourContext);
        ourDatabase = ourHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        ourHelper.close();
    }

    public long addExpense(String item, String cost) {
        String[] dateTime = new GetDateTime().getDateTime();
        ContentValues cv = new ContentValues();
        cv.put(KEY_DATE, dateTime[0]);
        cv.put(KEY_ITEM, item);
        cv.put(KEY_COST, cost);
        cv.put(KEY_SYNCEDTOPC, "No");
        return ourDatabase.insert(DATABASE_TABLE, null, cv);

    }

    public Cursor getPreviousExpenses() {
        String[] columns = new String[]{KEY_ROWID, KEY_ITEM, KEY_COST, KEY_DATE, KEY_SYNCEDTOPC};
        Cursor c = null;
        try {
            c = ourDatabase.query(DATABASE_TABLE, columns, null, null, null, null, null);
        } catch (Exception e) {
            Log.d("Database Error", "Error");
        }
        return c;
    }

    public int deleteExpense(int id) {
        Log.d("Delete id is", "" + id);
        return ourDatabase.delete(DATABASE_TABLE, KEY_ROWID + "=?", new String[]{"" + id});
    }

    public String putDataToServer() {
        String[] columns = new String[]{KEY_ROWID, KEY_ITEM, KEY_COST, KEY_DATE, KEY_SYNCEDTOPC};
        String result = "";
        try {
            Cursor c = ourDatabase.query(DATABASE_TABLE, columns, KEY_SYNCEDTOPC + "= ?", new String[]{"No"}, null, null, null, "30");
            int iRow = c.getColumnIndex(KEY_ROWID);
            int iItem = c.getColumnIndex(KEY_ITEM);
            int iDate = c.getColumnIndex(KEY_DATE);
            int iCost = c.getColumnIndex(KEY_COST);
            if (c != null) {
                for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                    try {
                        PostCallToServer pc = new PostCallToServer("http://192.168.0.1/smart_home/API/android/addExpenses.php",
                                new String[]{"ITEM", "COST", "DAT"},
                                new String[]{c.getString(iItem), c.getString(iCost), c.getString(iDate)});

                        int timer = 0;
                        while (pc.getResponse() == null || timer < 5000) {
                            timer++;
                        }
                        if (pc.getResponse().contains("1")) {
                            //ourDatabase.update();
                            ContentValues cvUpdateDb = new ContentValues();
                            cvUpdateDb.put(KEY_SYNCEDTOPC, "Yes");
                            ourDatabase.update(DATABASE_TABLE, cvUpdateDb, KEY_ROWID + "=" + c.getInt(iRow), null);

                        }
                    } catch (Exception e) {
                        Log.d("Upload_Error", e.getMessage());
                    }
                }
            }
        } catch (Exception e) {
            Log.d("Upload_Error", e.getMessage());
        }
        return result;
    }

    private static class DbHelper extends SQLiteOpenHelper {
        public DbHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE " + DATABASE_TABLE + " (" +
                            KEY_ROWID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                            KEY_DATE + " TEXT NOT NULL, " +
                            KEY_ITEM + " TEXT NOT NULL, " +
                            KEY_COST + " TEXT NOT NULL, " +
                            KEY_SYNCEDTOPC + " TEXT NOT NULL);"
            );
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);
            onCreate(db);
        }
    }
}
