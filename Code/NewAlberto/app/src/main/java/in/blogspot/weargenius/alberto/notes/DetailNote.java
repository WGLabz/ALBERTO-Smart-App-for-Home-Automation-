package in.blogspot.weargenius.alberto.notes;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import in.blogspot.weargenius.alberto.R;
import in.blogspot.weargenius.alberto.databases.AllImagesDB;
import in.blogspot.weargenius.alberto.databases.NotesDetailDB;

public class DetailNote extends AppCompatActivity implements View.OnClickListener {
    private String imageFileName;
    private String mCurrentPhotoPath;
    private AppCompatEditText noteContent;
    private String location = " ";
    private AppCompatEditText header;
    private boolean old_note = false;
    private int noteId;
    private String imageFileNames = " ";
    private String imageFilePaths = " ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_note);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getApplicationContext(), NotesInterface.class);
                startActivity(intent);
                finish();
                Log.d("Notes_Details", "home selected");
            }
        });
        CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        header = (AppCompatEditText) findViewById(R.id.header);
        noteContent = (AppCompatEditText) findViewById(R.id.noteText);
        CardView noteType = (CardView) findViewById(R.id.note_type);
        CardView noteFacebookPost = (CardView) findViewById(R.id.note_post_facebook);
        CardView noteEmail = (CardView) findViewById(R.id.note_email);
        CardView noteLocation = (CardView) findViewById(R.id.note_location);
        CardView noteRemindTime = (CardView) findViewById(R.id.remind_date_time);
        CardView noteClearContent = (CardView) findViewById(R.id.clear_content);

        noteContent = (AppCompatEditText) findViewById(R.id.noteText);
        noteType.setOnClickListener(this);
        noteFacebookPost.setOnClickListener(this);
        noteEmail.setOnClickListener(this);
        noteLocation.setOnClickListener(this);
        noteRemindTime.setOnClickListener(this);
        noteClearContent.setOnClickListener(this);

        loadBackdrop();
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            header.setText(extras.getString("NOTE_TITLE"));
            noteContent.setText(extras.getString("NOTE_BODY"));
            noteId = extras.getInt("NOTE_ID");
            old_note = true;

            AllImagesDB newImageDB = new AllImagesDB(getApplicationContext());
            newImageDB.open();
            Cursor images = newImageDB.getImage("NOTE" + noteId);

            if (images.getCount() > 0) {
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(300, 300);
                LinearLayout layout = (LinearLayout) findViewById(R.id.images);
                int i = 0;
                for (images.moveToFirst(); i < images.getCount(); images.moveToNext()) {
                    String imagePath = images.getString(images.getColumnIndex("imagePath"));
                    ImageView imageView = new ImageView(this);
                    imageView.setPadding(2, 2, 2, 2);
                    Log.d("Camera_App", imagePath);
                    imageView.setLayoutParams(layoutParams);
                    // imageView.setImageBitmap(BitmapFactory.decodeFile(imagePath));
                    Glide.with(getApplicationContext()).load(imagePath).into(imageView);
                    imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                    layout.addView(imageView);
                    i++;
                }
            }
            newImageDB.close();
        }
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
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
    public void onBackPressed() {
        startActivity(new Intent(getApplicationContext(), NotesInterface.class));
        Log.d("Notes_Interface", "Back Pressed");
        super.onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (old_note) {
                AllImagesDB newImageDB = new AllImagesDB(getApplicationContext());
                newImageDB.open();
                newImageDB.addNewImage("NOTE" + noteId, imageFileName, "No", mCurrentPhotoPath);
                newImageDB.close();
                Log.d("Camera_App", "Iamge added to Db" + imageFileName + " " + mCurrentPhotoPath);
            }
            if (!old_note) {
                imageFileNames = imageFileNames + "," + imageFileName;
                imageFilePaths = imageFilePaths + "," + mCurrentPhotoPath;
                Log.d("Camera_App", "Iamge added to Db new note. " + imageFileName + " " + mCurrentPhotoPath);
            }
            Bundle extras = data.getExtras();
            Bitmap imageBmp = (Bitmap) extras.get("data");
            //cameraImage.setImageBitmap(imageBmp);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(300, 300);
            LinearLayout layout = (LinearLayout) findViewById(R.id.images);
            ImageView imageView = new ImageView(this);
            imageView.setPadding(2, 2, 2, 2);
            imageView.setLayoutParams(layoutParams);
            imageView.setImageBitmap(imageBmp);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            layout.addView(imageView);
        }
    }

    private File createImageFile() throws Exception {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_detailnote, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.save_note) {
            if (!old_note) {
                NotesDetailDB notesDB = new NotesDetailDB(getApplicationContext());
                notesDB.open();
                noteId = notesDB.addNewNote(header.getText().toString(), noteContent.getText().toString(), " ", location, " ", " ");
                notesDB.close();
                Log.d("Notes_all", noteContent.getText().toString());
                if (imageFileNames != " ") {
                    String[] fileNames = imageFileNames.split(",");
                    String[] filePaths = imageFilePaths.split(",");
                    AllImagesDB newImageDB = new AllImagesDB(getApplicationContext());
                    newImageDB.open();
                    for (int i = 1; i < fileNames.length; i++) {
                        newImageDB.addNewImage("NOTE" + noteId, fileNames[i], "No", filePaths[i]);
                        Log.d("Camera_App", "New image note added" + fileNames[i] + " " + filePaths[i]);
                    }
                    newImageDB.close();
                }

                Toast.makeText(getApplicationContext(), "Note saved", Toast.LENGTH_LONG).show();
            }
            if (old_note) {
                Toast.makeText(getApplicationContext(), "Note " + noteId + " Updated", Toast.LENGTH_LONG).show();
                NotesDetailDB notesDB = new NotesDetailDB(getApplicationContext());
                notesDB.open();
                notesDB.updateNote(header.getText().toString(), noteContent.getText().toString(), " ", noteId, " ");
                notesDB.close();
                Log.d("Notes_all", noteContent.getText().toString());
            }
            Intent intent = new Intent(getApplicationContext(), NotesInterface.class);
            startActivity(intent);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadBackdrop() {
        final ImageView imageView = (ImageView) findViewById(R.id.backdrop);
        Glide.with(this).load(R.drawable.note_header).into(imageView);
    }

    @Override
    public void onClick(View v) {
        Toast.makeText(getApplicationContext(), "Clicked." + v.getId(), Toast.LENGTH_LONG).show();
        switch (v.getId()) {
            case R.id.note_type:
                break;
            case R.id.note_post_facebook:
                break;
            case R.id.note_email:
                break;
            case R.id.note_location:
                break;
            case R.id.remind_date_time:
                break;
            case R.id.clear_content:
                noteContent.setText("");
                break;
        }
    }
}
