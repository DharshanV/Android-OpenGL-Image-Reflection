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
    private final float TOUCH_SCALE_FACTOR = 180.0f / 320;
    private float previousX;
    private float previousY;
    
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
        previousX = (float)this.getResources().getDisplayMetrics().widthPixels /2;
        previousX = (float)this.getResources().getDisplayMetrics().heightPixels /2;
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
        float x = e.getX();
        float y = e.getY();
        
        switch (e.getAction()) {
            case MotionEvent.ACTION_MOVE: {
    
                float dx = x - previousX;
                float dy = y - previousY;
                
                //renderer.processTouch(dx,dy);
            }
        }
        
        previousX = x;
        previousY = y;
        return true;
    }
}