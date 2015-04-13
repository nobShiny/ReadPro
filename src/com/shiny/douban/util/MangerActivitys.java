package com.shiny.douban.util;

import java.util.ArrayList;
import java.util.List;

/**
 * 管理activity，把activity增加到集合中
 * @author Administrator
 *
 */
public class MangerActivitys {
	public static List<Object> activitys=new ArrayList<Object>();
	public static void addActivity(Object object)  //添加新創建的activity
	{
		activitys.add(object);
	}

}
