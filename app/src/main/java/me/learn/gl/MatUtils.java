package me.learn.gl;

import android.opengl.Matrix;

import androidx.annotation.NonNull;
public class MatUtils {

 public static void setRotateEulerM2(@NonNull float[] rm, int rmOffset,
                                        float x, float y, float z) {
        if (rm == null) {
            throw new IllegalArgumentException("rm == null");
        }
        if (rmOffset < 0) {
            throw new IllegalArgumentException("rmOffset < 0");
        }
        if (rm.length < rmOffset + 16) {
            throw new IllegalArgumentException("rm.length < rmOffset + 16");
        }

        x *= (float) (Math.PI / 180.0f);
        y *= (float) (Math.PI / 180.0f);
        z *= (float) (Math.PI / 180.0f);
        float cx = (float) Math.cos(x);
        float sx = (float) Math.sin(x);
        float cy = (float) Math.cos(y);
        float sy = (float) Math.sin(y);
        float cz = (float) Math.cos(z);
        float sz = (float) Math.sin(z);
        float cxsy = cx * sy;
        float sxsy = sx * sy;

        rm[rmOffset + 0]  =  cy * cz;
        rm[rmOffset + 1]  = -cy * sz;
        rm[rmOffset + 2]  =  sy;
        rm[rmOffset + 3]  =  0.0f;

        rm[rmOffset + 4]  =  sxsy * cz + cx * sz;
        rm[rmOffset + 5]  = -sxsy * sz + cx * cz;
        rm[rmOffset + 6]  = -sx * cy;
        rm[rmOffset + 7]  =  0.0f;

        rm[rmOffset + 8]  = -cxsy * cz + sx * sz;
        rm[rmOffset + 9]  =  cxsy * sz + sx * cz;
        rm[rmOffset + 10] =  cx * cy;
        rm[rmOffset + 11] =  0.0f;

        rm[rmOffset + 12] =  0.0f;
        rm[rmOffset + 13] =  0.0f;
        rm[rmOffset + 14] =  0.0f;
        rm[rmOffset + 15] =  1.0f;
    }
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

    public static void setTranslationMat4(float[] mat4, float[] xyz) {
        for (int i=0 ; i<3 ; i++){
            mat4[12 + i] = xyz[i];
        }
    }
    public static float[] add(float[] a, float[] b) {
        float[] ret = new float[a.length];
        for(int i = 0; i < a.length; i++){
            ret[i] = a[i] + b[i];
        }
        return ret;
    }

    public static float[] sub(float[] a, float[] b) {
        float[] ret = new float[a.length];
        for(int i = 0; i < a.length; i++){
            ret[i] = a[i] - b[i];
        }
        return ret;
    }

    public static void scale(float[] a, float s) {
        for(int i = 0; i < a.length; i++){
            a[i] *= s;
        }
    }

    public static float norm(float[] a) {
        float n = 0F;
        for(int i = 0; i < a.length; i++){
            n += a[i] * a[i];
        }
        return (float)Math.sqrt(n);
    }

    public static void normalize(float[] a){
        float s = norm(a);
        if(s != 0){
            s = 1.0F/s;
            scale(a, s);
        }
    }

    public static float[] cross(float[] a, float[] b) {
        float x = a[1] * b[2] - a[2] * b[1];
        float y = a[2] * b[0] - a[0] * b[2];
        float z = a[0] * b[1] - a[1] * b[0];
        return new float[]{x, y, z};
    }

    public static float[] makeNewCopy(float[] a) {
        float[] ret = new float[a.length];
        System.arraycopy(a, 0, ret, 0, ret.length);
        return ret;
    }

    public static float[] rotateVec3(float[] v, float angleDegree, float[] axis) {
        float[] m = new float[16];
        set4MatrixIdentity(m);
        Matrix.rotateM(m, 0, angleDegree, axis[0], axis[1], axis[2]);
        return  matVecMultiply(m, v, 4);
    }

    public static float[] matVecMultiply(float[] m, float[] v, int nRowsInMatrix ) {
        int l = v.length;
        float[] res = new float[l];
        for(int i = 0; i < l; i++){
            res[i] = 0;
            for(int j = 0; j < l; j++) {
                res[i] += m[j*nRowsInMatrix + i]*v[j];
            }
        }
        return res;
    }

    public static float angle(float[] a, float[] b) {
        float d = dot(a, b);
        float na = norm(a);
        float nb = norm(b);
        if( (na != 0) && (nb != 0) ){
            float cosAngle = d / (na*nb);
            return (float)((Math.acos(cosAngle) - Math.PI/2)*180/Math.PI);
        }
        return 0;
    }

    public static float dot(float[] a, float[] b) {
        float d = 0;
        for(int i = 0; i < a.length; i++){
            d += a[i]*b[i];
        }
        return d;    }
}
