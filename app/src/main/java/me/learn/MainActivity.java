package me.learn;

import android.opengl.GLES20;
import android.opengl.GLES32;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import me.learn.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity implements GLSurfaceView.Renderer {

    ActivityMainBinding mBinding;

    private static String TAG = MainActivity.class.getSimpleName();

    float[] mVerticesData = new float[]{
            0.0F, 0.5F, 0.0F,
            -0.5F, 0.0F, 0.0F,
            0.5F, 0.0F, 0.0F,

            0.0F, -0.5F, 0.0F,
            -0.5F, 0.0F, 0.0F,
            0.5F, 0.0F, 0.0F,
    };


    final static int BytesPerFloat = 4;
    final static int BytesPerShort = 2;
    final static int FloatsPerPosition = 3;


    private String readAssetFile(String fileName){
        BufferedReader reader = null;
        try {
            reader = new BufferedReader( new InputStreamReader(getAssets().open(fileName)) );
            StringBuilder sb = new StringBuilder();
            String mLine;
            while ((mLine = reader.readLine()) != null) {
                sb.append(mLine);
                sb.append("\n");
            }
            return  sb.toString();
        } catch (IOException e) {
            Log.e(TAG, e.toString());
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    Log.e(TAG, e.toString());
                }
            }
        }
        return null;
    }

    private int compileShader(String name, int type){
        String shaderCode;
        if(type == GLES32.GL_VERTEX_SHADER){
            shaderCode = readAssetFile(name+".vert");
        }else{
            shaderCode = readAssetFile(name+".frag");
        }
        int shaderId = GLES32.glCreateShader(type);
        GLES32.glShaderSource(shaderId, shaderCode);
        GLES32.glCompileShader(shaderId);

        // Get the compilation status.
        final int[] compileStatus = new int[1];
        GLES20.glGetShaderiv(shaderId, GLES20.GL_COMPILE_STATUS, compileStatus, 0);
        if (compileStatus[0] == 0) {
            String str = GLES20.glGetShaderInfoLog(shaderId);
            Log.e(TAG, "Error compiling shader: " + str);
            GLES20.glDeleteShader(shaderId);
            return -1;
        }
        return shaderId;
    }

    private int createProgram(String name){
        int vertexShaderId =  compileShader(name, GLES32.GL_VERTEX_SHADER);

        int fragmentShaderId = compileShader(name, GLES32.GL_FRAGMENT_SHADER);

        int programId = GLES32.glCreateProgram();
        GLES32.glAttachShader(programId, vertexShaderId);
        GLES32.glAttachShader(programId, fragmentShaderId);
        GLES32.glLinkProgram(programId);
        checkErr(0);
        int[] success = new int[1];
        GLES32.glGetProgramiv(programId, GLES20.GL_LINK_STATUS, success, 0);
        // error
        if(success[0] == 0){
            String str = GLES32.glGetProgramInfoLog(programId);
            Log.e(TAG, str);
        }
        return programId;
    }

    private int gl_vArray;
    private int gl_program;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        mBinding.surfaceView.setEGLContextClientVersion(3);
        mBinding.surfaceView.setRenderer(this);
    }

    private void checkErr(int loop){
        int err  = GLES32.glGetError();
        if( err != 0){
            Log.d("Err(", "" + err + ") in loop (" + loop + ")");
        }
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        GLES32.glClearColor(1.0F, 0.0F, 0.0F, 1.0F);

        gl_program = createProgram("simple");

        GLES32.glUseProgram(gl_program);
        int gl_position = GLES32.glGetAttribLocation(gl_program, "position");

        int[] tmp = sendVertexDataToGL(mVerticesData, gl_position);
        gl_vArray = tmp[0];
    }

    private int[] sendVertexDataToGL(float[] data, int gl_position) {
        ByteBuffer vertices_data_bytes = ByteBuffer.allocateDirect(data.length*BytesPerFloat)
                .order(ByteOrder.nativeOrder());
        FloatBuffer vertices_data = vertices_data_bytes.asFloatBuffer();
        vertices_data.put(data).position(0);


        int[] tmp = new int[1];
        GLES32.glGenVertexArrays(1, tmp, 0);
        int gl_array_id = tmp[0];

        GLES32.glGenBuffers(1, tmp, 0);
        int gl_buffer_id = tmp[0];

        GLES32.glBindVertexArray(gl_array_id);

        GLES32.glBindBuffer(GLES32.GL_ARRAY_BUFFER, gl_buffer_id);
        GLES32.glBufferData(GLES32.GL_ARRAY_BUFFER, data.length*4, vertices_data, GLES32.GL_STATIC_DRAW);


        GLES32.glEnableVertexAttribArray(gl_position);
        GLES32.glVertexAttribPointer(gl_position, FloatsPerPosition, GLES32.GL_FLOAT, false,
                FloatsPerPosition*BytesPerFloat, 0);

        return  new int[]{gl_array_id, gl_buffer_id};
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {

    }

    int loop = 0;
    @Override
    public void onDrawFrame(GL10 gl) {
        loop++;
        checkErr(loop);
        GLES32.glClear(GLES32.GL_COLOR_BUFFER_BIT | GLES32.GL_DEPTH_BUFFER_BIT);

        GLES32.glUseProgram(gl_program);
        GLES32.glBindVertexArray(gl_vArray);
        GLES32.glDrawArrays(GLES32.GL_TRIANGLES, 0, 6);
    }
}