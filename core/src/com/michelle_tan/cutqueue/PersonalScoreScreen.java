package com.michelle_tan.cutqueue;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
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
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.ScalingViewport;

public class PersonalScoreScreen implements Screen{

	OrthographicCamera camera;

	Skin skin;
	Stage stage;
	SpriteBatch batch;
	CutQueue game;

	public BitmapFont font;
	
	TextureRegionDrawable background;
	
	Sound sfxButton;
	
	public PersonalScoreScreen(CutQueue g){
		this.game = g;
		create();
	}
	
	public void create(){

		batch = new SpriteBatch();
		stage = new Stage();

		font = new BitmapFont(Gdx.files.internal("chewy_tryagain.fnt"));
		
		camera = new OrthographicCamera(Global.SCREEN_WIDTH, Global.SCREEN_HEIGHT);
		camera.setToOrtho(false, Global.SCREEN_WIDTH, Global.SCREEN_HEIGHT);
		camera.position.set(Global.SCREEN_WIDTH/2, Global.SCREEN_HEIGHT/2, 0f); 
		stage = new Stage(new ScalingViewport(Scaling.fill, Global.SCREEN_WIDTH, Global.SCREEN_HEIGHT, camera), batch);

		skin = new Skin();

		Pixmap pixmap = new Pixmap(100, 100, Format.RGBA8888);
		pixmap.setColor(Color.WHITE);
		pixmap.fill();

		skin.add("white", new Texture(pixmap));

		font.scale(Global.FONT_SCALE);
		skin.add("default",font);

		//screen title
		CustomLabel title = new CustomLabel("High Scores", new Label.LabelStyle(font, Color.BLACK));
		title.setPosition(Global.SCREEN_WIDTH/2 - title.getWidth()/2, Global.SCREEN_HEIGHT/13*12-title.getHeight()/2);
		stage.addActor(title);
		
		//sound
		sfxButton = game.assets.get("sfx_button.mp3", Sound.class);
		
		// Create a TextureAtlas for the UI elements
		TextureAtlas atlas;
		atlas = new TextureAtlas("ui.txt");
		background = new TextureRegionDrawable(atlas.findRegion("bg_full"));
		final AtlasRegion btn_back = atlas.findRegion("btn_back");
		final AtlasRegion btn_back_t = atlas.findRegion("btn_back_t");
		
		//back button
		final Image backButton = new Image(btn_back);
		backButton.setScale(Global.SCREEN_SCALE);
		backButton.setPosition(Global.SCREEN_WIDTH/16 - backButton.getWidth()/2, Global.SCREEN_HEIGHT/16*15 - Global.SCREEN_SCALE*backButton.getHeight()/2);
		stage.addActor(backButton);
		backButton.addListener(new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				System.out.println("back clicked");
				backButton.setDrawable(new TextureRegionDrawable(btn_back_t));
				sfxButton.play(Global.sfxVolume);
				return true;
			}

			@Override
			public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
				backButton.setDrawable(new TextureRegionDrawable(btn_back));
				game.setScreen(game.mainMenuScreen);
			}

		});
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
		
		int psnW = Global.SCREEN_WIDTH;
		int psnH = Global.SCREEN_HEIGHT - Global.SCREEN_HEIGHT/15*3;
		
		Scores scores = Global.getScores();
		
		//draw out scores in format: level name, player name, score
		batch.begin(); 
		font.setColor(Color.MAROON);
		background.draw(batch, 0, 0, Global.SCREEN_WIDTH, Global.SCREEN_HEIGHT);
		for (int i=0; i<Global.STATIONS.length; i++) {
			font.draw(batch, Global.STATIONS[i], psnW/9, psnH); 
			font.draw(batch,scores.getName(i), psnW/2, psnH); 
			font.draw(batch, Integer.toString(scores.getScore(i)), psnW/10*9, psnH); 
			psnH = psnH - Global.SCREEN_HEIGHT/15;
		}
		batch.end();
		
		stage.draw();

	}

	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub

	}

	@Override
	public void show() {
		Gdx.input.setInputProcessor(stage);
		System.out.println("show score");
		
		Gdx.input.setCatchBackKey(true);
		game.bgmMain.setVolume(Global.volume);
	}

	@Override
	public void hide() {
		Gdx.input.setInputProcessor(null);
		System.out.println("hide score");

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
