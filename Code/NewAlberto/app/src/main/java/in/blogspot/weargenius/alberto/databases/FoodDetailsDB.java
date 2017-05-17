package in.blogspot.weargenius.alberto.databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class FoodDetailsDB {
    public static final String KEY_ROWID = "Sl_No";
    public static final String KEY_IMAGE = "foodImage";
    public static final String KEY_NAME = "foodName";
    public static final String KEY_CALORIE = "foodCalorie";
    public static final String KEY_TYPE = "foodType";
    public static final String KEY_DATE = "date";

    private static final String DATABASE_NAME = "AlbertoFoods";
    private static final String DATABASE_TABLE = "foodDetails";
    private static final int DATABASE_VERSION = 2;
    private final Context ourContext;
    private DbHelper ourHelper;
    private SQLiteDatabase ourDatabase;

    public FoodDetailsDB(Context c) {
        ourContext = c;
    }

    public FoodDetailsDB open() {
        ourHelper = new DbHelper(ourContext);
        ourDatabase = ourHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        ourHelper.close();
    }

    public Cursor getAllFoods(String type) {
        String[] columns = new String[]{KEY_ROWID, KEY_IMAGE, KEY_NAME, KEY_CALORIE, KEY_TYPE, KEY_DATE};
        Cursor c = ourDatabase.query(DATABASE_TABLE, columns, KEY_TYPE + " LIKE ?",
                new String[]{"%" + type + "%"}, null, null, KEY_ROWID + " DESC", null);

        return c;
    }

    public long addNewFood(String image, String name, String calorie, String type, String date) {
        ContentValues cv = new ContentValues();
        cv.put(KEY_IMAGE, image);
        cv.put(KEY_NAME, name);
        cv.put(KEY_CALORIE, calorie);
        cv.put(KEY_TYPE, type);
        cv.put(KEY_DATE, date);
        return ourDatabase.insert(DATABASE_TABLE, null, cv);

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
                            KEY_IMAGE + " TEXT NOT NULL, " +
                            KEY_NAME + " TEXT NOT NULL, " +
                            KEY_CALORIE + " TEXT NOT NULL, " +
                            KEY_TYPE + " TEXT NOT NULL);"
            );
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);
            onCreate(db);
        }
    }
}
