package com.shiny.douban;

import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.client.CookieStore;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;

import com.google.gdata.client.douban.DoubanService;
import com.shiny.douban.entity.MyClientCookie;
import com.shiny.douban.util.NetUtil;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LayoutAnimationController;
import android.view.animation.TranslateAnimation;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class BaseActivity extends Activity {

	private DoubanService doubanService = NetUtil.getDoubanService();
	ProgressDialog pd;

	public DoubanService getDoubanService() {
		return doubanService;
	}

	public void setDoubanService(DoubanService doubanService) {
		this.doubanService = doubanService;
	}

	@Override
	protected void onResume() {
		super.onResume();
		SharedPreferences sharedata = getSharedPreferences("data", 0);
		String accessToken = sharedata.getString("accessToken", null);
		String tokenSecret = sharedata.getString("tokenSecret", null);
		doubanService.setAccessToken(accessToken, tokenSecret);
	}
	
	// �˳�
	protected void doExit() {
		new AlertDialog.Builder(BaseActivity.this).setTitle("��ʾ").setMessage(
				"ȷ��Ҫ�˳��ҵĶ�����").setPositiveButton("ȷ��",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialoginterface, int i) {
						finish();
					}
				}).setNeutralButton("ȡ��",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface arg0, int arg1) {
					}

				}).show();

	}
	
	//���ضԻ���
	 public void showDialog()
	 {
		pd=ProgressDialog.show(BaseActivity.this, "��Ϣ", "����������..."); 
	 }
	 
	 public void showProgressBar() {
			AnimationSet set = new AnimationSet(true);

			Animation animation = new AlphaAnimation(0.0f, 1.0f);
			animation.setDuration(500);
			set.addAnimation(animation);

			animation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
					Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
					-1.0f, Animation.RELATIVE_TO_SELF, 0.0f);
			animation.setDuration(500);
			set.addAnimation(animation);

			LayoutAnimationController controller = new LayoutAnimationController(
					set, 0.5f);
			RelativeLayout loading = (RelativeLayout) findViewById(R.id.loading);
			loading.setVisibility(View.VISIBLE);
			loading.setLayoutAnimation(controller);
		}

		public void closeProgressBar() {

			AnimationSet set = new AnimationSet(true);

			Animation animation = new AlphaAnimation(0.0f, 1.0f);
			animation.setDuration(500);
			set.addAnimation(animation);

			animation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
					Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
					0.0f, Animation.RELATIVE_TO_SELF, -1.0f);
			animation.setDuration(500);
			set.addAnimation(animation);

			LayoutAnimationController controller = new LayoutAnimationController(
					set, 0.5f);
			RelativeLayout loading = (RelativeLayout) findViewById(R.id.loading);

			loading.setLayoutAnimation(controller);

			loading.setVisibility(View.INVISIBLE);
		}
		
		public void showProgressBar(String title) {
			TextView loading = (TextView) findViewById(R.id.txt_loading);
			loading.setText(title);
			showProgressBar();
		}
		
		public boolean checkLogin() {
			SharedPreferences sharedata = getSharedPreferences("data", 0);
			String accessToken = sharedata.getString("accessToken", null);
			String tokenSecret = sharedata.getString("tokenSecret", null);
			String uid = sharedata.getString("uid", null);

			// δ��¼
			if (accessToken == null || tokenSecret == null) {
				return false;
			}
			// �ѵ�¼
			else {
				NetUtil.getDoubanService().setAccessToken(accessToken, tokenSecret);
				NetUtil.setUid(uid);
				// ��ȡcookie
				try {
					restoreCookieFromFile();
				} catch (Exception e) {
					e.printStackTrace();
					return false;
				}
				
				return checkExperid(NetUtil.getCkStore().getCookies());
			}
		}

		// ��ȡcookie
		private void restoreCookieFromFile() throws Exception {
			FileInputStream fs = this.openFileInput("cookie.dat");
			ObjectInputStream ois = new ObjectInputStream(fs);

			Object[] myCookies = (Object[]) ois.readObject();
			ois.close();
			BasicCookieStore ckstore = new BasicCookieStore();
			for (Object myCookie : myCookies) {
				ckstore.addCookie(((MyClientCookie) myCookie).toBasicCookie());
			}
			NetUtil.setCkStore(ckstore);
		}

		// ������ʱ��
		private boolean checkExperid(List<Cookie> cookies) {
			for (Cookie cookie : cookies) {
				if ("ue".equals(cookie.getName())) {
					Date now = new Date();
					if (now.compareTo(cookie.getExpiryDate()) > 0) {
						return false;
					}
				}
			}
			return true;
		}
}
