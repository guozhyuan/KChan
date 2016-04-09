package com.example.guo.ui;

import android.graphics.Bitmap;

import com.example.guo.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

public class ImageLoaderOptions {
	// listview中使用的options
	public static DisplayImageOptions list_options = new DisplayImageOptions.Builder()
			.showImageOnLoading(R.drawable.bg_card)// 设置加载过程中显示的图片
			.showImageForEmptyUri(R.drawable.bg_card)// 设置如图图片为空的时候显示的图片
			.showImageOnFail(R.drawable.bg_card)// 设置加载失败显示的图片
			.cacheInMemory(true)// 在内存中缓存
			.cacheOnDisk(true)// 在硬盘缓存
			.considerExifParams(true)// 会识别图片的方向信息
			.displayer(new FadeInBitmapDisplayer(500)).build();// 渐渐显示的动画
	// .displayer(new RoundedBitmapDisplayer(28)).build();//带有圆角的图片;


}
