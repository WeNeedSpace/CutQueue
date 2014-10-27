package com.michelle_tan.cutqueue;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.Timer.Task;
import com.badlogic.gdx.utils.viewport.ScalingViewport;
import com.michelle_tan.cutqueue.GameScreen.State;

public class LoadingScreen implements Screen {

	CutQueue game;

	private OrthographicCamera camera;
	Stage stage;

	Texture background;

	BitmapFont font;
	SpriteBatch batch;

	private boolean timerIsOn = false;

	public LoadingScreen(CutQueue game) {
		this.game = game;
		create();
	}

	public void create() {

		camera = new OrthographicCamera(Global.SCREEN_WIDTH, Global.SCREEN_HEIGHT);
		camera.setToOrtho(false, Global.SCREEN_WIDTH, Global.SCREEN_HEIGHT);
		camera.position.set(Global.SCREEN_WIDTH/2, Global.SCREEN_HEIGHT/2, 0f); 
		stage = new Stage(new ScalingViewport(Scaling.fill, Global.SCREEN_WIDTH, Global.SCREEN_HEIGHT, camera), batch);

		font = new BitmapFont(Gdx.files.internal("chewy_tryagain.fnt"));
		try {
			font.setScale(Global.FONT_SCALE);
		} catch (IllegalArgumentException e) {
			font.setScale(Gdx.graphics.getWidth()/480);
		}

		batch = new SpriteBatch();

		background = new Texture(Gdx.files.internal("splash.png"));

		// which assets do we want to be loaded
		game.assets = new AssetManager();

		game.assets.load("chewy_tryagain.fnt",BitmapFont.class);
		game.assets.load("chars.txt",TextureAtlas.class);
		game.assets.load("ui.txt",TextureAtlas.class);
		//game.assets.load("bgm_main.mp3", Music.class);
		game.assets.load("bgm_game.mp3", Music.class);
		game.assets.load("sfx_button.mp3", Sound.class);
		game.assets.load("sfx_char_pickup.mp3", Sound.class);
		game.assets.load("sfx_char_throw.mp3", Sound.class);
		game.assets.load("sfx_grandma_hit.mp3", Sound.class);

		game.assets.finishLoading();
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		stage.draw();

		batch.begin();
		batch.draw(background, 0, 0, Global.SCREEN_WIDTH, Global.SCREEN_HEIGHT);
		font.setColor(Color.WHITE);
		font.draw(batch, "Loading... "+(int)game.assets.getProgress()*100+"%", Global.SCREEN_WIDTH/3, Global.SCREEN_HEIGHT/3);
		batch.end();

		if(!timerIsOn) {
			timerIsOn = true;

			Timer.schedule(new Timer.Task() {

				@Override
				public void run() {
					if(game.assets.update()){
						// all the assets are loaded
						game.createScreens(); //will change to main menu screen
					}
				}

			}, 10);

		} else if(Gdx.input.isTouched()) { //if player touches screen
			//change screen if assets have been loaded
			if(game.assets.update()){
				// Remove the task so we don't call changeScreen twice: 
				Timer.instance().clear(); 
				// all the assets are loaded
				game.createScreens(); //will change to main menu screen
			}

		}

	}

	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub

	}

	@Override
	public void show() {
		// TODO Auto-generated method stub

	}

	@Override
	public void hide() {
		// TODO Auto-generated method stub

	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub

	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub

	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

}