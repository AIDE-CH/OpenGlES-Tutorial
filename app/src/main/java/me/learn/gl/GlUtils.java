package me.learn.gl;

import static android.opengl.GLES32.*;

import android.util.Log;

public class GlUtils {
    public static void checkErr(int loop) {
        int err  = glGetError();
        if( err != 0){
            Log.d("Err(", "" + err + ") in loop (" + loop + ")");
        }
    }
}
