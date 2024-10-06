package me.learn.gl.ui;


import static android.opengl.GLES32.*;

import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.widget.ImageButton;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.Date;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import me.learn.gl.GlUtils;
import me.learn.gl.core.AScene;

public class Scene extends AScene implements GLSurfaceView.Renderer {
    private Input mInput;
    private GLSurfaceView mSurface;
    private Activity mActivity;

    int loop = 0;
    private int frameCount = 0;
    private long startTime = new Date().getTime();
    private TextView mUpperRightTextView;

    public Scene(Activity activity,
                 GLSurfaceView surface,
                 ImageButton moveImageButton,
                 ImageButton rotateImageButton,
                 ImageButton upDownImageButton,
                 ImageButton resetImageButton,
                 TextView upperRightTextView){
        super(activity);
        mActivity = activity;
        mSurface = surface;
        mUpperRightTextView = upperRightTextView;
        mInput = new Input(activity,
                moveImageButton,
                rotateImageButton,
                upDownImageButton,
                resetImageButton);
        mInput.addReceiver(mCamera);

        mSurface.setEGLContextClientVersion(3);
        mSurface.setRenderer(this);
        mSurface.setOnTouchListener(mInput);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        glClearColor(1.0F, 0.0F, 0.0F, 1.0F);
        glEnable(GL_DEPTH_TEST);
        mCamera.init(this);
        initObjs();
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        mWidth = width;
        mHeight = height;

        mCamera.update();
    }



    private void updateFPS() {frameCount++;
        long currTime = new Date().getTime();
        double seconds = (double) (currTime - startTime)/1000.0;
        if(seconds > 1){
            double fps = (double) frameCount / seconds;
            frameCount = 0;
            startTime = currTime;
            DecimalFormat df = new DecimalFormat("#.00");
            mActivity.runOnUiThread(()-> {mUpperRightTextView.setText("FPS:" + df.format(fps));} );
        }
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        updateFPS();
        loop++;
        GlUtils.checkErr(loop);

        super.draw(gl);
    }
}