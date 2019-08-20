package com.example.ddvyu.imagereflection;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES30;
import android.opengl.GLUtils;

import java.io.IOException;
import java.io.InputStream;

public class Texture {
    private int textureID[] = {0};

    public Texture(AssetManager asset,final String path){
        GLES30.glGenTextures(1,textureID,0);
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D,textureID[0]);

        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_S, GLES30.GL_REPEAT);	// set texture wrapping to GL_REPEAT (default wrapping method)
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_T, GLES30.GL_REPEAT);
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MIN_FILTER, GLES30.GL_LINEAR);
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MAG_FILTER, GLES30.GL_LINEAR);

        Bitmap data = getTextureData(asset,path);
        GLUtils.texImage2D(GLES30.GL_TEXTURE_2D,0,data,0);
        data.recycle();

        unbind();
    }

    public void bind(int slot){
        GLES30.glActiveTexture(GLES30.GL_TEXTURE0+slot);
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D,textureID[0]);
    }

    public void unbind(){
        GLES30.glActiveTexture(0);
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D,0);
    }

    private Bitmap getTextureData(AssetManager asset,final String path){
        Bitmap data = null;
        try{
            InputStream stream = asset.open(path);
            data =  BitmapFactory.decodeStream(stream);

        }catch (IOException e){
            e.printStackTrace();
        }

        return data;
    }
}
