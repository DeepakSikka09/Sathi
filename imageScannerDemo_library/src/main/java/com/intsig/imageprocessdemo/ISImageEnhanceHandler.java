package com.intsig.imageprocessdemo;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.intsig.scanner.ScannerEngine;
import com.intsig.scanner.ScannerSDK;

import java.io.File;
import java.util.Arrays;

/**
 * {@link ImageScannerActivity} 图片切边，增强，保存操作
 * 
 * @author Ben
 * 
 */
public class ISImageEnhanceHandler {

	private static final String TAG = "ISImageEnhanceHandler";

	private int mEngineContext;		// MainUiThread Context;
	/**
	 * ScanRecordControl instance to record scanning processing
	 */
	private final ScanRecordControl mScanRecordControl;

	private String mThumbJpgPath;
	/**
	 * decode from {@link ISImageEnhanceHandler#mThumbJpgPath}
	 */
	private int mThumbStruct;

	private ImageStoreRequest mCurRequest;	// 当前正在处理的请求
	private ImageStoreRequest mRequest;	//待处理的请求
	
	//--------------singleton------------
	private static ISImageEnhanceHandler sInstance;
	ScannerSDK mScannerSDK ;
	/**
	 * 如果当前对象为空，创建新对象
	 * @param context
	 * @return
	 */
	public static ISImageEnhanceHandler newInstance(Context context, Handler handler,ScannerSDK mScannerSDK){
		sInstance = new ISImageEnhanceHandler(context, handler,mScannerSDK);
		
		return sInstance;
	}

	/**
	 * 将当前单例对象置为空
	 */
	public static void releaseInstace(){
		sInstance = null;
	}
	
	private ISImageEnhanceHandler(Context context, Handler handler,ScannerSDK mScannerSDK){
		mEngineContext = 0;
		this.mScannerSDK=mScannerSDK;
		mScanRecordControl = ScanRecordControl.getInstance(context);
	}

	public void setImagePath(String rawPath, String thumbPath){
		mThumbJpgPath = thumbPath;
		mScanRecordControl.setImageRawPath(rawPath);
	}
	/**
	 * 设置engine context
	 * @param enginecontext
	 */
	public void setEngineContext(int enginecontext){
		mEngineContext = enginecontext;
		Log.d(TAG, "setEngineContext = " + enginecontext);
	}
	
	/**
	 * call {@link ScannerEngine#decodeImageS(String)} to decode image
	 * @param imgPath 
	 * @return imageStruct
	 */
	private int decodeImageS(String imgPath){
		Log.d(TAG, "decodeImageS beign: " + imgPath);
		long start = System.currentTimeMillis();
		int imageStruct = ScannerEngine.decodeImageS(imgPath);
		start = System.currentTimeMillis() - start;
		Log.d(TAG, "decodeImageS consume " + start);
		Log.d(TAG, "decodeImageS finished, memory address:" + imageStruct);
		if(!isLegalImageStruct(imageStruct)){
			Log.d(TAG, "decodeImageS fail: " + imageStruct + ", file exist = " + new File(imgPath).exists());
		}
		return imageStruct;
	}

	public static boolean isLegalImageStruct(int imageStruct){
		return imageStruct>0;
}
	/**
	 * 缩略图切边
	 * @param bounds
	 */
	public void trimThumb(int[] bounds){
		long start = System.currentTimeMillis();
		if(!isLegalImageStruct(mThumbStruct)){	// 是否需要重新加载图片
			mThumbStruct = decodeImageS(mThumbJpgPath);
		}
		if(isLegalImageStruct(mThumbStruct)){	// 切边
			mScanRecordControl.setCurrentScanStep(ScanRecordControl.TRIM_THUMB_STEP);
			mScanRecordControl.setTrimmedImageBorder(null, bounds);
			mScanRecordControl.setImageRawPath(mThumbJpgPath);
			try{
				boolean result = mScannerSDK.trimImage(mEngineContext, mThumbStruct, bounds,ImageScannerActivity.TRIM_IMAGE_MAXSIDE);
				Log.d(TAG, "trimThumb result " + result + ", " + Arrays.toString(bounds));
			}catch (RuntimeException e){
				Log.d(TAG, e.getMessage());
			}
		}else{
			Log.d(TAG, "trimThumb decode thumb struct fail " + mThumbStruct);
		}
		start = System.currentTimeMillis() - start;
		Log.d(TAG, "trimThumb consume " + start);
	}

	/**
	 * Request for Image Handle
	 * @author Ben
	 *
	 */
	public class ImageStoreRequest{
		public int[] bounds;
		public int rotation;
		public int enhanceMode;
		public int brightness;
		public int detail;
		public int contrast;

		@Override
		public boolean equals(Object o) {
			boolean res = false;
			if(o instanceof ImageStoreRequest){
				ImageStoreRequest other = (ImageStoreRequest)o;
				if(bounds != null && other.bounds != null){
					for(int i = 0; i < bounds.length; i++){
						if(bounds[i] != other.bounds[i]){
							return res;
						}
					}
				}else if(bounds == null && other.bounds == null){
					
				}else{	// 两个bounds 一个null，另一个不是null
					return res;
				}
				
				res = rotation == other.rotation && enhanceMode == other.enhanceMode &&
						brightness == other.brightness && detail == other.detail &&
						contrast == other.contrast;
			}
			return res;
		}
		
		@Override
		public String toString() {
			return bounds + ", rotation = " + rotation + ", mode = " + enhanceMode + ", " +
					"brightness = " + brightness + ", detail = " + detail + ", contrast = " + contrast;
		}
	}
}
