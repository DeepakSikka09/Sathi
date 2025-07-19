package com.intsig.imageprocessdemo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Logger;

import android.animation.AnimatorInflater;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.intsig.scanner.CommonUtil;
import com.intsig.scanner.ScannerEngine;
import com.intsig.scanner.ScannerEngine.ScannerProcessListener;
import com.intsig.scanner.ScannerSDK;
import com.intsig.view.DocumentUtil;
import com.intsig.view.ImageEditView;
import com.intsig.view.ImageEditView.OnCornorChangeListener;
import com.intsig.view.MagnifierView;
import com.intsig.view.RotateBitmap;

/**
 * 该界面主要展示图片切边与增强 This view is used to show image's crop and enhance process
 */
public class ImageScannerActivity extends Activity implements
        OnItemSelectedListener, OnClickListener {
    private static final String TAG = ImageScannerActivity.class
            .getSimpleName();
    private static final int REQ_CODE_GALLERY_IMPORT = 0;

    /**
     * 用于显示增强结果 This view is used to show image enhance result
     */
    private ImageView mIVEnhance;
    /**
     * 显示切边图 This view is used to show image crop result
     */
    private ImageEditView mIvEditView;
    /**
     * 切边视图层 Crop layout
     */
    private View mTrimView;
    /**
     * 增强视图层 Enhance layout
     */
    private View mEnhanceView;
    /**
     * 添加图片的视图层 The layout used to add image
     */
    private View mAddImageView, take_photo_layout;
    private LinearLayout mBtnNext;
    private String mRootPath;
    private String mOriTrimImagePath;

    private String mOriTrimImagePathResult;

    /**
     * 用于显示增强模式 used to show the enhance model set
     */
    private Spinner mSpinner;
    private static final SimpleDateFormat sPdfTime = new SimpleDateFormat(
            "yyyy-MM-dd_HH-mm-ss");

    private ScannerSDK mScannerSDK;
    /**
     * 压缩图/原图 Compress image/source image
     */
    private float mScale = 1.0f;
    /**
     * 增强处理前的图片 Original enhance image
     */
    private Bitmap mOriginalEnhanceBitmap;
    /**
     * 当前增强处理的图片 Enhancing image
     */
    private Bitmap mEnhanceBitmap;
    /**
     * 记录当前导入图片的路径 Current input image path
     */
    private String mCurrentInputImagePath;
    /**
     * 记录上一次引检测图片切边结果 Last detected border by engine
     */
    private int[] mLastDetectBorder;
    public static int TRIM_IMAGE_MAXSIDE = 1600;
    /**
     * 返回外部授权错误码
     */
    public static String EXTRA_KEY_RESULT_ERROR_CODE = "EXTRA_KEY_RESULT_ERROR_CODE";
    public static String EXTRA_KEY_RESULT_ERROR_MSG = "EXTRA_KEY_RESULT_ERROR_MSG";
    /**
     * 外部传入引擎缩放参数
     */
    public static String EXTRA_TRIM_IMAGE_MAXSIDE = "EXTRA_TRIM_IMAGE_MAXSIDE";

    /**
     * 用于引擎指针，用于辅助检测切边区域是否合法
     */
    private int mEngineContext;

    RelativeLayout mTakePhotoLayout;
    private int mCurOrientation;

    /**
     * 从外面传入参数来控制切边增强的 appkey,颜色，保存之后返回图片
     */
    /**
     * your app key
     */
    private static String APPKEY = "hA8AUCJ4VKeHL98YLLyK9Q2Y";
    /**
     * APPKEY:识别验证的key
     */
    public static String EXTRA_KEY_APP_KEY = "EXTRA_KEY_APP_KEY";
    /**
     * EXTRA_TRIM_NORMAL_COLOR:切边的正常颜色
     */
    public static String EXTRA_TRIM_NORMAL_COLOR = "EXTRA_TRIM_NORMAL_COLOR";

    /**
     * EXTRA_TRIM_ERROR_COLOR:切边的异常颜色
     */
    public static String EXTRA_TRIM_ERROR_COLOR = "EXTRA_TRIM_ERROR_COLOR";

    /**
     * EXTRA_KEY_RESULT_DATA_PATH:切边之后的图片
     */
    public static String EXTRA_KEY_RESULT_DATA_PATH = "EXTRA_KEY_RESULT_DATA_PATH";

    /**
     * EXTRA_IMPORT_IMG_TYPE:导入图片方式 0 通过相机 1通过选图
     */
    public static String EXTRA_IMPORT_IMG_TYPE = "EXTRA_IMPORT_IMG_TYPE";
    private static int importImgType = 0;
    public final static int IMPORT_FROM_CAMERA = 0;
    public final static int IMPORT_FROM_GALLERY = 1;
    public final static int IMPORT_FROM_FILEDIR = 2;// 文件绝对路径
    /**
     * EXTRA_KEY_INPUTFILE_DATA_PATH:需要切边的绝对路径
     */
    public static String EXTRA_KEY_INPUTFILE_DATA_PATH = "EXTRA_KEY_INPUTFILE_DATA_PATH";
    /**
     * EXTRA_KEY_ENHANCE_MODE_INDEX:传入的是增强模式的模式 ，不传则可以选择模式列表，传入值则只会显示指定的模式,0到6
     */
    public static String EXTRA_KEY_ENHANCE_MODE_INDEX = "EXTRA_KEY_ENHANCE_MODE_INDEX";


    public static String EXTRA_KEY_JUDGE_GRAYORCOLOR = "EXTRA_KEY_JUDGE_GRAYORCOLOR";

    String outPutFilePath = null;// 存储文件保存的路径，可以从外面传入
    String inPutFilePath = null;// 输入文件保存的路径，可以从外面传入 type为 IMPORT_FROM_FILEDIR

    int mEnhanceModeIndexExtra = -1;
    /**
     * 正常的切边颜色 Normal crop color
     */
    private static int mNormalColor = 0xff5F95F5;
    /**
     * 异常的切边颜色 Abnormal crop color
     */
    private static int mErrorColor = 0xffff9500;

    /**
     * 当前图片自动模式对应的增强模式
     */
    private int mAutoMode;

    public static final int ENHANCE_AUTO_MODE = 0;// 自动
    public static final int ENHANCE_RAW_MODE = 1;// 原图
    public static final int ENHANCE_LINER_FUTURE_MODE = 2;// 增亮
    public static final int ENHANCE_MAGIC_FUTURE_MODE = 3;// 增强并锐化
    public static final int ENHANCE_GRAY_FUTURE_MODE = 4;// 灰度
    public static final int ENHANCE_BLACKWHITE_FUTURE_MODE = 5;// 黑白

    MagnifierView mMagnifierView;
    RelativeLayout ocr_scan_rel;
    ImageView ocr_scan_line;
    LinearLayout bt_toolbar_line, bt_process_line;
    TextView bt_process_comment_id;
    ProgressBar progress_horizontal;
    View mEnhanceModeBar;
    private boolean boolJudgeGrayOrColor = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        // getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
        // WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.ac_scanner);

        // 获取外部参数
        try {
            Intent intent = getIntent();
            String appkeyString = intent.getStringExtra(EXTRA_KEY_APP_KEY);
            if (!TextUtils.isEmpty(appkeyString)) {
                APPKEY = appkeyString;
            }
            mNormalColor = intent.getIntExtra(EXTRA_TRIM_NORMAL_COLOR, mNormalColor);
            mErrorColor = intent.getIntExtra(EXTRA_TRIM_ERROR_COLOR, mErrorColor);
            importImgType = intent.getIntExtra(EXTRA_IMPORT_IMG_TYPE, IMPORT_FROM_CAMERA);// IMPORT_FROM_CAMERA 0是从相机
            outPutFilePath = intent.getStringExtra(EXTRA_KEY_RESULT_DATA_PATH);// 获取返回图片的路径
            inPutFilePath = intent.getStringExtra(EXTRA_KEY_INPUTFILE_DATA_PATH);// 获取切边的图片的路径
            TRIM_IMAGE_MAXSIDE = intent.getIntExtra(EXTRA_TRIM_IMAGE_MAXSIDE,
                    TRIM_IMAGE_MAXSIDE);
            boolJudgeGrayOrColor = intent.getBooleanExtra(EXTRA_KEY_JUDGE_GRAYORCOLOR, false);
            mEnhanceModeIndexExtra = intent.getIntExtra(
                    EXTRA_KEY_ENHANCE_MODE_INDEX, -1);// 增强模式

            mRootPath = Environment.getExternalStorageDirectory().getAbsolutePath();
            mRootPath += File.separator + "intsig" + File.separator
                    + "demo_imagescanner";
            mOriTrimImagePath = mRootPath + File.separator + "oriTrim.jpg";
            mOriTrimImagePathResult = mRootPath + File.separator
                    + "oriTrimResult.jpg";

            File dir = new File(mRootPath);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            mScannerSDK = new ScannerSDK();

            new Thread(new Runnable() {

                @Override
                public void run() {
                    int code = mScannerSDK.initSDK(ImageScannerActivity.this, APPKEY);
                    mEngineContext = mScannerSDK.initThreadContext();
                    mHandler.sendEmptyMessage(code);
                    Log.d(TAG, "code=" + code);
                }
            }).start();

            mBitmapDetectBound = new int[8];
            mViewTtrimBound = new float[8];
            mBitmapDetectBoundLast = new int[8];
            mCurOrientation = getResources().getConfiguration().orientation;
            mModeNames = getResources().getStringArray(R.array.arrays_enhance);
            initView();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private String[] mModeNames = {"自动", "原图", "增亮", "增强并锐化", "灰度模式", "黑白"};

    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            try {
                if (msg.what != 0) {
                    String loginfoString = CommonUtil.getPkgSigKeyLog(ImageScannerActivity.this, APPKEY);
                    Log.e("Init MSG:", loginfoString);
                    Toast.makeText(ImageScannerActivity.this,
                            "授权失败" + "-->" + msg.what + "\n" + loginfoString, Toast.LENGTH_LONG).show();
                    boolClick = false;
                    Intent data = new Intent();
                    data.putExtra(EXTRA_KEY_RESULT_ERROR_CODE, msg.what);
                    data.putExtra(EXTRA_KEY_RESULT_ERROR_MSG, CommonUtil.commentMsg(msg.what));

                    setResult(RESULT_CANCELED, data);
                    ImageScannerActivity.this.finish();
                } else {
                    if (importImgType == IMPORT_FROM_CAMERA) {
                        startCapture();
                    } else if (importImgType == IMPORT_FROM_GALLERY) {
                        startGalley();
                    } else if (importImgType == IMPORT_FROM_FILEDIR) {
                        mOriTrimImagePath = inPutFilePath;

                        File file = new File(mOriTrimImagePath);
                        if (file.exists()) {
                            // mOriTrimImagePath= loadBitmap(mOriTrimImagePath);

                            loadTrimImageFile(mOriTrimImagePath);
                            // oriBitmap= loadBitmap(mOriTrimImagePath);
                            //
                            // loadTrimImageBitmap(oriBitmap);

                        } else {
                            Toast.makeText(ImageScannerActivity.this, "输入切边图路径不对",
                                    Toast.LENGTH_LONG).show();
                        }

                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            super.handleMessage(msg);
        }

    };
    boolean boolClick = true;
    /**
     * 拍照
     */
    private final int REQUEST_CAPTURE_PIC = 100;

    private void startCapture() {
        // if (mOriTrimImagePath == null) {
        // mTakePicPath = DIR_ICR + "card.jpg";
        // }
        File dstFile = new File(mOriTrimImagePath);
        Uri uri = Uri.fromFile(dstFile);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        startActivityForResult(intent, REQUEST_CAPTURE_PIC);

    }

    private void startGalley() {
        // click 'add' button,and go to gallery
        Log.d(TAG, "go2Gallery");
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, REQ_CODE_GALLERY_IMPORT);
    }

    boolean isFullRegion = false;

    @Override
    public void onClick(View v) {
        int viewId = v.getId();

        if (viewId == R.id.bt_add) {
            if (!boolClick) {
                Toast.makeText(ImageScannerActivity.this, "授权失败" + "-->-3",
                        Toast.LENGTH_LONG).show();
                return;
            }

            startGalley();
        } else if (viewId == R.id.cui_trim_rotate_right) {// 切边---向右旋转

            mRotation = (mRotation + 90);

            bitmapRotate = new RotateBitmap(oriBitmap, mRotation);

            mIvEditView.rotate(bitmapRotate, true);

            mBitmapDetectBound = mIvEditView.getRegion(false);
            for (int i = 0; i < mBitmapDetectBound.length; i++) {

                mViewTtrimBound[i] = mBitmapDetectBound[i];
            }

        } else if (viewId == R.id.cui_trim_totate_left) {// 切边---向左旋转
            mRotation = (mRotation - 90);
            bitmapRotate = new RotateBitmap(oriBitmap, mRotation);

            mIvEditView.rotate(bitmapRotate, true);
            mBitmapDetectBound = mIvEditView.getRegion(false);
            for (int i = 0; i < mBitmapDetectBound.length; i++) {

                mViewTtrimBound[i] = mBitmapDetectBound[i];
            }

        } else if (viewId == R.id.cui_trim_selectall) {// 切边---切边点全选/真实切边

            if (!isFullRegion) {
                mBitmapDetectBoundLast = mIvEditView.getRegion(false);

                mIvEditView.setFullRegion(mScale, null);
                isFullRegion = true;
            } else {
                isFullRegion = false;

                if (mBitmapDetectBoundLast != null) {
                    for (int i = 0; i < mBitmapDetectBoundLast.length; i++) {

                        mViewTtrimBound[i] = mBitmapDetectBoundLast[i];
                    }

                    mIvEditView.setRegion(mViewTtrimBound, mScale);

                }
            }
            mBitmapDetectBound = mIvEditView.getRegion(false);
            for (int i = 0; i < mBitmapDetectBound.length; i++) {

                mViewTtrimBound[i] = mBitmapDetectBound[i];
            }

        } else if (viewId == R.id.cui_enhance_left_line) {// 增强页面---旋转

            mOriginalEnhanceBitmap = reviewPicRotate(mOriginalEnhanceBitmap,
                    270);

            mEnhanceBitmap = reviewPicRotate(mEnhanceBitmap, 270);
            showIvEnhance(mEnhanceBitmap);

        } else if (viewId == R.id.take_photo_id) {// 自定义相机拍照

        } else if (viewId == R.id.close_photo_id) {
            mAddImageView.setVisibility(View.VISIBLE);
            take_photo_layout.setVisibility(View.GONE);
        } else if (viewId == R.id.bt_add_from_camera) {

            startCapture();// 调用系统相机拍照

        } else if (viewId == R.id.bt_back_add_line) {// 切边页面返回
            // enterAddImageLayout();
            setResult(RESULT_CANCELED);
            finish();

        } else if (viewId == R.id.bt_enhance) {
            try {
                // click 'next' button, and start trim
                if (mIvEditView.isCanTrim(mEngineContext)) {
                    // startTtrim();
                    startProcess();
                } else {
                    Toast.makeText(ImageScannerActivity.this,
                            R.string.bound_trim_error, Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else if (viewId == R.id.bt_back_trim_line) {// 返回切边视图页面
            // Bitmap bitmap = BitmapFactory.decodeFile(mOriTrimImagePath);
            try {
                int[] imgBound = getImageSizeBound(mOriTrimImagePath);

                mIvEditView.setRawImageBounds(imgBound);
                mIvEditView.loadDrawBitmap(oriBitmap);

                mIvEditView.setRegion(mViewTtrimBound, mScale);
                // 显示切边区域
                // set the crop are to be shown
                mIvEditView.setRegionVisibility(true);

                bitmapRotate = new RotateBitmap(oriBitmap, mRotation);

                mIvEditView.rotate(bitmapRotate, true);
                // 加载
                // Load the image
                enterTrimLayout();
                mMagnifierView.setVisibility(View.VISIBLE);
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else if (viewId == R.id.cui_enhance_save_line) {
            try {
                mBtnNext.setVisibility(View.GONE);
                mAddImageView.setVisibility(View.GONE);
                mEnhanceView.setVisibility(View.GONE);
                mTrimView.setVisibility(View.VISIBLE);
                mMagnifierView.setVisibility(View.GONE);
                mMagnifierView.recycleAllBitmap();
                mMagnifierView.recycleBGBitmap();

                final String outputPath = saveBitmap2File(mEnhanceBitmap);

                Intent data = new Intent();
                data.putExtra(EXTRA_KEY_RESULT_DATA_PATH, outputPath);

                setResult(RESULT_OK, data);
                finish();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position,
                               long id) {
        Log.d(TAG, "position=" + position);
        if (mOriginalEnhanceBitmap != null) {
            EnhanceTask enhanceTask = new EnhanceTask(getEnhanceMode(position));
            enhanceTask.execute();
        } else {
            Log.d(TAG, "mOriginalEnhanceBitmap=" + mOriginalEnhanceBitmap);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    RotateBitmap bitmapRotate = null;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQ_CODE_GALLERY_IMPORT) {
                // 先隐藏之前结果,
                // hide the previous result
                mIvEditView.setRegionVisibility(false);

                progressExportImage(data);
            }
            if (requestCode == REQUEST_CAPTURE_PIC) {// 拍照回来的

                loadTrimImageFile(mOriTrimImagePath);

            }
        } else {
            setResult(RESULT_CANCELED);
            finish();
        }
    }

    Bitmap oriBitmap = null;

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mEngineContext != 0 && mScannerSDK != null) {
            mScannerSDK.destroyContext(mEngineContext);
        }
        if (mOriginalEnhanceBitmap != null
                && !mOriginalEnhanceBitmap.isRecycled()) {
            mOriginalEnhanceBitmap.recycle();
            mOriginalEnhanceBitmap = null;
        }
        if (mEnhanceBitmap != null && !mEnhanceBitmap.isRecycled()) {
            mEnhanceBitmap.recycle();
            mEnhanceBitmap = null;
        }
        if (mThumb != null && !mThumb.isRecycled()) {
            mThumb.recycle();
            mThumb = null;
        }
        if (mEnhanceSource != null && !mEnhanceSource.isRecycled()) {
            mEnhanceSource.recycle();
            mEnhanceSource = null;
        }
        releaseModeThumb();
        if (mRawImageHandler != null) {


            mRawImageHandler.setEngineContext(0);
            mRawImageHandler = null;
            ISImageEnhanceHandler.releaseInstace();
        }
        if (mFixedThreadPool != null) {
            mFixedThreadPool.shutdown();
            mFixedThreadPool = null;
        }
        if (oriBitmap != null) {
            oriBitmap.recycle();
            oriBitmap = null;
        }
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
        if (mRotateBitmap != null) {
            mRotateBitmap.recycle();
            mRotateBitmap = null;
        }
        if (bitmapRotate != null) {
            bitmapRotate.recycle();
            bitmapRotate = null;
        }

        if (mIvEditView != null) {

            mIvEditView = null;
        }
        if (mMagnifierView != null) {
            mMagnifierView.setVisibility(View.GONE);
            mMagnifierView.recycleAllBitmap();

            mMagnifierView = null;
        }

        if (mIVEnhance != null) {

            recycleImageView(mIVEnhance);

        }
        if (bmmIVEnhance != null && !bmmIVEnhance.isRecycled()) {
            bmmIVEnhance.recycle();
            bmmIVEnhance = null;
        }
        mHandler = null;
        bt_addButton = null;
        bt_add_from_camera = null;

        mBitmapDetectBound = null;
        mBitmapDetectBoundLast = null;
        mViewTtrimBound = null;
        mRawImageHandler = null;
        mScanRecordControl = null;
        mThumb = null;
        mEnhanceSource = null;
        mImageProcessListener = null;
        mIVEnhance = null;
        mTrimView = null;
        mEnhanceView = null;
        mAddImageView = null;
        take_photo_layout = null;
        mBtnNext = null;
        mSpinner = null;

        System.gc();

    }

    TextView bt_addButton, bt_add_from_camera, gray_comment_id, gray_comment_idCrop;

    private void initView() {
        mAddImageView = findViewById(R.id.rl_add_image);
        take_photo_layout = findViewById(R.id.take_photo_layout);

        mTrimView = findViewById(R.id.rl_trim);
        mEnhanceView = findViewById(R.id.ll_enhance);
        mIvEditView = (ImageEditView) findViewById(R.id.iv_trim);
        gray_comment_id = (TextView) findViewById(R.id.gray_comment_id);
        gray_comment_idCrop = (TextView) findViewById(R.id.gray_comment_id2);
        if (boolJudgeGrayOrColor) {
            gray_comment_id.setVisibility(View.VISIBLE);
            gray_comment_idCrop.setVisibility(View.VISIBLE);

        } else {
            gray_comment_id.setVisibility(View.GONE);
            gray_comment_idCrop.setVisibility(View.GONE);

        }


        mIvEditView.setDrapPoint(R.drawable.dragpoint);
        mIvEditView.setRegionVisibility(false);
        mIvEditView.setOnCornorChangeListener(new MyCornorChangeListener());
        mIvEditView.setOffset(getResources().getDimension(
                R.dimen.highlight_point_diameter));

        bt_addButton = (TextView) findViewById(R.id.bt_add);
        bt_addButton.setOnClickListener(this);
        bt_add_from_camera = (TextView) findViewById(R.id.bt_add_from_camera);

        bt_add_from_camera.setOnClickListener(this);
        // findViewById(R.id.bt_back_add).setOnClickListener(this);
        findViewById(R.id.take_photo_id).setOnClickListener(this);
        findViewById(R.id.close_photo_id).setOnClickListener(this);
        findViewById(R.id.bt_back_add_line).setOnClickListener(this);

        mBtnNext = (LinearLayout) findViewById(R.id.bt_enhance);
        mBtnNext.setOnClickListener(this);

        findViewById(R.id.cui_trim_rotate_right).setOnClickListener(this);
        findViewById(R.id.cui_trim_totate_left).setOnClickListener(this);
        findViewById(R.id.cui_trim_selectall).setOnClickListener(this);

        // findViewById(R.id.bt_back_trim).setOnClickListener(this);
        findViewById(R.id.bt_back_trim_line).setOnClickListener(this);

        findViewById(R.id.cui_enhance_save_line).setOnClickListener(this);

        findViewById(R.id.cui_enhance_left_line).setOnClickListener(this);

        bt_toolbar_line = (LinearLayout) findViewById(R.id.bt_toolbar_line);
        bt_process_line = (LinearLayout) findViewById(R.id.bt_process_line);
        progress_horizontal = (ProgressBar) findViewById(R.id.progress_horizontal);
        bt_process_comment_id = (TextView) findViewById(R.id.bt_process_comment_id);

        mIVEnhance = (ImageView) findViewById(R.id.iv_enhance);

        mEnhanceModeBar = findViewById(R.id.iv_enhance_groupbar);

        ocr_scan_rel = (RelativeLayout) findViewById(R.id.ocr_scan_rel);

        ocr_scan_line = (ImageView) findViewById(R.id.ocr_scan_line);
        mMagnifierView = (MagnifierView) findViewById(R.id.magnifier_view);

    }

    /**
     * 进入添加图片视图 Enter the add image layout
     */
    public void enterAddImageLayout() {
        mAddImageView.setVisibility(View.VISIBLE);
        mEnhanceView.setVisibility(View.GONE);
        mTrimView.setVisibility(View.GONE);
    }

    /**
     * 进入切边视图界面 Enter the crop image layout
     */
    public void enterTrimLayout() {
        mAddImageView.setVisibility(View.GONE);
        mEnhanceView.setVisibility(View.GONE);
        mTrimView.setVisibility(View.VISIBLE);
    }

    /**
     * 进入增强视图界面 Enter the enhance image layout
     */
    public void enterEnhanceLayout() {
        if (mSpinner == null) {
            mSpinner = (Spinner) findViewById(R.id.sp_enhance_mode);
            ArrayAdapter<CharSequence> adapter = ArrayAdapter
                    .createFromResource(ImageScannerActivity.this,
                            R.array.arrays_enhance,
                            R.layout.spinner_checked_text);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            mSpinner.setAdapter(adapter);
            mSpinner.setOnItemSelectedListener(ImageScannerActivity.this);
        }

        mAddImageView.setVisibility(View.GONE);
        mEnhanceView.setVisibility(View.VISIBLE);
        mTrimView.setVisibility(View.GONE);

        showIvEnhance(mOriginalEnhanceBitmap);
    }

    public void showIvEnhance(Bitmap morBitmap) {

        Matrix matrix = new Matrix();
        float scaleFloat = 0.5f;
        int w = morBitmap.getWidth();
        int h = morBitmap.getHeight();

        float maxwh = w > h ? w : h;
        scaleFloat = 1200 / maxwh;
        Log.d("test", maxwh + ",scaleFloat:" + scaleFloat);
        scaleFloat = scaleFloat > 1 ? 1 : scaleFloat;

        matrix.setScale(scaleFloat, scaleFloat);
        bmmIVEnhance = Bitmap.createBitmap(morBitmap, 0, 0,
                morBitmap.getWidth(), morBitmap.getHeight(), matrix, true);
        Log.i("bmmIVEnhance",
                "压缩后图片的大小"
                        + (bmmIVEnhance.getAllocationByteCount() / 1024 / 1024)
                        + "M宽度为" + bmmIVEnhance.getWidth() + "高度为"
                        + bmmIVEnhance.getHeight());

        mIVEnhance.setImageBitmap(bmmIVEnhance);

    }

    /**
     * 探测边缘结果,在图片上
     */
    private int[] mBitmapDetectBound;

    /**
     * 探测边缘结果,在图片上---上次的切边点
     */
    private int[] mBitmapDetectBoundLast;
    /**
     * 在视图显示探测的边缘
     */
    private float[] mViewTtrimBound;

    /**
     * detect image border
     */
    class DetectBorderTask extends AsyncTask<Void, Void, Boolean> {
        private long mStartTime;
        /**
         * 待检测图片的绝对路径 it's a absolute path of the source image
         */
        private final String mPath;
        private float[] mOrginBounds = null;

        private int grayInt = -1;

        public DetectBorderTask(String path) {
            mPath = path;
        }

        @Override
        protected void onPreExecute() {
            showProgressDialog();

        }

        @Override
        protected Boolean doInBackground(Void... params) {
            boolean succed = false;
            long tempTime = 0;
            mStartTime = System.currentTimeMillis();
            int threadContext = mScannerSDK.initThreadContext();
            System.out
                    .println("DetectBorderTask, initThreadContext, cost time:"
                            + (System.currentTimeMillis() - mStartTime));
            tempTime = System.currentTimeMillis();
            int imageStruct = mScannerSDK.decodeImageS(mPath);
            System.out.println("DetectBorderTask, decodeImageS, cost time:"
                    + (System.currentTimeMillis() - tempTime));

            mLastDetectBorder = null;
            int[] imgBound = getImageSizeBound(mPath);
            if (imageStruct != 0) {
                // 检测边缘
                // Detect the border of the image
                tempTime = System.currentTimeMillis();
                mLastDetectBorder = mScannerSDK.detectBorder(threadContext,
                        imageStruct);

                if (boolJudgeGrayOrColor)
                    grayInt = mScannerSDK.grayOrColorJudge(imageStruct);


                System.out.println("DetectBorderTask, detectBorder, cost time:"
                        + (System.currentTimeMillis() - tempTime));
                Log.d(TAG,
                        "detectAndTrimImageBorder, borders="
                                + Arrays.toString(mLastDetectBorder));
                tempTime = System.currentTimeMillis();
                mOrginBounds = getScanBoundF(imgBound, mLastDetectBorder);

                float[] bound = getScanBoundF(imgBound, mLastDetectBorder);
                if (bound != null) {
                    for (int i = 0; i < bound.length; i++) {
                        mBitmapDetectBound[i] = (int) (bound[i]);
                        mViewTtrimBound[i] = mBitmapDetectBound[i];
                    }

                }

                System.out.println("DetectBorderTask, fix border, cost time:"
                        + (System.currentTimeMillis() - tempTime));
                tempTime = System.currentTimeMillis();
                mScannerSDK.releaseImage(imageStruct);
                System.out.println("DetectBorderTask, releaseImage, cost time:"
                        + (System.currentTimeMillis() - tempTime));
                succed = true;
            } else {
                mOrginBounds = getScanBoundF(imgBound, null);
            }
            mScannerSDK.destroyContext(threadContext);
            System.out.println("DetectBorderTask, cost time:"
                    + (System.currentTimeMillis() - mStartTime));
            return succed;
        }

        protected void onPostExecute(Boolean result) {
            dismissProgressDialog();
            Log.d(TAG, "result=" + result);
            if (result) {

                // 标识切边区域
                // remark the cropped area
                if (mScale < 0.001 && mScale > -0.001) {
                    mScale = 1.0f;
                }
                if (mOrginBounds != null) {
                    // 加载在原图的切边区载
                    // mScale=1f;
                    // load the source image crop area
                    mIvEditView.setRegion(mOrginBounds, mScale);
                    // 显示切边区域
                    // set the crop are to be shown
                    mIvEditView.setRegionVisibility(true);
                    // 设置显示切边区域，但不显示锚点和不能手动更改区域
                    // mIvEditView.showDrawPoints(false);
                    // mIvEditView.enableMovePoints(false);
                }
                mBtnNext.setVisibility(View.VISIBLE);

                if (grayInt == 0) {
                    gray_comment_id.setText("当前图片是彩色");

                } else if (grayInt == 1) {
                    gray_comment_id.setText("当前图片是黑白");
                }


                // getTrimRegions();
            } else {
                Log.d(TAG, "result=" + result);
            }
        }

    }

    /**
     * 异步处理切边 deal with the cropping asynchronous
     */
    class TrimTask extends AsyncTask<Void, Void, Boolean> {
        private long mStartTime;
        /**
         * 待切边图片的绝对路径 The absolute path of the image going to be cropped
         */
        private final String mPath;

        public TrimTask(String path) {
            mPath = path;
        }

        TranslateAnimation mTranslateAnimation;
        private int grayInt = -1;

        @Override
        protected void onPreExecute() {
            // showProgressDialog();
            // popupwindowShowProgress();
            // 当内存不足时则不显示--Fox

            mMagnifierView.setVisibility(View.GONE);

            ocr_scan_rel.setVisibility(View.VISIBLE);
            // if (isVertical) {
            mTranslateAnimation = new TranslateAnimation(
                    TranslateAnimation.ABSOLUTE, 0f,
                    TranslateAnimation.ABSOLUTE, 0f,
                    TranslateAnimation.RELATIVE_TO_PARENT, 0f,
                    TranslateAnimation.RELATIVE_TO_PARENT, 1f);
            // } else {
            // mTranslateAnimation = new TranslateAnimation(
            // TranslateAnimation.RELATIVE_TO_PARENT, 1f,
            // TranslateAnimation.RELATIVE_TO_PARENT, 0f,
            // TranslateAnimation.ABSOLUTE, 0f,
            // TranslateAnimation.ABSOLUTE, 0f);
            // }
            mTranslateAnimation.setDuration(1000);
            // mTranslateAnimation.setRepeatCount(1);
            // mTranslateAnimation.setRepeatMode(Animation.RESTART);
            mTranslateAnimation.setInterpolator(new LinearInterpolator());
            ocr_scan_line.setAnimation(mTranslateAnimation);
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                Log.d(TAG, "TrimTask, doInBackground");
                long tempTime = 0;
                boolean succeed = false;
                mStartTime = System.currentTimeMillis();
                // 用于多线程执行图像处理 该函数返回一块内存指针，在该线程上执行图像操作时，作为第一个参数传入
                int threadContext = mScannerSDK.initThreadContext();
                System.out.println("TrimTask, initThreadContext, cost time:"
                        + (System.currentTimeMillis() - mStartTime));

                tempTime = System.currentTimeMillis();
                int imageStruct = mScannerSDK.decodeImageS(mPath);
                System.out.println("TrimTask, decodeImageS, cost time:"
                        + (System.currentTimeMillis() - tempTime));

                if (imageStruct != 0) {
                    // 检测边缘，并切边处理
                    // Detect the border and crop it
                    tempTime = System.currentTimeMillis();
                    // 从控件中，获取对应图片上的边缘
                    // Get the edge of the image on the control
                    // int[] bound = mIvEditView.getRegion(false);
                    int[] bound = mBitmapDetectBound;
                    // for (int i = 0; i < bound.length; i++) {
                    // bound[i] = (int) (bound[i] );
                    // }

                    Log.d(TAG, "bound=" + Arrays.toString(bound));
                    mScannerSDK.trimImage(threadContext, imageStruct, bound,
                            TRIM_IMAGE_MAXSIDE);
                    System.out.println("TrimTask, trimImage, cost time:"
                            + (System.currentTimeMillis() - tempTime));

                    tempTime = System.currentTimeMillis();
                    mScannerSDK.saveImage(imageStruct, mOriTrimImagePathResult, 80);
                    System.out.println("TrimTask, saveImage, cost time:"
                            + (System.currentTimeMillis() - tempTime));

                    tempTime = System.currentTimeMillis();
                    mScannerSDK.releaseImage(imageStruct);
                    System.out.println("TrimTask, releaseImage, cost time:"
                            + (System.currentTimeMillis() - tempTime));


                    //尝试对切边图片进行黑白判断
                    if (boolJudgeGrayOrColor) {
                        int imageStructGray = mScannerSDK.decodeImageS(mOriTrimImagePathResult);

                        grayInt = mScannerSDK.grayOrColorJudge(imageStructGray);
                        System.out.println("mScannerSDK.grayOrColorJudge grayInt:"
                                + grayInt);
                        mScannerSDK.releaseImage(imageStructGray);


                    }

                    File file = new File(mOriTrimImagePathResult);
                    if (file.exists()) {
                        tempTime = System.currentTimeMillis();
                        Bitmap trimBitmap = null;
                        BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inJustDecodeBounds = true;
                        BitmapFactory.decodeFile(mOriTrimImagePathResult, options);
                        int bitmapWidth = options.outWidth;
                        int bitmapHeight = options.outHeight;
                        Log.d(TAG, "bitmapWidth=" + bitmapWidth + " bitmapHeight="
                                + bitmapHeight);
                        if (bitmapWidth > 0 && bitmapHeight > 0) {
                            int viewWidth = mIvEditView.getWidth();
                            int viewHeight = mIvEditView.getHeight();
                            float scaleX = 1.0f * viewWidth / bitmapWidth;
                            float scaleY = 1.0f * viewHeight / bitmapHeight;

                            Log.i("testBitmap", "viewWidth:" + viewWidth
                                    + "viewHeight" + viewHeight + "bitmapWidth"
                                    + bitmapWidth + "bitmapHeight：" + bitmapHeight);
                            float scale = scaleX > scaleY ? scaleY : scaleX;
                            if (scale >= 0.5 && scale < 1)
                                scale = 0.5f;

                            int inSampleSize = (int) (1 / scale);
                            if (inSampleSize == 0) {
                                inSampleSize = 1;
                            }
                            options.inJustDecodeBounds = false;

                            if (bitmapHeight >= 2400 || bitmapWidth >= 2400) {
                                options.inSampleSize = inSampleSize;
                                options.inPreferredConfig = Config.RGB_565;
                            } else if (bitmapHeight >= 1600 || bitmapWidth >= 1600) {
                                options.inPreferredConfig = Config.RGB_565;
                            }
                            trimBitmap = BitmapFactory.decodeFile(
                                    mOriTrimImagePathResult, options);

                            Log.i("testBitmap",
                                    "压缩后图片的大小"
                                            + (trimBitmap.getAllocationByteCount() / 1024 / 1024)
                                            + "M宽度为" + trimBitmap.getWidth()
                                            + "高度为" + trimBitmap.getHeight()
                                            + "inSampleSize：" + inSampleSize);
                        }
                        // 回收之前生成增强图片
                        if (mOriginalEnhanceBitmap != null
                                && !mOriginalEnhanceBitmap.isRecycled()) {
                            mOriginalEnhanceBitmap.recycle();
                        }
                        trimBitmap = reviewPicRotate(trimBitmap, mRotation);

                        mOriginalEnhanceBitmap = trimBitmap;
                        try {
                            // file.delete();
                        } catch (Exception e) {
                            Log.e(TAG, "Exception", e);
                        }
                        System.out
                                .println("TrimTask, BitmapFactory.decodeFile, cost time:"
                                        + (System.currentTimeMillis() - tempTime));
                    } else {
                        Log.d(TAG, "file is not exist");
                    }
                    succeed = true;
                }
                mScannerSDK.destroyContext(threadContext);
                System.out.println("TrimTask, cost time:"
                        + (System.currentTimeMillis() - mStartTime));
                return succeed;
            } catch (Exception e) {
                e.printStackTrace();
            }

            return false;
        }

        protected void onPostExecute(Boolean result) {
            if (result) {
                dismissProgressDialog();
                Log.d(TAG, "result=" + result);
                if (grayInt == 0) {
                    gray_comment_idCrop.setText("当前切边图片是彩色");

                } else if (grayInt == 1) {
                    gray_comment_idCrop.setText("当前切边图片是黑白");
                }
                if (result) {

                    ocr_scan_rel.setVisibility(View.GONE);
                    mTranslateAnimation.cancel();
                    mTranslateAnimation = null;
                    ocr_scan_line.clearAnimation();
                    ocr_scan_line.setVisibility(View.GONE);
                    enterEnhanceLayout();
                    mEnhanceBitmap = mOriginalEnhanceBitmap.copy(
                            mOriginalEnhanceBitmap.getConfig(), true);

                    /**************************** 默认指定的增强模式 ********************************/

                    if (mEnhanceModeIndexExtra != -1) {
                        if (mOriginalEnhanceBitmap != null) {
                            EnhanceTask enhanceTask = new EnhanceTask(
                                    getEnhanceMode(mEnhanceModeIndexExtra));
                            enhanceTask.execute();
                        } else {
                            Log.d(TAG, "mOriginalEnhanceBitmap="
                                    + mOriginalEnhanceBitmap);
                        }
                    } else {
                        mEnhanceModeIndex = 0;
                        initEnhanceBar();

                    }
                    // startProcess();
                } else {
                    Log.d(TAG, "result=" + result);
                }
            } else {
                Toast.makeText(ImageScannerActivity.this, "Image corrupted please try again", Toast.LENGTH_SHORT).show();
            }

        }
    }

    public Bitmap reviewPicRotate(Bitmap bitmap, int degree) {
        if (degree != 0) {
            Matrix m = new Matrix();
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();
            m.setRotate(degree); // 旋转angle度
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, m, true);// 从新生成图片
        }
        return bitmap;
    }

    /**
     * 开始对图像进行切边 Start to crop the image
     */
    private void startTtrim() {
        if (TextUtils.isEmpty(mOriTrimImagePath)) {
            return;
        }

        if (!TextUtils.isEmpty(mCurrentInputImagePath)) {
            TrimTask trimTask = new TrimTask(mCurrentInputImagePath);
            trimTask.execute();
        }
    }

    /**
     * 开始切边线程
     */
    private void startProcess() {
        getTrimRegions();
        TrimAnimTask task = new TrimAnimTask();
        task.execute();
    }

    private int mEnhanceMode = ScannerSDK.ENHANCE_MODE_AUTO;

    /**
     * 图像增强处理 Enhance the image asynchronous
     */
    class EnhanceTask extends AsyncTask<Void, Void, Void> {

        public EnhanceTask(int mode) {
            mEnhanceMode = mode;
        }

        @Override
        protected void onPreExecute() {
            showProgressDialog();
        }

        @Override
        protected Void doInBackground(Void... params) {
            int threadContext = mScannerSDK.initThreadContext();
            // 回收之前生成增强图片
            // Recycle the previous enhanced image
            if (mEnhanceBitmap != null && !mEnhanceBitmap.isRecycled()) {
                mEnhanceBitmap.recycle();
            }
            // 先拷贝一份
            // Copy a piece
            mEnhanceBitmap = mOriginalEnhanceBitmap.copy(
                    mOriginalEnhanceBitmap.getConfig(), true);
            Log.d(TAG, "mEnhanceBitmap");
            mScannerSDK.enhanceImage(threadContext, mEnhanceBitmap,
                    mEnhanceMode);
            Log.e(TAG, "CS enhanceImage mEnhanceMode" + mEnhanceMode);

            mScannerSDK.destroyContext(threadContext);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            dismissProgressDialog();

            // 增强显示的图片

            // showIvEnhance(mEnhanceBitmap);
            Log.d(TAG, "finish, EnhanceTask");
        }
    }

    Bitmap bmmIVEnhance;

    /**
     * 获取图片大小
     *
     * @param pathName
     * @param pathName
     * @return image size
     */
    private static int[] getImageSizeBound(String pathName) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        options.inSampleSize = 1;
        BitmapFactory.decodeFile(pathName, options);
        int[] wh = null;
        if (options.mCancel || options.outWidth == -1
                || options.outHeight == -1) {
            Log.d(TAG, "getImageBound error " + pathName);
        } else {
            wh = new int[2];
            wh[0] = options.outWidth;
            wh[1] = options.outHeight;
        }
        return wh;
    }

    /**
     * 对切边区域进行调整，保证切边区域在图片内部
     *
     * @param size    原图宽高
     * @param borders 探测边界
     * @param size    : source image wide and high
     * @param borders : it's an int array of the 4 corner points
     * @return the new crop image area after the adjustment
     */
    private static float[] getScanBoundF(int[] size, int[] borders) {
        float[] bound = null;
        if (size != null) {
            if ((borders == null)) {
                Log.d(TAG, "did not found bound");
                bound = new float[]{0, 0, size[0], 0, size[0], size[1], 0,
                        size[1]};
            } else {
                bound = new float[8];
                for (int j = 0; j < bound.length; j++) {
                    bound[j] = borders[j];
                }
                for (int i = 0; i < 4; i++) {
                    if (bound[i * 2] < 0)// x
                        bound[i * 2] = 0;
                    if (bound[i * 2 + 1] < 0)// y
                        bound[i * 2 + 1] = 0;
                    if (bound[i * 2] > size[0])// x
                        bound[i * 2] = size[0];
                    if (bound[i * 2 + 1] > size[1])// y
                        bound[i * 2 + 1] = size[1];
                }
            }
        }
        return bound;
    }

    /**
     * get enhance mode
     *
     * @param which <pre>
     *                                                                                                                                                                          0:自动
     *                                                                                                                                                                          1：原图
     *                                                                                                                                                                          2：增强
     *                                                                                                                                                                          3：增强并锐化
     *                                                                                                                                                                          4：灰度模式
     *                                                                                                                                                                          5：黑白模式
     *                                                                                                                                                                          其它：自动
     *                                                                                                                                                             </pre>
     *              <p>
     *              get enhance mode
     *              <p>
     *              <pre>
     *                                                                                                                                                                          0: Auto
     *                                                                                                                                                                          1：No enhance
     *                                                                                                                                                                          2：Enhance
     *                                                                                                                                                                          3：Enhance and Magic
     *                                                                                                                                                                          4：Gray
     *                                                                                                                                                                          5：Black-and-White
     *                                                                                                                                                             </pre>
     */
    public int getEnhanceMode(int which) {
        int mode = ScannerSDK.ENHANCE_MODE_AUTO;
        switch (which) {
            case 1:
                // 自动
                // Auto
                mode = ScannerSDK.ENHANCE_MODE_AUTO;
                break;
            case 0:
                // 原图
                // No Enhance
                mode = ScannerSDK.ENHANCE_MODE_NO_ENHANCE;
                break;
            case 2:
                // 增强
                // Enhance
                mode = ScannerSDK.ENHANCE_MODE_LINEAR;
                break;
            case 3:
                // 增强并锐化
                // Enhance and Magic
                mode = ScannerSDK.ENHANCE_MODE_MAGIC;
                break;
            case 4:
                // 灰度模式
                // Gray
                mode = ScannerSDK.ENHANCE_MODE_GRAY;
                break;
            case 5:
                // 黑白模式
                // Black-and-White
                mode = ScannerSDK.ENHANCE_MODE_BLACK_WHITE;
                break;
            default:
                mode = ScannerSDK.ENHANCE_MODE_AUTO;
        }
        return mode;
    }

    private ProgressDialog mProgressDialog;

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        super.onBackPressed();

    }

    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(ImageScannerActivity.this,android.R.style.Theme_Material_Light_Dialog);
            mProgressDialog.setProgress(ProgressDialog.STYLE_SPINNER);
            mProgressDialog.setMessage(getResources().getString(
                    R.string.a_msg_working));
        }
        mProgressDialog.show();
    }

    public void dismissProgressDialog() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
    }

    /**
     * 保存图片
     *
     * @param src Save the result image--cropped and enhanced
     */
    public String saveBitmap2File(Bitmap src) {

        if (TextUtils.isEmpty(outPutFilePath)) {
            outPutFilePath = mRootPath + File.separator
                    + sPdfTime.format(new Date()) + ".jpg";
        } else {
            Utils.createFile(outPutFilePath);
        }
        FileOutputStream outPutStream = null;
        try {
            outPutStream = new FileOutputStream(outPutFilePath);
            src.compress(Bitmap.CompressFormat.JPEG, 90, outPutStream);
        } catch (FileNotFoundException e) {
            Log.e(TAG, "FileNotFoundException", e);

        } finally {
            if (outPutStream != null) {
                try {
                    outPutStream.close();
                } catch (IOException e) {
                    Log.e(TAG, "IOException", e);
                }
            }
            if (src != null && !src.isRecycled()) {
                src.recycle();
                src = null;
            }
        }
        Log.d(TAG, "saveBitmap2File, outPutFilePath=" + outPutFilePath);
        return outPutFilePath;
    }

    /**
     * 判断图片是否合法，png或jpg为合法图片
     *
     * @param path 图片路径
     * @return true image is valid
     * <p>
     * Whether the image is valid or not, only support .png and .jpg
     */
    public boolean isValidImage(String path) {
        return !TextUtils.isEmpty(path)
                && (path.endsWith("png") || path.endsWith("jpg"));
    }

    /**
     * 处理导入的图片
     *
     * @param data Process the input image
     */
    private void progressExportImage(Intent data) {
        if (data != null) {
            Uri u = data.getData();
            Log.d(TAG, "data.getData()=" + u);
            if (u != null) {
                String path = DocumentUtil.getInstance().getPath(this, u);
                if (isValidImage(path)) {
                    loadTrimImageFile(path);

                    // mOriTrimImagePath=path;
                    // oriBitmap= loadBitmap(mOriTrimImagePath);
                    //
                    // loadTrimImageBitmap(oriBitmap);
                } else {
                    Toast.makeText(this, R.string.a_msg_illegal,
                            Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            Log.d(TAG, "data==null");
        }
    }

    private void loadTrimImageFile(final String imageFilePath) {
        if (TextUtils.isEmpty(imageFilePath)) {
            Log.d(TAG, "imageFilePath is empty");
            return;
        }
        File file = new File(imageFilePath);
        if (!file.exists()) {
            Log.d(TAG, "imageFilePath is not exist");
            return;
        }

        Log.d(TAG, "loadTrimImageFile, imageFilePath=" + imageFilePath);
        enterTrimLayout();
        mIvEditView.post(new Runnable() {

            @Override
            public void run() {
                // 为了防止图片过大出现内存溢出，使用压缩后的图进行显示
                // to prevent out of memory error when the image is too large,

                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(imageFilePath, options);
                int bitmapWidth = options.outWidth;
                int bitmapHeight = options.outHeight;
                Log.d(TAG, "bitmapWidth=" + bitmapWidth + " bitmapHeight="
                        + bitmapHeight);
                if (bitmapWidth > 0 && bitmapHeight > 0) {
                    int viewWidth = mIvEditView.getWidth() / 2;
                    int viewHeight = mIvEditView.getHeight() / 2;

                    if (viewHeight == 0 || viewWidth == 0) {
                        viewWidth = 1080;
                        viewHeight = 1920;
                    }
                    if (viewWidth > 0 && viewHeight > 0) {
                        float scaleX = 1.0f * viewWidth / bitmapWidth;
                        float scaleY = 1.0f * viewHeight / bitmapHeight;
                        float scale = scaleX > scaleY ? scaleY : scaleX;
                        int inSampleSize = (int) (1 / scale);
                        if (inSampleSize == 0) {
                            inSampleSize = 1;
                        }
                        options.inSampleSize = inSampleSize;
                        // inSampleSizeAll = inSampleSize;

                        Log.d("inSampleSize:", inSampleSize + "");
                        options.inJustDecodeBounds = false;
                        options.inPreferredConfig = Config.RGB_565;
                        Bitmap testBitmap = BitmapFactory.decodeFile(
                                imageFilePath, options);
                        //  testBitmap = applyWaterMark(testBitmap, "33003");
                        mScale = 1.0f * testBitmap.getWidth() / bitmapWidth;
                        Log.i("testBitmap",
                                "压缩后图片的大小"
                                        + (testBitmap.getAllocationByteCount() / 1024 / 1024)
                                        + "M宽度为" + testBitmap.getWidth()
                                        + "高度为" + testBitmap.getHeight()
                                        + "mScale：" + mScale);
                        int[] imgBound = new int[]{bitmapWidth, bitmapHeight};// 原图的宽高
                        // 设置加载原图的宽高
                        // Set the source image wide and high
                        mIvEditView.setRawImageBounds(imgBound);// 设置原图的宽高
                        // 加载
                        // Load the image
                        mIvEditView.loadDrawBitmap(testBitmap);// 设置显示图片的大小

                        RectF imgbound = new RectF(0, 0, testBitmap.getWidth(),
                                testBitmap.getHeight());

                        oriBitmap = testBitmap;
                        mIvEditView.getImageMatrix().mapRect(imgbound);
                        setMagnifierView(testBitmap, imgbound);

                        mCurrentInputImagePath = imageFilePath;
                        DetectBorderTask detectTask = new DetectBorderTask(imageFilePath);
                        detectTask.execute();
                    }

                } else {
                    Log.d(TAG, "bitmapWidth=" + bitmapWidth + " bitmapHeight="
                            + bitmapHeight);
                }

            }
        });
    }

    private Bitmap applyWaterMark(Bitmap bmp, String employeeCode) {
       /* if (waterMark == null) {
            Logger.e(TAG, "No Water Mark");
            return bmp;
        }*/
        int w = bmp.getWidth();
        int h = bmp.getHeight();
        Bitmap result = Bitmap.createBitmap(w, h, bmp.getConfig());

        //Write image on canvas only once.
        Canvas canvas = new Canvas(result);
        canvas.drawBitmap(bmp, 0, 0, null);

        //Now write all items on this canvas

        int subInterval = 40;
        int startY = 220;

        Paint paint = new Paint();
        paint.setTextSize(18);
        paint.setColor(Color.YELLOW);
        paint.setAntiAlias(true);
        paint.setUnderlineText(true);

        Point point = new Point();

        // Logger.e(TAG, "bmp w: " + bmp.getWidth() + ", h:" + bmp.getHeight());
        if (bmp.getWidth() > bmp.getHeight()) {
            int temp = point.x;
            point.x = point.y;
            point.y = temp;
        }

        if (bmp.getHeight() <= 200) {
            startY = 180;
        }
        String printStr = "dhananjay";
        point.x = 12;
        point.y = bmp.getHeight() - startY;
        canvas.drawText(printStr, point.x, point.y, paint);
        startY = startY - subInterval;
       /* //1. Ecom Express
        if (waterMark.getEcom_text() != null && waterMark.getEcom_text().length() > 0) {
            String printStr = "dhananjay";
            point.x = 12;
            point.y = bmp.getHeight() - startY;
            canvas.drawText(printStr, point.x, point.y, paint);
            startY = startY - subInterval;
        }
        //2. OSV
        if (waterMark.getOsv_text() != null && waterMark.getOsv_text().length() > 0) {
            String printStr = waterMark.getOsv_text();
            point.x = 12;
            point.y = bmp.getHeight() - startY;
            canvas.drawText(printStr, point.x, point.y, paint);
            startY = startY - subInterval;
        }
        //3. Employee Name
        if (waterMark.getEmp_name() != null && waterMark.getEmp_name().length() > 0) {
            String printStr = "Emp Name: " + waterMark.getEmp_name();
            point.x = 12;
            point.y = bmp.getHeight() - startY;
            canvas.drawText(printStr, point.x, point.y, paint);
            startY = startY - subInterval;
        }
        //4. Employee Code
        if (waterMark.getEmp_code() != null && waterMark.getEmp_code().length() > 0) {
            String printStr = "Emp Code: " + waterMark.getEmp_code();
            point.x = 12;
            point.y = bmp.getHeight() - startY;
            canvas.drawText(printStr, point.x, point.y, paint);
            startY = startY - subInterval;
        }
        //5. Date
        if (waterMark.getDate() != null && waterMark.getDate().length() > 0) {
            point.y = bmp.getHeight() - startY;
            String date = "Date: " + waterMark.getDate();
            canvas.drawText(date, point.x, point.y, paint);
            startY = startY - subInterval;
        }
        //6
        if (waterMark.getLat() != null && waterMark.getLat().length() > 0 && waterMark.getLng() != null && waterMark.getLng().length() > 0) {
            String wLocation = "Lat: " + waterMark.getLat() + ", Lng: " + waterMark.getLng();
            point.y = bmp.getHeight() - startY;
            canvas.drawText(wLocation, point.x, point.y, paint);
        }*/
        return result;
    }

    public void setMagnifierView(Bitmap testBitmap, RectF imgbound) {
        Bitmap mBg = BitmapFactory.decodeResource(getResources(), R.drawable.ic_done);

        float RADIUS = getResources().getDimension(
                R.dimen.magnifier_radius);
        float BOTTOM_HEIGHT = getResources().getDimension(
                R.dimen.dock_bar_height);

        mMagnifierView.setImage(testBitmap, imgbound, RADIUS,
                BOTTOM_HEIGHT, mBg);
    }

    /**
     * 监听切边区域的调整事件
     *
     * @author zhongze_wu listen the event of crop area adjustment
     */
    private class MyCornorChangeListener implements OnCornorChangeListener {
        /**
         * 开始移动点或边 Start to move the frame or the point
         */
        @Override
        public void onPreMove() {
        }

        /**
         * 移动结束 Moving finished
         */
        @Override
        public void onPostMove() {
            dismissMagnifierView();
        }

        @Override
        public void onCornorChanged() {
            if (mIvEditView != null) {
                if (mIvEditView.isCanTrim(mEngineContext)) {
                    mIvEditView.setLinePaintColor(mNormalColor);
                } else {
                    mIvEditView.setLinePaintColor(mErrorColor);
                }

                mBitmapDetectBound = mIvEditView.getRegion(false);
                for (int i = 0; i < mBitmapDetectBound.length; i++) {

                    mViewTtrimBound[i] = mBitmapDetectBound[i];
                }
                mIvEditView.invalidate();
            }
        }

        @Override
        public void onClickPoint(float x, float y) {
            // TODO Auto-generated method stub
            updateMagnifierView(x, y);
        }
    }

    public void updateMagnifierView(float x, float y) {
        // mMagnifierView.update(x/4+100, y/4, mRotation,
        // mIvEditView.getImageMatrix());
        mMagnifierView.setVisibility(View.VISIBLE);

        mMagnifierView.update(x, y, mRotation, mIvEditView.getImageMatrix());

    }

    public void dismissMagnifierView() {
        if (mMagnifierView != null) {
            mMagnifierView.setVisibility(View.GONE);
            mMagnifierView.dismiss();
            mMagnifierView.recycleBGBitmap();
        }
    }

    // **********************************切边效果*****START******************************************
    private double mTrimScale;
    /**
     * 切边动画时，temp bitmap 最大边
     */
    private static double sTrimAnimThumbMaxSize;
    protected final int PROCESS_FINISH = 1008;
    protected static final int PROGRESS_ENHANCE_INCREASE = 1006;
    protected final int PROGRESS_STEP_CHANGED = 1007;
    protected static final int PROGRESS_TRIM_INCREASE = 1005;

    /**
     * 当前文字方向是否有效，满足两个条件其一：初始已经识别文字方向，用户旋转过；
     */
    private boolean mIsTextRotationValid;
    private boolean mIsNameCardMode;
    private static final int MSG_ROTATE_IMAGE = 1014;
    private int mRotation = 90;
    private int[] mCurrentThumbBounds, mCurrentFileBounds;

    public void changeDisplayAndSide() {
        StringBuffer sb = new StringBuffer(1024);
        DisplayMetrics outMetrics = new DisplayMetrics();
        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(outMetrics);
        int minLen = Math.min(outMetrics.heightPixels, outMetrics.widthPixels);
        int maxLen = Math.max(outMetrics.heightPixels, outMetrics.widthPixels);
        minLen = Math.max(minLen, 960);
        maxLen = Math.max(maxLen, 1280);
        // sb.append("\nMax pixels:").append(StringUtil.fileSizeFormat(1280 * 960 * 4));
        // sb.append("\nMin Width:").append(minLen);
        // sb.append("\nMax Width:").append(maxLen);
        // sb.append("\nMax pixels(New):").append(StringUtil.fileSizeFormat(maxLen
        // * maxLen * 4));
        // sb.append("\nMax Memory:").append(StringUtil.fileSizeFormat(Runtime.getRuntime().maxMemory()));
        float ratio = maxLen * maxLen * 4 * 3 * 100.0f
                / Runtime.getRuntime().maxMemory();
        sb.append("\nRatio(New):").append(String.format("%.3f%%", ratio));
        if (ratio > 25) {
            final int offset = 50;
            if (ratio > 35) {
                maxLen -= (offset * (outMetrics.density + 2));
            } else if (ratio > 30) {
                maxLen -= (offset * (outMetrics.density + 1));
            } else if (ratio > 25) {
                maxLen -= (offset * outMetrics.density);
            }
            // sb.append("\nMax Width(Adjust):").append(maxLen);
            // sb.append("\nMax pixels(Adjust):").append(StringUtil.fileSizeFormat(maxLen
            // * maxLen * 4));
            ratio = maxLen * maxLen * 4 * 3 * 100.0f
                    / Runtime.getRuntime().maxMemory();
            // sb.append("\nRatio(Adjust):").append(String.format("%.3f%%",
            // ratio));
        }
        ratio = 1280 * 960 * 4 * 3 * 100.0f / Runtime.getRuntime().maxMemory();
        // sb.append("\nRatio(Old):").append(String.format("%.3f%%", ratio));
        MIN_SIDE_LENGTH = minLen;// 取屏幕最小边，最大不超过960
        MAX_DISPLAY_WIDTH = maxLen;// 取屏幕最大边，最大不超过1280
        // sb.append("\n\n").append("AppConfig.MIN_SIDE_LENGTH ").append(AppConfig.MIN_SIDE_LENGTH);
        // sb.append("\n").append("AppConfig.MAX_DISPLAY_WIDTH ").append(AppConfig.MAX_DISPLAY_WIDTH);
        // Util.LOGD(TAG, sb.toString());
        // return sb;
    }

    public static int MIN_SIDE_LENGTH = 800;
    public static int MAX_DISPLAY_WIDTH = 1280;
    ISImageEnhanceHandler mRawImageHandler;

    private void getTrimRegions() {
        if (mIvEditView.isRegionVisible()) {
            mCurrentThumbBounds = mIvEditView.getRegion(true);
            mCurrentFileBounds = mIvEditView.getRegion(false);
            // mCurrentFileBoundsF = mImageView.getRegionF(false);
        } else {
            Log.d(TAG,
                    "getTrimRegions while mImageView.isRegionAvailable() = false");
        }
        changeDisplayAndSide();
        // mThumb = loadBitmap();
        Config config = null;
        if (config == null) {
            config = getDefaultConfig();
        }
        mThumb = Utils.loadBitmap(mCurrentInputImagePath, MIN_SIDE_LENGTH,
                MIN_SIDE_LENGTH * MAX_DISPLAY_WIDTH, config, false,
                mCurrentFileBounds);

        // RectF imgbound = new RectF(0, 0, mThumb.getWidth(),
        // mThumb.getHeight());
        //
        // mIvEditView.getImageMatrix().mapRect(imgbound);
        // mMagnifierView.setImage(mThumb, imgbound);

        for (int i = 0; i < mCurrentThumbBounds.length; i++) {
            mCurrentThumbBounds[i] = (int) (mCurrentFileBounds[i]);

        }

        // mRotation = Utils.getImageRotation(mCurrentInputImagePath);

        // mRotation = (mRotation + 90) % 360;//切边图片旋转时候

        mRotateBitmap = new RotateBitmap(mThumb, mRotation);

        sTrimAnimThumbMaxSize = mThumb.getWidth() / 2;
        if (sTrimAnimThumbMaxSize < 400) {
            sTrimAnimThumbMaxSize = 400;
        }

        mRawImageHandler = ISImageEnhanceHandler.newInstance(
                getApplicationContext(), mHandlerAnim, mScannerSDK);
        // 保存thumb
        sPreStoreThumbPath = mRootPath + File.separator + "pretempthumb.jpg";

        storeThumbToFile(sPreStoreThumbPath);
        // 设置image，thumb路径
        mRawImageHandler.setImagePath(mCurrentInputImagePath,
                sPreStoreThumbPath);
        mRawImageHandler.setEngineContext(mEngineContext);

        mScanRecordControl = ScanRecordControl
                .getInstance(getApplicationContext());
        if (!mScanRecordControl.isScannFinishNormal()
                && (new File(mScanRecordControl.getImageRawPath()).exists())) {
            mScanRecordControl.setCrashedImageFound(true);
        }

    }

    ScanRecordControl mScanRecordControl;
    String sPreStoreThumbPath = null;

    /**
     * 将 {@link ImageScannerActivity#mThumb} 保存到path中
     *
     * @param path thumb 保存的路径
     */
    private void storeThumbToFile(String path) {
        try {
            FileOutputStream out = new FileOutputStream(path);
            mThumb.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.close();
        } catch (Exception e) {
            Log.d(TAG, e.getMessage());
        }
        Log.d(TAG, "storeThumbToFile： " + path);
    }

    /**
     * 复制bitmap，并进行缩放，使其最大宽高不超过
     * {@link ImageScannerActivity#sTrimAnimThumbMaxSize}
     * <p>
     * <p>
     * 用于创建动画用-小小图
     *
     * @param src 小图，当前屏幕显示的bitmap
     * @return 小小图，切边动画用
     */
    private Bitmap copySmallBitmap(Bitmap src) {
        Bitmap dst = null;
        if (src != null) {
            Config config = src.getConfig();
            if (config == null) {
                config = getDefaultConfig();
            }
            try {
                double scale = Math.min(sTrimAnimThumbMaxSize / src.getWidth(),
                        sTrimAnimThumbMaxSize / src.getHeight());
                mTrimScale = scale;
                dst = Bitmap.createScaledBitmap(src,
                        (int) (src.getWidth() * scale),
                        (int) (src.getHeight() * scale), true);
                Log.d(TAG,
                        "ori w,h = " + src.getWidth() + ", " + src.getHeight()
                                + "; dst w,h = "
                                + (int) (src.getWidth() * scale) + ", "
                                + (int) (src.getHeight() * scale)
                                + ", mTrimScale = " + mTrimScale);
                // dst = src.copy(config, true);
            } catch (OutOfMemoryError e) {
                Log.d(TAG, "copyBitmap", e);
                System.gc();
            }
        }
        return dst;
    }

    public Config getDefaultConfig() {
        ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memInfo = new ActivityManager.MemoryInfo();
        am.getMemoryInfo(memInfo);
        Runtime runtime = Runtime.getRuntime();

        long PROCESS_MEMORY_LIMIT = runtime.maxMemory();
        Config DEFAULT_BITMAP_CONFIG;
        boolean boolavailMem = memInfo.availMem >= (48 * 1024 * 1024);
        boolean boollimit = PROCESS_MEMORY_LIMIT >= 48 * 1024 * 1024;

        if (boolavailMem && boollimit) {
            DEFAULT_BITMAP_CONFIG = Config.ARGB_8888;
        } else {
            DEFAULT_BITMAP_CONFIG = Config.RGB_565;
        }
        return DEFAULT_BITMAP_CONFIG;
    }

    public Bitmap copyBitmap(Bitmap src, Config config) {
        Bitmap dst = null;
        if (src != null) {

            try {
                if (config == null) {
                    config = src.getConfig();
                }
                if (config == null) {
                    config = getDefaultConfig();
                }

                dst = src.copy(config, true);

            } catch (OutOfMemoryError e) {
                Log.d(TAG, "copyBitmap", e);
                System.gc();
            }
        }
        return dst;
    }

    /**
     * 切边/增强 动画时帧响应listenter
     *
     * @author Ben
     */
    private class ImageProcessListener implements ScannerProcessListener {
        // private int[] connerPoints;
        // private Bitmap thumb, b_out;
        private final Handler mHandlerAnim;

        private Bitmap mSrc; // 小小图
        private Bitmap mOut;
        private int[] mSmallTrimBounds;

        private final int TrimGipTime;

        public ImageProcessListener(Handler handler) {
            super();
            mHandlerAnim = handler;
            TrimGipTime = 100;
        }

        public void setTrim(Bitmap thumb) {
            mSrc = copySmallBitmap(thumb);
            mSmallTrimBounds = new int[mCurrentThumbBounds.length];
            for (int i = 0; i < mSmallTrimBounds.length; i++) {
                mSmallTrimBounds[i] = (int) (mCurrentThumbBounds[i] * mTrimScale);
            }
        }

        private long mLastProcessTime = 0;

        public boolean onProcess(int sessionId, int progress) {
            if (isFinishing())
                return false;
            // if(mLastProcessTime != 0){
            // Log.d(TAG, "onProcess interval at " + progress + " = " +
            // (System.currentTimeMillis() - mLastProcessTime));
            // }
            // mLastProcessTime = System.currentTimeMillis();
            int target;
            if (sessionId == ScannerEngine.IN_PROGRESS_ENHANCE
                    || sessionId == 0) {
                // step enhance
                target = PROGRESS_ENHANCE_INCREASE;
                mHandlerAnim.sendMessage(Message.obtain(mHandlerAnim, target,
                        progress, 0));
            }
            if (sessionId == ScannerEngine.IN_PROGRESS_DEWARP) { // trim
                target = PROGRESS_TRIM_INCREASE;
                progress += 10;
                if (progress > 100) {
                    progress = 100;
                }
                long time = System.currentTimeMillis();

                // mOut = BitmapUtils.copyBitmap(mSrc);
                mOut = copyBitmap(mSrc, null);// fox修改

                int result = ScannerEngine.drawDewarpProgressImage(
                        mEngineContext, mSrc, mSmallTrimBounds, mOut, progress,
                        100);
                // poolFinish[index] = true;
                // Log.d(TAG, "trimTemp " + progress + " comsume = " +
                // (System.currentTimeMillis() - time) + ", result = " +
                // result);

                // 100ms 1帧

                long sleepTime = TrimGipTime
                        - (System.currentTimeMillis() - mLastProcessTime);
                if (sleepTime > 0) {
                    Log.d(TAG, "trim anim sleep: " + sleepTime);
                    try {
                        Thread.sleep(sleepTime);
                    } catch (InterruptedException e) {
                        Log.d(TAG, e.getMessage());
                    }
                }

                mHandlerAnim.sendMessage(mHandlerAnim.obtainMessage(target,
                        progress, 0, mOut));
            }
            // Log.d(TAG, "onProcess cosume = " + (System.currentTimeMillis() -
            // mLastProcessTime));
            mLastProcessTime = System.currentTimeMillis();
            return true;
        }

    }

    private Bitmap mThumb;
    private Bitmap mEnhanceSource;
    boolean mIsTrim = false;
    ImageProcessListener mImageProcessListener;

    /**
     * 切边动画类
     *
     * @author Ben
     */
    public class AnimationView extends View {
        public AnimationView(Context context) {
            super(context);
            ObjectAnimator objectAnimator = (ObjectAnimator) AnimatorInflater
                    .loadAnimator(ImageScannerActivity.this, R.drawable.coloranimation);
            objectAnimator.setEvaluator(new ArgbEvaluator());
            objectAnimator.setTarget(this);
            objectAnimator.start();
        }

    }

    public void recycleBitmap(Bitmap bitmapRec) {
        if (bitmapRec != null && !bitmapRec.isRecycled()) {
            bitmapRec.recycle();
        }
    }

    private class TrimAnimTask extends AsyncTask<Void, Integer, Boolean> {

        @Override
        protected void onPreExecute() {
            mImageProcessListener = new ImageProcessListener(mHandlerAnim);

            mIvEditView.setRegionVisibility(false);
            bt_toolbar_line.setVisibility(View.GONE);
            bt_process_line.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(Boolean result) {
            Log.d(TAG, "TrimAnimTask requestStoreImage after task");
            // mRawImageHandler.setPrepared(true);
            /**
             * 3.5版本中，仅在图片切边完成后，做一次预保存
             */

            bt_toolbar_line.setVisibility(View.VISIBLE);
            bt_process_line.setVisibility(View.GONE);
            startTtrim();
        }

        @Override
        protected Boolean doInBackground(Void... arg0) {

            Config config = mThumb.getConfig();
            if (config == null) {
                config = getDefaultConfig();
            }

			/*
             * copy mThumb to trimSrc, trimSrc will be the source bitmap in
			 * trimming animation,while mThumb as the output
			 */

            if (mIvEditView.isRegionAvailable()) { // 需要切边
                Bitmap trimSrc = copyBitmap(mThumb, config);
                mEnhanceSource = trimImage(trimSrc, mCurrentThumbBounds);
                recycleBitmap(trimSrc);
                mIsTrim = true;
            } else {
                mIsTrim = false;
                // mRawImageHandler.restoreThumb(); // 不切边，需要重新解析小图
            }
            // 切边失败 或者 不需要切边
            if (mEnhanceSource == null) {
                mEnhanceSource = copyBitmap(mThumb, config);
            }
            // 无法复制 增强需要的原图
            if (mEnhanceSource == null) {
                // 结束处理流程，不更新ImageView的图片-mThumb
                releaseModeThumb();
                mHandlerAnim.sendMessage(mHandlerAnim.obtainMessage(
                        PROCESS_FINISH, 0, 0, mThumb));
                // mHandler.sendMessage(mHandler.obtainMessage(PROCESS_FINISH,
                // 0, 0, mThumb));
            } else {
                handleModeMenuThumb(mEnhanceSource);
            }
            // enhanceImage(config, mIsTrim);
            // TimeLogger.endTrimAnim();

            return null;
        }

        /**
         * [自动、原图、增亮、增强并锐化、灰度，黑白,省墨]
         */
        private final Bitmap[] mEnhanceModeBitmap = new Bitmap[7];

        public Bitmap dewarpImagePlane(int context, Bitmap image,
                                       int[] corner_xy, boolean bAntiAliasing) {
            Config config = image.getConfig();
            if (config == null) {
                config = Config.RGB_565;
            }
            Bitmap out = copyBitmap(image, config);
            try {
                int ret = ScannerEngine.trimBitmap(context, image, corner_xy,
                        out, 1, 1);
                if (ret < 0) {
                    if (out != null) {
                        out.recycle();
                        out = null;
                    }
                }
            } catch (OutOfMemoryError e) {
                Log.d(TAG, e.getMessage());
                out = null;
            }
            return out;
        }

        /**
         * @param src
         * @param bounds
         * @return an Bitmap after trim and scale,while mThumb is the output of
         * trim progress
         */
        private Bitmap trimImage(Bitmap src, int[] bounds) {
            if (src == null) {
                Log.d(TAG, "skip trimImage");
                return mThumb;
            }
            // send msg of starting trim
            mHandlerAnim.sendMessage(mHandlerAnim.obtainMessage(
                    PROGRESS_STEP_CHANGED, R.string.step_trim, 0, mThumb));
            mScanRecordControl
                    .setCurrentScanStep(ScanRecordControl.DEWARP_IMAGE_PLANE);
            Bitmap trimmed = dewarpImagePlane(mEngineContext, src, bounds, true);

            // mLastCropBounds = mCurrentFileBoundsF;

            mImageProcessListener.setTrim(src);

            Log.d(TAG, "dewarpImagePlane beign");
            long start = System.currentTimeMillis();
            ScannerEngine.setProcessListener(mEngineContext,
                    mImageProcessListener);

            mRawImageHandler.trimThumb(mCurrentThumbBounds);
            ScannerEngine.setProcessListener(mEngineContext, null);
            int cost = (int) (System.currentTimeMillis() - start);
            Log.d(TAG, "dewarpImagePlane ok consume " + cost + ", finish at "
                    + System.currentTimeMillis());

            return trimmed;
        }

    }

    private void releaseModeThumb() {
        if (mEnhanceModeBitmap != null) {
            for (int index = 0; index < mEnhanceModeBitmap.length; index++) {
                if (mEnhanceModeBitmap[index] != null
                        && !mEnhanceModeBitmap[index].isRecycled()) {
                    mEnhanceModeBitmap[index].recycle();
                    mEnhanceModeBitmap[index] = null;
                }
            }
        }
    }

    private Bitmap getEnhanceSourceCopyBitmap(Config config) {
        Bitmap result = null;
        result = copyBitmap(mEnhanceSource, config);
        return result;
    }

    private RotateBitmap mRotateBitmap;

    private final Handler mHandlerAnim = new Handler() {
        @Override
        public void dispatchMessage(Message msg) {
            switch (msg.what) {

                case PROGRESS_STEP_CHANGED:
                    try {
                        mThumb = (Bitmap) msg.obj;
                        // mProcessStepText.setText(msg.arg1);
                        progress_horizontal.setProgress(0);
                        mRotateBitmap.setBitmap(mThumb);
                        mRotateBitmap.setRotation(mRotation);
                        mIvEditView.setImageRotateBitmapResetBase(mRotateBitmap, false);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    // RectF imgbound = new RectF(0, 0, mThumb.getWidth(),
                    // mThumb.getHeight());
                    // mIvEditView.getImageMatrix().mapRect(imgbound);
                    // mMagnifierView.setImage(mThumb, imgbound);
                    break;
                case PROGRESS_TRIM_INCREASE:
                    mThumb = (Bitmap) msg.obj;
                    progress_horizontal.setProgress(msg.arg1);
                    if (msg.arg1 > 70) {
                        bt_process_comment_id.setText(getResources().getString(R.string.begin_enhance));

                        //"正在增强...");

                    } else {
                        bt_process_comment_id.setText(getResources().getString(R.string.begin_trim));

                    }
                    mRotateBitmap.setBitmap(mThumb);
                    mRotateBitmap.setRotation(mRotation);
                    mIvEditView.setImageRotateBitmapResetBase(mRotateBitmap, false);
                    // imgbound = new RectF(0, 0, mThumb.getWidth(),
                    // mThumb.getHeight());
                    // mIvEditView.getImageMatrix().mapRect(imgbound);
                    // mMagnifierView.setImage(mThumb, imgbound);
                    break;
                case PROGRESS_ENHANCE_INCREASE:
                    // mProgressBar.setProgress(msg.arg1);
                    // mIvEditView.setEnhanceProcess(msg.arg1);
                    break;

                default:
                    break;

            }
            super.dispatchMessage(msg);

        }
    };
    // ********************************增强图*******************
    /**
     * 最大次数
     */
    private static final int MAX_COPY_TRY_TIME = 2;

    private class EnhanceCallable implements Callable<Bitmap> {
        private final Bitmap mSrc;
        private final int mMode;

        public EnhanceCallable(Bitmap src, int enhanceMode) {
            mSrc = src;
            mMode = enhanceMode;
        }

        @Override
        public Bitmap call() throws Exception {
            Bitmap dstBitmap = null;
            int repeat = 0;
            while (dstBitmap == null && repeat < MAX_COPY_TRY_TIME) {
                // 重试机制，如果一次拷贝不成功。
                dstBitmap = copyBitmap(mSrc, null);
                repeat++;
            }
            if (dstBitmap != null) {
                int threadContext = ScannerEngine.initThreadContext();
                ScannerEngine.enhanceImage(threadContext, dstBitmap, mMode);
                ScannerEngine.destroyThreadContext(threadContext);
            }
            return dstBitmap;
        }
    }

    private ExecutorService mFixedThreadPool = null;
    private static final int WIDTH = 100;
    private static final int HEIGHT = 100;

    /**
     * [自动、原图、增亮、增强并锐化、灰度，黑白,省墨]
     */
    private final Bitmap[] mEnhanceModeBitmap = new Bitmap[7];

    /**
     * 获取增强模式菜单的item 缩略图尺寸
     *
     * @param thumbWidth
     * @param thumbHeight
     * @param rotation
     * @return
     */
    private int[] getEnhanceMenuThumbSize(int thumbWidth, int thumbHeight,
                                          int rotation) {
        int[] size = new int[2];
        if (thumbWidth <= 0) {
            thumbWidth = WIDTH;
        }
        if (thumbHeight <= 0) {
            thumbHeight = HEIGHT;
        }
        if (rotation == 90 || rotation == 270) {
            int temp = thumbWidth;
            thumbWidth = thumbHeight;
            thumbHeight = temp;
        }
        size[0] = thumbWidth;
        size[1] = thumbHeight;
        return size;
    }

    /**
     * true表示准备好增强菜单缩略图，false表示没有准备好增强菜单缩略图
     */
    private boolean mFinishPrepareEnhanceMenuThumb = true;
    /**
     * 生成相关增强模式的缩略图
     *
     * @param src
     */
    boolean isSmallScreen = true;
    boolean mIs7Tablet = false;

    /**
     * 手机每个模式菜单项尺寸
     *
     * @return [宽，高]
     */
    private int[] getPhoneEnhanceMenuSize() {
        int[] size = {};
        try {
            size = new int[2];
            size[0] = (int) (mIvEditView.getWidth() / 4.5f);
            size[1] = getResources().getDimensionPixelSize(
                    R.dimen.enhance_menu_height);
            return size;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return size;
    }

    /**
     * 7寸每个模式菜单项尺寸
     *
     * @return [宽，高]
     */
    private int[] get7TabletEnhanceMenuSize() {
        int[] size = new int[2];
        size[0] = mIvEditView.getWidth() / 6;
        if (mCurOrientation == Configuration.ORIENTATION_LANDSCAPE) {
            size[1] = getResources().getDimensionPixelSize(
                    R.dimen.enhance_menu_height);
        } else if (mCurOrientation == Configuration.ORIENTATION_PORTRAIT) {
            size[1] = getResources().getDimensionPixelSize(
                    R.dimen.enhance_menu_height_7tablet_portrait);
        }
        return size;
    }

    /**
     * 10寸每个模式菜单项尺寸
     *
     * @return[宽，高]
     */
    private int[] get10TabletEnhanceMenuSize() {
        int[] size = new int[2];
        if (mCurOrientation == Configuration.ORIENTATION_LANDSCAPE) {
            size[0] = getResources().getDimensionPixelSize(
                    R.dimen.pad_image_scan_gridview_item_width);
            size[1] = mIvEditView.getHeight() / 6;
        } else if (mCurOrientation == Configuration.ORIENTATION_PORTRAIT) {
            size[0] = mIvEditView.getWidth() / 6;
            size[1] = getResources().getDimensionPixelSize(
                    R.dimen.pad_image_scan_gridview_item_height);
        }
        return size;
    }

    /**
     * 7寸平板，竖屏的模式菜单高度比横屏时，要高些
     */
    private void adust7TabletEnhanceMenuHeight() {
        int[] size = get7TabletEnhanceMenuSize();
        if (size[1] > 0) {
            ViewGroup.LayoutParams params = mEnhanceModeBar.getLayoutParams();
            params.height = size[1];
            mEnhanceModeBar.setLayoutParams(params);
        }
    }

    private void handleModeMenuThumb(Bitmap src) {
        try {
            mFinishPrepareEnhanceMenuThumb = false;
            if (mFixedThreadPool == null) {
                int numberProcessors = 1;
                if (Runtime.getRuntime().availableProcessors() > 2) {
                    // 限制不超过2个线程，防止同时对多张图片进行处理
                    numberProcessors = 2;
                }
                mFixedThreadPool = Executors.newFixedThreadPool(numberProcessors);
            }
            int engineContext = ScannerEngine.initThreadContext();
            int[] size = ScannerEngine.nativeDewarpImagePlaneForSize(engineContext,
                    src.getWidth(), src.getHeight(), mCurrentThumbBounds);
            ScannerEngine.destroyThreadContext(engineContext);
            int[] menuSize = null;
            int[] thumbSize = null;
            // int margin=10;
            int margin = getResources().getDimensionPixelSize(
                    R.dimen.enhance_menu_margin);
            if ((isSmallScreen && !mIs7Tablet)) {
                // 手机
                menuSize = getPhoneEnhanceMenuSize();
                menuSize[0] = menuSize[0] - margin - margin;
                thumbSize = getEnhanceMenuThumbSize(menuSize[0], menuSize[1],
                        mRotation);
            } else if (mIs7Tablet) {
                // 7寸的平板
                menuSize = get7TabletEnhanceMenuSize();
                menuSize[0] = menuSize[0] - margin - margin;
                menuSize[1] = menuSize[1] - margin - margin;
                thumbSize = getEnhanceMenuThumbSize(menuSize[0], menuSize[1],
                        mRotation);
            } else {
                menuSize = get10TabletEnhanceMenuSize();
                menuSize[0] = menuSize[0] - margin - margin;
                menuSize[1] = menuSize[1] - margin - margin;
                thumbSize = getEnhanceMenuThumbSize(menuSize[0], menuSize[1],
                        mRotation);
            }

            final Bitmap rawThumb = getCentreCropScaleBitmap(src, thumbSize[0],
                    thumbSize[1], size);
            // new Thread(new Runnable() {
            // @Override
            // public void run() {
            long startTime = System.currentTimeMillis();
            Future<Bitmap> autoFuture = mFixedThreadPool
                    .submit(new EnhanceCallable(rawThumb, mAutoMode));
            Future<Bitmap> linerFuture = mFixedThreadPool
                    .submit(new EnhanceCallable(rawThumb,
                            ScannerSDK.ENHANCE_MODE_LINEAR));
            Future<Bitmap> magicFuture = mFixedThreadPool
                    .submit(new EnhanceCallable(rawThumb,
                            ScannerSDK.ENHANCE_MODE_MAGIC));
            Future<Bitmap> grayFuture = mFixedThreadPool
                    .submit(new EnhanceCallable(rawThumb,
                            ScannerSDK.ENHANCE_MODE_GRAY));
            Future<Bitmap> blackWhiteFuture = mFixedThreadPool
                    .submit(new EnhanceCallable(rawThumb,
                            ScannerSDK.ENHANCE_MODE_BLACK_WHITE));
            // Future<Bitmap> whiteBlackFuture = mFixedThreadPool.submit(new
            // EnhanceCallable(rawThumb, ScannerSDK.ENHANCE_MODE_WHITE_BLACK ));
            try {
                // Future 的get()方法是阻塞
                mEnhanceModeBitmap[1] = autoFuture.get();// 自动
                mEnhanceModeBitmap[0] = rawThumb;// 原图
                mEnhanceModeBitmap[2] = linerFuture.get();// 增亮
                mEnhanceModeBitmap[3] = magicFuture.get();// 增强并锐化
                mEnhanceModeBitmap[4] = grayFuture.get();// 灰度
                mEnhanceModeBitmap[5] = blackWhiteFuture.get();// 黑白
                // mEnhanceModeBitmap[6] = whiteBlackFuture.get();//省墨
            } catch (InterruptedException e) {
                Log.d(TAG, "InterruptedException msg=" + e.getMessage());
            } catch (ExecutionException e) {
                Log.d(TAG, "ExecutionException msg=" + e.getMessage());
            }
            boolean findNumThumb = false;
            for (int index = 0; index < mEnhanceModeBitmap.length; index++) {
                if (mEnhanceModeBitmap[index] == null) {
                    findNumThumb = true;
                    break;
                }
            }
            if (!findNumThumb) {
                mFinishPrepareEnhanceMenuThumb = true;
            }
            Log.d(TAG, "handleModeMenuThumb Time="
                    + (System.currentTimeMillis() - startTime));
            // }
            // }).start();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(ImageScannerActivity.this, "Image corrupted try Again...", Toast.LENGTH_SHORT).show();
        }
    }

    private Bitmap getCentreCropScaleBitmap(Bitmap src, int viewWidth,
                                            int viewHeight, int[] trimSize) {
        Bitmap dstBitmap = null;
        if (src != null && viewWidth > 0 && viewHeight > 0) {
            int left = 0;
            int top = 0;
            int cropWidth = 0;
            int cropHeght = 0;
            float imageScale;
            float srcScale = 1.0f * src.getWidth() / src.getHeight();
            if (trimSize != null && trimSize[0] > 0 && trimSize[1] > 0) {
                imageScale = 1.0f * trimSize[0] / trimSize[1];
                if (srcScale > imageScale) {
                    cropHeght = src.getHeight();
                    cropWidth = (int) (imageScale * cropHeght);
                } else {
                    cropWidth = src.getWidth();
                    cropHeght = (int) (cropWidth / imageScale);
                }
            } else {
                imageScale = srcScale;
                cropHeght = src.getHeight();
                cropWidth = src.getWidth();
            }

            float viewScale = 1.0f * viewWidth / viewHeight;
            float scale = 1.0f;
            if (Math.abs(viewScale - imageScale) > 0.001) {
                if (viewScale > imageScale) {
                    scale = viewWidth / (float) cropWidth;
                    cropHeght = (int) (cropWidth / viewScale);
                } else {
                    scale = viewHeight / (float) cropHeght;
                    cropWidth = (int) (cropHeght * viewScale);
                }
            } else {
                scale = viewWidth / (float) cropWidth;
            }
            top = (src.getHeight() - cropHeght) / 2;
            left = (src.getWidth() - cropWidth) / 2;
            if (left >= 0 && left <= src.getWidth() && top >= 0
                    && top <= src.getHeight() && cropWidth > 0
                    && cropWidth <= src.getWidth() && cropHeght > 0
                    && cropHeght <= src.getHeight()) {
                Matrix m = null;
                if (scale < 1.0f) {
                    m = new Matrix();
                    m.postScale(scale, scale);
                }
                if (mRotation != 0) {
                    if (m == null) {
                        m = new Matrix();
                    }
                    m.postRotate(mRotation);
                }
                try {
                    dstBitmap = Bitmap.createBitmap(src, left, top, cropWidth,
                            cropHeght, m, true);
                    if (dstBitmap == src) {
                        Log.d(TAG, "dstBitmap == src");
                        dstBitmap = copyBitmap(src, null);
                    }
                } catch (OutOfMemoryError e) {
                    Log.d(TAG, e.getMessage());
                }
            }
        }
        return dstBitmap;
    }

    private int mEnhanceModeIndex = 0;

    /**
     * 初始化增强模式工作栏
     */
    private void initEnhanceBar() {
        if (isSmallScreen && !mIs7Tablet
                && mEnhanceModeBar instanceof HorizontalListView) {
            int[] size = getPhoneEnhanceMenuSize();
            if (size[0] <= 0) {
                DisplayMetrics metrics = new DisplayMetrics();
                WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
                wm.getDefaultDisplay().getMetrics(metrics);
                size[0] = (int) ((metrics.widthPixels) / 4.5f);
            }
            final int oneItemWidth = size[0];
            Log.d(TAG, " oneItemWidth=" + oneItemWidth);
            final HorizontalListView list = (HorizontalListView) mEnhanceModeBar;
            int margin = getResources().getDimensionPixelSize(
                    R.dimen.enhance_menu_margin);
            final int thumbWidth = oneItemWidth - margin - margin;
            final BaseAdapter adapter = new BaseAdapter() {

                @SuppressWarnings("deprecation")
                @Override
                public View getView(int position, View convertView,
                                    ViewGroup parent) {
                    View view = convertView;
                    ImageView iv = null;
                    TextView tv = null;
                    RelativeLayout item_rel;
                    // View selectView = null;
                    if (view == null) {
                        view = View.inflate(ImageScannerActivity.this,
                                R.layout.horizontal_list_item, null);
                        view.setMinimumWidth(oneItemWidth);
                        iv = (ImageView) view.findViewById(R.id.item_image);
                        tv = (TextView) view.findViewById(R.id.item_text);
                        tv.setMinimumWidth(thumbWidth);
                        ViewGroup.LayoutParams params = iv.getLayoutParams();
                        params.width = thumbWidth;
                        params.height = thumbWidth;
                        iv.setLayoutParams(params);
                    } else {
                        iv = (ImageView) view.findViewById(R.id.item_image);
                        tv = (TextView) view.findViewById(R.id.item_text);
                    }
                    // selectView = view.findViewById(R.id.item_v_mask);
                    item_rel = (RelativeLayout) view
                            .findViewById(R.id.item_rel);

                    try {
                        if (mEnhanceModeBitmap[position] != null
                                && !mEnhanceModeBitmap[position].isRecycled()) {
                            iv.setImageBitmap(mEnhanceModeBitmap[position]);
                        } else {
                            iv.setImageBitmap(null);
                        }
                        if (mEnhanceModeIndex == position) {
                            // selectView.setVisibility(View.VISIBLE);
                            item_rel.setBackgroundDrawable(getResources()
                                    .getDrawable(R.drawable.rounded_choose));
                        } else {
                            item_rel.setBackgroundDrawable(getResources()
                                    .getDrawable(R.drawable.rounded_unchoose));

                            // selectView.setVisibility(View.GONE);
                        }
                    } catch (OutOfMemoryError e) {// LGE OS 2.3
                        Log.d(TAG, e.getMessage());
                    }
                    // 当发现增强并锐化按钮时记录这个view
                    // if(3 == position && mEnableTips){
                    // mGuideWindowAnchorView = view;
                    // }
                    Log.d(TAG, "getView mEnhanceModeIndex=" + mEnhanceModeIndex
                            + " pos=" + position);
                    tv.setText(mModeNames[position]);
                    return view;
                }

                @Override
                public long getItemId(int position) {
                    return position;
                }

                @Override
                public Object getItem(int position) {
                    return position;
                }

                @Override
                public int getCount() {
                    return mModeNames.length;
                }
            };
            list.setAdapter(adapter);
            list.setOnItemClickListener(new OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int pos, long id) {

                    mEnhanceModeIndex = pos;
                    if (mOriginalEnhanceBitmap != null) {
                        EnhanceTask enhanceTask = new EnhanceTask(
                                getEnhanceMode(pos));
                        enhanceTask.execute();
                    } else {
                        Log.d(TAG, "mOriginalEnhanceBitmap="
                                + mOriginalEnhanceBitmap);
                    }
                    // previewOneMode(pos);
                    // 点击显示不全的图标，滚动使其显示完全
                    int firstViewPos = list.getFirstVisiblePosition();
                    //
                    int offset = 0;
                    int childpos = mEnhanceModeIndex - firstViewPos;
                    if (mEnhanceModeIndex > 0
                            && mEnhanceModeIndex < mModeNames.length - 1) { // 不是首尾两个模式，需要显示偏移
                        offset = oneItemWidth / 2;
                    }
                    adapter.notifyDataSetChanged();
                    // list.makeChildAtVisable(childpos, offset);
                    // // Util.LOGE(TAG,
                    // "list.setOnItemClickListener(new OnItemClickListener() {");
                    // //
                    // mRawImageHandler.requestStoreImage(createRawStoreRequest());
                    // mBigImageDirty = true;
                }
            });
            if (mEnhanceModeIndex > 3) {
                float offset = (mEnhanceModeIndex - 3.5f) * oneItemWidth;
                list.scrollTo((int) offset);
            }
        } else {
            // loadTabletEnhanceMenu();
            // selectEnhanceMenu(mEnhanceModeIndex);
        }
    }

    /**
     * 回收ImageView占用的图像内存;
     *
     * @param view
     */
    public static void recycleImageView(View view) {
        if (view == null)
            return;
        if (view instanceof ImageView) {
            Drawable drawable = ((ImageView) view).getDrawable();
            if (drawable instanceof BitmapDrawable) {
                Bitmap bmp = ((BitmapDrawable) drawable).getBitmap();
                if (bmp != null && !bmp.isRecycled()) {
                    ((ImageView) view).setImageBitmap(null);
                    bmp.recycle();
                    bmp = null;
                }
            }
        }
    }

}
