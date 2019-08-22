package com.example.ddvyu.imagereflection;

public class Vector2 {
    float x,y;
    public Vector2(final Vector2 other){
        this.x = other.x;
        this.y = other.y;
    }
    
    public Vector2(int x,int y){
        this.x = x;
        this.y = y;
    }
}
