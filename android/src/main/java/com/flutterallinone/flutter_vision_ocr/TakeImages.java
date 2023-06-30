package com.flutterallinone.flutter_vision_ocr;
import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.util.Log;
import android.util.SparseArray;
import android.widget.Toast;

import io.flutter.plugin.common.MethodChannel.Result;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

public class TakeImages extends AppCompatActivity{
    Result result;
    Activity activity;
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 100;
    private static final int GALLERY_PERMISSION_REQUEST_CODE = 101;
    private static final int REQUEST_IMAGE_CAPTURE = 100;
    boolean isCamera = true;
    TakeImages(Result result,Activity activity){
        this.result = result;
        this.activity = activity;
    }

    void takeImage( boolean isCamera){
        this.isCamera =isCamera;

        if(isCamera){
            if(ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);
            }else{
                openCamera();
            }
        }else{
            if(ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},GALLERY_PERMISSION_REQUEST_CODE);
            }else{
                openGallery();
            }
        }


    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Camera permission granted, proceed with camera access
                openCamera();
            } else {
                // Camera permission denied, handle accordingly (e.g., show a message or disable camera-related functionality)
                Toast.makeText(activity, "Camera permission denied", Toast.LENGTH_SHORT).show();
            }
        }

        if (requestCode ==GALLERY_PERMISSION_REQUEST_CODE) {

            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Camera permission granted, proceed with camera access
                openGallery();
            } else {
                // Camera permission denied, handle accordingly (e.g., show a message or disable camera-related functionality)
                Toast.makeText(activity, "Gallery permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void openGallery(){
        if (activity == null) {
            result.error("ACTIVITY_NOT_AVAILABLE", "Activity not available.", null);
            return;
        }
        Intent takePictureIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        if (takePictureIntent.resolveActivity(activity.getPackageManager()) != null) {
            result = result;
            activity.startActivityForResult(takePictureIntent, GALLERY_PERMISSION_REQUEST_CODE);
            Log.d("calling1222222222222222","Hellooooooooooooooooooooooooo");
        } else {
            result.error("NO_CAMERA_APP", "No camera app available.", null);
        }

    }
    private void openCamera(){
        if (activity == null) {
            result.error("ACTIVITY_NOT_AVAILABLE", "Activity not available.", null);
            return;
        }
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(activity.getPackageManager()) != null) {
            result = result;
            activity.startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            Log.d("calling1222222222222222","Hellooooooooooooooooooooooooo");
        } else {
            result.error("NO_CAMERA_APP", "No camera app available.", null);
        }

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//     super.onActivityResult(requestCode, resultCode, data);

      super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            Bitmap imageBitmap = (Bitmap) data.getExtras().get("data");

            TextRecognizer textRecognizer = new TextRecognizer.Builder(activity.getApplicationContext()).build();

            Frame frameImage = new Frame.Builder().setBitmap(imageBitmap).build();
            SparseArray<TextBlock> textBlockSparseArray = textRecognizer.detect(frameImage);

            String stringImageText = "";
            for (int i = 0; i < textBlockSparseArray.size(); i++) {
                TextBlock textBlock = textBlockSparseArray.get(textBlockSparseArray.keyAt(i));
                stringImageText = stringImageText + " " + textBlock.getValue();
            }

            Log.d("stringImageText", stringImageText);
            result.success(stringImageText);
        }



    }
}
