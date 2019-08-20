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
import java.io.FileReader;
import java.io.IOException;

public class Shader extends Application {
    private static String TAG = "Shader";
    private int programID;
    private static Context context;

    public Shader(final String vertexPath,final String fragmentPath){
        listAssetFiles("");
        try{
            String[] vertexCode = Resources.getSystem().getAssets().getLocales();
            Log.i(TAG,vertexCode[0]);
            String fragmentCode = Resources.getSystem().getAssets().open(fragmentPath).toString();
//            int vertex = loadShader(GLES30.GL_VERTEX_SHADER,vertexCode);
//            int fragment = loadShader(GLES30.GL_FRAGMENT_SHADER,fragmentCode);
//            programID = loadProgram(vertex,fragment);
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
    }

    private boolean listAssetFiles(String path) {

        String [] list;
        try {
            list = getAssets().list(path);
            if (list.length > 0) {
                for (String file : list) {
                    if (!listAssetFiles(path + "/" + file))
                        return false;
                    else {
                        Log.i("Main",file);
                    }
                }
            }
        } catch (IOException e) {
            return false;
        }

        return true;
    }

    public void use(){
        GLES30.glUseProgram(programID);
    }

    @NonNull
    private String getCode(final String path) throws IOException{
        BufferedReader br = new BufferedReader(new FileReader(path));
        try {
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            while (line != null) {
                sb.append(line);
                sb.append("\n");
                line = br.readLine();
            }
            return sb.toString();
        } finally {
            br.close();
        }
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
