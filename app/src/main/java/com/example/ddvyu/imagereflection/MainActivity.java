package com.example.ddvyu.imagereflection;

import android.Manifest;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

public class MainActivity extends AppCompatActivity {
    public static String TAG = "Dharshan";
    public static String RENDER_TYPE = "RENDERER";
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
    private int currentFace = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        requestPermission();

        if(permissionGranted){
            //showPreview();
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

    View.OnClickListener takePictureListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //showPreview();
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


    public void showPreview(){
        if(currentFace >= faces.length){
            currentFace = 0;
        }
        frameLayout = findViewById(faces[currentFace]);
        if(isCameraInUse()){
            camera.stopPreview();
            camera.release();
        }
        camera = Camera.open();
        showCamera = null;
        showCamera = new ShowCamera(frameLayout.getContext(),camera);
        frameLayout.addView(showCamera);
        currentFace++;
    }

    public void clearPreviews(){
        for(int i=0;i<faces.length;i++){
            frameLayout = findViewById(faces[i]);
            frameLayout.removeAllViews();
        }
        currentFace = 0;
        //showPreview();
    }

    public void startSampleRender(){
        Intent intent = new Intent(this,OpenGLActivity.class);
        intent.putExtra(RENDER_TYPE,true);
        startActivity(intent);
    }
    
    public void startRenderActivity(){
        Intent intent = new Intent(this,OpenGLActivity.class);
        intent.putExtra(RENDER_TYPE,false);
        startActivity(intent);
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case REQUEST_CAMERA_PERMISSION:{
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    permissionGranted = true;
                    //showPreview();
                }
                else{
                    finish();
                }
            }
        }
    }
}