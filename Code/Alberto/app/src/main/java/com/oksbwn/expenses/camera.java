package com.oksbwn.expenses;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.oksbwn.R;
import com.oksbwn.allDatabaseSetups.purchasedItemDatabaseSetup;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class camera extends Activity {
    ImageButton takePicture;
    ImageView cameraImage;
    int cameraData=0;
    Bitmap imageBmp;
    EditText cost;
    EditText from;
    EditText itemPurchased;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.photo);

        ActionBar ab = getActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        cameraImage=(ImageView)findViewById(R.id.camera_image);
        cost=(EditText)findViewById(R.id.itemCost);
        from=(EditText)findViewById(R.id.itemFrom);
        itemPurchased=(EditText)findViewById(R.id.itemName);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.camera, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        Thread th;
        switch (item.getItemId()) {
            // action with ID action_refresh was selected
            case R.id.cameraSave:
                File photoFile = null;
                try {
                    photoFile = createImageFile();

                    Intent i=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    i.putExtra(MediaStore.EXTRA_OUTPUT,
                            Uri.fromFile(photoFile));
                    startActivityForResult(i, cameraData);
                    galleryAddPic();
                } catch (Exception ex) {
                    Log.d("Camera Exception", ex.getMessage());
                }
                break;
            case R.id.savePurchase:
                try {
                    SimpleDateFormat sdf= new SimpleDateFormat("dd/MMM/yyyy");
                    purchasedItemDatabaseSetup itemDb= new purchasedItemDatabaseSetup(camera.this);
                    itemDb.open();
                    itemDb.insertData(sdf.format(new Date()),cost.getText().toString(),itemPurchased.getText().toString(),from.getText().toString(),mCurrentPhotoPath);
                    itemDb.close();
                } catch (Exception ex) {
                    Log.d("Camera Exception", ex.getMessage());
                }
                break;
            case android.R.id.home:
                try {
                    Intent iHome = new Intent(getApplicationContext(),com.oksbwn.main.mainActivity.class);
                    iHome.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(iHome);
                    finish();
                }catch(Exception e){
                    Log.d("Exception :",e.getMessage());
                }
                return true;
            default:
                break;
        }

        return true;
    }
    @Override
    protected void onActivityResult(int requestCode,int resultCode,Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        if(resultCode==RESULT_OK){
            Bundle extras =data.getExtras();
            imageBmp=(Bitmap)extras.get("data");
            cameraImage.setImageBitmap(imageBmp);
        }
    }
    String mCurrentPhotoPath;
    String imageFileName;
    private File createImageFile() throws Exception {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        //"file:" +
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }
    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }
}