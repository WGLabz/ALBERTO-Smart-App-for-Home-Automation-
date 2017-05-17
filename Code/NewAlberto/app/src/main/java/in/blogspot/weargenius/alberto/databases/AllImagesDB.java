package in.blogspot.weargenius.alberto.databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import in.blogspot.weargenius.alberto.utilities.GetDateTime;

public class AllImagesDB {
    public static final String KEY_ROWID = "Sl_No";
    public static final String KEY_DATE = "currentDate";
    public static final String KEY_TIME = "time";
    public static final String KEY_IMAGE_NAME = "imageName";
    public static final String KEY_SYNCEDTOPC = "syncPC";
    public static final String KEY_IMAGE_OF = "imageOf";
    public static final String KEY_IMAGE_PATH = "imagePath";

    private static final String DATABASE_NAME = "Alberto_Images";
    private static final String DATABASE_TABLE = "image_details";
    private static final int DATABASE_VERSION = 2;
    private final Context con;
    private DbHelper ourHelper;
    private SQLiteDatabase ourDatabase;

    public AllImagesDB(Context c) {
        this.con = c;
    }

    public AllImagesDB open() {
        ourHelper = new DbHelper(con);
        ourDatabase = ourHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        ourHelper.close();
    }

    public long addNewImage(String imageOf, String imageName, String synced, String path) {

        String[] dateTime = new GetDateTime().getDateTime();

        ContentValues cv = new ContentValues();
        cv.put(KEY_DATE, dateTime[0]);
        cv.put(KEY_TIME, dateTime[1]);
        cv.put(KEY_IMAGE_NAME, imageName);
        cv.put(KEY_IMAGE_OF, imageOf);
        cv.put(KEY_SYNCEDTOPC, synced);
        cv.put(KEY_IMAGE_PATH, path);
        return ourDatabase.insert(DATABASE_TABLE, null, cv);

    }

    public Cursor getImage(String id) {

        String[] columns = new String[]{KEY_ROWID, KEY_DATE, KEY_TIME, KEY_IMAGE_NAME, KEY_IMAGE_OF, KEY_SYNCEDTOPC, KEY_IMAGE_PATH};
        Cursor c = ourDatabase.query(DATABASE_TABLE, columns, KEY_IMAGE_OF + "= ?", new String[]{id}, null, null, null, "30");
        //Cursor c = ourDatabase.query(DATABASE_TABLE, columns, null, null, null, null, null, "30");
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
                            KEY_IMAGE_NAME + " TEXT NOT NULL, " +
                            KEY_IMAGE_OF + " TEXT NOT NULL, " +
                            KEY_IMAGE_PATH + " TEXT NOT NULL, " +
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
