package me.learn.gl.customobjs;

import android.util.Log;

public class PathVert {

    public static float[] ellipse(float scale, float e, float theta){
        // https://en.wikipedia.org/wiki/Orbit_equation
        double r = scale/(1+e*Math.cos(theta));
        float x = (float) (r*Math.cos(theta));
        float z = (float) (r*Math.sin(theta));
        return  new float[] {(float) r, x, z};
    }

    public static float[] generateEllipse(float scale, float e, int nPoints, float y){
        float[] path = new float[3*(nPoints+1)];
        int l = 0;
        for(int i = 0; i <= nPoints; i++){
            float theta = (float)(((float) i/(float)nPoints)*Math.PI*2);
            float[] tmp = ellipse(scale, e, theta);
            double r = tmp[0];
            float x = tmp[1];
            float z = tmp[2];
//            if( (Math.abs(x) < 1) ){
//                Log.e("PathVert", "Test");
//            }
            path[l] = x; l++;
            path[l] = y; l++;
            path[l] = z; l++;
        }
        return path;
    }
}
