package in.blogspot.weargenius.alberto.databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class FoodHadTodayDB {
    public static final String KEY_ROWID = "Sl_No";
    public static final String KEY_DATE = "currentDate";
    public static final String KEY_ITEM = "foodItem";
    public static final String KEY_WHAT = "what";
    public static final String KEY_SYNCEDTOPC = "syncPC";

    private static final String DATABASE_NAME = "myDailyFood";
    private static final String DATABASE_TABLE = "myFood";
    private static final int DATABASE_VERSION = 1;
    private final Context ourContext;
    private DbHelper ourHelper;
    private SQLiteDatabase ourDatabase;

    public FoodHadTodayDB(Context c) {
        ourContext = c;
    }

    public FoodHadTodayDB open() {
        ourHelper = new DbHelper(ourContext);
        ourDatabase = ourHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        ourHelper.close();
    }

    public long insertData(String date, String items, String what) {
        ContentValues cv = new ContentValues();
        cv.put(KEY_DATE, date);
        cv.put(KEY_ITEM, items);
        cv.put(KEY_WHAT, what);
        cv.put(KEY_SYNCEDTOPC, "No");
        return ourDatabase.insert(DATABASE_TABLE, null, cv);

    }

    public boolean addedTodayMeals(String what, String date) {
        String[] columns = new String[]{KEY_ROWID};
        boolean statReturn = false;
        try {
            Cursor c = ourDatabase.query(DATABASE_TABLE, columns, KEY_WHAT + "= ? AND " + KEY_DATE + "= ?", new String[]{what, date}, null, null, null);
            if (c.getCount() == 0) {
                statReturn = true;
            } else {
                statReturn = false;
            }
        } catch (Exception e) {
            Log.d("Meals database Error", e.getMessage());
        }

        return statReturn;
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
                            KEY_WHAT + " TEXT NOT NULL, " +
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
