package com.michelle_tan.cutqueue;

import java.util.ArrayList;
import java.util.HashMap;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.swarmconnect.Swarm;
import com.swarmconnect.SwarmActiveUser.GotCloudDataCB;

public class Global {
	//the game
	private static CutQueue game;

	//FINAL VALUES
	public final static boolean IS_SOCIAL = true;

	//get screen width and height
	public final static int SCREEN_WIDTH = Gdx.graphics.getWidth();
	public final static int SCREEN_HEIGHT = Gdx.graphics.getHeight();
	public final static int BUTTON_WIDTH = SCREEN_WIDTH/4;
	public final static int BUTTON_HEIGHT = SCREEN_HEIGHT/12;
	public final static float ASPECT_RATIO = (float)Gdx.graphics.getWidth()/(float)Gdx.graphics.getHeight();
	public final static float SCREEN_SCALE = (float)Gdx.graphics.getWidth()/(float)480;
	public static float FONT_SCALE = (float)Gdx.graphics.getWidth()/(float)480;


	//level to show at game screen, follows index of levels
	public static int currLevel = 0;

	public final static String[] STATIONS = {"Bishan",
		"Braddell",
		"Toa Payoh",
		"Novena",
		"Newton",
		"Orchard",
		"Somerset",
		"Dhoby Ghaut",
		"City Hall",
		"Raffles Place",
		"Marina Bay"
	};

	public final static Level[] LEVELS = {
		new Level(0, "Bishan", 6, 0, 150, true, 16740),
		new Level(1, "Braddell", 15, 1, 100, false, 16742),
		new Level(2, "Toa Payoh", 20, 2, 200, false, 16746),
		new Level(3, "Novena", 25, 3, 300,false, 16748),
		new Level(4, "Newton", 30, 4, 300, false, 16750),
		new Level(5, "Orchard", 35, 5, 300, false, 16752),
		new Level(6, "Somerset", 40, 6, 300, false, 16754),
		new Level(7, "Dhoby Ghaut", 45, 7, 300, false, 16756),
		new Level(8, "City Hall", 50, 8, 300, false, 16758),
		new Level(9, "Raffles Place", 55, 9, 300, false, 16760),
		new Level(10, "Marina Bay", 60, 10, 300, false, 16762)};

	public final static int NUM_LEVELS = LEVELS.length;

	public static enum Achievements {
		STAGE_FIRST(21070),
		BONUS_FIRST(21222),
		GRANDMA_1(21224),
		GRANDMA_10(21226),
		GRANDMA_20(21228),
		GRANDMA_30(21230),
		KIDS_10(21232),
		KIDS_50(21234),
		WOMEN_10(21236),
		WOMEN_50(21238),
		MEN_10(21240),
		MEN_50(21242),
		BONUS_5(21244),
		BONUS_10(21246),
		STAGES_HALF(21248),
		STAGES_ALL(21250),
		STAGES_FAST(21252);

		private final int id;

		Achievements(int value) {
			id = value;
		}

		public int value() {
			return id;
		}
	}

	//MUTABLE STATES
	private static Player currPlayer = new Player();
	private static TextureRegion playerPicture = null;

	private static boolean gameState = false; //whether player has won or not

	//sound
	public static boolean sound = true;
	public static float volume = 0.5f;
	public static float sfxVolume = 1.0f;

	//swarm connect
	private static boolean loggedIn = false;
	public static boolean playerDataLoaded = false;
	private static int lastScore;

	/**
	 * Tutorial and Bonuses!
	 * 
	 */
	//Tutorial state
	private static boolean flag=false;

	//Bonus state
	private static int bonusScore;
	private static boolean fromBonus;
	private static ArrayList<Image> oldCharArray;
	private static int oldClock;


	//highscores saved on device, only top score for each level saved
	private static Scores scores = new Scores();

	//methods for sound
	public static void onSound() {
		sound = true;
		volume = 0.5f;
		sfxVolume = 1.0f;
	}

	public static void offSound() {
		sound = false;
		volume = 0.0f;
		sfxVolume = 0.0f;
	}

	/*
	 * Getter and setter methods for game, login, player, scores
	 */
	public static CutQueue getGame() {
		return game;
	}

	public static void setGame(CutQueue g) {
		System.out.println("setGame");
		FONT_SCALE = (float)Gdx.graphics.getWidth()/(float)480;
		game = g;
	}

	public static boolean isLoggedIn() {
		return loggedIn;
	}

	public static void setLoggedIn(boolean log) {
		loggedIn = log;
	}

	public static boolean getGameState() {
		return gameState;
	}

	public static void setGameState(boolean state) {
		gameState = state;
	}

	public static int getLastScore() {
		return lastScore;
	}

	public static void setLastScore(int score) {
		lastScore = score;
	}

	public static Player getPlayer() {
		return currPlayer;
	}

	public static void setPlayer(Player play) {
		currPlayer = play;
	}

	public static void setPlayerDetails(String name, int id, String url) {
		currPlayer.setName(name);
		currPlayer.setUserId(id);
		currPlayer.setPicture(url);
	}

	public static TextureRegion getPlayerPicture() {
		return playerPicture;
	}

	public static void setPlayerPicture(TextureRegion tex) {
		playerPicture = tex;
	}

	public static Scores getScores() {
		return scores;
	}

	public static void setScores(Scores score) {
		scores = score;
	}

	public static void readState() {
		System.out.println("readState, not logged in, reading json");
		scores = game.playerService.retrieveScores();
		currPlayer = game.playerService.retrieveProfile();
	}

	/**
	 * 
	 * Tutorial methods.
	 * @return
	 */
	public static boolean getFlag(){
		return flag;
	}
	public static void setFlag(boolean bool){
		flag = bool;
	}
	/**
	 * 
	 * Bonus methods
	 * @return
	 */
	public static int getBonusScore(){
		return bonusScore;
	}
	public static void setBonusScore(int bonus){
		bonusScore = bonus;
	}
	public static boolean comingFromBonus(){
		return fromBonus;
	}
	public static void setComingFromBonus(boolean bool){
		fromBonus = bool;
	}
	public static int getOldClock(){
		return oldClock;
	}
	public static void setOldClock(int time){
		oldClock = time;
	}
	public static ArrayList<Image> getOldCharArray(){
		return oldCharArray;
	}
	public static void setOldCharArray(ArrayList<Image> charArr){
		oldCharArray = charArr;
	}


	//takes care of end game stuff
	public static void endGame(boolean win, int score) {
		gameState = win;
		lastScore = (win ? score : score/2); //score is halved if game was lost

		currPlayer.unlockLevel(currLevel); //unlock new level
		if (submitScore(currLevel, score)) { //returns true if new high score
			System.out.println("yay new high score!");
		}

		saveState();

	}

	/* submitScore
	 * Overwrites existing highscore if submitted score is higher for that
	 * level, returns true if overwritten.
	 */
	public static boolean submitScore(int levelId, int score) {
		return scores.submitScore(levelId, score, currPlayer.getName());
	}

	public static void saveState() {
		game.playerService.persistScore(); //save scores after unlocking stage

		//save user's progress, coins, powerups (by array id & boolean) to swarm
		if (Swarm.isLoggedIn()) {
			Swarm.user.saveCloudData("levelProgress", ""+currPlayer.getLevelProgress());

			/*
			//create an array to save
			String s = "";
			for (int i=0; i<POWERUPS.length; i++) {
				//if the player has this powerup, add true
				if (currPlayer.hasPowerup(POWERUPS[i].getName())) {
					s+="true";
				} else { //else, add false 
					s+="false";
				}

				//put a comma at the end, except on the last powerup
				if (i != POWERUPS.length-1) {
					s+=",";
				}
			}

			Swarm.user.saveCloudData("powerupsBought", s);
			 */

		} else { //if user is offline, save to json
			game.playerService.persist(); //save player after unlocking stage
		}
	}

	/* loadState
	 * Loads player data from Swarm if player is logged in.
	 */
	public static void loadState() {
		if (Swarm.isLoggedIn()) {

			Swarm.user.getCloudData("levelProgress", new GotCloudDataCB() {
				public void gotData(String data) {
					if (data != null) {
						// If the key has been set, parse and set global var
						if (data.length() != 0) {
							// Set player progress
							currPlayer.setLevelProgress(Integer.parseInt(data)); 
							System.out.println("level progress retrieved");
						}
					}
				}
			});

		}
	}

	public static void main(String[] arg) {
		System.out.println("hello main");
		if (FONT_SCALE == 0) {
			System.out.println("font "+FONT_SCALE);
			System.out.println("font "+FONT_SCALE);
		}
	}

}
