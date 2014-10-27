package com.michelle_tan.cutqueue;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.Json.Serializable;
import com.badlogic.gdx.utils.JsonValue;

public class Scores implements Serializable{
	private HashMap<Integer, String> playerNames;
	private HashMap<Integer, Integer> scores; //keys are level ids

	public Scores() {
		playerNames = new HashMap<Integer, String>();
		scores = new HashMap<Integer, Integer>();
	}

	public int getScore(int levelId) {
		if (!scores.isEmpty() && scores.containsKey(levelId)) {
			return scores.get(levelId);
		} else {
			return 0;
		}

	}

	public String getName(int levelId) {
		if (!playerNames.isEmpty() && playerNames.containsKey(levelId)) {
			return playerNames.get(levelId);
		} else {
			return " ";
		}
	}

	public boolean submitScore(int levelId, int score, String player) {
		if (getScore(levelId) < score) { //if new score is higher
			scores.put(levelId, score); //update scores
			playerNames.put(levelId, player); //update names
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void write(Json json) {
		System.out.println("writing json values");
		json.writeValue("playerNames", playerNames);
		json.writeValue("scores", scores);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void read(Json json, JsonValue jsonData) {
		System.out.println("reading json values");
		// libgdx handles the keys of JSON formatted HashMaps as Strings, but we
		// want it to be an integer instead 
		Map<String,String> names = json.readValue("playerNames", HashMap.class,
				String.class, jsonData);
		for(String levelIdAsString : names.keySet()) {
			int levelId = Integer.valueOf(levelIdAsString);
			String s = names.get(levelIdAsString);
			this.playerNames.put(levelId, s);
		}

		// libgdx handles the keys of JSON formatted HashMaps as Strings, but we
		// want it to be an integer instead 
		Map<String,Integer> highScores = json.readValue("scores", HashMap.class,
				Integer.class, jsonData);
		for(String levelIdAsString : highScores.keySet()) {
			int levelId = Integer.valueOf(levelIdAsString);
			Integer highScore = highScores.get(levelIdAsString);
			this.scores.put(levelId, highScore);
		}
	}


}
