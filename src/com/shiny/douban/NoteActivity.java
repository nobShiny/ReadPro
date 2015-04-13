package com.shiny.douban;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LayoutAnimationController;
import android.view.animation.TranslateAnimation;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.AdapterContextMenuInfo;

import com.shiny.douban.util.NetUtil;
import com.google.gdata.data.TextContent;
import com.google.gdata.data.douban.NoteEntry;
import com.google.gdata.data.douban.NoteFeed;
import com.google.gdata.data.douban.UserEntry;

public class NoteActivity extends BaseListActivity {

	private NoteFeed notes;
	private SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	private ImageButton backButton;
	private ImageButton editButton;

	private static final int INSERT_ID = 0x000001;
	private static final int DELETE_ID = 0x000002;
	private static final int EDIT_ID = 0x000003;

	private NoteListAdapter listAdapter;

	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_note);

		editButton = (ImageButton) findViewById(R.id.edit_button);

		editButton.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				insertNote();
			}

		});
		backButton = (ImageButton) findViewById(R.id.back_button);
		backButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				doExit();
			}
		});

		TextView titleText = (TextView) findViewById(R.id.myTitle);
		titleText.setText("�����ռ�");

		fillData();
		registerForContextMenu(getListView());
	}

	

	// ��ȡ�ռ�����
	private void fillData() {
		new AsyncTask<Void, Void, NoteFeed>() {
			@Override
			protected NoteFeed doInBackground(Void... arg0) {
				NoteFeed notefeeds = null;
				try {
					UserEntry ue = NetUtil.getDoubanService().getAuthorizedUser();
					notefeeds = NetUtil.getDoubanService().getUserNotes(ue.getUid(),1, 100);
				} catch (Exception e) {
					e.printStackTrace();
				}
				return notefeeds;
			}

			//��doInBackground ִ����ɺ�onPostExecute ��������UI thread���ã�
			//��̨�ļ�������ͨ���÷������ݵ�UI thread.
			@Override
			protected void onPostExecute(NoteFeed noteFeeds) {
				closeProgressBar();
				if (noteFeeds != null) {
					super.onPostExecute(noteFeeds);
					listAdapter = new NoteListAdapter(NoteActivity.this,noteFeeds);
					setListAdapter(listAdapter);
					notes = noteFeeds;
				} else {
					Toast.makeText(NoteActivity.this, "���ݼ���ʧ�ܣ�",
							Toast.LENGTH_SHORT);
				}
			}

			@Override
			protected void onPreExecute() {
				super.onPreExecute();
				showProgressBar("���ڼ�������...");
			}
		}.execute();
	}

	// ɾ���ռ�
	private void deleteNote(NoteEntry noteEntry) {
		new AsyncTask<NoteEntry, Void, Boolean>() {

			@Override
			protected void onPreExecute() {
				super.onPreExecute();
				showProgressBar("����ɾ������...");
			}

			@Override
			protected Boolean doInBackground(NoteEntry... args) {
				NoteEntry ne = args[0];
				try {
					NetUtil.getDoubanService().deleteNote(ne);
				} catch (Exception e) {
					e.printStackTrace();
					return false;
				}
				return true;
			}

			@Override
			protected void onPostExecute(Boolean result) {
				super.onPostExecute(result);
				if (result) {
					closeProgressBar();
					Toast.makeText(NoteActivity.this, "�ռ�ɾ���ɹ���",
							Toast.LENGTH_SHORT).show();
					fillData();
				} else {
					closeProgressBar();
					Toast.makeText(NoteActivity.this, "�ռ�ɾ��ʧ�ܣ�",
							Toast.LENGTH_SHORT).show();
				}
			}

		}.execute(noteEntry);
	}

	// �޸��ռ�
	private void editNote(NoteEntry ne) {
		String id = ne.getId();
		String title = ne.getTitle().getPlainText();
		String content = ((TextContent) ne.getContent()).getContent().getPlainText();
		Intent i = new Intent(this, NoteEditActivity.class);
		i.putExtra("id", id);
		i.putExtra("title", title);
		i.putExtra("content", content);
		startActivityForResult(i, 1);
	}

	// �����ռ�
	private void insertNote() {
		Intent i = new Intent(this, NoteEditActivity.class);
		startActivityForResult(i, 1);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == 1) {
			fillData();
		}
	}

	/**
	 * �Զ���List������
	 * 
	 * @author chenyc
	 * 
	 */
	private class NoteListAdapter extends BaseAdapter {

		private NoteFeed mNotes;
		private LayoutInflater mInflater;

		public NoteListAdapter(Context context, NoteFeed notes) {
			super();
			mNotes = notes;
			mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		public int getCount() {
			return mNotes.getEntries().size();
		}

		public Object getItem(int i) {
			return mNotes.getEntries().get(i);
		}

		public long getItemId(int i) {
			return i;
		}

		public View getView(int i, View view, ViewGroup vgroup) {

			if (view == null) {
				view = mInflater.inflate(R.layout.note_item, null);
			}
			TextView txtTitle = (TextView) view.findViewById(R.id.note_title);

			txtTitle.setText(mNotes.getEntries().get(i).getTitle()
					.getPlainText());

			return view;
		}
	}

	// ѡ���¼�
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		Intent i = new Intent(this, NoteViewActivity.class);

		NoteEntry noteEntity = notes.getEntries().get(position);

		String title = noteEntity.getTitle().getPlainText();
		String content = ((TextContent) noteEntity.getContent()).getContent().getPlainText();

		Date date = new Date(noteEntity.getPublished().getValue());

		String publishDate = df.format(date);

		i.putExtra("id", id);
		i.putExtra("title", title);
		i.putExtra("content", content);
		i.putExtra("publish_date", publishDate);

		startActivity(i);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.add(0, INSERT_ID, 0, R.string.menu_insert);
		menu.add(0, EDIT_ID, 0, R.string.menu_edit);
		menu.add(0, DELETE_ID, 0, R.string.menu_delete);
	}

	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		int id = (int) info.id;
		NoteEntry ne = notes.getEntries().get(id);
		switch (item.getItemId()) {
		case DELETE_ID:
			deleteNote(ne);
			break;
		case EDIT_ID:
			editNote(ne);
			break;
		case INSERT_ID:
			insertNote();
			break;

		}
		return super.onContextItemSelected(item);
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			finish();
			return true;
		}
		return true;
	}

}

