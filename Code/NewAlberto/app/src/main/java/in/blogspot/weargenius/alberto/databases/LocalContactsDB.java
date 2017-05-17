package in.blogspot.weargenius.alberto.databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class LocalContactsDB {
    public static final String KEY_ROWID = "Sl_No";
    public static final String KEY_PERSON_NAME = "person_name";
    public static final String KEY_MOBILE_NO = "mobile_no";
    public static final String KEY_SYNCEDTOPC = "syncPC";
    public static final String KEY_EMAIL_ID = "email_id";

    private static final String DATABASE_NAME = "Alberto_Contacts";
    private static final String DATABASE_TABLE = "all_Contacts";
    private static final int DATABASE_VERSION = 2;
    private final Context con;
    private DbHelper ourHelper;
    private SQLiteDatabase ourDatabase;

    public LocalContactsDB(Context c) {
        this.con = c;
    }

    public LocalContactsDB open() {
        ourHelper = new DbHelper(con);
        ourDatabase = ourHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        ourHelper.close();
    }

    public long addNewContact(String person_name, String mobile_no, String email_id) {

        String[] columns = new String[]{KEY_ROWID, KEY_PERSON_NAME, KEY_MOBILE_NO, KEY_EMAIL_ID};
        Cursor c = ourDatabase.query(DATABASE_TABLE, columns, KEY_MOBILE_NO + "= ?", new String[]{mobile_no}, null, null, KEY_ROWID + " DESC", "1");
        if (c.getCount() > 0) {
            c.moveToFirst();
            Log.d("Contact_Details", person_name + " " + mobile_no + " " + c.getCount());
            String emailId = c.getString(c.getColumnIndex(KEY_EMAIL_ID));
            if ((!email_id.equals(" ")) && (emailId == null || emailId == "" || emailId.isEmpty())) {
                ContentValues cvUpdateDb = new ContentValues();
                cvUpdateDb.put(KEY_EMAIL_ID, email_id);
                ourDatabase.update(DATABASE_TABLE, cvUpdateDb, KEY_MOBILE_NO + "=" + mobile_no, null);
            }

        } else {

            ContentValues cv = new ContentValues();
            cv.put(KEY_PERSON_NAME, person_name);
            cv.put(KEY_MOBILE_NO, mobile_no);
            cv.put(KEY_SYNCEDTOPC, "No");
            cv.put(KEY_EMAIL_ID, email_id);
            Log.d("Call_Maneger_NEWCONTACT", "Contact " + person_name + " added.");
            return ourDatabase.insert(DATABASE_TABLE, null, cv);
        }
        return 0;
    }

    public String getPersonName(String personNumber) {

        String[] columns = new String[]{KEY_ROWID, KEY_PERSON_NAME, KEY_MOBILE_NO};
        Cursor c = ourDatabase.query(DATABASE_TABLE, columns, KEY_MOBILE_NO + "= ?", new String[]{personNumber}, null, null, KEY_ROWID + " DESC", "1");
        Log.d("Contacts_Count", "" + c.getCount() + " " + personNumber);
        if (c.getCount() > 0) {
            c.moveToFirst();
            return c.getString(c.getColumnIndex(KEY_PERSON_NAME));
        } else
            return personNumber;
    }

    private static class DbHelper extends SQLiteOpenHelper {

        public DbHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE " + DATABASE_TABLE + " (" +
                            KEY_ROWID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                            KEY_PERSON_NAME + " TEXT NOT NULL, " +
                            KEY_MOBILE_NO + " TEXT NOT NULL, " +
                            KEY_EMAIL_ID + " TEXT NOT NULL, " +
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
