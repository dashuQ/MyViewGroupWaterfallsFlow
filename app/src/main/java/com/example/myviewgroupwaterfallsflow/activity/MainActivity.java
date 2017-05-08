package com.example.myviewgroupwaterfallsflow.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;

import com.example.myviewgroupwaterfallsflow.R;
import com.example.myviewgroupwaterfallsflow.util.ImageURL;
import com.example.myviewgroupwaterfallsflow.util.VirtualImage;
import com.example.myviewgroupwaterfallsflow.view.MyIV;
import com.example.myviewgroupwaterfallsflow.view.MyViewGroup;

import java.net.URL;
import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    int mPicNum = 40;
    private MyViewGroup myviewgroup;
    public static int mLineNum = 3;
    public static int mWidth = 0;
    Handler mHan;
    HandlerThread mLoadThread;
    int mLayoutHeight[];
    public static ArrayList<VirtualImage> mVImages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myviewgroup = (MyViewGroup) findViewById(R.id.myviewgroup);
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        mWidth = dm.widthPixels - 3 * (1 + mLineNum);
        mLoadThread = new HandlerThread("loadPic");
        mLoadThread.start();
        mHan = new Handler(mLoadThread.getLooper()) {
            @Override
            public void handleMessage(final Message msg) {
                // TODO Auto-generated method stub
                super.handleMessage(msg);
                if (msg.what == 100) {
                    final Bitmap bmp = (Bitmap) msg.getData().get("bitmap");
                    final int index = msg.arg1;
                    final String path = msg.getData().getString("path");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            addImageView(bmp, index, path);
                        }
                    });
                } else if (msg.what == 101) {

                }
            }
        };
        refreshView(mPicNum, mLineNum);
    }

    private void refreshView(int picNum, int lineNum) {
        mLayoutHeight = new int[lineNum];
        if (mVImages != null) {
            for (int i = 0; i < mVImages.size(); i++)
                mVImages.get(i).recycleBMP(myviewgroup);
        }
        mVImages = new ArrayList<VirtualImage>();
        loadPics(0);
    }

    private void loadPics(final int index) {
        Runnable run = new Runnable() {
            public void run() {
                Random r = new Random();
                int id = r.nextInt(ImageURL.imageUrls.length);
                String path = ImageURL.imageUrls[id];
                int l=ImageURL.imageUrls.length;
                try {

//                for(int i=0;i<l;i++){
//                    String path = ImageURL.imageUrls[id];
//                    String path = ImageURL.imageUrls[i];
                    Bitmap bmp = null;

                    bmp = BitmapFactory.decodeStream((new URL(path))
                            .openStream());
                    if (bmp != null) {
                        float sx = (float) mWidth / mLineNum / bmp.getWidth();
                        Matrix matrix = new Matrix();
                        matrix.postScale(sx, sx);
                        Bitmap resizeBmp = Bitmap.createBitmap(bmp, 0, 0,
                                bmp.getWidth(), bmp.getHeight(), matrix, true);
                        bmp.recycle();
                        Message msg = new Message();
                        msg.what = 100;
                        msg.arg1 = index;
                        Bundle b = new Bundle();
                        b.putParcelable("bitmap", resizeBmp);
                        b.putString("path", path);
                        msg.setData(b);
                        mHan.sendMessage(msg);
                    } else {
                        loadPics(index + 1);
                        return;
                    }
//                }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        };
        mHan.post(run);
    }

    private void addImageView(Bitmap bmp, int index, String path) {
        VirtualImage vi = new VirtualImage(mHan);
        vi.mIndex = index;
        vi.mUrl = path;
        vi.mBmp = bmp;
        vi.mHeight = bmp.getHeight();
        vi.mWidth = bmp.getWidth();
        int minIndex = 0;
        for (int j = 1; j < mLayoutHeight.length; j++) {
            if (mLayoutHeight[j] < mLayoutHeight[minIndex]) {
                minIndex = j;
            }
        }
        vi.x = minIndex * vi.mWidth + (minIndex + 1) * 3;
        vi.y = mLayoutHeight[minIndex] + 3;
        mLayoutHeight[minIndex] = vi.y + vi.mHeight;
        if (myviewgroup.isInScreenVisible(vi)) {
            MyIV iv = new MyIV(this);
            iv.mVI = vi;
            vi.mIV = iv;
            iv.setImageBitmap(vi.mBmp);
            iv.mVI.mState = VirtualImage.ImageViewState.LOADED;
            myviewgroup.addView(iv);
            loadPics(index + 1);
        } else {
            myviewgroup.setFirstInVisibleVI(vi);
        }
        mVImages.add(vi);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(1001, 100, 1, "add one line");
        menu.add(1001, 101, 2, "reduce one line");
        menu.add(1001, 102, 3, "add 20 pics");
        menu.add(1001, 103, 4, "reduce 20 pics");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 100:
                mLineNum++;
                break;
            case 101:
                mLineNum--;
                break;
            case 102:
                mPicNum += 20;
                break;
            case 103:
                mPicNum -= 20;
                break;
            default:
                break;
        }
        refreshView(mPicNum, mLineNum);
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mLoadThread.quit();
        for (int i = 0; i < mVImages.size(); i++)
            mVImages.get(i).recycleBMP(myviewgroup);
        myviewgroup.removeAllViews();
    }

    public static int getLineWithth() {
        return mWidth / mLineNum;
    }

}
