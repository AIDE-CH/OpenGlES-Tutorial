package me.learn.gl.customobjs;

import android.util.Pair;

import me.learn.gl.MatUtils;
import me.learn.gl.Utils;

public class CubeVert {
    public final static float[] CubeVertices = {

            -0.5f,  0.5f,  0.5f,  // parallel X
            -0.5f,  0.5f, -0.5f,
            -0.5f, -0.5f, -0.5f,
            -0.5f, -0.5f, -0.5f,
            -0.5f, -0.5f,  0.5f,
            -0.5f,  0.5f,  0.5f,

            0.5f,  0.5f,  0.5f,
            0.5f,  0.5f, -0.5f,
            0.5f, -0.5f, -0.5f,
            0.5f, -0.5f, -0.5f,
            0.5f, -0.5f,  0.5f,
            0.5f,  0.5f,  0.5f,

            -0.5f, -0.5f, -0.5f,  // parallel Y
            0.5f, -0.5f, -0.5f,
            0.5f, -0.5f,  0.5f,
            0.5f, -0.5f,  0.5f,
            -0.5f, -0.5f,  0.5f,
            -0.5f, -0.5f, -0.5f,

            -0.5f,  0.5f, -0.5f,
            0.5f,  0.5f, -0.5f,
            0.5f,  0.5f,  0.5f,
            0.5f,  0.5f,  0.5f,
            -0.5f,  0.5f,  0.5f,
            -0.5f,  0.5f, -0.5f,

            -0.5f, -0.5f, -0.5f, // parallel z
            0.5f, -0.5f, -0.5f,
            0.5f,  0.5f, -0.5f,
            0.5f,  0.5f, -0.5f,
            -0.5f,  0.5f, -0.5f,
            -0.5f, -0.5f, -0.5f,

            -0.5f, -0.5f,  0.5f,
            0.5f, -0.5f,  0.5f,
            0.5f,  0.5f,  0.5f,
            0.5f,  0.5f,  0.5f,
            -0.5f,  0.5f,  0.5f,
            -0.5f, -0.5f,  0.5f,
    };

    public static float[] create(float xScale, float yScale, float zScale){
        float[] ret = new float[CubeVertices.length];
        float[] scale = {xScale, yScale, zScale};
        for(int i = 0; i < 3; i++) {
            MatUtils.copyMatColumn(CubeVertices, i, 3, ret, i, 3);
            MatUtils.scaleMatColumn(ret,  i, 3, scale[i]);
        }
        return ret;
    }


    public static float[] createWithOneFileTexture(float xScale, float yScale, float zScale,
                                                   int nColsInTexture, int nRowsInTexture){
        int nVertices = CubeVertices.length / Utils.FloatsPerPosition;
        int stride = Utils.FloatsPerPosition + Utils.FloatsPerTexture;
        float[] ret = new float[nVertices*stride];
        float[] scale = {xScale, yScale, zScale};
        for(int i = 0; i < 3; i++) {
            MatUtils.copyMatColumn(CubeVertices, i, 3, ret, i, stride);
            MatUtils.scaleMatColumn(ret,  i, stride, scale[i]);
        }

        // make the texture
        for (int i = 0; i < nVertices; i += 3){
            float[] x = new float[3];
            float[] y = new float[3];
            float[] z = new float[3];
            for(int j = 0; j < 3; j++){
                x[j] = ret[(i+j)*stride    ];
                y[j] = ret[(i+j)*stride + 1];
                z[j] = ret[(i+j)*stride + 2];
            }
            float[] u = new float[3];
            float[] v = new float[3];
            Pair<Float, Float> uv = null;

            if(i < 12) { // parallel to x
                for (int j = 0; j < 3; j++) {
                    uv = matchTexture(-Math.signum(x[j]) * z[j], y[j], i, nColsInTexture, nRowsInTexture);
                    u[j] = uv.first;
                    v[j] = uv.second;
                }
            }else if(i < 24) { // parallel to y
                for(int j = 0; j < 3; j++) {
                    uv = matchTexture( x[j], -Math.signum(y[j]) * z[j], i, nColsInTexture, nRowsInTexture);
                    u[j] = uv.first;
                    v[j] = uv.second;
                }
            }else{ // paralle to z
                for(int j = 0; j < 3; j++) {
                    uv = matchTexture(Math.signum(z[j]) * x[j], y[j], i, nColsInTexture, nRowsInTexture);
                    u[j] = uv.first;
                    v[j] = uv.second;
                }
            }
            for(int j = 0; j < 3; j++) {
                ret[(i + j) * stride + Utils.FloatsPerPosition] = u[j];
                ret[(i + j) * stride + Utils.FloatsPerPosition + 1] = v[j];
            }
        }

        return ret;
    }

    private static Pair<Float, Float> matchTexture(float x, float y, int vIdx,
                                                   int nColsInTexture, int nRowsInTexture) {
        int faceIdx = vIdx / 6; // from 0 to 5 since the cube has 6 faces
        int textureRowIdx = faceIdx/nColsInTexture; // for faces 0, 1, ..., nColsInTexture, the textureRowIdx will be 1
        // for faces nColsInTexture, nColsInTexture+1, ..., 2*nColsInTexture-1,
        //the textureRowIdx will be 2 and so on
        int textureColIdx = faceIdx % nColsInTexture;

        float u = x > 0? 1: 0;
        float v = y > 0? 0: 1;
        float uStep = 1.0F/(float)nColsInTexture;
        float vStep = 1.0F/(float)nRowsInTexture;
        return new Pair<>(u*uStep+ textureColIdx * uStep,
                v*vStep+ textureRowIdx * vStep);
    }
}
