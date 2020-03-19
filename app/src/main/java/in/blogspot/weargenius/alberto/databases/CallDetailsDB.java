package in.blogspot.weargenius.alberto.databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import in.blogspot.weargenius.alberto.utilities.GetDateTime;

public class CallDetailsDB {
    public static final String KEY_ROWID = "Sl_No";
    public static final String KEY_DATE = "currentDate";
    public static final String KEY_TIME = "time";
    public static final String KEY_PHONE_NO = "mobileNo";
    public static final String KEY_SYNCEDTOPC = "syncPC";
    public static final String KEY_CALL_TYPE = "type";
    public static final String KEY_CALL_DURATION = "duration";
    public static final String KEY_NOTE_ID = "noteId";

    private static final String DATABASE_NAME = "Alberto";
    private static final String DATABASE_TABLE = "call_details";
    private static final int DATABASE_VERSION = 1;
    private final Context con;
    private DbHelper ourHelper;
    private SQLiteDatabase ourDatabase;

    public CallDetailsDB(Context c) {
        this.con = c;
    }

    public CallDetailsDB open() {
        ourHelper = new DbHelper(con);
        ourDatabase = ourHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        ourHelper.close();
    }

    public long addNewCall(String callDuration, String phoneNo, String noteId, String callType, String synced) {

        String[] dateTime = new GetDateTime().getDateTime();

        ContentValues cv = new ContentValues();
        cv.put(KEY_DATE, dateTime[0]);
        cv.put(KEY_TIME, dateTime[1]);
        cv.put(KEY_CALL_DURATION, callDuration);
        cv.put(KEY_PHONE_NO, phoneNo);
        cv.put(KEY_CALL_TYPE, callType);
        cv.put(KEY_SYNCEDTOPC, synced);
        cv.put(KEY_NOTE_ID, noteId);
        return ourDatabase.insert(DATABASE_TABLE, null, cv);

    }

    public Cursor getCallDetails(String noOfContacts) {

        String[] columns = new String[]{KEY_ROWID, KEY_DATE, KEY_TIME, KEY_CALL_DURATION, KEY_PHONE_NO, KEY_CALL_TYPE, KEY_SYNCEDTOPC, KEY_NOTE_ID};
        Cursor c = ourDatabase.query(DATABASE_TABLE, columns, null, null, null, null, KEY_ROWID + " DESC", noOfContacts);

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
                            KEY_CALL_DURATION + " TEXT NOT NULL, " +
                            KEY_PHONE_NO + " TEXT NOT NULL, " +
                            KEY_NOTE_ID + " TEXT NOT NULL, " +
                            KEY_CALL_TYPE + " TEXT NOT NULL, " +
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
