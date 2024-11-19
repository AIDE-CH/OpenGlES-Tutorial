package me.learn.gl.ui;

import android.content.Context;
import android.graphics.Color;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import me.learn.gl.core.IReceiveInput;
import me.learn.gl.core.InputMode;

public class Input implements View.OnTouchListener, GestureDetector.OnGestureListener{

    private ImageButton mMoveImageButton;
    private ImageButton mRotateImageButton;
    private ImageButton mUpDownImageButton;
    private ImageButton mResetImageButton;

    private GestureDetector mGestureDetector;

    private InputMode mCurrInputMode = InputMode.MOVE;
    private List<IReceiveInput> mInReceivers = new ArrayList<>();
    public Input(Context ctx,
                 ImageButton moveImageButton,
                 ImageButton rotateImageButton,
                 ImageButton upDownImageButton,
                 ImageButton resetImageButton){
        mMoveImageButton = moveImageButton;
        mRotateImageButton = rotateImageButton;
        mUpDownImageButton = upDownImageButton;
        mResetImageButton = resetImageButton;


        mMoveImageButton.setOnClickListener(v -> {setCurrentInputMode(InputMode.MOVE);});
        mRotateImageButton.setOnClickListener(v -> {setCurrentInputMode(InputMode.ROTATE);});
        mUpDownImageButton.setOnClickListener(v -> {setCurrentInputMode(InputMode.UP_DOWN);});
        mResetImageButton.setOnClickListener(v -> {onResetCamera();});

        mGestureDetector = new GestureDetector(ctx, this);
        setCurrentInputMode(mCurrInputMode);
    }

    public void addReceiver(IReceiveInput receiver){
        mInReceivers.add(receiver);
    }

    private void onScroll(float distanceX, float distanceY){
        for(IReceiveInput r:mInReceivers){
            r.scroll(mCurrInputMode, distanceX, distanceY);
        }
    }

    private void onResetCamera(){
        for(IReceiveInput r:mInReceivers){
            r.resetCamera();
        }
    }

    private void setCurrentInputMode(InputMode inputMode) {
        int selectedColor = Color.argb(255, 255, 200, 0);
        int notSelectedColor = Color.argb(255, 200, 200, 200);
        mCurrInputMode = inputMode;
        if(mCurrInputMode == InputMode.MOVE){
            mMoveImageButton.setBackgroundColor(selectedColor);
        }else{
            mMoveImageButton.setBackgroundColor(notSelectedColor);
        }
        if(mCurrInputMode == InputMode.ROTATE){
            mRotateImageButton.setBackgroundColor(selectedColor);
        }else{
            mRotateImageButton.setBackgroundColor(notSelectedColor);
        }
        if(mCurrInputMode == InputMode.UP_DOWN){
            mUpDownImageButton.setBackgroundColor(selectedColor);
        }else{
            mUpDownImageButton.setBackgroundColor(notSelectedColor);
        }
    }

    @Override
    public boolean onDown(@NonNull MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(@NonNull MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(@NonNull MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(@Nullable MotionEvent e1, @NonNull MotionEvent e2, float distanceX, float distanceY) {
        onScroll(distanceX, distanceY);

        return false;
    }

    @Override
    public void onLongPress(@NonNull MotionEvent e) {

    }

    @Override
    public boolean onFling(@Nullable MotionEvent e1, @NonNull MotionEvent e2, float velocityX, float velocityY) {
        return false;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if(mInReceivers.size() == 0) return true;
        mGestureDetector.onTouchEvent(event);
        return true;
    }

    public void destroy() {
        try {
            mInReceivers.clear();

            mGestureDetector = null;
            mMoveImageButton.setOnClickListener(null);
            mRotateImageButton.setOnClickListener(null);
            mUpDownImageButton.setOnClickListener(null);
            mResetImageButton.setOnClickListener(null);
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }
}
