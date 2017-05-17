package com.oksbwn.allDatabaseSetups;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.oksbwn.serverActivity.PostCallToServer;

/**
 * Created by OKSBWN on 1/1/2015.
 */
public class expenseDatabaseSetup {
    public static final String KEY_ROWID="Sl_No";
    public static final String KEY_DATE="currentDate";
    public static final String KEY_ITEM="item";
    public static final String KEY_COST="cost";
    public static final String KEY_SYNCEDTOPC="syncPC";

    private static final String DATABASE_NAME="myAlbertoExpenses";
    private static final String DATABASE_TABLE="myExpenses";
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
                            KEY_COST+ " TEXT NOT NULL, " +
                            KEY_SYNCEDTOPC+ " TEXT NOT NULL);"
            );
        }
        @Override
        public void onUpgrade(SQLiteDatabase db,int oldVersion,int newVersion){
            db.execSQL("DROP TABLE IF EXISTS "+DATABASE_TABLE);
            onCreate(db);
        }
    }
    public expenseDatabaseSetup(Context c){
        ourContext=c;
    }
    public expenseDatabaseSetup open(){
        ourHelper=new DbHelper(ourContext);
        ourDatabase=ourHelper.getWritableDatabase();
        return this;
    }
    public void close(){
        ourHelper.close();
    }
    public long insertData(String date,String note,String type){
        ContentValues cv=new ContentValues();
        cv.put(KEY_DATE,date);
        cv.put(KEY_ITEM,type);
        cv.put(KEY_COST,note);
        cv.put(KEY_SYNCEDTOPC,"No");
        return ourDatabase.insert(DATABASE_TABLE,null,cv);

    }
    public Cursor getPreviousExpenses(){
        String[] columns=new String[]{KEY_ROWID,KEY_ITEM,KEY_COST,KEY_DATE,KEY_SYNCEDTOPC};
        Cursor c=null;
        try{
             c=ourDatabase.query(DATABASE_TABLE, columns,null,null, null, null, null);
        }catch(Exception e){Log.d("Database Error","Error"); }
        return c;
    }
    public String putDataToServer()
    {
        String[] columns=new String[]{KEY_ROWID,KEY_ITEM,KEY_COST,KEY_DATE,KEY_SYNCEDTOPC};
        String result="";
        try{
            Cursor c=ourDatabase.query(DATABASE_TABLE, columns, KEY_SYNCEDTOPC +"= ?", new String[] {"No"}, null, null, null,"30");
            int iRow=c.getColumnIndex(KEY_ROWID);
            int iItem=c.getColumnIndex(KEY_ITEM);
            int iDate=c.getColumnIndex(KEY_DATE);
            int iCost=c.getColumnIndex(KEY_COST);
            if(c!=null){
                for(c.moveToFirst();!c.isAfterLast();c.moveToNext()){
                   // result=result+c.getString(iRow)+" "+c.getString(iDate)+" "+ c.getString(iLat)+" "+c.getString(iLong)+" "+c.getString(iTime)+" "+c.getString(iVel);
                    try{
                        PostCallToServer pc=  new PostCallToServer("http://192.168.0.1/smart_home/API/android/addExpenses.php",
                                new String[]{"ITEM","COST","DAT"},
                                new String[]{c.getString(iItem),c.getString(iCost),c.getString(iDate)});

                        while(pc.getResponse()==null){}
                        if(pc.getResponse().contains("1"))
                        {
                            //ourDatabase.update();
                            ContentValues cvUpdateDb= new ContentValues();
                            cvUpdateDb.put(KEY_SYNCEDTOPC,"Yes");
                            ourDatabase.update(DATABASE_TABLE,cvUpdateDb,KEY_ROWID+"="+c.getInt(iRow),null);

                        }
                    }catch(Exception e){
                        Log.d("Error", e.getMessage());
                    }
                }
            }
        }catch(Exception e){ }
        return result;
    }
    public int deleteExpense(int id){
        Log.d("Delete id is",""+id);
        return ourDatabase.delete(DATABASE_TABLE, KEY_ROWID+"=?", new String[]{""+id});
    }
}
