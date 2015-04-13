package com.shiny.douban.music.interfaces;

import java.util.List;

import com.shiny.douban.music.model.MusicInfo;


public interface IQueryFinished {
	
	public void onFinished(List<MusicInfo> list);

}
