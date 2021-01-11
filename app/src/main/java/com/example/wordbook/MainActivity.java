package com.example.wordbook;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;

public class MainActivity extends AppCompatActivity {
    private static final int SELECT_PHOTO = 102;
    public static final int WRITE_STORAGE = 100;
    private Button cam,gal;


    private static final int REQUEST_IMAGE_CAPTURE = 1;
    //private static final int MY_CAMERA_PERMISSION_CODE = 100;
    public File photo;


    String currentPhotoPath;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkPermission(WRITE_STORAGE);
        setContentView(R.layout.activity_main);
        cam = findViewById(R.id.camera);
        gal= findViewById(R.id.gallery);
        cam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = getApplicationContext();
                String filename = "photo";
                File StorageDirectory = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
                try {
                    File imagefile = new File(context.getExternalFilesDir("jpg"), "my_images");

                    currentPhotoPath = imagefile.getAbsolutePath();

                    if (imagefile!=null) {


                        Uri imageUri = FileProvider.getUriForFile(MainActivity.this, "com.example.wordbook", imagefile);

                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                      intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                        startActivityForResult(intent,REQUEST_IMAGE_CAPTURE);

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        gal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
       //    checkPermission(WRITE_STORAGE);
selectPicture();

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case SELECT_PHOTO:
                Uri dataUri = data.getData();
                String path = MyHelper.getPath(this, dataUri);
//                Toast.makeText(this,"Path : "+path,Toast.LENGTH_SHORT).show();
                Intent v =new Intent(this,MainActivity2.class);
 //               Toast.makeText(this, "URI : ", Toast.LENGTH_LONG).show();
              //    v.setData(dataUri);
                v.putExtra("cappath",path);
                  startActivity(v);
            break;
        }

        if (requestCode==REQUEST_IMAGE_CAPTURE && resultCode==RESULT_OK)
        {


            Intent w  =new Intent(this,MainActivity2.class);
            w.putExtra("cappath",currentPhotoPath);
            startActivity(w);
//            myBitmap = BitmapFactory.decodeFile(currentPhotoPath);
            //    ImageView imageView = findViewById(R.id.imageview1);
//            imageView.setImageBitmap(myBitmap);




        }
//        Toast.makeText(this, "URI : ", Toast.LENGTH_LONG).show();
    }

    public void checkPermission(int requestCode) {
        switch (requestCode) {
            case WRITE_STORAGE:
                int hasWriteExternalStoragePermission = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

//If we have access to external storage...//

                if (hasWriteExternalStoragePermission == PackageManager.PERMISSION_GRANTED) {

//...call selectPicture, which launches an Activity where the user can select an image//

                 //   selectPicture();

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
