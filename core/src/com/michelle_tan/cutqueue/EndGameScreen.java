package com.michelle_tan.cutqueue;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Input.Keys;
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
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.ScalingViewport;
import com.swarmconnect.Swarm;
import com.swarmconnect.SwarmAchievement;
import com.swarmconnect.SwarmLeaderboard;
import com.michelle_tan.cutqueue.CustomLabel;

public class EndGameScreen implements Screen{

	OrthographicCamera camera;

	Skin skin;
	Stage stage;
	SpriteBatch batch;
	CutQueue game;

	public BitmapFont font;
	TextButtonStyle textButtonStyle;

	TextureRegionDrawable background;
	AtlasRegion btn_submit;
	AtlasRegion btn_submit_t;
	AtlasRegion btn_home;
	AtlasRegion btn_home_t;
	AtlasRegion btn_next;
	AtlasRegion btn_next_t;
	AtlasRegion btn_replay;
	AtlasRegion btn_replay_t;

	Image postButton;
	Image firstButton;
	Image secondButton;
	Image thirdButton;

	CustomLabel endText;
	CustomLabel scoreText;
	
	Sound sfxButton;

	public EndGameScreen(CutQueue g){
		this.game = g;
		create();
	}

	public void create(){
		//back button functionality
		//catch the back button, don't let it exit game
		Gdx.input.setCatchBackKey(true);

		batch = new SpriteBatch();
		stage = new Stage();

		camera = new OrthographicCamera(Global.SCREEN_WIDTH, Global.SCREEN_HEIGHT);
		camera.setToOrtho(false, Global.SCREEN_WIDTH, Global.SCREEN_HEIGHT);
		camera.position.set(Global.SCREEN_WIDTH/2, Global.SCREEN_HEIGHT/2, 0f); 
		stage = new Stage(new ScalingViewport(Scaling.fill, Global.SCREEN_WIDTH, Global.SCREEN_HEIGHT, camera), batch);

		skin = new Skin();

		Pixmap pixmap = new Pixmap(100, 100, Format.RGBA8888);
		pixmap.setColor(Color.WHITE);
		pixmap.fill();

		skin.add("white", new Texture(pixmap));

		font = new BitmapFont(Gdx.files.internal("chewy_tryagain.fnt"));

		try {
			font.setScale(Global.FONT_SCALE);
		} catch (IllegalArgumentException e) {
			font.setScale(Gdx.graphics.getWidth()/480);
		}

		skin.add("default",font);
		
		sfxButton = game.assets.get("sfx_button.mp3", Sound.class);

		// Create a TextureAtlas for the UI elements
		TextureAtlas atlas;
		atlas = game.assets.get("ui.txt", TextureAtlas.class);
		background = new TextureRegionDrawable(atlas.findRegion("bg_full"));
		btn_submit = atlas.findRegion("btn_submit");
		btn_submit_t = atlas.findRegion("btn_submit_t");
		btn_home = atlas.findRegion("btn_home");
		btn_home_t = atlas.findRegion("btn_home_t");
		btn_next = atlas.findRegion("btn_next");
		btn_next_t = atlas.findRegion("btn_next_t");
		btn_replay = atlas.findRegion("btn_replay");
		btn_replay_t = atlas.findRegion("btn_replay_t");
		final AtlasRegion header = atlas.findRegion("header");
		final AtlasRegion game_display = atlas.findRegion("game_display");

		//replay button
		firstButton = new Image(btn_replay);
		firstButton.setScale(Global.SCREEN_SCALE);
		firstButton.setPosition(Global.SCREEN_WIDTH/7*2 - firstButton.getWidth()/2, Global.SCREEN_HEIGHT/5 - firstButton.getHeight()/2);
		stage.addActor(firstButton);
		firstButton.addListener(new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				System.out.println("replay clicked");
				firstButton.setDrawable(new TextureRegionDrawable(btn_replay_t));
				sfxButton.play(Global.sfxVolume);
				return true;
			}

			@Override
			public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
				firstButton.setDrawable(new TextureRegionDrawable(btn_replay));
				game.setScreen(game.gameScreen);
			}

		});

		secondButton = new Image(btn_home);
		secondButton.setScale(Global.SCREEN_SCALE);
		secondButton.setPosition(Global.SCREEN_WIDTH/2 - secondButton.getWidth()/2, Global.SCREEN_HEIGHT/5- secondButton.getHeight()/2);
		stage.addActor(secondButton);
		secondButton.addListener(new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				System.out.println("home clicked");
				secondButton.setDrawable(new TextureRegionDrawable(btn_home_t));
				sfxButton.play(Global.sfxVolume);
				return true;
			}

			@Override
			public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
				secondButton.setDrawable(new TextureRegionDrawable(btn_home));
				game.setScreen(game.mainMenuScreen);
			}

		});

		thirdButton = new Image(btn_next);
		thirdButton.setScale(Global.SCREEN_SCALE);
		thirdButton.setPosition(Global.SCREEN_WIDTH/7*5 - thirdButton.getWidth()/2, Global.SCREEN_HEIGHT/5- thirdButton.getHeight()/2);
		stage.addActor(thirdButton);

		thirdButton.addListener(new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				System.out.println("replay clicked");
				thirdButton.setDrawable(new TextureRegionDrawable(btn_next_t));
				sfxButton.play(Global.sfxVolume);
				return true;
			}

			@Override
			public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
				thirdButton.setDrawable(new TextureRegionDrawable(btn_next));
				Global.currLevel++;
				game.setScreen(game.gameScreen);
			}

		});

		//screen title
		CustomLabel title = new CustomLabel("Game Over", new Label.LabelStyle(font, Color.WHITE));
		font.setScale(Global.FONT_SCALE*2);
		title.setPosition(Global.SCREEN_WIDTH/2 - title.getWidth(), Global.SCREEN_HEIGHT/13*12);
		stage.addActor(title);

		font.setScale(Global.FONT_SCALE);
		endText = new CustomLabel("You did it!", new Label.LabelStyle(font, Color.MAROON));
		endText.setPosition(Global.SCREEN_WIDTH/2 - endText.getWidth()/2, Global.SCREEN_HEIGHT/13*9);
		stage.addActor(endText);
		
		font.setScale(Global.FONT_SCALE*2);
		scoreText = new CustomLabel("", new Label.LabelStyle(font, Color.ORANGE));
		scoreText.setPosition(Global.SCREEN_WIDTH/2 - scoreText.getWidth()/2, Global.SCREEN_HEIGHT/13*7);
		stage.addActor(scoreText);



	}
	@Override
	public void render(float delta) {

		Gdx.gl.glClearColor(0.8f, 0.8f, 0.8f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		// tell the camera to update its matrices
		camera.update();

		// tell the SpriteBatch to render in the
		// coordinate system specified by the camera.
		batch.setProjectionMatrix(camera.combined);

		batch.begin(); 
		background.draw(batch, 0, 0, Global.SCREEN_WIDTH, Global.SCREEN_HEIGHT);
		batch.end();

		stage.draw();

		batch.begin();
		font.setScale(Global.FONT_SCALE);
		font.setColor(Color.ORANGE);
		font.draw(batch, "You scored ", Global.SCREEN_WIDTH/5*2, Global.SCREEN_HEIGHT/13*8);
		batch.end();

	}

	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub

	}

	@Override
	public void show() {
		Gdx.input.setInputProcessor(stage);
		System.out.println("show end game");
		
		game.bgmMain.setVolume(Global.volume);

		if (postButton!=null) { //if the post btn exists, remove it
			postButton.remove();
		}

		//SwarmAchievement.unlock(21070); //unlock achievement

		scoreText.setText(Integer.toString(Global.getLastScore()));
		scoreText.setPosition(Global.SCREEN_WIDTH/2 - scoreText.getWidth(), Global.SCREEN_HEIGHT/13*7);

		if (Global.getGameState()) {
			endText.setText("You did it!");
			endText.setPosition(Global.SCREEN_WIDTH/2 - endText.getWidth(), Global.SCREEN_HEIGHT/13*9);

			firstButton.setPosition(Global.SCREEN_WIDTH/4 - Global.SCREEN_SCALE*firstButton.getWidth()/2, Global.SCREEN_HEIGHT/13*2- firstButton.getHeight()/2);
			secondButton.setPosition(Global.SCREEN_WIDTH/2 - Global.SCREEN_SCALE*secondButton.getWidth()/2, Global.SCREEN_HEIGHT/13*2- secondButton.getHeight()/2);
			thirdButton.setVisible(true);
			thirdButton.setPosition(Global.SCREEN_WIDTH/4*3 - Global.SCREEN_SCALE*thirdButton.getWidth()/2, Global.SCREEN_HEIGHT/13*2- thirdButton.getHeight()/2);

			if (Swarm.isLoggedIn()) { //only allow submitting score when logged in 
				postButton = new Image(btn_submit);
				postButton.setScale(Global.SCREEN_SCALE);
				postButton.setPosition(Global.SCREEN_WIDTH/2 - Global.SCREEN_SCALE*postButton.getWidth()/2, Global.SCREEN_HEIGHT/13*4);
				stage.addActor(postButton);
				postButton.addListener(new InputListener() {
					@Override
					public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
						System.out.println("submit score clicked");
						postButton.setDrawable(new TextureRegionDrawable(btn_submit_t));
						sfxButton.play(Global.sfxVolume);
						return true;
					}

					@Override
					public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
						postButton.setDrawable(new TextureRegionDrawable(btn_submit));
						SwarmLeaderboard.submitScoreAndShowLeaderboard(Global.LEVELS[Global.currLevel].getLeaderboard(), Global.getLastScore());
					}

				});
			}
		} else {
			endText.setText("No time...");
			endText.setPosition(Global.SCREEN_WIDTH/2 - endText.getWidth(), Global.SCREEN_HEIGHT/13*9);

			firstButton.setPosition(Global.SCREEN_WIDTH/3 - Global.SCREEN_SCALE*firstButton.getWidth()/2, Global.SCREEN_HEIGHT/13*2- firstButton.getHeight()/2);
			secondButton.setPosition(Global.SCREEN_WIDTH/3*2 - Global.SCREEN_SCALE*secondButton.getWidth()/2, Global.SCREEN_HEIGHT/13*2- secondButton.getHeight()/2);
			thirdButton.setVisible(false);
		}
	}

	@Override
	public void hide() {
		Gdx.input.setInputProcessor(null);
		System.out.println("hide end game");

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
