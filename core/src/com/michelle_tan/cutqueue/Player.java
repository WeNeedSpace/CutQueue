package com.michelle_tan.cutqueue;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.Json.Serializable;
import com.badlogic.gdx.utils.JsonValue;
import com.swarmconnect.Swarm;

public class Player implements Serializable{

	//player identity
	private String playerName; //'Local' if not set, let player set player name
	private int playerUserId; //swarm user id
	private boolean socialLoggedIn; //if player has logged in w Swarm
	private String playerPic;
	
	//player progress
	private int levelProgress; //last unlocked stage

	//player scores
	private Map<Integer, Integer> scores; //map level id to scores

	public Player() {
		playerName = "Local";
		playerUserId = 0;
		socialLoggedIn = false;
		playerPic = null;
		levelProgress = 0;
		scores = new HashMap<Integer, Integer>();
	}
	
	/*
	 * Getter and setter methods for name, picture, level progress, scores
	 */
	public void setName(String name) {
		playerName = name;
	}
	
	public String getName() {
		return playerName;
	}
	
	public void setUserId(int id) {
		playerUserId = id;
	}
	
	public int getUserId() {
		return playerUserId;
	}
	
	public void setPicture(String url) {
		playerPic = url;
	}
	
	public String getPicture() {
		return playerPic;
	}

	public int getLevelProgress() {
		return levelProgress;
	}
	
	public void setLevelProgress(int progress) {
		levelProgress = progress;
	}

	//unlocks next level
	public void unlockLevel(int levelId) {
		if (levelId == levelProgress) {
			levelProgress++;
			//save level progress to swarm
			if (Swarm.isLoggedIn()) {
			    Swarm.user.saveCloudData("levelProgress", ""+levelProgress);
			}
		}
	}

	public Map<Integer, Integer> getScores() {
		return scores;
	}

	public int getHighScore(int levelId) {
		if (scores.containsKey(levelId)) { //if score already exists, get score
			return scores.get(levelId);
		} else { //add new high score
			return 0;
		}
	}

	public void saveScore(int levelId, int score) {
		if (scores.containsKey(levelId)) { //if score already exists, compare
			if (scores.get(levelId) < score) { 	//if new score is higher than previous, overwrite
				scores.remove(levelId);
				scores.put(levelId, score);
			}
		} else { //add new high score
			scores.put(levelId, score);
		}
	}

	@Override
	public void write(Json json) {
		System.out.println("writing json values");
		json.writeValue("levelProgress", levelProgress);
		json.writeValue("scores", scores);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void read(Json json, JsonValue jsonData) {
		System.out.println("reading json values");
		
		levelProgress = json.readValue("levelProgress", Integer.class, jsonData);
		// libgdx handles the keys of JSON formatted HashMaps as Strings, but we
		// want it to be an integer instead (levelId)
		Map<String,Integer> highScores = json.readValue("scores", HashMap.class,
				Integer.class, jsonData);
		for(String levelIdAsString : highScores.keySet()) {
			int levelId = Integer.valueOf(levelIdAsString);
			Integer highScore = highScores.get(levelIdAsString);
			this.scores.put(levelId, highScore);
		}
	}

}
