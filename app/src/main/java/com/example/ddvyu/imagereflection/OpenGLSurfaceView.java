package com.example.ddvyu.imagereflection;

import android.content.Context;
import android.content.res.AssetManager;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import java.util.Map;

public class OpenGLSurfaceView extends GLSurfaceView {
    private GLRenderer renderer;
    private AssetManager manager;
    private static String TAG = "Surfaceview";
    private float lastX;
    private float lastY;
    private boolean firstRun;

    public OpenGLSurfaceView(Context context) {
        super(context);
        init();
    }

    public OpenGLSurfaceView(Context context, AttributeSet attrs) {
        super(context,attrs);
        init();
    }

    private void init(){
        setEGLContextClientVersion(2);
        renderer = new GLRenderer();
        setPreserveEGLContextOnPause(true);
        setRenderer(renderer);
        lastX = (float)this.getResources().getDisplayMetrics().widthPixels /2;
        lastY = (float)this.getResources().getDisplayMetrics().heightPixels /2;
    }

    public void setAssetManager(AssetManager manager){
        this.manager = manager;
        renderer.setAssets(manager);
    }
    
    public void executeMovement(Movement movement){
        renderer.processMovement(movement);
    }
    
    @Override
    public boolean onTouchEvent(MotionEvent e) {
        float x = e.getRawX();
        float y = e.getRawY();
        
        switch (e.getAction()) {
            case MotionEvent.ACTION_MOVE: {
                if(firstRun){
                    lastX = x;
                    lastY = y;
                    firstRun = false;
                }
    
                float xoffset = x - lastX;
                float yoffset = lastY - y;

                lastX = x;
                lastY = y;
                renderer.processTouch(xoffset,yoffset);
                break;
            }
            case MotionEvent.ACTION_UP:{
                firstRun = true;
                break;
            }
        }
        return true;
    }
}