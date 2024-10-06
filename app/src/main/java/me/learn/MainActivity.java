package me.learn;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import me.learn.databinding.ActivityMainBinding;
import me.learn.gl.Utils;
import me.learn.gl.core.AObj;
import me.learn.gl.core.IObjUpdateCall;
import me.learn.gl.customobjs.CubeVert;
import me.learn.gl.customobjs.PCTObj;
import me.learn.gl.customobjs.PObj;
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

        mScene = new Scene(this,
                mBinding.surfaceView,
                mBinding.moveImageButton,
                mBinding.rotateImageButton,
                mBinding.upDownImageButton,
                mBinding.resetImageButton,
                mBinding.upperRightTextView);

        float[] verts = CubeVert.createWithOneFileTexture(1.0F, 1.0F, 1.0F, 4, 2);
        PCTObj cube2 = new PCTObj(verts, false, true, "images/eightcolors.png");
        cube2.setUpdateCall((timestamp, obj) -> obj.rotate(0.5F,  0F, 1, 0));
        mScene.addObj(cube2);

        WireObj wo = new WireObj();
        wo.setColor(0, 1, 0);
        wo.setVerticesFromTrianglesBuffer(verts, 0, Utils.FloatsPerPosition+Utils.FloatsPerTexture);
        wo.setUpdateCall((timestamp, obj) -> obj.rotate(0.5F,  0F, 1, 0));
        mScene.addObj(wo);

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


    }

}