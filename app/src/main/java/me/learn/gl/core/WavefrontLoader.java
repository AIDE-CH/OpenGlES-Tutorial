package me.learn.gl.core;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import me.learn.gl.Utils;

public class WavefrontLoader {
    static class VertData{
        int vertIdx;
        int texIdx;
        int normalIdx;
    }
    static class Face{
        VertData[] d;

        public Face(int[] vertData) {
            d = new VertData[] {new VertData(), new VertData(), new VertData()};
            d[0].vertIdx = vertData[0];
            d[0].texIdx = vertData[1];
            d[0].normalIdx = vertData[2];

            d[1].vertIdx = vertData[3];
            d[1].texIdx = vertData[4];
            d[1].normalIdx = vertData[5];

            d[2].vertIdx = vertData[6];
            d[2].texIdx = vertData[7];
            d[2].normalIdx = vertData[8];
        }
    }

    private String mFileName;
    private Context mCtx;
    private List<float[]> mVertices;
    private List<float[]> mTexCoor;
    private List<float[]> mNormals;
    private List<Face> mFaces;


    public WavefrontLoader(Context ctx, String fileName) throws Exception {
        mFileName = fileName;
        mCtx = ctx;
        mVertices = new ArrayList<>();
        mTexCoor = new ArrayList<>();
        mNormals = new ArrayList<>();
        mFaces = new ArrayList<>();

        load();
    }

    private static float[] getFloats(String str, int n) throws Exception{
        String[] res = str.trim().split("\\s+");
        float[] ret = new float[n];
        int retIdx = 0;
        for(int i = 0; i < res.length; i++){
            String curr = res[i];
            if(Utils.isNullOrEmpty(curr)) continue;
            curr = curr.trim();
            if(Utils.isNullOrEmpty(curr)) continue;
            ret[retIdx] = Float.parseFloat(curr);
            retIdx++;
        }

        return ret;
    }

    private static int[] getInts(String str) throws Exception {
        String[] res = str.trim().split("\\s+");
        int[] ret = new int[9];
        for(int i = 0; i < res.length; i++) {
            String curr = res[i];
            if(Utils.isNullOrEmpty(curr)) continue;
            curr = curr.trim();
            if(Utils.isNullOrEmpty(curr)) continue;

            String[] verStrData = curr.trim().split("/");
            if(verStrData.length != 3) throw new Exception(str + " is not a valid vertex data!");

            for (int j = 0; j < 3; j++) {
                String intStr = verStrData[j];
                int retIdx = i * 3 + j;
                ret[retIdx] = -1;
                if(Utils.isNullOrEmpty(intStr)) continue;
                intStr = intStr.trim();
                if(Utils.isNullOrEmpty(intStr)) continue;
                int tmp = Integer.parseInt(intStr);
                if(tmp <= 0) throw new Exception(str + " contains not valid index!");
                ret[retIdx] = tmp - 1;
            }
        }

        return ret;
    }

    private void load() throws Exception {
        try {
            final String text = Utils.readAssetFile(mCtx, mFileName);
            if (Utils.isNullOrEmpty(text)) {
                throw new Exception(String.format("Empty object file %s", mFileName));
            }
            final String[] lines = text.split("\\r?\\n");
            for (int i = 0; i < lines.length; i++) {
                String line = lines[i];
                if (Utils.isNullOrEmpty(line)) continue;
                line = line.trim();
                if (line.isEmpty()) continue;
                if (line.startsWith("#")) continue;
                if (line.startsWith("mtllib")) continue;
                if (line.startsWith("usemtl")) continue;
                if (line.startsWith("o")) continue;
                if (line.startsWith("s")) continue;

                if (line.startsWith("vn")) {
                    float[] tmp = getFloats(line.replace("vn", ""), 3);
                    mNormals.add(tmp);
                    continue;
                }
                if (line.startsWith("vt")) {
                    float[] tmp = getFloats(line.replace("vt", ""), 2);
                    mTexCoor.add(tmp);
                    continue;
                }

                if (line.startsWith("v")) {
                    float[] tmp = getFloats(line.replace("v", ""), 3);
                    mVertices.add(tmp);
                    continue;
                }
                if (line.startsWith("f")) {
                    int[] tmp = getInts(line.replace("f", ""));
                    Face f = new Face(tmp);
                    mFaces.add(f);
                    continue;
                }
                throw new Exception(String.format("Reading frontwave file error: not recognized start of line in line %d, {%s}" +
                        " currently line should start with (#, mtllib, o, v, vn, vt, f) ", i, lines[i]));
            }
        }catch (Exception ex){
            ex.printStackTrace();
            throw ex;
        }
    }

    public boolean hasTexture(){
        return mTexCoor.size() > 0;
    }

    public boolean hasNormal(){
        return mNormals.size() > 0;
    }

    public float[] getFaces(boolean texture, boolean normal){
        int nStride = Utils.FloatsPerPosition;
        if(texture) nStride += Utils.FloatsPerTexture;
        if(normal) nStride += Utils.FloatsPerNormal;
        float[] ret = new float[nStride*mFaces.size()*3];
        int currVert = 0;
        for(Face f:mFaces){
            for(int i = 0; i < 3; i++) {
                float[] v =  mVertices.get(f.d[i].vertIdx);
                ret[currVert] = v[0]; ret[currVert+1] = v[1]; ret[currVert+2] = v[2];
                currVert += 3;
                if(texture){
                    float[] t =  mTexCoor.get(f.d[i].texIdx);
                    ret[currVert] = t[0]; ret[currVert+1] = t[1];
                    currVert += 2;
                }
                if(normal){
                    float[] n =  mNormals.get(f.d[i].normalIdx);
                    ret[currVert] = n[0]; ret[currVert+1] = n[1]; ret[currVert+2] = n[2];
                    currVert += 3;
                }
            }
        }
        return ret;
    }
}