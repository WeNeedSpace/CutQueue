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
import com.badlogic.gdx.scenes.scene2d.Event;
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
import com.michelle_tan.cutqueue.TutorialScreen.CustomDialog;
import com.michelle_tan.cutqueue.TutorialScreen.DialogState;
import com.michelle_tan.cutqueue.TutorialScreen.State;

public class BonusStageScreen implements Screen {
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
		Dialog bonusDialog1;
		Dialog bonusDialog2;

		//for swipe gesture
		private final int SWIPE_THRESHOLD = 100; //how long the swipe must be before detection
		private boolean panning;
		private float firstX;
		private float firstY;
		private float newX;
		private float newY;
		private ActorGestureListener swipe;

		//coins
		private int coins= 0;

		//timer
		private int clock;
		private Timer timer;
		private Timer.Task task;
		
		//spawning chara tasks
		private Timer.Task spawnChara1;
		private Timer.Task spawnChara2;
		private Timer.Task spawnChara3;
		private Timer.Task spawnChara4;
		private Timer.Task spawnChara5;
		//FOR STATE OF GAME

		public enum State
		{
			PAUSE,
			RUN,
			STOPPED
		}
		public enum BonusDialogState
		{
			ONE,
			TWO
		}
		
		//for the states of the game
		private State state = State.RUN;
		private BonusDialogState bState = BonusDialogState.ONE;

		//For backgrounds
		private Texture bg;
		private Image bottom;
		private Texture train;
		private Image doors;

		
		
		//constructor
	public BonusStageScreen(CutQueue g) {
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

		doors.setScale((Global.SCREEN_WIDTH/doors.getWidth())); 
		float off = doors.getWidth()/2*doors.getScaleX(); 
		doors.setPosition(Global.SCREEN_WIDTH/2-off, (float) (Global.SCREEN_HEIGHT*(0.75)));
		doors.setVisible(true);
		doors.addAction(Actions.scaleBy(Global.ASPECT_RATIO/2, Global.ASPECT_RATIO/2));
		float off2 = doors.getWidth()/2*Global.ASPECT_RATIO/2;
		doors.addAction(Actions.moveBy(-off2, - (Global.SCREEN_HEIGHT*1/10)));
		
		stage.addActor(doors);

		//SETUP PAUSE DIALOG
		pauseDialog = new CustomDialog(""){
			{
				pauseText("GAME PAUSED!");
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

		//SETUP  DIALOG 1
		bonusDialog1 = new CustomDialog(""){
			{
				this.getContentTable().defaults().minWidth(btnW);
				this.getContentTable().padLeft(btnW/32);
				text("RUSH HOUR TIME!!!\nSwipe as many characters as you can within 10s!");
				resumeButton(true);

			}

			@Override
			protected void result(Object object){
				if((Boolean) object){
					resume();
				}else{


				}
			}
		};

		//SETUP  DIALOG 2
		bonusDialog2 = new CustomDialog(""){
			{
				this.getContentTable().defaults().minWidth(btnW);
				text("You survived rush hour!");
				resumeButton(true);
			}

			@Override
			protected void result(Object object){
				if((Boolean) object){
					game.setScreen(game.gameScreen);
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
					bState = BonusDialogState.TWO;
					timer.stop();
					float delay = 1f; // seconds after it's been swiped

					Timer.schedule(new Timer.Task(){
						@Override
						public void run() {
							pause();

						}
					}, delay);
					
					
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

					//OTHER CHARACTERS' MOVEMENTS

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
					coins +=20;

				
				}else{
//					//nothing happens if you don't swipe long enough
				}
			}

		};

		//SETUP CHARA SPAWNS
		spawnChara1 = new Timer.Task() {
			@Override
			public void run() {
				System.out.println("spawn1");
				final Image img = createCharacter();
				stage.addActor(img);

				//init position 1
				img.setPosition(0, (float) (Global.SCREEN_HEIGHT*(0.20)));

				//move and scale to where
				float off = img.getWidth()/2*(Global.ASPECT_RATIO*1f+ Global.ASPECT_RATIO*3); 
				img.addAction(Actions.moveTo(Global.SCREEN_WIDTH/2 - off, 
						(float) (Global.SCREEN_HEIGHT*(0.55)), 1f));
				img.addAction(Actions.scaleTo(Global.ASPECT_RATIO*1f+ Global.ASPECT_RATIO*3, 
						Global.ASPECT_RATIO*1f+ Global.ASPECT_RATIO*3,1f));

				//disappear into train
				float delay = 1.2f; // seconds after it's been swiped

				Timer.schedule(new Timer.Task(){
					@Override
					public void run() {
						img.addAction(Actions.alpha(0, 0.2f));
						img.remove();

					}
				}, delay);
			}

		};

		spawnChara2 = new Timer.Task() {
			@Override
			public void run() {
				System.out.println("spawn2");
				final Image img = createCharacter();
				stage.addActor(img);

				//init position 2
				img.setPosition(0, 0);

				//move and scale to where
				float off = img.getWidth()/2*(Global.ASPECT_RATIO*1f+ Global.ASPECT_RATIO*3); 
				img.addAction(Actions.moveTo(Global.SCREEN_WIDTH/2 - off, 
						(float) (Global.SCREEN_HEIGHT*(0.55)), 1f));
				img.addAction(Actions.scaleTo(Global.ASPECT_RATIO*1f+ Global.ASPECT_RATIO*3, 
						Global.ASPECT_RATIO*1f+ Global.ASPECT_RATIO*3,1f));
				//disappear into train
				float delay = 1.2f; // seconds after it's been swiped

				Timer.schedule(new Timer.Task(){
					@Override
					public void run() {
						img.addAction(Actions.alpha(0, 0.2f));
						img.remove();

					}
				}, delay);
			}

		};

		spawnChara3 = new Timer.Task() {
			@Override
			public void run() {
				System.out.println("spawn3");
				final Image img = createCharacter();
				stage.addActor(img);

				//init position 3
				img.setPosition(Global.SCREEN_WIDTH, (float) (Global.SCREEN_HEIGHT*(0.2)));
				

				//move and scale to where
				float off = img.getWidth()/2*(Global.ASPECT_RATIO*1f+ Global.ASPECT_RATIO*3); 
				img.addAction(Actions.moveTo(Global.SCREEN_WIDTH/2 - off, 
						(float) (Global.SCREEN_HEIGHT*(0.55)), 1f));
				img.addAction(Actions.scaleTo(Global.ASPECT_RATIO*1f+ Global.ASPECT_RATIO*3, 
						Global.ASPECT_RATIO*1f+ Global.ASPECT_RATIO*3,0.5f));
				//disappear into train
				float delay = 1.2f; // seconds after it's been swiped

				Timer.schedule(new Timer.Task(){
					@Override
					public void run() {
						img.addAction(Actions.alpha(0, 0.2f));
						img.remove();

					}
				}, delay);
			}

		};

		spawnChara4 = new Timer.Task() {
			@Override
			public void run() {
				System.out.println("spawn4");
				final Image img = createCharacter();
				stage.addActor(img);

				//init position 4
				img.setPosition(Global.SCREEN_WIDTH, 0);

				//move and scale to where
				float off = img.getWidth()/2*(Global.ASPECT_RATIO*1f+ Global.ASPECT_RATIO*3); 
				img.addAction(Actions.moveTo(Global.SCREEN_WIDTH/2 - off, 
						(float) (Global.SCREEN_HEIGHT*(0.55)), 1f));
				img.addAction(Actions.scaleTo(Global.ASPECT_RATIO*1f+ Global.ASPECT_RATIO*3, 
						Global.ASPECT_RATIO*1f+ Global.ASPECT_RATIO*3,1f));

				//disappear into train
				float delay = 1.2f; // seconds after it's been swiped

				Timer.schedule(new Timer.Task(){
					@Override
					public void run() {
						img.addAction(Actions.alpha(0, 0.2f));
						img.remove();

					}
				}, delay);
			}

		};

		spawnChara5 = new Timer.Task() {
			@Override
			public void run() {
				System.out.println("spawn5");
				final Image img = createCharacter();
				stage.addActor(img);

				//init position 5
				img.setPosition(Global.SCREEN_WIDTH/2, 0);

				//move and scale to where
				float off = img.getWidth()/2*(Global.ASPECT_RATIO*1f+ Global.ASPECT_RATIO*3); 
				img.addAction(Actions.moveTo(Global.SCREEN_WIDTH/2 - off, 
						(float) (Global.SCREEN_HEIGHT*(0.55)), 1f));
				img.addAction(Actions.scaleTo(Global.ASPECT_RATIO*1f+ Global.ASPECT_RATIO*3, 
						Global.ASPECT_RATIO*1f+ Global.ASPECT_RATIO*3,1f));

				//disappear into train
				float delay = 0.5f; // seconds after it's been swiped

				Timer.schedule(new Timer.Task(){
					@Override
					public void run() {
						img.addAction(Actions.alpha(0, 0.2f));
						img.remove();

					}
				}, delay);


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
				

		//screen title
		title = new CustomLabel("RUSH HOUR", new Label.LabelStyle(font, Color.WHITE));
		font.setScale(2);
		title.setPosition(Global.SCREEN_WIDTH/2 - title.getWidth(), Global.SCREEN_HEIGHT/13*12);
		stage.addActor(title);
	
	}
	
	@Override
	public void render(float delta) {
//		System.out.println("render");
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
		if (bonusDialog1.isVisible()) {
			bonusDialog1.hide();
		}
		if (bonusDialog2.isVisible()) {
			bonusDialog2.hide();
		}

		
		charsSpawned = new ArrayList<Image>(); 	//new array for characters

		//run game after inital chars are created
		clock = 10; //resets timer to bonus level time! :D
		timer.scheduleTask(task, 0.5f, 1); //will run every 1 sec until cancelled in hide()
		
		//spawn the characters from different sides. at different intervals
		timer.scheduleTask(spawnChara1,0.5f, 1f);
		timer.scheduleTask(spawnChara2,0.5f, 0.8f);
		timer.scheduleTask(spawnChara3,0.5f, 1.5f);
		timer.scheduleTask(spawnChara4,0.5f, 0.5f);
		timer.scheduleTask(spawnChara5,0.5f, 1.2f);
		
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
		if (bonusDialog1.isVisible()) {
			bonusDialog1.hide();
		}
		if (bonusDialog2.isVisible()) {
			bonusDialog2.hide();
		}

		clock = Integer.MAX_VALUE; 
		timer.stop();
		timer.clear();
		coins = 0;
		bState = BonusDialogState.ONE;
		
		System.out.println("timer stop");

		clearCharacters();
		dispose();

	}
	@Override
	public void pause() {
		//for time
		timer.stop();

		if(state == State.PAUSE){
			//show pause dialog
			pauseDialog.show(stage);
			state = State.RUN;
		}else{

			//for the dialogs
			switch (bState){
			case ONE:

				//open dialog1
				bonusDialog1.show(stage);
				bState =BonusDialogState.TWO;
				break;

			case TWO:
				//open dialog2
				
				Global.setBonusScore(coins+Global.getBonusScore());
				bonusDialog2.show(stage);
				break;
				

			}
		}
	}
	@Override
	public void resume() {
		if (pauseDialog.isVisible()) {
			pauseDialog.hide();
		}
		if (bonusDialog1.isVisible()) {
			bonusDialog1.hide();
		}
		if (bonusDialog2.isVisible()) {
			bonusDialog2.hide();
		}

		state = State.RUN;
		timer.start();
		
	}
	@Override
	public void dispose() {
		batch.dispose();
		stage.dispose();
		
	}
	/**
	 * create character creates a character in 5 possible positions
	 * 
	 * 
	 * @param position
	 */
	public Image createCharacter(){
		
		
		
		Random generator = new Random(); 
		int r = generator.nextInt(4);
		TextureRegion tex = regions[r*3]; ; //random int (0,3,6 or 9) >> for each of the chars
		Image img = new Image(tex);
		img.setName(""+r*3);
		img.setScale(Global.ASPECT_RATIO*1f*5+ Global.ASPECT_RATIO*3,
				Global.ASPECT_RATIO*1f*5+ Global.ASPECT_RATIO*3);
		img.addListener(swipe);
		img.setTouchable(Touchable.enabled);
		charsSpawned.add(img); //add to array
		

		return img;	
		
		
		

	}

	public void clearCharacters() {
		if (charsSpawned!=null && charsSpawned.size()>0) {
			for (Image a: charsSpawned) {
				a.remove();
			}
		}
	}
}
