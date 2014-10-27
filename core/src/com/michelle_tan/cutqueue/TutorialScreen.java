package com.michelle_tan.cutqueue;

import java.util.ArrayList;
import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
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
import com.michelle_tan.cutqueue.GameScreen.CustomDialog;
import com.michelle_tan.cutqueue.GameScreen.State;

public class TutorialScreen implements Screen{
	
	private CutQueue game;
	//camera and view
		private OrthographicCamera camera;

		//game-related
		private Skin skin;	
		private Stage stage;
		private SpriteBatch batch;


		CustomLabel title; //to display title of screen
		CustomLabel coinButton;
		CustomLabel timerButton;

		//for character textures
		private TextureRegion[] regions = new TextureAtlas.AtlasRegion[17]; // #2
		private Texture texture;
		private TextureAtlas atlasChar;
		
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
		private ArrayList<Image> charsSpawned;

		//for buttons
		private BitmapFont font;
		private static int btnW = Global.BUTTON_WIDTH;
		private static int btnH = Global.BUTTON_HEIGHT;
		
		
		/**
		 * 
		 *I changed the dialog to this height. Fits better on screen.  
		 * 
		 */
		
		//for dialogs
		private static int dialogW = Global.SCREEN_WIDTH;
		private static int dialogH = Global.SCREEN_HEIGHT;

		Dialog pauseDialog;
		Dialog tutDialog1;
		Dialog tutDialog2;
		Dialog tutDialog3;
		Dialog tutDialog4;
		Dialog tutDialog5;

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
		public enum DialogState
		{
			ONE,
			TWO,
			THREE,
			FOUR,
			FIVE
		}
		public enum State
		{
			PAUSE,
			RUN,
			STOPPED
		}
		
		//for the states of the game
		private DialogState dState = DialogState.ONE;
		private State state = State.RUN;

		//For backgrounds
		private Texture bg;
		private Image bottom;
		private Texture train;
		private Image doors;
		private Boolean doorsDrawn = false;
		
		
		//constructor
	public TutorialScreen(CutQueue g) {
		create();
		this.game = g;
	}
	public void create(){

		batch = new SpriteBatch();



		font = new BitmapFont(Gdx.files.internal("chewy_tryagain.fnt"));

		camera = new OrthographicCamera(Global.SCREEN_WIDTH, Global.SCREEN_HEIGHT);
		camera.setToOrtho(false, Global.SCREEN_WIDTH, Global.SCREEN_HEIGHT);
		camera.position.set(Global.SCREEN_WIDTH/2, Global.SCREEN_HEIGHT/2, 0f); 
		stage = new Stage(new ScalingViewport(Scaling.fill, Global.SCREEN_WIDTH, Global.SCREEN_HEIGHT, camera), batch);

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

		//SETUP PAUSE DIALOG
		pauseDialog = new CustomDialog(""){
			{
				pauseText("GAME PAUSED!");
				resumeButton(true);
				this.getButtonTable().row();
				skipButton(false);
			}

			@Override
			protected void result(Object object){
				if((Boolean) object){
					resume();
				}else{
					if(Global.getFlag()){ // if first time
						game.setScreen(game.gameScreen);
					}else{
						game.setScreen(game.mainMenuScreen);
					}
				}
			}
		};

		//SETUP  DIALOG 1
		tutDialog1 = new CustomDialog(""){
			{
				this.getContentTable().defaults().minWidth(btnW);
				this.getContentTable().padLeft(btnW/32);
				text("Welcome to the tutorial.\nSwipe characters left or right!");
				resumeButton(true);
				this.getButtonTable().row();
				skipButton(false);
			}

			@Override
			protected void result(Object object){
				if((Boolean) object){
					resume();
				}else{
					if(Global.getFlag()){ // if first time
						game.setScreen(game.gameScreen);
					}else{
						game.setScreen(game.mainMenuScreen);
					}
				}
			}
		};

		//SETUP  DIALOG 2
		tutDialog2 = new CustomDialog(""){
			{
				this.getContentTable().defaults().minWidth(btnW);
				this.getContentTable().padLeft(btnW/32);
				text("Swipe all characters before timer at the top right hits zero!");
				resumeButton(true);
				this.getButtonTable().row();
				skipButton(false);
			}

			@Override
			protected void result(Object object){
				if((Boolean) object){
					resume();
				}else{
					if(Global.getFlag()){ // if first time
						game.setScreen(game.gameScreen);
					}else{
						game.setScreen(game.mainMenuScreen);
					}
				}
			}
		};

		//SETUP  DIALOG 3
		tutDialog3 = new CustomDialog(""){
			{
				this.getContentTable().defaults().minWidth(btnW);
				this.getContentTable().padLeft(btnW/32);
				text("Granny is stubborn! After swiping her, tap Granny till she's tired!");
				resumeButton(true);
				this.getButtonTable().row();
				skipButton(false);
			}

			@Override
			protected void result(Object object){
				if((Boolean) object){
					resume();
				}else{
					if(Global.getFlag()){ // if first time
						game.setScreen(game.gameScreen);
					}else{
						game.setScreen(game.mainMenuScreen);
					}
				}
			}
		};

		//SETUP  DIALOG 4
		tutDialog4 = new CustomDialog(""){
			{
				this.getContentTable().defaults().minWidth(btnW);
				this.getContentTable().padLeft(btnW/32);
				text("Great! Swipe her away!");
				resumeButton(true);
				this.getButtonTable().row();
				skipButton(false);
			}

			@Override
			protected void result(Object object){
				if((Boolean) object){
					resume();
				}else{
					if(Global.getFlag()){ // if first time
						game.setScreen(game.gameScreen);
					}else{
						game.setScreen(game.mainMenuScreen);
					}
				}
			}
		};

		//SETUP  DIALOG 5
		tutDialog5 = new CustomDialog(""){
			{
				this.getContentTable().defaults().minWidth(btnW);
				text("You're ready to play! Get to the train doors as fast as you can to earn more points!");
				mainMenuButton(false);
			}

			@Override
			protected void result(Object object){
				if((Boolean) object){
					resume();
				}else{
					if(Global.getFlag()){ // if first time
						game.setScreen(game.gameScreen);
					}else{
						game.setScreen(game.mainMenuScreen);
					}
				}
			}
		};

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
				}
				else {
					if(state == State.RUN){ //while game is running
						clock = clock - 1; //timer decreases
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

		// Create a TextureAtlas for the UI elements
		TextureAtlas atlas;
		atlas = new TextureAtlas("ui.txt");
		final AtlasRegion btn_pause = atlas.findRegion("btn_pause");
		final AtlasRegion btn_pause_t = atlas.findRegion("btn_pause_t");
		final AtlasRegion header = atlas.findRegion("header");
		final AtlasRegion game_display = atlas.findRegion("game_display");

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
				return true;
			}

			@Override
			public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
				pauseButton.setDrawable(new TextureRegionDrawable(btn_pause));
				state = State.PAUSE;
				pause();
			}

		});

		//SETUP SWIPE GESTURE	
		swipe = new ActorGestureListener() {
			Image fall;
			Image front;
			@Override
			public void pan(InputEvent event, float x, float y, float deltaX, float deltaY) {
				Actor actor = event.getListenerActor();

				//sets position when it starts panning
				if (!panning) {

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
						pause();
						
					//OTHER CHARACTERS' MOVEMENTS
					}else{
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

		//SETUP CHARACTERS
		//Get all the characters from sprite sheet
		atlasChar = new TextureAtlas(Gdx.files.internal("chars.txt"));

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
		title = new CustomLabel("Tutorial", new Label.LabelStyle(font, Color.WHITE));
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
		font.draw(batch, ""+coins, Global.SCREEN_WIDTH/4*3, Global.SCREEN_HEIGHT/15*13);
		batch.end();
	}
	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void show() {
		create();
		Gdx.input.setInputProcessor(stage);
		System.out.println("show game");

		if (pauseDialog.isVisible()) {
			pauseDialog.hide();
		}

		//reset level vars

		title.updateText("Tutorial");
		numChars = 6;

		charTypes = new int[numChars]; //new array for chartypes
		charsSpawned = new ArrayList<Image>(); 	//new array for characters
		lastCharSpawned = 0;

		Random generator = new Random();
		//randomise chars into the rest of the array
		for (int i=0; i<numChars; i++) {
			if(i!=1){
				int r = generator.nextInt(4);
				charTypes[i] = r*3; //random int (0,3,6 or 9) >> for each of the chars
				
				
			}else{
				charTypes[i] = 12;
				
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
		clock = 30; //resets timer to level time
		timer.scheduleTask(task, 0.5f, 1); //will run every 1 sec until cancelled in hide()
		timer.start();
		System.out.println("timer start");
		state = State.RUN;
		pause();
	}
	@Override
	public void hide() {
		Gdx.input.setInputProcessor(null);
		System.out.println("hide game");

		if (pauseDialog.isVisible()) {
			pauseDialog.hide();
		}

		clock = Integer.MAX_VALUE; 
		timer.stop();
		timer.clear();
		coins = 0;
		doorsDrawn = false;
		dState = DialogState.ONE;
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
		dispose();
	}
	@Override
	public void pause() {
		timer.stop();
		if(state == State.PAUSE){
			//show pause dialog
			pauseDialog.show(stage);
			state = State.RUN;
		}else{


			//for the dialogs
			switch (dState){
			case ONE:

				//open dialog1
				tutDialog1.show(stage);
				dState =DialogState.TWO;
				break;

			case TWO:
				//open dialog2
				tutDialog2.show(stage);
				dState =DialogState.THREE;

				break;

			case THREE:
				//open dialog3
				tutDialog3.show(stage);
				dState =DialogState.FOUR;

				break;

			case FOUR:
				//open dialog4
				tutDialog4.show(stage);
				dState =DialogState.FIVE;

				break;

			case FIVE:
				//open dialog5
				tutDialog5.show(stage);
				break;

			}
		}


	}
	@Override
	public void resume() {
		
		
		if (pauseDialog.isVisible()) {
			pauseDialog.hide();
		}
		if (tutDialog1.isVisible()) {
			tutDialog1.hide();
		}if (tutDialog2.isVisible()) {
			tutDialog2.hide();
		}if (tutDialog3.isVisible()) {
			tutDialog3.hide();
		}if (tutDialog4.isVisible()) {
			tutDialog4.hide();
		}if (tutDialog5.isVisible()) {
			tutDialog5.hide();
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
					pause();
				}
			}, delay);


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
						pause();
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

					}
				}, delay);



			}
		}

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
			Label textLabel = new Label(text, pSkin, "normal-text");
			textLabel.setFontScale(Global.FONT_SCALE*3/2);
			textLabel.setWrap(true);
			
			super.text(textLabel);
			return this;  
		} 
 
		public CustomDialog pauseText(String text) {  
			Label pauseTextLabel = new Label(text, pSkin, "title-text");
			pauseTextLabel.setFontScale(Global.FONT_SCALE*2);
			
			super.text(pauseTextLabel);
			return this;  
		}  

		/**  
		 * Adds a text button to the button table.  
		 * @param listener the input listener that will be attached to the button.  
		 */  
		public CustomDialog resumeButton(boolean res) {  
			Button resume = new Button(pSkin,"resume");  
			
			//	         button.addListener(listener);  
			
			button(resume, res);  
			return this;  
		} 
		
		public CustomDialog mainMenuButton(boolean res) {  
			Button mainMenu = new Button(pSkin,"menu");  
			//	         button.addListener(listener);
			
			button(mainMenu, res); 
			
			return this;  
		} 
		public CustomDialog skipButton(boolean res) {  
			Button skip = new Button(pSkin,"skip");  
			//	         button.addListener(listener);
			
			button(skip, res); 
			
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


}
