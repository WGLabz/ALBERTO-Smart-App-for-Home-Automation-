package in.blogspot.weargenius.alberto.databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import in.blogspot.weargenius.alberto.utilities.GetDateTime;

public class NotesDetailDB {
    public static final String KEY_ROWID = "Sl_No";
    public static final String KEY_DATE = "currentDate";
    public static final String KEY_TIME = "time";
    public static final String KEY_NOTE_HEAD = "noteHeader";
    public static final String KEY_SYNCEDTOPC = "syncPC";
    public static final String KEY_NOTE_CONTENT = "noteContent";
    public static final String KEY_LOCATION = "locationNote";
    public static final String KEY_NOTE_TYPE = "noteType";
    public static final String KEY_WITH_PHONE_NO = "phoneNo";
    public static final String KEY_IMAGES = "images";

    private static final String DATABASE_NAME = "Alberto_Notes";
    private static final String DATABASE_TABLE = "note_details";
    private static final int DATABASE_VERSION = 2;
    private final Context con;
    private DbHelper ourHelper;
    private SQLiteDatabase ourDatabase;

    public NotesDetailDB(Context c) {
        this.con = c;
    }

    public NotesDetailDB open() {
        ourHelper = new DbHelper(con);
        ourDatabase = ourHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        ourHelper.close();
    }

    public int addNewNote(String header, String content, String phoneNo, String location, String image, String noteType) {

        String[] dateTime = new GetDateTime().getDateTime();

        Log.d("Notes_all", content + " " + header);
        ContentValues cv = new ContentValues();
        cv.put(KEY_DATE, dateTime[0]);
        cv.put(KEY_TIME, dateTime[1]);
        cv.put(KEY_NOTE_HEAD, header);
        cv.put(KEY_NOTE_TYPE, noteType);
        cv.put(KEY_NOTE_CONTENT, content);
        cv.put(KEY_LOCATION, location);
        cv.put(KEY_SYNCEDTOPC, "No");
        cv.put(KEY_WITH_PHONE_NO, phoneNo);
        cv.put(KEY_IMAGES, image);
        ourDatabase.insert(DATABASE_TABLE, null, cv);
        Cursor c = ourDatabase.query(DATABASE_TABLE, new String[]{KEY_ROWID}, null, null, null, null, KEY_ROWID + " DESC", "1");
        c.moveToFirst();
        return c.getInt(c.getColumnIndex(KEY_ROWID));
    }

    public Cursor getAllNotes() {

        String[] columns = new String[]{KEY_NOTE_TYPE, KEY_ROWID, KEY_DATE, KEY_TIME, KEY_NOTE_HEAD, KEY_NOTE_CONTENT, KEY_LOCATION, KEY_SYNCEDTOPC, KEY_WITH_PHONE_NO, KEY_IMAGES};
        Cursor c = ourDatabase.query(DATABASE_TABLE, columns, null, null, null, null, KEY_ROWID + " DESC", null);

        return c;
    }

    public void updateNote(String header, String content, String image, int id, String noteType) {
        ContentValues cvUpdateDb = new ContentValues();
        cvUpdateDb.put(KEY_NOTE_HEAD, header);
        cvUpdateDb.put(KEY_NOTE_CONTENT, content);
        cvUpdateDb.put(KEY_NOTE_TYPE, noteType);
        cvUpdateDb.put(KEY_IMAGES, image);
        ourDatabase.update(DATABASE_TABLE, cvUpdateDb, KEY_ROWID + "=" + id, null);
    }

    public void deleteNote(int id) {
        ourDatabase.delete(DATABASE_TABLE, KEY_ROWID + "=" + id, null);
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
                            KEY_NOTE_HEAD + " TEXT NOT NULL, " +
                            KEY_NOTE_CONTENT + " TEXT NOT NULL, " +
                            KEY_NOTE_TYPE + " TEXT NOT NULL, " +
                            KEY_LOCATION + " TEXT NOT NULL, " +
                            KEY_IMAGES + " TEXT NOT NULL, " +
                            KEY_SYNCEDTOPC + " TEXT NOT NULL, " +
                            KEY_WITH_PHONE_NO + " TEXT NOT NULL);"
            );
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);
            onCreate(db);
        }
    }
}
