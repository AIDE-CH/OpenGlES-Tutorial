package me.learn.gl.core;


import static android.opengl.GLES20.GL_ARRAY_BUFFER;
import static android.opengl.GLES20.GL_DYNAMIC_DRAW;
import static android.opengl.GLES20.GL_STATIC_DRAW;
import static android.opengl.GLES20.glBindBuffer;
import static android.opengl.GLES20.glBufferData;
import static android.opengl.GLES20.glGenBuffers;
import static android.opengl.GLES30.glBindVertexArray;
import static android.opengl.GLES30.glGenVertexArrays;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import me.learn.gl.Utils;

public class VertexBuffer {
    protected int mId = -1;
    protected int mBuffId = -1;

    public VertexBuffer(){
        int[] tmp = new int[1];
        glGenVertexArrays(1, tmp, 0);
        mId = tmp[0];

        glGenBuffers(1, tmp, 0);
        mBuffId = tmp[0];
    }

    public void load(float[] data, boolean staticDraw){
        ByteBuffer vertices_data_bytes = ByteBuffer.allocateDirect(data.length* Utils.BytesPerFloat)
                .order(ByteOrder.nativeOrder());
        FloatBuffer vertices_data = vertices_data_bytes.asFloatBuffer();
        vertices_data.put(data).position(0);

        glBindVertexArray(mId);

        glBindBuffer(GL_ARRAY_BUFFER, mBuffId);
        glBufferData(GL_ARRAY_BUFFER,
                data.length*Utils.BytesPerFloat,
                vertices_data,
                staticDraw? GL_STATIC_DRAW:GL_DYNAMIC_DRAW);
    }

    public void bind(){
        glBindVertexArray(mId);
    }
}