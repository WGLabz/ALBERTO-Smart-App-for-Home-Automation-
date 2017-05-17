package com.oksbwn.allDatabaseSetups;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.oksbwn.serverActivity.PostCallToServer;

/**
 * Created by OKSBWN on 1/6/2015.
 */
public class addFoodToDatabase {
    public static final String KEY_ROWID="Sl_No";
    public static final String KEY_DATE="currentDate";
    public static final String KEY_ITEM="foodItem";
    public static final String KEY_WHAT="what";
    public static final String KEY_SYNCEDTOPC="syncPC";

    private static final String DATABASE_NAME="myDailyFood";
    private static final String DATABASE_TABLE="myFood";
    private static final int DATABASE_VERSION=1;

    private DbHelper ourHelper;
    private final Context ourContext;
    private SQLiteDatabase ourDatabase;

    private static class DbHelper extends SQLiteOpenHelper
    {
        public DbHelper(Context context){
            super(context,DATABASE_NAME,null,DATABASE_VERSION);
        }
        @Override
        public void onCreate(SQLiteDatabase db){
            db.execSQL("CREATE TABLE "+DATABASE_TABLE+" ("+
                            KEY_ROWID+" INTEGER PRIMARY KEY AUTOINCREMENT, "+
                            KEY_DATE+ " TEXT NOT NULL, " +
                            KEY_ITEM+ " TEXT NOT NULL, " +
                            KEY_WHAT+ " TEXT NOT NULL, " +
                            KEY_SYNCEDTOPC+ " TEXT NOT NULL);"
            );
        }
        @Override
        public void onUpgrade(SQLiteDatabase db,int oldVersion,int newVersion){
            db.execSQL("DROP TABLE IF EXISTS "+DATABASE_TABLE);
            onCreate(db);
        }
    }
    public addFoodToDatabase(Context c){
        ourContext=c;
    }
    public addFoodToDatabase open(){
        ourHelper=new DbHelper(ourContext);
        ourDatabase=ourHelper.getWritableDatabase();
        return this;
    }
    public void close(){
        ourHelper.close();
    }
    public long insertData(String date,String items,String what){
        ContentValues cv=new ContentValues();
        cv.put(KEY_DATE,date);
        cv.put(KEY_ITEM,items);
        cv.put(KEY_WHAT,what);
        cv.put(KEY_SYNCEDTOPC,"No");
        return ourDatabase.insert(DATABASE_TABLE,null,cv);

    }
    public boolean addedTodayMeals(String what,String date){
        String[] columns=new String[]{KEY_ROWID};
        boolean statReturn=false;
        try {
            Cursor c = ourDatabase.query(DATABASE_TABLE, columns, KEY_WHAT + "= ? AND "+KEY_DATE+"= ?", new String[]{what,date}, null, null, null);
            if(c.getCount()==0){
                statReturn=true;
             }
            else{
                statReturn=false;
            }
        }catch(Exception e){
            Log.d("Meals database Error",e.getMessage());
        }

        return statReturn;
    }
    public String putDataToServer()
    {
        String[] columns=new String[]{KEY_ROWID,KEY_ITEM,KEY_WHAT,KEY_DATE,KEY_SYNCEDTOPC};
        String result="";
        try{
            Cursor c=ourDatabase.query(DATABASE_TABLE, columns, KEY_SYNCEDTOPC + "= ?", new String[]{"No"}, null, null, null,"30");
            int iRow=c.getColumnIndex(KEY_ROWID);
            int iDate=c.getColumnIndex(KEY_DATE);
            int iNote=c.getColumnIndex(KEY_ITEM);
            int iType=c.getColumnIndex(KEY_WHAT);
            if(c!=null){
                for(c.moveToFirst();!c.isAfterLast();c.moveToNext()){
                    try{
                        PostCallToServer pc=  new PostCallToServer("http://192.168.0.1/smart_home/API/android/addFoodToDatabase.php",
                                new String[]{"DATE","ITEM","WHAT"},
                                new String[]{c.getString(iDate),c.getString(iNote),c.getString(iType)});

                        while(pc.getResponse()==null){}
                        if(pc.getResponse().contains("1"))
                        {
                            //ourDatabase.update();
                            ContentValues cvUpdateDb= new ContentValues();
                            cvUpdateDb.put(KEY_SYNCEDTOPC,"Yes");
                            ourDatabase.update(DATABASE_TABLE,cvUpdateDb,KEY_ROWID+"="+c.getInt(iRow),null);
                            Log.d("Server Response",pc.getResponse());
                        }
                    }catch(Exception e){
                        Log.d("Error", e.getMessage());
                    }
                }
            }
        }catch(Exception e){ }
        return result;
    }
}
