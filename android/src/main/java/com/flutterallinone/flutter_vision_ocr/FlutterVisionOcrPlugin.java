package com.flutterallinone.flutter_vision_ocr;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Log;
import android.util.SparseArray;

import androidx.annotation.NonNull;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.embedding.engine.plugins.activity.ActivityAware;
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;

/** FlutterVisionOcrPlugin */
public class FlutterVisionOcrPlugin implements FlutterPlugin, MethodCallHandler, ActivityAware, PreferenceManager.OnActivityResultListener {
  private Result pendingResult;
  private Activity activity;
  private MethodChannel channel;
  private static final int REQUEST_IMAGE_CAPTURE = 100;
  private static final int GALLERY_PERMISSION_REQUEST_CODE = 101;
  @Override
  public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
    channel = new MethodChannel(flutterPluginBinding.getBinaryMessenger(), "flutter_vision_ocr");
    channel.setMethodCallHandler(this);
  }

  @Override
  public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {

    TakeImages takeImages = new TakeImages(result,activity);
    pendingResult = result;
    if (call.method.equals("getPlatformVersion")) {
      result.success("Android " + android.os.Build.VERSION.RELEASE);
    }else if (call.method.equals("cameraImage")) {
      takeImages.takeImage(true);
    }else if (call.method.equals("galleryImage")) {
      takeImages.takeImage(false);
    } else {
      result.notImplemented();
    }
  }

  @Override
  public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
    channel.setMethodCallHandler(null);
  }


  @Override
  public void onAttachedToActivity(@NonNull ActivityPluginBinding binding) {
    activity = binding.getActivity();
    binding.addActivityResultListener(this::onActivityResult);
  }

  @Override
  public void onDetachedFromActivityForConfigChanges() {

  }

  @Override
  public void onReattachedToActivityForConfigChanges(@NonNull ActivityPluginBinding binding) {
    activity = binding.getActivity();
    binding.addActivityResultListener(this::onActivityResult);
  }

  @Override
  public void onDetachedFromActivity() {

  }
  private Bitmap getBitmapFromUri(Uri uri) {
    try {
      // Decode the image URI to a bitmap
      return BitmapFactory.decodeStream(activity.getContentResolver().openInputStream(uri));
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  @Override
  public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
    Log.d("onActivityResult", "onActivityResult: "+requestCode);
    if ( requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
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
      pendingResult.success(stringImageText);
    }
    if (requestCode==GALLERY_PERMISSION_REQUEST_CODE) {

      Uri selectedImageUri = data.getData();
      Bitmap imageBitmap = getBitmapFromUri(selectedImageUri);

    //  Bitmap imageBitmap = (Bitmap) data.getExtras().get("data");

      TextRecognizer textRecognizer = new TextRecognizer.Builder(activity.getApplicationContext()).build();

      Frame frameImage = new Frame.Builder().setBitmap(imageBitmap).build();
      SparseArray<TextBlock> textBlockSparseArray = textRecognizer.detect(frameImage);

      String stringImageText = "";
      for (int i = 0; i < textBlockSparseArray.size(); i++) {
        TextBlock textBlock = textBlockSparseArray.get(textBlockSparseArray.keyAt(i));
        stringImageText = stringImageText + " " + textBlock.getValue();
      }

      Log.d("stringImageText", stringImageText);
      pendingResult.success(stringImageText);
    }


    return false;
  }
}
