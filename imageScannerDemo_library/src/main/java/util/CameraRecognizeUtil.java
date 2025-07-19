package util;

import static android.app.Activity.RESULT_OK;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.intsig.imageprocessdemo.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import preview.PreviewActivity;

public class CameraRecognizeUtil {
	private final Activity activity;
	private final ImageView textView;

	public	CameraRecognizeUtil(Activity activity, ImageView textView) {
		this.activity = activity;
		this.textView = textView;

	}

	/**
	 * 做识别工作放这里
	 */
	@SuppressLint("StaticFieldLeak")
	public  void doRecogWork(final byte[] data) {
		popupHandler.sendEmptyMessageDelayed(MSG_POPUP, 10);
		new AsyncTask<Void, Void, String>() {
			@Override
			protected void onPreExecute() {
				super.onPreExecute();
			}

			@Override
			protected String doInBackground(Void... params) {
//				Bitmap bm = CameraRecognizeUtil.loadBitmap(data, 1200, 1600);
				BitmapFactory.Options opts = new BitmapFactory.Options();
			Bitmap	bm = BitmapFactory.decodeByteArray(data, 0, data.length, opts);
				String copyfilename =  Environment.getExternalStorageDirectory()+File.separator
						+ "oriTrimcamera.jpg";


				CameraRecognizeUtil.saveBitmap(copyfilename, bm);
				return copyfilename;

			}

			protected void onPostExecute(String result) {
				popupWindow.dismiss();
				Intent data = new Intent();
				data.putExtra(PreviewActivity.EXTRA_KEY_RESULT, result);
				activity.setResult(RESULT_OK, data);
				activity.finish();


			}
        }.execute();
	}

	private PopupWindow popupWindow;

	private boolean boolpopupWindow = true;

	private void popupwindowShowProgress() {
		RelativeLayout layout = (RelativeLayout) LayoutInflater.from(activity)
				.inflate(R.layout.cui_view_loadingdialog, null);
		popupWindow = new PopupWindow(layout,
				ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.MATCH_PARENT, true);

		popupWindow.showAtLocation(textView, Gravity.CENTER, 0, 0);

		loading_text = (TextView) layout.findViewById(R.id.loading_text);

		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				popupHandler.sendEmptyMessage(MSG_Loading);
			}
		}, 0, 500);
		if (!boolpopupWindow) {
			popupWindow.dismiss();
			boolpopupWindow = true;
		}
	}

	private static final int MSG_Loading = 0;
	private static final int MSG_POPUP = 1;
	private int count = 0;
	private TextView loading_text;

	@SuppressLint("HandlerLeak")
	private final Handler popupHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_POPUP:
				popupwindowShowProgress();
				break;

			case MSG_Loading:
				count++;
				if (count > 3) {
					count = 1;
				}
				switch (count) {
				case 1:
					loading_text.setText("识别中.  ");
					break;
				case 2:
					loading_text.setText("识别中.. ");
					break;
				case 3:
					loading_text.setText("识别中...");
					break;
				}
				break;
			}
		}

	};

	/**
	 * 临时储存
	 */
	public String saveTemp(Bitmap bitmap, String cameraPathString) {

		File file = new File(cameraPathString);
		if (file.exists()) {
			file.delete();
		}
		try {
			FileOutputStream out = new FileOutputStream(file);
			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
			out.flush();
			out.close();
			if (!bitmap.isRecycled()) {
				bitmap.recycle(); // 回收图片所占的内存
				System.gc(); // 提醒系统及时回收
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return cameraPathString;
	}



	public static void showFailedDialogAndFinish(final Activity context) {
		new AlertDialog.Builder(context,R.style.AppBaseTheme)
				.setMessage(R.string.fail_to_contect_camcard)
				.setCancelable(false)
				.setPositiveButton(android.R.string.ok,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								context.finish();
							}
						}).create().show();
	}

	public static void saveBitmap(String copyfilename, Bitmap bm) {
		Log.e("saveBitmap", "保存图片");
		File f = new File(copyfilename);
		if (f.exists()) {
			f.delete();
		}
		try {
			FileOutputStream out = new FileOutputStream(f);
			bm.compress(Bitmap.CompressFormat.JPEG, 100, out);
			out.flush();
			out.close();
			Log.i("saveBitmap", "已经保存");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (bm != null && !bm.isRecycled()) {
				bm.recycle();
			}
		}

	}

	@SuppressWarnings("unused")
	private static Bitmap loadBitmap(byte[] data, float ww, float hh) {
		long starttime=System.currentTimeMillis();
		Log.e("loadBitmap", starttime+"");
		Bitmap b;
		try {
			BitmapFactory.Options opts = new BitmapFactory.Options();
			opts.inJustDecodeBounds = true;
			b = BitmapFactory.decodeByteArray(data, 0, data.length, opts);

			int originalWidth = opts.outWidth;
			int originalHeight = opts.outHeight;

			// float hh = 1280f;// 这里设置高度为800f
			// float ww = 720f;// 这里设置宽度为480f
			// 缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
			int be = 1;// be=1表示不缩放
			if (originalWidth > originalHeight && originalWidth > ww) {// 如果宽度大的话根据宽度固定大小缩放
				be = (int) (originalWidth / ww);
			} else if (originalWidth < originalHeight && originalHeight > hh) {// 如果高度高的话根据宽度固定大小缩放
				be = (int) (originalHeight / hh);
			}
			if (be <= 0)
				be = 1;
			Log.d("decodeFile", "originalWidth:" + originalWidth
					+ ",originalHeight:" + originalHeight + ",be:" + be);

			BitmapFactory.Options optso = new BitmapFactory.Options();
			optso.inJustDecodeBounds = false;
			optso.inPreferredConfig = Bitmap.Config.RGB_565;
			optso.inSampleSize = be;
			b = BitmapFactory.decodeByteArray(data, 0, data.length, optso);
		} catch (Exception e) {
			e.printStackTrace();
			b = null;
		} catch (OutOfMemoryError e) {
			e.printStackTrace();
			b = null;
		}
		Log.e("loadBitmap",(System.currentTimeMillis()- starttime)+"");

		return b;
	}
}
