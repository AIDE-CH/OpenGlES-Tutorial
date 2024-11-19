package me.learn;

import android.opengl.Matrix;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import me.learn.databinding.ActivityMainBinding;
import me.learn.gl.MatUtils;
import me.learn.gl.core.AObj;
import me.learn.gl.core.AScene;
import me.learn.gl.core.WavefrontLoader;
import me.learn.gl.customobjs.CubeVert;
import me.learn.gl.customobjs.PCTNObj;
import me.learn.gl.customobjs.PathVert;
import me.learn.gl.customobjs.SkyBox;
import me.learn.gl.customobjs.SphereObj;
import me.learn.gl.customobjs.SphereVert2;
import me.learn.gl.customobjs.WireObj;
import me.learn.gl.ui.Scene;

public class MainActivity extends AppCompatActivity{

    ActivityMainBinding mBinding;

    private Scene mScene;

    private static String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onDestroy () {
        if(mScene != null){
            mScene.destroy();
        }
        super.onDestroy();
    }

    AObj moon;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
        mBinding.closeImageButton.setOnClickListener(v -> finish());
        mBinding.changeViewImageButton.setOnClickListener(v -> changeView());

        mScene = new Scene(this,
                mBinding.surfaceView,
                mBinding.moveImageButton,
                mBinding.rotateImageButton,
                mBinding.upDownImageButton,
                mBinding.resetImageButton,
                mBinding.upperRightTextView);

//        float[] verts = CubeVert.createWithOneFileTexture(1.0F, 1.0F, 1.0F, 4, 2);
//        PCTObj cube2 = new PCTObj(verts, false, true, "images/eightcolors.png");
//        cube2.setUpdateCall((timestamp, obj) -> obj.rotate(0.5F,  0F, 1, 0));
//        mScene.addObj(cube2);
//
//        WireObj wo = new WireObj();
//        wo.setColor(0, 1, 0);
//        wo.setVerticesFromTrianglesBuffer(verts, 0, Utils.FloatsPerPosition+Utils.FloatsPerTexture);
//        wo.setUpdateCall((timestamp, obj) -> obj.rotate(0.5F,  0F, 1, 0));
//        mScene.addObj(wo);

//        PObj cube = new PObj(CubeVert.create(1.0F, 1.0F, 1.0F), 1.0F, 1.0F, 0.0F);
//        cube.setUpdateCall((timestamp, obj) -> {
//            obj.rotate(1, 0, 1, 0);
//        });
//        mScene.addObj(cube);

//        PObj po = new PObj(mVerticesData, 1.0F, 1.0F, 0.0F);
//        mScene.addObj(po);
//
//
//        PObj po2 = new PObj(mVerticesData2, 1.0F, 1.0F, 0.0F);
//        mScene.addObj(po2);

        SkyBox sb = new SkyBox(300,
                "images/milkyway2/right.png",
                "images/milkyway2/left.png",
                "images/milkyway2/top.png",
                "images/milkyway2/bottom.png",
                "images/milkyway2/front.png",
                "images/milkyway2/back.png");
        //sb.setUpdateCall((timestamp, obj) -> obj.rotate(-0.5F,  1, 1, 0));
        mScene.addObj(sb);

//        SphereVert sv = new SphereVert(1);
//        float[] verts = sv.getPositionsAndTexture();
        SphereObj earth = new SphereObj(0.4F, 2,
                "images/earth/right.png",
                "images/earth/left.png",
                "images/earth/top.png",
                "images/earth/bottom.png",
                "images/earth/front.png",
                "images/earth/back.png"
                );
        earth.setUpdateCall((timestamp, obj) -> moveEarth(timestamp, obj));
        mScene.addObj(earth);

        SphereVert2 moonVert = new SphereVert2(0.3F, 10);
        moon = new PCTNObj(moonVert.getPositionsAndTexture(), false, true, false, "images/moon.png");
        //moon.setUpdateCall((timestamp, obj) -> moveMoon(timestamp, obj));
        mScene.addObj(moon);

        SphereVert2 sv2 = new SphereVert2(0.5F, 10);
        PCTNObj sun = new PCTNObj(sv2.getPositionsAndTexture(), false, true, false,
                "images/sun.jpg");
        sun.setUpdateCall((timestamp, obj) -> moveSun(timestamp, obj));
        mScene.addObj(sun);

        WireObj ellipse = new WireObj();
        ellipse.setColor(209 / 256.0F, 138 / 256.0F, 98 / 256.0F);
        float[] ellipseVert = PathVert.generateEllipse(3, 0.5F, 100, 0);
        ellipse.setVerticesFromPath(ellipseVert, 3, 0);
        mScene.addObj(ellipse);
//        WireObj wo = new WireObj();
//        wo.setColor(0, 1, 0);
//        wo.setVerticesFromTrianglesBuffer(verts, 0, Utils.FloatsPerPosition+Utils.FloatsPerTexture);
//        wo.setUpdateCall((timestamp, obj) -> obj.rotate(1F,  0F, 1, 0));
        //mScene.addObj(wo);

        PCTNObj cubeLike = null;
        try {
            WavefrontLoader wvLoader = new WavefrontLoader(this, "objs/cubelike/cube-like.obj");
            cubeLike = new PCTNObj(wvLoader.getFaces(true, true), false, true,
                    true,
                    "objs/cubelike/simpletexture.png");
            cubeLike.translate(new float[]{0, 3, 0});
            cubeLike.setUpdateCall(((timestamp, obj) -> obj.rotate(2, 1, 1, 0)));
            mScene.addObj(cubeLike);
        } catch (Exception e) {
            e.printStackTrace();
        }

        mScene.setUpdateCall(this::sceneUpdated);
    }

    Random rand = new Random();
    List<PCTNObj> cubes = new ArrayList<>();
    private void sceneUpdated(long ts, AScene scene){
        for(int i = 0; i < 10; i++) {
            float[] verts = CubeVert.createWithOneFileTexture(0.4F, 0.4F, 0.4F, 4, 2);
            PCTNObj cube = new PCTNObj(verts, false, true, false, "images/eightcolors.png");
            float r = (float)(Math.abs(rand.nextInt()) % 256)/255.0F;
            float g = (float)(Math.abs(rand.nextInt()) % 256)/255.0F;
            float b = (float)(Math.abs(rand.nextInt()) % 256)/255.0F;
            cube.setUniformColor( new float[]{r, g, b} );
            float x = rand.nextInt() % 40;
            float y = Math.abs(rand.nextInt()) % 100 ;
            float z = 1;
            cube.setTranslation(new float[]{x/10.0F, y/10.0F- 8.0F, z});

            mScene.addObj(cube);
            cubes.add(cube);
        }
        while (cubes.size() > 200){
            int idx = Math.abs(rand.nextInt()) % cubes.size();
            mScene.removeObj(cubes.get(idx));
            cubes.remove(idx);
        }
    }

    int currView = 1;
    private void changeView() {
        if(currView == 0){
            mScene.getCamera().setDefaultView(new float[]{0, 0, 10}, new float[]{0, 0, -0.1F});
        }else if(currView == 1){
            mScene.getCamera().setDefaultView(new float[]{0, 25, 1}, new float[]{0, -1, -0.1F});
        }

        currView++;
        currView = currView %2;
    }

    float theta = 0;
    float moonAroundItself = 0;
    float moonAroundTheEarth = 0;
    private void moveEarth(long timestamp, AObj earth) {
        theta -= (float) (1.0F/180.0F*Math.PI)/5;
        float[] res = PathVert.ellipse(3, 0.5F, theta);
        float[] currPos = new float[]{res[1], 0, res[2]};
        earth.setTranslation(new float[]{0,0,0});
        earth.rotate(5F, 0, 1, 0);
        earth.setTranslation(currPos);

        moon.setModelToIdentity();
        moonAroundItself += 5F; moonAroundItself %= 360;
        moonAroundTheEarth += 2.5F; moonAroundTheEarth %= 360;

        float[] a = new float[16];
        float[] b = new float[16];
        float[] c = new float[16];
        float[] d = new float[16];
        MatUtils.set4MatrixIdentity(a);
        MatUtils.set4MatrixIdentity(b);
        MatUtils.set4MatrixIdentity(c);
        MatUtils.set4MatrixIdentity(d);

        Matrix.rotateM(a, 0, moonAroundItself, 0, 1, 0);
        Matrix.translateM(b, 0, 0.9F, 0, 0);
        Matrix.rotateM(c, 0, moonAroundTheEarth, 0, 1, 0);
        Matrix.translateM(d, 0, currPos[0], currPos[1], currPos[2]);

        float[] cTran = MatUtils.mat4Mat4Multiply( MatUtils.mat4Mat4Multiply(d, c) ,
                MatUtils.mat4Mat4Multiply(b, a) );

        moon.mModelMatrix = cTran;
    }


    private void moveSun(long timestamp, AObj sun) {
        sun.rotate(1.5F, 0, 1, 0);
    }

}