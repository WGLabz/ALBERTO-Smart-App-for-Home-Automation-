package in.blogspot.weargenius.alberto.databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import in.blogspot.weargenius.alberto.utilities.GetDateTime;

/**
 * Created by OKSBWN on 1/1/2015.
 */
public class ProductsDB {
    public static final String KEY_ROWID = "Sl_No";
    public static final String KEY_DATE = "currentDate";
    public static final String KEY_ITEM = "item";
    public static final String KEY_COST = "cost";
    public static final String KEY_FROM = "fromVendor";
    public static final String KEY_FILE = "image";
    public static final String KEY_WARRANTY = "warranty";
    public static final String KEY_SYNCEDTOPC = "syncPC";

    private static final String DATABASE_NAME = "myAlbertoPurchase";
    private static final String DATABASE_TABLE = "myPurchases";
    private static final int DATABASE_VERSION = 1;
    private final Context ourContext;
    private DbHelper ourHelper;
    private SQLiteDatabase ourDatabase;

    public ProductsDB(Context c) {
        ourContext = c;
    }

    public ProductsDB open() {
        ourHelper = new DbHelper(ourContext);
        ourDatabase = ourHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        ourHelper.close();
    }

    public long addNewProduct(String cost, String warranty, String item, String from, String file) {
        String[] dateTime = new GetDateTime().getDateTime();
        ContentValues cv = new ContentValues();
        cv.put(KEY_DATE, dateTime[0]);
        cv.put(KEY_ITEM, item);
        cv.put(KEY_COST, cost);
        cv.put(KEY_FILE, file);
        cv.put(KEY_FROM, from);
        cv.put(KEY_WARRANTY, warranty);
        cv.put(KEY_SYNCEDTOPC, "No");
        Log.d("Products", item);
        return ourDatabase.insert(DATABASE_TABLE, null, cv);
    }

    public Cursor getOldProducts() {
        String[] columns = new String[]{KEY_DATE, KEY_ITEM, KEY_COST, KEY_FILE, KEY_FROM, KEY_WARRANTY, KEY_ROWID, KEY_SYNCEDTOPC};
        Cursor c = ourDatabase.query(DATABASE_TABLE, columns, null, null, null, null, KEY_ROWID + " DESC", null);

        return c;
    }

    public void deleteProduct(Integer id) {
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
                            KEY_ITEM + " TEXT NOT NULL, " +
                            KEY_COST + " TEXT NOT NULL, " +
                            KEY_FROM + " TEXT NOT NULL, " +
                            KEY_WARRANTY + " TEXT NOT NULL, " +
                            KEY_FILE + " TEXT NOT NULL, " +
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

