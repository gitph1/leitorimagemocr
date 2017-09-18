package com.example.pedro.leitorimagem;


import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.R.attr.bitmap;
import static android.R.attr.rotation;
import static android.R.attr.width;
import static com.example.pedro.leitorimagem.R.attr.height;

public class MainActivity extends AppCompatActivity {
    ImageView imageView;
    Button btnProcess;
    EditText txtResult;
    Button takePictureButton;
    Uri file;
    Bitmap bitmop;
    Bitmap adjustedBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        takePictureButton = (Button) findViewById(R.id.button_image);
        imageView = (ImageView)findViewById(R.id.image_view);
        btnProcess = (Button)findViewById(R.id.button_process);
        txtResult = (EditText)findViewById(R.id.textview_result);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            takePictureButton.setEnabled(false);
            ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE }, 0);
        }

        final Bitmap bitmap = BitmapFactory.decodeResource(
                getApplicationContext().getResources(),
                R.drawable.test_image
        );
        //imageView.setImageBitmap(bitmap);

        btnProcess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextRecognizer textRecognizer = new TextRecognizer.Builder(getApplicationContext()).build();
                if(!textRecognizer.isOperational()){
                    Log.w("Error", "Detector dependencies are not yet available");

                }else{
                    Frame frame = new Frame.Builder().setBitmap(adjustedBitmap).build();
                    SparseArray<TextBlock> items = textRecognizer.detect(frame);
                    StringBuilder stringBuilder = new StringBuilder();
                    for(int i = 0; i < items.size(); i++){
                        TextBlock item = items.valueAt(i);
                        stringBuilder.append(item.getValue());
                        stringBuilder.append("\n");
                    }
                    txtResult.setText(stringBuilder.toString(), TextView.BufferType.EDITABLE);
                    txtResult.setVisibility(View.VISIBLE);
                }
            }
        });

    }
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 0) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                takePictureButton.setEnabled(true);
            }
        }
    }

    public void takePicture(View view) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        file = Uri.fromFile(getOutputMediaFile());
        intent.putExtra(MediaStore.EXTRA_OUTPUT, file);
        startActivityForResult(intent, 100);

    }

    public void pickGallery(View view){
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, 377);
    }

    private static File getOutputMediaFile(){
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "CameraDemo");

        if (!mediaStorageDir.exists()){
            if (!mediaStorageDir.mkdirs()){
                Log.d("CameraDemo", "failed to create directory");
                return null;
            }
        }

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        return new File(mediaStorageDir.getPath() + File.separator +
                "IMG_"+ timeStamp + ".jpg");
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 100) {
            if (resultCode == RESULT_OK) {

                try {
                    bitmop = MediaStore.Images.Media.getBitmap(this.getContentResolver(), file);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {

                    CropImage.activity(file)
                            .setGuidelines(CropImageView.Guidelines.OFF)
                            .start(this);



                } catch (/*IO*/Exception e) {
                    e.printStackTrace();
                }


            }
        }
        if (requestCode == 377) {
            if (resultCode == RESULT_OK) {
                try {
                    final Uri imageUri = data.getData();
                    CropImage.activity(imageUri)
                            .setGuidelines(CropImageView.Guidelines.OFF)
                            .start(this);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else {
                //não pegou img
            }
        }

        if (requestCode == 367 && resultCode == Activity.RESULT_OK) {
            if (data == null) {
                //Display an error
                return;
            }
            try{
                InputStream inputStream = getApplicationContext().getContentResolver().openInputStream(data.getData());
            }catch(IOException e){
                e.printStackTrace();
            }

            //Now you can do whatever you want with your inpustream, save it as file, upload to a server, decode a bitmap...
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                try {
                    adjustedBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), resultUri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                imageView.setImageBitmap(adjustedBitmap);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
        if (requestCode == 29){
            if(data != null){
                Bundle bundle = data.getExtras();
                if(bundle != null){
                    adjustedBitmap = (Bitmap) bundle.get("data");
                    imageView.setImageBitmap(adjustedBitmap);
                }
            }
        }
    }
    private static int exifToDegrees(int exifOrientation) {
        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) { return 90; }
        else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {  return 180; }
        else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {  return 270; }
        return 0;
    }

}
