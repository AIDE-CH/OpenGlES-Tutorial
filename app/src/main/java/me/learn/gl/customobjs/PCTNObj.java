package me.learn.gl.customobjs;

import me.learn.gl.GlUtils;
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
public class PCTNObj extends AObj {
    protected Program mProgram;
    protected float[] mVertices;
    protected VertexBuffer mBuffer;
    protected int nVertices;
    protected int mStrideInFloats;
    protected boolean mHasColor;
    protected boolean mHasTexture;
    protected boolean mHasNormal;
    protected float[] mUniformColor = null;
    protected String mTexturePath;
    protected Texture mTexture;

    public PCTNObj(float[] vertices, boolean hasColor, boolean hasTexture, boolean hasNormal, String texturePath){
        mVertices = vertices;
        mStrideInFloats = Utils.FloatsPerPosition;
        mHasColor = hasColor;
        mHasTexture = hasTexture;
        mTexturePath = texturePath;
        mHasNormal = hasNormal;
        if(mHasColor) mStrideInFloats += Utils.FloatsPerColor;
        if(mHasTexture) mStrideInFloats += Utils.FloatsPerTexture;
        if(mHasNormal) mStrideInFloats += Utils.FloatsPerNormal;
        nVertices = vertices.length / mStrideInFloats;
    }

    @Override
    public void onInit() {
        mProgram = mScene.loadProgram("allshader");
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
            currentOffset += Utils.FloatsPerTexture;
        }
        if(mHasNormal) {
            mProgram.setFloatAttrib("a_Normal", Utils.FloatsPerNormal, mStrideInFloats, currentOffset);
            currentOffset += Utils.FloatsPerNormal;
            GlUtils.checkErr(0);
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
        mProgram.setUniformInt("hasNormal", mHasNormal? 1: 0);
        mProgram.setUniformInt("hasUniformColor", mUniformColor== null? 1: 0);

        mProgram.setUniformMatrix4fv("a_Model", mModelMatrix);
        mProgram.setUniformMatrix4fv("a_View", viewMatrix);
        mProgram.setUniformMatrix4fv("a_Projection", projectionMatrix);


        float[] lightPos = {0.0F, 0.0F, 0.0F};
        mProgram.setUniform3fv("light.position", lightPos);
        mProgram.setUniform3fv("cameraPosition", mScene.getCamera().getPos());

        mProgram.setUniform3fv("light.ambient", new float[]{1.0f, 1.0f, 0.0f});
        mProgram.setUniform3fv("light.diffuse", new float[]{1.0f, 1.0f, 0.0f});
        mProgram.setUniform3fv("light.specular", new float[]{1.0f, 1.0f, 0.0f});

        mProgram.setUniform3fv("material.ambient", new float[]{0.3f, 0.3f, 0.3f});
        mProgram.setUniform3fv("material.diffuse", new float[]{0.3f, 0.3f, 1.31f});
        mProgram.setUniform3fv("material.specular", new float[]{1.0f, 1.0f, 0.f}); // specular lighting doesn't have full effect on this object's material
        mProgram.setUniformFloat("material.shininess", 30.0f);


        if(mUniformColor != null){
            mProgram.setUniform3fv("u_Color", mUniformColor);
        }

        drawTriangles(0, nVertices);
    }
}
