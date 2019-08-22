package com.example.ddvyu.imagereflection;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLES30;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;
import android.opengl.GLUtils;
import android.opengl.Matrix;
import android.os.SystemClock;
import android.util.Log;
import android.widget.ArrayAdapter;

import java.io.IOException;
import java.io.InputStream;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL;
import javax.microedition.khronos.opengles.GL10;

public class GLRenderer implements GLSurfaceView.Renderer {
    private static final String TAG = "GLRenderer";
    private AssetManager assets;

    private Camera camera;
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
    private float zPosition = -3.0f;
    private float mAngle;
    private float rotateAngle;
    
    private int sphereVAO[] = {0};
    private int indexCount;
    private boolean createSphere = true;
    
    private Vector3 cameraPosition = new Vector3(0,0,3.0f);
    private Vector3 cameraFront = new Vector3(0,0,-1.0f);
    private Vector3 cameraUp= new Vector3(0,1.0f,0);
    
    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        GLES30.glClearColor(0.2f, 0.3f, 0.3f, 1.0f);
        GLES30.glEnable(GLES30.GL_DEPTH_TEST);

        camera = new Camera(new Vector3(0,0,3.0f));
        
        texture = new Texture(assets,"Textures/container.jpg");
        cubemapTexutre = loadCubeMap(Data.files);
        
        shader = new Shader(assets,"Shaders/vertexShader.vert","Shaders/fragmentShader.frag");
        skyboxShader = new Shader(assets,"Shaders/vertexSkybox.vert","Shaders/fragmentSkybox.frag");
        
        loadBox();
        loadSkybox();
        
        shader.use();
        shader.setInt("skybox",0);
        
        skyboxShader.use();
        skyboxShader.setInt("skybox",0);
    }
    
    @Override
    public void onDrawFrame(GL10 gl) {
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT | GLES30.GL_DEPTH_BUFFER_BIT);
        
        float time = (float)SystemClock.elapsedRealtime()/1000;
        deltaTime = time - lastFrame;
        lastFrame = time;
        time %= 60;
    
        Matrix.setIdentityM(model,0);
        Matrix.setIdentityM(view,0);
        Matrix.setIdentityM(projection,0);
        
        Matrix.rotateM(model,0,time,0.6f,0.4f,0.1f);
        view = camera.viewMatrix();
        Matrix.perspectiveM(projection,0,45.0f,(float)WIDTH/HEIGHT,.1f,100f);

        shader.use();
        shader.setMatrix4f("model",model);
        shader.setMatrix4f("view",view);
        shader.setMatrix4f("projection",projection);
        shader.setVec3f("cameraPos",camera.position);
        renderSphere();
        
        skyboxShader.use();
        removeTranslation(view);
        skyboxShader.setMatrix4f("view", view);
        skyboxShader.setMatrix4f("projection", projection);
        renderSkybox();
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
        GLES30.glVertexAttribPointer(0,3,GLES30.GL_FLOAT,false,8*4,0);
        GLES30.glEnableVertexAttribArray(1);
        GLES30.glVertexAttribPointer(1,3,GLES30.GL_FLOAT,false,8*4,3*4);
        GLES30.glEnableVertexAttribArray(2);
        GLES30.glVertexAttribPointer(2,2,GLES30.GL_FLOAT,false,8*4,6*4);
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
    
    private void renderSkybox(){
        GLES30.glDepthFunc(GLES30.GL_LEQUAL);
        GLES30.glBindVertexArray(VAOSkybox[0]);
        GLES30.glActiveTexture(GLES30.GL_TEXTURE0);
        GLES30.glBindTexture(GLES30.GL_TEXTURE_CUBE_MAP, cubemapTexutre);
        GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, 36);
        GLES30.glBindVertexArray(0);
        GLES30.glDepthFunc(GLES30.GL_LESS);
    }
    
    private void renderBox(){
        GLES30.glBindVertexArray(VAO[0]);
        GLES30.glDrawArrays(GLES30.GL_TRIANGLES,0,36);
        GLES30.glBindVertexArray(0);
    }
    
    private void renderSphere(){
        if(createSphere){
            createSphere =false;
            GLES30.glGenVertexArrays(0,sphereVAO,0);
            int[] VBO = {0};
            int[] EBO = {0};
            
            GLES30.glGenBuffers(1,VBO,0);
            GLES30.glGenBuffers(1,EBO,0);
            
            ArrayList<Vector3> positions = new ArrayList<>();
            ArrayList<Vector3> normals = new ArrayList<>();
            ArrayList<Integer> indices = new ArrayList<>();
            final int X_SEGMENTS = 32;
            final int Y_SEGMENTS = 32;
            final float PI = 3.14159265359f;
            
            for (int y = 0; y <= Y_SEGMENTS; ++y)
            {
                for (int x = 0; x <= X_SEGMENTS; ++x)
                {
                    float xSegment = (float)x / (float)X_SEGMENTS;
                    float ySegment = (float)y / (float)Y_SEGMENTS;
                    float xPos = (float)Math.cos(xSegment * 2.0f * PI) * (float)Math.sin(ySegment * PI);
                    float yPos = (float)Math.cos(ySegment * PI);
                    float zPos = (float)Math.sin(xSegment * 2.0f * PI) * (float)Math.sin(ySegment * PI);
            
                    positions.add(new Vector3(xPos, yPos, zPos));
                    normals.add(new Vector3(xPos, yPos, zPos));
                }
            }
    
            boolean oddRow = false;
            for (int y = 0; y < Y_SEGMENTS; ++y)
            {
                if (!oddRow) // even rows: y == 0, y == 2; and so on
                {
                    for (int x = 0; x <= X_SEGMENTS; ++x)
                    {
                        indices.add(y       * (X_SEGMENTS + 1) + x);
                        indices.add((y + 1) * (X_SEGMENTS + 1) + x);
                    }
                }
                else
                {
                    for (int x = X_SEGMENTS; x >= 0; --x)
                    {
                        indices.add((y + 1) * (X_SEGMENTS + 1) + x);
                        indices.add(y       * (X_SEGMENTS + 1) + x);
                    }
                }
                oddRow = !oddRow;
            }
            indexCount = indices.size();
    
            ArrayList<Float> data = new ArrayList<>();
            for (int i = 0; i < positions.size(); ++i)
            {
                data.add(positions.get(i).x);
                data.add(positions.get(i).y);
                data.add(positions.get(i).z);
                if (normals.size() > 0)
                {
                    data.add(normals.get(i).x);
                    data.add(normals.get(i).y);
                    data.add(normals.get(i).z);
                }
            }
            
            float[] vertices = new float[data.size()];
            for(int i=0;i<data.size();i++){
                vertices[i] = data.get(i);
            }
            data.clear();
            FloatBuffer verticesBuffer = null;
            verticesBuffer = verticesBuffer.wrap(vertices);
            
            int[] index = new int[indices.size()];
            for(int i=0;i<indices.size();i++){
                index[i] = indices.get(i);
            }
            indices.clear();
            IntBuffer indicesBuffer = null;
            indicesBuffer = indicesBuffer.wrap(index);
            
            GLES30.glBindVertexArray(sphereVAO[0]);
            GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, VBO[0]);
            GLES30.glBufferData(GLES30.GL_ARRAY_BUFFER, sizeof(vertices), verticesBuffer, GLES30.GL_STATIC_DRAW);
            
            GLES30.glBindBuffer(GLES30.GL_ELEMENT_ARRAY_BUFFER, EBO[0]);
            GLES30.glBufferData(GLES30.GL_ELEMENT_ARRAY_BUFFER, sizeof(index) ,indicesBuffer, GLES30.GL_STATIC_DRAW);

            
            GLES30.glEnableVertexAttribArray(0);
            GLES30.glVertexAttribPointer(0,3,GLES30.GL_FLOAT,false,6*4,0);
            GLES30.glEnableVertexAttribArray(1);
            GLES30.glVertexAttribPointer(1, 3, GLES30.GL_FLOAT,false,6*4,3*4);
        }
        GLES30.glBindVertexArray(sphereVAO[0]);
        GLES30.glDrawElements(GLES30.GL_TRIANGLE_STRIP, indexCount, GLES30.GL_UNSIGNED_INT, 0);
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
            camera.processInput(Movement.FORWARD,deltaTime);
        }
        else if(movement == Movement.BACKWARD){
            camera.processInput(Movement.BACKWARD,deltaTime);
        }
        else if(movement == Movement.LEFT){
            camera.processInput(Movement.LEFT,deltaTime);
        }
        else if(movement == Movement.RIGHT){
            camera.processInput(Movement.RIGHT,deltaTime);
        }
    }
    
    public void processTouch(float dx,float dy){
        camera.processTouch(dx,dy,true);
    }
    
    public float getAngle() {
        return mAngle;
    }
    
    public void setAngle(float angle) {
        mAngle = angle;
    }
}

class Data{
    static float vertices[] = {
            // positions          // normals
            -0.5f, -0.5f, -0.5f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f,
            0.5f, -0.5f, -0.5f, 0.0f, 0.0f, -1.0f, 1.0f, 0.0f,
            0.5f, 0.5f, -0.5f, 0.0f, 0.0f, -1.0f, 1.0f, 1.0f,
            0.5f, 0.5f, -0.5f, 0.0f, 0.0f, -1.0f, 1.0f, 1.0f,
            -0.5f, 0.5f, -0.5f, 0.0f, 0.0f, -1.0f, 0.0f, 1.0f,
            -0.5f, -0.5f, -0.5f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f,
            
            -0.5f, -0.5f, 0.5f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f,
            0.5f, -0.5f, 0.5f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f,
            0.5f, 0.5f, 0.5f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f,
            0.5f, 0.5f, 0.5f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f,
            -0.5f, 0.5f, 0.5f, 0.0f, 0.0f, 1.0f, 0.0f, 1.0f,
            -0.5f, -0.5f, 0.5f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f,
            
            -0.5f, 0.5f, 0.5f, -1.0f, 0.0f, 0.0f, 1.0f, 0.0f,
            -0.5f, 0.5f, -0.5f, -1.0f, 0.0f, 0.0f, 1.0f, 1.0f,
            -0.5f, -0.5f, -0.5f, -1.0f, 0.0f, 0.0f, 0.0f, 1.0f,
            -0.5f, -0.5f, -0.5f, -1.0f, 0.0f, 0.0f, 0.0f, 1.0f,
            -0.5f, -0.5f, 0.5f, -1.0f, 0.0f, 0.0f, 0.0f, 0.0f,
            -0.5f, 0.5f, 0.5f, -1.0f, 0.0f, 0.0f, 1.0f, 0.0f,
            
            0.5f, 0.5f, 0.5f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f,
            0.5f, 0.5f, -0.5f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f,
            0.5f, -0.5f, -0.5f, 1.0f, 0.0f, 0.0f, 0.0f, 1.0f,
            0.5f, -0.5f, -0.5f, 1.0f, 0.0f, 0.0f, 0.0f, 1.0f,
            0.5f, -0.5f, 0.5f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f,
            0.5f, 0.5f, 0.5f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f,
            
            -0.5f, -0.5f, -0.5f, 0.0f, -1.0f, 0.0f, 0.0f, 1.0f,
            0.5f, -0.5f, -0.5f, 0.0f, -1.0f, 0.0f, 1.0f, 1.0f,
            0.5f, -0.5f, 0.5f, 0.0f, -1.0f, 0.0f, 1.0f, 0.0f,
            0.5f, -0.5f, 0.5f, 0.0f, -1.0f, 0.0f, 1.0f, 0.0f,
            -0.5f, -0.5f, 0.5f, 0.0f, -1.0f, 0.0f, 0.0f, 0.0f,
            -0.5f, -0.5f, -0.5f, 0.0f, -1.0f, 0.0f, 0.0f, 1.0f,
            
            -0.5f, 0.5f, -0.5f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f,
            0.5f, 0.5f, -0.5f, 0.0f, 1.0f, 0.0f, 1.0f, 1.0f,
            0.5f, 0.5f, 0.5f, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f,
            0.5f, 0.5f, 0.5f, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f,
            -0.5f, 0.5f, 0.5f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f,
            -0.5f, 0.5f, -0.5f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f
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