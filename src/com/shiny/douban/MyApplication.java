package com.shiny.douban;

import com.shiny.douban.db.BookDB;
import com.shiny.douban.util.FinalDate;

import android.app.Application;
import android.content.Context;
import android.util.Log;


public class MyApplication extends Application {
	
	public static BookDB bookDB;
	private static Context context;

	@Override
	public void onCreate() {
		super.onCreate();
		Log.i("hck", "oncreat");
		context = getApplicationContext();
		initDateBase();
		
		
	}

	private static void initDateBase() {
		bookDB = new BookDB(context, FinalDate.DATABASE_TABKE);
	}
	

	
}
