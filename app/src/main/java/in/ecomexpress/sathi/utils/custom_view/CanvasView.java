package in.ecomexpress.sathi.utils.custom_view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff.Mode;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * @Author Anuj Bharat Patel
 * @Package ShrutiPickup
 * @FileName CanvasView.java
 * @DateTime 11-Nov-2015 - 5:49:51 pm
 */
@SuppressLint("ClickableViewAccessibility")
public class CanvasView extends View {
	private static final float STROKE_WIDTH = 7f;
	private static final int STROK_COLOR = 0xFF000000;
	public Bitmap mBitmap;
	public Canvas mCanvas;
	private final Path mPath;
	private final Paint mBitmapPaint;
	private final Paint mPaint;

	/** Optimizes painting by invalidating the smallest possible area. **/
	private final RectF dirtyRect = new RectF();

	public CanvasView(Context c, AttributeSet attrs) {
		super(c, attrs);
		mPath = new Path();
		mBitmapPaint = new Paint(Paint.DITHER_FLAG);
		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mPaint.setDither(true);
		mPaint.setColor(STROK_COLOR);
		mPaint.setStyle(Paint.Style.STROKE);
		mPaint.setStrokeJoin(Paint.Join.ROUND);
		mPaint.setStrokeCap(Paint.Cap.ROUND);
		mPaint.setStrokeWidth(STROKE_WIDTH);

	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
		mCanvas = new Canvas(mBitmap);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);
		canvas.drawPath(mPath, mPaint);
	}

	private float mX, mY;
	private static final float TOUCH_TOLERANCE = 4;

	private void touch_start(float x, float y) {
		mPath.reset();
		mPath.moveTo(x, y);
		mX = x;
		mY = y;
	}

	private void touch_move(float x, float y) {
		float dx = Math.abs(x - mX);
		float dy = Math.abs(y - mY);
		if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
			mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
			mX = x;
			mY = y;
		}
	}

	private void touch_up(MotionEvent event) {
		resetDirtyRect(event.getX(), event.getY());
		// When the hardware tracks events faster than they are delivered,
		// the event will contain a history of those skipped points.
		int historySize = event.getHistorySize();
		for (int i = 0; i < historySize; i++) {
			float historicalX = event.getHistoricalX(i);
			float historicalY = event.getHistoricalY(i);
			expandDirtyRect(historicalX, historicalY);
			mPath.lineTo(historicalX, historicalY);
		}
		mPath.lineTo(mX, mY);
		// commit the path to our off screen
		mCanvas.drawPath(mPath, mPaint);
		mPath.reset();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		float x = event.getX();
		float y = event.getY();

		switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN :
				touch_start(x, y);
				invalidate();
				break;
			case MotionEvent.ACTION_MOVE :
				touch_move(x, y);
				invalidate();
				break;
			case MotionEvent.ACTION_UP :
				touch_up(event);
				invalidate();
				break;
		}
		return true;
	}

	public Bitmap getBitmap() {
		this.setDrawingCacheEnabled(true);
		this.buildDrawingCache();
		Bitmap bmp = Bitmap.createBitmap(this.getDrawingCache());
		this.setDrawingCacheEnabled(false);
		return bmp;
	}

	public void clear() {
		mCanvas.drawColor(0, Mode.CLEAR);
		invalidate();
	}

	/** On replaying history to ensure the dirty region includes all points. **/
	private void expandDirtyRect(float historicalX, float historicalY) {
		if (historicalX < dirtyRect.left) {
			dirtyRect.left = historicalX;
		} else if (historicalX > dirtyRect.right) {
			dirtyRect.right = historicalX;
		}
		if (historicalY < dirtyRect.top) {
			dirtyRect.top = historicalY;
		} else if (historicalY > dirtyRect.bottom) {
			dirtyRect.bottom = historicalY;
		}
	}

	/** Resets the dirty region when the motion event occurs. **/
	private void resetDirtyRect(float eventX, float eventY) {
		// The lastTouchX and lastTouchY were set when the ACTION_DOWN motion
		dirtyRect.left = Math.min(mX, eventX);
		dirtyRect.right = Math.max(mX, eventX);
		dirtyRect.top = Math.min(mY, eventY);
		dirtyRect.bottom = Math.max(mY, eventY);
	}
}