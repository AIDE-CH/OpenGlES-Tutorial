package me.learn.gl.customobjs;



import me.learn.gl.Utils;
import me.learn.gl.core.AObj;
import me.learn.gl.core.AScene;
import me.learn.gl.core.Program;
import me.learn.gl.core.Texture;
import me.learn.gl.core.VertexBuffer;

public class SkyBox extends AObj {
    protected Program mProgram;
    protected float[] mVertices;
    protected VertexBuffer mBuffer;
    protected int nVertices;
    protected int mStrideInFloats;
    protected String[] mTextureIds;
    protected Texture mTexture;

    public SkyBox(float scale, String right, String left, String top,
                  String bottom, String front, String back){
        mVertices = CubeVert.create(scale, scale, scale);
        mStrideInFloats = Utils.FloatsPerPosition;;
        nVertices = mVertices.length / mStrideInFloats;
        mTextureIds = new String[]{right, left, top, bottom, front, back};
    }

    @Override
    public void onInit() {
        mProgram = mScene.loadProgram("colorcubetexture");
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
    public void destroy(AScene scene) {
    }

    @Override
    public void draw(float[] viewMatrix, float[] projectionMatrix) {
       setDepthFuncLEqual();

        mProgram.use();
        mBuffer.bind();

        mTexture.bind();

        mProgram.setUniformMatrix4fv("a_Model", mModelMatrix);
        mProgram.setUniformMatrix4fv("a_View", viewMatrix);
        mProgram.setUniformMatrix4fv("a_Projection", projectionMatrix);

        drawTriangles(0, nVertices);

        setDepthFuncLess();
    }
}
