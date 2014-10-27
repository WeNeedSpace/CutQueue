package com.michelle_tan.cutqueue;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Base64Coder;
import com.badlogic.gdx.utils.Json;

/**
 * PlayerService
 * Reads and writes player data from an internal JSON file.
 * 
 * Based on Gustavo Steigert's tutorial for saving game state:
 * http://steigert.blogspot.be/2012/03/5-libgdx-tutorial-files.html 
 * https://code.google.com/p/steigert-libgdx/source/browse/trunk/tyrian-game/src/com/blogspot/steigert/tyrian/services/ProfileService.java?r=19&spec=svn18
 * 
 */
public class PlayerService
{	
	// the location of the profile data file
	private static final String PROFILE_DATA_FILE = ".cutqueue/profile-v1.json";
	private static final String SCORE_DATA_FILE = ".cutqueue/scores-v1.json";

	// the loaded profile (may be null)
	private Player profile;

	//the loaded scores (may be null)
	private Scores scores;

	/*
	 * Creates the profile service.
	 */
	public PlayerService()
	{
	}

	//FOR PLAYERS
	/**
	 * Retrieves the player's profile, creating one if needed.
	 */
	public Player retrieveProfile()
	{

		// if the profile is already loaded, just return it
		if(profile != null) return profile;

		FileHandle profileDataFile = Gdx.files.external(PROFILE_DATA_FILE);

		// create the JSON utility object
		Json json = new Json();

		// check if the profile data file exists
		if(profileDataFile.exists()) {
			System.out.println("data exists");
			// load the profile from the data file
			try {

				// read the file as text
				String profileAsCode = profileDataFile.readString();

				// decode the contents
				String profileAsText = Base64Coder.decodeString(profileAsCode);

				// restore the state
				profile = json.fromJson(Player.class, profileAsText);
				System.out.println(profile.getLevelProgress());

			} catch(Exception e) {

				// log the exception
				System.out.println("Unable to parse existing profile data file");

				// recover by creating a fresh new profile data file;
				// note that the player will lose all game progress
				profile = new Player();
				persist(profile);

			}

		} else {
			// create a new profile data file
			profile = new Player();
			persist(profile);
		}

		// return the result
		return profile;
	}

	/**
	 * Persists the given profile.
	 */
	protected void persist(Player profile)
	{
		System.out.println("Persisting profile");

		// create the JSON utility object
		Json json = new Json();

		FileHandle profileDataFile = Gdx.files.external(PROFILE_DATA_FILE);

		// convert the given profile to text
		String profileAsText = json.toJson(profile);

		// encode the text
		String profileAsCode = Base64Coder.encodeString(profileAsText);

		// write the profile data file
		profileDataFile.writeString(profileAsCode, false);
	}

	/**
	 * Persists the player's profile.
	 * 
	 * If no profile is available, this method does nothing.
	 */
	public void persist()
	{
		if(profile != null) {
			persist(profile);
		}
	}

	//FOR SCORES
	public Scores retrieveScores()
	{

		// if the profile is already loaded, just return it
		if(scores != null) return scores;

		// create the handle for the profile data file
		FileHandle scoreDataFile = Gdx.files.external(SCORE_DATA_FILE);

		// create the JSON utility object
		Json json = new Json();

		// check if the profile data file exists
		if(scoreDataFile.exists()) {
			System.out.println("data exists");
			// load the profile from the data file
			try {

				// read the file as text
				String scoreAsCode = scoreDataFile.readString();

				// decode the contents
				String scoreAsText = Base64Coder.decodeString(scoreAsCode);

				// restore the state
				scores = json.fromJson(Scores.class, scoreAsText);

			} catch(Exception e) {

				// log the exception
				System.out.println("Unable to parse existing score data file");
				e.printStackTrace();
				// recover by creating a fresh new profile data file;
				// note that the player will lose all game progress
				scores = new Scores();
				persist(scores);

			}

		} else {
			// create a new score data file
			scores = new Scores();
			persist(scores);
		}

		// return the result
		return scores;
	}

	/**
	 * Persists the given scores.
	 */
	protected void persist(Scores score)
	{
		System.out.println("Persisting score");

		// create the JSON utility object
		Json json = new Json();

		// create the handle for the score data file
		FileHandle scoreDataFile = Gdx.files.external(SCORE_DATA_FILE);

		// convert the given score to text
		String scoreAsText = json.toJson(score);

		// encode the text
		String scoreAsCode = Base64Coder.encodeString(scoreAsText);

		// write the profile data file
		scoreDataFile.writeString(scoreAsCode, false);
	}

	/**
	 * Persists the player's profile.
	 * 
	 * If no profile is available, this method does nothing.
	 */
	public void persistScore()
	{
		if(scores != null) {
			persist(Global.getScores());
		}
	}
}
