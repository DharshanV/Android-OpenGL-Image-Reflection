package com.example.ddvyu.imagereflection;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import java.util.Map;
import java.util.TreeMap;

public class OpenGLActivity extends Activity {
    private OpenGLSurfaceView glSurfaceView;
    private static final String TAG = "MAIN";
    private Map<Integer,Movement> movementMap;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.opengl_activity);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        glSurfaceView = findViewById(R.id.openGLSurfaceView);
        glSurfaceView.setAssetManager(getAssets());
        
        findViewById(R.id.forwardButton).setOnTouchListener(eventListener);
        findViewById(R.id.backwardButton).setOnTouchListener(eventListener);
        findViewById(R.id.leftButton).setOnTouchListener(eventListener);
        findViewById(R.id.rightButton).setOnTouchListener(eventListener);
        
        movementMap = new TreeMap<>();
        movementMap.put(R.id.forwardButton,Movement.FORWARD);
        movementMap.put(R.id.backwardButton,Movement.BACKWARD);
        movementMap.put(R.id.leftButton,Movement.LEFT);
        movementMap.put(R.id.rightButton,Movement.RIGHT);
    }
    
    RepeatListener eventListener = new RepeatListener(200, 50, new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            glSurfaceView.executeMovement(movementMap.get(v.getId()));
        }});

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
