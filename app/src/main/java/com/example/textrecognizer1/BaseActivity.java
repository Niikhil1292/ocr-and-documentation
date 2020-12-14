package com.example.textrecognizer1;

import android.app.Activity;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.content.DialogInterface;
import android.content.Intent;
import android.Manifest;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.IOException;

public class BaseActivity extends AppCompatActivity {
    public static final int WRITE_STORAGE = 100;
    public static final int SELECT_PHOTO = 102;
    public static final String ACTION_BAR_TITLE = "action_bar_title";


    private static final int REQUEST_IMAGE_CAPTURE = 1;
    //private static final int MY_CAMERA_PERMISSION_CODE = 100;
    public File photo;


    String currentPhotoPath;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(getIntent().getStringExtra(ACTION_BAR_TITLE));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.my_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

//If “gallery_action” is selected, then...//

            case R.id.gallery_action:

//...check we have the WRITE_STORAGE permission//

                checkPermission(WRITE_STORAGE);
                break;



            case R.id.camera_action:

                createfileopencamera();
                break;



        }
        return super.onOptionsItemSelected(item);
    }

    private void createfileopencamera() {

        Context context = getApplicationContext();
        String filename = "photo";
        File StorageDirectory = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        try {
            File imagefile = new File(context.getExternalFilesDir("jpg"), "my_images");

            currentPhotoPath = imagefile.getAbsolutePath();

            if (imagefile!=null) {


                Uri imageUri = FileProvider.getUriForFile(BaseActivity.this, "com.example.textrecognizer1", imagefile);

                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(intent,REQUEST_IMAGE_CAPTURE);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    private void checkCAMERAPermission() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
//            {
//                requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_CAMERA_PERMISSION_CODE);
//            }
//            else
//            {
//                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                startActivityForResult(cameraIntent, CAMERA_REQUEST);
//            }
//        }
//    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case WRITE_STORAGE:

//If the permission request is granted, then...//

                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

//...call selectPicture//

                    selectPicture();

//If the permission request is denied, then...//

                } else {

//...display the “permission_request” string//

                    requestPermission(this, requestCode, R.string.permission_request);
                }
                break;

        }
    }

//Display the permission request dialog//

    public static void requestPermission(final Activity activity, final int requestCode, int msg) {
        AlertDialog.Builder alert = new AlertDialog.Builder(activity);
        alert.setMessage(msg);
        alert.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                Intent permissonIntent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                permissonIntent.setData(Uri.parse("package:" + activity.getPackageName()));
                activity.startActivityForResult(permissonIntent, requestCode);
            }
        });
        alert.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        alert.setCancelable(false);
        alert.show();
    }

//Check whether the user has granted the WRITE_STORAGE permission//

    public void checkPermission(int requestCode) {
        switch (requestCode) {
            case WRITE_STORAGE:
                int hasWriteExternalStoragePermission = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

//If we have access to external storage...//

                if (hasWriteExternalStoragePermission == PackageManager.PERMISSION_GRANTED) {

//...call selectPicture, which launches an Activity where the user can select an image//

                    selectPicture();

//If permission hasn’t been granted, then...//

                } else {

//...request the permission//

                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, requestCode);
                }
                break;

        }
    }

    private void selectPicture() {
        photo = MyHelper.createTempFile(photo);
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

//Start an Activity where the user can choose an image//

        startActivityForResult(intent, SELECT_PHOTO);
    }

}
