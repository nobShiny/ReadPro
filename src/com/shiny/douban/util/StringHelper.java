package com.shiny.douban.util;

import java.text.DecimalFormat;

import android.text.TextUtils;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

public class StringHelper {
	public static enum CharType {
		DELIMITER, // ����ĸ��ֹ�ַ������磬���������ȵȡ��� ����U0000-U0080��
		NUM, // 2�ֽ����֣�������
		LETTER, // gb2312�еģ�����:���£ã�2�ֽ��ַ�ͬʱ���� 1�ֽ��ܱ�ʾ�� basic latin and latin-1
		OTHER, // �����ַ�
		CHINESE;// ������
	}

	/**
	 * �ж�����char���ͱ������ַ�����
	 * 
	 * @param c
	 *            char���ͱ���
	 * @return CharType �ַ�����
	 */
	public static CharType checkType(char c) {
		CharType ct = null;

		// ���ģ���������0x4e00-0x9fbb
		if ((c >= 0x4e00) && (c <= 0x9fbb)) {
			ct = CharType.CHINESE;
		}

		// Halfwidth and Fullwidth Forms�� ��������0xff00-0xffef
		else if ((c >= 0xff00) && (c <= 0xffef)) { // 2�ֽ�Ӣ����
			if (((c >= 0xff21) && (c <= 0xff3a))
					|| ((c >= 0xff41) && (c <= 0xff5a))) {
				ct = CharType.LETTER;
			}

			// 2�ֽ�����
			else if ((c >= 0xff10) && (c <= 0xff19)) {
				ct = CharType.NUM;
			}

			// �����ַ���������Ϊ�Ǳ�����
			else
				ct = CharType.DELIMITER;
		}

		// basic latin���������� 0000-007f
		else if ((c >= 0x0021) && (c <= 0x007e)) { // 1�ֽ�����
			if ((c >= 0x0030) && (c <= 0x0039)) {
				ct = CharType.NUM;
			} // 1�ֽ��ַ�
			else if (((c >= 0x0041) && (c <= 0x005a))
					|| ((c >= 0x0061) && (c <= 0x007a))) {
				ct = CharType.LETTER;
			}
			// �����ַ���������Ϊ�Ǳ�����
			else
				ct = CharType.DELIMITER;
		}

		// latin-1����������0080-00ff
		else if ((c >= 0x00a1) && (c <= 0x00ff)) {
			if ((c >= 0x00c0) && (c <= 0x00ff)) {
				ct = CharType.LETTER;
			} else
				ct = CharType.DELIMITER;
		} else
			ct = CharType.OTHER;

		return ct;
	}

	/**
	 * ��ȡ�������ֵ�ƴ��������ĸ
	 * 
	 * @param c
	 *            һ������
	 * @return ���c���Ǻ��֣�����0�����򷵻ظú��ֵ�ƴ��������ĸ
	 */
	public static char getPinyinFirstLetter(char c) {
		String[] pinyin = null;
		HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
		format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);// ���������
		try {
			// ��ȡ�ú��ֵ�ƴ���������Ƕ����֣������������Ž����
			pinyin = PinyinHelper.toHanyuPinyinStringArray(c, format);
		} catch (BadHanyuPinyinOutputFormatCombination e) {
			e.printStackTrace();
		}

		// ���c���Ǻ��֣�toHanyuPinyinStringArray�᷵��null
		if (pinyin == null) {
			return 0;
		}

		// ֻȡһ������������Ƕ����֣���ȡ��һ������
		return pinyin[0].charAt(0);
	}

	public static String bytesToMB(long bytes) {
		float size = (float) (bytes * 1.0 / 1024 / 1024);
		String result = null;
		if (bytes >= (1024 * 1024)) {
			result = new DecimalFormat("###.00").format(size) + "MB";
		} else {
			result = new DecimalFormat("0.00").format(size) + "MB";
		}
		return result;
	}

	/**
	 * ���ַ����е�����ת��Ϊƴ��,�����ַ�����
	 * 
	 * @param inputString
	 * @return
	 */
	public static String getPingYin(String inputString) {
		if (TextUtils.isEmpty(inputString)) {
			return "";
		}
		HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
		format.setCaseType(HanyuPinyinCaseType.LOWERCASE);
		format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
		format.setVCharType(HanyuPinyinVCharType.WITH_V);

		char[] input = inputString.trim().toCharArray();
		String output = "";

		try {
			for (int i = 0; i < input.length; i++) {
				if (java.lang.Character.toString(input[i]).matches(
						"[\\u4E00-\\u9FA5]+")) {
					String[] temp = PinyinHelper.toHanyuPinyinStringArray(
							input[i], format);
					if (temp == null || TextUtils.isEmpty(temp[0])) {
						continue;
					}
					output += temp[0].replaceFirst(temp[0].substring(0, 1),
							temp[0].substring(0, 1).toUpperCase());
				} else
					output += java.lang.Character.toString(input[i]);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return output;
	}
}
