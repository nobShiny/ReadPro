package com.shiny.douban.util;

import java.util.HashMap;
import java.util.Map;

import com.shiny.douban.R;
import com.shiny.douban.entity.Subject;

public class StatusUtil {
	public static Map<Integer, String> statusDescMap = new HashMap<Integer, String>();;
	public static Map<Integer, String> statusMap = new HashMap<Integer, String>();
	public static Map<Integer, Integer> buttonStatusMap = new HashMap<Integer, Integer>();;

	static {

		buttonStatusMap.put(R.id.btn_book_wish, R.string.book_wish);
		buttonStatusMap.put(R.id.btn_book_reading, R.string.book_reading);
		buttonStatusMap.put(R.id.btn_book_read, R.string.book_read);

		buttonStatusMap.put(R.id.btn_movie_wish, R.string.movie_wish);
		buttonStatusMap.put(R.id.btn_movie_watched, R.string.movie_watched);

		buttonStatusMap.put(R.id.btn_music_wish, R.string.music_wish);
		buttonStatusMap.put(R.id.btn_music_listening, R.string.music_listening);
		buttonStatusMap.put(R.id.btn_music_listened, R.string.music_listened);

		statusMap.put(R.string.book_wish, "wish");
		statusMap.put(R.string.book_reading, "reading");
		statusMap.put(R.string.book_read, "read");

		statusMap.put(R.string.movie_wish, "wish");
		statusMap.put(R.string.movie_watched, "watched");

		statusMap.put(R.string.music_wish, "wish");
		statusMap.put(R.string.music_listening, "listening");
		statusMap.put(R.string.music_listened, "listened");

		statusDescMap.put(R.string.book_wish, "���");
		statusDescMap.put(R.string.book_reading, "�ڶ�");
		statusDescMap.put(R.string.book_read, "����");

		statusDescMap.put(R.string.movie_wish, "�뿴");
		statusDescMap.put(R.string.movie_watched, "����");

		statusDescMap.put(R.string.music_wish, "����");
		statusDescMap.put(R.string.music_listening, "����");
		statusDescMap.put(R.string.music_listened, "����");

	}

	//���ݰ�ťId����ȡ�ղ�״̬
	public static String getStatus(Integer buttonId) {
		return statusMap.get(buttonStatusMap.get(buttonId));
	}

	//���ݰ�ťId����ȡ�ղ�״̬��������
	public static String getStatusDesc(Integer buttonId) {
		return statusDescMap.get(buttonStatusMap.get(buttonId));
	}

	//������Ŀ����ȡ����Ŀ���ղ�״̬
	public static String getStatusDesc(Subject subject) {
		String status = subject.getStatus();
		String type = subject.getType();
		if (("wish".equals(status)) && ("book".equals(type))) {
			return statusDescMap.get(R.string.book_wish);
		}
		if (("reading".equals(status)) && ("book".equals(type))) {
			return statusDescMap.get(R.string.book_reading);
		}
		if (("read".equals(status)) && ("book".equals(type))) {
			return statusDescMap.get(R.string.book_read);
		}
		if (("wish".equals(status)) && ("movie".equals(type))) {
			return statusDescMap.get(R.string.movie_wish);
		}
		if (("watched".equals(status)) && ("movie".equals(type))) {
			return statusDescMap.get(R.string.movie_watched);
		}
		if (("wish".equals(status)) && ("music".equals(type))) {
			return statusDescMap.get(R.string.music_wish);
		}
		if (("listening".equals(status)) && ("music".equals(type))) {
			return statusDescMap.get(R.string.music_listening);
		}
		if (("listened".equals(status)) && ("music".equals(type))) {
			return statusDescMap.get(R.string.music_listened);
		}
		return "";
	}
}