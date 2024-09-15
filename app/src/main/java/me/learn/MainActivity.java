package me.learn;

import android.opengl.GLES20;
import android.opengl.GLES32;
import android.opengl.GLSurfaceView;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import me.learn.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity implements GLSurfaceView.Renderer {

    ActivityMainBinding mBinding;

    float[] mVerticesData = new float[]{
            0.0F, 0.5F, 0.0F,
            -0.5F, 0.0F, 0.0F,
            0.5F, 0.0F, 0.0F};


    final static int BytesPerFloat = 4;
    final static int BytesPerShort = 2;
    final static int FloatsPerPosition = 3;


    private String vertex_shader_code = String.join("\n",
            "#version 300 es",
            "in vec3 position;",
            "",
            "void main()",
            "{",
            "    gl_Position = vec4(position, 1.0f);",
            "}");

    private String fragment_shader_code = String.join("\n",
            "#version 300 es",
            "precision mediump float;",
            "",
            "out vec4 outColor;",
            "void main()",
            "{",
            "   outColor = vec4(1.0f, 1.0f, 1.0f, 1.0f);",
            "}");

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

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        GLES32.glClearColor(1.0F, 0.0F, 0.0F, 1.0F);

        int vertex_shader_id = GLES32.glCreateShader(GLES32.GL_VERTEX_SHADER);
        GLES32.glShaderSource(vertex_shader_id, vertex_shader_code);
        GLES32.glCompileShader(vertex_shader_id);


        int fragment_shader_id = GLES32.glCreateShader(GLES32.GL_FRAGMENT_SHADER);
        GLES32.glShaderSource(fragment_shader_id, fragment_shader_code);
        GLES32.glCompileShader(fragment_shader_id);

        gl_program = GLES32.glCreateProgram();
        GLES32.glAttachShader(gl_program, vertex_shader_id);
        GLES32.glAttachShader(gl_program, fragment_shader_id);
        GLES32.glLinkProgram(gl_program);

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
        GLES32.glVertexAttribPointer(gl_position, FloatsPerPosition, GLES20.GL_FLOAT, false,
                FloatsPerPosition*BytesPerFloat, 0);

        return  new int[]{gl_array_id, gl_buffer_id};
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {

    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES32.glClear(GLES32.GL_COLOR_BUFFER_BIT | GLES32.GL_DEPTH_BUFFER_BIT);

        GLES32.glUseProgram(gl_program);
        GLES32.glBindVertexArray(gl_vArray);
        GLES32.glDrawArrays(GLES32.GL_TRIANGLES, 0, 3);
    }
}