package me.learn.gl.core;

import static android.opengl.GLES32.*;

import android.content.Context;
import android.graphics.Bitmap;
import android.opengl.GLUtils;

import me.learn.gl.Utils;

public class Texture {
    private int mId = -1;
    private boolean mIsCube = false;


    private Texture(){
    }

    public void destroy(){
        if(mId != -1) {
            glDeleteTextures(1, new int[]{mId}, 0);
            mId = -1;
        }
    }

    public static Texture load(Context ctx, String path) {
        Texture t = new Texture();
        t.mIsCube = false;
        t.sendTextureToGl(ctx, path);
        return t;
    }

    public static Texture loadCube(Context ctx, String[] ids){
        Texture t = new Texture();
        t.mIsCube = true;
        t.sendCubeTextureToGl(ctx, ids);
        return t;
    }

//    public void unbind(){
//        if(mIsCube){
//            glBindTexture(GL_TEXTURE_CUBE_MAP, 0);
//        }else {
//            glBindTexture(GL_TEXTURE_2D, 0);
//        }
//    }

    public void bind(){
        if(mIsCube){
            glBindTexture(GL_TEXTURE_CUBE_MAP, mId);
        }else {
            glBindTexture(GL_TEXTURE_2D, mId);
        }
    }

    private void genId() {
        final int[] textureHandle = new int[1];
        glGenTextures(1, textureHandle, 0);
        if (textureHandle[0] == 0)
        {
            throw new RuntimeException("Error generating texture name.");
        }
        mId = textureHandle[0];
    }

    private void sendTextureToGl(Context ctx, String path) {
        genId();
        bind();

        final Bitmap bitmap = Utils.getBitmapFromAsset(ctx, path);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        GLUtils.texImage2D(GL_TEXTURE_2D, 0, bitmap, 0);
        glGenerateMipmap(GL_TEXTURE_2D);

        bitmap.recycle();
    }

    private void sendCubeTextureToGl(Context ctx, String[] ids) {
        genId();
        bind();

        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MIN_FILTER, GL_LINEAR);

        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_R, GL_CLAMP_TO_EDGE);

        for (int i = 0; i < ids.length; i++) {
            int width, height, nrChannels;
            final Bitmap bitmap = Utils.getBitmapFromAsset(ctx, ids[i]);
            if (bitmap != null) {
                GLUtils.texImage2D(GL_TEXTURE_CUBE_MAP_POSITIVE_X + i, 0, bitmap, 0);
                bitmap.recycle();
            } else {
            }
        }
    }
}
