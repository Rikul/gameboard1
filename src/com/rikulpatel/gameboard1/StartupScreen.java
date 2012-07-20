package com.rikulpatel.gameboard1;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

public class StartupScreen extends Activity {
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	      super.onCreate(savedInstanceState);
	      requestWindowFeature(Window.FEATURE_NO_TITLE);
	      
	      setContentView(R.layout.startupscreen);
	}
	
	public void play(View view) {
		Intent i = new Intent(StartupScreen.this, SelectLevel.class);		
	    startActivity(i);
	}
	
}
