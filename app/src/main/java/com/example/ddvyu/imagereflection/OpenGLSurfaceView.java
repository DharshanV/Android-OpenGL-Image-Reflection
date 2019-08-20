package com.example.ddvyu.imagereflection;

import android.content.Context;
import android.content.res.AssetManager;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class OpenGLSurfaceView extends GLSurfaceView {
    private GLRenderer renderer;
    private AssetManager manager;

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
    }

    public void setAssetManager(AssetManager manager){
        this.manager = manager;
        renderer.setAssets(manager);
    }
}
