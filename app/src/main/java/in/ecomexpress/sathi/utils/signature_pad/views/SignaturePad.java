package in.ecomexpress.sathi.utils.signature_pad.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import androidx.annotation.NonNull;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import in.ecomexpress.sathi.R;
import in.ecomexpress.sathi.utils.Logger;
import in.ecomexpress.sathi.utils.signature_pad.utils.Bezier;
import in.ecomexpress.sathi.utils.signature_pad.utils.ControlTimedPoints;
import in.ecomexpress.sathi.utils.signature_pad.utils.SvgBuilder;
import in.ecomexpress.sathi.utils.signature_pad.utils.TimedPoint;
import in.ecomexpress.sathi.utils.signature_pad.view.ViewCompat;
import in.ecomexpress.sathi.utils.signature_pad.view.ViewTreeObserverCompat;

public class SignaturePad extends View {

    private List<TimedPoint> mPoints;
    private boolean mIsEmpty;
    private Boolean mHasEditState;
    private float mLastTouchX;
    private float mLastTouchY;
    private float mLastVelocity;
    private float mLastWidth;
    private final RectF mDirtyRect;
    private Bitmap mBitmapSavedState;
    private final SvgBuilder mSvgBuilder = new SvgBuilder();
    private final List<TimedPoint> mPointsCache = new ArrayList<>();
    private final ControlTimedPoints mControlTimedPointsCached = new ControlTimedPoints();
    private final Bezier mBezierCached = new Bezier();
    private final int mMinWidth;
    private final int mMaxWidth;
    private final float mVelocityFilterWeight;
    private final boolean mClearOnDoubleClick;
    private long mFirstClick;
    private int mCountClick;
    private static final int DOUBLE_CLICK_DELAY_MS = 200;
    private final Paint mPaint = new Paint();
    private Bitmap mSignatureBitmap = null;
    private Canvas mSignatureBitmapCanvas = null;

    public SignaturePad(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.SignaturePad, 0, 0);
        try {
            int DEFAULT_ATTR_PEN_MIN_WIDTH_PX = 3;
            mMinWidth = a.getDimensionPixelSize(R.styleable.SignaturePad_penMinWidth, convertDpToPx(DEFAULT_ATTR_PEN_MIN_WIDTH_PX));
            int DEFAULT_ATTR_PEN_MAX_WIDTH_PX = 7;
            mMaxWidth = a.getDimensionPixelSize(R.styleable.SignaturePad_penMaxWidth, convertDpToPx(DEFAULT_ATTR_PEN_MAX_WIDTH_PX));
            int DEFAULT_ATTR_PEN_COLOR = Color.BLACK;
            mPaint.setColor(a.getColor(R.styleable.SignaturePad_penColor, DEFAULT_ATTR_PEN_COLOR));
            float DEFAULT_ATTR_VELOCITY_FILTER_WEIGHT = 0.9f;
            mVelocityFilterWeight = a.getFloat(R.styleable.SignaturePad_velocityFilterWeight, DEFAULT_ATTR_VELOCITY_FILTER_WEIGHT);
            boolean DEFAULT_ATTR_CLEAR_ON_DOUBLE_CLICK = false;
            mClearOnDoubleClick = a.getBoolean(R.styleable.SignaturePad_clearOnDoubleClick, DEFAULT_ATTR_CLEAR_ON_DOUBLE_CLICK);
        } finally {
            a.recycle();
        }

        //Fixed parameters
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeJoin(Paint.Join.ROUND);

        // Dirty rectangle to update only the changed portion of the view
        mDirtyRect = new RectF();
        clearView();
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle bundle = null;
        try {
            bundle = new Bundle();
            bundle.putParcelable("superState", super.onSaveInstanceState());
            if (this.mHasEditState == null || this.mHasEditState) {
                this.mBitmapSavedState = this.getTransparentSignatureBitmap();
            }
            cacheSignature(this.mBitmapSavedState);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bundle;
    }

    public void saveInstanceState() {
        onSaveInstanceState();
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;
            this.setSignatureBitmap(getCachedSignature());
            state = bundle.getParcelable("superState");
        }
        this.mHasEditState = false;
        super.onRestoreInstanceState(state);
    }

    public void clearView() {
        mSvgBuilder.clear();
        mPoints = new ArrayList<>();
        mLastVelocity = 0;
        mLastWidth = (float) (mMinWidth + mMaxWidth) / 2;
        if (mSignatureBitmap != null) {
            mSignatureBitmap = null;
            ensureSignatureBitmap();
        }
        setIsEmpty(true);
        invalidate();
    }

    public void clear() {
        this.clearView();
        this.mHasEditState = true;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isEnabled()) {
            return false;
        }
        float eventX = event.getX();
        float eventY = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                getParent().requestDisallowInterceptTouchEvent(true);
                mPoints.clear();
                if (isDoubleClick()) break;
                mLastTouchX = eventX;
                mLastTouchY = eventY;
                addPoint(getNewPoint(eventX, eventY));

            case MotionEvent.ACTION_MOVE:
                resetDirtyRect(eventX, eventY);
                addPoint(getNewPoint(eventX, eventY));
                break;

            case MotionEvent.ACTION_UP:
                resetDirtyRect(eventX, eventY);
                addPoint(getNewPoint(eventX, eventY));
                getParent().requestDisallowInterceptTouchEvent(true);
                setIsEmpty(false);
                break;

            default:
                return false;
        }

        invalidate((int) (mDirtyRect.left - mMaxWidth), (int) (mDirtyRect.top - mMaxWidth), (int) (mDirtyRect.right + mMaxWidth), (int) (mDirtyRect.bottom + mMaxWidth));
        return true;
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        if (mSignatureBitmap != null) {
            canvas.drawBitmap(mSignatureBitmap, 0, 0, mPaint);
        }
    }

    public boolean isEmpty() {
        return mIsEmpty;
    }

    public Bitmap getSignatureBitmap() {
        Bitmap originalBitmap = getTransparentSignatureBitmap();
        Bitmap whiteBgBitmap = Bitmap.createBitmap(originalBitmap.getWidth(), originalBitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(whiteBgBitmap);
        canvas.drawColor(Color.WHITE);
        canvas.drawBitmap(originalBitmap, 0, 0, null);
        return whiteBgBitmap;
    }

    public Bitmap getWhiteBackground(Bitmap originalBitmap){
        Bitmap whiteBgBitmap = Bitmap.createBitmap(originalBitmap.getWidth(), originalBitmap.getHeight(), Bitmap.Config.ARGB_8888);
        whiteBgBitmap.eraseColor(Color.WHITE);
        return whiteBgBitmap;
    }

    public void setSignatureBitmap(final Bitmap signature) {
        if (ViewCompat.isLaidOut(this)){
            clearView();
            ensureSignatureBitmap();
            RectF tempSrc = new RectF();
            RectF tempDst = new RectF();
            int dWidth=0;
            int dHeight=0;
            if(signature == null){
                 dWidth = 50;
                 dHeight = 50;
            }
            try{
                dWidth = signature.getWidth();
                dHeight = signature.getHeight();
            } catch(Exception e){
                e.printStackTrace();
            }

            int vWidth = 150;
            int vHeight = 150;
            // Generate the required transform.
            tempSrc.set(0, 0, dWidth, dHeight);
            tempDst.set(0, 0, vWidth, vHeight);
            Matrix drawMatrix = new Matrix();
            drawMatrix.setRectToRect(tempSrc, tempDst, Matrix.ScaleToFit.CENTER);
            Canvas canvas = new Canvas(mSignatureBitmap);
            canvas.drawBitmap(signature, drawMatrix, null);
            setIsEmpty(false);
            invalidate();
        } else {
            getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    ViewTreeObserverCompat.removeOnGlobalLayoutListener(getViewTreeObserver(), this);
                    setSignatureBitmap(signature);
                }
            });
        }
    }

    public Bitmap getTransparentSignatureBitmap() {
        ensureSignatureBitmap();
        return mSignatureBitmap;
    }

    private boolean isDoubleClick() {
        if (mClearOnDoubleClick) {
            if (mFirstClick != 0 && Calendar.getInstance().getTimeInMillis() - mFirstClick > DOUBLE_CLICK_DELAY_MS) {
                mCountClick = 0;
            }
            mCountClick++;
            if (mCountClick == 1) {
                mFirstClick = Calendar.getInstance().getTimeInMillis();
            } else if (mCountClick == 2) {
                long lastClick = Calendar.getInstance().getTimeInMillis();
                if (lastClick - mFirstClick < DOUBLE_CLICK_DELAY_MS) {
                    this.clearView();
                    return true;
                }
            }
        }
        return false;
    }

    private TimedPoint getNewPoint(float x, float y) {
        int mCacheSize = mPointsCache.size();
        TimedPoint timedPoint;
        if (mCacheSize == 0) {
            timedPoint = new TimedPoint();
        } else {
            timedPoint = mPointsCache.remove(mCacheSize - 1);
        }
        return timedPoint.set(x, y);
    }

    private void recyclePoint(TimedPoint point) {
        mPointsCache.add(point);
    }

    private void addPoint(TimedPoint newPoint) {
        mPoints.add(newPoint);
        int pointsCount = mPoints.size();
        if (pointsCount > 3) {
            ControlTimedPoints tmp = calculateCurveControlPoints(mPoints.get(0), mPoints.get(1), mPoints.get(2));
            TimedPoint c2 = tmp.c2;
            recyclePoint(tmp.c1);
            tmp = calculateCurveControlPoints(mPoints.get(1), mPoints.get(2), mPoints.get(3));
            TimedPoint c3 = tmp.c1;
            recyclePoint(tmp.c2);
            Bezier curve = mBezierCached.set(mPoints.get(1), c2, c3, mPoints.get(2));
            TimedPoint startPoint = curve.startPoint;
            TimedPoint endPoint = curve.endPoint;
            float velocity = endPoint.velocityFrom(startPoint);
            velocity = Float.isNaN(velocity) ? 0.0f : velocity;
            velocity = mVelocityFilterWeight * velocity + (1 - mVelocityFilterWeight) * mLastVelocity;

            // The new width is a function of the velocity. Higher velocities
            // correspond to thinner strokes.
            float newWidth = strokeWidth(velocity);

            // The Bezier's width starts out as last curve's final width, and
            // gradually changes to the stroke width just calculated. The new
            // width calculation is based on the velocity between the Bezier's
            // start and end mPoints.
            addBezier(curve, mLastWidth, newWidth);
            mLastVelocity = velocity;
            mLastWidth = newWidth;

            // Remove the first element from the list,
            // so that we always have no more than 4 mPoints in mPoints array.
            recyclePoint(mPoints.remove(0));
            recyclePoint(c2);
            recyclePoint(c3);
        } else if (pointsCount == 1) {
            // To reduce the initial lag make it work with 3 mPoints
            // by duplicating the first point
            TimedPoint firstPoint = mPoints.get(0);
            mPoints.add(getNewPoint(firstPoint.x, firstPoint.y));
        }
        this.mHasEditState = true;
    }

    private void addBezier(Bezier curve, float startWidth, float endWidth) {
        mSvgBuilder.append(curve, (startWidth + endWidth) / 2);
        ensureSignatureBitmap();
        float originalWidth = mPaint.getStrokeWidth();
        float widthDelta = endWidth - startWidth;
        float drawSteps = (float) Math.floor(curve.length());

        for (int i = 0; i < drawSteps; i++) {
            // Calculate the Bezier (x, y) coordinate for this step.
            float t = ((float) i) / drawSteps;
            float tt = t * t;
            float ttt = tt * t;
            float u = 1 - t;
            float uu = u * u;
            float uuu = uu * u;

            float x = uuu * curve.startPoint.x;
            x += 3 * uu * t * curve.control1.x;
            x += 3 * u * tt * curve.control2.x;
            x += ttt * curve.endPoint.x;

            float y = uuu * curve.startPoint.y;
            y += 3 * uu * t * curve.control1.y;
            y += 3 * u * tt * curve.control2.y;
            y += ttt * curve.endPoint.y;

            // Set the incremental stroke width and draw.
            mPaint.setStrokeWidth(startWidth + ttt * widthDelta);
            mSignatureBitmapCanvas.drawPoint(x, y, mPaint);
            expandDirtyRect(x, y);
        }
        mPaint.setStrokeWidth(originalWidth);
    }

    private ControlTimedPoints calculateCurveControlPoints(TimedPoint s1, TimedPoint s2, TimedPoint s3) {
        float dx1 = s1.x - s2.x;
        float dy1 = s1.y - s2.y;
        float dx2 = s2.x - s3.x;
        float dy2 = s2.y - s3.y;

        float m1X = (s1.x + s2.x) / 2.0f;
        float m1Y = (s1.y + s2.y) / 2.0f;
        float m2X = (s2.x + s3.x) / 2.0f;
        float m2Y = (s2.y + s3.y) / 2.0f;

        float l1 = (float) Math.sqrt(dx1 * dx1 + dy1 * dy1);
        float l2 = (float) Math.sqrt(dx2 * dx2 + dy2 * dy2);

        float dxm = (m1X - m2X);
        float dym = (m1Y - m2Y);
        float k = l2 / (l1 + l2);
        if (Float.isNaN(k)) k = 0.0f;
        float cmX = m2X + dxm * k;
        float cmY = m2Y + dym * k;

        float tx = s2.x - cmX;
        float ty = s2.y - cmY;

        return mControlTimedPointsCached.set(getNewPoint(m1X + tx, m1Y + ty), getNewPoint(m2X + tx, m2Y + ty));
    }

    private float strokeWidth(float velocity) {
        return Math.max(mMaxWidth / (velocity + 1), mMinWidth);
    }

    /**
     * Called when replaying history to ensure the dirty region includes all
     * mPoints.
     *
     * @param historicalX the previous x coordinate.
     * @param historicalY the previous y coordinate.
     */
    private void expandDirtyRect(float historicalX, float historicalY) {
        if (historicalX < mDirtyRect.left) {
            mDirtyRect.left = historicalX;
        } else if (historicalX > mDirtyRect.right) {
            mDirtyRect.right = historicalX;
        }
        if (historicalY < mDirtyRect.top) {
            mDirtyRect.top = historicalY;
        } else if (historicalY > mDirtyRect.bottom) {
            mDirtyRect.bottom = historicalY;
        }
    }

    /**
     * Resets the dirty region when the motion event occurs.
     *
     * @param eventX the event x coordinate.
     * @param eventY the event y coordinate.
     */
    private void resetDirtyRect(float eventX, float eventY) {
        // The mLastTouchX and mLastTouchY were set when the ACTION_DOWN motion event occurred.
        mDirtyRect.left = Math.min(mLastTouchX, eventX);
        mDirtyRect.right = Math.max(mLastTouchX, eventX);
        mDirtyRect.top = Math.min(mLastTouchY, eventY);
        mDirtyRect.bottom = Math.max(mLastTouchY, eventY);
    }

    private void setIsEmpty(boolean newValue) {
        mIsEmpty = newValue;
    }

    private void ensureSignatureBitmap() {
        try {
            if (mSignatureBitmap == null) {
                int width = getWidth();
                int height = getHeight();
                if (width > 0 && height > 0) {
                    mSignatureBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                    mSignatureBitmapCanvas = new Canvas(mSignatureBitmap);
                } else {
                    Logger.e("SignaturePad", "Invalid width or height of Bitmap: " + width + "x" + height);
                }
            }
        } catch (Exception e){
            Logger.e("SignaturePad", String.valueOf(e));
        }
    }


    private int convertDpToPx(float dp) {
        return Math.round(getContext().getResources().getDisplayMetrics().density * dp);
    }

    public List<TimedPoint> getPoints() {
        return mPoints;
    }

    private Context context;

    public void setContext(Context context) {
        this.context = context;
    }

    public Bitmap getCachedSignature() {
        if (context != null) {
            // Get the cache directory
            File fileDir = new File(context.getCacheDir().getPath());
            if (!fileDir.exists()) {
                fileDir.mkdirs(); // Create the directory if it doesn't exist
            }

            // Define the file where the signature is cached
            File file = new File(fileDir, "temp_signature.png");

            // Check if the file exists and return the cached signature
            if (file.exists()) {
                return BitmapFactory.decodeFile(file.getPath());
            } else {
                // If the file does not exist, you might want to log it or handle it accordingly
                return null;
            }
        } else {
            return null;
        }
    }

    public void cacheSignature(Bitmap mSignatureBitmap) {
        if (context == null) {
            return;
        }
        if (mSignatureBitmap == null) {
            return;
        }

        int originalWidth = mSignatureBitmap.getWidth();
        int originalHeight = mSignatureBitmap.getHeight();

        if (originalWidth <= 0 || originalHeight <= 0) {
            return;
        }
        FileOutputStream ostream = null;

        try {
            File fileDir = new File(context.getCacheDir().getPath());
            if (!fileDir.exists()) {
                fileDir.mkdirs();
            }

            File file = new File(fileDir, "temp_signature.png");
            if (file.exists()) {
                if (!file.delete()) {
                    return;
                }
            }

            if (!file.createNewFile()) {
                return;
            }

            ostream = new FileOutputStream(file);

            int width = 480;
            int height = (int) (((float) originalHeight / originalWidth) * width);

            if (height <= 0) {
                return;
            }

            Bitmap save = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

            Paint paint = new Paint();
            paint.setColor(Color.WHITE);

            Canvas now = new Canvas(save);
            Rect fullRect = new Rect(0, 0, width, height);
            now.drawRect(fullRect, paint);
            now.drawBitmap(mSignatureBitmap, new Rect(0, 0, mSignatureBitmap.getWidth(), mSignatureBitmap.getHeight()), fullRect, null);

            save.compress(Bitmap.CompressFormat.PNG, 100, ostream);
            ostream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (ostream != null) {
                try {
                    ostream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}