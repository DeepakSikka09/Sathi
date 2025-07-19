package preview;


import java.util.Map;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.Build;
import android.view.View;

/**
 * 功能：自定义相机上面的自定义预览框
 */

@SuppressLint("DrawAllocation") public class DetectView extends View {
    private Paint paint = null;
    private int[] border = null;
    private boolean match = false;
    private int previewWidth;
    private int previewHeight;

    // 蒙层位置路径
    Path mClipPath = new Path();
    RectF mClipRect = new RectF();
    float mRadius = 12;
    float cornerSize = 80;// 4个角的大小
    float cornerStrokeWidth = 8;

    public void showBorder(int[] border, boolean match) {
        this.border = border;
        this.match = match;
        postInvalidate();
    }

    public DetectView(Context context) {
        super(context);
        paint = new Paint();
        paint.setColor(0xffff0000);
    }

    public void setPreviewSize(int width, int height) {
        this.previewWidth = width;
        this.previewHeight = height;
    }

    // 计算蒙层位置
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void upateClipRegion(float scale, float scaleH) {
        float left, top, right, bottom;
        float density = getResources().getDisplayMetrics().density;
        mRadius = 0;
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB) {
            setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
        cornerStrokeWidth = 4 * density;
        Map<String, Float> map = ISCardScanActivity.getPositionWithArea(getWidth(),
                getHeight(), scale, scaleH);
        left = map.get("left");
        right = map.get("right");
        top = map.get("top");
        bottom = map.get("bottom");

        mClipPath.reset();
        mClipRect.set(left, top, right, bottom);
        mClipPath.addRoundRect(mClipRect, mRadius, mRadius, Path.Direction.CW);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        float scaleW;
        float scaleH;

        scaleW = getWidth() / (float) previewHeight;
        scaleH = getHeight() / (float) previewWidth;
        upateClipRegion(scaleW, scaleH);
    }

    @Override
    public void onDraw(Canvas c) {

        // 绘制预览框的四个角，根据预览是否匹配改变角的颜色

    	 if (match) {// 设置颜色
             int mColorMatch = 0xff005bd7;
             paint.setColor(mColorMatch);
         } else {
             int mColorNormal = 0xff01d2ff;
             paint.setColor(mColorNormal);
         }
        float len = cornerSize;
        float strokeWidth = cornerStrokeWidth;
        paint.setStrokeWidth(strokeWidth);
        // 左上
        c.drawLine(mClipRect.left - strokeWidth / 2, mClipRect.top,
                mClipRect.left + len, mClipRect.top, paint);
        c.drawLine(mClipRect.left, mClipRect.top, mClipRect.left,
                mClipRect.top + len, paint);
        // 右上
        c.drawLine(mClipRect.right - len, mClipRect.top, mClipRect.right
                + strokeWidth / 2, mClipRect.top, paint);
        c.drawLine(mClipRect.right, mClipRect.top, mClipRect.right,
                mClipRect.top + len, paint);
        // 右下
        c.drawLine(mClipRect.right - len, mClipRect.bottom, mClipRect.right
                + strokeWidth / 2, mClipRect.bottom, paint);
        c.drawLine(mClipRect.right, mClipRect.bottom - len,
                mClipRect.right, mClipRect.bottom, paint);
        // 左下
        c.drawLine(mClipRect.left - strokeWidth / 2, mClipRect.bottom,
                mClipRect.left + len, mClipRect.bottom, paint);
        c.drawLine(mClipRect.left, mClipRect.bottom - len, mClipRect.left,
                mClipRect.bottom, paint);

        if (border != null) {
            int width = getWidth();
            int height = getHeight();
            float scale = getWidth() / (float) previewHeight;

            int top_x = (int) (border[0] * scale);
            int top_y = (int) (border[1] * scale);
            int right_x = (int) (border[2] * scale);
            int right_y = (int) (border[3] * scale);
            int bottom_x = (int) (border[4] * scale);
            int bottom_y = (int) (border[5] * scale);
            int left_x = (int) (border[6] * scale);
            int left_y = (int) (border[7] * scale);
            Path path = new Path();
            paint.setStyle(Paint.Style.STROKE);// 空心矩形框
            paint.setStrokeWidth((float) 5.0);
            paint.setColor(Color.rgb(77, 223, 68));
            paint.setAntiAlias(true);
            path.moveTo(top_x, top_y);
            path.lineTo(right_x, right_y);
            path.lineTo(bottom_x, bottom_y);
            path.lineTo(left_x, left_y);
            path.close();
            c.drawPath(path, paint);

            int mColorMatchRect = 0x55ffffff;

            Path top_path = new Path();
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(mColorMatchRect);
            top_path.moveTo(0, 0);
            top_path.lineTo(width, 0);
            top_path.lineTo(right_x, right_y);
            top_path.lineTo(top_x, top_y);
            top_path.close();
            c.drawPath(top_path, paint);

            Path left_path = new Path();
            left_path.moveTo(0, 0);
            left_path.lineTo(top_x, top_y);
            left_path.lineTo(left_x, left_y);
            left_path.lineTo(0, height);
            left_path.close();
            c.drawPath(left_path, paint);

            Path right_path = new Path();
            right_path.moveTo(right_x, right_y);
            right_path.lineTo(width, 0);
            right_path.lineTo(width, height);
            right_path.lineTo(bottom_x, bottom_y);
            right_path.close();
            c.drawPath(right_path, paint);

            Path bottom_path = new Path();
            bottom_path.moveTo(left_x, left_y);
            bottom_path.lineTo(bottom_x, bottom_y);
            bottom_path.lineTo(width, height);
            bottom_path.lineTo(0, height);
            path.close();
            c.drawPath(bottom_path, paint);
            paint.reset();

        }
    }
}
