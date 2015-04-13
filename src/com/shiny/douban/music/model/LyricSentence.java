package com.shiny.douban.music.model;

/**
 * ��ʾ��ӣ���һ��ʱ�����һ�и����ɣ��硰[00.03.21.56]���ǵ������ǰ�Ĵ��족
 * */
public class LyricSentence {

	/** ���~�ı��Ŀ�ʼʱ���ת��Ϊ��������ֵ����[00.01.02.34]Ϊ62340���� */
	private long startTime = 0;

	/**һ���ʵ�ʵ��*/
	private long duringTime = 0;

	/** ÿ��ʱ�����Ӧ��һ�и���ı�,�硰[00.03.21.56]���ǵ������ǰ�Ĵ��족�еġ����ǵ������ǰ�Ĵ��족 */
	private String contentText = "";

	public LyricSentence(long time, String text) {
		startTime = time;
		contentText = text;
	}

	public long getStartTime() {
		return startTime;
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	public String getContentText() {
		return contentText;
	}

	public void setContentText(String contentText) {
		this.contentText = contentText;
	}

	public long getDuringTime() {
		return duringTime;
	}

	public void setDuringTime(long duringTime) {
		this.duringTime = duringTime;
	}
}
