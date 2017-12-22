package com.andrewyu.ninjaboy.States;

import com.andrewyu.ninjaboy.Control.SimpleDirectionGestureDetector;
import com.andrewyu.ninjaboy.Control.SimpleGestureListener;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import sun.font.TrueTypeFont;

public class GameScreen implements Screen {

	final NinjaBoy game;
	//private SpriteBatch batch;
	private Texture background;
	private Texture platform;
	private Texture ninjaboy;
	private Texture spike1;
	private Texture spike2;
	private Texture spike3;
	private Texture pillar2;
	private Texture pillar3;
	private Texture pillar6;
	private Texture overArrow;
	public static Preferences prefs;
	Runnable anomRunnable;
	ScheduledExecutorService exec;

	TextureRegion[] animationFrames;
	Animation animation;
	BitmapFont scoreFont;
	BitmapFont gameOverFont;
	BitmapFont anomFont;
	BitmapFont overScreenFont;
	Rectangle ninjaHitbox;
	Rectangle spikeHitbox;
	Rectangle pillarHitbox;
	ShapeRenderer shapeRenderer;

	//NINJABOY
	float deltaTime = 0;
	float charPosY;
	float defaultCharPosY;
	float platformY;
	float velocity = 0; //SPEED OF THE JUMP AND HOW HIGH TO JUMP PER TAP.
	float gravityFactor = 2.5f; //SPEED AND FACTOR IN WHICH CHARACTER IS PULLED DOWN. HIGHER THE NUMBER = STRONGER THE GRAVITY
	int gameState = 0;
	boolean isJumping = false;
	int jumpCounter = 0;

	float score = 1;

	//NINJABOY ANOMALIES
	boolean isHighGravity = false;
	boolean isSuperSpeed = false;
	boolean isAntiGravity = false;
	int previousAnomaly = 0;

	//OBSTACLES
	Random randomGenerator;
	float obstacleVelocity = 4;
	float originalObstacleVelocity;
	int numberOfObstacles = 1;
	float[] obstacleXPos = new float[numberOfObstacles];
	float distanceBetweenObstacles;
	ArrayList<Integer> typeOfObstacles = new ArrayList<Integer>();

	public GameScreen(final NinjaBoy game){
		this.game = game;
		background = new Texture("cityscape.png");
		ninjaboy = new Texture("ninjaboy_spritesheet_r.png"); //DEFAULT NINJABOY SKIN SPRITESHEET
		platform = new Texture("temp_platform.png");
		spike1 = new Texture("spike_1.png");
		spike2 = new Texture("spike_2.png");
		spike3 = new Texture("spike_3.png");
		pillar2 = new Texture("pillar_2.png");
		pillar3 = new Texture("pillar_3.png");
		pillar6 = new Texture("pillar_6.png");
		overArrow = new Texture("over_arrow.png");
		scoreFont = new BitmapFont();
		gameOverFont = new BitmapFont();
		anomFont = new BitmapFont();
		overScreenFont = new BitmapFont();
		shapeRenderer = new ShapeRenderer();
		ninjaHitbox = new Rectangle();
		Gdx.graphics.setVSync(false);
		initFonts();

//		scoreFont.setColor(Color.BLACK);
//		scoreFont.getData().setScale(5);
//		gameOverFont.setColor(Color.BLACK);
//		gameOverFont.getData().setScale(5);
//		anomFont.setColor(Color.BLACK);
//		anomFont.getData().setScale(5);

		//HIGHSCORE CONSTRUCT. CREATES "NinjaBoy" PREFERENCE AND USES KEY VALUE PAIRS TO OBTAIN AND RETRIEVE VALUES.
		//THE FLUSH() METHOD SAVES THE VALUE, WHICH IS RETAINED EVEN AFTER APP CLOSURE.
		prefs = Gdx.app.getPreferences("NinjaBoy"); //THE HIGHSCORE STORED WILL BE STORED IN THE PREFERENCE NAMED "NinjaBoy"
		if(!prefs.contains("highScore")){
			prefs.putInteger("highScore", 0);
		}

		TextureRegion[][] tempFrames = TextureRegion.split(ninjaboy, 195, 195);
		animationFrames = new TextureRegion[16];//CONVERT TO A 1D ARRAY
		int index = 0;
		for(int i = 0; i < 4; i++){ //ROWS
			for(int j = 0; j < 4; j++) {//COLS
				//FOR EACH ONE, ADD IT TO THE ANIMATION FRAMES
				animationFrames[index++] = tempFrames[i][j];
			}
		}
		animation = new Animation(.025f, animationFrames);

		defaultCharPosY = Gdx.graphics.getHeight()/2 / 2.5f;
		charPosY = Gdx.graphics.getHeight()/2 / 2.5f;
		platformY = Gdx.graphics.getHeight()/ 2 / 2.5f;

		distanceBetweenObstacles = Gdx.graphics.getWidth();
		originalObstacleVelocity = obstacleVelocity;

		//INITIAL FIRST RENDER OF THE OBSTACLES
		initObstacles();

		//GESTURE HANDLING
		Gdx.input.setInputProcessor(new SimpleDirectionGestureDetector(new SimpleDirectionGestureDetector.DirectionListener() {

			@Override
			public void onUp() {
				// TODO Auto-generated method stub
				if (jumpCounter == 1 && !isHighGravity) {
					jumpCounter++;
					velocity = -60; //WHEN PHONE IS TAPPED, CHARACTER IS JUMPED UP BY A VELOCITY OF 40.
					charPosY -= velocity;
				}

			}

			@Override
			public void onRight() {
				// TODO Auto-generated method stub

			}

			@Override
			public void onLeft() {
				// TODO Auto-generated method stub

			}

			@Override
			public void onDown() {
				// TODO Auto-generated method stub

			}

			@Override
			public void tap(){
				if(!isHighGravity && !isAntiGravity) {
					if (!isJumping && jumpCounter == 0 && gameState == 1) { //isTouched() METHOD ALSO COUNTS FOR LONG PRESSES, SO HOLDING DOWN ON THE SCREEN WOULD
						//CAUSE SPRITE TO INFINITELY RISE
						isJumping = true;
						jumpCounter++;
						velocity = -50; //WHEN PHONE IS TAPPED, CHARACTER IS JUMPED UP BY A VELOCITY OF 50.
						gravityFactor = 2.5f; //SPEED AND FACTOR IN WHICH CHARACTER IS PULLED DOWN. HIGHER THE NUMBER = STRONGER THE GRAVITY
					}
				}
				else {
					if (isHighGravity) { //IF HIGH GRAVITY MODE, DO THIS TAP
						velocity = -42; //WHEN PHONE IS TAPPED, CHARACTER IS JUMPED UP BY A VELOCITY OF 50.
						gravityFactor = 10; //SPEED AND FACTOR IN WHICH CHARACTER IS PULLED DOWN. HIGHER THE NUMBER = STRONGER THE GRAVITY
						isJumping = false;
						jumpCounter = 0;
					}
					if(isAntiGravity && !isJumping && jumpCounter == 0 && gameState == 1){ //IF LOW GRAVITY MODE, DO THIS TAP
						isJumping = true;
						jumpCounter++;
						velocity = -50; //WHEN PHONE IS TAPPED, CHARACTER IS JUMPED UP BY A VELOCITY OF 50.
						gravityFactor = 1; //SPEED AND FACTOR IN WHICH CHARACTER IS PULLED DOWN. HIGHER THE NUMBER = STRONGER THE GRAVITY
					}
				}
			}

		}));

//		Gdx.input.setInputProcessor(new SimpleGestureListener(new SimpleGestureListener.GestureListenerI(){
//			@Override
//			public void longPress(){
//				if(isJumping && isAntiGravity){
//					gravityFactor = 1.5f;
//				}
//			}
//		}));


		//SCHEDULED EXECUTOR. THIS IS TO EXECUTE A SPECIFIC FUNCTION(S) AT A SPECIFIC RATE.
		exec = Executors.newScheduledThreadPool(1);
		anomRunnable = new Runnable(){
			public void run(){
				setAnomaly();
			}
		};
		ScheduledFuture<?> handle = exec.scheduleAtFixedRate(anomRunnable, 10, 10, TimeUnit.SECONDS); //CALL ANOMALY FUNCTION EVERY 10 SECONDS.
	}

	@Override
	public void show() {

	}

	@Override
	public void render (float delta) {
		deltaTime += Gdx.graphics.getDeltaTime();
		distanceBetweenObstacles = floatRandomRange(Gdx.graphics.getWidth(), Gdx.graphics.getWidth() * 1.25f);


		game.batch.begin();
		game.batch.draw(background, 0-450, 0+250, Gdx.graphics.getWidth() * 1.5f, Gdx.graphics.getHeight());
		game.batch.draw(platform, 0, platformY - 560);
		game.batch.draw(animation.getKeyFrame(deltaTime, true), 0, charPosY); //GETS THE NEXT FRAME BASED ON THE CURRENT FRAME IN THE ANIMATION. 'TRUE' ALLOWS IT TO LOOP.

		if(Gdx.input.isTouched() && gameState == 0){
			gameState = 1;
		}
		if(gameState == 1) {
			//RANDOM OBSTACLE GENERATION
			if(isSuperSpeed && obstacleVelocity == originalObstacleVelocity){
				obstacleVelocity *= 1.25;
			}
			else{
				obstacleVelocity = originalObstacleVelocity;
			}

			if(isSuperSpeed){
				anomFont.draw(game.batch, "SPEED UP", Gdx.graphics.getWidth()/2 - 250, Gdx.graphics.getHeight()/2 + 300);
			}
			if(isHighGravity){
				anomFont.draw(game.batch, "HIGH GRAVITY", Gdx.graphics.getWidth()/2 - 250, Gdx.graphics.getHeight()/2 + 300);
			}
			if(isAntiGravity){
				anomFont.draw(game.batch, "LOW GRAVITY", Gdx.graphics.getWidth()/2 - 250, Gdx.graphics.getHeight()/2 + 300);
			}


			for(int i = 0; i < numberOfObstacles; i++) {
				if(typeOfObstacles.get(i) == 1){
					if (obstacleXPos[i] < -spike1.getWidth()) { //ONCE OBSTACLE GOES OFF THE SCREEN.
						typeOfObstacles.add(i, intRandomRange(1, 6));
						respawnObstacles(i);
					} else {
						obstacleXPos[i] -= obstacleVelocity;
					}
					obstacleXPos[i] -= obstacleVelocity;
					game.batch.draw(spike1, obstacleXPos[i], defaultCharPosY + 40);
					spikeHitbox = new Rectangle(obstacleXPos[i], defaultCharPosY + 40, spike1.getWidth(), spike1.getHeight());
				}
				if(typeOfObstacles.get(i) == 2){
					if (obstacleXPos[i] < -spike2.getWidth()) { //ONCE OBSTACLE GOES OFF THE SCREEN.
						typeOfObstacles.add(i, intRandomRange(1, 6));
						respawnObstacles(i);
					} else {
						obstacleXPos[i] -= obstacleVelocity;
					}
					obstacleXPos[i] -= obstacleVelocity;
					game.batch.draw(spike2, obstacleXPos[i], defaultCharPosY + 40);
					spikeHitbox = new Rectangle(obstacleXPos[i], defaultCharPosY + 40, spike2.getWidth(), spike2.getHeight());
				}
				if(typeOfObstacles.get(i) == 3){
					if (obstacleXPos[i] < -spike3.getWidth()) { //ONCE OBSTACLE GOES OFF THE SCREEN.
						typeOfObstacles.add(i, intRandomRange(1, 6));
						respawnObstacles(i);
					} else {
						obstacleXPos[i] -= obstacleVelocity;
					}
					obstacleXPos[i] -= obstacleVelocity;
					game.batch.draw(spike3, obstacleXPos[i], defaultCharPosY + 40);
					spikeHitbox = new Rectangle(obstacleXPos[i], defaultCharPosY + 40, spike3.getWidth(), spike3.getHeight());
				}
				if(typeOfObstacles.get(i) == 4) {
					if (obstacleXPos[i] < -pillar2.getWidth()) { //ONCE OBSTACLE GOES OFF THE SCREEN.
						typeOfObstacles.add(i, intRandomRange(1, 6));
						respawnObstacles(i);
					} else {
						obstacleXPos[i] -= obstacleVelocity;
					}
					obstacleXPos[i] -= obstacleVelocity;
					game.batch.draw(pillar2, obstacleXPos[i], defaultCharPosY + 40);
					spikeHitbox = new Rectangle(obstacleXPos[i], defaultCharPosY + 40, pillar2.getWidth(), pillar2.getHeight());
				}
				if(typeOfObstacles.get(i) == 5) {
					if (obstacleXPos[i] < -pillar3.getWidth()) { //ONCE OBSTACLE GOES OFF THE SCREEN.
						typeOfObstacles.add(i, intRandomRange(1, 6));
						respawnObstacles(i);
					} else {
						obstacleXPos[i] -= obstacleVelocity;
					}
					obstacleXPos[i] -= obstacleVelocity;
					game.batch.draw(pillar3, obstacleXPos[i], defaultCharPosY + 40);
					spikeHitbox = new Rectangle(obstacleXPos[i], defaultCharPosY + 40, pillar3.getWidth(), pillar3.getHeight());
				}
				if(typeOfObstacles.get(i) == 6) {
					if (obstacleXPos[i] < -pillar6.getWidth()) { //ONCE OBSTACLE GOES OFF THE SCREEN.
						typeOfObstacles.add(i, intRandomRange(1, 6));
						respawnObstacles(i);
					} else {
						obstacleXPos[i] -= obstacleVelocity;
					}
					obstacleXPos[i] -= obstacleVelocity;
					game.batch.draw(pillar6, obstacleXPos[i], defaultCharPosY + 40);
					spikeHitbox = new Rectangle(obstacleXPos[i], defaultCharPosY + 40, pillar6.getWidth(), pillar6.getHeight());
				}
			}

			if (charPosY > Gdx.graphics.getHeight() / 2 / 2.5f || velocity <= 0) {
				velocity += gravityFactor; //DECREASES THE VELOCITY EVERY ITERATION OF RENDER() BEING CALLED
				charPosY -= velocity; //WHEN VELOCITY BECOMES POSITIVE, THE charPosY DECREASES.

				if (charPosY <= defaultCharPosY) {
					charPosY = defaultCharPosY;
					isJumping = false; //THE isJumping FLAG PREVENTS THE USER FROM JUMPING WHILE THE CHARACTER IS ALREADY IN THE AIR.
					jumpCounter = 0; //COUNTER KEEPS TRACK OF JUMPS
				}
			}

			if(charPosY > Gdx.graphics.getHeight()){
				game.batch.draw(overArrow, 30, Gdx.graphics.getHeight() - overArrow.getHeight());
				overScreenFont.draw(game.batch, "+" + Integer.toString(java.lang.Math.round(charPosY - Gdx.graphics.getHeight())) + "m", 0, Gdx.graphics.getHeight() - overArrow.getHeight() - 10);
			}


			ninjaHitbox.set(0 + animation.getKeyFrame(deltaTime).getRegionWidth()/3.5f, charPosY + 36, animation.getKeyFrame(deltaTime).getRegionWidth()/1.5f ,animation.getKeyFrame(deltaTime).getRegionHeight()/ 1.2f);

			for(int i = 0; i < numberOfObstacles; i++) {
				if(ninjaHitbox != null && spikeHitbox != null && Intersector.overlaps(ninjaHitbox, spikeHitbox)){
					gameState = 2; //GAMESTATE = 2 INDICATES THAT THE GAME IS OVER
					if(score > getHighScore()) {
						setHighScore((int) score);
					}
				}
			}

			//SCORE MANIPULATION
			if (score > 0 && score < 30) {
				score = score + (score / 6 * obstacleVelocity * .006f);
				obstacleVelocity = 4;
				originalObstacleVelocity = obstacleVelocity;
			}
			if (score > 30 && score < 100) {
				score = score + (score / 4 * obstacleVelocity * .0009f);
				obstacleVelocity = 6;
				originalObstacleVelocity = obstacleVelocity;
			}
			if (score > 100 && score < 1000) {
				score = score + (50 * obstacleVelocity * .0004f);
				obstacleVelocity = 9;
				originalObstacleVelocity = obstacleVelocity;
			}
			//SCORE MULTIPLIER PEAKS
			if (score > 1000) {
				score = score + (75 * obstacleVelocity * .0005f);
				obstacleVelocity = 10;
				originalObstacleVelocity = obstacleVelocity;
			}
			int intScore = (int) score;
			Gdx.app.log("Score", Integer.toString(intScore));
			scoreFont.draw(game.batch, String.valueOf(intScore), Gdx.graphics.getWidth()/2 - 25, Gdx.graphics.getHeight()/2 + 400);

		}

		else if(gameState == 0){
			scoreFont.draw(game.batch, "Tap to start", Gdx.graphics.getWidth()/5 - 30, Gdx.graphics.getHeight()/2 + 400);
			if(Gdx.input.isTouched()){
				gameState = 1;
			}
		}
		else if(gameState == 2){ //WHEN THE GAME STATE IS 2, THE GAME IS OVER AND SHOULD BE RESET IF RE-TAPPED.
			gameOverFont.draw(game.batch, "GAME OVER \n" + "Score: " + String.valueOf((int)score) + "\nHighscore: " + getHighScore(), Gdx.graphics.getWidth()/16, Gdx.graphics.getHeight()/6);
			if(Gdx.input.isTouched()) {
				resetGameScreen();
			}
		}
		game.batch.end();
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
		background.dispose();
		platform.dispose();
		ninjaboy.dispose();
		spike1.dispose();
		spike2.dispose();
		spike3.dispose();
		pillar2.dispose();
		pillar3.dispose();
		pillar6.dispose();
		spikeHitbox = null;
		scoreFont.dispose();
		gameOverFont.dispose();
		anomFont.dispose();
	}

	float floatRandomRange(float min, float max){
		float range = (max - min) + 1;
		return (float) (Math.random() * range) + min;
	}

	int intRandomRange(int min, int max){
		int range = (max - min) + 1;
		return (int) (Math.random() * range) + min;
	}

	private void respawnObstacles(int i){
		if(typeOfObstacles.get(i) == 1) {
			obstacleXPos[i] += numberOfObstacles * distanceBetweenObstacles + spike1.getWidth();
		}
		if(typeOfObstacles.get(i) == 2) {
			obstacleXPos[i] += numberOfObstacles * distanceBetweenObstacles + spike2.getWidth();
		}
		if(typeOfObstacles.get(i) == 3) {
			obstacleXPos[i] += numberOfObstacles * distanceBetweenObstacles + spike3.getWidth();
		}
		if(typeOfObstacles.get(i) == 4) {
			obstacleXPos[i] += numberOfObstacles * distanceBetweenObstacles + spike1.getWidth();
		}
		if(typeOfObstacles.get(i) == 5) {
			obstacleXPos[i] += numberOfObstacles * distanceBetweenObstacles + spike2.getWidth();
		}
		if(typeOfObstacles.get(i) == 6) {
			obstacleXPos[i] += numberOfObstacles * distanceBetweenObstacles + spike3.getWidth();
		}
	}

	private void initObstacles(){
		for(int i = 0; i < numberOfObstacles; i++) {
			int typeOfObstacle = intRandomRange(1, 6);
			typeOfObstacles.add(typeOfObstacle);
			if (typeOfObstacle == 1) { //IF THE OBSTACLE IS SPIKE_1
				obstacleXPos[i] = Gdx.graphics.getWidth() / 2 - spike3.getWidth() / 2 + Gdx.graphics.getWidth() + i * distanceBetweenObstacles; //RENDER THE INITIAL ROW OF OBSTACLES, WITH THE ONES THAT ARE OFFSCREEN.
				spikeHitbox = new Rectangle(obstacleXPos[i], defaultCharPosY + 40, spike1.getWidth(), spike1.getHeight());
			}
			if (typeOfObstacle == 2) { //IF THE OBSTACLE IS SPIKE_2
				obstacleXPos[i] = Gdx.graphics.getWidth() / 2 - spike3.getWidth() / 2 + Gdx.graphics.getWidth() + i * distanceBetweenObstacles; //RENDER THE INITIAL ROW OF OBSTACLES, WITH THE ONES THAT ARE OFFSCREEN.
				spikeHitbox = new Rectangle(obstacleXPos[i], defaultCharPosY + 40, spike2.getWidth(), spike2.getHeight());
			}
			if (typeOfObstacle == 3) { //IF THE OBSTACLE IS SPIKE_3
				obstacleXPos[i] = Gdx.graphics.getWidth() / 2 - spike3.getWidth() / 2 + Gdx.graphics.getWidth() + i * distanceBetweenObstacles; //RENDER THE INITIAL ROW OF OBSTACLES, WITH THE ONES THAT ARE OFFSCREEN.
				spikeHitbox = new Rectangle(obstacleXPos[i], defaultCharPosY + 40, spike3.getWidth(), spike3.getHeight());
			}
			if (typeOfObstacle == 4) { //IF THE OBSTACLE IS PILLAR_2
				obstacleXPos[i] = Gdx.graphics.getWidth() / 2 - spike3.getWidth() / 2 + Gdx.graphics.getWidth() + i * distanceBetweenObstacles; //RENDER THE INITIAL ROW OF OBSTACLES, WITH THE ONES THAT ARE OFFSCREEN.
				pillarHitbox = new Rectangle(obstacleXPos[i], defaultCharPosY + 40, pillar2.getWidth(), pillar2.getHeight());
			}
			if (typeOfObstacle == 5) { //IF THE OBSTACLE IS PILLAR_3
				obstacleXPos[i] = Gdx.graphics.getWidth() / 2 - spike3.getWidth() / 2 + Gdx.graphics.getWidth() + i * distanceBetweenObstacles; //RENDER THE INITIAL ROW OF OBSTACLES, WITH THE ONES THAT ARE OFFSCREEN.
				pillarHitbox = new Rectangle(obstacleXPos[i], defaultCharPosY + 40, pillar3.getWidth(), pillar3.getHeight());
			}
			if (typeOfObstacle == 6) { //IF THE OBSTACLE IS PILLAR_6
				obstacleXPos[i] = Gdx.graphics.getWidth() / 2 - spike3.getWidth() / 2 + Gdx.graphics.getWidth() + i * distanceBetweenObstacles; //RENDER THE INITIAL ROW OF OBSTACLES, WITH THE ONES THAT ARE OFFSCREEN.
				pillarHitbox = new Rectangle(obstacleXPos[i], defaultCharPosY + 40, pillar6.getWidth(), pillar6.getHeight());
			}
		}
	}

	private void resetGameScreen(){
		score = 1;
		obstacleXPos[0] = Gdx.graphics.getWidth() / 2 - spike3.getWidth() / 2 + Gdx.graphics.getWidth() + 0 * distanceBetweenObstacles;
		charPosY = defaultCharPosY;
		typeOfObstacles.remove(0);
		spikeHitbox = null;
		initObstacles();
		gameState = 0;
		isJumping = false;
		jumpCounter = 0;
		isHighGravity = false;
		isSuperSpeed = false;
		isAntiGravity = false;
		previousAnomaly = 0;
		gravityFactor = 2.5f;
	}

	public static void setHighScore(int hs){
		prefs.putInteger("highScore", hs);
		prefs.flush();
	}
	public static int getHighScore(){
		return prefs.getInteger("highScore");
	}


	public void setAnomaly(){
		if(score > 200) { //ANOMALIES TO HAPPEN AFTER A SCORE OF 200 IS REACHED
			int anomaly;
			anomaly = intRandomRange(0, 3);

			//DO NOT WANT 2 ANOMALIES TO HAPPEN BACK TO BACK
			if (previousAnomaly != 0) {
				anomaly = 0;
			}

			switch (anomaly) {
				//default case
				case 0:
					isHighGravity = false;
					isSuperSpeed = false;
					isAntiGravity = false;
					previousAnomaly = 0;
					break;
				//highSpeed
				case 1:
					isHighGravity = false;
					isSuperSpeed = true;
					isAntiGravity = false;
					previousAnomaly = 1;
					break;
				//highGravity
				case 2:
					isHighGravity = true;
					isSuperSpeed = false;
					isAntiGravity = false;
					previousAnomaly = 2;
					break;
				//antiGravity
				case 3:
					isHighGravity = false;
					isSuperSpeed = false;
					isAntiGravity = true;
					previousAnomaly = 3;
			}
		}

		return;
	}

	private void initFonts(){
		FreeTypeFontGenerator scoreFontGen = new FreeTypeFontGenerator(Gdx.files.internal("fonts/silkscreen.ttf"));
		FreeTypeFontGenerator gameOverFontGen = new FreeTypeFontGenerator(Gdx.files.internal("fonts/silkscreen.ttf"));
		FreeTypeFontGenerator anomFontGen = new FreeTypeFontGenerator(Gdx.files.internal("fonts/silkscreen.ttf"));
		FreeTypeFontGenerator overScreenFontGen = new FreeTypeFontGenerator(Gdx.files.internal("fonts/silkscreen.ttf"));
		FreeTypeFontGenerator.FreeTypeFontParameter scoreParam = new FreeTypeFontGenerator.FreeTypeFontParameter();
		FreeTypeFontGenerator.FreeTypeFontParameter gameOverParam = new FreeTypeFontGenerator.FreeTypeFontParameter();
		FreeTypeFontGenerator.FreeTypeFontParameter anomParam = new FreeTypeFontGenerator.FreeTypeFontParameter();
		FreeTypeFontGenerator.FreeTypeFontParameter overScreenParam = new FreeTypeFontGenerator.FreeTypeFontParameter();

		scoreParam.size = 100;
		scoreParam.color = Color.BLACK;
		gameOverParam.size = 100;
		gameOverParam.color = Color.WHITE;
		anomParam.size = 100;
		anomParam.color = Color.BLACK;
		overScreenParam.size = 50;
		overScreenParam.color = Color.BLACK;

		scoreFont = scoreFontGen.generateFont(scoreParam);
		gameOverFont = gameOverFontGen.generateFont(gameOverParam);
		anomFont = anomFontGen.generateFont(anomParam);
		overScreenFont = overScreenFontGen.generateFont(overScreenParam);
	}
}
