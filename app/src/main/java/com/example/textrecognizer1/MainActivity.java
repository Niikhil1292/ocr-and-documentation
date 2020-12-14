

package com.example.textrecognizer1;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;


import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import com.google.mlkit.common.sdkinternal.Constants;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends BaseActivity {
    private static final int REQUEST_IMAGE_CAPTURE = 1;


    private Bitmap myBitmap;
    private ImageView myImageView;
    private TextView myTextView;
    private Button createpdf, check;
    private LinearLayout llPdf;
    private Object String;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        createpdf = findViewById(R.id.button);
        llPdf = findViewById(R.id.llpdf);
        myTextView = findViewById(R.id.textView);
        myImageView = findViewById(R.id.imageView);

        check = findViewById(R.id.checkText);

        check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (myBitmap != null) {
                    runTextRecog();
                }
            }
        });

        createpdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                createPdf();

            }
        });



    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case WRITE_STORAGE:
                    checkPermission(requestCode);
                    break;
                case SELECT_PHOTO:
                    Uri dataUri = data.getData();
                    String path = MyHelper.getPath(this, dataUri);
                    if (path == null) {
                        myBitmap = MyHelper.resizePhoto(photo, this, dataUri, myImageView);
                    } else {
                        myBitmap = MyHelper.resizePhoto(photo, path, myImageView);
                    }
                    if (myBitmap != null) {
                        myTextView.setText(null);
                        myImageView.setImageBitmap(myBitmap);
                    }
                    break;

            }

            if (requestCode==REQUEST_IMAGE_CAPTURE && resultCode==RESULT_OK)
            {



                 myBitmap = BitmapFactory.decodeFile(currentPhotoPath);
            //    ImageView imageView = findViewById(R.id.imageview1);
                myImageView.setImageBitmap(myBitmap);




            }

        }
    }

    private void runTextRecog() {
        InputImage image = InputImage.fromBitmap(myBitmap, 0);
        TextRecognizer recognizer = TextRecognition.getClient();
        recognizer.process(image)
                .addOnSuccessListener(new OnSuccessListener<Text>() {
                    @Override
                    public void onSuccess(Text visionText) {
                        myTextView.setText(visionText.getText());
                    }
                })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
    }


    private void createPdf() {


        PdfDocument document = new PdfDocument();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(1152, 1625, 1).create();
        PdfDocument.Page page = document.startPage(pageInfo);

        Canvas canvas = page.getCanvas();

        Paint paint = new Paint();
        paint.setColor(Color.WHITE);

        canvas.drawPaint(paint);
        paint.setTypeface(Typeface.create("Arial",Typeface.NORMAL) );


        paint.setColor(Color.BLACK);
        paint.setTextSize(20);

        String srn=myTextView.getText().toString();

        Rect bounds = new Rect();
        String[] lines = srn.split("\n");
       int x=60;
       int y=100;

        int yoff = 0;
        for (int i = 0; i < lines.length; ++i) {
            canvas.drawText(lines[i], x, y + yoff, paint);
            paint.getTextBounds(lines[i], 0, lines[i].length(), bounds);
            yoff += bounds.height() + 20;

        }





        document.finishPage(page);

        // write the document content
        Context context = getApplicationContext();

        File filePath = new File(context.getExternalFilesDir("pdf"), "external_files");

        try {

            document.writeTo(new FileOutputStream(filePath));

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Something wrong: " + e.toString(), Toast.LENGTH_LONG).show();
        }

        // close the document
        document.close();
        Toast.makeText(this, "PDF is created!!!", Toast.LENGTH_SHORT).show();

        openGeneratedPDF(filePath, context);
        myTextView.setText("");
        myImageView.setImageDrawable(getApplicationContext().getDrawable(R.drawable.ic_placeholder));

    }


    private void openGeneratedPDF(File filePath, Context context) {
        File file = filePath;
        if (file.exists()) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            Uri uri = FileProvider.getUriForFile(context, "com.example.textrecognizer1", file);
            context.grantUriPermission(context.getPackageName(), uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setDataAndType(uri, "application/pdf");
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);




            try {
                startActivity(intent);
            } catch (ActivityNotFoundException e) {
                Toast.makeText(MainActivity.this, "No Application available to view pdf", Toast.LENGTH_LONG).show();
            }
        }
    }
}