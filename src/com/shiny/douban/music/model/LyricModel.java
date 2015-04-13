package com.shiny.douban.music.model;

import java.io.Serializable;
import java.util.List;
public class LyricModel implements Serializable {
	private static final long serialVersionUID = 1316511579773918698L;
	private List<SentenceModel> sentenceList;
	
	public LyricModel(List<SentenceModel> list)
	{
		this.sentenceList = list;
	}
	
	public List<SentenceModel> getSentenceList()
	{
		return sentenceList;
	}
	
	/**
	 * �õ���ǰ���ڲ��ŵ���һ����±� �������Ҳ�������Ϊ�ͷҪ��һ�� �Լ��ľ��� �����Լ����Ժ�Ͳ������Ҳ�����
	 * 
	 * @return �±�
	 */
	public int getNowSentenceIndex(long t) {
		for (int i = 0; i < sentenceList.size(); i++) {
			if (sentenceList.get(i).isInTime(t)) {
				return i;
			}
		}
		// throw new RuntimeException("��Ȼ�������Ҳ����������");
		return -1;
	}
}
