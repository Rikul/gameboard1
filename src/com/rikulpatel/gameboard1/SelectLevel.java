package com.rikulpatel.gameboard1;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class SelectLevel extends ListActivity {
	
	Integer[] levelSizeCount = new Integer[3];
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {		
	      super.onCreate(savedInstanceState);
	      requestWindowFeature(Window.FEATURE_NO_TITLE);
	      setContentView(R.layout.selectlevel);
	      
	      gApp myApp = (gApp) getApplicationContext();
	      	      
	      //((gApp)getApplication()).getLevelSizeCount(5);
	      	      
	      try {
			levelSizeCount[0] = myApp.getLevelSizeCount(5);
			levelSizeCount[1] = myApp.getLevelSizeCount(8);
			levelSizeCount[2] = myApp.getLevelSizeCount(12);
	      } catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
	      }
	      
	      
	      List<String> bSize = new ArrayList<String>();
	      bSize.add("5 X 5 - " + levelSizeCount[0] + " Levels");
	      bSize.add("8 X 8 - "  + levelSizeCount[1] + " Levels");
	      bSize.add("12 X 12 - "  + levelSizeCount[2] + " Levels");
	      
	      TextView hText = (TextView) findViewById(R.id.headertext);
	      hText.setTypeface(((gApp) getApplication()).getTypeface());
	      
	      setListAdapter(new selLevelAdapter(this, R.layout.sellevelitem, bSize));	      
	}

	
	public void startPuzzleListView(int levelSize) {
		Intent i = new Intent(SelectLevel.this, PuzzleList.class);
		i.putExtra("levelSize", levelSize);		
	    startActivity(i);
	}
	
	public void onListItemClick(ListView lv, View v, int position, long id) {
		super.onListItemClick(lv, v, position, id);
		
		switch(position) {
			case 0:				
				startPuzzleListView(5);
				break;
			case 1:				
				startPuzzleListView(8);
				break;
			case 2:
				startPuzzleListView(12);
				break;
		}
		
		//Toast.makeText(this, "position: " + position, Toast.LENGTH_LONG).show();
	}
	
	private class selLevelAdapter extends ArrayAdapter<String> {
		
		private List<String> objects = null;
		
		public selLevelAdapter(Context context, int textViewResourceId,
				List<String> objects) {
			super(context, textViewResourceId, objects);
			
			this.objects = objects;						 
		}
		
		public View getView(int position, View convertView, ViewGroup parent) {			
			//View view = (View) findViewById(android.R.layout.simple_list_item_1);
			
			// reuse puzzlelistview  
			View v = convertView ;
			
			if (v == null) {
				// inflate puzzlelistview
				LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = inflater.inflate(R.layout.puzzlelistitem, null);
			}
						
			TextView bLabel = (TextView) v.findViewById(R.id.list_item_text);
			bLabel.setTypeface(((gApp) getApplication()).getTypeface());
			bLabel.setText(objects.get(position));			
			bLabel.setTextSize(30);
						
			return v;
		}
		
	}
}
