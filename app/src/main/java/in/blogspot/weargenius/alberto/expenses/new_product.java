package in.blogspot.weargenius.alberto.expenses;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import in.blogspot.weargenius.alberto.R;
import in.blogspot.weargenius.alberto.databases.ProductsDB;
import in.blogspot.weargenius.alberto.menu.UniversalMenuItemSelected;

public class new_product extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    String mCurrentPhotoPath;
    String imageFileName;
    private Bitmap imageBmp;
    private ImageView cameraImage;

    private TextView productCost;
    private TextView productWarranty;
    private TextView productFrom;
    private TextView productName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_product);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        cameraImage = (ImageView) findViewById(R.id.productImage);
        productName = (TextView) findViewById(R.id.item_name);
        productCost = (TextView) findViewById(R.id.item_cost);
        productWarranty = (TextView) findViewById(R.id.item_warranty);
        productFrom = (TextView) findViewById(R.id.item_from);

        cameraImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                File photoFile = null;
                try {
                    photoFile = createImageFile();

                    Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    i.putExtra(MediaStore.EXTRA_OUTPUT,
                            Uri.fromFile(photoFile));
                    startActivityForResult(i, 0);
                    galleryAddPic();
                } catch (Exception ex) {
                    Log.d("Camera Exception", ex.getMessage());
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            imageBmp = (Bitmap) extras.get("data");

            cameraImage.setImageBitmap(imageBmp);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.new_product, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.save) {
            ProductsDB products = new ProductsDB(getApplicationContext());
            products.open();
            products.addNewProduct(productCost.getText().toString(),
                    productWarranty.getText().toString(),
                    productName.getText().toString(),
                    productFrom.getText().toString(),
                    mCurrentPhotoPath);
            products.close();
            Toast.makeText(new_product.this, "New Product Added", Toast.LENGTH_SHORT).show();
            return true;
        }
        if (id == R.id.old_list) {
            startActivity(new Intent(getApplicationContext(), OldProducts.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        new UniversalMenuItemSelected(item, getApplicationContext());
        finish();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

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
