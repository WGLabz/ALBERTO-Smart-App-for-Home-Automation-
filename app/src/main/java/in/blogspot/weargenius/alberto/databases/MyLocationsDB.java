package in.blogspot.weargenius.alberto.databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import in.blogspot.weargenius.alberto.utilities.GetDateTime;

public class MyLocationsDB {
    public static final String KEY_ROWID = "Sl_No";
    public static final String KEY_DATE = "currentDate";
    public static final String KEY_TIME = "currentTime";
    public static final String KEY_LATITUDE = "longitude";
    public static final String KEY_LONGITUDE = "latitude";
    public static final String KEY_VELOCITY = "velocity";

    private static final String DATABASE_NAME = "myAlbertoApplication";
    private static final String DATABASE_TABLE = "currentLocationTable";
    private static final int DATABASE_VERSION = 1;
    private final Context ourContext;
    private DbHelper ourHelper;
    private SQLiteDatabase ourDatabase;

    public MyLocationsDB(Context c) {
        ourContext = c;
    }

    public MyLocationsDB open() {
        ourHelper = new DbHelper(ourContext);
        ourDatabase = ourHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        ourHelper.close();
    }

    public long addLocation(String lat, String lon, String vel) {
        String[] columns = new String[]{KEY_LATITUDE, KEY_LONGITUDE};
        Cursor c = null;
        try {
            c = ourDatabase.query(DATABASE_TABLE, columns, null, null, null, null, KEY_ROWID + " DESC", "1");
        } catch (Exception e) {
            Log.d("MY_LOCATION", e.getMessage().toString());
        }
        c.moveToFirst();
        if (c.getString(c.getColumnIndex("latitude")).equals(lat) && c.getString(c.getColumnIndex("longitude")).equals(lon)) {
            Log.d("MY_LOCATION", "Last Location is there");
            return 0;
        } else {
            String[] dateTime = new GetDateTime().getDateTime();
            ContentValues cv = new ContentValues();
            cv.put(KEY_DATE, dateTime[0]);
            cv.put(KEY_TIME, dateTime[1]);
            cv.put(KEY_LATITUDE, lat);
            cv.put(KEY_LONGITUDE, lon);
            cv.put(KEY_VELOCITY, vel);
            return ourDatabase.insert(DATABASE_TABLE, null, cv);
        }
    }


    public void deleteData() {
        try {
            ourDatabase.delete(DATABASE_TABLE, KEY_ROWID + "=1", null);
        } catch (Exception e) {
        }
    }

    public Cursor getAllLocations() {

        String[] columns = new String[]{KEY_ROWID, KEY_DATE, KEY_TIME, KEY_LATITUDE, KEY_LONGITUDE, KEY_VELOCITY};
        Cursor c = ourDatabase.query(DATABASE_TABLE, columns, null, null, null, null, KEY_ROWID + " ASC", null);
        return c;
    }

    public void delete(Integer locationId) {

    }

    public Cursor getLastLocations() {
        double[] lastLocation = new double[5];
        //  columns, KEY_SYNCEDTOPC + "= ?", new String[]{"No"},
        GetDateTime dateTime = new GetDateTime();
        String[] columns = new String[]{KEY_ROWID, KEY_DATE, KEY_TIME, KEY_LATITUDE, KEY_LONGITUDE, KEY_VELOCITY};
        Cursor c = ourDatabase.query(DATABASE_TABLE, columns, KEY_DATE + "= ?", new String[]{dateTime.getDateTime()[0]}, null, null, KEY_ROWID + " DESC", null);
        // Cursor c = ourDatabase.query(DATABASE_TABLE, columns, null, null, null, null, KEY_ROWID + " DESC", null);
        return c;
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
                            KEY_TIME + " TEXT NOT NULL, " +
                            KEY_LATITUDE + " TEXT NOT NULL, " +
                            KEY_LONGITUDE + " TEXT NOT NULL, " +
                            KEY_VELOCITY + " TEXT NOT NULL);"
            );
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);
            onCreate(db);
        }
    }
}
