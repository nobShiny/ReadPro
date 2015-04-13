package com.shiny.douban;

import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;

public class MainTabActivity extends TabActivity {
	private static final int ABOUT_ID = 1;
	private TabHost mTabHost;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// 取消标题
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.tab_activity);

		// 获取数据
		initTabHost();
	}

	private View prepareTabView(Context context, int titleId, int drawable) {
		View view = LayoutInflater.from(context).inflate(R.layout.tab_main_nav,null);
		TextView tv = (TextView) view.findViewById(R.id.tvTitle);
		tv.setText(getText(titleId).toString());
		ImageView iv = (ImageView) view.findViewById(R.id.ivIcon);
		iv.setImageResource(drawable);
		return view;
	}

	private void initTabHost() {
		if (mTabHost != null) {
			throw new IllegalStateException("Trying to intialize already initializd TabHost");
		}
		mTabHost = getTabHost();

		Intent myDoubanTab = new Intent(this, FavActivity.class);
		Intent bestReviewTab = new Intent(this, ReviewActivity.class);
		bestReviewTab.putExtra("best_review", true);
		Intent searchTab = new Intent(this, SearchActivity.class);
//		Intent newBookTab = new Intent(this, HomeActivity.class);
		Intent aboutTab = new Intent(this, AboutActivity.class);
//		Intent groupTab=new Intent(this,GroupActivity.class);

		// 我的豆瓣
		mTabHost.addTab(mTabHost.newTabSpec("one").setIndicator(
				prepareTabView(mTabHost.getContext(),
						R.string.tab_main_nav_fav,
						R.drawable.tab_main_nav_me_selector)).setContent(myDoubanTab));


		// 搜索
		mTabHost.addTab(mTabHost.newTabSpec("two").setIndicator(
				prepareTabView(mTabHost.getContext(),
						R.string.tab_main_nav_search,
						R.drawable.tab_main_nav_search_selector)).setContent(
				searchTab));

		// 关于
		mTabHost.addTab(mTabHost.newTabSpec("three").setIndicator(
				prepareTabView(mTabHost.getContext(), R.string.tab_main_nav_about,
						R.drawable.tab_main_nav_fav_selector)).setContent(aboutTab));

	}

}
