package com.example.tencent.view;

import com.example.tencent.view.DragLayout.Status;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.LinearLayout;

public class MyLinearLayout extends LinearLayout {
	private DragLayout mDragLayout;

	public MyLinearLayout(Context context) {
		super(context);
	}

	public MyLinearLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public void setDragLayout(DragLayout mDragLayout) {
		this.mDragLayout = mDragLayout;
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		if (mDragLayout.getStatus() == Status.close) {
			return super.onInterceptTouchEvent(ev);

		} else {
			return true; // ¶¼À¹½Ø
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (mDragLayout.getStatus() == Status.close) {
			return super.onInterceptTouchEvent(event);

		} else {
			if (event.getAction() == MotionEvent.ACTION_UP) {
				mDragLayout.close();
			}
			return true; // ¶¼À¹½Ø
		}
	}
}
