package me.learn;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import me.learn.databinding.ActivityMainBinding;
import me.learn.gl.core.AObj;
import me.learn.gl.core.WavefrontLoader;
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

    float[] mVerticesData = new float[]{
            -0.5F, 0.5F, 0.0F,
            -0.5F, 0.1F, 0.0F,
            0.5F, 0.1F, 0.0F,

            -0.5F, 0.5F, 0.0F,
            0.5F, 0.1F, 0.0F,
            0.5F, 0.5F, 0.0F
    };

    float[] mVerticesData2 = new float[]{
            -0.5F, -0.5F, 0.0F,
            -0.5F, -0.1F, 0.0F,
            0.5F, -0.1F, 0.0F,

            -0.5F, -0.5F, 0.0F,
            0.5F, -0.1F, 0.0F,
            0.5F, -0.5F, 0.0F
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        mBinding.chaneViewImageButton.setOnClickListener(v -> changeView());

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
        SphereObj earth = new SphereObj(1, 2,
                "images/earth/right.png",
                "images/earth/left.png",
                "images/earth/top.png",
                "images/earth/bottom.png",
                "images/earth/front.png",
                "images/earth/back.png"
                );
        earth.setUpdateCall((timestamp, obj) -> moveEarth(timestamp, obj));
        mScene.addObj(earth);


        SphereVert2 sv2 = new SphereVert2(1, 10);
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
    private void moveEarth(long timestamp, AObj earth) {
        theta -= (float) (1.0F/180.0F*Math.PI);
        float[] res = PathVert.ellipse(3, 0.5F, theta);
        float[] currPos = new float[]{res[1], 0, res[2]};
        earth.setTranslation(new float[]{0,0,0});
        earth.rotate(5F, 0, 1, 0);
        earth.setTranslation(currPos);
    }


    private void moveSun(long timestamp, AObj sun) {
        sun.rotate(1.5F, 0, 1, 0);
    }

}