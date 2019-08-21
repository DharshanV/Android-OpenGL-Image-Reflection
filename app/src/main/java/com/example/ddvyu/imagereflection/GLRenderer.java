package com.example.ddvyu.imagereflection;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES30;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.opengl.Matrix;
import android.os.SystemClock;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class GLRenderer implements GLSurfaceView.Renderer {
    private static final String TAG = "GLRenderer";
    private AssetManager assets;

    private Shader shader;
    private Texture texture;
    private Shader skyboxShader;
    private int cubemapTexutre;
    
    private int WIDTH;
    private int HEIGHT;

    private float[] model = new float[4*4];
    private float[] view = new float[4*4];
    private float[] projection = new float[4*4];

    private int VAO[] = {0};
    private int VBO[] = {0};
    private int VAOSkybox[] = {0};
    private int VBOSkybox[] = {0};
    
    private float deltaTime;
    private float lastFrame;
    private float movementSpeed = 0.1f;
    private float zPosition = -3.0f;
    public float mAngle;
    private float rotateAngle;
    
    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        GLES30.glEnable(GLES30.GL_DEPTH_TEST);
        
        texture = new Texture(assets,"Textures/container.jpg");
        cubemapTexutre = loadCubeMap(Data.files);
        
        shader = new Shader(assets,"Shaders/vertexShader.vert","Shaders/fragmentShader.frag");
        skyboxShader = new Shader(assets,"Shaders/vertexSkybox.vert","Shaders/fragmentSkybox.frag");
        
        loadBox();
        loadSkybox();
        
        shader.use();
        shader.setInt("texture1",0);
        
        skyboxShader.use();
        skyboxShader.setInt("skybox",0);
    }
    
    @Override
    public void onDrawFrame(GL10 gl) {

        float time = (float)SystemClock.uptimeMillis() % 4000L;
        rotateAngle = 0.090f * time;
        deltaTime = time - lastFrame;
        lastFrame = time;

        GLES30.glClearColor(0.2f, 0.3f, 0.3f, 1.0f);
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT | GLES30.GL_DEPTH_BUFFER_BIT);
    
        Matrix.setIdentityM(model,0);
        Matrix.setIdentityM(view,0);
        Matrix.setIdentityM(projection,0);
        
        renderBoxes();
        renderSkybox();
    }
    
    private void renderBoxes(){
        Matrix.rotateM(model,0,rotateAngle,0.4f,0.6f,0.3f);
        Matrix.translateM(view,0,0,0,zPosition);
        Matrix.rotateM(view,0,mAngle,0,-1.0f,0);
        Matrix.perspectiveM(projection,0,45.0f,(float)WIDTH/HEIGHT,.1f,100f);
        renderBox();
        Matrix.setIdentityM(model,0);
        Matrix.translateM(model,0,0.8f,0.9f,0.8f);
        Matrix.scaleM(model,0,0.5f,0.5f,0.5f);
        renderBox();
    }
    
    private void renderSkybox(){
        GLES30.glDepthFunc(GLES30.GL_LEQUAL);
        skyboxShader.use();
        removeTranslation(view);
        skyboxShader.setMatrix4f("view", view);
        skyboxShader.setMatrix4f("projection", projection);
        GLES30.glBindVertexArray(VAOSkybox[0]);
        GLES30.glActiveTexture(GLES30.GL_TEXTURE0);
        GLES30.glBindTexture(GLES30.GL_TEXTURE_CUBE_MAP, cubemapTexutre);
        GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, 36);
        GLES30.glBindVertexArray(0);
        GLES30.glDepthFunc(GLES30.GL_LESS);
    }
    
    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES30.glViewport(0, 0, width, height);
        WIDTH = width;
        HEIGHT = height;
    }

    public void setAssets(AssetManager assets){
        this.assets = assets;
    }
    
    private void loadBox() {
        FloatBuffer verticesBuffer = null;
        verticesBuffer = verticesBuffer.wrap(Data.vertices);
        
        GLES30.glGenVertexArrays(1,VAO,0);
        GLES30.glGenBuffers(1,VBO,0);
        
        GLES30.glBindVertexArray(VAO[0]);
        
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER,VBO[0]);
        GLES30.glBufferData(GLES30.GL_ARRAY_BUFFER,sizeof(Data.vertices),verticesBuffer,GLES30.GL_STATIC_DRAW);
        
        GLES30.glEnableVertexAttribArray(0);
        GLES30.glVertexAttribPointer(0,3,GLES30.GL_FLOAT,false,5*4,0);
        GLES30.glEnableVertexAttribArray(1);
        GLES30.glVertexAttribPointer(1,2,GLES30.GL_FLOAT,false,5*4,3*4);
    }
    
    private void renderBox(){
        shader.use();
        texture.bind(0);
    
        shader.setMatrix4f("model",model);
        shader.setMatrix4f("view",view);
        shader.setMatrix4f("projection",projection);
    
        GLES30.glBindVertexArray(VAO[0]);
        GLES30.glDrawArrays(GLES30.GL_TRIANGLES,0,36);
        GLES30.glBindVertexArray(0);
    }
    
    private void loadSkybox(){
        FloatBuffer verticesBuffer = null;
        verticesBuffer = verticesBuffer.wrap(Data.skyboxVertices);
        
        GLES30.glGenVertexArrays(1,VAOSkybox,0);
        GLES30.glGenBuffers(1,VBOSkybox,0);
        
        GLES30.glBindVertexArray(VAOSkybox[0]);
        
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER,VBOSkybox[0]);
        GLES30.glBufferData(GLES30.GL_ARRAY_BUFFER,sizeof(Data.skyboxVertices),verticesBuffer,GLES30.GL_STATIC_DRAW);
    
        GLES30.glEnableVertexAttribArray(0);
        GLES30.glVertexAttribPointer(0,3,GLES30.GL_FLOAT,false,3*4,0);
    }
    
    private int loadCubeMap(List<String> facesPath){
        int[] textureID = {0};
        GLES30.glGenTextures(1,textureID,0);
        GLES30.glBindTexture(GLES30.GL_TEXTURE_CUBE_MAP,textureID[0]);
        
        for(int i=0;i<facesPath.size();i++){
            Bitmap bitmap = null;
            try{
                InputStream stream = assets.open(facesPath.get(i));
                bitmap = BitmapFactory.decodeStream(stream);
            } catch (IOException e){
                e.printStackTrace();
            }
            
            GLUtils.texImage2D(GLES30.GL_TEXTURE_CUBE_MAP_POSITIVE_X+i,0,bitmap,0);
            
            bitmap.recycle();
        }
    
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_CUBE_MAP, GLES30.GL_TEXTURE_MIN_FILTER, GLES30.GL_LINEAR);
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_CUBE_MAP, GLES30.GL_TEXTURE_MAG_FILTER, GLES30.GL_LINEAR);
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_CUBE_MAP, GLES30.GL_TEXTURE_WRAP_S, GLES30.GL_CLAMP_TO_EDGE);
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_CUBE_MAP, GLES30.GL_TEXTURE_WRAP_T,GLES30.GL_CLAMP_TO_EDGE);
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_CUBE_MAP, GLES30.GL_TEXTURE_WRAP_R, GLES30.GL_CLAMP_TO_EDGE);
        
        return textureID[0];
    }

    private static int sizeof(float[] data){
        return 4 * data.length;
    }

    private static int sizeof(int[] data){
        return 4 * data.length;
    }

    private void removeTranslation(float[] m){
        m[index(3,0)] = 0;
        m[index(3,1)] = 0;
        m[index(3,2)] = 0;
    }
    
    private void print(float[] matrix){
        String output = "\n";
        for (int i=0 ; i<4 ; i++) {
            for (int j=0 ; j<4 ; j++) {
                output += String.valueOf(matrix[index(i,j)]) + " ";
            }
            output+="\n";
        }
        Log.i(TAG,output);
    }
    
    private int index(int i,int j){
        return 4*i+j;
    }
    
    public void processMovement(Movement movement){
        if(movement == Movement.FORWARD){
            zPosition += (movementSpeed * deltaTime);
        }
        else if(movement == Movement.BACKWARD){
            zPosition -= (movementSpeed * deltaTime);
        }
    }
    
    public float getAngle() {
        return mAngle;
    }
    
    public void setAngle(float angle) {
        mAngle = angle;
    }
}

class Data{
    //        long time = SystemClock.uptimeMillis() % 4000L;
//        float angle = 0.090f * ((int) time);
//        angle /= 2;
//        skyboxShader.use();
//        skyboxShader.setMatrix4f("view",view);
//        skyboxShader.setMatrix4f("projection",projection);
//
//        GLES30.glBindVertexArray(VAOSkybox[0]);
//        GLES30.glActiveTexture(GLES30.GL_TEXTURE0);
//        GLES30.glBindTexture(GLES30.GL_TEXTURE_CUBE_MAP, cubemapTexutre);
//        GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, 36);
//
//        GLES30.glBindVertexArray(VAO[0]);
//        GLES30.glDrawArrays(GLES30.GL_TRIANGLES,0,36);
//        GLES30.glDepthFunc(GLES30.GL_LEQUAL);
//        removeTranslation(view);
//        GLES30.glBindVertexArray(VAOSkybox[0]);
//        GLES30.glActiveTexture(GLES30.GL_TEXTURE0);
//        GLES30.glBindTexture(GLES30.GL_TEXTURE_CUBE_MAP, cubemapTexutre);
//        GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, 36);
//        GLES30.glBindVertexArray(0);
//        GLES30.glDepthFunc(GLES30.GL_LESS);
    
    static float vertices[] = {
            // positions          // texture Coords
            -0.5f, -0.5f, -0.5f,  0.0f, 0.0f,
            0.5f, -0.5f, -0.5f,  1.0f, 0.0f,
            0.5f,  0.5f, -0.5f,  1.0f, 1.0f,
            0.5f,  0.5f, -0.5f,  1.0f, 1.0f,
            -0.5f,  0.5f, -0.5f,  0.0f, 1.0f,
            -0.5f, -0.5f, -0.5f,  0.0f, 0.0f,
        
            -0.5f, -0.5f,  0.5f,  0.0f, 0.0f,
            0.5f, -0.5f,  0.5f,  1.0f, 0.0f,
            0.5f,  0.5f,  0.5f,  1.0f, 1.0f,
            0.5f,  0.5f,  0.5f,  1.0f, 1.0f,
            -0.5f,  0.5f,  0.5f,  0.0f, 1.0f,
            -0.5f, -0.5f,  0.5f,  0.0f, 0.0f,
        
            -0.5f,  0.5f,  0.5f,  1.0f, 0.0f,
            -0.5f,  0.5f, -0.5f,  1.0f, 1.0f,
            -0.5f, -0.5f, -0.5f,  0.0f, 1.0f,
            -0.5f, -0.5f, -0.5f,  0.0f, 1.0f,
            -0.5f, -0.5f,  0.5f,  0.0f, 0.0f,
            -0.5f,  0.5f,  0.5f,  1.0f, 0.0f,
        
            0.5f,  0.5f,  0.5f,  1.0f, 0.0f,
            0.5f,  0.5f, -0.5f,  1.0f, 1.0f,
            0.5f, -0.5f, -0.5f,  0.0f, 1.0f,
            0.5f, -0.5f, -0.5f,  0.0f, 1.0f,
            0.5f, -0.5f,  0.5f,  0.0f, 0.0f,
            0.5f,  0.5f,  0.5f,  1.0f, 0.0f,
        
            -0.5f, -0.5f, -0.5f,  0.0f, 1.0f,
            0.5f, -0.5f, -0.5f,  1.0f, 1.0f,
            0.5f, -0.5f,  0.5f,  1.0f, 0.0f,
            0.5f, -0.5f,  0.5f,  1.0f, 0.0f,
            -0.5f, -0.5f,  0.5f,  0.0f, 0.0f,
            -0.5f, -0.5f, -0.5f,  0.0f, 1.0f,
        
            -0.5f,  0.5f, -0.5f,  0.0f, 1.0f,
            0.5f,  0.5f, -0.5f,  1.0f, 1.0f,
            0.5f,  0.5f,  0.5f,  1.0f, 0.0f,
            0.5f,  0.5f,  0.5f,  1.0f, 0.0f,
            -0.5f,  0.5f,  0.5f,  0.0f, 0.0f,
            -0.5f,  0.5f, -0.5f,  0.0f, 1.0f
    };
    
    static float skyboxVertices[] = {
            // positions
            -1.0f,  1.0f, -1.0f,
            -1.0f, -1.0f, -1.0f,
            1.0f, -1.0f, -1.0f,
            1.0f, -1.0f, -1.0f,
            1.0f,  1.0f, -1.0f,
            -1.0f,  1.0f, -1.0f,
            
            -1.0f, -1.0f,  1.0f,
            -1.0f, -1.0f, -1.0f,
            -1.0f,  1.0f, -1.0f,
            -1.0f,  1.0f, -1.0f,
            -1.0f,  1.0f,  1.0f,
            -1.0f, -1.0f,  1.0f,
            
            1.0f, -1.0f, -1.0f,
            1.0f, -1.0f,  1.0f,
            1.0f,  1.0f,  1.0f,
            1.0f,  1.0f,  1.0f,
            1.0f,  1.0f, -1.0f,
            1.0f, -1.0f, -1.0f,
            
            -1.0f, -1.0f,  1.0f,
            -1.0f,  1.0f,  1.0f,
            1.0f,  1.0f,  1.0f,
            1.0f,  1.0f,  1.0f,
            1.0f, -1.0f,  1.0f,
            -1.0f, -1.0f,  1.0f,
            
            -1.0f,  1.0f, -1.0f,
            1.0f,  1.0f, -1.0f,
            1.0f,  1.0f,  1.0f,
            1.0f,  1.0f,  1.0f,
            -1.0f,  1.0f,  1.0f,
            -1.0f,  1.0f, -1.0f,
            
            -1.0f, -1.0f, -1.0f,
            -1.0f, -1.0f,  1.0f,
            1.0f, -1.0f, -1.0f,
            1.0f, -1.0f, -1.0f,
            -1.0f, -1.0f,  1.0f,
            1.0f, -1.0f,  1.0f
    };
    
    static ArrayList<String> files = new ArrayList<String>() {
        {
            add("Textures/Skybox/right.jpg");
            add("Textures/Skybox/left.jpg");
            add("Textures/Skybox/top.jpg");
            add("Textures/Skybox/bottom.jpg");
            add("Textures/Skybox/front.jpg");
            add("Textures/Skybox/back.jpg");
        }
    };
}