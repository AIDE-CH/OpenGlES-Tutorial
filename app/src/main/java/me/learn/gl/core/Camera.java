package me.learn.gl.core;


import static android.opengl.GLES32.*;

import android.opengl.Matrix;

import me.learn.gl.GlUtils;
import me.learn.gl.ui.Input;

public class Camera implements IReceiveInput{
    private float[] mViewMatrix;
    private float[] mProjectionMatrix;
    private float[] camPos = {0, 0, -3};
    private float camXAngle = 0;
    private float camYAngle = 0;
    private AScene mScene;

    public Camera(){
        mViewMatrix = new float[16];
        mProjectionMatrix = new float[16];

        Matrix.setIdentityM(mViewMatrix, 0);
        Matrix.setIdentityM(mProjectionMatrix, 0);

        Matrix.translateM(mViewMatrix, 0, 0.0F, 0.0F, -3.0F );
    }

    public void init(AScene scene){
        mScene = scene;
    }

    public void destroy(AScene scene){

    }

    public void update(){
        // Set the viewport
        glViewport(0, 0, (int)mScene.getWidth(), (int)mScene.getHeight());
        GlUtils.checkErr(0);
        float aspect = mScene.getWidth() / mScene.getHeight();
        Matrix.perspectiveM(mProjectionMatrix, 0, 45.0F, aspect, 0.1F, 100.0F);
    }

    @Override
    public void scroll(InputMode mode, float xDist, float yDist) {

        switch (mode){
            case MOVE:
                camPos[0] += 10*xDist/mScene.getWidth();
                camPos[2] += 10*yDist/mScene.getHeight();
                break;
            case ROTATE:
                camXAngle += 30*yDist/mScene.getHeight();
                camYAngle += 30*xDist/mScene.getWidth();
                break;
            case UP_DOWN:
                camPos[1] -= 10*yDist/mScene.getHeight();
                break;
        }

        Matrix.setRotateEulerM2(mViewMatrix, 0, camXAngle, camYAngle, 0);
        Matrix.translateM(mViewMatrix, 0, camPos[0], camPos[1], camPos[2]);

    }



    @Override
    public void resetCamera() {
        camPos[0] = 0; camPos[1] = 0; camPos[2] = -3;
        camXAngle = 0;
        camYAngle = 0;

        Matrix.setRotateEulerM2(mViewMatrix, 0, camXAngle, camYAngle, 0);
        Matrix.translateM(mViewMatrix, 0, camPos[0], camPos[1], camPos[2]);
    }

    public float[] getProjectionMatrix() {
        return mProjectionMatrix;
    }

    public float[] getViewMatrix() {
        return mViewMatrix;
    }
}
