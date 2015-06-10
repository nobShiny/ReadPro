package com.shiny.douban;

import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TextView;
import android.widget.TabHost.TabSpec;
import android.widget.Toast;

public class MainTabActivity extends TabActivity {
	private static final int ABOUT_ID = 1;
	private TabHost mTabHost;
	private LayoutInflater inflater;
//	private int myMenuSettingTag = 0;
//	protected Menu myMenu;

	@SuppressWarnings("deprecation")
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// ȡ������
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.tab_activity);

		inflater = LayoutInflater.from(this);
		mTabHost = getTabHost();
		// �ҵĶ���
		mTabHost.addTab(getMyDoubanTab());
		// ����
		mTabHost.addTab(getSearchTab());
		// ����
		mTabHost.addTab(getReadListenTab());
		
//		mTabHost.setOnTabChangedListener(new OnTabChangeListener(){
//            @Override
//            public void onTabChanged(String tagString) {
//            	if (tagString.equals("One")) {
//                    myMenuSettingTag = 1;
//                }
//                if (tagString.equals("Two")) {
//                    myMenuSettingTag = 2;
//                }
//                if (tagString.equals("Three")) {
//                    myMenuSettingTag = 3;
//                }
//                if (myMenu != null) {
//                    onCreateOptionsMenu(myMenu);
//                }
//            }           
//        });
	}

	private TabSpec getMyDoubanTab() {
		TabSpec spec = mTabHost.newTabSpec("one");
		// ָ����ǩ��ʾ������ , �����activity��Ӧ��intent����
		Intent intent = new Intent(this, FavActivity.class);
		spec.setContent(intent);
		// ���ñ�ǩ�����ֺ���ʽ
		spec.setIndicator(getIndicatorView(mTabHost.getContext(),
				R.string.tab_main_nav_fav, R.drawable.tab_main_nav_me_selector));
		return spec;
	}

	private TabSpec getSearchTab() {
		TabSpec spec = mTabHost.newTabSpec("two");
		// ָ����ǩ��ʾ������ , �����activity��Ӧ��intent����
		Intent intent = new Intent(this, SearchActivity.class);
		spec.setContent(intent);
		// ���ñ�ǩ�����ֺ���ʽ
		spec.setIndicator(getIndicatorView(mTabHost.getContext(),
				R.string.tab_main_nav_search,
				R.drawable.tab_main_nav_search_selector));
		return spec;
	}

	private TabSpec getReadListenTab() {
		TabSpec spec = mTabHost.newTabSpec("three");
		// ָ����ǩ��ʾ������ , �����activity��Ӧ��intent����
		Intent intent = new Intent(this, AboutActivity.class);
		spec.setContent(intent);
		// ���ñ�ǩ�����ֺ���ʽ
		spec.setIndicator(getIndicatorView(mTabHost.getContext(),
				R.string.tab_main_nav_about,
				R.drawable.tab_main_nav_fav_selector));
		return spec;
	}

	/**
	 * ��ȡ��Ŀ��ʾ��view����
	 */
	private View getIndicatorView(Context context, int titleId, int iconid) {
		View view = LayoutInflater.from(context).inflate(R.layout.tab_main_nav,null);
		ImageView ivicon = (ImageView) view.findViewById(R.id.ivIcon);
		TextView tvtitle = (TextView) view.findViewById(R.id.tvTitle);
		ivicon.setImageResource(iconid);
		tvtitle.setText(getText(titleId).toString());
		return view;
	}

	private static final int myMenuResources[] = {R.menu.main_tab_menu,
		R.menu.main_tab_menu,R.menu.main_tab_menu };
	// ע���û�

//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//		// TODO Auto-generated method stub
//		// Hold on to this
//		myMenu = menu;
//		myMenu.clear();//���MENU�˵�
//		// Inflate the currently selected menu XML resource.
//		MenuInflater inflater = getMenuInflater();        
//        //��TabActivity�����ȡһ��MENU������
//		switch (myMenuSettingTag) {
//		case 1:
//			inflater.inflate(myMenuResources[0], menu);
//            //��̬���������ж�Ӧ��XML MENU�˵�
//			break;
//		case 2:
//			inflater.inflate(myMenuResources[1], menu);
//			break;
//		case 3:
//			inflater.inflate(myMenuResources[2], menu);
//			break;
//		}
//		return super.onCreateOptionsMenu(menu);
//	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater in = getMenuInflater();
				//new MenuInflater(this);
		in.inflate(R.menu.main_tab_menu, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.main_tab_menu_clear_user:
			SharedPreferences sp = getSharedPreferences("data",Context.MODE_PRIVATE);
			Editor editor = sp.edit();
			editor.putString("accessToken", "");
			editor.putString("tokenSecret", "");
			editor.putString("uid", "");
			editor.putString("email", "");
			editor.commit();
			Toast.makeText(getApplicationContext(), "ע���ɹ�", 0).show();
			break;
		}
		return super.onOptionsItemSelected(item);
	}

}
