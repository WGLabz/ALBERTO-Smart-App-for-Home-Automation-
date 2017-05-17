package com.oksbwn.allDatabaseSetups;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by OKSBWN on 1/1/2015.
 * Edited last
 */
public class notesDatabaseSetup {
    public static final String KEY_ROWID="Sl_No";
    public static final String KEY_DATE="currentDate";
    public static final String KEY_TIME="currentTime";
    public static final String KEY_NOTE="note";
    public static final String KEY_NOTETYPE="noteType";
    public static final String KEY_SYNCEDTOPC="syncPC";

    private static final String DATABASE_NAME="myAlbertoAppNotes";
    private static final String DATABASE_TABLE="myNotes";
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
                            KEY_TIME+ " TEXT NOT NULL, " +
                            KEY_NOTE+ " TEXT NOT NULL, " +
                            KEY_NOTETYPE+ " TEXT NOT NULL, " +
                            KEY_SYNCEDTOPC+ " TEXT NOT NULL);"
            );
        }
        @Override
        public void onUpgrade(SQLiteDatabase db,int oldVersion,int newVersion){
            db.execSQL("DROP TABLE IF EXISTS "+DATABASE_TABLE);
            onCreate(db);
        }
    }
    public notesDatabaseSetup(Context c){
        ourContext=c;
    }
    public notesDatabaseSetup open(){
        ourHelper=new DbHelper(ourContext);
        ourDatabase=ourHelper.getWritableDatabase();
        return this;
    }
    public void close(){
        ourHelper.close();
    }
    public long insertData(String date,String time,String note,String type){
        ContentValues cv=new ContentValues();
        cv.put(KEY_DATE,date);
        cv.put(KEY_TIME,time);
        cv.put(KEY_NOTE,note);
        cv.put(KEY_NOTETYPE,type);
        cv.put(KEY_SYNCEDTOPC,"No");
        return ourDatabase.insert(DATABASE_TABLE,null,cv);

    }
    public Cursor getAllNotes(){
        String[] columns=new String[]{KEY_ROWID,KEY_NOTE,KEY_NOTETYPE,KEY_DATE,KEY_TIME,KEY_SYNCEDTOPC};
        Cursor c=null;
        try{
             c=ourDatabase.query(DATABASE_TABLE, columns,null,null, null, null, null);
        }catch(Exception e){
        Log.d("Error in notes database","Error in retrieving notes");
        }
        return c;
    }
    public int deleteNote(int id){
        Log.d("Delete id is",""+id);
       return ourDatabase.delete(DATABASE_TABLE, KEY_ROWID+"=?", new String[]{""+id});
    }
    public String putDataToServer()
    {
        String[] columns=new String[]{KEY_ROWID,KEY_NOTE,KEY_NOTETYPE,KEY_DATE,KEY_TIME,KEY_SYNCEDTOPC};
        String result="";
        try{
            Cursor c=ourDatabase.query(DATABASE_TABLE, columns, KEY_SYNCEDTOPC +"= ?", new String[] {"No"}, null, null, null,"30");
            int iRow=c.getColumnIndex(KEY_ROWID);
            int iTime=c.getColumnIndex(KEY_TIME);
            int iDate=c.getColumnIndex(KEY_DATE);
            int iNote=c.getColumnIndex(KEY_NOTE);
            int iType=c.getColumnIndex(KEY_NOTETYPE);
            if(c!=null){
                for(c.moveToFirst();!c.isAfterLast();c.moveToNext()){
                   // result=result+c.getString(iRow)+" "+c.getString(iDate)+" "+ c.getString(iLat)+" "+c.getString(iLong)+" "+c.getString(iTime)+" "+c.getString(iVel);
                    try{
                        HttpClient client = new DefaultHttpClient();
                        HttpPost post = new HttpPost("http://192.168.0.10/API/fromMobile/addMyNotes.php");

                        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
                        nameValuePairs.add(new BasicNameValuePair("NOTE",c.getString(iNote)));
                        nameValuePairs.add(new BasicNameValuePair("TYPE",c.getString(iType)));
                        nameValuePairs.add(new BasicNameValuePair("DAT",c.getString(iDate)+" "+c.getString(iTime)));
                        post.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                        HttpResponse response = client.execute(post);

                        BufferedReader rd = new BufferedReader(
                                new InputStreamReader(response.getEntity().getContent()));

                        StringBuffer res = new StringBuffer();
                        String line;
                        while ((line = rd.readLine()) != null) {
                            res.append(line);
                        }
                        if(res.toString().contains("1"))
                        {
                          //ourDatabase.update();
                            ContentValues cvUpdateDb= new ContentValues();
                            cvUpdateDb.put(KEY_SYNCEDTOPC,"Yes");
                            ourDatabase.update(DATABASE_TABLE,cvUpdateDb,KEY_ROWID+"="+c.getInt(iRow),null);

                        }
                    }catch(Exception e){
                        Log.d("Error",e.getMessage());
                    }
                }
            }
        }catch(Exception e){
        Log.d("Error in database","Error in notes syncing");
        }
        return result;
    }
}
