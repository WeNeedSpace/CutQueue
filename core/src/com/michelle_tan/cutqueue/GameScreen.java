package com.michelle_tan.cutqueue;

import java.util.ArrayList;
import java.util.Random;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.viewport.ScalingViewport;

public class GameScreen implements Screen, ApplicationListener, InputProcessor{

	private CutQueue game;

	//camera and view
	private OrthographicCamera camera;

	//game-related
	private Skin skin;	
	private Stage stage;
	private SpriteBatch batch;
	private Level level;

	CustomLabel title; //to display title of screen
	CustomLabel coinButton;
	CustomLabel timerButton;

	//for music and sounds
	Music bgmGame;
	Sound sfxCharPickup;
	Sound sfxCharThrow;
	Sound sfxGrandmaHit;
	Sound sfxButton;

	//sound button
	AtlasRegion btn_sound_on;
	AtlasRegion btn_sound_off;
	InputListener input_sound;
	Image soundButton;

	//for character textures
	private TextureRegion[] regions = new TextureRegion[17]; // #2
	private Texture texture;
	private enum Character {
		BOY, 
		GIRL,
		GRANDMA,
		BOY_FALL, 
		GIRL_FALL, 
		GRANDMA_FALL,
		GRANDMA_FIGHT, 
		GRANDMA_TIRED
	}

	//for character variables
	private final int MAX_CHARS = 5; //max chars allowed on screen
	private int numChars; 
	private int[] charTypes;
	private int lastCharSpawned;
	private int position;
	private int numBoss;
	private boolean hasBonus;
	private ArrayList<Image> charsSpawned;

	//for buttons
	private BitmapFont font;
	private static int btnW = Global.BUTTON_WIDTH;
	private static int btnH = Global.BUTTON_HEIGHT;

	//This height for dialog fits better
	//for dialogs
	private static int dialogW = Global.SCREEN_WIDTH;
	private static int dialogH = Global.SCREEN_HEIGHT;

	Dialog pauseDialog;

	//for swipe gesture
	private final int SWIPE_THRESHOLD = 100; //how long the swipe must be before detection
	private boolean panning;
	private float firstX;
	private float firstY;
	private float newX;
	private float newY;
	private ActorGestureListener swipe;
	private ActorGestureListener swipeGranny;

	//coins
	private int coins= 0;

	//timer
	private int clock;
	private Timer timer;
	private Timer.Task task;

	//FOR STATE OF GAME
	public enum State
	{
		PAUSE,
		RUN,
		BONUS,
		STOPPED
	}

	private State state = State.RUN;
	private boolean playerWin = false;
	private boolean gameRunning = false;

	//For backgrounds
	private Texture bg;
	private Image bottom;
	private Texture train;
	private Image doors;
	private Boolean doorsDrawn = false;

	//for back button
	InputMultiplexer input;

	//constructor
	public GameScreen(CutQueue g){
		this.game = g;
		create();
	}

	public void create(){

		batch = new SpriteBatch();
		//stage = new Stage();

		level = Global.LEVELS[Global.currLevel];

		font = new BitmapFont(Gdx.files.internal("chewy_tryagain.fnt"));

		camera = new OrthographicCamera(Global.SCREEN_WIDTH, Global.SCREEN_HEIGHT);
		camera.setToOrtho(false, Global.SCREEN_WIDTH, Global.SCREEN_HEIGHT);
		camera.position.set(Global.SCREEN_WIDTH/2, Global.SCREEN_HEIGHT/2, 0f); 
		stage = new Stage(new ScalingViewport(Scaling.fill, Global.SCREEN_WIDTH, Global.SCREEN_HEIGHT, camera), batch);

		input = new InputMultiplexer(this, stage);

		//SETUP MUSIC
		bgmGame = game.assets.get("bgm_game.mp3", Music.class);
		sfxCharPickup = game.assets.get("sfx_char_pickup.mp3", Sound.class);
		sfxCharThrow = game.assets.get("sfx_char_throw.mp3", Sound.class);
		sfxGrandmaHit = game.assets.get("sfx_grandma_hit.mp3", Sound.class);
		sfxButton = game.assets.get("sfx_button.mp3", Sound.class);

		//SETUP BACKGROUND
		bg = new Texture(Gdx.files.internal("bottom_game.png"));
		train = new Texture(Gdx.files.internal("train_doors.png"));

		bottom = new Image(bg);
		bottom.setScale(Global.SCREEN_WIDTH/bottom.getWidth());
		bottom.setPosition(0,0);
		stage.addActor(bottom);

		doors = new Image(train);
		doors.setScale(Global.SCREEN_WIDTH/doors.getWidth()); 
		float off = doors.getWidth()/2*doors.getScaleX(); 
		doors.setPosition(Global.SCREEN_WIDTH/2-off, (float) (Global.SCREEN_HEIGHT*(0.75)));
		doors.setVisible(false);
		doors.addAction(Actions.alpha(0));
		stage.addActor(doors);

		//SETUP TIMER
		timer = new Timer();
		task = new Timer.Task() {
			@Override
			public void run() {
				System.out.println("timer " +clock);
				if(clock == 0) { //when time runs out
					System.out.println("timer end");
					state = State.STOPPED;
					timer.stop();
					playerWin = false;
					endGame();
				}
				else {
					if(state == State.RUN){ //while game is running
						clock = clock - 1; //timer decreases
						//timerButton.setText(""+clock);
					}
				}
			}

		};

		//SETUP BUTTONS

		skin = new Skin();

		// Generate a 1x1 white texture and store it in the skin named "white".
		Pixmap pixmap = new Pixmap(100, 100, Format.RGBA8888);
		pixmap.setColor(Color.WHITE);
		pixmap.fill();

		skin.add("white", new Texture(pixmap));

		font.scale(Global.FONT_SCALE);
		skin.add("default",font);
		
		//SETUP PAUSE DIALOG
		pauseDialog = new CustomDialog(""){
			{
				text("GAME PAUSED!");
				resumeButton(true);
				this.getButtonTable().row();
				mainMenuButton(false);
			}

			@Override
			protected void result(Object object){
				if((Boolean) object){
					resume();
				}else{
					game.setScreen(game.mainMenuScreen);
				}
			}
		};

		// Create a TextureAtlas for the UI elements
		TextureAtlas atlas;
		atlas = game.assets.get("ui.txt", TextureAtlas.class);
		final AtlasRegion btn_pause = atlas.findRegion("btn_pause");
		final AtlasRegion btn_pause_t = atlas.findRegion("btn_pause_t");
		final AtlasRegion header = atlas.findRegion("header");
		final AtlasRegion game_display = atlas.findRegion("game_display");
		btn_sound_on = atlas.findRegion("btn_music");
		btn_sound_off = atlas.findRegion("btn_music_off");

		final Image bg_header = new Image(header);
		bg_header.setWidth(Global.SCREEN_WIDTH);
		bg_header.setHeight(Global.SCREEN_HEIGHT/800*bg_header.getHeight()+10);
		bg_header.setPosition(0, Global.SCREEN_HEIGHT - bg_header.getHeight());
		bg_header.setTouchable(Touchable.disabled);

		final Image bg_display = new Image(game_display);
		bg_display.setScale(Global.SCREEN_SCALE);
		bg_display.setPosition(Global.SCREEN_WIDTH - Global.SCREEN_SCALE*bg_display.getWidth(), Global.SCREEN_HEIGHT - bg_header.getHeight() + Global.SCREEN_SCALE*4- Global.SCREEN_SCALE*bg_display.getHeight());
		bg_display.setTouchable(Touchable.disabled);

		stage.addActor(bg_display);
		stage.addActor(bg_header);

		final Image pauseButton = new Image(btn_pause);
		pauseButton.setScale(Global.SCREEN_SCALE);
		pauseButton.setPosition(Global.SCREEN_WIDTH/16 - pauseButton.getWidth()/2, Global.SCREEN_HEIGHT/16*15 - Global.SCREEN_SCALE*pauseButton.getHeight()/2);
		stage.addActor(pauseButton);
		pauseButton.addListener(new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				System.out.println("pause clicked");
				pauseButton.setDrawable(new TextureRegionDrawable(btn_pause_t));
				sfxButton.play(Global.sfxVolume);
				return true;
			}

			@Override
			public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
				pauseButton.setDrawable(new TextureRegionDrawable(btn_pause));
				pause();
			}

		});

		//SETUP SOUND BUTTON LISTENER
		//input listeners for sound on and off
		input_sound = new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				System.out.println("sound on clicked");
				return true;
			}

			@Override
			public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
				if (Global.sound) { //on
					soundButton.setDrawable(new TextureRegionDrawable(btn_sound_off));
					//sound off
					Global.offSound();
				} else { //off
					soundButton.setDrawable(new TextureRegionDrawable(btn_sound_on));
					//on sound
					Global.onSound();
					sfxButton.play(Global.sfxVolume);
				}
				bgmGame.setVolume(Global.volume);
			}

		};

		soundButton = new Image(btn_sound_on);
		soundButton.setScale(Global.SCREEN_SCALE);
		soundButton.setPosition(Global.SCREEN_WIDTH - Global.SCREEN_SCALE*(soundButton.getWidth()+15), Global.SCREEN_HEIGHT - Global.SCREEN_SCALE*(soundButton.getHeight()+15));
		stage.addActor(soundButton);
		soundButton.addListener(input_sound);

		//SETUP SWIPE GESTURE	
		swipe = new ActorGestureListener() {
			Image fall;
			Image front;
			@Override
			public void pan(InputEvent event, float x, float y, float deltaX, float deltaY) {
				Actor actor = event.getListenerActor();

				//sets position when it starts panning
				if (!panning) {
					//play sound when first touched
					sfxCharPickup.play(Global.sfxVolume);
					panning = true;
					firstX = actor.getX();
					firstY= actor.getY();

				}

				//makes character move with touch
				Vector3 touchPos = new Vector3();
				touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
				camera.unproject(touchPos); //converts coordinates to game world coor
				actor.setPosition(touchPos.x - actor.getWidth()/2*actor.getScaleX(), touchPos.y - actor.getHeight()/2*actor.getScaleY());

			}

			@Override
			public void touchDown(InputEvent event, float x, float y, int pointer, int button) {
				//handled
			}

			@Override
			public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
				panning = false;

				Actor chara = event.getListenerActor();

				newX = chara.getX();
				newY = chara.getY();

				//difference between original and release position 
				if (Math.abs(newX - firstX) >= SWIPE_THRESHOLD) {

					//GRANNY's MOVEMENTS
					if(Integer.valueOf(chara.getName())==12){

						TextureRegion tex = regions[13]; 

						front = new Image(tex);
						front.setName("14");
						front.setScale(chara.getScaleX());
						float off = front.getWidth()/2*front.getScaleX(); 
						front.setPosition(Global.SCREEN_WIDTH/2 - off, 
								(float) (Global.SCREEN_HEIGHT*(-0.05)));
						front.addListener(swipeGranny);
						chara.setTouchable(Touchable.disabled);
						chara.remove();

						stage.addActor(front);

						System.out.println ("create granny");

						//OTHER CHARACTERS' MOVEMENTS
					}else{
						//play falling sound for normal chars
						sfxCharThrow.play(Global.sfxVolume);

						if (newX > firstX) { //right swipe

							System.out.println("right swipe");	

							//creates falling character
							TextureRegion tex = regions[Integer.valueOf(chara.getName())+2]; 

							//get whether its boy or girl

							fall = new Image(tex);
							fall.setScale(chara.getScaleX());
							fall.setOrigin(fall.getWidth()/2, fall.getHeight()/2);
							fall.setPosition(newX + fall.getHeight()/2*chara.getScaleX(),
									newY+ fall.getWidth()/2*chara.getScaleX());

							fall.addAction(Actions.moveTo(Global.SCREEN_WIDTH 
									+ fall.getWidth()*chara.getScaleX(), 
									- fall.getHeight()*chara.getScaleX(), 0.7f));
							chara.remove();

							stage.addActor(fall);



						} else if (x < firstX) { //left swipe

							System.out.println("left swipe");
							//creates falling character

							TextureRegion tex = regions[Integer.valueOf(chara.getName())+1]; 

							//get whether its boy or girl
							fall = new Image(tex);
							fall.setScale(chara.getScaleX());
							fall.setOrigin(fall.getWidth()/2, fall.getHeight()/2);
							fall.setPosition(newX + fall.getHeight()/2*chara.getScaleX(),
									newY+ fall.getWidth()/2*chara.getScaleX());

							fall.addAction(Actions.moveTo(-fall.getWidth()*chara.getScaleX(), 
									-fall.getHeight()*chara.getScaleX(), 0.7f));
							chara.remove();

							stage.addActor(fall);



						}
						//after it's been swiped away
						//delete falling chara
						float delay = 0.7f; // seconds after it's been swiped

						Timer.schedule(new Timer.Task(){
							@Override
							public void run() {
								fall.remove();

							}
						}, delay);
						if(Integer.valueOf(chara.getName())>=14){
							coins +=35;
						}else{
							coins +=10;
						}


						updatePosition();
					}
				}else{
					float off = chara.getWidth()/2*chara.getScaleX(); 
					chara.addAction(Actions.moveTo(Global.SCREEN_WIDTH/2 - off, 
							(float) (Global.SCREEN_HEIGHT*(-0.05))));
				}
			}
		};

		//SETUP granny gesture!
		swipeGranny = new ActorGestureListener() {
			int k=0;
			float scale;
			float initX;
			float initY;
			Image front;
			@Override
			public void touchDown(InputEvent event, float x, float y, int pointer, int button) {
				//handled
				sfxGrandmaHit.play(Global.sfxVolume); //play sound when granny is touched
				System.out.println ("touching granny");
			}

			@Override
			public void touchUp(InputEvent event, float x, float y, int pointer, int button) {

				final Actor chara = event.getListenerActor();
				k++;
				System.out.println("key: "+k);
				scale = chara.getScaleX();
				initX = chara.getX();
				initY = chara.getY();

				if(k==10){
					//granny is tired.
					//swipe her away~
					TextureRegion tex = regions[14]; 

					front = new Image(tex);
					front.setName("14");
					front.setScale(chara.getScaleX());
					float off = front.getWidth()/2*front.getScaleX(); 
					front.setPosition(Global.SCREEN_WIDTH/2 - off, 
							(float) (Global.SCREEN_HEIGHT*(-0.05)));
					front.addListener(swipe);
					chara.setTouchable(Touchable.disabled);
					chara.remove();

					stage.addActor(front);

					k=0;

				}
				else{
					//granny grows big and back to normal 
					chara.addAction(Actions.scaleBy(Global.ASPECT_RATIO, Global.ASPECT_RATIO,0.1f));
					float off = chara.getWidth()/2*Global.ASPECT_RATIO;
					float off2 = chara.getHeight()/2*Global.ASPECT_RATIO;
					chara.addAction(Actions.moveBy(-off, off2, 0.1f));
					chara.addAction(Actions.scaleBy(-Global.ASPECT_RATIO, -Global.ASPECT_RATIO,0.1f));
					chara.addAction(Actions.moveTo(initX, initY, 0.1f));
				}

			}
		};

		//SETUP CHARACTERS
		//Get all the characters from sprite sheet
		TextureAtlas atlasChar = game.assets.get("chars.txt", TextureAtlas.class);

		regions[0] = atlasChar.findRegion("boy");      // boy
		regions[1] = atlasChar.findRegion("boy_fall_left");		// boy fall left
		regions[2] = atlasChar.findRegion("boy_fall_right");    // boy fall right

		regions[3] = atlasChar.findRegion("girl");    // girl
		regions[4] = atlasChar.findRegion("girl_fall_left");    // girl fall left   
		regions[5] = atlasChar.findRegion("girl_fall_right");    // girl fall right

		regions[6] = atlasChar.findRegion("boy2");      // school boy
		regions[7] = atlasChar.findRegion("boy2_fall_left");		// school boy fall left
		regions[8] = atlasChar.findRegion("boy2_fall_right");    // school boy fall right

		regions[9] = atlasChar.findRegion("girl2");     // school girl
		regions[10] = atlasChar.findRegion("girl2_fall_left");    // school girl fall left   
		regions[11] = atlasChar.findRegion("girl2_fall_right");    // school girl fall right

		regions[12] = atlasChar.findRegion("granny");		//granny				
		regions[13] = atlasChar.findRegion("granny_front");		//granny front
		regions[14] = atlasChar.findRegion("granny_lose");	 //granny tired
		regions[15] = atlasChar.findRegion("granny_fall_left");	 //granny fall left	
		regions[16] = atlasChar.findRegion("granny_fall_right");	 //granny fall right


		//screen title
		title = new CustomLabel(level.getLevelName(), new Label.LabelStyle(font, Color.WHITE));
		font.setScale(2);
		title.setPosition(Global.SCREEN_WIDTH/2 - title.getWidth(), Global.SCREEN_HEIGHT/13*12);
		stage.addActor(title);
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0.26f, 0.26f, 0.26f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		// tell the camera to update its matrices
		camera.update();

		batch.setProjectionMatrix(camera.combined);

		font.setScale(Global.FONT_SCALE*2);
		stage.draw();

		stage.act(Gdx.graphics.getDeltaTime());
		Table.drawDebug(stage);

		batch.begin();
		font.setColor(Color.ORANGE);
		font.setScale(Global.FONT_SCALE*1.2f);
		font.draw(batch, ""+clock, Global.SCREEN_WIDTH/7*6, Global.SCREEN_HEIGHT/15*13);
		font.draw(batch, ""+coins, Global.SCREEN_WIDTH/14*8, Global.SCREEN_HEIGHT/15*13);
		batch.end();

	}

	@Override
	public void resize(int width, int height) {

	}

	@Override
	public void show() {
		Gdx.input.setInputProcessor(input);
		Gdx.input.setCatchBackKey(true);
		System.out.println("show game");

		game.stopMusic();

		if (Global.sound) { //on
			soundButton.setDrawable(new TextureRegionDrawable(btn_sound_on));
			Global.onSound();
		} else { //off
			soundButton.setDrawable(new TextureRegionDrawable(btn_sound_off));
			Global.offSound();
		}

		bgmGame.setVolume(Global.volume);
		bgmGame.play();

		if (pauseDialog.isVisible()) {
			pauseDialog.hide();
		}


		if(Global.getPlayer().getLevelProgress()==0&&!Global.getFlag()){ 
			//first time and haven't run through tutorial
			Global.setFlag(true);
			game.setScreen(game.tutorialScreen);

			//if coming from bonus
		}else if(Global.comingFromBonus()){
			//if coming from bonus stage, add score and resume game
			coins+=Global.getBonusScore();
			Global.setComingFromBonus(false);

			create();
			Gdx.input.setInputProcessor(stage);
			System.out.println("show game");

			if (pauseDialog.isVisible()) {
				pauseDialog.hide();
			}

			//reset level vars
			level = Global.LEVELS[Global.currLevel];
			title.updateText(level.getLevelName());
			numChars = level.getSpawnAmount();
			numBoss = level.getBossAmount();
			hasBonus = false;

			//from global
			charsSpawned = Global.getOldCharArray(); 		

			//from previous settings
			lastCharSpawned = numChars;
			position = 4;

			//add characters to stage, smallest one first
			for(int j=numChars-1; j>=numChars-4;j--){
				Image charac = charsSpawned.get(j);
				stage.addActor(charac); //add to stage
			}	

			//run game after inital chars are created
			clock =  Global.getOldClock();//resets timer to level time where it stopped
			timer.scheduleTask(task, 0.5f, 1); //will run every 1 sec until cancelled in hide()

			timer.start();
			System.out.println("timer start");
			state = State.RUN;
			updatePosition();



			//normal showing of the game screen 
		}else{
			create();
			Gdx.input.setInputProcessor(stage);
			System.out.println("show game");

			if (pauseDialog.isVisible()) {
				pauseDialog.hide();
			}

			//reset level vars
			level = Global.LEVELS[Global.currLevel];
			title.updateText(level.getLevelName());
			numChars = level.getSpawnAmount();
			numBoss = level.getBossAmount();
			hasBonus = level.getBonusStage();

			charTypes = new int[numChars]; //new array for chartypes
			charsSpawned = new ArrayList<Image>(); 	//new array for characters
			lastCharSpawned = 0;


			//randomise grannies into the array
			Random generator = new Random(); 
			if(numBoss!=0){
				for (int i=0; i<numBoss; i++) {
					int r = generator.nextInt(numChars);
					charTypes[r] = 12; //random int between 0-end of array
					System.out.println(r +"where granny at");
				}
			}

			//randomise chars into the rest of the array
			for (int i=0; i<numChars; i++) {
				if(charTypes[i] !=12){
					int r = generator.nextInt(4);
					charTypes[i] = r*3; //random int (0,3,6 or 9) >> for each of the chars
					System.out.println(r*3 +"random num");

				}else{
					//if it's already filled with the granny

				}
			}

			position = 5; //to set scale, 5 is biggest

			//create characters
			for (int i=0; i<MAX_CHARS; i++) { //for the max no of chars on screen which is 5
				TextureRegion tex = regions[charTypes[i]]; //gets random int from charTypes
				Image img = new Image(tex);
				img.setName(""+charTypes[i]);
				img.setScale(Global.ASPECT_RATIO*1f*position+ Global.ASPECT_RATIO*3); 
				float off = img.getWidth()/2*img.getScaleX(); 
				img.setPosition(Global.SCREEN_WIDTH/2 - off, (float) (Global.SCREEN_HEIGHT*(0.55) - ((position-1)*Global.SCREEN_HEIGHT*0.15)));
				img.addListener(swipe);
				if (i==0) { //make the first char touchable and bring to front
					img.setTouchable(Touchable.enabled);
				} else {
					img.setTouchable(Touchable.disabled);	
				}

				charsSpawned.add(img); //add to array
				position--;
				lastCharSpawned++;
			}

			//add characters to stage, smallest one first
			position = 4;
			for(int j=charsSpawned.size()-1; j>=0;j--){
				Image charac = charsSpawned.get(j);
				stage.addActor(charac); //add to stage
			}

			//run game after inital chars are created
			clock = level.getTimeLimit(); //resets timer to level time
			timer.scheduleTask(task, 0.5f, 1); //will run every 1 sec until cancelled in hide()

			//timer above delaysec changed to 0.5s

			timer.start();
			System.out.println("timer start");
			state = State.RUN;

		}

	}

	@Override
	public void hide() {
		Gdx.input.setInputProcessor(null);
		System.out.println("hide game");

		bgmGame.stop();
		game.playMusic();

		if (pauseDialog.isVisible()) {
			pauseDialog.hide();
		}

		//if bonus, save stuff
		if(state == State.BONUS){
			Global.setComingFromBonus(true);
			Global.setBonusScore(coins);
			Global.setOldCharArray(charsSpawned);
			Global.setOldClock(clock);

		}

		clock = Integer.MAX_VALUE; 
		timer.stop();
		timer.clear();
		coins = 0;
		doorsDrawn = false;
		/*
		 * This should fix the door issue.
		 * 
		 */

		doors.setScale(Global.SCREEN_WIDTH/doors.getWidth()); 
		float off = doors.getWidth()/2*doors.getScaleX(); 
		doors.setPosition(Global.SCREEN_WIDTH/2-off, (float) (Global.SCREEN_HEIGHT*(0.75)));
		doors.setVisible(false);

		System.out.println("timer stop");
		//clear previous stage
		clearCharacters();
		//dispose();

	}

	@Override
	public void pause() {
		//for time
		state = State.PAUSE;
		timer.stop();

		//for dialog calling. 
		switch(state){
		case BONUS:
			game.setScreen(game.bonusStageScreen);
			break;
		default:
			pauseDialog.show(stage);	
			break;
		}

	}

	@Override
	public void resume() {
		if (pauseDialog.isVisible()) {
			pauseDialog.hide();
		}

		state = State.RUN;
		timer.start();
	}

	@Override
	public void dispose() {
		batch.dispose();
		stage.dispose();
	}

	public void createCharacter(){
		TextureRegion tex = regions[charTypes[lastCharSpawned]]; //gets random int from charTypes
		Image img = new Image(tex);
		img.setName(""+charTypes[lastCharSpawned]);
		img.setScale(Global.ASPECT_RATIO*1f+ Global.ASPECT_RATIO*3); 
		float off = img.getWidth()/2*img.getScaleX(); 
		img.setPosition(Global.SCREEN_WIDTH/2 - off, (float) (Global.SCREEN_HEIGHT*(0.55)));
		img.addListener(swipe);
		img.setTouchable(Touchable.disabled);
		charsSpawned.add(img); //add to array
		lastCharSpawned++;

	}

	public void updatePosition(){

		final int m = lastCharSpawned -4;
		//move forward
		if(m< numChars-4){
			for(int i = m ; i<lastCharSpawned; i++){
				Image img = charsSpawned.get(i);
				img.addAction(Actions.scaleBy(Global.ASPECT_RATIO, Global.ASPECT_RATIO, 0.2f));
				float off = img.getWidth()/2*Global.ASPECT_RATIO;
				img.addAction(Actions.moveBy(-off, - (Global.SCREEN_HEIGHT*3/20) , 0.2f));
			}

			float delay = 0.2f; // seconds for last chara to appear

			Timer.schedule(new Timer.Task(){
				@Override
				public void run() {
					createCharacter();
					for(int i = lastCharSpawned-1 ; i>= m; i--){	
						stage.addActor(charsSpawned.get(i));
					}
					charsSpawned.get(m).setTouchable(Touchable.enabled);
				}
			}, delay);

			//start moving the doors forward
		}else{
			//if bonus, open up bonus stage
			if(hasBonus){
				hasBonus= false;
				state = state.BONUS;
				pause();
			}else{
				if(!doorsDrawn){
					doors.setVisible(true);
					doors.addAction(Actions.alpha(1f, 0.5f));
					doorsDrawn = true;

				}

				final int k = numChars - position;
				if(k == numChars){//final char
					doors.addAction(Actions.scaleBy(Global.ASPECT_RATIO/2, Global.ASPECT_RATIO/2, 0.2f));
					float off = doors.getWidth()/2*Global.ASPECT_RATIO/2;

					doors.addAction(Actions.moveBy(-off, - (Global.SCREEN_HEIGHT*1/10) , 0.2f));
				}else{
					for(int i = k ; i<lastCharSpawned; i++){
						Image img = charsSpawned.get(i);
						img.addAction(Actions.scaleBy(Global.ASPECT_RATIO, Global.ASPECT_RATIO, 0.2f));
						float off = img.getWidth()/2*Global.ASPECT_RATIO;
						img.addAction(Actions.moveBy(-off, - (Global.SCREEN_HEIGHT*3/20) , 0.2f));
					}

					doors.addAction(Actions.scaleBy(Global.ASPECT_RATIO/2, Global.ASPECT_RATIO/2, 0.2f));
					float off = doors.getWidth()/2*Global.ASPECT_RATIO/2;

					doors.addAction(Actions.moveBy(-off, - (Global.SCREEN_HEIGHT*1/10) , 0.2f));

					float delay = 0.2f; // seconds for last chara to appear

					Timer.schedule(new Timer.Task(){
						@Override
						public void run() {

							for(int i = lastCharSpawned-1 ; i>= k; i--){	

								stage.addActor(charsSpawned.get(i));
							}
							charsSpawned.get(k).setTouchable(Touchable.enabled);

						}
					}, delay);
					position--;
				}


				//end game
				if(k==numChars && state!= State.STOPPED){
					float delay = 0.5f; // seconds after it's been swiped
					Timer.schedule(new Timer.Task(){
						@Override
						public void run() {
							state = State.STOPPED;
							playerWin = true;
							endGame();

						}
					}, delay);



				}
			}
		}

	}

	/* endGame
	 * Calls the End Game Screen, sends data via Global class
	 * 
	 */
	public void endGame(){
		timer.stop();
		coins = (level.getTimeLimit() - clock)*coins; // coins depend on clock!
		Global.endGame(playerWin, coins); //takes care of end game stuff
		game.setScreen(game.endGameScreen);
	}

	public static class CustomDialog extends Dialog {  

		static TextureAtlas atlas = new TextureAtlas(Gdx.files.internal("dialogui.txt"));
		static Skin pSkin = new Skin(Gdx.files.internal("dialogui.json"), atlas);

		public CustomDialog (String title) {  
			super(title, pSkin);  
			initialize();  
		}  

		private void initialize() {  
			
			padTop(100); // set padding on top of the dialog title 
			padBottom(100);
			getButtonTable().padBottom(Global.SCREEN_HEIGHT/4); // set buttons height  
			getButtonTable().defaults().minHeight(btnH);
			getButtonTable().defaults().minWidth(btnW);
			getContentTable().padTop(Global.SCREEN_HEIGHT/4);
			getContentTable().padBottom(Global.SCREEN_HEIGHT/9);
			
			setModal(true);  
			setPosition(Global.SCREEN_WIDTH/2, Global.SCREEN_HEIGHT/2);

			setMovable(false);  
			setResizable(false);  

		}  

		@Override  
		public CustomDialog text(String text) {  
			Label textLabel = new Label(text, pSkin, "title-text");
			textLabel.setFontScale(Global.FONT_SCALE*2);
			super.text(textLabel);
			return this;  
		}  

		/**  
		 * Adds a text button to the button table.  
		 * @param listener the input listener that will be attached to the button.  
		 */  
		public CustomDialog resumeButton(boolean res) {  
			Button resume = new Button(pSkin,"resume");  
			

			
			button(resume, res);  
			return this;  
		} 
		
		public CustomDialog mainMenuButton(boolean res) {  
			Button mainMenu = new Button(pSkin,"menu");  

			
			button(mainMenu, res); 
			
			return this;  
		} 

		@Override  
		public float getPrefWidth() {  
			// force dialog width  
			return dialogW;
		}  

		@Override  
		public float getPrefHeight() {  
			// force dialog height  
			return dialogH;  
		}  
	}  

	//removes all characters on stage
	public void clearCharacters() {
		if (charsSpawned!=null && charsSpawned.size()>0) {
			for (Image a: charsSpawned) {
				a.remove();
			}
		}
	}


	@Override
	public boolean keyDown(int keycode) {
		if(keycode == Keys.BACK){
			pause();
			return true;
		}
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void render() {
		// TODO Auto-generated method stub

	}

}
