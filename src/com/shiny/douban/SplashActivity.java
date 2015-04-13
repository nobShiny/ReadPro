package com.shiny.douban;


import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

/**
 * 首次启动进入欢迎页面
 * @author Administrator
 *
 */
public class SplashActivity extends Activity {

	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		getWindow().setFormat(PixelFormat.RGBA_8888);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_DITHER);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_splash);

		// Display the current version number
		PackageManager pm = getPackageManager();
		try {
			PackageInfo pi = pm.getPackageInfo("com.shiny.douban", 0);
			TextView versionNumber = (TextView) findViewById(R.id.versionNumber);
			versionNumber.setText("Version " + pi.versionName);
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		new Handler().postDelayed(new Runnable() {
			public void run() {
				Intent mainIntent;
				mainIntent = new Intent(SplashActivity.this, MainTabActivity.class);
				startActivity(mainIntent);
				finish();
			}
		}, 1500); // 2900 for release

	}
}
