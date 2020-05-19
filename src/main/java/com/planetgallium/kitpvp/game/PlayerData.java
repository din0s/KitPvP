package com.planetgallium.kitpvp.game;

public class PlayerData {
	
	private String username;
	private int level;
	private int experience;
    private int kills;
    private int deaths;
    private int soups;
   
    public PlayerData(String username, int level, int experience, int kills, int deaths, int soups) {
    	this.username = username;
    	this.level = level;
    	this.experience = experience;
    	this.kills = kills;
    	this.deaths = deaths;
    	this.soups = soups;
    }

    public void addKills(int amount) {
    	
        this.kills += amount;
        
    }

    public void addDeaths(int amount) {
    	
        this.deaths += amount;
        
    }

    public void setExperience(int amount) {
    	
        this.experience = amount;
        
    }

    public void setLevel(int level) {
    	
        this.level = level;
        
    }

    public void addSoup() {

        this.soups++;

    }

    public void resetSoups() {

        this.soups = 0;

    }
    
    public int getKills() { return kills; }

    public int getDeaths() { return deaths; }

    public int getExperience() { return experience; }

    public int getLevel() { return level; }

    public String getUsername() { return username; }

    public int getSoups() { return soups; }
}


