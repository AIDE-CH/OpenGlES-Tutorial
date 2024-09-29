package me.learn;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.opengl.GLES20;
import android.opengl.GLES32;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.opengl.Matrix;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.text.DecimalFormat;
import java.util.Date;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import me.learn.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity implements GLSurfaceView.Renderer,
View.OnTouchListener, GestureDetector.OnGestureListener{
    enum InputMode{
        MOVE,
        ROTATE,
        UP_DOWN
    }

    private InputMode mCurrInputMode = InputMode.MOVE;

    ActivityMainBinding mBinding;

    private GestureDetector mGestureDetector;

    private static String TAG = MainActivity.class.getSimpleName();

    final static int BytesPerFloat = 4;
    final static int BytesPerShort = 2;
    final static int FloatsPerPosition = 3;
    final static int FloatsPerColor = 3;
    final static int FloatsPerTexture = 2;

    float[] mVerticesData = new float[]{
            -0.5F, 0.5F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F,
            -0.5F, 0.1F, 0.0F, 1.0F, 1.0F, 0.0F, 0, 0,
            0.5F, 0.1F, 0.0F, 1.0F, 1.0F, 0.0F, 1.0F, 0,

            -0.5F, 0.5F, 0.0F, 1.0F, 1.0F, 0.0F, 0.0F, 1.0F,
            0.5F, 0.1F, 0.0F, 1.0F, 1.0F, 0.0F,  1.0F, 0.0F,
            0.5F, 0.5F, 0.0F, 1.0F, 1.0F, 0.0F,  1.0F, 1.0F
    };

    float[] mVerticesData2 = new float[]{
            -0.5F, -0.5F, 0.0F, 1.0F, 1.0F, 1.0F, 0.0F, 1.0F,
            -0.5F, -0.1F, 0.0F, 1.0F, 1.0F, 1.0F, 0, 0,
            0.5F, -0.1F, 0.0F, 1.0F, 1.0F, 1.0F, 1.0F, 0,

            -0.5F, -0.5F, 0.0F, 1.0F, 1.0F, 1.0F, 0.0F, 1.0F,
            0.5F, -0.1F, 0.0F, 1.0F, 1.0F, 1.0F, 1.0F, 0,
            0.5F, -0.5F, 0.0F, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F
    };

    private int gl_vArray;
    private int gl_vArray2;
    private int glTexBuffer;
    private int glTexBuffer2;

    private float[] model;
    private float[] view;
    private float[] projection;

    private float[] identity;
    private int glModel, glView, glProjection;

    private float mWidth;
    private float mHeight;

    private int glProgram;


    private void checkErr(int loop){
        int err  = GLES32.glGetError();
        if( err != 0){
            Log.d("Err(", "" + err + ") in loop (" + loop + ")");
        }
    }

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

    private int sendTextureToGl(final int resourceId) {
        final int[] textureHandle = new int[1];
        GLES32.glGenTextures(1, textureHandle, 0);
        if (textureHandle[0] == 0)
        {
            throw new RuntimeException("Error generating texture name.");
        }
        final int tex = textureHandle[0];

        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        final Bitmap bitmap = BitmapFactory.decodeResource(getResources(), resourceId, options);

        GLES32.glBindTexture(GLES32.GL_TEXTURE_2D, tex);
        GLES32.glTexParameteri(GLES32.GL_TEXTURE_2D, GLES32.GL_TEXTURE_MIN_FILTER, GLES32.GL_NEAREST);
        GLUtils.texImage2D(GLES32.GL_TEXTURE_2D, 0, bitmap, 0);
        GLES32.glGenerateMipmap(GLES32.GL_TEXTURE_2D);
        bitmap.recycle();

        return tex;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        mBinding.surfaceView.setEGLContextClientVersion(3);
        mBinding.surfaceView.setRenderer(this);
        mGestureDetector = new GestureDetector(this, this);
        mBinding.surfaceView.setOnTouchListener(this);
        
        mBinding.moveImageButton.setOnClickListener(v -> {setCurrentInputMode(InputMode.MOVE);});
        mBinding.rotateImageButton.setOnClickListener(v -> {setCurrentInputMode(InputMode.ROTATE);});
        mBinding.upDownImageButton.setOnClickListener(v -> {setCurrentInputMode(InputMode.UP_DOWN);});
        mBinding.resetImageButton.setOnClickListener(v -> {resetCamera();});

        setCurrentInputMode(mCurrInputMode);
    }

    private float[] camPos = {0, 0, -3};
    private float camXAngle = 0;
    private float camYAngle = 0;

    private void resetCamera() {
        camPos[0] = 0; camPos[1] = 0; camPos[2] = -3;
        camXAngle = 0;
        camYAngle = 0;

        Matrix.setRotateEulerM2(view, 0, camXAngle, camYAngle, 0);
        Matrix.translateM(view, 0, camPos[0], camPos[1], camPos[2]);
    }

    private void setCurrentInputMode(InputMode inputMode) {
        int selectedColor = Color.argb(255, 255, 200, 0);
        int notSelectedColor = Color.argb(255, 200, 200, 200);
        mCurrInputMode = inputMode;
        if(mCurrInputMode == InputMode.MOVE){
            mBinding.moveImageButton.setBackgroundColor(selectedColor);
        }else{
            mBinding.moveImageButton.setBackgroundColor(notSelectedColor);
        }
        if(mCurrInputMode == InputMode.ROTATE){
            mBinding.rotateImageButton.setBackgroundColor(selectedColor);
        }else{
            mBinding.rotateImageButton.setBackgroundColor(notSelectedColor);
        }
        if(mCurrInputMode == InputMode.UP_DOWN){
            mBinding.upDownImageButton.setBackgroundColor(selectedColor);
        }else{
            mBinding.upDownImageButton.setBackgroundColor(notSelectedColor);
        }
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        GLES32.glClearColor(1.0F, 0.0F, 0.0F, 1.0F);


        glTexBuffer = sendTextureToGl(R.drawable.wall);
        checkErr(0);
        glTexBuffer2 = sendTextureToGl(R.drawable.wall_with_face);
        checkErr(0);

        glProgram = createProgram("colortexture");

        GLES32.glUseProgram(glProgram);
        int gl_position = GLES32.glGetAttribLocation(glProgram, "a_Position");

        glModel = GLES32.glGetUniformLocation(glProgram, "a_Model");
        checkErr(0);
        glView = GLES32.glGetUniformLocation(glProgram, "a_View");
        checkErr(0);
        glProjection = GLES32.glGetUniformLocation(glProgram, "a_Projection");
        checkErr(0);

        int gl_color = GLES32.glGetAttribLocation(glProgram, "a_Color");
        int gl_texture = GLES32.glGetAttribLocation(glProgram, "a_Texture");

        int[] tmp = sendVertexDataToGL(mVerticesData, gl_position, gl_color, gl_texture);
        gl_vArray = tmp[0];

        tmp = sendVertexDataToGL(mVerticesData2, gl_position, gl_color, gl_texture);
        gl_vArray2 = tmp[0];

        model = new float[16];
        view = new float[16];
        projection = new float[16];
        identity = new float[16];

        Matrix.setIdentityM(model, 0);
        Matrix.setIdentityM(view, 0);
        Matrix.setIdentityM(projection, 0);
        Matrix.setIdentityM(identity, 0);

        Matrix.translateM(view, 0, 0.0F, 0.0F, -3.0F );
    }

    private int[] sendVertexDataToGL(float[] data, int gl_position, int gl_color, int gl_texture) {
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
                (FloatsPerPosition+FloatsPerColor+FloatsPerTexture)*BytesPerFloat, 0);

        GLES32.glEnableVertexAttribArray(gl_color);
        GLES32.glVertexAttribPointer(gl_color, 3, GLES32.GL_FLOAT, false,
                (FloatsPerPosition+FloatsPerColor+FloatsPerTexture)*BytesPerFloat, FloatsPerPosition*BytesPerFloat);

        GLES32.glEnableVertexAttribArray(gl_texture);
        GLES32.glVertexAttribPointer(gl_texture, 2, GLES32.GL_FLOAT, false,
                (FloatsPerPosition+FloatsPerColor+FloatsPerTexture)*BytesPerFloat,
                (FloatsPerPosition+FloatsPerColor)*BytesPerFloat);

        return  new int[]{gl_array_id, gl_buffer_id};
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        mWidth = width;
        mHeight = height;
        // Set the viewport
        GLES32.glViewport(0, 0, width, height);
        checkErr(loop);
        float aspect = mWidth / mHeight;
        Matrix.perspectiveM(projection, 0, 45.0F, aspect, 0.1F, 100.0F);
    }

    private int frameCount = 0;
    private long startTime = new Date().getTime();

    private void updateFPS() {frameCount++;
        long currTime = new Date().getTime();
        double seconds = (double) (currTime - startTime)/1000.0;
        if(seconds > 1){
            double fps = (double) frameCount / seconds;
            frameCount = 0;
            startTime = currTime;
            DecimalFormat df = new DecimalFormat("#.00");
            runOnUiThread(()-> {mBinding.upperRightTextView.setText("FPS:" + df.format(fps));} );
        }
    }

    int loop = 0;
    @Override
    public void onDrawFrame(GL10 gl) {
        updateFPS();

        loop++;
        checkErr(loop);
        GLES32.glClear(GLES32.GL_COLOR_BUFFER_BIT | GLES32.GL_DEPTH_BUFFER_BIT);

        Matrix.rotateM(model, 0, 3F, 0, 1, 0);
        GLES32.glUseProgram(glProgram);
        GLES32.glUniformMatrix4fv(glModel, 1, false, model, 0);
        GLES32.glUniformMatrix4fv(glView, 1, false, view, 0);
        GLES32.glUniformMatrix4fv(glProjection, 1, false, projection, 0);


        int gl_color = GLES32.glGetUniformLocation(glProgram, "a_Color");
        GLES32.glUniform3f(gl_color, 1.0F, 1.0F, 1.0F);
        GLES32.glBindVertexArray(gl_vArray);
        GLES32.glBindTexture(GLES20.GL_TEXTURE_2D, glTexBuffer);
        GLES32.glDrawArrays(GLES32.GL_TRIANGLES, 0, 6);


        GLES32.glUniformMatrix4fv(glModel, 1, false, identity, 0);
        GLES32.glUniform3f(gl_color, 1.0F, 1.0F, 1.0F);
        GLES32.glBindVertexArray(gl_vArray2);
        GLES32.glBindTexture(GLES20.GL_TEXTURE_2D, glTexBuffer2);
        GLES32.glDrawArrays(GLES32.GL_TRIANGLES, 0, 6);
    }

    @Override
    public boolean onDown(@NonNull MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(@NonNull MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(@NonNull MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(@Nullable MotionEvent e1, @NonNull MotionEvent e2, float distanceX, float distanceY) {
        switch (mCurrInputMode){
            case MOVE:
                camPos[0] += 10*distanceX/mWidth;
                camPos[2] += 10*distanceY/mHeight;
                break;
            case ROTATE:
                camXAngle += 30*distanceY/mHeight;
                camYAngle += 30*distanceX/mWidth;
                break;
            case UP_DOWN:
                camPos[1] -= 10*distanceY/mHeight;
                break;
        }

        Matrix.setRotateEulerM2(view, 0, camXAngle, camYAngle, 0);
        Matrix.translateM(view, 0, camPos[0], camPos[1], camPos[2]);

        return false;
    }

    @Override
    public void onLongPress(@NonNull MotionEvent e) {

    }

    @Override
    public boolean onFling(@Nullable MotionEvent e1, @NonNull MotionEvent e2, float velocityX, float velocityY) {
        return false;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        mGestureDetector.onTouchEvent(event);
        return true;
    }
}