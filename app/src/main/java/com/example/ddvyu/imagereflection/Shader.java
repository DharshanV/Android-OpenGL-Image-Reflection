package com.example.ddvyu.imagereflection;

import android.app.Application;
import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.opengl.GLES20;
import android.opengl.GLES30;
import android.support.annotation.NonNull;
import android.util.Log;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;

public class Shader extends Application {
    private static String TAG = "Shader";
    private int programID;

    public Shader(AssetManager assets,final String vertexPath,final String fragmentPath){
        try{
            String vertexCode = getCode(assets.open(vertexPath));
            String fragmentCode = getCode(assets.open(fragmentPath));
            int vertex = loadShader(GLES30.GL_VERTEX_SHADER,vertexCode);
            int fragment = loadShader(GLES30.GL_FRAGMENT_SHADER,fragmentCode);
            programID = loadProgram(vertex,fragment);
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public void use(){
        GLES30.glUseProgram(programID);
    }

    public static String getCode(InputStream inputStream) throws IOException {
        ByteArrayOutputStream into = new ByteArrayOutputStream();
        byte[] buf = new byte[4096];
        for (int n; 0 < (n = inputStream.read(buf));) {
            into.write(buf, 0, n);
        }
        into.close();
        return new String(into.toByteArray(), "UTF-8"); // Or whatever encoding
    }

    private int loadShader(int type,String source){
        int success[] = {0};
        int id = GLES20.glCreateShader(type);
        GLES30.glShaderSource(id,source);
        GLES30.glCompileShader(id);
        GLES30.glGetShaderiv(id,GLES30.GL_COMPILE_STATUS,success,0);
        if(success[0] == 0){
            Log.e(TAG,GLES30.glGetShaderInfoLog(id));
        }
        return id;
    }

    private int loadProgram(int vertexID,int fragmentID){
        int success[] = {0};
        int id = GLES20.glCreateProgram();
        GLES20.glAttachShader(id,vertexID);
        GLES20.glAttachShader(id,fragmentID);
        GLES20.glLinkProgram(id);
        GLES30.glGetShaderiv(id,GLES30.GL_LINK_STATUS,success,0);
        if(success[0] == 0){
            Log.e(TAG,GLES30.glGetProgramInfoLog(id));
        }
        GLES30.glDeleteShader(vertexID);
        GLES30.glDeleteShader(fragmentID);
        return id;
    }

}
