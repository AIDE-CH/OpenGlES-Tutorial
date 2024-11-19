package me.learn.gl.core;


import static android.opengl.GLES32.*;

import android.opengl.Matrix;

import me.learn.gl.MatUtils;

public abstract class AObj {
    public float[] mModelMatrix;
    protected AScene mScene;
    protected IObjUpdateCall mUpdateCall;

    public boolean isInitialized() {
        return initialized;
    }

    public void setInitialized(boolean initialized) {
        this.initialized = initialized;
    }

    protected boolean initialized = false;

    public void setUpdateCall(IObjUpdateCall call){
        mUpdateCall = call;
    }
    public void init(AScene scene) throws Exception {
        mScene = scene;
        onInit();
    }
    public void update(long timestamp){
        if(mUpdateCall != null){
            mUpdateCall.update(timestamp, this);
        }
        onUpdate(timestamp);
    }

    public abstract void onInit() throws Exception;
    public abstract void onUpdate(long timestamp);
    public abstract void destroy();
    public abstract void draw(float[] viewMatrix, float[] projectionMatrix);

    public AObj(){
        mModelMatrix = new float[16];
        MatUtils.set4MatrixIdentity(mModelMatrix);
    }

    public void translate(float[] xyz){
        MatUtils.translateMat4(mModelMatrix, xyz);
    }

    public void setTranslation(float[] xyz){
        MatUtils.setTranslationMat4(mModelMatrix, xyz);
    }

    public void rotate(float angle, float x, float y, float z){
        Matrix.rotateM(mModelMatrix, 0, angle, x, y, z);
    }

    protected void drawTriangles(int first, int count) {
        glDrawArrays(GL_TRIANGLES, first, count);
    }

    protected void drawLines(int first, int count, float lineWidth) {
        glLineWidth(lineWidth);
        glDrawArrays(GL_LINES, first, count);
    }


    protected void setDepthFuncLEqual() {
        glDepthFunc(GL_LEQUAL);
    }

    protected void setDepthFuncLess(){
        glDepthFunc(GL_LESS);
    }

    public void setModelToIdentity() {
        Matrix.setIdentityM(mModelMatrix, 0);
    }
}