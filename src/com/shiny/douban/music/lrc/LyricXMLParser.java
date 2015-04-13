package com.shiny.douban.music.lrc;

import java.io.InputStream;
import java.util.ArrayList;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.util.Log;

/**
 * ��ʽ���
 * @author longdw(longdawei1988@gmail.com)
 *
 */
public class LyricXMLParser {
	private static final String TAG = "LyricXMLParser";

	private final String ELEMENT_COUNT = "count";
	private final String ELEMENT_LRCID = "lrcid";

	public int parseLyricId(InputStream is) throws Exception {
		SAXParserFactory factory = SAXParserFactory.newInstance(); // ȡ��SAXParserFactoryʵ��
		SAXParser parser = factory.newSAXParser(); // ��factory��ȡSAXParserʵ��
		MyHandler handler = new MyHandler(); // ʵ�����Զ���Handler
		parser.parse(is, handler); // �����Զ���Handler�������������
		is.close();
		return handler.getFirstLyricId();
	}

	class MyHandler extends DefaultHandler {
		private int mSongCount = 0;
		private ArrayList<Integer> mLyricIds = new ArrayList<Integer>();
		private StringBuilder mStringBuilder = new StringBuilder();;

		public int getSongCount() {
			return mSongCount;
		}

		// �����������lyricID��ֻ���ص�һ��
		public int getFirstLyricId() {
			if (mSongCount == 0) {
				return -1;
			} else {
				return mLyricIds.get(0);
			}
		}

		@Override
		public void startDocument() throws SAXException {
			super.startDocument();
			mLyricIds.clear();
		}

		@Override
		public void endDocument() throws SAXException {
			super.endDocument();
		}

		@Override
		public void startElement(String uri, String localName, String qName,
				Attributes attributes) throws SAXException {
			super.startElement(uri, localName, qName, attributes);
			mStringBuilder.setLength(0); // ���ַ���������Ϊ0 �Ա����¿�ʼ��ȡԪ���ڵ��ַ��ڵ�
		}

		@Override
		public void endElement(String uri, String localName, String qName)
				throws SAXException {
			super.endElement(uri, localName, qName);
			if (localName.equals(ELEMENT_COUNT)) {
				mSongCount = Integer.valueOf(mStringBuilder.toString());
				Log.i(TAG, "��������ƥ�������Ŀ:" + mSongCount);
			}
			if (localName.equals(ELEMENT_LRCID)) {
				Log.i(TAG, "�������id:" + mStringBuilder.toString());
				mLyricIds.add(Integer.valueOf(mStringBuilder.toString()));
			}
		}

		@Override
		public void characters(char[] ch, int start, int length)
				throws SAXException {
			super.characters(ch, start, length);
			mStringBuilder.append(ch, start, length); // ����ȡ���ַ�����׷�ӵ�builder��
		}
	}
}
