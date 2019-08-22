package com.example.ddvyu.imagereflection;

import android.opengl.Matrix;

public class Vector3 {
    public float[] data = {0,0,0};
    
    public Vector3(final Vector3 other){
        this.data[0] = other.data[0];
        this.data[1] = other.data[1];
        this.data[2] = other.data[2];
    }
    
    public Vector3(float x,float y,float z){
        this.data[0] = x;
        this.data[1] = y;
        this.data[2] = z;
    }
    
    public Vector3(float value){
        this.data[0] = value;
        this.data[1] = value;
        this.data[2] = value;
    }
    
    public Vector3 subtract(final Vector3 other){
        return new Vector3(data[0]-other.data[0],
                            data[1]-other.data[1],
                            data[2]-other.data[2]);
    }
    
    public Vector3 add(final Vector3 other){
        return new Vector3(data[0]+other.data[0],
                data[1]+other.data[1],
                data[2]+other.data[2]);
    }
    
    public Vector3 normalize(){
        float magValue = this.magnitude();
        return new Vector3(data[0]/magValue,data[1]/magValue,data[2]/magValue);
    }
    
    public float magnitude(){
        return (float)Math.sqrt(data[0]*data[0] +
                            data[1]*data[1] +
                            data[2]*data[2]);
    }
    
    public Vector3 cross(final Vector3 other){
        float[] X = data;
        float[] Y = other.data;
        
        float x = X[1]*Y[2] - X[2]*Y[1];
        float y = X[2]*Y[0]-X[0]*Y[2];
        float z = X[0]*Y[1]-X[1]*Y[0];
        
        return new Vector3(x,y,z);
    }
    
    public Vector3 multiply(float value){
        return new Vector3(data[0]*value,data[1]*value,data[2]*value);
    }
}
