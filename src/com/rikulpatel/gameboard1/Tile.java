package com.rikulpatel.gameboard1;

import android.graphics.Color;

class Tile {
	
	public float top, left, bottom, right; 
	public int tileColor;
	public int state, solution;
	
	public Tile() {
		top=left=bottom=right=0;
		state=0;
		solution=0;
		tileColor=Color.WHITE;
	}	
}