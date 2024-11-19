package me.learn.gl.customobjs;


import me.learn.gl.Utils;
import me.learn.gl.core.AObj;
import me.learn.gl.core.AScene;
import me.learn.gl.core.Program;
import me.learn.gl.core.VertexBuffer;

/**
 * Position Object (PObj)
 * An object represented only by a position, i.e., each vertex has only x,y, z coordinates.
 * The object has one color.
 */
public class PObj extends AObj {
    protected Program mProgram;
    protected VertexBuffer mBuffer;
    protected float[] mVertices;

    protected float mR, mG, mB;
    protected int nVertices;

    public PObj(float[] vertices, float r, float g, float b){
        mVertices = vertices;
        mR = r;
        mG = g;
        mB = b;
        nVertices = vertices.length / Utils.FloatsPerPosition;
    }

    @Override
    public void onInit() throws Exception {
        mProgram = mScene.loadProgram("modelviewprojection");
        mBuffer = new VertexBuffer();
        mBuffer.load(mVertices, true);
        mProgram.use();
        mProgram.setFloatAttrib("a_Position", Utils.FloatsPerPosition, Utils.FloatsPerPosition, 0);
    }

    @Override
    public void destroy() {
        if(mBuffer != null){
            mBuffer.destroy();
            mBuffer = null;
        }
    }

    @Override
    public void onUpdate(long time) {
    }

    @Override
    public void draw(float[] viewMatrix, float[] projectionMatrix) {
        mProgram.use();
        mBuffer.bind();
        mProgram.setUniform3f("a_Color", mR, mG, mB);
        mProgram.setUniformMatrix4fv("a_Model", mModelMatrix);
        mProgram.setUniformMatrix4fv("a_View", viewMatrix);
        mProgram.setUniformMatrix4fv("a_Projection", projectionMatrix);

        drawTriangles(0, nVertices);
    }
}