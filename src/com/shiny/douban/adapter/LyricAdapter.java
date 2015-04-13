package com.shiny.douban.adapter;

import java.util.ArrayList;
import java.util.List;

import com.shiny.douban.R;
import com.shiny.douban.music.model.LyricSentence;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


public class LyricAdapter extends BaseAdapter {
	private static final String TAG = LyricAdapter.class.getSimpleName();

	/** ��ʾ��Ӽ��� */
	List<LyricSentence> mLyricSentences = null;

	Context mContext = null;

	/** ��ǰ�ľ��������� */
	int mIndexOfCurrentSentence = 0;

	float mCurrentSize = 20;
	float mNotCurrentSize = 17;

	public LyricAdapter(Context context) {
		mContext = context;
		mLyricSentences = new ArrayList<LyricSentence>();
		mIndexOfCurrentSentence = 0;
	}

	/** ���ø�ʣ����ⲿ���ã� */
	public void setLyric(List<LyricSentence> lyric) {
		mLyricSentences.clear();
		if (lyric != null) {
			mLyricSentences.addAll(lyric);
			Log.i(TAG, "��ʾ�����Ŀ=" + mLyricSentences.size());
		}
		mIndexOfCurrentSentence = 0;
	}

	@Override
	public boolean isEmpty() {
		// ���Ϊ��ʱ����ListView��ʾEmptyView
		if (mLyricSentences == null) {
			Log.i(TAG, "isEmpty:null");
			return true;
		} else if (mLyricSentences.size() == 0) {
			Log.i(TAG, "isEmpty:size=0");
			return true;
		} else {
			Log.i(TAG, "isEmpty:not empty");
			return false;
		}
	}

	@Override
	public boolean isEnabled(int position) {
		// ��ֹ���б���Ŀ�ϵ��
		return false;
	}

	@Override
	public int getCount() {
		return mLyricSentences.size();
	}

	@Override
	public Object getItem(int position) {
		return mLyricSentences.get(position).getContentText();
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			LayoutInflater inflater = (LayoutInflater) mContext
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.lyric_line, null);
			holder.lyric_line = (TextView) convertView
					.findViewById(R.id.lyric_line_text);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		if (position >= 0 && position < mLyricSentences.size()) {
			holder.lyric_line.setText(mLyricSentences.get(position)
					.getContentText());
		}
		if (mIndexOfCurrentSentence == position) {
			// ��ǰ���ŵ��ľ�������Ϊ��ɫ�������С����
			holder.lyric_line.setTextColor(Color.WHITE);
			holder.lyric_line.setTextSize(mCurrentSize);
		} else {
			// �����ľ�������Ϊ��ɫ�������С��С
			holder.lyric_line.setTextColor(mContext.getResources().getColor(
					R.color.trans_white));
			holder.lyric_line.setTextSize(mNotCurrentSize);
		}
		return convertView;
	}

	public void setCurrentSentenceIndex(int index) {
		mIndexOfCurrentSentence = index;
	}

	static class ViewHolder {
		TextView lyric_line;
	}
}
