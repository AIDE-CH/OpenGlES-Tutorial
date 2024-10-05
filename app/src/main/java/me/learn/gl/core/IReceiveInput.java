package me.learn.gl.core;

public interface IReceiveInput {
    void scroll(InputMode mode, float xDist, float yDist);
    void resetCamera();
}