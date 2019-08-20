package com.example.ddvyu.imagereflection;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.io.IOException;

public class OpenGLActivity extends Activity {
    private OpenGLSurfaceView glSurfaceView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.opengl_activity);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        glSurfaceView = findViewById(R.id.openGLSurfaceView);
        glSurfaceView.setAssetManager(getAssets());
    }

    @Override
    protected void onResume(){
        super.onResume();
        glSurfaceView.onResume();
    }

    @Override
    protected void onPause(){
        super.onPause();
        glSurfaceView.onPause();
    }
}
