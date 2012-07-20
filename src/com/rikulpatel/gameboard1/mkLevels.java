package com.rikulpatel.gameboard1;

import java.io.InputStream;
import java.lang.reflect.Array;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.Random;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.AbstractCursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Picture;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.rikulpatel.gameboard1.R;

public class mkLevels extends Activity implements OnClickListener {
		
	private final int maxTileSize = 256; 
	private static final int PREV_ID = 1;
	private static final int NEXT_ID = 2;
	private static final int CHECK_SOL_ID = 3;
	private static final int RELOAD_ID = 4;
	private static final int SAVE_ID = 5;
	private static final int SETSTATE_ID = 6;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	      super.onCreate(savedInstanceState);
	      setContentView(R.layout.mklevels_view);	
	      
	      int h=getWindowManager().getDefaultDisplay().getHeight();
	      int w=getWindowManager().getDefaultDisplay().getWidth();
	      
	      // Create square view in middle of layout	
	      int sz=0;
	      if (h>w) sz=w; 
	      else sz=h;
	      
	      myBoard = new mySurface(this);	      
	      LinearLayout layout = (LinearLayout) findViewById(R.id.mklevels_view);
	      LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(sz,sz);
	      myBoard.setLayoutParams(params);	      
	      layout.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);	      
	      layout.addView(myBoard, params);
	      	      
	      
	      LinearLayout layout_nav = new LinearLayout(this);
	      layout_nav.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, 100));
	      
	      
	      Button mksol = new Button(this);
	      mksol.setText("make solution");
	      mksol.setId(this.PREV_ID);
	      mksol.setOnClickListener(this);
	      
	      
	      Button check = new Button(this);
	      check.setText("Save");
	      check.setId(this.SAVE_ID);
	      check.setOnClickListener(this);
	      
	      /*
	      Button next = new Button(this);
	      next.setText("Next");
	      next.setId(this.NEXT_ID);
	      next.setOnClickListener(this);*/
	      
	      
	      Button reload = new Button(this);
	      reload.setText("Reload");
	      reload.setId(this.RELOAD_ID);
	      reload.setOnClickListener(this);
	      
	      EditText setState = new EditText(this);
	      setState.setId(this.SETSTATE_ID);	     
	      setState.setTag(this.SETSTATE_ID);
	      setState.setWidth(50);
	      	      
	      //layout_nav.addView(prev);
	      layout_nav.addView(check);
	      //layout_nav.addView(next);
	      layout_nav.addView(reload);
	      layout_nav.addView(setState);
	      
	      layout.addView(layout_nav);
	      
	}
	
	private class Tile {
		
		public int top, left, bottom, right; 
		public int tileColor;
		public int state, solution;
		
		public Tile() {
			top=left=bottom=right=0;
			state=0;
			solution=0;
			tileColor=Color.WHITE;
		}	
	}
	
	private class Level {
		
		int levelNum, levelSize;
		
		Tile[] levelData = new Tile[maxTileSize];
		
		public Level() {
			levelSize=5;	// num of rows and cols 
			levelNum=1;
		}
	}
	
	private class mySurface extends View implements View.OnTouchListener {
		
		boolean initDone = false;						
		int selectedTile = -1;		// currently selected tile
		final int MAXLEVELS = 10;		
		Level[] myLevels = new Level[MAXLEVELS]; 
		
		int padding=5;		// gap between tiles on board
		int tilesize=0;		// width / height of each tile
		int currentGridSize = 0;	// current selected level's grid size
		
		public int foundLevels = 0;	// number of levels 
		public int currentLevel = 0;
		
		private class CellT {
			int index=0, state=0;
		}
		
		private class CellTCompare implements Comparator<CellT> {

			public int compare(CellT lhs, CellT rhs) {
				// TODO Auto-generated method stub
				return ((lhs.state > rhs.state) ? 1 : (lhs.state == rhs.state ? 0 : -1)); 
			}
			
		}
		
		public mySurface(Context context) {
			super(context);
			
			this.setOnTouchListener(this);
		
			// TODO Auto-generated constructor stub
		}
		
		public mySurface(Context context, AttributeSet attrs) {
			super(context, attrs);
		}
		
		public mySurface(Context context, AttributeSet attrs, int defStyle) {
			super(context, attrs, defStyle);
		}
		
		
		public void init() {			   		 	
   		 	initDone=true;   		 	
			setup();
		}
		
		/*		
		public void findTileSolution(List<CellT> stateList, List<Integer> lData, int index) {
			
		}*/
		
		
		/**
		 * returns true if any neighbors are non empty
		 * @param lData
		 * @param currentState
		 * @param index
		 * @return
		 */
		private int hasNeighborState(List<Integer> lData, int currentState, int index) {
			
			int levelSize = (int) Math.sqrt(lData.size());
			int x = index % levelSize;
		 	int y = (index - x) / levelSize;
		 			 			  
 			int ret = 0;
 			
 			if ((y-1) >= 0) {
 				int p = ((y-1)*levelSize) + x;
 				if ((lData.get(p) != -1) && (lData.get(p) != currentState))
 					return 1;
 			}
 				 			
 			if ((y+1) < levelSize) {
 				int p = ((y+1)*levelSize) + x;
 				if ((lData.get(p) != -1) && (lData.get(p) != currentState))
 					return 1;
 			}
 				
 			if ((x-1) >= 0) {
 				int p = (y*levelSize) + (x-1);
 				if ((lData.get(p) != -1) && (lData.get(p) != currentState))
 					return 1;
 			}
 			
 			if ((x+1) < levelSize) {
 				int p = (y*levelSize) + (x+1);
 				if ((lData.get(p) != -1) && (lData.get(p) != currentState)) 			
 					return 1;
 			}
 			
			return 0;			
		}
		
		private boolean makeSolution(List<Integer> lData) {
			
			List<CellT> stateList = new ArrayList();  
			
			for(int i=0; i<lData.size(); i++) {
				if (lData.get(i) > 0) {
					//findTileSolution(lData, i);
					CellT c = new CellT();
					c.index = i;
					c.state = lData.get(i);
					stateList.add(c);
				}
			}					
			Collections.sort(stateList, new CellTCompare());			
			int levelSize = (int) Math.sqrt(lData.size());
			
			for(int i=0; i<stateList.size(); i++) {
				CellT thisCell = stateList.get(i);
				List<CellT> addCells = new ArrayList();
				
				if (hasNeighborState(lData, thisCell.state, thisCell.index) > 0) {
					Log.d("makeSolution", "makeSolution failed for " + thisCell.state + " " + thisCell.index);
					break;
				}
	
				int x = thisCell.index % levelSize;
			 	int y = (thisCell.index - x) / levelSize;
				
			}
			
			return false;			
		}
		
		public void mksol() {			
		}
		
		private void setup() {									
			
			int currentGameSize = 10;
			int currentGridSize = currentGameSize * currentGameSize;

			foundLevels=0;
			int i=0;
			
			//for(int i=0; i<MAXLEVELS; i++) {
				/*
				int resId = getResources().getIdentifier("nurikabe_5_" + String.valueOf(i+1), 
														"array", getPackageName());
				if (resId == 0) 					
					break;
				*/
				
				//int[] lData = getResources().getIntArray(resId);
				
				List<Integer> lData = new ArrayList<Integer>(currentGridSize);								
				for (int k=1; k<currentGameSize; k++)
					lData.add(k);				
				for(int k=currentGameSize; k <=currentGridSize; k++) {
					lData.add(-1); 
				}
								
				Collections.shuffle(lData);
				makeSolution(lData);
				
				foundLevels++;
				int dim = (int) Math.sqrt(lData.size());
				myLevels[i] = new Level();
				myLevels[i].levelSize = dim; 
						
				for(int x=0; x<dim; x++) {
					for(int y=0; y<dim; y++) {
						int p = (x*dim) + y;												
						myLevels[i].levelData[p]  = new Tile();						
						myLevels[i].levelData[p].solution = lData.get(p);
						myLevels[i].levelData[p].state = lData.get(p);

						if (lData.get(p) >= 0)
							myLevels[i].levelData[p].tileColor = Color.WHITE;
						else
							myLevels[i].levelData[p].tileColor = Color.BLACK;																			
					}
				}				
				
				
				Log.d("setup", "i: " + i + " levelData: " + lData.toString());
			//}
			
			if (foundLevels == 0) {
				throw new MissingResourceException("Game data not found", "gameboard", "nurikabe_" );
			}
			
			currentLevel=0;			
			// x * y is grid size
			//currentGridSize = myLevels[currentLevel].levelSize * myLevels[currentLevel].levelSize;  	
		}
		
		@Override
		public void draw(Canvas canvas) {
			
			if (initDone == false)
				init();
			
			Paint p = new Paint();
   		 	p.setStyle(Style.FILL);   		 	
   		 	
			
   		 	p.setColor(Color.DKGRAY);
   		 	canvas.drawPaint(p);
   		 	
   		 	//Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.canvasbg);
			//canvas.drawBitmap(bmp, 0, 0, p);
   		 	//bmp.recycle();
   		 	
   		 	p.setColor(Color.WHITE);
   		 	p.setTextAlign(Paint.Align.CENTER);
   		 	p.setTextSize(20);
   		 	p.setAntiAlias(true);
   		 	
			//Toast.makeText(getApplicationContext(), "width:" + this.getWidth() + " height: " + this.getHeight(), 
			//			Toast.LENGTH_LONG).show();
   		 	   		 
   		 	Level cLevel = myLevels[currentLevel];
   		 	int gridSize = cLevel.levelSize * cLevel.levelSize;
   		 	
   		 	// calculate size of each tile
   		 	tilesize = (getWidth() - ((cLevel.levelSize+1)*padding)) / cLevel.levelSize;
   		 	for(int i=0; i<gridSize; i++) {
   		 		RectF r = new RectF();
   		 		
   		 		// Get row and column 
   		 		int x = i % cLevel.levelSize;
   		 		int y = (i - x) / cLevel.levelSize;
   		 	
   		 		// calculate rectangle location and save locations
		 		r.top = myLevels[currentLevel].levelData[i].top =  (padding*(y+1)) + (tilesize*y);
		 		r.bottom = myLevels[currentLevel].levelData[i].bottom = (int) (r.top + tilesize);
		 		r.left = myLevels[currentLevel].levelData[i].left = (padding*(x+1)) + (tilesize*x);
		 		r.right = myLevels[currentLevel].levelData[i].right = (int) (r.left + tilesize);
		 				 		
		 		int color = cLevel.levelData[i].tileColor;		 		
		 		p.setColor(color);
   		 		canvas.drawRoundRect(r, 5, 5, p);
   		 		
   		 		// draw rect with labels
		 		String dispLabel = "";
		 		if (cLevel.levelData[i].state > 0) {		 			
		 			float xtext = r.right - (tilesize/2);
			 		float ytext = r.bottom - (tilesize/2);
			 		p.setColor(Color.BLACK);
		 			dispLabel = String.valueOf(cLevel.levelData[i].state);		 		
		 			canvas.drawText(dispLabel, xtext, ytext, p);
		 		}
   		 	}
   		 		
			super.draw(canvas);
			invalidate();
		}

		private int getTileIndex(float x, float y) {
			Log.d("getTile", "x:" + x + "y:" + y);
			
			Tile[] tData = myLevels[currentLevel].levelData;
			currentGridSize = myLevels[currentLevel].levelSize * myLevels[currentLevel].levelSize;
			
			
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
						
			TextView setStateText = (TextView)((View) getParent()).findViewWithTag(mkLevels.SETSTATE_ID);
			String setState =  setStateText.getText().toString();						
			
							
			if (setState.length() != 0) {
				int tmp = Integer.valueOf(setState);
				myLevels[currentLevel].levelData[tIndex].state = tmp;
				myLevels[currentLevel].levelData[tIndex].tileColor = Color.WHITE;
				return;
			}
			
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
			invalidate();
		}
		
		public void prevLevel() {
			if (currentLevel == 0)
				return;
			
			currentLevel--;
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
				
		public void reload() {
			setup();
		}
				
		public void save() {
			// save board to data file
			SQLiteDatabase db;
			
			db = openOrCreateDatabase("gamedata.db", SQLiteDatabase.CREATE_IF_NECESSARY, null);
			db.setVersion(1);
			db.setLocale(Locale.getDefault());
			db.setLockingEnabled(true);
			
			// Create table 
			final String CREATE_TABLE_LEVELDATA = " CREATE TABLE tbl_leveldata ( id integer primary key autoincrement, " +
													"	level_size int, grid text ); ";
			try {
				db.execSQL(CREATE_TABLE_LEVELDATA);
			} catch (SQLiteException e) {
				// create table exists.
				e.printStackTrace();
			}
			
			// check if solution is correct
			if (checkSolution() == false) {
				Toast.makeText(getApplicationContext(), "solution not correct", Toast.LENGTH_LONG).show();
				return;
			}
			
			// Create string for this level to put in db
			String lvlDataStr = "";
			int dLen = myLevels[currentLevel].levelSize * myLevels[currentLevel].levelSize;
			for(int i=0; i<dLen; i++) {
				if (i>0) lvlDataStr += " ";
				int state = myLevels[currentLevel].levelData[i].state;
				if (state < 0)
					state = 0;
				lvlDataStr += String.valueOf(state);
			}
			
			// check if level exists
			String checkQuery = "grid = ?";
			AbstractCursor c = (AbstractCursor) db.query("tbl_leveldata", null, checkQuery, new String[] { lvlDataStr }, null, null, null);
			if (c.getCount() > 0) {
				// duplicate entry ? do nothing
				Toast.makeText(getApplicationContext(), "duplicate entry", Toast.LENGTH_LONG).show();
				return;
			}
			
			// insert data in database
			ContentValues values = new ContentValues();
			values.put("level_size", myLevels[currentLevel].levelSize);
			values.put("grid", lvlDataStr);
			db.insert("tbl_leveldata", null, values);
						
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
	
	mySurface myBoard = null;

	private void nextClick() {
		myBoard.nextLevel();
	}
	
	private void prevClick() {
		myBoard.mksol();
	}
	
	private void checkSolClick() {
		if (myBoard.checkSolution()) {
			Toast.makeText(getApplicationContext(), "pass", Toast.LENGTH_LONG).show();
		} else 
			Toast.makeText(getApplicationContext(), "fail", Toast.LENGTH_LONG).show();		
	}
	
	private void reloadClick() {
		myBoard.reload();
	}
	
	private void saveClick() {		
		myBoard.save();
	}
	
	public void onClick(View b) {
		// TODO Auto-generated method stub
		
		int id = b.getId();
		switch(id) {
			case NEXT_ID:
				//Toast.makeText(getApplicationContext(), "next", Toast.LENGTH_LONG).show();
				nextClick();
				break;
			case PREV_ID:
				//Toast.makeText(getApplicationContext(), "prev", Toast.LENGTH_LONG).show();
				prevClick();
				break;
			case SAVE_ID:				
				saveClick();
				break;
			case RELOAD_ID:
				//Toast.makeText(getApplicationContext(), "check sol id", Toast.LENGTH_LONG).show();
				reloadClick();
				break;
			default:
				break;				
		}
	}
}
