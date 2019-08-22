package com.example.ddvyu.imagereflection;

import android.opengl.Matrix;

enum Movement{
    FORWARD,
    BACKWARD,
    LEFT,
    RIGHT
}

public class Camera {
    final float YAW         = -90.0f;
    final float PITCH       =  0.0f;
    final float SPEED       =  3f;
    final float SENSITIVITY =  0.1f;
    
    Vector3 position;
    Vector3 front;
    Vector3 up;
    Vector3 worldUp;
    Vector3 right;
    
    float yaw,pitch,movementSpeed,lookSensitivity;
    
    public Camera(Vector3 position){
        this.position = position;
        this.yaw = YAW;
        
        this.worldUp = new Vector3(0,1,0);
        this.front = new Vector3(0,0,-1.0f);
        
        this.movementSpeed = SPEED;
        this.lookSensitivity = SENSITIVITY;
        this.pitch = PITCH;
        
        updateCameraVectors();
    }
    
    public float[] viewMatrix(){
        float[] view = new float[4*4];
        Vector3 center = front.add(position);
        Matrix.setLookAtM(view,0,position.x,position.y,position.z,
                center.x,center.y,center.z,
                up.x,up.y,up.z);
        return view;
    }
    
    public void processInput(Movement movement,float deltaTime){
        float movementSpeed = 3.0f * deltaTime;
        if(movement == Movement.FORWARD){
            position = position.add(front.multiply(movementSpeed));
        }
        else if(movement == Movement.BACKWARD){
            position = position.subtract(front.multiply(movementSpeed));
        }
        else if(movement == Movement.LEFT){
            position = position.subtract(right.multiply(movementSpeed));
        }
        else if(movement == Movement.RIGHT){
            position = position.add(right.multiply(movementSpeed));
        }
    }
    
    public void processTouch(float xoffset,float yoffset,boolean constrainPitch){
        xoffset *= lookSensitivity;
        yoffset *= lookSensitivity;
        
        yaw += xoffset;
        pitch += yoffset;
    
        if (constrainPitch)
        {
            if (pitch > 89.0f)
                pitch = 89.0f;
            if (pitch < -89.0f)
                pitch = -89.0f;
        }
        updateCameraVectors();
    }
    
    private int index(int i,int j){
        return 4*i+j;
    }
    
    private void updateCameraVectors()
    {
        Vector3 front = new Vector3(0);
        front.x = (float)Math.cos(Math.toRadians(yaw))*(float)Math.cos(Math.toRadians(pitch));
        front.y = (float)Math.sin(Math.toRadians(pitch));
        front.z = (float)Math.sin(Math.toRadians(yaw))*(float)Math.cos(Math.toRadians(pitch));
        front = front.normalize();
        
        right = front.cross(worldUp).normalize();
        up = right.cross(front).normalize();
    }
    
}
