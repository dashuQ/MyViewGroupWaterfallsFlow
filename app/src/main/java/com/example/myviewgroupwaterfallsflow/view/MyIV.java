package com.example.myviewgroupwaterfallsflow.view;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;

import com.example.myviewgroupwaterfallsflow.util.VirtualImage;

public class MyIV extends android.support.v7.widget.AppCompatImageView {
	int index;
	public VirtualImage mVI;
	public MyIV(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	public MyIV(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public MyIV(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		// TODO Auto-generated method stub
		super.onLayout(changed, left, top, right, bottom);
		
	}
	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
		Paint p =  new Paint();
		p.setColor(Color.YELLOW);
		p.setStrokeWidth(30f);
		p.setTextSize(100f);
		canvas.drawText(mVI.mIndex+" ", mVI.mWidth/2, mVI.mHeight/2, p);
	}

	public int getIndex() {
		return index;
	}
	
	@Override
	public void setImageBitmap(Bitmap bm) {
		// TODO Auto-generated method stub
		super.setImageBitmap(bm);
	}

	public void setIndex(int index) {
		this.index = index;
		Log.e("IV", "onlayout "+index);
	}
	
}
