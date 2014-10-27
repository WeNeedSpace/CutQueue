package com.michelle_tan.cutqueue;

/* Powerup
 * Represents a powerup/ability to be bought in the shop
 * and used in-game.
 */
public class Powerup {
	private String name;
	private int cost;
	
	public Powerup(String pName, int pCost) {
		name = pName;
		cost = pCost;
	}
	
	public void setName(String pName) {
		name = pName;
	}
	
	public String getName() {
		return name;
	}
	
	public void setCost(int pCost) {
		cost = pCost;
	}
	
	public int getCost() {
		return cost;
	}
}
