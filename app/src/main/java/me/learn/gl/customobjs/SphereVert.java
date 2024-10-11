package me.learn.gl.customobjs;

import android.util.Log;
import android.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.learn.gl.MatUtils;
import me.learn.gl.Utils;

public class SphereVert {
    private static String TAG = SphereVert.class.getSimpleName();

    private final static float X = 0.525731112F;
    private final static float Z = 0.850650808F;

    private static float[][] startVertices = {
            {-X, 0.0F, Z}, {X, 0.0F, Z}, {-X, 0.0F, -Z}, {X, 0.0F, -Z},
            {0.0F, Z, X}, {0.0F, Z, -X}, {0.0F, -Z, X}, {0.0F, -Z, -X},
            {Z, X, 0.0F}, {-Z, X, 0.0F}, {Z, -X, 0.0F}, {-Z, -X, 0.0F}
    };

    private static int[][] startTriangles = {
            {0,4,1}, {0,9,4}, {9,5,4}, {4,5,8}, {4,8,1},
            {8,10,1}, {8,3,10}, {5,3,8}, {5,2,3}, {2,7,3},
            {7,10,3}, {7,6,10}, {7,11,6}, {11,0,6}, {0,1,6},
            {6,1,10}, {9,0,11}, {9,11,2}, {9,2,5}, {7,2,11} };


    static class MMap{
        private Map<String, Integer> map = new HashMap<>();


        public static String key(int first, int second) {
            return first + "," + second;
        }

        public boolean insert(String key, int val) {
            if(map.containsKey(key)) return false;
            map.put(key, val);
            return true;
        }

        public int get(String key) {
            return map.get(key);
        }
    }

    private int getVertexIdx(MMap available, int first, int second){
        if (first < second) {
            int tmp = second;
            second = first;
            first = tmp;
        }
        String key = MMap.key(first, second);
        boolean  inserted = available.insert(key, vertices.size());
        if (inserted)
        {
            float[] edge0 = vertices.get(first);
            float[] edge1 = vertices.get(second);
            float[] newPoint = MatUtils.add(edge0, edge1);
            MatUtils.normalize(newPoint);
            vertices.add(newPoint);
        }

        return available.get(key);
    }

    private List<int[]> divide(){
        MMap available = new MMap();
        List<int[]> result = new ArrayList<>();

        for (int[] t:triangles)
        {
            int[] mid = new int[3];
            for (int edge = 0; edge < 3; ++edge){
                mid[edge] = getVertexIdx(available, t[edge], t[(edge+1)%3]);
            }

            int[][] newTriangles = {{t[0], mid[0], mid[2]},
                    {t[1], mid[1], mid[0]},
                    {t[2], mid[2], mid[1]},
                    {mid[0], mid[1], mid[2]}};

            for(int i = 0; i < newTriangles.length; i++){
                //Pair<float[], float[]> uvs = oneTriangleTexture(newRectangles[i]);
                result.add(newTriangles[i]);
            }
        }

        return result;
    }

    private List<float[]> vertices;
    private List<int[]> triangles;
    public SphereVert(int divide)
    {
        vertices = new ArrayList<>();
        triangles = new ArrayList<>();
        for(int i = 0; i < startVertices.length; i++){
            vertices.add(startVertices[i]);
        }
        for(int i = 0; i < startTriangles.length; i++){
            triangles.add(startTriangles[i]);
        }
        for (int i = 0; i < divide; ++i){
            triangles = divide();
        }
    }

    public float[] getPositions(){
        int VerticesPerTriangle =3;
        int size = VerticesPerTriangle * Utils.FloatsPerPosition * triangles.size();
        float[] buff = new float[size];
        int buffIdx = 0;
        for(int[] triangleVertIdxs: triangles){
            for (int vertIdx : triangleVertIdxs) {
                float[] vert = vertices.get(vertIdx);
                for (float c: vert) {
                    buff[buffIdx] = c;
                    buffIdx++;
                }
            }
        }
        return buff;
    }

    public Pair<float[], float[]> oneTriangleTexture(int[] triangleVertIdxs){
        float[] us = new float[triangleVertIdxs.length];
        float[] vs = new float[triangleVertIdxs.length];
        for (int i = 0; i < us.length; i++) {
            int vertIdx = triangleVertIdxs[i];
            float[] vert = vertices.get(vertIdx);
            float rho = MatUtils.norm(vert);
            float theta = Utils.wrapTo2PI( (float) Math.atan2(vert[0], vert[2]) );
            float phi = Utils.wrapTo2PI( (float) Math.acos(vert[1]/rho) );
            float u = theta/(2*(float) Math.PI);
            if( (u > (2*Math.PI)) || (u < 0) ) {
                Log.e(TAG, u/Math.PI + "");
            }
            float v = phi/(float) Math.PI;
            if( (v > (2*Math.PI)) || (v < 0) ) {
                Log.e(TAG, v/Math.PI + "");
            }
            us[i] = u;
            vs[i] = v;
        }
        return new Pair<>(us, vs);
    }

    public float[] getPositionsAndTexture(){
        int VerticesPerTriangle =3;
        int size = VerticesPerTriangle * (Utils.FloatsPerPosition+Utils.FloatsPerTexture) * triangles.size();
        float[] buff = new float[size];
        int buffIdx = 0;
        for(int[] triangleVertIdxs: triangles){// loop over the triangles
            // get the texture coordinates of the triangle, i.e., for the 3 vertices
            Pair<float[], float[]> uvs = oneTriangleTexture(triangleVertIdxs);
            for (int i = 0; i < triangleVertIdxs.length; i++) { // loop over 3 vertx indices of a triangle
                int vertIdx = triangleVertIdxs[i];
                float[] vert = vertices.get(vertIdx);
                // add the x,y,z coordinates of the current vertex
                for (float c: vert) {
                    buff[buffIdx] = c;
                    buffIdx++;
                }
                // add the corresponding texture coordinates
                buff[buffIdx] = uvs.first[i];
                buffIdx++;
                buff[buffIdx] = uvs.second[i];
                buffIdx++;
            }
        }
        return buff;
    }
}
