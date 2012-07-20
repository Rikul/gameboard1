package com.rikulpatel.gameboard1;

import java.io.InputStream;
import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Collections;
import java.util.Random;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Picture;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.rikulpatel.gameboard1.R;

public class MyView extends Activity implements OnClickListener {
		
	private static final int PREV_ID = 1;
	private static final int NEXT_ID = 2;
	private static final int CHECK_SOL_ID = 3;
	mySurface myBoard = null;
	int boardSize = 0;

	@Override
	public void onCreate(Bundle savedInstanceState) {
	      super.onCreate(savedInstanceState);
	      requestWindowFeature(Window.FEATURE_NO_TITLE);
	      
	      setContentView(R.layout.myview);	
	      
	      int h=getWindowManager().getDefaultDisplay().getHeight();
	      int w=getWindowManager().getDefaultDisplay().getWidth();
	      
	      // Create square view in middle of layout	
	      int sz=0;
	      if (h>w) sz=w; 
	      else sz=h;

	      // create 50 px padding
	      sz -= 50;
	      	      
	      //myBoard = new mySurface(this, this);
	      myBoard = (mySurface) findViewById(R.id.mysurface);	      
	      int currentLevelSize = getIntent().getIntExtra("levelSize", 0);
	      int selectedPuzzleId = getIntent().getIntExtra("puzzleId", 0);
	      myBoard.setLevelSize(currentLevelSize);
	      myBoard.setPuzzleId(selectedPuzzleId);
	      myBoard.setParent(this);
	      
	      /*
	      LinearLayout layout = (LinearLayout) findViewById(R.id.myview);
	      layout.setBackgroundResource(R.drawable.boardbg1);
	      */
	      
	      LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(sz,sz);
	      myBoard.setLayoutParams(params);	      
	      
	     // layout.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);	      
	     // layout.addView(myBoard, params);
	      	      
	      /*
	      LinearLayout layout_nav = new LinearLayout(this);
	      layout_nav.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, 100));
	      
	      Button prev = new Button(this);
	      prev.setText("Previous");
	      prev.setId(this.PREV_ID);
	      prev.setOnClickListener(this);
	      
	      Button check = new Button(this);
	      check.setText("Check Solution");
	      check.setId(this.CHECK_SOL_ID);
	      check.setOnClickListener(this);
	      
	      Button next = new Button(this);
	      next.setText("Next");
	      next.setId(this.NEXT_ID);
	      next.setOnClickListener(this);
	      
	      layout_nav.addView(prev);
	      layout_nav.addView(check);
	      layout_nav.addView(next);
	      
	      layout.addView(layout_nav);
	      */
	}
	

	public void nextClick(View v) {
		myBoard.nextLevel();
	}
	
	public void prevClick(View v) {
		myBoard.prevLevel();
	}
	
	private void checkSolClick() {
		if (myBoard.checkSolution()) {
			Toast.makeText(getApplicationContext(), "pass", Toast.LENGTH_LONG).show();
		} else 
			Toast.makeText(getApplicationContext(), "fail", Toast.LENGTH_LONG).show();
		
	}
	
	public void setLevelSize(int levelSize) {
		this.boardSize = levelSize;
	}
	
	public void onClick(View b) {
		int id = b.getId();
		switch(id) {
			case NEXT_ID:
				Toast.makeText(getApplicationContext(), "next", Toast.LENGTH_LONG).show();
				nextClick(b);
				break;
			case PREV_ID:
				Toast.makeText(getApplicationContext(), "prev", Toast.LENGTH_LONG).show();
				prevClick(b);
				break;
			case CHECK_SOL_ID:
				Toast.makeText(getApplicationContext(), "check sol id", Toast.LENGTH_LONG).show();
				checkSolClick();
				break;
			default:
				break;				
		}
	}
}
