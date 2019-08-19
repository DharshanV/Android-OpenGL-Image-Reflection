package com.example.ddvyu.imagereflection;

import android.content.Context;
import android.content.res.Configuration;
import android.hardware.Camera;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;

public class ShowCamera extends SurfaceView implements SurfaceHolder.Callback {
    Camera camera;
    SurfaceHolder holder;
    public ShowCamera(Context context,Camera camera){
        super(context);
        this.camera = camera;
        holder = this.getHolder();
        holder.addCallback(this);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Camera.Parameters parameters = camera.getParameters();

        if(this.getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE){
            parameters.set("orientation","portrait");
            camera.setDisplayOrientation(90);
            parameters.setRotation(90);
        }
        else{
            parameters.set("orientation","landscape");
            camera.setDisplayOrientation(0);
            parameters.setRotation(0);
        }
        parameters.setSceneMode(Camera.Parameters.SCENE_MODE_HDR);
        parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
        camera.setParameters(parameters);
        try {
            camera.setPreviewDisplay(holder);
            camera.startPreview();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        try {
            camera.setPreviewDisplay(holder);
            camera.startPreview();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        camera.stopPreview();
        camera.release();
    }
}
