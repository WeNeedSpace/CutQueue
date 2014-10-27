package com.michelle_tan.cutqueue;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;

public class CutQueue extends Game{
	AssetManager assets;
	
	//variables for each screen to be accessed in other screens
	LoadingScreen loadingScreen;
	MainMenuScreen mainMenuScreen;
	GameScreen gameScreen;
	EndGameScreen endGameScreen;
	PersonalScoreScreen personalScoreScreen;
	StageSelectScreen stageSelectScreen;
	TutorialScreen tutorialScreen;
	BonusStageScreen bonusStageScreen;
	PlayerService playerService;
	CreditsScreen creditsScreen;
	
	Music bgmMain;
	boolean playingGame;
	
	public CutQueue() {
		playerService = new PlayerService();
		//fbConnect = fb;
		
	}
	
	@Override
	public void create () {
		System.out.println("cutqueue create");
		Global.setGame(this);
		
		playingGame = false;
		
		bgmMain = Gdx.audio.newMusic(Gdx.files.internal("bgm_main.mp3"));
		bgmMain.setLooping(true);
		bgmMain.play();
		
		//init all screens
		loadingScreen = new LoadingScreen(this);
		
        this.setScreen(loadingScreen);
       
	}

	@Override
	public void render () {
		super.render();
	}
	
	@Override 
	public void resume() {
		super.resume();
		System.out.println("game resumed");
	}
	
	@Override
	public void pause() {
		super.pause();
	}
	
	@Override
	public void dispose(){
		playerService.persist(); //save player data before closing game
		playerService.persistScore(); //save score data before closing game
		assets.dispose();
		super.dispose();
	}
	
	//called after assets are done loading in LoadingScreen
	public void createScreens() {
		System.out.println("create screens");
		
		mainMenuScreen = new MainMenuScreen(this);
		gameScreen = new GameScreen(this);
		endGameScreen = new EndGameScreen(this);
		personalScoreScreen = new PersonalScoreScreen(this);
		stageSelectScreen = new StageSelectScreen(this);
		tutorialScreen = new TutorialScreen(this);
		bonusStageScreen = new BonusStageScreen(this);
		creditsScreen = new CreditsScreen(this);

		this.setScreen(mainMenuScreen);
	}
	
	//plays music if on game screen (start up main music before exiting game screen)
	public void playMusic() {
		if (playingGame) {
			bgmMain.setVolume(Global.volume);
			bgmMain.play();
			playingGame = false;
		} 
	}
	
	//stops music if not on game screen (stop main music before going to game screen)
	public void stopMusic() {
		if (!playingGame) {
			bgmMain.stop();
			playingGame = true;
		}
	}
}
