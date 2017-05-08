package com.example.myviewgroupwaterfallsflow.util;

import java.net.URL;
import java.util.Random;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.ViewGroup;

import com.example.myviewgroupwaterfallsflow.activity.MainActivity;
import com.example.myviewgroupwaterfallsflow.view.MyIV;
import com.example.myviewgroupwaterfallsflow.view.MyViewGroup;

public class VirtualImage {
	public enum ImageViewState {
		NONE, LOADINF, LOADED, DRAWED
	}
	public int mIndex;
	public String mUrl;
	public int mWidth;
	public int mHeight;
	public int x;
	public int y;
	public Bitmap mBmp;
	public ImageViewState mState = ImageViewState.NONE;
	public MyIV mIV;
	public Handler mHan;

	public VirtualImage(Handler han) {
		this.mHan = han;
	}

	public void changeSizeWithWidth(int newWidth) {
		mHeight = (int) (((float) newWidth / mWidth) * mHeight);
		mWidth = newWidth;
	}

	public void recycleBMP(ViewGroup parent) {
		mState = ImageViewState.NONE;
		if (mIV != null) {
			mIV.setImageDrawable(null);
			justRecycleBMP();
			parent.removeView(mIV);
			mIV = null;
		}
	}

	public void justRecycleBMP() {
		if (mBmp != null) {
			mBmp.recycle();
			mBmp = null;
		}
//		mState = ImageViewState.NONE;
	}

	public void reLoadBMP(final ViewGroup parent) {
		mState = ImageViewState.LOADINF;
		if (mIV != null) {
			if (mBmp != null) {
				mIV.setImageBitmap(mBmp);
				mState = ImageViewState.LOADED;
				return;
			}
		}
		Runnable run = new Runnable() {
			@Override
			public void run() {
				try {
					if (!((MyViewGroup) parent).isInScreenVisible(VirtualImage.this))
						return;
					Bitmap bmpTmp = BitmapFactory.decodeStream((new URL(mUrl))
							.openStream());
					if (bmpTmp != null) {
						float sx = (float) MainActivity.getLineWithth()
								/ bmpTmp.getWidth();
						Matrix matrix = new Matrix();
						matrix.postScale(sx, sx);
						Bitmap resizeBmp = Bitmap.createBitmap(bmpTmp, 0, 0,
								bmpTmp.getWidth(), bmpTmp.getHeight(), matrix,
								true);
						bmpTmp.recycle();
						if (mBmp != null)
							mBmp.recycle();
						mBmp = resizeBmp;
						if (((MyViewGroup) parent)
								.isInScreenVisible(VirtualImage.this)) {
							parent.post(new Runnable() {
								@Override
								public void run() {
									// TODO Auto-generated method stub
									mIV = new MyIV(parent.getContext());
									mIV.setImageBitmap(mBmp);
									mIV.mVI = VirtualImage.this;
									parent.addView(mIV);
									mState = ImageViewState.LOADED;
								}
							});
						}
						else justRecycleBMP();

					}

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		mHan.post(run);
	}

	public void loadMoreBMP() {
		Runnable run = new Runnable() {
			public void run() {
				Random r = new Random();
				int id = r.nextInt(ImageURL.imageUrls.length);
				String path = ImageURL.imageUrls[id];
				Bitmap bmp = null;
				try {
					bmp = BitmapFactory.decodeStream((new URL(path))
							.openStream());
					float sx = (float) MainActivity.mWidth
							/ MainActivity.mLineNum / bmp.getWidth();
					Matrix matrix = new Matrix();
					matrix.postScale(sx, sx);
					Bitmap resizeBmp = Bitmap.createBitmap(bmp, 0, 0,
							bmp.getWidth(), bmp.getHeight(), matrix, true);
					bmp.recycle();
					Message msg = new Message();
					msg.what = 100;
					msg.arg1 = mIndex + 1;
					Bundle b = new Bundle();
					b.putParcelable("bitmap", resizeBmp);
					b.putString("path", path);
					msg.setData(b);
					mHan.sendMessage(msg);
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		};
		mHan.post(run);
	}
}
