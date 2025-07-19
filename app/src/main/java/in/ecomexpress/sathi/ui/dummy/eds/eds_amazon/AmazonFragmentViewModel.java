package in.ecomexpress.sathi.ui.dummy.eds.eds_amazon;


import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.google.gson.Gson;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import java.nio.charset.StandardCharsets;
import java.security.PrivateKey;
import java.security.Signature;
import java.util.Base64;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import in.ecomexpress.sathi.R;
import in.ecomexpress.sathi.repo.IDataManager;
import in.ecomexpress.sathi.ui.base.BaseViewModel;
import in.ecomexpress.sathi.utils.rx.ISchedulerProvider;

@HiltViewModel
public class AmazonFragmentViewModel extends BaseViewModel<AmazonFragmentNavigation> {

    @Inject
    public AmazonFragmentViewModel(IDataManager dataManager, ISchedulerProvider schedulerProvider, Application sathiApplication) {
        super(dataManager, schedulerProvider, sathiApplication);
    }


    public void generateBitmap(String Value, Context context) {
        new AsyncTask<Void, Void, Bitmap>() {
            Bitmap bitmap = null;

            @Override
            protected Bitmap doInBackground(Void... voids) {
                try {
                    setIsLoading(true);
                    bitmap = textToImageEncode(Value, context);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return bitmap;
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                super.onPostExecute(bitmap);
                setIsLoading(false);
                getNavigator().setQRCode(bitmap);

            }
        }.execute();
    }


    public Bitmap textToImageEncode(String Value, Context context) throws WriterException {
        BitMatrix bitMatrix;
        try {
            bitMatrix = new MultiFormatWriter().encode(
                    Value, BarcodeFormat.QR_CODE,
                    500, 500, null
            );

        } catch (IllegalArgumentException Illegalargumentexception) {

            return null;
        }
        int bitMatrixWidth = bitMatrix.getWidth();

        int bitMatrixHeight = bitMatrix.getHeight();

        int[] pixels = new int[bitMatrixWidth * bitMatrixHeight];

        for (int y = 0; y < bitMatrixHeight; y++) {
            int offset = y * bitMatrixWidth;

            for (int x = 0; x < bitMatrixWidth; x++) {

                pixels[offset + x] = bitMatrix.get(x, y) ?
                        context.getResources().getColor(R.color.black) : context.getResources().getColor(R.color.white);
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(bitMatrixWidth, bitMatrixHeight, Bitmap.Config.ARGB_4444);

        bitmap.setPixels(pixels, 0, 500, 0, 0, bitMatrixWidth, bitMatrixHeight);
        return bitmap;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public String generateHash(String plainText) throws Exception {
        Gson gson = new Gson();
        PrivateKey privateKey = gson.fromJson(getDataManager().getPrivateKey(), PrivateKey.class);
        Signature privateSignature = Signature.getInstance("SHA256withRSA");
        privateSignature.initSign(privateKey);
        privateSignature.update(plainText.getBytes(StandardCharsets.UTF_8));

        byte[] signature = privateSignature.sign();
        return Base64.getEncoder().encodeToString(signature);
    }

//    public void fadeQRCode(Context context , Bitmap bitmap , ImageView imageView) {
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                Blurry.with(context).from(bitmap).into(imageView);
//            }
//        } ,1000);
//    }
}
