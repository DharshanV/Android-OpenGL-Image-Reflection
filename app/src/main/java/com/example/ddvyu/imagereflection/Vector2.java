package com.example.ddvyu.imagereflection;

public class Vector2 {
    public float[] data = {0,0};
    
    public Vector2(final Vector2 other){
        this.data[0] = other.data[0];
        this.data[1] = other.data[1];
    }
    
    public Vector2(int x,int y){
        this.data[0] = x;
        this.data[1] = y;
    }
}
