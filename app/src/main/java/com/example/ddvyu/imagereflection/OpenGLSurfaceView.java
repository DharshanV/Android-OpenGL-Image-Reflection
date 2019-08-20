package com.example.ddvyu.imagereflection;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

public class OpenGLSurfaceView extends GLSurfaceView {
    private GLRenderer renderer;

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
}
