package com.michelle_tan.cutqueue;

public class Level {
	
	//private vars so levels cannot be modified after construction
	private int levelId; //following index of Global.station[]
	private String levelName; //station name
	private int spawnAmt; //number of characters spawned
	private int bossAmt; //number of bosses spawned
	private int timeLimit; //in seconds
	private boolean hasBonusStage; //true or false to tell whether there's a bonus stage
	private int swarmLeaderboardId;
	
	/* Level
	 * Creates a Level with level number (following index of Global.station[]),
	 * level name, amount of characters spawned and time limit.
	 */
	public Level(int id, String name, int chars, int boss, int time, boolean bonus, int leaderboard) {
		levelId = id;
		levelName = name;
		spawnAmt = chars;
		bossAmt = boss;
		timeLimit = time;
		hasBonusStage = bonus; //here too
		swarmLeaderboardId = leaderboard;
	}
	
	//get methods to return level vars
	public int getLevelId() {
		return levelId;
	}
	
	public String getLevelName() {
		return levelName;
	}
	
	public int getSpawnAmount() {
		return spawnAmt;
	}
	
	public int getBossAmount() {
		return bossAmt;
	}
	
	public int getTimeLimit() {
		return timeLimit;
	}
	//added bonus hehe
	public boolean getBonusStage(){
		return hasBonusStage;
	}
	
	public int getLeaderboard() {
		return swarmLeaderboardId;
	}
}
