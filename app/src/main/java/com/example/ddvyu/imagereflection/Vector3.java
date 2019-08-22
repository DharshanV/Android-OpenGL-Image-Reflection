package com.example.ddvyu.imagereflection;

import android.opengl.Matrix;

public class Vector3 {
    float x,y,z;
    
    public Vector3(final Vector3 other){
        this.x = other.x;
        this.y = other.y;
        this.z = other.z;
    }
    
    public Vector3(float x,float y,float z){
        this.x = x;
        this.y = y;
        this.z = z;
    }
    
    public Vector3(float value){
        this.x = value;
        this.y = value;
        this.z = value;
    }
    
    public Vector3 subtract(final Vector3 other){
        return new Vector3(x-other.x,
                            y-other.y,
                            z-other.z);
    }
    
    public Vector3 add(final Vector3 other){
        return new Vector3(x+other.x,
                y+other.y,
                z+other.z);
    }
    
    public Vector3 normalize(){
        float magValue = this.magnitude();
        return new Vector3(x/magValue,y/magValue,z/magValue);
    }
    
    public float magnitude(){
        return (float)Math.sqrt(x*x +
                            y*y +
                            z*z);
    }
    
    public Vector3 cross(final Vector3 other){
        float newX = y*other.z - z*other.y;
        float newY = z*other.x- x*other.z;
        float newZ = x*other.y- y*other.x;
        return new Vector3(newX,newY,newZ);
    }
    
    public Vector3 multiply(float value){
        return new Vector3(x*value,y*value,z*value);
    }
}
