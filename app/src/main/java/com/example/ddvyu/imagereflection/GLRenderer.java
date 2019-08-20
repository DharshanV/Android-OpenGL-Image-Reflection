package com.example.ddvyu.imagereflection;

import android.opengl.GLES20;
import android.opengl.GLES30;
import android.opengl.GLSurfaceView;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class GLRenderer implements GLSurfaceView.Renderer {
    private static final String TAG = "GLRenderer";
    private final String vertexShaderCode =
            "#version 300 es \n"+
            "layout (location = 0) in vec3 aPos; \n"+
            "void main() \n"+
            "{"+
            "   gl_Position = vec4(aPos.x, aPos.y, aPos.z, 1.0);"+
            "}";

    private final String fragmentShaderCode =
            "#version 300 es  \n"+
            "out vec4 FragColor; \n"+
            "void main() \n"+
            "{"+
            "   FragColor = vec4(1.0f, 0.5f, 0.2f, 1.0f);"+
            "}";

    private int shaderProgram;
    private FloatBuffer verticesBuffer;

    float vertices[] = {
            -0.5f, -0.5f, 0.0f, // left
            0.5f, -0.5f, 0.0f, // right
            0.0f,  0.5f, 0.0f  // top
    };
    int VAO[] = {0};
    int VBO[] = {0};

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        GLES30.glClearColor(0.2f, 0.3f, 0.3f, 1.0f);

        int vertexShader = GLRenderer.loadShader(GLES30.GL_VERTEX_SHADER, vertexShaderCode);
        int fragmentShader = GLRenderer.loadShader(GLES30.GL_FRAGMENT_SHADER, fragmentShaderCode);
        shaderProgram = loadProgram(vertexShader,fragmentShader);

        verticesBuffer = verticesBuffer.wrap(vertices);

        GLES30.glGenVertexArrays(1,VAO,0);
        GLES30.glGenBuffers(1,VBO,0);

        GLES30.glBindVertexArray(VAO[0]);
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER,VBO[0]);
        GLES30.glBufferData(GLES30.GL_ARRAY_BUFFER,4*vertices.length,verticesBuffer,GLES30.GL_STATIC_DRAW);

        GLES30.glEnableVertexAttribArray(0);
        GLES30.glVertexAttribPointer(0,3,GLES30.GL_FLOAT,false,3*4,0);

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

        GLES30.glUseProgram(shaderProgram);
        GLES30.glBindVertexArray(VAO[0]);
        GLES30.glDrawArrays(GLES30.GL_TRIANGLES,0,3);
    }

    public static int loadShader(int type,String source){
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

    public static int loadProgram(int vertexID,int fragmentID){
        int success[] = {0};
        int id = GLES20.glCreateProgram();
        GLES20.glAttachShader(id,vertexID);
        GLES20.glAttachShader(id,fragmentID);
        GLES20.glLinkProgram(id);
        GLES30.glGetShaderiv(id,GLES30.GL_LINK_STATUS,success,0);
        if(success[0] == 0){
            Log.e(TAG,GLES30.glGetProgramInfoLog(id));
        }
        else{
            GLES30.glDeleteShader(vertexID);
            GLES30.glDeleteShader(fragmentID);
        }
        return id;
    }

}
