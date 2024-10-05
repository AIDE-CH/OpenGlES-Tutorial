package me.learn.gl.core;


import static android.opengl.GLES20.GL_TRIANGLES;
import static android.opengl.GLES20.glDrawArrays;

import android.opengl.Matrix;

import me.learn.gl.MatUtils;

public abstract class AObj {
    protected float[] mModelMatrix;
    protected AScene mScene;


    public void init(AScene scene){
        mScene = scene;
        onInit();
    }

    public abstract void onInit();
    public abstract void destroy(AScene scene);
    public abstract void update(long time);
    public abstract void draw(float[] viewMatrix, float[] projectionMatrix);

    public AObj(){
        mModelMatrix = new float[16];
        MatUtils.set4MatrixIdentity(mModelMatrix);
    }

    public void translate(float[] xyz){
        MatUtils.translateMat4(mModelMatrix, xyz);
    }

    public void rotate(float[] xyzAngles){
        Matrix.setRotateEulerM2(mModelMatrix, 0, xyzAngles[0], xyzAngles[1], xyzAngles[2]);
    }

    protected void drawTriangles(int first, int count) {
        glDrawArrays(GL_TRIANGLES, first, count);
    }

}