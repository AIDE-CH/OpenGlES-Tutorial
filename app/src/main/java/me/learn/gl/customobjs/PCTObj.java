package me.learn.gl.customobjs;

import me.learn.gl.Utils;
import me.learn.gl.core.AObj;
import me.learn.gl.core.AScene;
import me.learn.gl.core.Program;
import me.learn.gl.core.Texture;
import me.learn.gl.core.VertexBuffer;

/**
 * Position Color Texture Object (PTCObj)
 * An object whose vertices represented by a position, color, texture, i.e.,
 * each vertex has x,y, z, r, g, b, u, v
 * The object has one color.
 */
public class PCTObj extends AObj {
    protected Program mProgram;
    protected float[] mVertices;
    protected VertexBuffer mBuffer;
    protected int nVertices;
    protected int mStrideInFloats;
    protected boolean mHasColor;
    protected boolean mHasTexture;
    protected String mTexturePath;
    protected Texture mTexture;

    public PCTObj(float[] vertices, boolean hasColor, boolean hasTexture, String texturePath){
        mVertices = vertices;
        mStrideInFloats = Utils.FloatsPerPosition;
        mHasColor = hasColor;
        mHasTexture = hasTexture;
        mTexturePath = texturePath;
        if(mHasColor) mStrideInFloats += Utils.FloatsPerColor;
        if(mHasTexture) mStrideInFloats += Utils.FloatsPerTexture;
        nVertices = vertices.length / mStrideInFloats;
    }

    @Override
    public void onInit() {
        mProgram = mScene.loadProgram("colortexture");
        mBuffer = new VertexBuffer();
        mBuffer.load(mVertices, true);
        mProgram.use();
        int currentOffset = 0;
        mProgram.setFloatAttrib("a_Position", Utils.FloatsPerPosition, mStrideInFloats, currentOffset);
        currentOffset += Utils.FloatsPerPosition;
        if(mHasColor) {
            mProgram.setFloatAttrib("a_Color", Utils.FloatsPerColor, mStrideInFloats, currentOffset);
            currentOffset += Utils.FloatsPerColor;
        }
        if(mHasTexture) {
            mProgram.setFloatAttrib("a_Texture", Utils.FloatsPerTexture, mStrideInFloats, currentOffset);
            mTexture = mScene.loadTexture(mTexturePath);
        }
    }

    @Override
    public void onUpdate(long time) {
    }

    @Override
    public void destroy(AScene scene) {
    }

    @Override
    public void draw(float[] viewMatrix, float[] projectionMatrix) {
        mProgram.use();
        mBuffer.bind();
        if(mHasTexture)
            mTexture.bind();

        mProgram.setUniformInt("hasColor", mHasColor? 1: 0);
        mProgram.setUniformInt("hasTexture", mHasTexture? 1: 0);
        mProgram.setUniformMatrix4fv("a_Model", mModelMatrix);
        mProgram.setUniformMatrix4fv("a_View", viewMatrix);
        mProgram.setUniformMatrix4fv("a_Projection", projectionMatrix);

        drawTriangles(0, nVertices);
    }
}
