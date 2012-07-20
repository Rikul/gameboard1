package com.rikulpatel.gameboard1;

import java.util.ArrayList;
import java.util.List;
import java.util.MissingResourceException;
import java.util.Scanner;

import android.content.Context;
import android.database.AbstractCursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.RectF;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewParent;
import android.widget.TextView;
import android.widget.Toast;


class mySurface extends View implements View.OnTouchListener {
	
	/**
	 * 
	 */
	private MyView myView = null;
	private final int MAXLEVELS=50;		
	boolean initDone = false;						
	int selectedTile = -1;		// currently selected tile 		
	Level[] myLevels = new Level[MAXLEVELS]; 		
	int padding=1;		// gap between tiles on board
	float tilesize=0;		// width / height of each tile
	int currentGridSize = 0;	// current selected level's grid size
	int currentLevelSize = 0;
	public int foundLevels = 0;	// number of levels 
	public int currentLevel = 0;
	int selectedPuzzleId = 0;
	float boardWidth=0, boardHeight=0;
	
	public mySurface(MyView myView, Context context) {
		super(context);
		this.myView = myView;
		
	}
	
	public mySurface(MyView myView, Context context, AttributeSet attrs) {
		super(context, attrs);
		this.myView = myView;
	}
	
	public mySurface(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public mySurface(MyView myView, Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.myView = myView;
	}
	
	
	public void init() {			   		 	
	 	initDone=true;   
		this.setOnTouchListener(this);
		setup();
	}
	
	public void setLevelSize(int levelSize) {
		this.currentLevelSize = levelSize;
	}
	
	
	public void setPuzzleId(int puzzleId) {
		this.selectedPuzzleId = puzzleId;
	}
	
	private ArrayList<Integer> parseGridDbString(String gridStr) {
		ArrayList<Integer> ret = new ArrayList<Integer>();
		String[] intList = gridStr.split("[ ]");
				
		for(int i=0; i< intList.length ; i++) {
			ret.add(Integer.valueOf(intList[i]));
		}
		return ret;
	}
		
	public void setParent(MyView p) {
		this.myView = p;
		ViewParent vp = this.getParent();
		
	}
	
	
	private void setup() {									
		
		// Get all levels for this board size 
	    gApp myApp = (gApp) myView.getApplicationContext(); 
	    AbstractCursor c = myApp.getLevelSizeRows(currentLevelSize);  
	    currentLevel=0;
	    
		for(int i=0; i < c.getCount(); i++) {
			/*			
			int resId = getResources().getIdentifier("nurikabe_5_" + String.valueOf(i+1), 
													"array", this.myView.getPackageName());
			if (resId == 0) 					
				break;
			*/				
			
			c.moveToPosition(i);
			foundLevels++;
			
			int gridIndex = c.getColumnIndex("grid");
			int levelSizeIndex = c.getColumnIndex("level_size");	
			int idIndex = c.getColumnIndex("id");
			
			if (c.getInt(idIndex) == selectedPuzzleId) 
				currentLevel = i;
			
			String gridStr = c.getString(gridIndex);			
			ArrayList<Integer> lData = parseGridDbString(gridStr);
			int dim =  c.getInt(levelSizeIndex);
						
			TextView headerText = (TextView) myView.findViewById(R.id.leveltext);			
			headerText.setText("LEVEL " + (currentLevel+1));
			headerText.setTypeface(((gApp)myView.getApplication()).getTypeface());

			TextView gridText = (TextView) myView.findViewById(R.id.gridtext);
			gridText.setText(dim  + "x" + dim);
			gridText.setTypeface(((gApp)myView.getApplication()).getTypeface());

			
			myLevels[i] = new Level(this.myView);
			myLevels[i].levelSize = dim; 
					
			for(int x=0; x<dim; x++) {
				for(int y=0; y<dim; y++) {
					int p = (x*dim) + y;
											
					myLevels[i].levelData[p]  = new Tile();						
					myLevels[i].levelData[p].solution = lData.get(p);
					myLevels[i].levelData[p].tileColor = Color.WHITE;
					if (lData.get(p) > 0) {														
						myLevels[i].levelData[p].state = lData.get(p);
					}
					
				}
			}				
			
			Log.d("setup", "i: " + i + " levelData: " + lData.toString());
		}
		
		if (foundLevels == 0) {
			throw new MissingResourceException("Game data not found", "gameboard", "nurikabe_" );
		}
		
				
		// x * y is grid size
		currentGridSize = myLevels[currentLevel].levelSize * myLevels[currentLevel].levelSize;  	
		int i=0;
	}
	
	public void drawGridLines(Canvas canvas, float tilesize) {
		//int width = boardWidth;
		//int height = boardHeight;
		Paint p = new Paint();
		p.setColor(Color.BLACK);
		ArrayList<Float> pts = new ArrayList<Float>();
		
		for(float x=0; x<=boardWidth; x += tilesize) {
			/*pts.add(x);
			pts.add((float) 0.00);
			pts.add(x);
			pts.add(boardHeight);*/
			canvas.drawLine(x, 0, x, boardHeight, p);
		}
		
		for(float y=0; y<=boardHeight; y += tilesize) {
			/*
			pts.add((float) 0.0);
			pts.add(y);
			pts.add(boardWidth);
			pts.add(y);*/
			canvas.drawLine(0, y, boardWidth, y, p);
		}
								
	}
	
	
	@Override
	public void draw(Canvas canvas) {
		
		if (initDone == false) {
			init();
		}
		
		// clear canvas
		Paint p = new Paint();
	 	p.setColor(Color.TRANSPARENT);
	 	p.setStyle(Style.FILL);	 	 	
	 	canvas.drawPaint(p);
	 	
	 	p.setColor(Color.WHITE);
	 	p.setTextAlign(Paint.Align.CENTER);	 		 	
	 	p.setAntiAlias(true); 
	 	p.setTypeface(((gApp)myView.getApplication()).getTypeface());
	 	
	 	Level cLevel = myLevels[currentLevel];
	 	int gridSize = cLevel.levelSize * cLevel.levelSize;
	 	if (cLevel.levelSize > 8) {
	 		p.setTextSize(28);
	 	} else p.setTextSize(30);
	 	
	 	// calculate size of each tile
	 	//tilesize = (getWidth() - ((cLevel.levelSize+1)*padding)) / cLevel.levelSize;	 	
	 	float gridTileSize = getWidth() / cLevel.levelSize;
	 	tilesize = gridTileSize - padding; 
	 	boardWidth = boardHeight = gridTileSize * cLevel.levelSize;
	 	
	 	drawGridLines(canvas, gridTileSize);
	 	
	 	for(int i=0; i<gridSize; i++) {
	 		RectF r = new RectF();
	 		
	 		// Get row and column 
	 		float x = i % cLevel.levelSize;
	 		float y = (i - x) / cLevel.levelSize;
	 	
	 		// calculate rectangle location and save locations
	 		r.top = myLevels[currentLevel].levelData[i].top =  (padding*(y+1)) + (tilesize*y);
	 		r.bottom = myLevels[currentLevel].levelData[i].bottom = (int) (r.top + tilesize) - padding;
	 		r.left = myLevels[currentLevel].levelData[i].left = (padding*(x+1)) + (tilesize*x);
	 		r.right = myLevels[currentLevel].levelData[i].right = (int) (r.left + tilesize) - padding;
	 		
	 			 		
	 		int color = cLevel.levelData[i].tileColor;		 		
	 		p.setColor(color);	 		
	 		canvas.drawRect(r, p);	 	
	 		
	 		
	 		// draw rect with labels
	 		String dispLabel = "";
	 		if (cLevel.levelData[i].state > 0) {		 			
	 			float xtext = r.right - (tilesize/2);
		 		float ytext = r.bottom - (tilesize/2);
		 		p.setColor(Color.BLACK);
		 		
	 			dispLabel = String.valueOf(cLevel.levelData[i].state);	
	 			
	 			/*
	 			String resName = this.myView.getPackageName() + ":drawable/number" + dispLabel;
	 			Log.d("draw", "resource name: " + resName);
	 			int bitmapId = getResources().getIdentifier(resName, null,null);
	 			Bitmap myBitmap = BitmapFactory.decodeResource(getResources(), bitmapId);	 			
	 			canvas.drawBitmap(myBitmap, null, r, null);*/			 				 			
	 			canvas.drawText(dispLabel, xtext, ytext, p);
	 		}
	 	}
	 		
		super.draw(canvas);
		invalidate();
	}

	private int getTileIndex(float x, float y) {
		Log.d("getTile", "x:" + x + "y:" + y);
		
		Tile[] tData = myLevels[currentLevel].levelData;
		
		// figure out with tile user clicks
		for(int i=0; i<currentGridSize; i++) {
			if ((tData[i].left <= x) && (tData[i].right >= x) && 
				(tData[i].top <= y) && (tData[i].bottom >= y)) {
				
				Log.d("getTile", "left:" + tData[i].left  + "right:" + tData[i].right +
								"top:" + tData[i].top + "bottom:" + tData[i].bottom);
				return i;				
			}	
		}
		return -1;	
	}
	
	private void tileSelected(int tIndex) {
		
		if (myLevels[currentLevel].levelData[tIndex].state > 0) {
			return;
		}
		
		// Switch state of tile when clicked
		if (myLevels[currentLevel].levelData[tIndex].state == 0) {
			myLevels[currentLevel].levelData[tIndex].state = -1;
			myLevels[currentLevel].levelData[tIndex].tileColor = Color.TRANSPARENT;
		} else {
			myLevels[currentLevel].levelData[tIndex].state = 0;
			myLevels[currentLevel].levelData[tIndex].tileColor = Color.WHITE;
		}
		
		// Check if puzzle is solved
		if (checkSolution() == true) {
			Toast.makeText(myView, String.valueOf("finished"), Toast.LENGTH_LONG).show();
		}
	}
	
	public boolean onTouch(View arg0, MotionEvent arg1) {
		if (arg1.getAction() != arg1.ACTION_DOWN) {
			return true;
		}
		
		int tileIndex = getTileIndex(arg1.getX(), arg1.getY());
		 						
		if (tileIndex != -1) {
		//	Toast.makeText(getApplicationContext(), "found tile #" + tileIndex, Toast.LENGTH_LONG).show();
		}	else
			return true;
		
		tileSelected(tileIndex);
		return true;
	}
	
	public void nextLevel() {
		
		if ((currentLevel+1) >= foundLevels) {
			return;
		}
					
		currentLevel++;			
		TextView headerText = (TextView) myView.findViewById(R.id.leveltext);
		headerText.setTypeface(((gApp)myView.getApplication()).getTypeface());
		headerText.setText("LEVEL " + (currentLevel+1));		
		
		TextView gridText = (TextView) myView.findViewById(R.id.gridtext);
		gridText.setTypeface(((gApp)myView.getApplication()).getTypeface());
		gridText.setText(this.currentLevelSize  + "x" + this.currentLevelSize);
		
		invalidate();
	}
	
	public void prevLevel() {
		if (currentLevel == 0)
			return;
		
		currentLevel--;
		TextView headerText = (TextView) myView.findViewById(R.id.leveltext);
		headerText.setTypeface(((gApp)myView.getApplication()).getTypeface());
		headerText.setText("LEVEL " + (currentLevel+1));
		
		TextView gridText = (TextView) myView.findViewById(R.id.gridtext);
		gridText.setTypeface(((gApp)myView.getApplication()).getTypeface());
		gridText.setText(currentLevelSize  + "x" + currentLevelSize);
		
		invalidate();
	}
	
	
	private int getNeighborCount(List<Integer> visited, int i) throws Exception {
		
		Tile[] tData = myLevels[currentLevel].levelData;
		int levelSize = myLevels[currentLevel].levelSize;
		// Get row and column 
	 	int x = i % levelSize;
	 	int y = (i - x) / levelSize;
	 	
	 	// if tile is blank return 0
		if (tData[i].state == -1)
			return 0;		
		
		// tile has label and is not our first tile with label 
		if ((tData[i].state > 0) && (visited.size() > 0) && (i != visited.get(0)))
			throw new Exception();
		
		// already here
		if (visited.contains(i))
			return 0;						
		visited.add(i);
		
		
		// add up all neighbors
		int ret = 0;
		if (y-1 >= 0)
			ret += getNeighborCount(visited, ((y-1)*levelSize) + x);	// top cell
		if (y+1 < levelSize)
			ret += getNeighborCount(visited, ((y+1)*levelSize) + x);	// bottom cell
		if (x-1 >= 0)
			ret += getNeighborCount(visited, (y*levelSize) + (x-1));	// left cell
		if (x+1 < levelSize)
			ret += getNeighborCount(visited, (y*levelSize) + (x+1));	// right cell
		
		return 1+ret;			
	}
	
	public boolean checkSolution() {
		Tile[] tData = myLevels[currentLevel].levelData;
		int gridSize = (int) Math.pow(myLevels[currentLevel].levelSize, 2);						
		boolean solutionOk = true;
		List<Integer> allVisited = new ArrayList<Integer>();	// stores all valid tiles
		
		for(int i=0; i<gridSize; i++) {								 				
			if (tData[i].state > 0) {					
				List<Integer> visited = new ArrayList<Integer>() ;				
				try {
					int neighborCount = getNeighborCount(visited, i);
					if (neighborCount != tData[i].state)
						solutionOk = false;					
					else
						allVisited.addAll(visited);
					
				} catch (Exception e) {						
					e.printStackTrace();
					solutionOk = false;
					break;
				}
			}
		}
		
		// find any empty non blank cells 
		if (solutionOk == true) {
			for(int i=0; i < gridSize; i++) {
				if (tData[i].state == 0) {
					if (! allVisited.contains(i)) {
						solutionOk = false;
						break;
					}
				}
			}
		}
		
		return solutionOk;					
	}
	
}