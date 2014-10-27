package com.michelle_tan.cutqueue;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.StreamUtils;
import com.badlogic.gdx.utils.viewport.ScalingViewport;
import com.swarmconnect.Swarm;

public class MainMenuScreen implements Screen{
	CutQueue game;

	//camera and view
	private OrthographicCamera camera;

	private Skin skin;	
	private Stage stage;
	private SpriteBatch batch;

	TextureRegionDrawable background;
	BitmapFont font;
	TextButtonStyle textButtonStyle;

	Sound sfxButton;

	Image loginButton;
	Texture placeholder;
	Image userPic;
	CustomLabel loginText;

	//resources for login/logout buttons
	AtlasRegion btn_login;
	AtlasRegion btn_login_t;
	AtlasRegion btn_logout;
	AtlasRegion btn_logout_t;
	AtlasRegion btn_sound_on;
	AtlasRegion btn_sound_off;

	InputListener input_login;
	InputListener input_logout;
	InputListener input_sound;

	Image soundButton;

	public MainMenuScreen(CutQueue g) {
		this.game = g;
		create();
	}

	public void create(){
		Gdx.input.setCatchBackKey(false);

		batch = new SpriteBatch();

		camera = new OrthographicCamera(Global.SCREEN_WIDTH, Global.SCREEN_HEIGHT);
		camera.setToOrtho(false, Global.SCREEN_WIDTH, Global.SCREEN_HEIGHT);
		camera.position.set(Global.SCREEN_WIDTH/2, Global.SCREEN_HEIGHT/2, 0f); 
		stage = new Stage(new ScalingViewport(Scaling.fill, Global.SCREEN_WIDTH, Global.SCREEN_HEIGHT, camera), batch);

		//image placeholder for user profile picture
		placeholder = new Texture(Gdx.files.internal("badlogic.jpg"));

		//set placeholder user picture
		userPic = new Image(placeholder);
		userPic.setWidth(Global.SCREEN_WIDTH/7);
		userPic.setHeight(Global.SCREEN_WIDTH/7);
		userPic.setPosition(Global.SCREEN_HEIGHT/15-userPic.getWidth()/2, Global.SCREEN_HEIGHT/15*14 - userPic.getHeight()/2);
		stage.addActor(userPic);

		skin = new Skin();

		// Generate a 1x1 white texture and store it in the skin named "white".
		Pixmap pixmap = new Pixmap(100, 100, Format.RGBA8888);
		pixmap.setColor(Color.GREEN);
		pixmap.fill();

		skin.add("white", new Texture(pixmap));

		// Store the default libgdx font under the name "default".
		font = new BitmapFont(Gdx.files.internal("chewy_tryagain.fnt"));
		try {
			font.setScale(Global.FONT_SCALE);
		} catch (IllegalArgumentException e) {
			font.setScale(Gdx.graphics.getWidth()/480);
		}
		skin.add("default", font);

		// Init music and sounds
		sfxButton = game.assets.get("sfx_button.mp3", Sound.class);

		// Create a TextureAtlas for the UI elements
		TextureAtlas atlas;
		atlas = game.assets.get("ui.txt", TextureAtlas.class);
		background = new TextureRegionDrawable(atlas.findRegion("bg"));
		final AtlasRegion btn_play = atlas.findRegion("btn_play");
		final AtlasRegion btn_play_t = atlas.findRegion("btn_play_t");
		btn_login = atlas.findRegion("btn_login");
		btn_login_t = atlas.findRegion("btn_login_t");
		btn_logout = atlas.findRegion("btn_logout");
		btn_logout_t = atlas.findRegion("btn_logout_t");
		btn_sound_on = atlas.findRegion("btn_music");
		btn_sound_off = atlas.findRegion("btn_music_off");
		final AtlasRegion btn_score = atlas.findRegion("btn_score");
		final AtlasRegion btn_score_t = atlas.findRegion("btn_score_t");
		final AtlasRegion btn_achieve = atlas.findRegion("btn_achieve");
		final AtlasRegion btn_achieve_t = atlas.findRegion("btn_achieve_t");
		final AtlasRegion btn_help = atlas.findRegion("btn_help");
		final AtlasRegion btn_help_t = atlas.findRegion("btn_help_t");

		//input listeners for logging in and out
		input_login = new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				System.out.println("login clicked");
				loginButton.setDrawable(new TextureRegionDrawable(btn_login_t));
				sfxButton.play(Global.sfxVolume);
				return true;
			}

			@Override
			public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
				loginButton.setDrawable(new TextureRegionDrawable(btn_login));
				Swarm.showLogin();
			}

		};

		input_logout = new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				System.out.println("logout clicked");
				loginButton.setDrawable(new TextureRegionDrawable(btn_logout_t));
				sfxButton.play(Global.sfxVolume);
				return true;
			}

			@Override
			public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
				loginButton.setDrawable(new TextureRegionDrawable(btn_login));
				Swarm.logOut();
			}

		};

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
				game.bgmMain.setVolume(Global.volume);
			}

		};

		soundButton = new Image(btn_sound_on);
		soundButton.setScale(Global.SCREEN_SCALE);
		soundButton.setPosition(Global.SCREEN_WIDTH - Global.SCREEN_SCALE*(soundButton.getWidth()+15), Global.SCREEN_HEIGHT - Global.SCREEN_SCALE*(soundButton.getHeight()+15));
		stage.addActor(soundButton);
		soundButton.addListener(input_sound);

		final Image playButton = new Image(btn_play);
		playButton.setScale(Global.SCREEN_SCALE);
		playButton.setPosition(Global.SCREEN_WIDTH/2 - Global.SCREEN_SCALE*playButton.getWidth()/2, Global.SCREEN_HEIGHT/16*7 - Global.SCREEN_SCALE*playButton.getHeight()/2);
		stage.addActor(playButton);
		playButton.addListener(new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				System.out.println("play clicked");
				playButton.setDrawable(new TextureRegionDrawable(btn_play_t));
				sfxButton.play(Global.sfxVolume);
				return true;
			}

			@Override
			public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
				playButton.setDrawable(new TextureRegionDrawable(btn_play));
				game.setScreen(game.stageSelectScreen);
			}

		});

		loginButton = new Image(btn_login);
		loginButton.setScale(Global.SCREEN_SCALE);
		loginButton.setPosition(Global.SCREEN_WIDTH/2 - Global.SCREEN_SCALE*loginButton.getWidth()/2, Global.SCREEN_HEIGHT/16*5 - Global.SCREEN_SCALE*loginButton.getHeight()/2);
		stage.addActor(loginButton);
		loginButton.addListener(input_login);

		final Image scoreButton = new Image(btn_score);
		scoreButton.setScale(Global.SCREEN_SCALE);
		scoreButton.setPosition(Global.SCREEN_WIDTH/4 - Global.SCREEN_SCALE*scoreButton.getWidth()/2, Global.SCREEN_HEIGHT/16*3 - Global.SCREEN_SCALE*scoreButton.getHeight()/2);
		stage.addActor(scoreButton);
		scoreButton.addListener(new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				System.out.println("score clicked");
				scoreButton.setDrawable(new TextureRegionDrawable(btn_score_t));
				sfxButton.play(Global.sfxVolume);
				return true;
			}

			@Override
			public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
				scoreButton.setDrawable(new TextureRegionDrawable(btn_score));
				if (Swarm.isLoggedIn()) {
					Swarm.showLeaderboards();
				} else { //not logged in or not online, show local score screen
					game.setScreen(game.personalScoreScreen);
				}
			}

		});

		final Image achieveButton = new Image(btn_achieve);
		achieveButton.setScale(Global.SCREEN_SCALE);
		achieveButton.setPosition(Global.SCREEN_WIDTH/4*2 - Global.SCREEN_SCALE*achieveButton.getWidth()/2, Global.SCREEN_HEIGHT/16*3 - Global.SCREEN_SCALE*achieveButton.getHeight()/2);
		stage.addActor(achieveButton);
		achieveButton.addListener(new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				System.out.println("achieve clicked");
				achieveButton.setDrawable(new TextureRegionDrawable(btn_achieve_t));
				sfxButton.play(Global.sfxVolume);
				return true;
			}

			@Override
			public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
				achieveButton.setDrawable(new TextureRegionDrawable(btn_achieve));
				Swarm.showAchievements();
			}

		});

		final Image helpButton = new Image(btn_help);
		helpButton.setScale(Global.SCREEN_SCALE);
		helpButton.setPosition(Global.SCREEN_WIDTH/4*3 - Global.SCREEN_SCALE*helpButton.getWidth()/2, Global.SCREEN_HEIGHT/16*3 - Global.SCREEN_SCALE*helpButton.getHeight()/2);
		stage.addActor(helpButton);
		helpButton.addListener(new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				System.out.println("help clicked");
				helpButton.setDrawable(new TextureRegionDrawable(btn_help_t));
				sfxButton.play(Global.sfxVolume);
				return true;
			}

			@Override
			public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
				helpButton.setDrawable(new TextureRegionDrawable(btn_help));
				game.setScreen(game.creditsScreen);
			}

		});

		//init title
		CustomLabel title = new CustomLabel("CUT QUEUE", new Label.LabelStyle(font, Color.WHITE));
		title.setPosition(Global.SCREEN_WIDTH/2 - title.getWidth()/2, Global.SCREEN_HEIGHT/4*3 - title.getHeight()/2);
		stage.addActor(title);

		CustomLabel footer = new CustomLabel("Created by We Need Space", new Label.LabelStyle(font, Color.WHITE));
		footer.setPosition(Global.SCREEN_WIDTH/2 - footer.getWidth()/2, Global.SCREEN_HEIGHT/20 - footer.getHeight()/2);
		stage.addActor(footer);

		//init label for login
		loginText = new CustomLabel(" ", new Label.LabelStyle(font, Color.BLACK));
		loginText.setPosition(Global.SCREEN_WIDTH/15 + userPic.getWidth() + 10, Global.SCREEN_HEIGHT/15*14);
		stage.addActor(loginText);
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0.8f, 0.8f, 0.8f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		// tell the camera to update its matrices
		camera.update();
		batch.setProjectionMatrix(camera.combined);


		if (Global.isLoggedIn()) { //check if logged in to swarm
			loginText.updateText(Global.getPlayer().getName()); //update name

			//try to download the user's actual picture
			TextureRegion pic = Global.getPlayerPicture();
			if (pic!=null) { //if picture has been loaded before
				userPic.setDrawable(new TextureRegionDrawable(pic));
				userPic.setVisible(true);
			} else {
				if (Global.getPlayer().getPicture()!=null) { //if player has a picture url
					downloadTextureAsync(Global.getPlayer().getPicture()); //downloads and sets the user's picture
				} else {
					userPic.setVisible(false);
				}
			}
		}

		batch.begin();
		background.draw(batch, 0, 0, Global.SCREEN_WIDTH, Global.SCREEN_HEIGHT);
		batch.end();

		stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
		stage.draw();

		Table.drawDebug(stage);
	}

	@Override
	public void resize(int width, int height) {

	}

	@Override
	public void show() {
		Gdx.input.setCatchBackKey(false);

		Gdx.input.setInputProcessor(stage);
		System.out.println("show menu");
		Global.readState(); //load player and score data from json

		loginButton.clearListeners();

		if (Global.sound) { //on
			soundButton.setDrawable(new TextureRegionDrawable(btn_sound_on));
			Global.onSound();
		} else { //off
			soundButton.setDrawable(new TextureRegionDrawable(btn_sound_off));
			Global.offSound();
		}

		game.bgmMain.setVolume(Global.volume);

		if (Global.isLoggedIn()) { //check if logged in to swarm

			loginButton.setDrawable(new TextureRegionDrawable(btn_logout));
			loginButton.addListener(input_logout);

		} else {
			Global.readState(); //load player and score data from json
			userPic.setVisible(false);
			loginText.updateText(" ");

			loginButton.setDrawable(new TextureRegionDrawable(btn_login));
			loginButton.addListener(input_login);
		}


	}

	@Override
	public void hide() {
		Gdx.input.setInputProcessor(null);
		System.out.println("hide menu");

	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub

	}

	@Override
	public void resume() {
		show();
	}

	@Override
	public void dispose() {
		stage.dispose();
		skin.dispose();

	}

	/* downloadTextureAsync
	 * Private helper method to download user picture from given url string.
	 * Adapted from: TextureDownloadTest.java by Nathan Sweet @ Github
	 * https://github.com/libgdx/libgdx/blob/master/tests/gdx-tests/src/com/badlogic/gdx/tests/TextureDownloadTest.java
	 * 
	 */
	private void downloadTextureAsync(final String picURL) {
		new Thread(new Runnable() {
			/** Downloads the content of the specified url to the array. The array has to be big enough. */
			private int download (byte[] out, String url) {
				InputStream in = null;
				try {
					HttpURLConnection conn = null;
					conn = (HttpURLConnection)new URL(url).openConnection();
					conn.setDoInput(true);
					conn.setDoOutput(false);
					conn.setUseCaches(true);
					conn.connect();
					in = conn.getInputStream();
					int readBytes = 0;
					while (true) {
						int length = in.read(out, readBytes, out.length - readBytes);
						if (length == -1) break;
						readBytes += length;
					}
					return readBytes;
				} catch (Exception ex) {
					return 0;
				} finally {
					StreamUtils.closeQuietly(in);
				}
			}

			@Override
			public void run () {
				byte[] bytes = new byte[200 * 1024]; // assuming the content is not bigger than 200kb.
				int numBytes = download(bytes, picURL);
				if (numBytes != 0) {
					// load the pixmap, make it a power of two if necessary (not needed for GL ES 2.0!)
					Pixmap pixmap = new Pixmap(bytes, 0, numBytes);
					final int originalWidth = pixmap.getWidth();
					final int originalHeight = pixmap.getHeight();
					int width = MathUtils.nextPowerOfTwo(pixmap.getWidth());
					int height = MathUtils.nextPowerOfTwo(pixmap.getHeight());
					final Pixmap potPixmap = new Pixmap(width, height, pixmap.getFormat());
					potPixmap.drawPixmap(pixmap, 0, 0, 0, 0, pixmap.getWidth(), pixmap.getHeight());
					pixmap.dispose();
					Gdx.app.postRunnable(new Runnable() {
						@Override
						public void run () {
							//add user's picture
							TextureRegion texture = new TextureRegion(new Texture(potPixmap), 0, 0, originalWidth, originalHeight);
							userPic.setDrawable(new TextureRegionDrawable(texture));

							Global.setPlayerPicture(texture);
						}
					});
				}
			}
		}).start();
	}

}
