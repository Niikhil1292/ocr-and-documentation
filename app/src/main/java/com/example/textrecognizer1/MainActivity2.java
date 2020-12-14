//package com.example.textrecognizer1;
//
//import android.content.Context;
//import android.graphics.Canvas;
//import android.os.Environment;
//
//import androidx.appcompat.app.AppCompatActivity;
//
//import android.util.Log;
//import android.graphics.Bitmap;
//import android.os.Bundle;
//import android.view.WindowManager;
//import android.widget.Button;
//import android.widget.ImageView;
//import android.content.Intent;
//import android.widget.LinearLayout;
//import android.widget.TextView;
//import android.widget.Toast;
//import android.view.View;
//import android.net.Uri;
//
//import androidx.annotation.NonNull;
//import java.io.File;
//import java.io.FileNotFoundException;
//import java.io.FileOutputStream;
//import java.io.OutputStream;
//import java.text.SimpleDateFormat;
//import java.util.Date;
//
//import com.google.firebase.ml.vision.common.FirebaseVisionImage;
//import com.google.firebase.ml.vision.text.FirebaseVisionText;
//import com.google.firebase.ml.vision.FirebaseVision;
//import com.google.android.gms.tasks.OnSuccessListener;
//import com.google.android.gms.tasks.OnFailureListener;
//import com.lowagie.text.Document;
//import com.lowagie.text.DocumentException;
//import com.lowagie.text.Element;
//import com.lowagie.text.PageSize;
//import com.lowagie.text.pdf.PdfWriter;
//
//public class MainActivity extends BaseActivity implements View.OnClickListener {
//
//    private Bitmap myBitmap;
//    private ImageView myImageView;
//    private TextView myTextView;
//    private Button btn;
//    private LinearLayout llPdf;
//
//
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//        btn = findViewById(R.id.button);
//        llPdf = findViewById(R.id.llpdf);
//        myTextView = findViewById(R.id.textView);
//        myImageView = findViewById(R.id.imageView);
//        findViewById(R.id.checkText).setOnClickListener(this);
//        btn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Log.d("size"," "+llPdf.getWidth() +"  "+llPdf.getWidth());
//                myBitmap = loadBitmapFromView(llPdf, llPdf.getWidth(), llPdf.getHeight());
//
//          //      int hasWriteExternalStoragePermission = ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
//            //    if (hasWriteExternalStoragePermission == PackageManager.PERMISSION_GRANTED) {
//
////...call selectPicture, which launches an Activity where the user can select an image//
//
//                    createPdf();
//
////If permission hasn’t been granted, then...//
//
//              //  }
//
//
//
//
//            }
//        });
//
//    }
//    public static Bitmap loadBitmapFromView(View v, int width, int height) {
//        Bitmap b = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
//        Canvas c = new Canvas(b);
//        v.draw(c);
//
//        return b;
//    }
//    private void createPdf()throws FileNotFoundException, DocumentException {
//
//
////        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
////        //  Display display = wm.getDefaultDisplay();
////        DisplayMetrics displaymetrics = new DisplayMetrics();
////        this.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
////       float hight = displaymetrics.heightPixels ;
////        float width = displaymetrics.widthPixels ;
////        float hight = displaymetrics.heightPixels ;
////     float width = displaymetrics.widthPixels ;
//
//
//        int convertHighet = (int) hight, convertWidth = (int) width;
//
//        Resources mResources = getResources();
//        Bitmap bitmap = BitmapFactory.decodeResource(mResources, R.drawable.screenshot);
//
//        PdfDocument document = new PdfDocument();
//        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(convertWidth, convertHighet, 1).create();
//        PdfDocument.Page page = document.startPage(pageInfo);
//
//        Canvas canvas = page.getCanvas();
//
//        Paint paint = new Paint();
//        paint.setColor(Color.WHITE);
//
//        canvas.drawPaint(paint);
//
//        myBitmap = Bitmap.createScaledBitmap(myBitmap, convertWidth, convertHighet, true);
//
//        paint.setColor(Color.BLUE);
//        paint.setTextSize(150);
//
//       canvas.drawBitmap(myBitmap, 0, 0 , null);
//
//        canvas.drawText(myTextView.getText().toString(),0,0,paint);
//
//        document.finishPage(page);
//
//        // write the document content
//        Context context = getApplicationContext();
//
//        File filePath=new File(context.getExternalFilesDir("pdf"), "external_files");;
//
//        try {
//           filePath =  new File(context.getFilesDir(), "docs");
//            document.writeTo(new FileOutputStream(filePath));
//
//        } catch (IOException e) {
//            e.printStackTrace();
//            Toast.makeText(this, "Something wrong: " + e.toString(), Toast.LENGTH_LONG).show();
//        }
//
//        // close the document
//        document.close();
//        Toast.makeText(this, "PDF is created!!!", Toast.LENGTH_SHORT).show();
//
//        openGeneratedPDF(filePath,context);
//
//
//
//        File pdfFolder = new File(Environment.getExternalStoragePublicDirectory(
//                Environment.DIRECTORY_DOCUMENTS), "pdfdemo");
//        if (!pdfFolder.exists()) {
//            pdfFolder.mkdir();
//            Log.i(LOG_TAG, "Pdf Directory created");
//        }
//
//        //Create time stamp
//        Date date = new Date() ;
//        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(date);
//
//        File myFile = new File(pdfFolder + timeStamp + ".pdf");
//
//        OutputStream output = new FileOutputStream(myFile);
//
//        Document document = new Document(PageSize.LETTER);
//
//        PdfWriter.getInstance(document, output);
//
//        document.open();
//
//        document.add((Element) new FirebaseVisionCloudText.Paragraph(myTextView.getText().toString()));
//
//
//        document.close();
//
//        viewPdf();
//      // openGeneratedPDF(filePath,context);
//
//    }
//    private void openGeneratedPDF(File filePath, Context context){
//        File file = filePath;
//        if (file.exists())
//        {
//            Intent intent=new Intent(Intent.ACTION_VIEW);
//            Uri uri = FileProvider.getUriForFile(context, "com.example.textrecognizer1",file);
//            context.grantUriPermission(context.getPackageName(), uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
//            intent.setDataAndType(uri, "application/pdf");
//            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//
//            try
//            {
//                startActivity(intent);
//            }
//            catch(ActivityNotFoundException e)
//            {
//                Toast.makeText(MainActivity.this, "No Application available to view pdf", Toast.LENGTH_LONG).show();
//            }
//        }
//    }
//
////    private void viewPdf(){
////        Intent intent = new Intent(Intent.ACTION_VIEW);
////        intent.setDataAndType(Uri.fromFile(myFile), "application/pdf");
////        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
////        startActivity(intent);
//    }
//    @Override
//    public void onClick(View view) {
//        switch (view.getId()) {
//            case R.id.checkText:
//                if (myBitmap != null) {
//                    runTextRecog();
//                }
//                break;
//
//        }
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (resultCode == RESULT_OK) {
//            switch (requestCode) {
//                case WRITE_STORAGE:
//                    checkPermission(requestCode);
//                    break;
//                case SELECT_PHOTO:
//                    Uri dataUri = data.getData();
//                    String path = MyHelper.getPath(this, dataUri);
//                    if (path == null) {
//                        myBitmap = MyHelper.resizePhoto(photo, this, dataUri, myImageView);
//                    } else {
//                        myBitmap = MyHelper.resizePhoto(photo, path, myImageView);
//                    }
//                    if (myBitmap != null) {
//                        myTextView.setText(null);
//                        myImageView.setImageBitmap(myBitmap);
//                    }
//                    break;
//
//            }
//        }
//    }
//
//    private void runTextRecog() {
//        FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(myBitmap);
//        FirebaseVisionTextDetector detector = FirebaseVision.getInstance().getVisionTextDetector();
//        detector.detectInImage(image).addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
//            @Override
//            public void onSuccess(FirebaseVisionText texts) {
//                processExtractedText(texts);
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure
//                    (@NonNull Exception exception) {
//                Toast.makeText(MainActivity.this,
//                        "Exception", Toast.LENGTH_LONG).show();
//            }
//        });
//    }
//
//    private void processExtractedText(FirebaseVisionText firebaseVisionText) {
//        myTextView.setText(null);
//        if (firebaseVisionText.getBlocks().size() == 0) {
//            myTextView.setText(R.string.no_text);
//            return;
//        }
//        for (FirebaseVisionText.Block block : firebaseVisionText.getBlocks()) {
//            myTextView.append(block.getText());
//
//        }
//    }
//
//}