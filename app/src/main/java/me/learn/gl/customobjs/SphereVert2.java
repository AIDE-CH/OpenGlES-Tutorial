package me.learn.gl.customobjs;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import me.learn.gl.Utils;

public class SphereVert2 {

    static  float PI = (float) Math.PI;
    static class VD{
        float x, y, z;
        float u, v;
        float phi, theta;
        int i, j;
        int idx;

        static VD fromGrad(int i, int j, int gradation, float radius){
            VD ret = new VD();
            ret.i = i; ret.j = j;
            ret.phi = ((float) j / gradation) * PI;
            ret.theta =  ((float)i / (float)gradation) * 2.0F * PI;
            ret.x = (float) (radius*Math.cos(ret.theta) * Math.sin(ret.phi));
            ret.z = (float) (radius*Math.sin(ret.theta) * Math.sin(ret.phi));
            ret.y = (float) (radius*Math.cos(ret.phi));

            float theta = Utils.wrapTo2PI( ret.theta);
            float phi = Utils.wrapTo2PI( ret.phi);
            float u = theta/(2*(float) Math.PI);
            if( (u > (2*Math.PI)) || (u < 0) ) {
                Log.e("GGGG", u/Math.PI + "");
            }
            float v = phi/(float) Math.PI;
            if( (v > (2*Math.PI)) || (v < 0) ) {
                Log.e("GGGG", v/Math.PI + "");
            }
            ret.u = 1-u;
            ret.v = v;
            ret.idx = 0;
            return ret;
        }
    }

    //using IndexedMesh=std::pair<VertexList, TriangleList>;
    private List<VD> vertices;
    private List<int[]> triangles;

    public SphereVert2(float radius, int gradation){
        vertices = new ArrayList<>();
        triangles = new ArrayList<>();
        createWithOneTexture(radius, gradation);
    }

    private void addVD(List<VD> lst, int i, int j, int gradation, float radius){
        VD v = VD.fromGrad(i, j, gradation, radius);
        v.idx = vertices.size();
        vertices.add(v);
        lst.add(v);
    }

    private void createWithOneTexture(float radius, int gradation) {
        List<VD> prev = new ArrayList<>();
        addVD(prev, 0, 0, gradation, radius);
        for (int j = 1; j <= gradation; j++) {
            //float alpha = ((float) j / gradation) * PI;
            List<VD> curr = new ArrayList<>();
            if(j == gradation){
                addVD(curr, 0, gradation, gradation, radius);
            }else{
                for (int i = 0; i <= gradation; i++) {
                    addVD(curr, i, j, gradation, radius);
                }
            }
            connect(prev, curr);
            prev = curr;
        }
    }

    private void connect(List<VD> prev, List<VD> curr){
        if(prev.size() == 1){
            VD one = prev.get(0);
            for(int i = 1; i < curr.size(); i++) {
                triangles.add(new int[]{ one.idx, curr.get(i-1).idx, curr.get(i).idx } );
            }
            return;
        }
        if(curr.size() == 1){
            VD one = curr.get(0);
            for(int i = 1; i < prev.size(); i++) {
                triangles.add(new int[]{ one.idx, prev.get(i-1).idx, prev.get(i).idx } );
            }
            return;
        }
        for(int i = 1; i < prev.size(); i++){
            VD one = prev.get(i-1);
            VD two = prev.get(i);
            VD three = curr.get(i);
            VD four = curr.get(i-1);
            triangles.add(new int[]{ one.idx, two.idx, three.idx } );
            triangles.add(new int[]{ one.idx, three.idx, four.idx } );
        }
    }

    public float[] getPositionsAndTexture(){
        int VerticesPerTriangle =3;
        int size = VerticesPerTriangle * (Utils.FloatsPerPosition+Utils.FloatsPerTexture) * triangles.size();
        float[] buff = new float[size];
        int buffIdx = 0;
        for(int[] triangleVertIdxs: triangles){// loop over the triangles

            for (int i = 0; i < triangleVertIdxs.length; i++) { // loop over 3 vertx indices of a triangle
                int vertIdx = triangleVertIdxs[i];
                VD v = vertices.get(vertIdx);
                // add the x,y,z coordinates of the current vertex
                buff[buffIdx] = v.x; buffIdx++;
                buff[buffIdx] = v.y; buffIdx++;
                buff[buffIdx] = v.z; buffIdx++;
                // add the corresponding texture coordinates
                buff[buffIdx] = v.u; buffIdx++;
                buff[buffIdx] = v.v; buffIdx++;
            }
        }
        return buff;
    }
}
