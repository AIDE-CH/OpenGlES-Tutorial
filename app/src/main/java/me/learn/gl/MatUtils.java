package me.learn.gl;

public class MatUtils {

    public static void set4MatrixIdentity(float[] mat){
        for(int i = 0; i < 4; i++){
            for(int j = 0; j < 4; j++){
                mat[i*4+j] = (i == j)? 1: 0;
            }
        }
    }

    public static void scaleMatColumn(float[] mat, int colIdx, int nColumns, float scale){
        for(int i = colIdx; i < mat.length; i += nColumns){
            mat[i] *= scale;
        }
    }

    public static void shiftMatColumn(float[] mat, int colIdx, int nColumns, float offset){
        for(int i = colIdx; i < mat.length; i += nColumns){
            mat[i] += offset;
        }
    }

    public static void copyMatColumn(float[] src, int srcColIdx, int srcNCols,
                                    float[] dest, int destColIdx, int destNCols) {
        for(int i = destColIdx, j = srcColIdx; i < dest.length; i += destNCols, j+= srcNCols){
            dest[i] = src[j];
        }
    }

    public static void translateMat4(float[] mat4, float[] xyz) {
        for (int i=0 ; i<3 ; i++){
            mat4[12 + i] += xyz[i];
        }
    }
}
