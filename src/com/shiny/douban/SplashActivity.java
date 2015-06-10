package com.shiny.douban;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.PixelFormat;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Ê×´ÎÆô¶¯½øÈë»¶Ó­Ò³Ãæ
 * @author Administrator
 *
 */
public class SplashActivity extends Activity {

	private LinearLayout myLinearLayout ;
	
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

//		new Handler().postDelayed(new Runnable() {
//			public void run() {
//				Intent mainIntent;
//				mainIntent = new Intent(SplashActivity.this, MainTabActivity.class);
//				startActivity(mainIntent);
//				finish();
//			}
//		}, 1500); // 2900 for release
		
		//ÅÐ¶Ïµ±Ç°ÍøÂç×´Ì¬ÊÇ·ñ¿ÉÓÃ
		myLinearLayout = (LinearLayout) this.findViewById(R.id.LinearLayout01);
		if (isNetworkConnected()) {
			AlphaAnimation aa = new AlphaAnimation(0.0f, 1.0f);
			aa.setDuration(2000);
			myLinearLayout.setAnimation(aa);
			myLinearLayout.startAnimation(aa);
			new Handler().postDelayed(new LoadMainTabTask(), 2000);
			
		}else {
			showSetNetworkDialog();
		}
	}
	
	private class LoadMainTabTask implements Runnable {

		public void run() {
			Intent intent = new Intent(SplashActivity.this,MainTabActivity.class);
			startActivity(intent);
			finish();
			
		}	
	}
	
	private void showSetNetworkDialog() {
		AlertDialog.Builder builder = new Builder(this);
		builder.setTitle("ÉèÖÃÍøÂç×´Ì¬");
		builder.setMessage("ÍøÂç´íÎó£¬Çë¼ì²éÍøÂç×´Ì¬£¡");
		builder.setPositiveButton("ÉèÖÃÍøÂç", new OnClickListener() {
			
			public void onClick(DialogInterface arg0, int arg1) {
				Intent intent = new Intent();
				intent.setClassName("com.android.settings", "com.android.settings.Settings$WifiSettingsActivity");
				startActivity(intent);
				finish();
			}
		});
		builder.setNegativeButton("È¡Ïû", new OnClickListener() {
			public void onClick(DialogInterface arg0, int arg1) {
				Intent intent = new Intent(SplashActivity.this, MainTabActivity.class);
				startActivity(intent);
				finish();
			}
		});
		builder.setCancelable(false); 
		builder.create().show();
	}

	//ÅÐ¶Ïµ±Ç°ÍøÂç×´Ì¬
	private boolean isNetworkConnected(){
		boolean result;
		ConnectivityManager manager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
		NetworkInfo info = manager.getActiveNetworkInfo();
		if(info!=null&&info.isConnected()){
			result = true;
		}else{
			result = false;
		}
		return result;
	}
}
