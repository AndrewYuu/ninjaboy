package com.andrewyu.ninjaboy.Control;


import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by Andrew Yu on 8/22/2017.
 */

public class SimpleGestureListener extends GestureDetector {

    public interface GestureListenerI{
        void longPress();
    }

    public SimpleGestureListener(GestureListenerI gestureListener){
        super(new myGestureListener(gestureListener));
    }

    private static class myGestureListener implements GestureDetector.GestureListener {

        GestureListenerI gestureListenerI;

        public myGestureListener(GestureListenerI gestureListener){
            this.gestureListenerI = gestureListener;
        }

        @Override
        public boolean touchDown(float x, float y, int pointer, int button) {
            return false;
        }

        @Override
        public boolean tap(float x, float y, int count, int button) {
            return false;
        }

        @Override
        public boolean longPress(float x, float y) {
            gestureListenerI.longPress();
            return true;
        }

        @Override
        public boolean fling(float velocityX, float velocityY, int button) {
            return false;
        }

        @Override
        public boolean pan(float x, float y, float deltaX, float deltaY) {
            return false;
        }

        @Override
        public boolean panStop(float x, float y, int pointer, int button) {
            return false;
        }

        @Override
        public boolean zoom(float initialDistance, float distance) {
            return false;
        }

        @Override
        public boolean pinch(Vector2 initialPointer1, Vector2 initialPointer2, Vector2 pointer1, Vector2 pointer2) {
            return false;
        }
    }
}
