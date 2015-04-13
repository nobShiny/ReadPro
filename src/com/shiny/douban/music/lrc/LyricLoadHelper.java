package com.shiny.douban.music.lrc;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.shiny.douban.music.model.LyricSentence;

import android.annotation.SuppressLint;
import android.util.Log;


/**
 * ��ʵ���ʾ����
 * @author longdw(longdawei1988@gmail.com)
 *
 */
public class LyricLoadHelper {
	/** ��������֪ͨ������롢�仯�ļ����� */
	public interface LyricListener {

		/**
		 * �������ʱ����
		 * 
		 * @param lyricSentences
		 *            ����ı����������и�ʾ���
		 * @param indexOfCurSentence
		 *            ���ڲ��ŵľ����ھ��Ӽ����е�������
		 */
		public abstract void onLyricLoaded(List<LyricSentence> lyricSentences,
				int indexOfCurSentence);

		/**
		 * ��ʱ仯ʱ����
		 * 
		 * @param indexOfCurSentence
		 *            ���ڲ��ŵľ����ھ��Ӽ����е�������
		 * @param currentTime
		 *            �Ѿ����ŵĺ�����
		 * */
		public abstract void onLyricSentenceChanged(int indexOfCurSentence);
	}

	private static final String TAG = LyricLoadHelper.class.getSimpleName();

	/** ���Ӽ��� */
	private ArrayList<LyricSentence> mLyricSentences = new ArrayList<LyricSentence>();

	private LyricListener mLyricListener = null;

	private boolean mHasLyric = false;

	/** ��ǰ���ڲ��ŵĸ�ʾ��ӵ��ھ��Ӽ����е������� */
	private int mIndexOfCurrentSentence = -1;

	/** ���ڻ����һ��������ʽ����,ʶ��[]�е����ݣ������������� */
	private final Pattern mBracketPattern = Pattern
			.compile("(?<=\\[).*?(?=\\])");
	private final Pattern mTimePattern = Pattern
			.compile("(?<=\\[)(\\d{2}:\\d{2}\\.?\\d{0,3})(?=\\])");

	private final String mEncoding = "utf-8";

	public List<LyricSentence> getLyricSentences() {
		return mLyricSentences;
	}

	public void setLyricListener(LyricListener listener) {
		this.mLyricListener = listener;
	}

	public void setIndexOfCurrentSentence(int index) {
		mIndexOfCurrentSentence = index;
	}

	public int getIndexOfCurrentSentence() {
		return mIndexOfCurrentSentence;
	}

	/**
	 * ���ݸ���ļ���·������ȡ������ı�������
	 * 
	 * @param lyricPath
	 *            ����ļ�·��
	 * @return true��ʾ���ڸ�ʣ�false��ʾ�����ڸ��
	 */
	public boolean loadLyric(String lyricPath) {
		Log.i(TAG, "LoadLyric begin,path is:" + lyricPath);
		mHasLyric = false;
		mLyricSentences.clear();

		if (lyricPath != null) {
			File file = new File(lyricPath);
			if (file.exists()) {
				Log.i(TAG, "����ļ�����");
				mHasLyric = true;
				try {
					FileInputStream fr = new FileInputStream(file);
					InputStreamReader isr = new InputStreamReader(fr, mEncoding);
					BufferedReader br = new BufferedReader(isr);

					String line = null;

					// ���з�������ı�
					while ((line = br.readLine()) != null) {
						Log.i(TAG, "lyric line:" + line);
						parseLine(line);
					}

					// ��ʱ��������Ӽ���
					Collections.sort(mLyricSentences,
							new Comparator<LyricSentence>() {
								// ��Ƕ��������compare��
								public int compare(LyricSentence object1,
										LyricSentence object2) {
									if (object1.getStartTime() > object2
											.getStartTime()) {
										return 1;
									} else if (object1.getStartTime() < object2
											.getStartTime()) {
										return -1;
									} else {
										return 0;
									}
								}
							});

					for (int i = 0; i < mLyricSentences.size() - 1; i++) {
						mLyricSentences.get(i).setDuringTime(
								mLyricSentences.get(i + 1).getStartTime());
					}
					mLyricSentences.get(mLyricSentences.size() - 1)
							.setDuringTime(Integer.MAX_VALUE);
					fr.close();
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
				}
			} else {
				Log.i(TAG, "����ļ�������");
			}
		}
		// �����˭�ڼ�����֪ͨ�����������������������ľ��Ӽ���Ҳ���ݹ�ȥ
		if (mLyricListener != null) {
			mLyricListener.onLyricLoaded(mLyricSentences,
					mIndexOfCurrentSentence);
		}
		if (mHasLyric) {
			Log.i(TAG, "Lyric file existed.Lyric has " + mLyricSentences.size()
					+ " Sentences");
		} else {
			Log.i(TAG, "Lyric file does not existed");
		}
		return mHasLyric;
	}

	/**
	 * ���ݴ��ݹ������Ѳ��ŵĺ�����������Ӧ����Ӧ�����Ӽ����е���һ�䣬��֪ͨ�����߲��ŵ���λ�á�
	 * 
	 * @param millisecond
	 *            �Ѳ��ŵĺ�����
	 */
	public void notifyTime(long millisecond) {
		// Log.i(TAG, "notifyTime");
		if (mHasLyric && mLyricSentences != null && mLyricSentences.size() != 0) {
			int newLyricIndex = seekSentenceIndex(millisecond);
			if (newLyricIndex != -1 && newLyricIndex != mIndexOfCurrentSentence) {// ����ҵ��ĸ�ʺ����ڵĲ���һ�䡣
				if (mLyricListener != null) {
					// ����һ��������Ѿ��������һ������
					mLyricListener.onLyricSentenceChanged(newLyricIndex);
				}
				mIndexOfCurrentSentence = newLyricIndex;
			}
		}
	}

	private int seekSentenceIndex(long millisecond) {
		int findStart = 0;
		if (mIndexOfCurrentSentence >= 0) {
			// ����Ѿ�ָ���˸�ʣ�������λ�ÿ�ʼ
			findStart = mIndexOfCurrentSentence;
		}

		try {
			long lyricTime = mLyricSentences.get(findStart).getStartTime();

			if (millisecond > lyricTime) { // �����Ҫ���ҵ�ʱ����������Ļ��ʱ��֮��
				// �����ʼλ�þ������һ���ˣ�ֱ�ӷ������һ�䡣
				if (findStart == (mLyricSentences.size() - 1)) {
					return findStart;
				}
				int new_index = findStart + 1;
				// �ҵ���һ�俪ʼʱ���������ʱ��ĸ��
				while (new_index < mLyricSentences.size()
						&& mLyricSentences.get(new_index).getStartTime() <= millisecond) {
					++new_index;
				}
				// ����ʵ�ǰһ���������Ҫ�ҵ��ˡ�
				return new_index - 1;
			} else if (millisecond < lyricTime) { // �����Ҫ���ҵ�ʱ����������Ļ��ʱ��֮ǰ
				// �����ʼλ�þ��ǵ�һ���ˣ�ֱ�ӷ��ص�һ�䡣
				if (findStart == 0)
					return 0;

				int new_index = findStart - 1;
				// �ҵ���ʼʱ��С������ʱ��ĸ��
				while (new_index > 0
						&& mLyricSentences.get(new_index).getStartTime() > millisecond) {
					--new_index;
				}
				// �������ˡ�
				return new_index;
			} else {
				// ��������
				return findStart;
			}
		} catch (IndexOutOfBoundsException e) {
			e.printStackTrace();
			Log.i(TAG, "�µĸ�������ˣ����Բ�����Խ����󣬲�����ᣬ����0");
			return 0;
		}
	}

	/** ����ÿ�и���ı�,һ���ı���ʿ��ܶ�Ӧ���ʱ��� */
	private void parseLine(String line) {
		if (line.equals("")) {
			return;
		}
		String content = null;
		int timeLength = 0;
		int index = 0;
		Matcher matcher = mTimePattern.matcher(line);
		int lastIndex = -1;// ���һ��ʱ���ǩ���±�
		int lastLength = -1;// ���һ��ʱ���ǩ�ĳ���

		// һ���ı���ʿ��ܶ�Ӧ���ʱ������硰[01:02.3][01:11:22.33]�����������ĵĴ����
		// һ��Ҳ���ܰ���������ӣ��硰[01:02.3]�����������ĵĴ�����[01:02:22.33]�ҵ������̲�ס���ʡ�
		List<String> times = new ArrayList<String>();

		// Ѱ�ҳ���������ʱ���������times��
		while (matcher.find()) {
			// ƥ���������������ַ�������01:02.3��01:11:22.33

			String s = matcher.group();
			index = line.indexOf("[" + s + "]");
			if (lastIndex != -1 && index - lastIndex > lastLength + 2) {
				// ��������ϴεĴ�С�����м���˱������������
				// ���ʱ���Ҫ�ֶ���
				content = trimBracket(line.substring(
						lastIndex + lastLength + 2, index));
				for (String string : times) {
					// ��ÿ��ʱ�����Ӧ��һ�ݾ��Ӵ�����Ӽ���
					long t = parseTime(string);
					if (t != -1) {
						Log.i(TAG, "line content match-->" + content);
						mLyricSentences.add(new LyricSentence(t, content));
					}
				}
				times.clear();
			}
			times.add(s);
			lastIndex = index;
			lastLength = s.length();

			Log.i(TAG, "time match--->" + s);
		}
		// ����б�Ϊ�գ����ʾ����û�з������κα�ǩ
		if (times.isEmpty()) {
			return;
		}

		timeLength = lastLength + 2 + lastIndex;
		if (timeLength > line.length()) {
			content = trimBracket(line.substring(line.length()));
		} else {
			content = trimBracket(line.substring(timeLength));
		}
		Log.i(TAG, "line content match-->" + content);
		// ��ÿ��ʱ�����Ӧ��һ�ݾ��Ӵ�����Ӽ���
		for (String s : times) {
			long t = parseTime(s);
			if (t != -1) {
				mLyricSentences.add(new LyricSentence(t, content));
			}
		}
	}

	/** ȥ��ָ���ַ����а���[XXX]��ʽ���ַ��� */
	private String trimBracket(String content) {
		String s = null;
		String result = content;
		Matcher matcher = mBracketPattern.matcher(content);
		while (matcher.find()) {
			s = matcher.group();
			result = result.replace("[" + s + "]", "");
		}
		return result;
	}

	/** ����ʵ�ʱ���ַ���ת���ɺ����������������00:01:23.45 */
	@SuppressLint("DefaultLocale")
	private long parseTime(String strTime) {
		String beforeDot = new String("00:00:00");
		String afterDot = new String("0");

		// ���ַ�����С�����ֳ����벿�ֺ�С�����֡�
		int dotIndex = strTime.indexOf(".");
		if (dotIndex < 0) {
			beforeDot = strTime;
		} else if (dotIndex == 0) {
			afterDot = strTime.substring(1);
		} else {
			beforeDot = strTime.substring(0, dotIndex);// 00:01:23
			afterDot = strTime.substring(dotIndex + 1); // 45
		}

		long intSeconds = 0;
		int counter = 0;
		while (beforeDot.length() > 0) {
			int colonPos = beforeDot.indexOf(":");
			try {
				if (colonPos > 0) {// �ҵ�ð���ˡ�
					intSeconds *= 60;
					intSeconds += Integer.valueOf(beforeDot.substring(0,
							colonPos));
					beforeDot = beforeDot.substring(colonPos + 1);
				} else if (colonPos < 0) {// û�ҵ���ʣ�¶���һ���������ˡ�
					intSeconds *= 60;
					intSeconds += Integer.valueOf(beforeDot);
					beforeDot = "";
				} else {// ��һ������ð�ţ������ܣ�
					return -1;
				}
			} catch (NumberFormatException e) {
				return -1;
			}
			++counter;
			if (counter > 3) {// ���ᳬ��Сʱ���֣���ɡ�
				return -1;
			}
		}
		// intSeconds=83

		String totalTime = String.format("%d.%s", intSeconds, afterDot);// totaoTimer
		// =
		// "83.45"
		Double doubleSeconds = Double.valueOf(totalTime); // ת��С��83.45
		return (long) (doubleSeconds * 1000);// ת�ɺ���8345
	}

}
