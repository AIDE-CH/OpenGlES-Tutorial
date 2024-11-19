package me.learn.gl.customobjs;

import me.learn.gl.Utils;
import me.learn.gl.core.AObj;
import me.learn.gl.core.AScene;
import me.learn.gl.core.Program;
import me.learn.gl.core.VertexBuffer;


public class WireObj extends AObj {
    protected Program mProgram;
    protected float mR, mG, mB;
    protected float[] mVertices;
    protected VertexBuffer mBuffer;
    protected int nLines;
    protected int mStrideInFloats;
    private float mLineWidth = 20.0F;

    public WireObj(){
        mStrideInFloats = Utils.FloatsPerPosition;
    }

    public void setColor(float r, float g, float b) {
        mR = r; mG = g; mB = b;
    }

    public void setVerticesFromTrianglesBuffer(float[] vert, int offset, int stride) {
        nLines = vert.length / stride;
        int linesCount = nLines;
        mVertices = new float[linesCount * 2 * 3];
        int l = 0;
        for (int i = 0; i < nLines; i += 3) {
            float x1 = vert[stride * (i) + offset];
            float y1 = vert[stride * (i) + offset + 1];
            float z1 = vert[stride * (i) + offset + 2];

            float x2 = vert[stride * (i + 1) + offset];
            float y2 = vert[stride * (i + 1) + offset + 1];
            float z2 = vert[stride * (i + 1) + offset + 2];

            float x3 = vert[stride * (i + 2) + offset];
            float y3 = vert[stride * (i + 2) + offset + 1];
            float z3 = vert[stride * (i + 2) + offset + 2];
            mVertices[l] = x1;
            l++;
            mVertices[l] = y1;
            l++;
            mVertices[l] = z1;
            l++;
            mVertices[l] = x2;
            l++;
            mVertices[l] = y2;
            l++;
            mVertices[l] = z2;
            l++;

            mVertices[l] = x1;
            l++;
            mVertices[l] = y1;
            l++;
            mVertices[l] = z1;
            l++;
            mVertices[l] = x3;
            l++;
            mVertices[l] = y3;
            l++;
            mVertices[l] = z3;
            l++;

            mVertices[l] = x3;
            l++;
            mVertices[l] = y3;
            l++;
            mVertices[l] = z3;
            l++;
            mVertices[l] = x2;
            l++;
            mVertices[l] = y2;
            l++;
            mVertices[l] = z2;
            l++;
        }
    }

    public void setVerticesFromPath(float[] vert, int stride, int offset) {
        nLines = (vert.length/Utils.FloatsPerPosition - 1);
        int linesCount = nLines;
        mVertices = new float[linesCount * 2 * 3];
        int l = 0;
        for (int i = 0; i < (nLines); i ++) {
            float x1 = vert[stride * (i) + offset];
            float y1 = vert[stride * (i) + offset + 1];
            float z1 = vert[stride * (i) + offset + 2];

            float x2 = vert[stride * (i + 1) + offset];
            float y2 = vert[stride * (i + 1) + offset + 1];
            float z2 = vert[stride * (i + 1) + offset + 2];
            mVertices[l] = x1;
            l++;
            mVertices[l] = y1;
            l++;
            mVertices[l] = z1;
            l++;
            mVertices[l] = x2;
            l++;
            mVertices[l] = y2;
            l++;
            mVertices[l] = z2;
            l++;
        }
    }

    @Override
    public void onInit() throws Exception {
        mProgram = mScene.loadProgram("wireframe");
        mBuffer = new VertexBuffer();
        mBuffer.load(mVertices, true);
        mProgram.use();

        int currentOffset = 0;
        mProgram.setFloatAttrib("a_Position", Utils.FloatsPerPosition, Utils.FloatsPerPosition, currentOffset);
    }

    @Override
    public void onUpdate(long time) {
    }

    @Override
    public void destroy() {
        if(mBuffer != null){
            mBuffer.destroy();
            mBuffer = null;
        }
    }

    @Override
    public void draw(float[] viewMatrix, float[] projectionMatrix) {
        mProgram.use();
        mBuffer.bind();

        mProgram.setUniform3f("a_Color", mR, mG, mB);
        mProgram.setUniformMatrix4fv("a_Model", mModelMatrix);
        mProgram.setUniformMatrix4fv("a_View", viewMatrix);
        mProgram.setUniformMatrix4fv("a_Projection", projectionMatrix);

        drawLines(0, nLines *2, mLineWidth);
    }
}
