package com.example.ddvyu.imagereflection;

import android.content.res.AssetManager;
import android.opengl.GLSurfaceView;

public abstract class MyRenderer implements GLSurfaceView.Renderer {
    public abstract void setAssets(AssetManager assetManager);
    public abstract void processMovement(Movement movement);
    public abstract void processTouch(float dx,float dy);
}
