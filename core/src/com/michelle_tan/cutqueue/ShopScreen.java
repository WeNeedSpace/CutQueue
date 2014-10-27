package com.michelle_tan.cutqueue;

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
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener.ChangeEvent;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.ScalingViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.michelle_tan.cutqueue.GameScreen.CustomDialog;

public class ShopScreen implements Screen{

	OrthographicCamera camera;

	Skin skin;
	Stage stage;
	SpriteBatch batch;
	CutQueue game;

	public BitmapFont font;

	TextButton coinButton;

	TextButton[] buttons;

	public ShopScreen(CutQueue g){
		create();
		this.game = g;
	}

	public void create(){

		batch = new SpriteBatch();

		font = new BitmapFont();

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

		// Configure a TextButtonStyle and name it "default". Skin resources are stored by type, so this doesn't overwrite the font.
		TextButtonStyle textButtonStyle = new TextButtonStyle();
		textButtonStyle.up = skin.newDrawable("white", Color.DARK_GRAY);
		textButtonStyle.down = skin.newDrawable("white", Color.DARK_GRAY);
		textButtonStyle.checked = skin.newDrawable("white", Color.BLUE);
		textButtonStyle.over = skin.newDrawable("white", Color.LIGHT_GRAY);

		textButtonStyle.font = skin.getFont("default");

		skin.add("default", textButtonStyle);

		//screen title
		CustomLabel title = new CustomLabel("Shop", new Label.LabelStyle(font, Color.BLACK));
		title.setPosition(Global.SCREEN_WIDTH/2, Global.SCREEN_HEIGHT/13*12);
		title.setScale(Global.FONT_SCALE);
		stage.addActor(title);

		//back button
		final TextButton backButton = new TextButton("BACK",textButtonStyle);
		backButton.setWidth(Global.BUTTON_WIDTH);
		backButton.setHeight(Global.BUTTON_HEIGHT);
		backButton.setPosition(0, Global.SCREEN_HEIGHT - backButton.getHeight());
		stage.addActor(backButton);

		backButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				System.out.println("back clicked");
				game.setScreen(game.mainMenuScreen);
			}

		});

		//replay button
		coinButton = new TextButton(Integer.toString(Global.getPlayer().getCoins()) + " coins",textButtonStyle);
		coinButton.setWidth(Global.BUTTON_WIDTH);
		coinButton.setHeight(Global.BUTTON_HEIGHT);
		coinButton.setPosition(Global.SCREEN_WIDTH - coinButton.getWidth(), Global.SCREEN_HEIGHT - coinButton.getHeight());
		stage.addActor(coinButton);
		coinButton.setTouchable(Touchable.disabled);

		int btnPsnW = Global.SCREEN_WIDTH/2;
		int btnPsnH = Global.SCREEN_HEIGHT - Global.SCREEN_HEIGHT/13*3;
		buttons = new TextButton[Global.POWERUPS.length];
		int i = 0;

		//for each station, create a button
		for (final Powerup s : Global.POWERUPS) {

			final TextButton button = new TextButton("Buy",textButtonStyle);
			button.setWidth(Global.SCREEN_WIDTH/5);
			button.setHeight(Global.SCREEN_WIDTH/12);
			button.setPosition(Global.SCREEN_WIDTH/10*9 - button.getWidth()/2 - Global.SCREEN_WIDTH/15, btnPsnH - button.getHeight());
			stage.addActor(button);

			button.addListener(new ClickListener() {
				@Override
				public void clicked(InputEvent event, float x, float y) {
					System.out.println(s.getName() + " clicked");

					//try to buy powerup
					if (Global.getPlayer().addPowerup(s)) {
						//if successful, refresh screen
						show();
					}

					/*
			    	//show a buy dialog to confirm purchase
			    	Dialog buyDialog = new CustomDialog("Confirm Purchase"){
						{
							text("Buy "+s.getName()+" for "+s.getCost()+" coins?");
							button("Confirm",true);
							button("Cancel",false);
						}

						@Override
						protected void result(Object object){
							if((Boolean) object){
						    	//try to buy powerup
						    	if (Global.getPlayer().addPowerup(s)) {
						    		//if successful, button cannot be touched
						    		button.setTouchable(Touchable.disabled);
						    	}

								resume();
							}
						}
					};
					buyDialog.show(stage);
					 */
				}

			});

			btnPsnH = btnPsnH - Global.SCREEN_HEIGHT/12;

			buttons[i] = button; //add to button array
			i++;

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

		int psnW = Global.SCREEN_WIDTH/2;
		int psnH = Global.SCREEN_HEIGHT - Global.SCREEN_HEIGHT/13*3;

		batch.begin();
		for (int i=0; i<Global.POWERUPS.length; i++) { //display the powerup info
			font.draw(batch, Global.POWERUPS[i].getName(), psnW/10, psnH); //print name
			font.draw(batch, Integer.toString(Global.POWERUPS[i].getCost()), psnW, psnH); //print cost
			psnH = psnH - Global.SCREEN_HEIGHT/12;
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
		System.out.println("show shop");

		System.out.println("player coins "+Global.getPlayer().getCoins());
		coinButton.setText(Global.getPlayer().getCoins()+" coins");

		//updates text on button based on conditions:
		//-player has bought item, does not have enough coins, or has not unlocked powerup
		for (int i=0; i<Global.POWERUPS.length; i++) {
			if (Global.getPlayer().hasPowerup(Global.POWERUPS[i].getName())) { //already bought
				buttons[i].setTouchable(Touchable.disabled);
				buttons[i].setText("Bought");
				//make it greyed out
			} else if (Global.getPlayer().getLevelProgress() < i) { //not unlocked yet
				buttons[i].setTouchable(Touchable.disabled);
				buttons[i].setText("Locked");
			} else if (Global.getPlayer().getCoins() < Global.POWERUPS[i].getCost()) { //not enough coins
				buttons[i].setTouchable(Touchable.disabled);
				buttons[i].setText("Not Enough Coins");
			} else { //available to buy
				buttons[i].setTouchable(Touchable.enabled);
				buttons[i].setText("Buy");
			}
		}
	}

	@Override
	public void hide() {
		Gdx.input.setInputProcessor(null);
		System.out.println("hide shop");

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
