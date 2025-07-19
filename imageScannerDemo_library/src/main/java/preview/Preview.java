package preview;

import android.content.Context;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.io.IOException;
import java.util.List;

/**
 * Created by fei_cen on 2018/1/8.
 */

public class Preview extends ViewGroup implements SurfaceHolder.Callback {
	private final String TAG = "Preview";
	public SurfaceView mSurfaceView = null;
	public SurfaceHolder mHolder = null;
	private Camera.Size mPreviewSize = null;
	private List<Camera.Size> mSupportedPreviewSizes = null;
	private Camera mCamera = null;
	private DetectView mDetectView = null;
	private TextView mInfoView = null;

	public Preview(Context context) {
		super(context);

		// 自定义viewgrop上添加SurfaceView 然后对应的其他ui DetectView是自定义的预览框

		mSurfaceView = new SurfaceView(context);
		addView(mSurfaceView);

		mInfoView = new TextView(context);
		addView(mInfoView);

		mDetectView = new DetectView(context);
		addView(mDetectView);

		mHolder = mSurfaceView.getHolder();
		mHolder.addCallback(this);
	}

	public void setCamera(Camera camera) {
		mCamera = camera;
		if (mCamera != null) {
			mSupportedPreviewSizes = mCamera.getParameters()
					.getSupportedPreviewSizes();
			requestLayout();
		}
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// We purposely disregard child measurements because act as a
		// wrapper to a SurfaceView that centers the camera preview instead
		// of stretching it.
		final int width = resolveSize(getSuggestedMinimumWidth(),
				widthMeasureSpec);
		final int height = resolveSize(getSuggestedMinimumHeight(),
				heightMeasureSpec);
		setMeasuredDimension(width, height);
		Log.e(TAG, "xxxx onMesaure " + width + " " + height);
		if (mSupportedPreviewSizes != null) {
			int targetHeight = 720;
			if (width > targetHeight && width <= 1080)
				targetHeight = width;
			mPreviewSize = getOptimalPreviewSize(mSupportedPreviewSizes,
					height, width, targetHeight);// 竖屏模式，寬高颠倒

			Log.e(TAG, "xxxx mPreviewSize " + mPreviewSize.width + " "
					+ mPreviewSize.height);

		}
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		if (changed && getChildCount() > 0) {
			final View child = getChildAt(0);

			final int width = r - l;
			final int height = b - t;

			int previewWidth = width;
			int previewHeight = height;
			if (mPreviewSize != null) {
				previewWidth = mPreviewSize.height;
				previewHeight = mPreviewSize.width;
			}

			// Center the child SurfaceView within the parent.
			if (width * previewHeight > height * previewWidth) {
				final int scaledChildWidth = previewWidth * height
						/ previewHeight;
				child.layout((width - scaledChildWidth) / 2, 0,
						(width + scaledChildWidth) / 2, height);
				mDetectView.layout((width - scaledChildWidth) / 2, 0,
						(width + scaledChildWidth) / 2, height);
			} else {
				final int scaledChildHeight = previewHeight * width
						/ previewWidth;
				child.layout(0, (height - scaledChildHeight) / 2, width,
						(height + scaledChildHeight) / 2);
				mDetectView.layout(0, (height - scaledChildHeight) / 2, width,
						(height + scaledChildHeight) / 2);
			}
			getChildAt(1).layout(l, t, r, b);

		}
	}

	public void surfaceCreated(SurfaceHolder holder) {
		// The Surface has been created, acquire the camera and tell it
		// where to draw.
		try {
			if (mCamera != null) {
				mCamera.setPreviewDisplay(holder);
			}
		} catch (IOException exception) {
			Log.e(TAG, "IOException caused by setPreviewDisplay()", exception);
		}
	}

	public void surfaceDestroyed(SurfaceHolder holder) {
		// Surface will be destroyed when we return, so stop the preview.
		if (mCamera != null) {
			mCamera.stopPreview();
		}
	}

	private Camera.Size getOptimalPreviewSize(List<Camera.Size> sizes, int w,
			int h, int targetHeight) {
		final double ASPECT_TOLERANCE = 0.2;
		double targetRatio = (double) w / h;
		if (sizes == null)
			return null;
		Camera.Size optimalSize = null;
		double minDiff = Double.MAX_VALUE;

		// Try to find an size match aspect ratio and size
		for (Camera.Size size : sizes) {

			double ratio = (double) size.width / size.height;
			if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE)
				continue;
			if (Math.abs(size.height - targetHeight) < minDiff) {
				optimalSize = size;
				minDiff = Math.abs(size.height - targetHeight);
			}
		}

		// Cannot find the one match the aspect ratio, ignore the
		// requirement
		if (optimalSize == null) {
			minDiff = Double.MAX_VALUE;
			for (Camera.Size size : sizes) {
				if (Math.abs(size.height - targetHeight) < minDiff) {
					optimalSize = size;
					minDiff = Math.abs(size.height - targetHeight);
				}
			}
		}
		return optimalSize;
	}

	private Camera.Size getOptimalPictureSize(List<Camera.Size> sizes) {

		Camera.Size optimalSize = null;
		// Try to find an size match aspect ratio and size
		for (Camera.Size size : sizes) {
//			Log.e("size:", size.width+","+size.height);
			if (optimalSize == null) {
				optimalSize = size;
			}

			if (size.width * size.height >= 2400 * 1200
					&& size.width * size.height <= 2592 * 1700) {
				// 自行控制拍照分辨率的范围 这里指定了 1600*1200
				optimalSize = size;
				break;
			} else {
				if (optimalSize.width <= size.width
						&& optimalSize.height <= size.height)
					optimalSize = size;
			}

		}
		return optimalSize;
	}

	public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
		if (mCamera != null) {
			// Now that the size is known, set up the camera parameters and
			// begin the preview.
			Camera.Parameters parameters = mCamera.getParameters();
			parameters.setRotation(90);
			parameters.setPreviewSize(mPreviewSize.width, mPreviewSize.height);
			parameters.setPreviewFormat(ImageFormat.NV21);

			parameters.setPictureFormat(ImageFormat.JPEG); // 设置图片格式
			requestLayout();
			mDetectView.setPreviewSize(mPreviewSize.width, mPreviewSize.height);
			List<Camera.Size> mSupportedPictureSizes = mCamera.getParameters()
					.getSupportedPictureSizes();
			Camera.Size picSize = getOptimalPictureSize(mSupportedPictureSizes);
			Log.e(TAG, "picSize:width:" + picSize.width + ",height:"
					+ picSize.height);
			parameters.setPictureSize(picSize.width, picSize.height); // 设置保存的图片尺寸

			parameters.setJpegQuality(100); // 设置照片质量
			mCamera.setParameters(parameters);
			mCamera.startPreview();
		}
	}

	public void showBorder(int[] border, boolean match) {
		mDetectView.showBorder(border, match);
	}

}
