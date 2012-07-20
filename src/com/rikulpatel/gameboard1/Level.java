package com.rikulpatel.gameboard1;


class Level {
	
	/**
	 * 
	 */
	private final MyView myView;
	final int maxTileSize = 256; 

	int levelNum, levelSize;
	
	Tile[] levelData = new Tile[maxTileSize];
	
	public Level(MyView myView) {
		this.myView = myView;
		levelSize=5;	// num of rows and cols 
		levelNum=1;
	}
}