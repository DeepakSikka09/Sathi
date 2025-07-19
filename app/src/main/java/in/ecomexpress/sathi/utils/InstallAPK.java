package in.ecomexpress.sathi.utils;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import in.ecomexpress.sathi.ui.dummy.eds.paytm.IPaytmFragmentNavigator;

import static in.ecomexpress.sathi.ui.dummy.eds.paytm.PaytmFragment.progress_bar_type;


public class InstallAPK extends AsyncTask<String, String, String> {

        ProgressDialog progressDialog;
        int status = 0;

private Context context;

public void setContext(Context context, ProgressDialog progress) {
        this.context = context;
        this.progressDialog = progress;
        }

public void onPreExecute() {
        progressDialog.show();

        }

  /*  protected void onProgressUpdate(String... progress) {
        // setting progress percentage
        progressDialog.setProgress(Integer.parseInt(progress[0]));
    }*/

    @Override
    protected String doInBackground(String... arg0) {
        int len1 = 0;
        try {
            URL url = new URL(arg0[0]);
            HttpURLConnection c = (HttpURLConnection) url.openConnection();
            c.setRequestMethod("GET");
            c.setDoOutput(true);
            c.connect();
            // getting file length
            int lenghtOfFile = c.getContentLength();
            // input stream to read file - with 8k buffer
            //   InputStream input = new BufferedInputStream(url.openStream(), 8192);

            File sdcard = Environment.getExternalStorageDirectory();
            File myDir = new File(sdcard, "Android/data/paytm");
            myDir.mkdirs();
            File outputFile = new File(myDir, "paytm.apk");
            if (outputFile.exists()) {
                outputFile.delete();
            }
            FileOutputStream fos = new FileOutputStream(outputFile);

            InputStream is = c.getInputStream();

            byte[] buffer = new byte[1024];

            long total = 0;
            while ((len1 = is.read(buffer)) != -1) {
                total += len1;
                // publishing the progress....
                // After this onProgressUpdate will be called
                publishProgress("" + (int) ((total * 100) / lenghtOfFile));
                fos.write(buffer, 0, len1);
            }
            fos.flush();
            fos.close();
            is.close();

            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.fromFile(new File(sdcard, "Android/data/paytm/paytm.apk")), "application/vnd.android.package-archive");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // without this flag android returned a intent error!
            context.startActivity(intent);
          //  publishProgress((int) (((i+1) / (float) count) * 100));

        } catch (FileNotFoundException fnfe) {
            // SathiLogger.e(fnfe.getMessage());
            status = 1;
            // Log.e("File", "FileNotFoundException! " + fnfe);
        } catch (Exception e) {
            // SathiLogger.e(e.getMessage());
            Log.e("UpdateAPP", "Exception " + e);
        }
        return null;
    }

protected void onProgressUpdate(String... progress) {
        Log.d("ANDRO_ASYNC", progress[0]);
        progressDialog.setProgress(Integer.parseInt(progress[0]));
        }

@Override
protected void onPostExecute(String unused) {
        progressDialog.dismiss();



        }
        }