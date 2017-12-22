package com.andrewyu.ninjaboy.States;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;

/**
 * Created by Andrew Yu on 8/16/2017.
 */

public class HtpScreen implements Screen {

    final NinjaBoy game;

    private Texture htpBackground;
    private Texture backArrow;
    private BitmapFont htpFontBody;
    private BitmapFont htpFontHeader;
    String htp;
    String htpHeader;

    public HtpScreen(final NinjaBoy game){
        this.game = game;

        htpBackground = new Texture("temp_background.png");
        backArrow = new Texture("back_arrow.png");
        htpFontBody = new BitmapFont();
        htpFontHeader = new BitmapFont();
        htpHeader = "How to Play";
        htp = "-Tap to jump."+
                "\n-Swipe up during a jump \n to perform a double\n jump." +
                "\n-Avoid all obstacles.\n-After 200 points:\n" +
                "\n1) Speed Up: \nTemp. Speed Increase.\n" +
                "\n2) Low Gravity: \nHigher Jumps and \nslower fall.\n" +
                "\n3) High Gravity: \nLower Jumps, faster fall, \nand infinite jumps. \n!!!!Alternate Taps!!!!";

        initFonts();

    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        game.batch.begin();
        game.batch.draw(htpBackground, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        game.batch.draw(backArrow, 40, 20, backArrow.getWidth() / 1.5f, backArrow.getHeight());
        htpFontHeader.draw(game.batch, htpHeader, Gdx.graphics.getWidth()/5 - 20, Gdx.graphics.getHeight()/2 + 800);
        htpFontBody.draw(game.batch, htp, 10,  Gdx.graphics.getHeight()/2 + 600);
        game.batch.end();

        if(Gdx.input.getX() > 40 && Gdx.input.getX() < backArrow.getWidth() / 1.5f + 40
               && Gdx.input.getY() < Gdx.graphics.getHeight() && Gdx.input.getY() > Gdx.graphics.getHeight() - 200) {
            if (Gdx.input.justTouched()) {
                game.setScreen(new MainMenuScreen(game));
                dispose();
            }
        }
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        htpBackground.dispose();
        backArrow.dispose();
    }

    private void initFonts(){
        FreeTypeFontGenerator htpFontBodyGen = new FreeTypeFontGenerator(Gdx.files.internal("fonts/silkscreen.ttf"));
        FreeTypeFontGenerator htpFontHeaderGen = new FreeTypeFontGenerator(Gdx.files.internal("fonts/silkscreen.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter htpFontBodyParam = new FreeTypeFontGenerator.FreeTypeFontParameter();
        FreeTypeFontGenerator.FreeTypeFontParameter htpFontHeaderParam = new FreeTypeFontGenerator.FreeTypeFontParameter();

        htpFontBodyParam.size = 70;
        htpFontBodyParam.color = Color.BLACK;
        htpFontHeaderParam.size = 100;
        htpFontHeaderParam.color = Color.BLACK;

        htpFontBody = htpFontBodyGen.generateFont(htpFontBodyParam);
        htpFontHeader = htpFontHeaderGen.generateFont(htpFontHeaderParam);
    }
}
