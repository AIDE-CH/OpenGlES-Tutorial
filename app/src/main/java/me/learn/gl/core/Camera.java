package me.learn.gl.core;


import static android.opengl.GLES32.*;

import android.opengl.Matrix;

import me.learn.gl.GlUtils;
import me.learn.gl.MatUtils;

public class Camera implements IReceiveInput{
    private float[] mViewMatrix;
    private float[] mProjectionMatrix;
    private float[] mPos;

    private float[] mOrientation;
    private final float[] mUp = {0.0F, 1.0F, 0.0F};

    private float[] mDefaultPos = {0, 0, 10};
    private float[] mDefaultOrientation = {0.0F, 0.0F, -1.0F};

    private AScene mScene;

    public Camera(){
        mViewMatrix = new float[16];
        mProjectionMatrix = new float[16];
        resetCamera();
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
        float camXAngle = 0, camYAngle = 0;
        switch (mode){
            case MOVE:
                mPos[0] -= 10*xDist/mScene.getWidth();
                mPos[2] -= 10*yDist/mScene.getHeight();
                break;
            case ROTATE:
                camXAngle = 30*yDist/mScene.getHeight();
                camYAngle = 30*xDist/mScene.getWidth();
                break;
            case UP_DOWN:
                mPos[1] += 10*yDist/mScene.getHeight();
                break;
        }
        updateViewMatrix(camXAngle, camYAngle);
    }

    private void updateViewMatrix(float camXAngle, float camYAngle) {
        float[] cross = MatUtils.cross(mOrientation, mUp);
        float[] newOrientation =  MatUtils.rotateVec3(mOrientation, camXAngle, cross);
        float aa = MatUtils.angle(newOrientation, mUp);
        if (Math.abs(aa) <=85){
            mOrientation = newOrientation;
        }
        mOrientation = MatUtils.rotateVec3(mOrientation, camYAngle, mUp);

        float[] cent = MatUtils.add(mPos, mOrientation);
        Matrix.setLookAtM(mViewMatrix, 0, mPos[0], mPos[1], mPos[2],
                cent[0], cent[1], cent[2], mUp[0], mUp[1], mUp[2]);
    }


    @Override
    public void resetCamera() {
        mPos = MatUtils.makeNewCopy(mDefaultPos);
        mOrientation = MatUtils.makeNewCopy(mDefaultOrientation);
        updateViewMatrix(0, 0);
    }

    public float[] getProjectionMatrix() {
        return mProjectionMatrix;
    }

    public float[] getViewMatrix() {
        return mViewMatrix;
    }

    public void setDefaultView(float[] pos, float[] orientation) {
        mDefaultPos = pos;
        mDefaultOrientation = orientation;
        resetCamera();
    }

    public float[] getPos() {
        return mPos;
    }
}
