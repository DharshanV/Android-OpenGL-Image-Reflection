package com.example.ddvyu.imagereflection;

import android.content.res.AssetManager;
import android.opengl.GLES30;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.SystemClock;
import android.util.Log;

import java.nio.FloatBuffer;
import java.util.Arrays;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class GLRenderer implements GLSurfaceView.Renderer {
    private static final String TAG = "GLRenderer";
    private AssetManager assets;

    private Shader shader;
    private FloatBuffer verticesBuffer;

    private float vertices[] = {
            // positions          // normals
            -0.5f, -0.5f, -0.5f,  0.0f,  0.0f, 1.0f,
            0.5f, -0.5f, -0.5f,  0.0f,  0.0f, 1.0f,
            0.5f,  0.5f, -0.5f,  0.0f,  0.0f, 1.0f,
            0.5f,  0.5f, -0.5f,  0.0f,  0.0f, 1.0f,
            -0.5f,  0.5f, -0.5f,  0.0f,  0.0f, 1.0f,
            -0.5f, -0.5f, -0.5f,  0.0f,  0.0f, 1.0f,

            -0.5f, -0.5f,  0.5f,  0.0f,  0.0f, 1.0f,
            0.5f, -0.5f,  0.5f,  0.0f,  0.0f, 1.0f,
            0.5f,  0.5f,  0.5f,  0.0f,  0.0f, 1.0f,
            0.5f,  0.5f,  0.5f,  0.0f,  0.0f, 1.0f,
            -0.5f,  0.5f,  0.5f,  0.0f,  0.0f, 1.0f,
            -0.5f, -0.5f,  0.5f,  0.0f,  0.0f, 1.0f,

            -0.5f,  0.5f,  0.5f, 1.0f,  0.0f,  0.0f,
            -0.5f,  0.5f, -0.5f, 1.0f,  0.0f,  0.0f,
            -0.5f, -0.5f, -0.5f, 1.0f,  0.0f,  0.0f,
            -0.5f, -0.5f, -0.5f, 1.0f,  0.0f,  0.0f,
            -0.5f, -0.5f,  0.5f, 1.0f,  0.0f,  0.0f,
            -0.5f,  0.5f,  0.5f, 1.0f,  0.0f,  0.0f,

            0.5f,  0.5f,  0.5f,  1.0f,  0.0f,  0.0f,
            0.5f,  0.5f, -0.5f,  1.0f,  0.0f,  0.0f,
            0.5f, -0.5f, -0.5f,  1.0f,  0.0f,  0.0f,
            0.5f, -0.5f, -0.5f,  1.0f,  0.0f,  0.0f,
            0.5f, -0.5f,  0.5f,  1.0f,  0.0f,  0.0f,
            0.5f,  0.5f,  0.5f,  1.0f,  0.0f,  0.0f,

            -0.5f, -0.5f, -0.5f,  0.0f, 1.0f,  0.0f,
            0.5f, -0.5f, -0.5f,  0.0f, 1.0f,  0.0f,
            0.5f, -0.5f,  0.5f,  0.0f, 1.0f,  0.0f,
            0.5f, -0.5f,  0.5f,  0.0f, 1.0f,  0.0f,
            -0.5f, -0.5f,  0.5f,  0.0f, 1.0f,  0.0f,
            -0.5f, -0.5f, -0.5f,  0.0f, 1.0f,  0.0f,

            -0.5f,  0.5f, -0.5f,  0.0f,  1.0f,  0.0f,
            0.5f,  0.5f, -0.5f,  0.0f,  1.0f,  0.0f,
            0.5f,  0.5f,  0.5f,  0.0f,  1.0f,  0.0f,
            0.5f,  0.5f,  0.5f,  0.0f,  1.0f,  0.0f,
            -0.5f,  0.5f,  0.5f,  0.0f,  1.0f,  0.0f,
            -0.5f,  0.5f, -0.5f,  0.0f,  1.0f,  0.0f
    };

    private int WIDTH;
    private int HEIGHT;

    private float[] model = new float[4*4];
    private float[] view = new float[4*4];
    private float[] projection = new float[4*4];

    private int VAO[] = {0};
    private int VBO[] = {0};

    public GLRenderer(){
    }

    public void setAssets(AssetManager assets){
        this.assets = assets;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        GLES30.glClearColor(0.2f, 0.3f, 0.3f, 1.0f);
        GLES30.glEnable(GLES30.GL_DEPTH_TEST);
        shader = new Shader(assets,"Shaders/vertexShader.txt","Shaders/fragmentShader.txt");

        verticesBuffer = verticesBuffer.wrap(vertices);

        GLES30.glGenVertexArrays(1,VAO,0);
        GLES30.glBindVertexArray(VAO[0]);

        GLES30.glGenBuffers(1,VBO,0);
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER,VBO[0]);
        GLES30.glBufferData(GLES30.GL_ARRAY_BUFFER,sizeof(vertices),verticesBuffer,GLES30.GL_STATIC_DRAW);

        GLES30.glEnableVertexAttribArray(0);
        GLES30.glVertexAttribPointer(0,3,GLES30.GL_FLOAT,false,6*4,0);
        GLES30.glEnableVertexAttribArray(1);
        GLES30.glVertexAttribPointer(1,3,GLES30.GL_FLOAT,false,6*4,3*4);

        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER,0);
        GLES30.glBindVertexArray(0);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT | GLES30.GL_DEPTH_BUFFER_BIT);

        shader.use();
        GLES30.glBindVertexArray(VAO[0]);
        Matrix.setIdentityM(model,0);
        Matrix.setIdentityM(view,0);
        Matrix.setIdentityM(projection,0);

        Matrix.perspectiveM(projection,0,45.0f,(float)WIDTH/HEIGHT,.1f,100f);
        Matrix.translateM(view,0,0,0,-3.0f);

        long time = SystemClock.uptimeMillis() % 4000L;
        float angle = 0.090f * ((int) time);
        Matrix.rotateM(model,0,angle ,1.0f,0.3f,0.5f);

        shader.setMatrix4f("model",model);
        shader.setMatrix4f("view",view);
        shader.setMatrix4f("projection",projection);

        GLES30.glDrawArrays(GLES30.GL_TRIANGLES,0,36);
    }


    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES30.glViewport(0,0,width,height);
        WIDTH = width;
        HEIGHT = height;
    }

    private static int sizeof(float[] data){
        return 4 * data.length;
    }

    private static int sizeof(int[] data){
        return 4 * data.length;
    }

    private float radians(float angle){
        return (angle* 180)/(float)Math.PI;
    }
}
