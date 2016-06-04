package com.example.tencent;

import java.util.Random;

import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.CycleInterpolator;
import android.webkit.WebView.FindListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.cebianlan.R;
import com.example.tencent.domain.Cheeses;
import com.example.tencent.utils.Utils;
import com.example.tencent.view.DragLayout;
import com.example.tencent.view.DragLayout.DragStatusListener;
import com.example.tencent.view.MyLinearLayout;
import com.nineoldandroids.view.ViewHelper;

public class MainActivity extends Activity {

	protected static final String TAG = "com.qumoy.tencent";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		final ListView mMainList = (ListView) findViewById(R.id.lv_main);
		final ListView mLeftList = (ListView) findViewById(R.id.lv_left);
		final ImageView mImageHeader = (ImageView) findViewById(R.id.iv_header);
		DragLayout mDragLayout = (DragLayout) findViewById(R.id.dl);
		MyLinearLayout mMyLinearLayout = (MyLinearLayout) findViewById(R.id.mll);
		mMyLinearLayout.setDragLayout(mDragLayout);

		mDragLayout.setDragStatusListener(new DragStatusListener() {

			@Override
			public void onOpen() {
				Utils.showToast(MainActivity.this, "onopen");
				Random random = new Random();
				int nextInt = random.nextInt(50);
				mLeftList.smoothScrollToPosition(nextInt);
			}

			@Override
			public void onDraging(float percent) {
				Log.d(TAG, "Draging:" + percent);
				ViewHelper.setAlpha(mImageHeader, 1 - percent);
			}

			@Override
			public void onClose() {
				Utils.showToast(MainActivity.this, "onclose");
				ObjectAnimator mAnim = ObjectAnimator.ofFloat(mImageHeader,
						"translationX", 15.0f);// 不是translation就无法晃动
				mAnim.setInterpolator(new CycleInterpolator(4));
				mAnim.setDuration(500);
				mAnim.start();
			}
		});

		mLeftList.setAdapter(new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, Cheeses.sCheeseStrings) {
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				View view = super.getView(position, convertView, parent);
				TextView mText = (TextView) view
						.findViewById(android.R.id.text1);
				mText.setTextColor(Color.WHITE);
				return view;
			}
		});

		mMainList.setAdapter(new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, Cheeses.NAMES));
	}

}
