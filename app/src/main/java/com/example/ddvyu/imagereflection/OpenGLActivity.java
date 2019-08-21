package com.example.ddvyu.imagereflection;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class OpenGLActivity extends Activity {
    private OpenGLSurfaceView glSurfaceView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.opengl_activity);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        glSurfaceView = findViewById(R.id.openGLSurfaceView);
        glSurfaceView.setAssetManager(getAssets());
        
        findViewById(R.id.forwardButton).setOnClickListener(eventListener);
        findViewById(R.id.backwardButton).setOnClickListener(eventListener);
    }
    
    View.OnClickListener eventListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.forwardButton:{
                    glSurfaceView.executeMovement(Movement.FORWARD);
                    break;
                }
                case R.id.backwardButton:{
                    glSurfaceView.executeMovement(Movement.BACKWARD);
                    break;
                }
            }
        }
    };

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
