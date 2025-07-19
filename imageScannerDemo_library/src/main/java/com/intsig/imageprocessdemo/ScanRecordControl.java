package com.intsig.imageprocessdemo;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * Use this class to record image scanning procedure and upload crashed image to server
 * @author bryantshen
 *
 */
public class ScanRecordControl {
	private static final String TAG = "ScanRecordControl";
	
	private static final String SET_CRASHED_IMAGE_FOUND = "setcrashedimagefound";
	private static final String SET_CRASHED_IMAGE_PATH = "setcrashedimagepath";
	private static final String SET_TRIMMED_IMAGE_BORDER = "settrimmedimageborder";
	private static final String SET_IMAGE_BRIGHT_DETAIL_CONTRAST = "setimagebrightdetailcontrast";
	private static final String SET_IMAGE_SCAN_STEP = "setimagescanstep";
	private static final String SET_IMAGE_ENHANCE_MODE = "setimageenhancemode";
	private static final String SET_SCAN_FINISH_NORMAL = "setscanfinishnormal";
	
	public static final String DECODE_IMAGE_BYTE_STEP = "decode_image_byte";
	public static final String DETECT_IMAGE_STEP = "detect_image";
	public static final String DETECT_COLOR_IMAGE_MODE_STEP = "detect_color_image_mode_step";
	public static final String DETECT_IMAGE_ANGLE_STEP = "detect_image_angle";
	
	public static final String DECODE_IMAGE_STEP = "decode_image";
	public static final String TRIM_IMAGE_STEP = "trim_image";
	public static final String ENHANCE_IMAGE_STEP = "enhance_image";
	public static final String ROTATE_SCALE_IMAGE_STEP = "rotate_scale_image";
	public static final String ADJUST_IMAGE_STEP = "adjust_image";
	public static final String ENCODE_IMAGE_STEP = "encode_image";
	public static final String INIT_CONTEXT_STEP = "init_context";
	public static final String DESTROY_CONTEXT_STEP = "destroy_context";
	public static final String RELEASE_IMAGE_STEP = "release_image";
	
	public static final String DEWARP_IMAGE_PLANE = "dewarp_image_plane";
	public static final String DECODE_THUMB_STEP = "decode_thumb";
	public static final String TRIM_THUMB_STEP = "trim_thumb";
	public static final String ENHANCE_THUMB_STEP = "enhance_thumb";
	public static final String ROTATE_SCALE_THUMB_STEP = "rotate_scale_thumb";
	public static final String ADJUST_THUMB_STEP = "adjust_thumb";
	public static final String ENCODE_THUMB_STEP = "encode_thumb";
//	public static final String INIT_CONTEXT_STEP = "init_context";
//	public static final String DESTROY_CONTEXT_STEP = "destroy_context";
//	public static final String RELEASE_IMAGE_STEP = "release_image";
	/**
	 * single ScanRecordControl instance 
	 */
	private static ScanRecordControl mScanRecordControl;
	/**
	 * the SharedPreferences instance
	 */
	private final SharedPreferences mPreferences;
	/**
	 * ScanRecordControl constructor
	 * @param context   the Application context
	 */
	private ScanRecordControl(Context context) {
		mPreferences = PreferenceManager.getDefaultSharedPreferences(context);
	}
	/**
	 * get the single instance of ScanRecordControl
	 * @param context  the Application context
	 * @return mScanRecordControl
	 */
	public static ScanRecordControl getInstance(Context context) {
		if (mScanRecordControl == null) {
			mScanRecordControl = new ScanRecordControl(context);
		}
		return mScanRecordControl;
	}
	/**
	 * to judge whether the crashed image 
	 * @return  true if finds crashed image; otherwise false
	 */
	public boolean isCrashedImageFound(){
		boolean result = false;
//		if (mPreferences != null && !AppSwitch.IS_DEVELOPER_MODE) {
//			result = mPreferences.getBoolean(SET_CRASHED_IMAGE_FOUND, false);
//		}
//		Log.d(TAG, "isCrashedImageFound result="+result);
		return result;
	}
	/**
	 * set crashed image found state to be true or false
	 * @param found  boolean true if found otherwise false
	 */
	public void setCrashedImageFound(boolean found){
		if (mPreferences != null) {
			mPreferences.edit().putBoolean(SET_CRASHED_IMAGE_FOUND, found).commit();
		}
		Log.d(TAG, "setCrashedImageFound found="+found);
	}
	/**
	 * set raw image path to SharedPreference only if isCrashedImageFound() = false;
	 * @param path the absolute raw image path
	 */
	public void setImageRawPath(String path){
//		if ((!isScannFinishNormal()) && (FileUtil.isExists(getImageRawPath()))) {
//			setCrashedImageFound(true);
//		}
		if ((mPreferences != null) && (!isCrashedImageFound())) {
			mPreferences.edit().putString(SET_CRASHED_IMAGE_PATH, path).commit();
		}
		Log.d(TAG, "setImageRawPath path="+path+" is crashed "+isCrashedImageFound()+" mPreferences != null="+(mPreferences != null));
	}
	/**
	 * get raw image path which is set by setImageRawPath()
	 * @return  the absolute raw image path
	 */
	public String getImageRawPath(){
		String path = "";
		if(mPreferences != null){
			path = mPreferences.getString(SET_CRASHED_IMAGE_PATH, "");
		}
		Log.d(TAG, "getImageRawPath path="+path+" mPreferences != null="+(mPreferences != null));
		return path;
	}
	/**
	 * set trimmed image border to SharedPreference only if isCrashedImageFound() = false;
	 * @param border  the trimmed image border information in database
	 */
	public void setTrimmedImageBorder(String border){
		if ((mPreferences != null) && (!isCrashedImageFound())) {
			mPreferences.edit().putString(SET_TRIMMED_IMAGE_BORDER, border).commit();
		}
		Log.d(TAG, "setTrimmedImageBorder border="+border);
	} 
	/**
	 * 
	 * @param rawBounds   raw image bounds
	 * @param requestBounds  request trimmed image bounds
	 */
	public void setTrimmedImageBorder(int[] rawBounds, int[] requestBounds){
		StringBuilder stringBuilder = new StringBuilder();
		if (rawBounds != null) {
			for (int i : rawBounds) {
				stringBuilder.append(i).append(",");
			}
		}
		if (requestBounds != null) {
			for (int i : requestBounds) {
				stringBuilder.append(i).append(",");
			}
		}
		setTrimmedImageBorder(stringBuilder.toString());
	}
	/**
	 * get trimmed image border which is set by setTrimmedImageBorder()
	 * @return  trimmed image border
	 */
	public String getTrimmedImageBorder(){
		String border = "";
		if (mPreferences != null) {
			border = mPreferences.getString(SET_TRIMMED_IMAGE_BORDER, "");
		}
		Log.d(TAG, "getTrimmedImageBorder border="+border);
		return border;
	}
	/**
	 * set image bright, detail, contrast indexs to SharedPreference only if isCrashedImageFound() = false;
	 * 
	 * @param bright  the image bright index
	 * @param detail  the image detail index
	 * @param contrast  the image contrast index
	 */
	public void setImageBrightDetailContrastIndexs(int bright, int detail, int contrast){
		if ((mPreferences != null) && (!isCrashedImageFound())) {
			mPreferences.edit().putString(SET_IMAGE_BRIGHT_DETAIL_CONTRAST, bright+","+detail+","+contrast).commit();
		}
		Log.d(TAG, "setImageBrightDetailContrastIndexs index="+bright+","+detail+","+contrast);
	}
	/**
	 * get image bright,detail,contrast string value
	 * 
	 * @return  bright,detail,contrast string value which is set by setImageBrightDetailContrastIndexs()
	 */
	public String getImageBrightDetailContrastIndexs(){
		String index = "";
		if (mPreferences != null) {
			index = mPreferences.getString(SET_IMAGE_BRIGHT_DETAIL_CONTRAST, "");
		}
		Log.d(TAG, "getImageBrightDetailContrastIndexs index="+index);
		return index;
	}
	/**
	 * set image scanned step to SharedPreference only if isCrashedImageFound() = false;
	 * @param step   such as trim, enhance and so on.
	 */
	public void setCurrentScanStep(String step){
		if ((mPreferences != null) && (!isCrashedImageFound())) {
			mPreferences.edit().putString(SET_IMAGE_SCAN_STEP, step).commit();
		}
		Log.d(TAG, "setCurrentScanStep step="+step);
	}
	/**
	 * get image scanned step set by setCurrentScanStep()
	 * @return  image scan step 
	 */
	public String getCurrentScanStep(){
		String step = "";
		if (mPreferences != null) {
			step = mPreferences.getString(SET_IMAGE_SCAN_STEP, "");
		}
		Log.d(TAG, "getCurrentScanStep step="+step);
		return step;
	}
	/**
	 * set image enhance mode to SharedPreference
	 * @param mode  image enhance mode
	 */
	public void setImageEnhanceMode(int mode){
		if (mPreferences != null) {
			mPreferences.edit().putInt(SET_IMAGE_ENHANCE_MODE, mode).commit();
		}
		Log.d(TAG, "setImageEnhanceMode mode="+mode);
	}
	/**
	 * get image enhance mode which is set by setImageEnhanceMode()
	 * 
	 * @return  image enhance mode
	 */
	public int getImageEnhanceMode(){
		int mode = -1;
		if (mPreferences !=  null) {
			mode = mPreferences.getInt(SET_IMAGE_ENHANCE_MODE, -1);
		}
		Log.d(TAG, "getImageEnhanceMode mode="+mode);
		return mode;
	}
	/**
	 * upload crashed image to server
	 * 
	 */
	public void uploadCrashedImage2Server(){
//		String path = getImageRawPath();
//		File file = new File(path);
//		String name = file.getName();
//		name = name.replace(SyncUtil.JPG_SUFFIX, "");
//		String model = android.os.Build.MANUFACTURER + "@" +android.os.Build.MODEL;		
//		try { 
//			String basicParam = "id="+"andrcs14jpg"+System.currentTimeMillis() +"&name="+StringUtil.encodeToUTF8(name)+"&M="+StringUtil.encodeToUTF8(model)+"&PV="+StringUtil.encodeToUTF8(SyncUtil.ClientApp)
//					+"&border="+StringUtil.encodeToUTF8(getTrimmedImageBorder()+"&bdc="+StringUtil.encodeToUTF8(getImageBrightDetailContrastIndexs()
//					+"&scan="+getImageEnhanceMode()+"&crash="+StringUtil.encodeToUTF8(getCurrentScanStep())+"&uid="+StringUtil.encodeToUTF8(mPreferences.getString(Const.KEY_ACCOUNT_UID, "")))+"&os_version="+Build.VERSION.RELEASE+"&device_id="+ScannerApplication.DEVICE_ID);
//			boolean result = SyncUtil.uploadBug2Server(basicParam, path);
//			if (result) {
//				setCrashedImageFound(false);
//				setScannFinishNormal(true);
//			}
//		} catch (Exception e) {
//			Util.LOGE(TAG, "Exception", e);
//		}	
	}
	/**
	 * set scanned image finishing normal
	 * 
	 * @param normal  true or false
	 */
	public void setScannFinishNormal(boolean normal){
		if (mPreferences != null) {
			mPreferences.edit().putBoolean(SET_SCAN_FINISH_NORMAL, normal).commit();
		}
		Log.d(TAG, "setScannFinishNormal normal="+normal);
	}
	/**
	 * judge whether scanned image finishing normal which is set by setScannFinishNormal()
	 * 
	 * @return  true if scan finish normal; otherwise false
	 */
	public boolean isScannFinishNormal(){
		boolean result = true;
		if (mPreferences != null) {
			result = mPreferences.getBoolean(SET_SCAN_FINISH_NORMAL, true);
		}
		Log.d(TAG, "isScannFinishNormal result="+result);
		return result;
	}
}
