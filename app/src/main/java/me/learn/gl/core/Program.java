package me.learn.gl.core;

import static android.opengl.GLES20.glDeleteShader;
import static android.opengl.GLES32.*;

import android.content.Context;
import android.util.Log;

import me.learn.gl.GlUtils;
import me.learn.gl.Utils;

public class Program {
    private final static String TAG = Utils.class.getSimpleName();
    private int mId = -1;
    private Program(){
    }

    public void destroy(){
        if(mId != -1) {
            glDeleteShader(mId);
            mId = -1;
        }
    }

    public static Program load(Context ctx, String lcName) throws Exception {
        Program p = new Program();
        p.createProgram(ctx, lcName);
        return p;
    }


    private int compileShader(Context ctx, String name, int type) throws Exception {
        String shaderCode;
        if(type == GL_VERTEX_SHADER){
            shaderCode = Utils.readAssetFile(ctx, name+".vert");
        }else{
            shaderCode = Utils.readAssetFile(ctx, name+".frag");
        }
        int shaderId = glCreateShader(type);
        glShaderSource(shaderId, shaderCode);
        glCompileShader(shaderId);

        // Get the compilation status.
        final int[] compileStatus = new int[1];
        glGetShaderiv(shaderId, GL_COMPILE_STATUS, compileStatus, 0);
        if (compileStatus[0] == 0) {
            String str = glGetShaderInfoLog(shaderId);
            Log.e(TAG, "Error compiling shader: " + str);
            glDeleteShader(shaderId);
            throw  new Exception("Shader compile error: " + str);
        }
        return shaderId;
    }

    private void createProgram(Context ctx, String name) throws Exception {
        int vertexShaderId =  compileShader(ctx, name, GL_VERTEX_SHADER);
        int fragmentShaderId = compileShader(ctx, name, GL_FRAGMENT_SHADER);

        mId = glCreateProgram();
        glAttachShader(mId, vertexShaderId);
        glAttachShader(mId, fragmentShaderId);
        glLinkProgram(mId);
        GlUtils.checkErr(0);
        int[] success = new int[1];
        glGetProgramiv(mId, GL_LINK_STATUS, success, 0);
        // error
        if(success[0] == 0){
            String str = glGetProgramInfoLog(mId);
            Log.e(TAG, str);
            throw  new Exception("program link error: " + str);
        }
        glDeleteShader(vertexShaderId);
        glDeleteShader(fragmentShaderId);
    }

    public void use() {
        glUseProgram(mId);
    }

    private int getUniformLocation(String name) {
        return glGetUniformLocation(mId, name);
    }

    private int getAttribLocation(String name){
        return glGetAttribLocation(mId, name);
    }

    public void setFloatAttrib(String name, int lengthInFloats, int strideInFloats, int offsetInFloats){
        final int tmp = getAttribLocation(name);
        glEnableVertexAttribArray(tmp);
        glVertexAttribPointer(tmp, lengthInFloats, GL_FLOAT, false,
                strideInFloats*Utils.BytesPerFloat,
                offsetInFloats*Utils.BytesPerFloat);
    }

    public void setUniformMatrix4fv(String name, float[] val) {
        final int tmp = getUniformLocation(name);
        glUniformMatrix4fv(tmp, 1, false, val, 0);
    }
    public void setUniform3f(String name, float x, float y, float z){
        final int tmp = getUniformLocation(name);
        glUniform3f(tmp, x, y, z);
    }

    public void setUniformInt(String name, int val){
        final int tmp = getUniformLocation(name);
        glUniform1i(tmp, val);
    }

    public void setUniform3fv(String name, float[] v){
        final int tmp = getUniformLocation(name);
        glUniform3fv(tmp, 1, v, 0);
    }

    public void setUniformFloat(String name, float val){
        final int tmp = getUniformLocation(name);
        glUniform1f(tmp, val);
    }
}
