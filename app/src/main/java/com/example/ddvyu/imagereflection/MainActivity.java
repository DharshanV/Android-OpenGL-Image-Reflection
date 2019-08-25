package com.example.ddvyu.imagereflection;

import android.Manifest;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    public static String TAG = "Main";
    public static String SAMPLE_RENDER_TYPE = "RENDERER";
    Camera camera = null;
    FrameLayout frameLayout;
    ShowCamera showCamera;
    Button takePictureButton;
    Button clearButton;
    Button renderButton;
    Button sampleRenderButton;

    private static final int REQUEST_CAMERA_PERMISSION = 200;
    private boolean permissionGranted = false;
    private int faces[] = {R.id.leftImageView,R.id.frontImageView,
                            R.id.topImageView,R.id.bottomImageView,
                            R.id.rightImageView,R.id.backImageView};
    private List<byte[]> cubeMapData = new ArrayList<>();
    private List<String> savedImagePath = new ArrayList<>();
    
    private int currentFace = 0;
    private boolean firstRun = true;
    private boolean resumed = false;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        requestPermission();

        if(permissionGranted){
            showPreview();
        }

        takePictureButton = findViewById(R.id.takePictureButton);
        takePictureButton.setOnClickListener(takePictureListener);

        renderButton = findViewById(R.id.renderButton);
        renderButton.setOnClickListener(renderButtonListener);

        clearButton = findViewById(R.id.clearButton);
        clearButton.setOnClickListener(clearButtonListener);
    
        sampleRenderButton = findViewById(R.id.sampleRenderButton);
        sampleRenderButton.setOnClickListener(sampleButtonListener);
    }
    
    @Override
    protected void onPostResume() {
        super.onPostResume();
    }
    
    @Override
    protected void onDestroy() {
        camera.stopPreview();
        camera.release();
        super.onDestroy();
    }
    
    View.OnClickListener takePictureListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(camera == null)
                return;
            camera.takePicture(null,null,pictureTaken);
        }
    };

    View.OnClickListener renderButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            startRenderActivity();
        }
    };


    View.OnClickListener clearButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            clearPreviews();
        }
    };
    
    View.OnClickListener sampleButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            startSampleRender();
        }
    };
    
    private Camera.PictureCallback pictureTaken = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            camera.stopPreview();
            if(cubeMapData.size()<=faces.length){
                cubeMapData.add(data);
                showPreview();
            }
        }
    };
    
    public void showPreview(){
        if(currentFace>=faces.length){
            return;
        }
        if(firstRun){
            camera = Camera.open();
            Camera.Parameters parameters = camera.getParameters();
            List<Camera.Size> temp = parameters.getSupportedPictureSizes();
            Camera.Size size = temp.get(temp.size()-2);
            parameters.setPictureSize(size.width,size.height);
            camera.setParameters(parameters);
            firstRun = false;
        }
        frameLayout = findViewById(faces[currentFace]);
        showCamera = new ShowCamera(frameLayout.getContext(),camera);
        frameLayout.addView(showCamera);
        currentFace++;
    }

    public void clearPreviews(){
        cubeMapData.clear();
        camera.stopPreview();
        for(int i=0;i<faces.length;i++){
            frameLayout = findViewById(faces[i]);
            frameLayout.removeAllViews();
        }
        currentFace = 0;
        firstRun = true;
        showPreview();
    }

    public void startSampleRender(){
        Intent intent = new Intent(this,OpenGLActivity.class);
        intent.putExtra(SAMPLE_RENDER_TYPE,true);
        startActivity(intent);
    }
    
    public void startRenderActivity(){
        Intent intent = new Intent(this,OpenGLActivity.class);
        if(cubeMapData.size()==faces.length){
            intent.putExtra("count",cubeMapData.size());
            intent.putExtra("path",Environment.getExternalStorageDirectory().toString());
            saveCubemaps();
            camera.stopPreview();
            startActivity(intent);
        }
        else{
            Log.i(TAG,"INVALID CUBEMAP COUNT");
        }
    }

    public boolean isCameraInUse() {
        try {
            camera = Camera.open();
        } catch (RuntimeException e) {
            return true;
        }
        return false;
    }

    public void requestPermission(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CAMERA_PERMISSION);
            return;
        }
        else{
            permissionGranted = true;
        }
    }

    public void saveCubemaps(){
        for(int i=0;i<cubeMapData.size();i++){
            saveImage(processImage(cubeMapData.get(i)),String.valueOf(i));
            byte[] temp = cubeMapData.get(i);
            temp = null;
        }
    }
    
    private Bitmap processImage(byte[] data) {
        // Determine the width/height of the image
        int width = camera.getParameters().getPictureSize().width;
        int height = camera.getParameters().getPictureSize().height;
        
        // Load the bitmap from the byte array
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length, options);
        
        // Rotate and crop the image into a square
        int croppedWidth = (width > height) ? height : width;
        int croppedHeight = (width > height) ? height : width;
        
        Matrix matrix = new Matrix();
        Bitmap cropped = Bitmap.createBitmap(bitmap, 0, 0, croppedWidth, croppedHeight, matrix, true);
        bitmap.recycle();
        
        // Scale down to the output size
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(cropped, 500, 500, true);
        cropped.recycle();
        
        return scaledBitmap;
    }
    
    private void saveImage(Bitmap finalBitmap, String image_name) {
        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root);
        myDir.mkdirs();
        String fname = "image" + image_name+ ".jpg";
        File file = new File(myDir, fname);
        if (file.exists()) file.delete();
        file.deleteOnExit();
        Log.i("LOAD", root + fname);
        try {
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void deleteImage(String image_name){
        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root);
        myDir.mkdirs();
        String fname = "image" + image_name+ ".jpg";
        File file = new File(myDir, fname);
        file.delete();
    }
    
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case REQUEST_CAMERA_PERMISSION:{
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    permissionGranted = true;
                    showPreview();
                }
                else{
                    finish();
                }
            }
        }
    }
}