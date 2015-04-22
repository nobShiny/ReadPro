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
import android.widget.TabHost.TabSpec;

public class MainTabActivity extends TabActivity {
	private static final int ABOUT_ID = 1;
	private TabHost mTabHost;
	private LayoutInflater inflater;

	@SuppressWarnings("deprecation")
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 取消标题
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.tab_activity);
		
		inflater =  LayoutInflater.from(this);
		mTabHost = getTabHost();
		// 我的豆瓣
		mTabHost.addTab(getMyDoubanTab());
		// 搜索
		mTabHost.addTab(getSearchTab());
		// 阅听
		mTabHost.addTab(getReadListenTab());

	}

	private TabSpec getMyDoubanTab(){
		TabSpec spec = mTabHost.newTabSpec("one");
		//指定标签显示的内容 , 激活的activity对应的intent对象
		Intent intent = new Intent(this,FavActivity.class);
		spec.setContent(intent);
		// 设置标签的文字和样式
		spec.setIndicator(getIndicatorView(mTabHost.getContext(),
				                           R.string.tab_main_nav_fav,
										   R.drawable.tab_main_nav_me_selector));
		return spec;
	}
	private TabSpec getSearchTab(){
		TabSpec spec = mTabHost.newTabSpec("two");
		//指定标签显示的内容 , 激活的activity对应的intent对象
		Intent intent = new Intent(this,SearchActivity.class);
		spec.setContent(intent);
		// 设置标签的文字和样式
		spec.setIndicator(getIndicatorView(mTabHost.getContext(),
				                           R.string.tab_main_nav_search,
				                           R.drawable.tab_main_nav_search_selector));
		return spec;
	}
	private TabSpec getReadListenTab(){
		TabSpec spec = mTabHost.newTabSpec("three");
		//指定标签显示的内容 , 激活的activity对应的intent对象
		Intent intent = new Intent(this,AboutActivity.class);
		spec.setContent(intent);
		// 设置标签的文字和样式
		spec.setIndicator(getIndicatorView(mTabHost.getContext(),
										   R.string.tab_main_nav_about,
										   R.drawable.tab_main_nav_fav_selector));
		return spec;
	}
	
	/**
	 * 获取条目显示的view对象 
	 */
	private View getIndicatorView(Context context,int titleId, int iconid){
		View view = LayoutInflater.from(context).inflate(R.layout.tab_main_nav,null);
	    ImageView ivicon =	(ImageView) view.findViewById(R.id.ivIcon);
	    TextView tvtitle =	(TextView) view.findViewById(R.id.tvTitle);
	    ivicon.setImageResource(iconid);
	    tvtitle.setText(getText(titleId).toString());
	    return view;
	}

//	private View prepareTabView(Context context, int titleId, int drawable) {
//		View view = LayoutInflater.from(context).inflate(R.layout.tab_main_nav,null);
//		TextView tv = (TextView) view.findViewById(R.id.tvTitle);
//		tv.setText(getText(titleId).toString());
//		ImageView iv = (ImageView) view.findViewById(R.id.ivIcon);
//		iv.setImageResource(drawable);
//		return view;
//	}
//
//	private void initTabHost() {
//		if (mTabHost != null) {
//			throw new IllegalStateException("Trying to intialize already initializd TabHost");
//		}
//		mTabHost = getTabHost();
//
//		Intent bestReviewTab = new Intent(this, ReviewActivity.class);
//		bestReviewTab.putExtra("best_review", true);
//		Intent myDoubanTab = new Intent(this, FavActivity.class);
//		Intent searchTab = new Intent(this, SearchActivity.class);
//		Intent aboutTab = new Intent(this, AboutActivity.class);
//
//		// 我的豆瓣
//		mTabHost.addTab(mTabHost.newTabSpec("one").setIndicator(
//				prepareTabView(mTabHost.getContext(),
//						R.string.tab_main_nav_fav,
//						R.drawable.tab_main_nav_me_selector)).setContent(myDoubanTab));
//
//
//		// 搜索
//		mTabHost.addTab(mTabHost.newTabSpec("two").setIndicator(
//				prepareTabView(mTabHost.getContext(),
//						R.string.tab_main_nav_search,
//						R.drawable.tab_main_nav_search_selector)).setContent(searchTab));
//
//		// 阅听
//		mTabHost.addTab(mTabHost.newTabSpec("three").setIndicator(
//				prepareTabView(mTabHost.getContext(), 
//						R.string.tab_main_nav_about,
//						R.drawable.tab_main_nav_fav_selector)).setContent(aboutTab));
//
//	}

}
