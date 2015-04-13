package com.shiny.douban.music.db;

import java.util.ArrayList;
import java.util.List;

import com.shiny.douban.music.model.MusicInfo;
import com.shiny.douban.util.IConstants;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class FavoriteInfoDao implements IConstants {

	private static final String TABLE_FAVORITE = "favorite_info";
	private Context mContext;

	public FavoriteInfoDao(Context context) {
		this.mContext = context;
	}

	/**
	 * ���ղع������ֱ�������
	 * @param music
	 */
	public void saveMusicInfo(MusicInfo music) {
		SQLiteDatabase db = DatabaseHelper.getInstance(mContext);
		ContentValues cv = new ContentValues();
		cv.put("_id", music._id);
		cv.put("songid", music.songId);
		cv.put("albumid", music.albumId);
		cv.put("duration", music.duration);
		cv.put("musicname", music.musicName);
		cv.put("artist", music.artist);
		cv.put("data", music.data);
		cv.put("folder", music.folder);
		cv.put("musicnamekey", music.musicNameKey);
		cv.put("artistkey", music.artistKey);
		cv.put("favorite", 1);
		db.insert(TABLE_FAVORITE, null, cv);
	}
	
	public void deleteById(int _id) {
		SQLiteDatabase db = DatabaseHelper.getInstance(mContext);
		db.delete(TABLE_FAVORITE, "_id=?", new String[]{ _id+"" });
	}

	public List<MusicInfo> getMusicInfo() {
		SQLiteDatabase db = DatabaseHelper.getInstance(mContext);
		String sql = "select * from " + TABLE_FAVORITE;

		return parseCursor(db.rawQuery(sql, null));
	}

	private List<MusicInfo> parseCursor(Cursor cursor) {
		List<MusicInfo> list = new ArrayList<MusicInfo>();
		while (cursor.moveToNext()) {
			MusicInfo music = new MusicInfo();
			music._id = cursor.getInt(cursor.getColumnIndex("_id"));
			music.songId = cursor.getInt(cursor.getColumnIndex("songid"));
			music.albumId = cursor.getInt(cursor.getColumnIndex("albumid"));
			music.duration = cursor.getInt(cursor.getColumnIndex("duration"));
			music.musicName = cursor.getString(cursor
					.getColumnIndex("musicname"));
			music.artist = cursor.getString(cursor.getColumnIndex("artist"));
			music.data = cursor.getString(cursor.getColumnIndex("data"));
			music.folder = cursor.getString(cursor.getColumnIndex("folder"));
			music.musicNameKey = cursor.getString(cursor
					.getColumnIndex("musicnamekey"));
			music.artistKey = cursor.getString(cursor
					.getColumnIndex("artistkey"));
			music.favorite = cursor.getInt(cursor.getColumnIndex("favorite"));
			list.add(music);
		}
		cursor.close();
		return list;
	}


	/**
	 * ���ݿ����Ƿ�������
	 * 
	 * @return
	 */
	public boolean hasData() {
		SQLiteDatabase db = DatabaseHelper.getInstance(mContext);
		String sql = "select count(*) from " + TABLE_FAVORITE;
		Cursor cursor = db.rawQuery(sql, null);
		boolean has = false;
		if (cursor.moveToFirst()) {
			int count = cursor.getInt(0);
			if (count > 0) {
				has = true;
			}
		}
		cursor.close();
		return has;
	}
	
	public int getDataCount() {
		SQLiteDatabase db = DatabaseHelper.getInstance(mContext);
		String sql = "select count(*) from " + TABLE_FAVORITE;
		Cursor cursor = db.rawQuery(sql, null);
		int count = 0;
		if(cursor.moveToFirst()) {
			count = cursor.getInt(0);
		}
		return count;
	}

}
