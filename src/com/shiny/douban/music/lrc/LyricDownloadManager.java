package com.shiny.douban.music.lrc;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;

import com.shiny.douban.music.MusicApp;

import android.content.Context;
import android.util.Log;


/**
 * �������
 * @author Administrator
 *
 */
public class LyricDownloadManager {
	private static final String TAG = LyricDownloadManager.class
			.getSimpleName();
	public static final String GB2312 = "GB2312";
	public static final String UTF_8 = "utf-8";
	private final int mTimeOut = 10 * 1000;
	private LyricXMLParser mLyricXMLParser = new LyricXMLParser();
	private URL mUrl = null;
	private int mDownloadLyricId = -1;

//	private Context mContext = null;

	public LyricDownloadManager(Context c) {
//		mContext = c;
	}

	/*
	 * ���ݸ������͸�����ȡ�øø��XML��Ϣ�ļ� ���ظ�ʱ���·��
	 */
	public String searchLyricFromWeb(String musicName, String singerName, String oldMusicName) {
		Log.i(TAG, "����ǰ��������:" + musicName + ",������:" + singerName);

		// ������������Ǻ��֣���ô��Ҫ���б���ת��
		try {
			musicName = URLEncoder.encode(musicName, UTF_8);
			singerName = URLEncoder.encode(singerName, UTF_8);
		} catch (UnsupportedEncodingException e2) {
			e2.printStackTrace();
		}

		// �ٶ����ֺе�API
		String strUrl = "http://box.zhangmen.baidu.com/x?op=12&count=1&title="
				+ musicName + "$$" + singerName + "$$$$";

		// ����URL
		try {
			mUrl = new URL(strUrl);
			Log.i(TAG, "�����ȡ�����Ϣ��URL��" + mUrl);
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		try {
			HttpURLConnection httpConn = (HttpURLConnection) mUrl
					.openConnection();
			httpConn.setReadTimeout(mTimeOut);
			if (httpConn.getResponseCode() != HttpURLConnection.HTTP_OK) {
				Log.i(TAG, "http����ʧ��");
				return null;
			}
			httpConn.connect();
			Log.i(TAG, "http���ӳɹ�");

			// ���ٶ����ֺеķ��ص����������ݸ��Զ����XML����������������ʵ�����ID
			mDownloadLyricId = mLyricXMLParser.parseLyricId(httpConn
					.getInputStream());
			httpConn.disconnect();
		} catch (IOException e1) {
			e1.printStackTrace();
			Log.i(TAG, "http��������IO�쳣");
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			Log.i(TAG, "XML��������");
			return null;
		}
		return fetchLyricContent(musicName, singerName, oldMusicName);
	}

	/** ���ݸ������ID����ȡ�����ϵĸ���ı����� 
	 * @param musicInfo */
	private String fetchLyricContent(String musicName, String singerName, String oldMusicName) {
		if (mDownloadLyricId == -1) {
			Log.i(TAG, "δָ���������ID");
			return null;
		}
		BufferedReader br = null;
		StringBuilder content = null;
		String temp = null;
		String lyricURL = "http://box.zhangmen.baidu.com/bdlrc/"
				+ mDownloadLyricId / 100 + "/" + mDownloadLyricId + ".lrc";
		Log.i(TAG, "��ʵ���ʵ���ص�ַ:" + lyricURL);

		try {
			mUrl = new URL(lyricURL);
		} catch (MalformedURLException e2) {
			e2.printStackTrace();
		}

		// ��ȡ����ı��������ַ�������
		try {
			// ������������
			br = new BufferedReader(new InputStreamReader(mUrl.openStream(),
					GB2312));
			if (br != null) {
				content = new StringBuilder();
				// ���л�ȡ����ı�
				while ((temp = br.readLine()) != null) {
					content.append(temp);
					Log.i(TAG, "<Lyric>" + temp);
				}
				br.close();
			}
		} catch (IOException e1) {
			e1.printStackTrace();
			Log.i(TAG, "��ʻ�ȡʧ��");
		}

		try {
			musicName = URLDecoder.decode(musicName, UTF_8);
			singerName = URLDecoder.decode(singerName, UTF_8);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		if (content != null) {
			// ��鱣���Ŀ¼�Ƿ��Ѿ�����

//			String folderPath = PreferenceManager.getDefaultSharedPreferences(
//					mContext).getString(SettingFragment.KEY_LYRIC_SAVE_PATH,
//					Constant.LYRIC_SAVE_FOLDER_PATH);
			String folderPath = MusicApp.lrcPath;

			File savefolder = new File(folderPath);
			if (!savefolder.exists()) {
				savefolder.mkdirs();
			}
			String savePath = folderPath + File.separator + oldMusicName
					+ ".lrc";
//			String savePath = folderPath + File.separator + musicName + ".lrc";
			Log.i(TAG, "��ʱ���·��:" + savePath);

			saveLyric(content.toString(), savePath);

			return savePath;
		} else {
			return null;
		}

	}

	/** ����ʱ��浽���أ�д������� */
	private void saveLyric(String content, String filePath) {
		// ���浽����
		File file = new File(filePath);
		try {
			OutputStream outstream = new FileOutputStream(file);
			OutputStreamWriter out = new OutputStreamWriter(outstream);
			out.write(content);
			out.close();
		} catch (java.io.IOException e) {
			e.printStackTrace();
			Log.i(TAG, "���ź��������д�����ʱ������IO����");
		}
		Log.i(TAG, "��ʱ���ɹ�");
	}
}
