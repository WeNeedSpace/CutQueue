package com.michelle_tan.cutqueue;

import android.os.Bundle;
import android.widget.Toast;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.michelle_tan.cutqueue.CutQueue;
import com.swarmconnect.Swarm;
import com.swarmconnect.SwarmActiveUser;
import com.swarmconnect.SwarmActiveUser.GotCloudDataCB;
import com.swarmconnect.SwarmLoginManager;
import com.swarmconnect.delegates.SwarmLoginListener;

public class AndroidLauncher extends AndroidApplication {

	//AndroidFacebookConnection afc;
	CutQueue game;

	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		//afc = new AndroidFacebookConnection(this);
		game = new CutQueue();
		initialize(game, config);

		Swarm.setAllowGuests(true); //allow guest accounts
		Swarm.setActive(this);

	}

	@Override
	public void onResume() {
		System.out.println("onResume");
		super.onResume();
		Swarm.setActive(this);

		initSwarm();
	}

	@Override
	public void onPause() {
		super.onPause();
		Swarm.setInactive(this);
	}

	public void initSwarm() {
		Swarm.init(this, 12144, "d7a338dfbd5a03ff6eaffde96ce9451d");
		SwarmLoginManager.addLoginListener(new SwarmLoginListener() {

			@Override
			public void loginCanceled() {
				System.out.println("login cancelled");
				//showShortToast("Login cancelled.");
				game.setScreen(game.mainMenuScreen);
			}

			@Override
			public void loginStarted() {
				System.out.println("login started");
			}

			@Override
			public void userLoggedIn(SwarmActiveUser arg0) {
				System.out.println("login success");
				showShortToast("Welcome, "+arg0.username);
				Global.setLoggedIn(true);
				Global.setPlayerDetails(arg0.username, arg0.userId, arg0.picUrl); //update player with username and pic
				Global.loadState();

				game.setScreen(game.mainMenuScreen);
			}

			@Override
			public void userLoggedOut() {
				System.out.println("logged out");
				Global.setLoggedIn(false);	
				game.setScreen(game.mainMenuScreen);
			}

		});

	}

	// Show a message in a toast
	void showShortToast(String msg) {
		Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
	}

	// Show a message in a toast
	void showLongToast(String msg) {
		Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
	}
}
