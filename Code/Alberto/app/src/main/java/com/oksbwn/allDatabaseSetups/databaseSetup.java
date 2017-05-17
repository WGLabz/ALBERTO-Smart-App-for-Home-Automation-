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
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by OKSBWN on 1/1/2015.
 */
public class databaseSetup {
    public static final String KEY_ROWID="Sl_No";
    public static final String KEY_DATE="currentDate";
    public static final String KEY_TIME="currentTime";
    public static final String KEY_LATITUDE="longitude";
    public static final String KEY_LONGITUDE="latitude";
    public static final String KEY_VELOCITY="velocity";

    private static final String DATABASE_NAME="myAlbertoApplication";
    private static final String DATABASE_TABLE="currentLocationTable";
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
                            KEY_LATITUDE+ " TEXT NOT NULL, " +
                            KEY_LONGITUDE+ " TEXT NOT NULL, " +
                            KEY_VELOCITY+ " TEXT NOT NULL);"
            );
        }
        @Override
        public void onUpgrade(SQLiteDatabase db,int oldVersion,int newVersion){
            db.execSQL("DROP TABLE IF EXISTS "+DATABASE_TABLE);
            onCreate(db);
        }
    }
    public databaseSetup(Context c){
        ourContext=c;
    }
    public databaseSetup open(){
        ourHelper=new DbHelper(ourContext);
        ourDatabase=ourHelper.getWritableDatabase();
        return this;
    }
    public void close(){
        ourHelper.close();
    }
    public long insertData(String date,String time,String lat,String lon,String vel){
        ContentValues cv=new ContentValues();
        cv.put(KEY_DATE,date);
        cv.put(KEY_TIME,time);
        cv.put(KEY_LATITUDE,lat);
        cv.put(KEY_LONGITUDE,lon);
        cv.put(KEY_VELOCITY,vel);
        return ourDatabase.insert(DATABASE_TABLE,null,cv);

    }
    public String putDataToServer()
    {
        String[] columns=new String[]{KEY_ROWID,KEY_LONGITUDE,KEY_LATITUDE,KEY_DATE,KEY_TIME,KEY_VELOCITY};
        String result="";
        int idsToDelete[];
        int counter=0;
        try{
            Cursor c=ourDatabase.query(DATABASE_TABLE, columns, null, null, null, null, null,"30");
            int iRow=c.getColumnIndex(KEY_ROWID);
            int iTime=c.getColumnIndex(KEY_TIME);
            int iDate=c.getColumnIndex(KEY_DATE);
            int iLong=c.getColumnIndex(KEY_LONGITUDE);
            int iLat=c.getColumnIndex(KEY_LATITUDE);
            int iVel=c.getColumnIndex(KEY_VELOCITY);
            idsToDelete=new int[c.getCount()];
            JSONObject jsonObj = new JSONObject();
            JSONArray jsonArr = new JSONArray();
            if(c!=null){
                for(c.moveToFirst();!c.isAfterLast();c.moveToNext()){
                   // result=result+c.getString(iRow)+" "+c.getString(iDate)+" "+ c.getString(iLat)+" "+c.getString(iLong)+" "+c.getString(iTime)+" "+c.getString(iVel);
                    try{
                        JSONObject pnObj = new JSONObject();
                        pnObj.put("LAT",c.getString(iLat));
                        pnObj.put("LONG",c.getString(iLong));
                        pnObj.put("VEL",c.getString(iVel));
                        pnObj.put("TIM",c.getString(iTime));
                        pnObj.put("DAT",c.getString(iDate));
                        jsonArr.put(pnObj);
                        idsToDelete[counter]= c.getInt(iRow);
                        counter++;
                    }catch(Exception e){}
                }
                try {
                    jsonObj.put("DATA", jsonArr);
                    Log.d("Message is : ", jsonObj.toString());


                    HttpClient client = new DefaultHttpClient();
                    HttpPost post = new HttpPost("http://192.168.0.10/API/fromMobile/addMyLocations.php");

                    List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
                    nameValuePairs.add(new BasicNameValuePair("DATA",jsonObj.toString()));
                    post.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                    HttpResponse response = client.execute(post);

                    BufferedReader rd = new BufferedReader( new InputStreamReader(response.getEntity().getContent()));

                    StringBuffer res = new StringBuffer();
                    String line = "";
                    while ((line = rd.readLine()) != null) {
                        res.append(line);
                    }
                    if(res.toString().contains("1"))
                    {
                        for(counter=0;counter<idsToDelete.length;counter++) {
                            ourDatabase.delete(DATABASE_TABLE, KEY_ROWID + "=" + idsToDelete[counter], null);
                        }
                    }

                }catch(Exception e){
                    Log.d("Exception is : ", e.getMessage());
                }
            }
        }catch(Exception e){ }
        return result;
    }
    public void deleteData()
    {
        try {
            ourDatabase.delete(DATABASE_TABLE, KEY_ROWID + "=1", null);
        }catch(Exception e){ }
    }
}
