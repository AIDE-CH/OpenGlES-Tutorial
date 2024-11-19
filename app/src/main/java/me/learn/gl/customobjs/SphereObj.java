package me.learn.gl.customobjs;



import androidx.core.math.MathUtils;

import me.learn.gl.MatUtils;
import me.learn.gl.Utils;
import me.learn.gl.core.AObj;
import me.learn.gl.core.AScene;
import me.learn.gl.core.Program;
import me.learn.gl.core.Texture;
import me.learn.gl.core.VertexBuffer;

public class SphereObj extends AObj {
    protected Program mProgram;
    protected float[] mVertices;
    protected VertexBuffer mBuffer;
    protected int nVertices;
    protected int mStrideInFloats;
    protected String[] mTextureIds;
    protected Texture mTexture;

    public SphereObj(float scale, int divide, String right, String left, String top,
                     String bottom, String front, String back){
        SphereVert sv = new SphereVert(divide);
        mVertices = sv.getPositions();
        MatUtils.scale(mVertices, scale);
        mStrideInFloats = Utils.FloatsPerPosition;;
        nVertices = mVertices.length / mStrideInFloats;
        mTextureIds = new String[]{right, left, top, bottom, front, back};
    }

    @Override
    public void onInit() throws Exception {
        mProgram = mScene.loadProgram("cubetexturenormal");
        mBuffer = new VertexBuffer();
        mBuffer.load(mVertices, true);
        mProgram.use();
        int currentOffset = 0;
        mProgram.setFloatAttrib("a_Position", Utils.FloatsPerPosition, mStrideInFloats, currentOffset);
        currentOffset += Utils.FloatsPerPosition;

        mTexture = mScene.loadCubeTexture(mTextureIds);
    }

    @Override
    public void onUpdate(long time) {
    }

    @Override
    public void destroy() {
        if(mBuffer != null) {
            mBuffer.destroy();
            mBuffer = null;
        }
    }

    @Override
    public void draw(float[] viewMatrix, float[] projectionMatrix) {
       //setDepthFuncLEqual();

        mProgram.use();
        mBuffer.bind();

        mTexture.bind();

        mProgram.setUniformMatrix4fv("a_Model", mModelMatrix);
        mProgram.setUniformMatrix4fv("a_View", viewMatrix);
        mProgram.setUniformMatrix4fv("a_Projection", projectionMatrix);


        float[] lightPos = {0.0F, 0.0F, 0.0F};
        mProgram.setUniform3fv("light.position", lightPos);
        mProgram.setUniform3fv("cameraPosition", mScene.getCamera().getPos());

        mProgram.setUniform3fv("light.ambient", new float[]{1.0f, 1.0f, 0.0f});
        mProgram.setUniform3fv("light.diffuse", new float[]{1.0f, 1.0f, 0.0f});
        mProgram.setUniform3fv("light.specular", new float[]{1.0f, 1.0f, 0.0f});

        mProgram.setUniform3fv("material.ambient", new float[]{0.1f, 0.1f, 0.1f});
        mProgram.setUniform3fv("material.diffuse", new float[]{0.7f, 0.7f, 0.7f});
        mProgram.setUniform3fv("material.specular", new float[]{1.0f, 1.0f, 1.0f});
        mProgram.setUniformFloat("material.shininess", 20.0f);


        drawTriangles(0, nVertices);

        //setDepthFuncLess();
    }
}
