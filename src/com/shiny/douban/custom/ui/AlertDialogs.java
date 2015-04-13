package com.shiny.douban.custom.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.shiny.douban.R;
import com.shiny.douban.ReadBaseActivity;
import com.shiny.douban.util.MyTool;


public class AlertDialogs {
	private Context context;
	private Button exitButton;
	private Button noButton;
	private TextView textView, textView2;
	private AlertDialog aDialog;
	private ReadBaseActivity activity;

	public AlertDialogs(Context context, ReadBaseActivity activity) {
		this.context = context;
		this.activity = activity;
	}

	public void alertDialog(String title, String content, String btsString1,
			String btString2, String tag) {
		final View view;
		view = LayoutInflater.from(context).inflate(R.layout.bottom_btn, null);
		exitButton = (Button) view.findViewById(R.id.bt2);
		noButton = (Button) view.findViewById(R.id.bt1);
		textView = (TextView) view.findViewById(R.id.d_title);
		textView2 = (TextView) view.findViewById(R.id.d_content);
		exitButton.setText(btsString1);
		noButton.setText(btString2);
		textView.setText(title);
		textView2.setText(content);
		aDialog = new AlertDialog.Builder(context).create();
		aDialog.show();
		WindowManager.LayoutParams params = aDialog.getWindow().getAttributes();
		params.width =(int) (MyTool.getWidth()*0.6);
		params.height = (int) (MyTool.getHight()*0.2);
		aDialog.getWindow().setAttributes(params);
		aDialog.getWindow().setContentView(view);
		noButton.setTag(tag);
		exitButton.setTag(tag);
		noButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				aDialog.dismiss();
				if (v.getTag().equals("exit")) {
					aDialog.dismiss();
				} else if (v.getTag().equals("net")) {
					aDialog.dismiss();
					new Exit(context).exit();
				}
			}
		});
		exitButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
			
				if (v.getTag().equals("exit")) {
					aDialog.dismiss();
					new Exit(context).exit();
				} else if (v.getTag().equals("delete")) {
					aDialog.dismiss();
				 activity.server();
				}
			}
		});
	}

}
