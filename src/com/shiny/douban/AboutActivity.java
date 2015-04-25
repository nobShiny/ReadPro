package com.shiny.douban;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

public class AboutActivity extends BaseActivity {
	
	private RelativeLayout rl_read,rl_listen;
	private Intent intent;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_read_listen);
		
		rl_read = (RelativeLayout) findViewById(R.id.rl_read);
		rl_listen = (RelativeLayout) findViewById(R.id.rl_listen);
		
		//我读模块
		rl_read.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				intent = new Intent(AboutActivity.this,ReadLoadingActivity.class);
				startActivity(intent);
			}
		});
		//我听模块
		rl_listen.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				intent = new Intent(AboutActivity.this,MusicPlayActivity.class);
				startActivity(intent);
			}
		});

		// 回退按钮
		ImageButton backButton = (ImageButton) findViewById(R.id.back_button);
		backButton.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				doExit();
			}
		});
	}

}
