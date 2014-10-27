package com.michelle_tan.cutqueue;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
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
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.ScalingViewport;

public class CreditsScreen implements Screen, ApplicationListener, InputProcessor{

	OrthographicCamera camera;

	Skin skin;
	Stage stage;
	SpriteBatch batch;
	CutQueue game;

	public BitmapFont font;

	TextureRegionDrawable background;

	Sound sfxButton;

	//for back button
	InputMultiplexer input;

	String[] credits = {"Special Thanks to ",
						"NUS", 
						"Orbital",
						"Our Beta Testers",
						"",
						"Made with libGDX",
						"",
						"Graphics by ",
						"Haritha Ramesh & Michelle Tan",
						"",
						"Music and SFX from",
						"Kevin MacLeod", 
						"(http://incompetech.com/)",
						"Ryan Curtis",
						"(http://ryan-curtis.com)",
						"Intermedia Design Graphics",
						"(http://www.idgraphics.com/)",
						"Beat Suite",
						"(www.beatsuite.com)",
						"SoundJay",
						"(http://www.soundjay.com/)"};

	public CreditsScreen(CutQueue g){
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

		input = new InputMultiplexer(this, stage);

		// A skin can be loaded via JSON or defined programmatically, either is fine. Using a skin is optional but strongly
		// recommended solely for the convenience of getting a texture, region, etc as a drawable, tinted drawable, etc.

		skin = new Skin();

		// Generate a 1x1 white texture and store it in the skin named "white".

		Pixmap pixmap = new Pixmap(100, 100, Format.RGBA8888);
		pixmap.setColor(Color.WHITE);
		pixmap.fill();

		skin.add("white", new Texture(pixmap));

		try {
			font.setScale(Global.FONT_SCALE);
		} catch (IllegalArgumentException e) {
			font.setScale(Gdx.graphics.getWidth()/480);
		}
		skin.add("default",font);

		//screen title
		/*
		CustomLabel title = new CustomLabel("Credits", new Label.LabelStyle(font, Color.WHITE));
		title.setPosition(Global.SCREEN_WIDTH/2 - title.getWidth(), Global.SCREEN_HEIGHT/13*12);
		stage.addActor(title);
		*/

		//sound
		sfxButton = game.assets.get("sfx_button.mp3", Sound.class);

		// Create a TextureAtlas for the UI elements
		TextureAtlas atlas;
		atlas = game.assets.get("ui.txt", TextureAtlas.class);
		background = new TextureRegionDrawable(atlas.findRegion("bg_full"));
		final AtlasRegion btn_back = atlas.findRegion("btn_back");
		final AtlasRegion btn_back_t = atlas.findRegion("btn_back_t");

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

		int btnPsnW = Global.SCREEN_WIDTH/2;
		int btnPsnH = Global.SCREEN_HEIGHT - Global.SCREEN_HEIGHT/26*3;

		font.setScale(Global.FONT_SCALE);
		for (String s : credits) {
			
			CustomLabel text = new CustomLabel(s, new Label.LabelStyle(font, Color.MAROON));
			text.setPosition(btnPsnW - text.getWidth()/2, btnPsnH - text.getHeight()/2*3);
			stage.addActor(text);

			btnPsnH = btnPsnH - Global.SCREEN_HEIGHT/26;
		}

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
		font.setScale(Global.FONT_SCALE*2);
		font.draw(batch, "Credits", Global.SCREEN_WIDTH/2 - Global.SCREEN_WIDTH/6, Global.SCREEN_HEIGHT/26*25);
		// font.drawMultiLine(spriteBatch, text, x, y, width, HAlignment.CENTER);
		batch.end();

		font.setScale(Global.FONT_SCALE);
		stage.draw();

	}

	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub

	}

	@Override
	public void show() {
		System.out.println("show credits");
		Gdx.input.setInputProcessor(input);
		Gdx.input.setCatchBackKey(true);

		game.bgmMain.setVolume(Global.volume);
	}

	@Override
	public void hide() {
		Gdx.input.setInputProcessor(null);
		System.out.println("hide credits");

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

	@Override
	public void render() {
		// TODO Auto-generated method stub

	}

	//back button functionality
	//back to main menu when back pressed
	@Override
	public boolean keyDown(int keycode) {
		if(keycode == Keys.BACK){
			System.out.println("back");
			game.setScreen(game.mainMenuScreen);
			return true;
		} else {
			System.out.println("anykey" +keycode);
			game.setScreen(game.mainMenuScreen);
			return true;
		}
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

}
