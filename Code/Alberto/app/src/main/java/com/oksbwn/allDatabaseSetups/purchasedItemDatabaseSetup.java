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
 */
public class purchasedItemDatabaseSetup {
    public static final String KEY_ROWID="Sl_No";
    public static final String KEY_DATE="currentDate";
    public static final String KEY_ITEM="item";
    public static final String KEY_COST="cost";
    public static final String KEY_FROM="fromVendor";
    public static final String KEY_FILE="image";
    public static final String KEY_SYNCEDTOPC="syncPC";

    private static final String DATABASE_NAME="myAlbertoPurchase";
    private static final String DATABASE_TABLE="myPurchases";
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
                            KEY_FROM+ " TEXT NOT NULL, " +
                            KEY_FILE+ " TEXT NOT NULL, " +
                            KEY_SYNCEDTOPC+ " TEXT NOT NULL);"
            );
        }
        @Override
        public void onUpgrade(SQLiteDatabase db,int oldVersion,int newVersion){
            db.execSQL("DROP TABLE IF EXISTS "+DATABASE_TABLE);
            onCreate(db);
        }
    }
    public purchasedItemDatabaseSetup(Context c){
        ourContext=c;
    }
    public purchasedItemDatabaseSetup open(){
        ourHelper=new DbHelper(ourContext);
        ourDatabase=ourHelper.getWritableDatabase();
        return this;
    }
    public void close(){
        ourHelper.close();
    }
    public long insertData(String date,String cost,String item,String from,String file){
        ContentValues cv=new ContentValues();
        cv.put(KEY_DATE,date);
        cv.put(KEY_ITEM,item);
        cv.put(KEY_COST,cost);
        cv.put(KEY_FILE,file);
        cv.put(KEY_FROM,from);
        cv.put(KEY_SYNCEDTOPC,"No");
        return ourDatabase.insert(DATABASE_TABLE,null,cv);

    }
    public String[] getDataToUpload()
    {
        String[] columns=new String[]{KEY_ROWID,KEY_ITEM,KEY_COST,KEY_DATE,KEY_FILE,KEY_FROM,KEY_SYNCEDTOPC};
        String[] paths=null;
        try{
            Cursor c=ourDatabase.query(DATABASE_TABLE, columns, KEY_SYNCEDTOPC +"= ?", new String[] {"No"}, null, null, null,"30");
            int iRow=c.getColumnIndex(KEY_ROWID);
            int iItem=c.getColumnIndex(KEY_ITEM);
            int iDate=c.getColumnIndex(KEY_DATE);
            int iCost=c.getColumnIndex(KEY_COST);
            int ifrom=c.getColumnIndex(KEY_FROM);
            int ipath=c.getColumnIndex(KEY_FILE);
            paths=new String[c.getCount()];
            int iCountPaths=0;
            if(c!=null){
                for(c.moveToFirst();!c.isAfterLast();c.moveToNext()){
                   // result=result+c.getString(iRow)+" "+c.getString(iDate)+" "+ c.getString(iLat)+" "+c.getString(iLong)+" "+c.getString(iTime)+" "+c.getString(iVel);
                    try{
                            HttpClient client = new DefaultHttpClient();
                            HttpPost post = new HttpPost("http://192.168.0.10/API/fromMobile/addPurchaseDetails.php");

                            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
                            nameValuePairs.add(new BasicNameValuePair("ITEM",c.getString(iItem)));
                            nameValuePairs.add(new BasicNameValuePair("COST",c.getString(iCost)));
                            nameValuePairs.add(new BasicNameValuePair("DAT",c.getString(iDate)));
                            nameValuePairs.add(new BasicNameValuePair("FRM",c.getString(ifrom)));
                            nameValuePairs.add(new BasicNameValuePair("IMG",c.getString(ipath)));
                            post.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                            HttpResponse response = client.execute(post);

                            BufferedReader rd = new BufferedReader(
                                    new InputStreamReader(response.getEntity().getContent()));

                            StringBuffer res = new StringBuffer();
                            String line = "";
                            while ((line = rd.readLine()) != null) {
                                res.append(line);
                            }
                            if(res.toString().contains("1"))
                            {
                                //ourDatabase.update();
                                ourDatabase.delete(DATABASE_TABLE,KEY_ROWID+"="+c.getInt(iRow),null);

                            }
                        paths[iCountPaths]=c.getString(c.getColumnIndex(KEY_FILE));
                        iCountPaths++;
                    }catch(Exception e){
                        Log.d("Error",e.getMessage());
                    }
                }
            }
        }catch(Exception e){ }
        return paths;
    }
}
