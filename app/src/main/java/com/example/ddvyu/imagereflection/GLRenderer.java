package com.example.ddvyu.imagereflection;

import android.content.res.AssetManager;
import android.opengl.GLES20;
import android.opengl.GLES30;
import android.opengl.GLSurfaceView;
import java.nio.FloatBuffer;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class GLRenderer implements GLSurfaceView.Renderer {
    private static final String TAG = "GLRenderer";
    AssetManager assets;

    private Shader shader;
    private FloatBuffer verticesBuffer;

    float vertices[] = {
            -0.5f, -0.5f, 0.0f, // left
            0.5f, -0.5f, 0.0f, // right
            0.0f,  0.5f, 0.0f  // top
    };
    int VAO[] = {0};
    int VBO[] = {0};

    public void setAssets(AssetManager assets){
        this.assets = assets;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        GLES30.glClearColor(0.2f, 0.3f, 0.3f, 1.0f);

        shader = new Shader(assets,"Shaders/vertexShader.txt","Shaders/fragmentShader.txt");

        verticesBuffer = verticesBuffer.wrap(vertices);

        GLES30.glGenVertexArrays(1,VAO,0);
        GLES30.glGenBuffers(1,VBO,0);

        GLES30.glBindVertexArray(VAO[0]);
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER,VBO[0]);
        GLES30.glBufferData(GLES30.GL_ARRAY_BUFFER,4*vertices.length,verticesBuffer,GLES30.GL_STATIC_DRAW);

        GLES30.glEnableVertexAttribArray(0);
        GLES30.glVertexAttribPointer(0,3,GLES30.GL_FLOAT,false,12,0);

        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER,0);
        GLES30.glBindVertexArray(0);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES30.glViewport(0,0,width,height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES30.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        shader.use();
        GLES30.glBindVertexArray(VAO[0]);
        GLES30.glDrawArrays(GLES30.GL_TRIANGLES,0,3);
    }
}
