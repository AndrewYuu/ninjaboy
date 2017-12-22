package com.andrewyu.ninjaboy.Control;

import com.badlogic.gdx.input.GestureDetector;

/**
 * Created by Andrew Yu on 8/7/2017.
 */

public class SimpleDirectionGestureDetector extends GestureDetector{

    public interface DirectionListener { //INTERFACE METHODS ARE CONCRETELY IMPLEMENTED FOR THE MAINSCREEN CLASS DURING CONSTRUCTION OF THE DirectionGestureListener
        void onLeft();

        void onRight();

        void onUp();

        void onDown();

        void tap();
    }

    public SimpleDirectionGestureDetector(DirectionListener directionListener) { //CONSTRUCTOR FOR THE DIRECTION GESTURE DETECTOR
        super(new DirectionGestureListener(directionListener));
    }

    private static class DirectionGestureListener extends GestureAdapter{ //DIRECTION GESTURE LISTENER INNER CLASS. IMPLEMENTS THE METHODS OF THE DirectionListener OBJECT FROM THE INTERFACE
        DirectionListener directionListener;

        public DirectionGestureListener(DirectionListener directionListener){
            this.directionListener = directionListener;
        }

        @Override
        public boolean tap(float x, float y, int count, int button){
            if(count == 1){ //  IF SINGLE TAP...
                directionListener.tap(); //TAP METHOD FOR THE directionListener OBJECT IS CALLED, WHICH IS THE METHOD IMPLEMENTED WITH THE INTERFACE.
            }
            return super.tap(x, y, count, button);
        }

        @Override
        public boolean fling(float velocityX, float velocityY, int button) {
            if(Math.abs(velocityX)>Math.abs(velocityY)){
                if(velocityX>0){
                    directionListener.onRight();
                }else{
                    directionListener.onLeft();
                }
            }else{
                if(velocityY>0){
                    directionListener.onDown();
                }else{
                    directionListener.onUp();
                }
            }
            return super.fling(velocityX, velocityY, button);
        }
    }
}
