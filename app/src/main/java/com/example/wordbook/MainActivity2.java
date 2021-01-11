package com.example.wordbook;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;

import java.io.File;
import java.io.FileOutputStream;

public class MainActivity2 extends AppCompatActivity {
    Uri ss;
    ImageView imageView;
    TextView myTextView;
    private Bitmap myBitmap;
    public File photo;
    Button check, save;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
//        ss = getIntent().getData();
        imageView = findViewById(R.id.imageview);
        myTextView = findViewById(R.id.textview);
        String path = getIntent().getStringExtra("cappath");
//        String path = MyHelper.getPath(this, ss);
        photo = MyHelper.createTempFile(photo);


//          Toast.makeText(this, "Path : "+path, Toast.LENGTH_LONG).show();

        if (path == null) {

            Toast.makeText(this, "Path not found ", Toast.LENGTH_LONG).show();
//            myBitmap = MyHelper.resizePhoto(photo, this, ss, imageView);
        } else {
            myBitmap = MyHelper.resizePhoto(photo, path, imageView);
//            Toast.makeText(this, "Path : " + path, Toast.LENGTH_LONG).show();
        }
        if (myBitmap != null) {
            // myTextView.setText(null);
            imageView.setImageBitmap(myBitmap);
        }
//
//        Toast.makeText(this,ss.toString(),Toast.LENGTH_SHORT).show();


        check = findViewById(R.id.check);
        save = findViewById(R.id.save);
        check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                runTextRecog();

            }
        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            createPdf();
            }
        });

    }

    private void runTextRecog() {
        InputImage image = InputImage.fromBitmap(myBitmap, 0);
        TextRecognizer recognizer = TextRecognition.getClient();
        recognizer.process(image)
                .addOnSuccessListener(new OnSuccessListener<Text>() {
                    @Override
                    public void onSuccess(Text visionText) {
                        if(visionText.getText()=="")
                        {
                            Toast.makeText(MainActivity2.this, "No text found", Toast.LENGTH_SHORT).show();
                        }
                        myTextView.setText(visionText.getText());
                    }
                })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(MainActivity2.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
    }


    private void createPdf() {






        String srn = myTextView.getText().toString();
        PdfDocument document = new PdfDocument();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(1152, 1625, 1).create();
        PdfDocument.Page page = document.startPage(pageInfo);

        Canvas canvas = page.getCanvas();

        Paint paint = new Paint();
        paint.setColor(Color.WHITE);

        canvas.drawPaint(paint);
        paint.setTypeface(Typeface.create("Arial", Typeface.NORMAL));


        paint.setColor(Color.BLACK);
        paint.setTextSize(20);



        Rect bounds = new Rect();
        String[] lines = srn.split("\n");
        int x = 60;
        int y = 100;
        int j = 1;
        int yoff = 0;
        int i;
        for ( i = 0; i < lines.length; ++i) {
            canvas.drawText(lines[i], x, y + yoff, paint);
            paint.getTextBounds(lines[i], 0, lines[i].length(), bounds);
            yoff += bounds.height() + 20;
            j++;
            if(j==35)
            {
                document.finishPage(page);
                pageInfo = new PdfDocument.PageInfo.Builder(1152, 1625, 1).create();
                page = document.startPage(pageInfo);
                canvas = page.getCanvas();
                paint.setColor(Color.WHITE);
                canvas.drawPaint(paint);
                paint.setTypeface(Typeface.create("Arial", Typeface.NORMAL));
                paint.setColor(Color.BLACK);
                paint.setTextSize(20);
                bounds = new Rect();


                x = 60;
                 y = 100;
                 yoff = 0;
                 j=1;
            }

        }

        document.finishPage(page);
//
////
//        PdfDocument.PageInfo pageInfo2 = new PdfDocument.PageInfo.Builder(1152, 1625, 2).create();
//        PdfDocument.Page page2 = document.startPage(pageInfo2);
//
//        Canvas canvas2 = page2.getCanvas();
//
//        Paint paint2 = new Paint();
//        paint2.setColor(Color.WHITE);
//
//        canvas2.drawPaint(paint2);
//        paint2.setTypeface(Typeface.create("Arial", Typeface.NORMAL));
//
//
//        paint2.setColor(Color.BLACK);
//        paint2.setTextSize(20);
//
////        String srn = myTextView.getText().toString();
////
////        Rect bounds = new Rect();
////        String[] lines = srn.split("\n");
//         x = 60;
//         y = 100;
//
//         yoff = 0;
//        for (int j = 36; j < lines.length; ++j) {
//            canvas2.drawText(lines[j], x, y + yoff, paint2);
//            paint2.getTextBounds(lines[j], 0, lines[j].length(), bounds);
//            yoff += bounds.height() + 20;
////            if(i=40)
////            {
////                document.finishPage(page);
////            }
//
//        }
//
//
//        document.finishPage(page2);



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
//        imageView.setImageDrawable(getApplicationContext().getDrawable(R.drawable.ic_placeholder));

    }

    private void openGeneratedPDF(File filePath, Context context) {
        File file = filePath;
        if (file.exists()) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            Uri uri = FileProvider.getUriForFile(context, "com.example.wordbook", file);
            context.grantUriPermission(context.getPackageName(), uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setDataAndType(uri, "application/pdf");
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);




            try {
                startActivity(intent);
            } catch (ActivityNotFoundException e) {
                Toast.makeText(MainActivity2.this, "No Application available to view pdf", Toast.LENGTH_LONG).show();
            }
        }
    }

}