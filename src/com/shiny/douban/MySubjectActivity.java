package com.shiny.douban;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LayoutAnimationController;
import android.view.animation.TranslateAnimation;
import android.widget.AbsListView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.AbsListView.OnScrollListener;
import android.widget.RelativeLayout.LayoutParams;

import com.shiny.douban.adapter.SubjectListAdapter;
import com.shiny.douban.entity.Subject;
import com.shiny.douban.util.ConvertUtil;
import com.shiny.douban.util.NetUtil;
import com.google.gdata.data.douban.CollectionFeed;
import com.google.gdata.data.douban.UserEntry;

public class MySubjectActivity extends BaseListActivity {

	private List<Subject> subjects = new ArrayList<Subject>();
	private String cat;
	private SubjectListAdapter listAdapter;
	private int index = 1;
	private int count = 10; // ÿ�λ�ȡ��Ŀ
	private int total; // �����Ŀ��
	private boolean isFilling = false; // �ж��Ƿ����ڻ�ȡ����

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_subject);

		ImageButton editButton = (ImageButton) findViewById(R.id.edit_button);
		editButton.setVisibility(View.INVISIBLE);
		ImageButton backButton = (ImageButton) findViewById(R.id.back_button);
		backButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				finish();
			}
		});

		Bundle extras = getIntent().getExtras();
		cat = extras != null ? extras.getString("cat") : Subject.BOOK;

		getListView().setOnScrollListener(new OnScrollListener() {

			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {

			}

			public void onScrollStateChanged(AbsListView view, int scrollState) {
				if (scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
					// �жϹ������ײ�
					if (view.getLastVisiblePosition() == (view.getCount() - 1)) {
						loadRemnantListItem();
					}
				}
			}

		});

		fillData();

	}

	// ���ظ������Ŀ
	private void loadRemnantListItem() {
		if (isFilling) {
			return;
		}
		index = index + count;
		if (index > total) {
			return;
		}
		RelativeLayout loading = (RelativeLayout) findViewById(R.id.loading);
		LayoutParams lp = (LayoutParams) loading.getLayoutParams();
		lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		loading.setLayoutParams(lp);
		
		fillData();
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


	private void fillData() {
		new AsyncTask<Void, Void, Boolean>() {

			@Override
			protected Boolean doInBackground(Void... arg0) {
				try {
					UserEntry ue = NetUtil.getDoubanService().getAuthorizedUser();
					CollectionFeed feed = NetUtil.getDoubanService()
							.getUserCollections(ue.getUid(), cat, "", "",
									index, count);
					total = feed.getTotalResults();
					subjects.addAll(ConvertUtil.ConvertCollection(feed, cat));
					NetUtil.getDoubanService().getAccessToken();
					
					return true;
				} catch (Exception e) {
					e.printStackTrace();
				}
				return false;
			}

			@Override
			protected void onPostExecute(Boolean result) {
				super.onPostExecute(result);
				closeProgressBar();
				if (result) {
					if (listAdapter == null) {
						listAdapter = new SubjectListAdapter(MySubjectActivity.this,
								getListView(), subjects);
						setListAdapter(listAdapter);
					} else {
						listAdapter.notifyDataSetChanged();
					}
				} else {
					Toast.makeText(MySubjectActivity.this, "���ݼ���ʧ�ܣ�",
							Toast.LENGTH_SHORT).show();
				}
				isFilling = false;
			}

			@Override
			protected void onPreExecute() {
				super.onPreExecute();
				showProgressBar();
				isFilling = true;
			}

		}.execute();

	}

	// ѡ���¼�
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		Intent i = new Intent(this, SubjectViewActivity.class);
		Subject subject = subjects.get(position);
		i.putExtra("subject", subject);
		startActivity(i);
	}

}
