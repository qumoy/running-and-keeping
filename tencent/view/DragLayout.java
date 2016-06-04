package com.example.tencent.view;

import com.nineoldandroids.view.ViewHelper;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.support.v4.widget.ViewDragHelper.Callback;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

public class DragLayout extends FrameLayout {
	private ViewDragHelper mDraghelper;
	private ViewGroup mLeftContent;
	private ViewGroup mMainContent;
	private int mWidth;
	private int mHeight;
	private int mRange;

	public Status getStatus() {
		return mStatus;
	}

	public void setStatus(Status mStatus) {
		this.mStatus = mStatus;
	}

	private DragStatusListener mListener;
	private Status mStatus = Status.close;

	public static enum Status {
		open, close, draging;
	}

	public interface DragStatusListener {
		void onOpen();

		void onClose();

		void onDraging(float percent);
	}

	public void setDragStatusListener(DragStatusListener mListener) {
		this.mListener = mListener;

	}

	public DragLayout(Context context) {
		this(context, null);
	}

	public DragLayout(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public DragLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

		mDraghelper = ViewDragHelper.create(this, mCallBack);
	}

	ViewDragHelper.Callback mCallBack = new Callback() {

		@Override
		public boolean tryCaptureView(View child, int pointerId) {
			return true;
		}

		@Override
		public void onViewCaptured(View capturedChild, int activePointerId) {
			super.onViewCaptured(capturedChild, activePointerId);
		}

		@Override
		public int getViewHorizontalDragRange(View child) {
			return mRange;
		}

		public int clampViewPositionHorizontal(View child, int left, int dx) {
			if (child == mMainContent) {
				left = fixLeft(left);
			}
			return left;
		}

		@Override
		public int clampViewPositionVertical(View child, int top, int dy) {
			return super.clampViewPositionVertical(child, top, dy);
		}

		@Override
		public void onViewPositionChanged(View changedView, int left, int top,
				int dx, int dy) {
			super.onViewPositionChanged(changedView, left, top, dx, dy);

			int newLeft = left;
			if (changedView == mLeftContent) {
				newLeft = mMainContent.getLeft() + dx;
			}
			newLeft = fixLeft(newLeft);
			if (changedView == mLeftContent) {
				mLeftContent.layout(0, 0, 0 + mWidth, 0 + mHeight);
				mMainContent.layout(newLeft, 0, newLeft + mWidth, 0 + mHeight);
			}

			dispatchDragEvent(newLeft);

		}

		@Override
		public void onViewReleased(View releasedChild, float xvel, float yvel) {
			super.onViewReleased(releasedChild, xvel, yvel);

			if (xvel == 0 && mMainContent.getLeft() > mRange / 2) {
				open();
			} else if (xvel > 0) {
				open();
			} else {
				close();
			}
		}

		@Override
		public void onViewDragStateChanged(int state) {
			super.onViewDragStateChanged(state);
		}

	};

	private int fixLeft(int left) {
		if (left < 0) {
			left = 0;
		} else if (left > mRange) {
			left = mRange;
		}
		return left;
	}

	private void dispatchDragEvent(int newLeft) {
		float percent = newLeft * 1.0f / mRange;
		// 侧面板
		// 根据根据状态进行回调
		if (mListener != null) {
			mListener.onDraging(percent);
		}
		Status preStatus = mStatus;
		mStatus = updateStatus(percent);
		if (mStatus != preStatus) {
			if (mStatus == Status.close) {
				if (mListener != null) {
					mListener.onClose();
				}
			} else if (mStatus == Status.open) {
				if (mListener != null) {
					mListener.onOpen();
				}
			}
		}
		animView(percent);

	}

	private Status updateStatus(float percent) {
		if (percent == 0) {
			return Status.close;
		} else if (percent == 1) {
			return Status.open;
		}

		return Status.draging;
	}

	private void animView(float percent) {
		ViewHelper.setScaleX(mLeftContent, evaluate(percent, 0.5f, 1.0f));
		ViewHelper.setScaleY(mLeftContent, 0.5f + 0.5f * percent);

		ViewHelper.setTranslationX(mLeftContent,
				evaluate(percent, -mWidth / 2.0f, 0));

		ViewHelper.setAlpha(mLeftContent, evaluate(percent, 0.5f, 1.0f));
		// 主面板
		ViewHelper.setScaleX(mMainContent, evaluate(percent, 1.0f, 0.8f));
		ViewHelper.setScaleY(mMainContent, evaluate(percent, 1.0f, 0.8f));
		// 背景
		// getBackground().setColorFilter((Integer)caculateValue(percent,
		// Color.YELLOW, Color.BLUE), Mode.SRC_OVER);
	}

	public Float evaluate(float fraction, Number startValue, Number endValue) {
		float startFloat = startValue.floatValue();
		return startFloat + fraction * (endValue.floatValue() - startFloat);
	}

	private int caculateValue(float fraction, Object start, Object end) {

		int startInt = (Integer) start;
		int startIntA = startInt >> 24 & 0xff;
		int startIntR = startInt >> 16 & 0xff;
		int startIntG = startInt >> 8 & 0xff;
		int startIntB = startInt & 0xff;

		int endInt = (Integer) end;
		int endIntA = endInt >> 24 & 0xff;
		int endIntR = endInt >> 16 & 0xff;
		int endIntG = endInt >> 8 & 0xff;
		int endIntB = endInt & 0xff;

		return ((int) (startIntA + (endIntA - startIntA) * fraction)) << 24
				| ((int) (startIntR + (endIntR - startIntR) * fraction)) << 16
				| ((int) (startIntG + (endIntG - startIntG) * fraction)) << 8
				| ((int) (startIntB + (endIntB - startIntB) * fraction));
	}

	@Override
	public void computeScroll() {

		if (mDraghelper.continueSettling(true)) {
			ViewCompat.postInvalidateOnAnimation(this);
		}
	}

	private void open() {
		open(true);
	}

	public void close() {
		close(true);
	}

	public void close(boolean isSmooth) {
		int finalLeft = 0;
		if (isSmooth) {
			if (mDraghelper.smoothSlideViewTo(mMainContent, finalLeft, 0)) {
				ViewCompat.postInvalidateOnAnimation(this);
			}

		} else {

			mMainContent.layout(finalLeft, 0, finalLeft + mWidth, 0 + mHeight);
		}
	}

	private void open(boolean isSmooth) {
		int finalLeft = mRange;
		if (isSmooth) {
			if (mDraghelper.smoothSlideViewTo(mMainContent, finalLeft, 0)) {
				ViewCompat.postInvalidateOnAnimation(this);
			}

		} else {

			mMainContent.layout(finalLeft, 0, finalLeft + mWidth, 0 + mHeight);
		}
	}

	public boolean onInterceptTouchEvent(MotionEvent ev) {
		return mDraghelper.shouldInterceptTouchEvent(ev);
	};

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		try {
			mDraghelper.processTouchEvent(event);
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 改成true ，在keydown之后持续接收事件
		return true;
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		if (getChildCount() < 2) {
			throw new IllegalStateException("至少要有孩纸");
		}

		if (!(getChildAt(0) instanceof ViewGroup && getChildAt(1) instanceof ViewGroup)) {
			throw new IllegalArgumentException("子view 必须是viewgroup的子类");
		}
		mLeftContent = (ViewGroup) getChildAt(0);
		mMainContent = (ViewGroup) getChildAt(1);

	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);

		mWidth = getMeasuredWidth();
		mHeight = getMeasuredHeight();
		mRange = (int) (mWidth * 0.6f);
	}

}
