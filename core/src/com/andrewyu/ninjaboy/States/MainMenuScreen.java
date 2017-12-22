package com.andrewyu.ninjaboy.States;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;

/**
 * Created by Andrew Yu on 8/7/2017.
 */
public class MainMenuScreen implements Screen {

    final NinjaBoy game;
    private Texture menuBackground;
    private Texture playButtonActive;
    private Texture playButtonInactive;
    private Texture playButton;
    private Texture howToButton;
    private Texture menuHead;
    private BitmapFont title;
    String titleText;

    public MainMenuScreen(final NinjaBoy game) {
        this.game = game;

        menuBackground = new Texture("cityscape.png");
        playButton = new Texture("play_button.png");
        howToButton = new Texture("howtoplay_button.png");
        playButtonActive = new Texture("button_play_active.png");
        playButtonInactive = new Texture("button_play_inactive.png");
        menuHead = new Texture("menuscreenhead.png");

        title = new BitmapFont();
        titleText = "Ninjaboy";

        initFonts();
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta){
        game.batch.begin();
        game.batch.draw(menuBackground, 0, 0, Gdx.graphics.getWidth() * 1.5f, Gdx.graphics.getHeight());
        game.batch.draw(menuHead, Gdx.graphics.getWidth()/5 - 30, Gdx.graphics.getHeight() - menuHead.getHeight(), menuHead.getWidth(), menuHead.getHeight());
        title.draw(game.batch, titleText, Gdx.graphics.getWidth()/5 - 170, Gdx.graphics.getHeight() - menuHead.getHeight() + 25 );
        game.batch.draw(playButton, Gdx.graphics.getWidth()/2 - playButton.getWidth()/2, Gdx.graphics.getHeight()/2 - playButton.getHeight());
        game.batch.draw(howToButton, Gdx.graphics.getWidth()/2 - howToButton.getWidth()/2, Gdx.graphics.getHeight()/2 - howToButton.getHeight() - 250);
        game.batch.end();

        if(Gdx.input.getX() > Gdx.graphics.getWidth()/2 - playButton.getWidth()/2 && Gdx.input.getX() < Gdx.graphics.getWidth()/2 + playButton.getWidth()/2
                && Gdx.input.getY() > Gdx.graphics.getHeight()/2 && Gdx.input.getY() < Gdx.graphics.getHeight()/2 + playButton.getHeight()) {
            if (Gdx.input.justTouched()) {
                game.setScreen(new GameScreen(game));
                dispose();
            }
        }

        if(Gdx.input.getX() > Gdx.graphics.getWidth()/2 - howToButton.getWidth()/2 && Gdx.input.getX() < Gdx.graphics.getWidth()/2 + howToButton.getWidth()/2
                && Gdx.input.getY() > Gdx.graphics.getHeight()/2 + 250 && Gdx.input.getY() < Gdx.graphics.getHeight()/2 + playButton.getHeight()+250) {
            if (Gdx.input.justTouched()) {
                game.setScreen(new HtpScreen(game));
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
    public void dispose() { //DISPOSE TO PREVENT MEMORY LEAKS
        menuBackground.dispose();
        playButton.dispose();
        howToButton.dispose();
        menuHead.dispose();
    }
    private void initFonts(){
        FreeTypeFontGenerator titleFontGen = new FreeTypeFontGenerator(Gdx.files.internal("fonts/silkscreen.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter titleFontParam = new FreeTypeFontGenerator.FreeTypeFontParameter();

        titleFontParam.size = 180;
        titleFontParam.color = Color.BLACK;

        title = titleFontGen.generateFont(titleFontParam);

    }

}
