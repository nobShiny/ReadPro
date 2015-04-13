package com.shiny.douban;

import java.util.ArrayList;
import java.util.List;

import com.shiny.douban.music.MusicApp;
import com.shiny.douban.music.db.MusicInfoDao;
import com.shiny.douban.music.fragment.MainFragment;
import com.shiny.douban.music.fragment.MenuFragment;
import com.shiny.douban.slidemenu.SlidingMenu;
import com.shiny.douban.util.IConstants;
import com.shiny.douban.util.MusicUtils;
import com.shiny.douban.util.SplashScreen;

import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * �״ν��������� �ᵽ����
 * @author Administrator
 *
 */
public class ListenMainContentActivity extends FragmentActivity implements
		IConstants {

	public static final String ALARM_CLOCK_BROADCAST = "alarm_clock_broadcast";
	public SlidingMenu mSlidingMenu;
	private List<OnBackListener> mBackListeners = new ArrayList<OnBackListener>();
	public MainFragment mMainFragment;

	private Handler mHandler;
	private MusicInfoDao mMusicDao;
	private SplashScreen mSplashScreen;
	private int mScreenWidth;

	public interface OnBackListener {
		public abstract void onBack();
	}

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);

		DisplayMetrics metric = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metric);
		mScreenWidth = metric.widthPixels;

		initSDCard();

		IntentFilter filter = new IntentFilter();
		filter.addAction(ALARM_CLOCK_BROADCAST);
		registerReceiver(mAlarmReceiver, filter);

		setContentView(R.layout.frame_main);
		mSplashScreen = new SplashScreen(this);
		mSplashScreen.show(R.drawable.image_splash_background,
				SplashScreen.SLIDE_LEFT);
		// set the Above View
		mMainFragment = new MainFragment();
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.frame_main, mMainFragment).commit();

		// configure the SlidingMenu
		mSlidingMenu = new SlidingMenu(this);
		mSlidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
		mSlidingMenu.setMode(SlidingMenu.RIGHT);
		mSlidingMenu.setShadowWidthRes(R.dimen.shadow_width);
		mSlidingMenu.setShadowDrawable(R.drawable.shadow);
		mSlidingMenu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
		mSlidingMenu.setFadeDegree(0.35f);
		mSlidingMenu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
		mSlidingMenu.setMenu(R.layout.frame_menu);
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.frame_menu, new MenuFragment()).commit();

		mMusicDao = new MusicInfoDao(this);

		mHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				mSplashScreen.removeSplashScreen();
			}
		};

		getData();
	}

	private void initSDCard() {
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.setPriority(1000);// ����������ȼ�
		intentFilter.addAction(Intent.ACTION_MEDIA_MOUNTED);// sd�������룬���Ѿ�����
		intentFilter.addAction(Intent.ACTION_MEDIA_UNMOUNTED);// sd�����ڣ�����û�й���
		intentFilter.addAction(Intent.ACTION_MEDIA_REMOVED);// sd�����Ƴ�
		intentFilter.addAction(Intent.ACTION_MEDIA_SHARED);// sd����Ϊ
															// USB�������洢���������ر����
		intentFilter.addAction(Intent.ACTION_MEDIA_BAD_REMOVAL);// sd���Ѿ���sd����۰γ������ǹ��ص㻹û���
		intentFilter.addDataScheme("file");
		registerReceiver(sdCardReceiver, intentFilter);// ע���������
	}

	private void getData() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				if (mMusicDao.hasData()) {
					// ��������ݾ͵�������ת
					mHandler.sendMessageDelayed(mHandler.obtainMessage(), 3000);
				} else {
					MusicUtils.queryMusic(ListenMainContentActivity.this,START_FROM_LOCAL);
					MusicUtils.queryAlbums(ListenMainContentActivity.this);
					MusicUtils.queryArtist(ListenMainContentActivity.this);
					MusicUtils.queryFolder(ListenMainContentActivity.this);
					mHandler.sendEmptyMessage(1);
				}
			}
		}).start();
	}

	public void registerBackListener(OnBackListener listener) {
		if (!mBackListeners.contains(listener)) {
			mBackListeners.add(listener);
		}
	}

	public void unRegisterBackListener(OnBackListener listener) {
		mBackListeners.remove(listener);
	}

	@Override
	public void onBackPressed() {
		if (mSlidingMenu.isMenuShowing()) {
			mSlidingMenu.showContent();
		} else {
			if (mBackListeners.size() == 0) {
				// super.onBackPressed();
				// ��activity�е��� moveTaskToBack (boolean nonRoot)�������ɽ�activity
				// �˵���̨��ע�ⲻ��finish()�˳���
				// ����Ϊfalse����ֻ�е�ǰactivity��task����ָӦ�������ĵ�һ��activityʱ������Ч;
				moveTaskToBack(true);
			}
			for (OnBackListener listener : mBackListeners) {
				listener.onBack();
			}
		}
	}

	private final BroadcastReceiver sdCardReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals("android.intent.action.MEDIA_REMOVED")// ����δ����״̬
					|| action.equals("android.intent.action.MEDIA_UNMOUNTED")
					|| action.equals("android.intent.action.MEDIA_BAD_REMOVAL")
					|| action.equals("android.intent.action.MEDIA_SHARED")) {
				finish();
				Toast.makeText(ListenMainContentActivity.this, "SD������γ�����������û����ʼ��!",
						Toast.LENGTH_SHORT).show();
			}
		}
	};

	public void showSleepDialog() {

		if (MusicApp.mIsSleepClockSetting) {
			cancleSleepClock();
			Toast.makeText(getApplicationContext(), "��ȡ˯��ģʽ��",
					Toast.LENGTH_SHORT).show();
			return;
		}

		View view = View.inflate(this, R.layout.sleep_time, null);
		final Dialog dialog = new Dialog(this, R.style.lrc_dialog);
		dialog.setContentView(view);
		dialog.setCanceledOnTouchOutside(false);

		Window dialogWindow = dialog.getWindow();
		WindowManager.LayoutParams lp = dialogWindow.getAttributes();
		dialogWindow.setGravity(Gravity.CENTER);
		// lp.x = 100; // ��λ��X����
		// lp.y = 100; // ��λ��Y����
		lp.width = (int) (mScreenWidth * 0.7); // ���
		// lp.height = 400; // �߶�

		// ��Window��Attributes�ı�ʱϵͳ����ô˺���,����ֱ�ӵ�����Ӧ������Դ��ڲ����ĸ���,Ҳ������setAttributes
		// dialog.onWindowAttributesChanged(lp);
		dialogWindow.setAttributes(lp);

		dialog.show();

		final Button cancleBtn = (Button) view.findViewById(R.id.cancle_btn);
		final Button okBtn = (Button) view.findViewById(R.id.ok_btn);
		final EditText timeEt = (EditText) view.findViewById(R.id.time_et);
		OnClickListener listener = new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (v == cancleBtn) {
					dialog.dismiss();
				} else if (v == okBtn) {
					String timeS = timeEt.getText().toString();
					if (TextUtils.isEmpty(timeS)
							|| Integer.parseInt(timeS) == 0) {
						Toast.makeText(getApplicationContext(), "������Ч��",
								Toast.LENGTH_SHORT).show();
						return;
					}
					setSleepClock(timeS);
					dialog.dismiss();
				}
			}
		};

		cancleBtn.setOnClickListener(listener);
		okBtn.setOnClickListener(listener);
	}

	/**
	 * ����˯������
	 * 
	 * @param timeS
	 */
	private void setSleepClock(String timeS) {
		Intent intent = new Intent(ALARM_CLOCK_BROADCAST);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(
				ListenMainContentActivity.this, 0, intent, 0);
		// ����timeʱ��֮���˳�����
		int time = Integer.parseInt(timeS);
		long longTime = time * 60 * 1000L;
		AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
		am.set(AlarmManager.RTC, System.currentTimeMillis() + longTime,
				pendingIntent);
		MusicApp.mIsSleepClockSetting = true;
		Toast.makeText(getApplicationContext(), "����" + timeS + "���Ӻ��˳����",
				Toast.LENGTH_SHORT).show();
	}

	/**
	 * ȡ��˯������
	 */
	private void cancleSleepClock() {
		Intent intent = new Intent(ALARM_CLOCK_BROADCAST);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(
				ListenMainContentActivity.this, 0, intent, 0);
		AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
		am.cancel(pendingIntent);
		MusicApp.mIsSleepClockSetting = false;
	}

	private BroadcastReceiver mAlarmReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			// �˳�����
			finish();
		}

	};

	@Override
	public void onDestroy() {
		super.onDestroy();
		unregisterReceiver(sdCardReceiver);
		unregisterReceiver(mAlarmReceiver);
		MusicApp.mServiceManager.exit();
		MusicApp.mServiceManager = null;
		MusicUtils.clearCache();
		cancleSleepClock();
		System.exit(0);
	}

}
