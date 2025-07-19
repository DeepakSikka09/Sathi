/**
 * Project Name:IDCardScanCaller
 * File Name:PreviewActivity.java
 * Package Name:com.intsig.idcardscancaller
 * Date:2016年3月15日下午2:14:46
 * Copyright (c) 2016, 上海合合信息 All Rights Reserved.
 */

package preview;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.hardware.Camera.Size;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.intsig.imageprocessdemo.R;
import com.intsig.scanner.ScannerEngine;
import com.intsig.scanner.ScannerSDK;

/**
 * 功能：自定义相机预览识别的基类，不建议任何修改，可以集成该类重写相关方法来处理预览数据和自定义扫描的相机
 * ISCardScanActivity <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason: TODO ADD REASON. <br/>
 * Date: 2018年1月8日 上午12:14:46 <br/>
 *
 * @author feicen
 */
public class ISCardScanActivity extends Activity implements
        Camera.PreviewCallback {

    public static final String EXTRA_KEY_APP_KEY = "EXTRA_KEY_APP_KEY";
    public static final String EXTRA_KEY_IMG_CAMERA_PATH = "EXTRA_KEY_IMG_CAMERA_PATH";

    private Preview mPreview = null;
    private Camera mCamera = null;
    private int numberOfCameras;

    private int defaultCameraId;

    private int mEngineContext;
    private DetectThread mDetectThread = null;

    private ScannerSDK mScannerSDK;
    private RelativeLayout rootView;

    String cameraPathString = "";

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        // Hide the window title.
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // 隐藏当前Activity界面的导航栏, 隐藏后,点击屏幕又会显示出来.
        if (Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) { // lower
            View v = this.getWindow().getDecorView();
            v.setSystemUiVisibility(View.GONE);
        } else if (Build.VERSION.SDK_INT >= 19) {
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);
        }
        // 设置为半透明
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }

        mPreview = new Preview(this);
        RelativeLayout root = new RelativeLayout(this);
        root.setBackgroundColor(Color.TRANSPARENT);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT);

        root.addView(mPreview, lp);

        setContentView(root);
        rootView = root;
        // 初始化预览界面左边按钮组
        initButtonGroup();

        numberOfCameras = Camera.getNumberOfCameras();
        // Find the ID of the default camera
        CameraInfo cameraInfo = new CameraInfo();
        for (int i = 0; i < numberOfCameras; i++) {
            Camera.getCameraInfo(i, cameraInfo);
            if (cameraInfo.facing == CameraInfo.CAMERA_FACING_BACK) {
                defaultCameraId = i;
            }
        }

        mPreview.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (!isRecognize) {
                    if (mCamera != null) {
                        mCamera.autoFocus(null);
                    }
                }
                return false;
            }
        });
        cameraPathString = getIntent()
                .getStringExtra(EXTRA_KEY_IMG_CAMERA_PATH);

        final String appkeyString = getIntent()
                .getStringExtra(EXTRA_KEY_APP_KEY);

        mScannerSDK = new ScannerSDK();
        new AsyncTask<Void, Void, Integer>() {
            @Override
            protected Integer doInBackground(Void... params) {

                int ret = mScannerSDK.initSDK(ISCardScanActivity.this,
                        appkeyString);
                mEngineContext = mScannerSDK.initThreadContext();
                Log.d("mEngineContext", mEngineContext + "mEngineContext");
                return ret;
            }

            @Override
            protected void onPostExecute(Integer result) {
                if (result != 0) {
                    new AlertDialog.Builder(ISCardScanActivity.this,android.R.style.Theme_Material_Light_Dialog)
                            .setMessage("Error：" + result)
                            .setNegativeButton(android.R.string.ok,
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(
                                                DialogInterface dialog,
                                                int which) {
                                            finish();
                                        }
                                    }).setCancelable(false).create().show();
                }
            }
        }.execute();

    }

    private boolean mNeedInitCameraInResume = false;

    @Override
    protected void onResume() {
        super.onResume();
        try {
            mCamera = Camera.open(defaultCameraId);// open the default camera
        } catch (Exception e) {
            e.printStackTrace();
            showFailedDialogAndFinish();
            return;
        }
        // preview是自定义的viewgroup 继承了surfaceview,将相机和surfaceview 通过holder关联
        mPreview.setCamera(mCamera);
        // 设置显示的图片和预览角度一致
        setDisplayOrientation();
        try {
            // 对surfaceview的PreviewCallback的 callback监听，回调onPreviewFrame
            mCamera.setOneShotPreviewCallback(this);
        } catch (Exception e) {
            e.printStackTrace();
            showFailedDialog(e.getMessage());
            return;
        }
        // 当按power键后,再回到程序,surface 不会调用created/changed,所以需要主动初始化相机参数
        if (mNeedInitCameraInResume) {
            mPreview.surfaceCreated(mPreview.mHolder);
            mPreview.surfaceChanged(mPreview.mHolder, 0,
                    mPreview.mSurfaceView.getWidth(),
                    mPreview.mSurfaceView.getHeight());
            mHandler.sendEmptyMessageDelayed(100, 100);
        }
        mNeedInitCameraInResume = true;
    }

    private void showFailedDialog(String msg) {
        new AlertDialog.Builder(this,android.R.style.Theme_Material_Light_Dialog)
                .setMessage(msg)
                .setCancelable(false)
                .setPositiveButton(android.R.string.ok,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                finish();
                            }
                        }).create().show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mCamera != null) {
            Camera camera = mCamera;
            mCamera = null;
            camera.setOneShotPreviewCallback(null);
            mPreview.setCamera(null);
            camera.release();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeMessages(MSG_AUTO_FOCUS);
        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
    }

    private boolean isRecognize = false;// 判断是否在找边，如果进入识别则停止找边

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        Size size = camera.getParameters().getPreviewSize();
        if (mDetectThread == null) {
            mDetectThread = new DetectThread();
            mDetectThread.start();
            // 自动对焦的核心 启动handler 来进行循环对焦
            mHandler.sendEmptyMessageDelayed(MSG_AUTO_FOCUS, 100);
        }
        // 向预览线程队列中 加入预览的 data 分析是否ismatch
        if (!isRecognize) {
            mDetectThread.addDetect(data, size.width, size.height);
        } else {
            resumePreviewCallback();
        }
    }

    private void showFailedDialogAndFinish() {
        new AlertDialog.Builder(this,android.R.style.Theme_Material_Light_Dialog)
                .setMessage(R.string.fail_to_contect_camcard)
                .setCancelable(false)
                .setPositiveButton(android.R.string.ok,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                finish();
                            }
                        }).create().show();
    }

    private void resumePreviewCallback() {
        if (mCamera != null) {
            mCamera.setOneShotPreviewCallback(this);
        }
    }

    /**
     * 功能：将显示的照片和预览的方向一致
     */
    private void setDisplayOrientation() {
        CameraInfo info = new CameraInfo();
        Camera.getCameraInfo(defaultCameraId, info);
        int rotation = getWindowManager().getDefaultDisplay().getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }
        int result = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            result = (info.orientation - degrees + 360) % 360;
        }
        mCamera.setDisplayOrientation(result);
    }

    private static final int MSG_AUTO_FOCUS = 100;
    @SuppressLint("HandlerLeak")
    private final Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == MSG_AUTO_FOCUS) {
                if (!isRecognize) {
                    autoFocus();
                }
            }
        }
    };

    private void autoFocus() {
        if (mCamera != null) {
            try {
                mCamera.autoFocus(focusCallback);
            } catch (Exception e) {
                showFailedDialog(e.getMessage());
                return;
            }
        }
    }

    /**
     * 功能：对焦后的回调，每次返回bool值，延时2秒对焦
     */
    private final AutoFocusCallback focusCallback = new AutoFocusCallback() {

        @Override
        public void onAutoFocus(boolean success, Camera camera) {
//			Log.d("lz", "success==" + success);
            mHandler.sendEmptyMessageDelayed(MSG_AUTO_FOCUS, 2000L);
        }
    };

    private static final boolean isVertical = false;

    /**
     * 功能：根据当前屏幕的方向还有 宽高，还有证件的比例比如身份证高宽比 0.618来算出
     * 预览框的位置和大小，可以更改此处来更改预览框的方向位置还有大小
     */
    public static Map<String, Float> getPositionWithArea(int newWidth,
                                                         int newHeight, float scaleW, float scaleH) {
        float left, top, right, bottom;
        Map<String, Float> map = new HashMap<String, Float>();
        if (isVertical) {// vertical
            float dis = 1 / 20f;
            left = newWidth * dis;
            right = newWidth - left;
            top = 200f * scaleH;
            bottom = top + (newWidth - left - left) * 0.618f;
        } else {
            float dis = 1 / 15f;// 10
            left = newWidth * dis;
            right = newWidth - left;
            top = (newHeight - (newWidth - left - left) / 0.707f) / 2;
            bottom = newHeight - top;

        }
        map.put("left", left);
        map.put("right", right);
        map.put("top", top);
        map.put("bottom", bottom);
        return map;

    }

    /**
     * 功能：将每一次预览的data 存入ArrayBlockingQueue 队列中，然后依次进行ismatch的验证，如果匹配就会就会进行进一步的识别
     * 注意点： 1.其中 控制预览框的位置大小，需要
     */
    int continue_match_time = 0;
    private final PictureCallback pictureCallback = new PictureCallback() {

        @Override
        public void onPictureTaken(final byte[] data, Camera camera) {
            try {

                Log.d("onpicturetaken size:", data.length + "");
                if (mScannerSDK != null) {
                    mScannerSDK.destroyContext(mEngineContext);
                }
                mCamera.stopPreview();
                recognizeCardCallBack(data);
            } catch (Exception e) {
                showFailedDialog(e.getMessage());
                return;
            }
        }
    };

    ToneGenerator tone;

    /**
     * 功能：拍照功能
     */
    public void takepictrueCameraTake() {
        isRecognize = true;// 已经开始识别则不允许继续预览找边操作
        try {
            mCamera.autoFocus(new AutoFocusCallback() {

                @Override
                public void onAutoFocus(boolean arg0, Camera arg1) {
                    try {
                        mCamera.takePicture(new ShutterCallback() {
                            @Override
                            public void onShutter() {
                                if (tone == null) {
                                    // 发出提示用户的声音
                                    tone = new ToneGenerator(AudioManager.STREAM_MUSIC,
                                            ToneGenerator.MAX_VOLUME);
                                }
                                tone.startTone(ToneGenerator.TONE_PROP_BEEP);
                                Handler handler = new Handler(Looper.getMainLooper());
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (tone != null) {

                                            tone.release();
                                            tone = null;
                                        }
                                    }

                                }, 200);
                            }
                        }, null, pictureCallback);
                    } catch (Exception e) {
                       // showFailedDialog(e.getMessage());
                        finish();
                        // return;
                    }
                }

            });
        } catch (Exception e) {
            //showFailedDialog(e.getMessage());
            finish();
            // return;
            // return;
        }
    }

    /**
     * 初始化预览界面左边按钮组，可以选择正反面识别 正面识别 反面识别 注：如果客户想要自定义预览界面，可以参考
     * initButtonGroup中的添加方式
     */
    ImageView take_photo_id;

    private void initButtonGroup() {

        addCameraUi(rootView, isRecognize);
    }

    int[] lastout = null;

    private class DetectThread extends Thread {
        private final ArrayBlockingQueue<byte[]> mPreviewQueue = new ArrayBlockingQueue<byte[]>(
                1);
        private int width;
        private int height;

        @Override
        public void run() {
            try {
                while (true) {
                    byte[] data = mPreviewQueue.take();// block here, if no data
                    // in the queue.
                    if (data.length == 1) {// quit the thread, if we got special
                        // byte array put by stopRun().
                        return;
                    }
//					int[] out = mScannerSDK.detectBorderYuv(mEngineContext,
//							data, ScannerEngine.COLOR_FORMAT_YUV_Y, width,
//							height);
                    float left, top, right, bottom;
                    int newWidth = height;
                    int newHeight = width;

                    Map<String, Float> map = getPositionWithArea(newWidth,
                            newHeight, 1, 1);
                    left = map.get("left");
                    right = map.get("right");
                    top = map.get("top");
                    bottom = map.get("bottom");

                    int[] out = new int[8];
                    int ret = ScannerEngine.detectYuvImage(mEngineContext, data, ScannerEngine.COLOR_FORMAT_YUV_Y,
                            width, height, out, ScannerEngine.DETECT_MODE_CREDIT_CARD);
                    if (out != null) {

                        if (out[0] != 0 && out[2] != 0 && out[4] != 0 && out[6] != 0) {
                            Log.d("DetectCard", "DetectCard >>>>>>>>>>>>> "
                                    + Arrays.toString(out));
                            Log.d("DetectCard", "left:" + left + ",right:" + right + ",top:" + top + ",bottom" + bottom);


                            for (int i = 0; i < 4; i++) {
                                int tmp = out[i * 2];
                                out[i * 2] = height - out[1 + i * 2];
                                out[1 + i * 2] = tmp;
                            }
                            boolean match;
                            match = isMatch((int) left, (int) top, (int) right,
                                    (int) bottom, out);
                            lastout = out;
                            // 实时画出预览 证件的虚拟边框，用来辅助 将证件 与预览框重合 更好识别
                            mPreview.showBorder(out, match);
                            // 当前预览帧的 证件四个点的坐标 和 预览框的证件4个点的坐标 校验，在一定范围内认定校验成功
                            if (match) {
                                // 自动拍摄
                                // takepictrueCameraTake();
                                // return;
                            }
                        }
                    } else {// no find border, continue to preview;
                        mPreview.showBorder(null, false);
                    }

                    // continue to preview;
                    resumePreviewCallback();
                }
            } catch (Exception e) {
                showFailedDialog(e.getMessage());
                return;
            }
        }

        /**
         * 当前预览帧的 证件四个点的坐标 和 预览框的证件4个点的坐标 校验，在一定范围内认定校验成功
         * 注意点：其中120是多次验证的值，没有其他理由校验比较稳定，这个值可以自己尝试改变 注意：判断远近 稍有误差 可以作为适当辅助预
         */
        boolean isMatch(int left, int top, int right, int bottom,
                        int[] qua) {
            int dif = 90;
            int num = 0;

            if (Math.abs(left - qua[6]) < dif && Math.abs(top - qua[7]) < dif) {
                num++;
            }
            if (Math.abs(right - qua[0]) < dif && Math.abs(top - qua[1]) < dif) {
                num++;
            }
            if (Math.abs(right - qua[2]) < dif
                    && Math.abs(bottom - qua[3]) < dif) {
                num++;
            }
            if (Math.abs(left - qua[4]) < dif
                    && Math.abs(bottom - qua[5]) < dif) {
                num++;
            }
            if (num > 2) {
                continue_match_time++;
                return continue_match_time > 1;
            } else {
                continue_match_time = 0;
            }
            return false;
        }

        boolean isMatchTwoArray(int[] lastqua, int[] qua) {
            int dif = 10;
            int num = 0;
            if (lastqua == null || qua == null)
                return false;
            if (Math.abs(lastqua[6] - qua[6]) < dif
                    && Math.abs(lastqua[7] - qua[7]) < dif) {
                num++;
            }
            if (Math.abs(lastqua[0] - qua[0]) < dif
                    && Math.abs(lastqua[1] - qua[1]) < dif) {
                num++;
            }
            if (Math.abs(lastqua[2] - qua[2]) < dif
                    && Math.abs(lastqua[3] - qua[3]) < dif) {
                num++;
            }
            if (Math.abs(lastqua[4] - qua[4]) < dif
                    && Math.abs(lastqua[5] - qua[5]) < dif) {
                num++;
            }
            if (num > 3) {
                continue_match_time++;
                return continue_match_time > 5;
            } else {
                continue_match_time = 0;
            }
            return false;
        }

        void addDetect(byte[] data, int width, int height) {
            if (mPreviewQueue.size() == 1) {
                mPreviewQueue.clear();
            }
            mPreviewQueue.add(data);
            this.width = width;
            this.height = height;
        }
    }

    /**
     * 功能：识别或者拍照之后的方法
     *
     * @param data
     */
    public void recognizeCardCallBack(byte[] data) {
    }

    /**
     * 功能：客户自定义相机界面的方法
     *
     * @param parentView
     * @param boolRecognize
     */
    public void addCameraUi(RelativeLayout parentView, Boolean boolRecognize) {
    }
}
