package com.rikulpatel.gameboard1;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.annotation.SuppressLint;
import android.app.Application;
import android.database.AbstractCursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.Typeface;
import android.os.Bundle;

public class gApp extends Application {
	
	SQLiteDatabase db = null;	
	final String DBFNAME = "gamedata";
	final String DBPATH = "/data/data/com.rikulpatel.gameboard1/databases/" + DBFNAME; 	
	final String DBDIR = "/data/data/com.rikulpatel.gameboard1/databases/";
	private static gApp singleton;
	private Typeface tf = null;
		
	@SuppressLint("NewApi") public void copyAssetsDb() {
		
		InputStream dbIn = null;
		FileOutputStream dbOut = null;
				
		try {			
			dbIn = getResources().getAssets().open(DBFNAME);	
			File outFile = new File(DBPATH);			
			if (outFile.exists() == false) {
				//outFile.mkdirs();
				if (new File(DBDIR).exists() == false) {
					new File(DBDIR).mkdirs();
				}
				outFile.setReadable(true);
				outFile.createNewFile();
			}
			dbOut = new FileOutputStream(outFile);
			
			byte[] b = new byte[1024];
			
			int nRead = dbIn.read(b, 0, 1024);	
			while(nRead != -1) {
				dbOut.write(b, 0, nRead);
				nRead = dbIn.read(b, 0, 1024);
			}
			
			dbIn.close(); 
			dbOut.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();			
		} finally {
			
		}
		
	}
	
	
	public Typeface getTypeface() {
		return tf;	
	}
	
	public gApp getInstance(){
		return singleton;
	}
	
	
	public void onCreate() {		
		super.onCreate();
		singleton = this;

		try {
			File dbFile = new File(DBPATH);
			if (dbFile.exists() == false) {				
				copyAssetsDb();
			}
			
			
			db = SQLiteDatabase.openDatabase(this.getDatabasePath(DBFNAME).getAbsolutePath(), 
										null, SQLiteDatabase.OPEN_READONLY);
			
		} catch (SQLiteException e) {
			e.printStackTrace();
		}		
		
		tf = Typeface.createFromAsset(getBaseContext().getAssets(),
                "fonts/datagothicr_nc.otf");		
	}
	
	
	public int getLevelSizeCount(int levelSize) throws IOException {
		if (db == null) {
			throw new IOException("not connected to database");
		}
		
		AbstractCursor c = (AbstractCursor) db.query("tbl_leveldata", null, "level_size=?", new String[] { String.valueOf(levelSize) }, null, null, "id");
		return c.getCount();
		//return 0;		
	}
		
	public AbstractCursor getLevelSizeRows(int levelSize) {
		AbstractCursor c = (AbstractCursor) db.query("tbl_leveldata", null, "level_size=?", new String[] { String.valueOf(levelSize) }, null, null, "id");		
		return c;		
	}
	
}
