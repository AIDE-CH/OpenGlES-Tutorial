package me.learn.gl.core;

import android.content.Context;

import static android.opengl.GLES32.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.microedition.khronos.opengles.GL10;

public abstract class AScene {
    protected float mWidth;
    protected float mHeight;

    protected Context mContext;
    protected List<AObj> mObjects = new ArrayList<>();
    protected Map<String, Program> mPrograms = new HashMap<>();
    protected Map<String, Texture> mTextures = new HashMap<>();
    protected Camera mCamera = new Camera();

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

    protected void initObjs(){
        for(AObj obj:mObjects){
            obj.init(this);
        }
    }

    protected void updateObjs(){
        long ts = new Date().getTime();
        for(AObj obj:mObjects){
            obj.update(ts);
        }
    }

    protected void drawObjs(){
        for(AObj obj:mObjects){
            obj.draw(mCamera.getViewMatrix(), mCamera.getProjectionMatrix());
        }
    }

    public Program loadProgram(final String name){
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

    protected void draw(GL10 gl) {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        
        updateObjs();
        drawObjs();
    }

    public Camera getCamera(){
        return mCamera;
    }
}
