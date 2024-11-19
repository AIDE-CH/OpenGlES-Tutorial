package me.learn.gl.core;

import android.content.Context;

import static android.opengl.GLES32.*;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.microedition.khronos.opengles.GL10;

import me.learn.gl.customobjs.PCTNObj;

public abstract class AScene {
    protected float mWidth;
    protected float mHeight;

    protected Context mContext;
    protected List<AObj> mObjects = new ArrayList<>();
    protected Map<String, Program> mPrograms = new HashMap<>();
    protected Map<String, Texture> mTextures = new HashMap<>();
    protected Camera mCamera = new Camera();

    protected ISceneUpdateCall mUpdateCall;
    public void setUpdateCall(ISceneUpdateCall call){
        mUpdateCall = call;
    }

    public AScene(Context ctx){
        mContext = ctx;
    }

    public float getWidth(){
        return mWidth;
    }

    public float getHeight(){
        return mHeight;
    }

    public void addObj(AObj obj){
        mObjects.add(obj);
    }

    public void removeObj(AObj obj) {
        mObjects.remove(obj);
        obj.destroy();
    }

    protected void initObjs() throws Exception {
        for(AObj obj:mObjects){
            obj.init(this);
            obj.setInitialized(true);
        }
    }

    protected void updateObjs(long ts) throws Exception {
        for(AObj obj:mObjects){
            if(!obj.isInitialized()){
                obj.init(this);
                obj.setInitialized(true);
            }
            obj.update(ts);
        }
    }

    protected void drawObjs() throws Exception {
        for(AObj obj:mObjects){
            if(!obj.isInitialized()){
                obj.init(this);
                obj.setInitialized(true);
            }
            obj.draw(mCamera.getViewMatrix(), mCamera.getProjectionMatrix());
        }
    }

    public void destroy(){
        for(AObj obj:mObjects){
            obj.destroy();
        }
        mObjects = null;
        for(Program p:mPrograms.values()){
            p.destroy();
        }
        mPrograms = null;
        for(Texture t:mTextures.values()){
            t.destroy();
        }
        mTextures = null;
        mCamera.destroy();
        mCamera = null;
    }

    public Program loadProgram(final String name) throws Exception {
        String lcName = name.toLowerCase();
        if(mPrograms.containsKey(lcName)){
            return mPrograms.get(lcName);
        }
        Program p = Program.load(mContext, lcName);
        mPrograms.put(lcName, p);
        return p;
    }

    public Texture loadTexture(String id){
        if(mTextures.containsKey(id)){
            return mTextures.get(id);
        }
        Texture t = Texture.load(mContext, id);
        mTextures.put(id, t);
        return t;
    }

    public Texture loadCubeTexture(String[] ids){
        if(mTextures.containsKey(ids[0])){
            return mTextures.get(ids[0]);
        }
        Texture t = Texture.loadCube(mContext, ids);
        mTextures.put(ids[0], t);
        return t;
    }

    protected void draw(GL10 gl) throws Exception {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        long ts = new Date().getTime();
        updateObjs(ts);
        if(mUpdateCall != null){
            mUpdateCall.update(ts, this);
        }
        drawObjs();
    }

    public Camera getCamera(){
        return mCamera;
    }
}
