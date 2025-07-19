/**
 * Project Name:IDCardScanCaller
 * File Name:PreviewActivity.java
 * Package Name:com.intsig.idcardscancaller
 * Date:2016年3月15日下午2:14:46
 * Copyright (c) 2016, 上海合合信息 All Rights Reserved.
 */

package preview;


import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.intsig.imageprocessdemo.R;

import util.CameraRecognizeUtil;

/**
 * 功能：建议客户集成ISCardScanActivity 重写 addCameraUi 和 recognizeCardCallBack 来自定义相机和相关的业务逻辑，如果有必要请自行修改
 * ClassName:PreviewActivity <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason: TODO ADD REASON. <br/>
 * Date: 2018年1月8日 上午12:14:46 <br/>
 *
 * @author feicen
 */
public class PreviewActivity extends ISCardScanActivity {

    ImageView take_photo_id;
    public static final String EXTRA_KEY_RESULT = "EXTRA_KEY_RESULT";


    @Override
    public void addCameraUi(RelativeLayout parentView, final Boolean boolRecognize) {
        // TODO Auto-generated method stub
        super.addCameraUi(parentView, boolRecognize);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);

        lp.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
        // **********************************添加动态的布局
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.camera, null);
        take_photo_id = (ImageView) view.findViewById(R.id.take_photo_id);
        take_photo_id.setVisibility(View.GONE);
        Toast.makeText(PreviewActivity.this, "Please adjust camera on to scan first.", Toast.LENGTH_LONG).show();
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                take_photo_id.setVisibility(View.VISIBLE);
            }
        }, 5000);

        take_photo_id.setOnTouchListener((arg0, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    break;
                case MotionEvent.ACTION_UP:
                    try {
                        if (!boolRecognize) {
                            takepictrueCameraTake();
                        } else {
                            Log.e("takepictrueCameraTake", "正在预览识别");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        break;

                    }

                    break;

                default:
                    break;
            }
            return true;
        });

        parentView.addView(view, lp);
    }


    @Override
    public void recognizeCardCallBack(byte[] data) {
        // TODO Auto-generated method stub
        super.recognizeCardCallBack(data);

        //存储到本地然后返回即可
        //数据处理，客户可以根据自己的逻辑自行处理
        new CameraRecognizeUtil(PreviewActivity.this, take_photo_id)
                .doRecogWork(data);
    }

}
