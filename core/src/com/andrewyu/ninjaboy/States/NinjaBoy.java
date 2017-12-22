package com.andrewyu.ninjaboy.States;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * Created by Andrew Yu on 8/7/2017.
 */

public class NinjaBoy extends Game {

    public SpriteBatch batch;
    public BitmapFont font;

    public void create(){
        batch = new SpriteBatch();
        font = new BitmapFont();
        this.setScreen(new MainMenuScreen(this)); //WHEN THE GAME STARTS FROM ANDROIDLAUNCHER.JAVA, IT STARTS AT THE MAINMENUSCREEN
    }

    public void render(){
        super.render();
    }

    public void dispose(){
        batch.dispose();
        font.dispose();
    }
}
