package me.learn.gl.core;

import static android.opengl.GLES32.*;

import android.content.Context;
import android.graphics.Bitmap;
import android.opengl.GLUtils;

import me.learn.gl.Utils;

public class Texture {
    private int mId = -1;

    private Texture(){
    }

    public static Texture load(Context ctx, String path) {
        Texture t = new Texture();
        t.sendTextureToGl(ctx, path);
        return t;
    }

    public void bind(){
        glBindTexture(GL_TEXTURE_2D, mId);
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
}
