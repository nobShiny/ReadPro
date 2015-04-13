package com.shiny.douban.util;

import com.shiny.douban.R;

import android.view.View;
import android.widget.ImageView;


public class ViewCache {

	private View baseView;
	private ImageView imageView;

	public ViewCache(View baseView) {
		this.baseView = baseView;
	}

	public ImageView getImageView() {
		if (imageView == null) {
			imageView = (ImageView) baseView.findViewById(R.id.book_img);
		}
		return imageView;
	}

}