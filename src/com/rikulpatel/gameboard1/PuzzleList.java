package com.rikulpatel.gameboard1;

import java.util.ArrayList;
import java.util.List;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.database.AbstractCursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;


public class PuzzleList extends ListActivity {
		
	private class puzzleListT { 
		int puzzleId = -1;
		String puzzleLabel = "";
	}
	
	List<puzzleListT> puzzleList = null; 
	int levelSize = 0; 
	
	@Override
	public void onCreate(Bundle savedInstanceState) {		
	      super.onCreate(savedInstanceState);
	      requestWindowFeature(Window.FEATURE_NO_TITLE);
	      setContentView(R.layout.puzzlelist);		      
	      
	      
	      levelSize = getIntent().getIntExtra("levelSize", 0);	      
	      AbstractCursor c = ((gApp) getApplication()).getLevelSizeRows(levelSize);	      
	      int puzzleCount = c.getCount();
	      
	      //String[] puzzleList = new String[puzzleCount];
	      puzzleList = new ArrayList<puzzleListT>(puzzleCount);
	      	      
	      for(int i=0; i < c.getCount(); i++) {
	    	  c.moveToPosition(i);
	    	  puzzleListT plItem = new puzzleListT(); 
	    	  
	    	  plItem.puzzleLabel = "Puzzle #" + (i+1);
	    	  plItem.puzzleId = c.getInt(c.getColumnIndex("id"));
	    	  	    	  
	    	  puzzleList.add(plItem);
	      }
	      
	      TextView hText = (TextView) findViewById(R.id.headertext);
	      hText.setTypeface(((gApp) getApplication()).getTypeface());
	      
	      
	      setListAdapter(new puzzleListAdapter(this, R.layout.puzzlelistitem ,  puzzleList));
	      
	}
	

	public void startBoardView(int puzzleId) {
		Intent i = new Intent(PuzzleList.this, MyView.class);
		i.putExtra("levelSize", levelSize);		
		i.putExtra("puzzleId", puzzleId);
	    startActivity(i);
	}	

	public void onListItemClick(ListView lv, View v, int position, long id) {		
		super.onListItemClick(lv, v, position, id);
		startBoardView(((puzzleListAdapter) lv.getAdapter()).getId(position));
		return;
	}
	
	private class puzzleListAdapter extends ArrayAdapter<puzzleListT> {
		private List<puzzleListT> objects = null;
		
		public puzzleListAdapter(Context context, int textViewResourceId,
				List<puzzleListT> objects) {
			super(context, textViewResourceId, objects);
			
			this.objects = objects;						 
		}
		
		public int getId(int position) {
			return objects.get(position).puzzleId;			
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
			
			// set the list item at this position
			puzzleListT plistItem = puzzleList.get(position);
			if (plistItem != null) {
				TextView pLabel = (TextView) v.findViewById(R.id.list_item_text);
				pLabel.setTypeface(((gApp) getApplication()).getTypeface());
				pLabel.setText(plistItem.puzzleLabel);
				pLabel.setTextSize(30);
			}
			
			return v;
		}
		
	}
	
}
