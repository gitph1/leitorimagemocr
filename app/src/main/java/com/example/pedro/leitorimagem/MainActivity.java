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
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.SparseArray;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.view.MenuItem;


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
    DatabaseHelper mDatabaseHelper;
    ImageView imageView;
    Button btnProcess, btnAdd;
    ImageButton btnView, takePictureButton, btnGallery;
    EditText txtResult;
    Uri file;
    Bitmap bitmop;
    Bitmap adjustedBitmap;
    private Toolbar mToolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        takePictureButton = (ImageButton) findViewById(R.id.button_image);
        btnGallery = (ImageButton) findViewById(R.id.button_gallery);
        imageView = (ImageView) findViewById(R.id.image_view);
        btnProcess = (Button) findViewById(R.id.button_process);
        btnAdd = (Button) findViewById(R.id.button_add);
        btnView = (ImageButton) findViewById(R.id.button_view);
        txtResult = (EditText) findViewById(R.id.textview_result);
        mDatabaseHelper = new DatabaseHelper(this);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            takePictureButton.setEnabled(false);
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
        }


        btnProcess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextRecognizer textRecognizer = new TextRecognizer.Builder(getApplicationContext()).build();
                if (!textRecognizer.isOperational()) {
                    Log.w("Error", "Detector dependencies are not yet available");

                } else {
                    Frame frame = new Frame.Builder().setBitmap(adjustedBitmap).build();
                    SparseArray<TextBlock> items = textRecognizer.detect(frame);
                    StringBuilder stringBuilder = new StringBuilder();
                    for (int i = 0; i < items.size(); i++) {
                        TextBlock item = items.valueAt(i);
                        stringBuilder.append(item.getValue());
                        stringBuilder.append("\n");
                    }
//                    txtResult.setText(stringBuilder.toString(), TextView.BufferType.EDITABLE);
//                    txtResult.setVisibility(View.VISIBLE);
//                    btnAdd.setVisibility(View.VISIBLE);
                }
            }
        });

//        btnAdd.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String newEntry = txtResult.getText().toString();
//                if (txtResult.length() != 0) {
//                    AddData(newEntry);
//                    txtResult.setText("");
//                } else {
//                    toastMessage("DETECTA ALGO CARA");
//                }
//
//            }
//        });
        //todo: apagar esses onclick  e deixar tudo direto

        btnView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ListDataActivity.class);
                startActivity(intent);
            }
        });

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

        if (id == R.id.action_camera) {
            takePicture(takePictureButton);
        }
        if (id == R.id.action_gallery) {
            pickGallery(btnGallery);
        }
        if (id == R.id.action_texts) {
            viewTexts();
        }


        return super.onOptionsItemSelected(item);
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 0) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                takePictureButton.setEnabled(true);
            }
        }
    }
    //todo: remover View view dos métodos
    public void takePicture(View view) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        file = Uri.fromFile(getOutputMediaFile());
        intent.putExtra(MediaStore.EXTRA_OUTPUT, file);
        startActivityForResult(intent, 100);

    }

    public void viewTexts() {
        Intent intent = new Intent(MainActivity.this, ListDataActivity.class);
        startActivity(intent);
    }

    public void detectText(View view){
        TextRecognizer textRecognizer = new TextRecognizer.Builder(getApplicationContext()).build();
        if (!textRecognizer.isOperational()) {
            Log.w("Error", "Detector dependencies are not yet available");

        } else {
            Frame frame = new Frame.Builder().setBitmap(adjustedBitmap).build();
            SparseArray<TextBlock> items = textRecognizer.detect(frame);
            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 0; i < items.size(); i++) {
                TextBlock item = items.valueAt(i);
                stringBuilder.append(item.getValue());
                stringBuilder.append("\n");
            }
                    txtResult.setText(stringBuilder.toString(), TextView.BufferType.EDITABLE);
                    saveText(btnAdd);
//                    txtResult.setVisibility(View.VISIBLE);
//                    btnAdd.setVisibility(View.VISIBLE);
        }
    }

    public void saveText(View view){
        String newEntry = txtResult.getText().toString();
        if (txtResult.length() != 0) {
            AddData(newEntry);
            txtResult.setText("");
        } else {
            toastMessage("DETECTA ALGO CARA");
        }
    }

    public void pickGallery(View view) {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, 377);
    }

    private static File getOutputMediaFile() {
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "CameraDemo");

        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("CameraDemo", "failed to create directory");
                return null;
            }
        }

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        return new File(mediaStorageDir.getPath() + File.separator +
                "IMG_" + timeStamp + ".jpg");
    }

    public void AddData(String newEntry) {
        int insertData = mDatabaseHelper.addData(newEntry);
        if (insertData != -1) {
            toastMessage("Your text has been saved!");
            Intent editScreenIntent = new Intent(MainActivity.this, EditDataActivity.class);
            editScreenIntent.putExtra("id",insertData);
            editScreenIntent.putExtra("name",newEntry);
            startActivity(editScreenIntent);
        } else {
            toastMessage("Something went wrong");
        }
    }

    /**
     * customizable toast
     * @param message
     */
    private void toastMessage(String message){
        Toast.makeText(this,message, Toast.LENGTH_SHORT).show();
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

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                try {
                    adjustedBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), resultUri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //imageView.setImageBitmap(adjustedBitmap);
                //todo: remover isso também
                detectText(btnProcess);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
        //todo: que porra é esse 29, apagar
        if (requestCode == 29) {
            if (data != null) {
                Bundle bundle = data.getExtras();
                if (bundle != null) {
                    adjustedBitmap = (Bitmap) bundle.get("data");
                    imageView.setImageBitmap(adjustedBitmap);
                }
            }
        }
    }



}
