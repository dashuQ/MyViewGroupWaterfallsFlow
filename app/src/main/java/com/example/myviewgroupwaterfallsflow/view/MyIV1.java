package com.example.myviewgroupwaterfallsflow.view;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.myviewgroupwaterfallsflow.util.VirtualImage;

public class MyIV1 extends FrameLayout {
	private int mIndex;
	private ImageView mIV;
	private TextView mTV;
	private VirtualImage mVI;
	public MyIV1(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	public MyIV1(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public MyIV1(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	private void initView(){
		
	}
	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
	}

	public int getIndex() {
		return mIndex;
	}
	
	public void setImageBitmap(Bitmap bm) {
		// TODO Auto-generated method stub
		mIV.setImageBitmap(bm);
	}

	public void setIndex(int index) {
		this.mIndex = index;
		mTV.setText(mIndex+"");
	}

	public VirtualImage getmVI() {
		return mVI;
	}

	public void setmVI(VirtualImage mVI) {
		this.mVI = mVI;
	}
	
}
