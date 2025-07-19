package in.ecomexpress.sathi.ui.dummy.eds.eds_signature;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import com.google.gson.Gson;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Calendar;
import java.util.List;
import javax.inject.Inject;
import dagger.hilt.android.AndroidEntryPoint;
import in.ecomexpress.sathi.BR;
import in.ecomexpress.sathi.R;
import in.ecomexpress.sathi.databinding.ActivityEdsSignatureBinding;
import in.ecomexpress.sathi.repo.local.data.eds.EDSActivityResponseWizard;
import in.ecomexpress.sathi.repo.local.data.eds.EdsCommit;
import in.ecomexpress.sathi.repo.remote.RestApiErrorHandler;
import in.ecomexpress.sathi.repo.remote.model.drs_list.edsnew.EDSResponse;
import in.ecomexpress.sathi.ui.base.BaseActivity;
import in.ecomexpress.sathi.ui.dummy.eds.eds_success_fail.EDSSuccessFailActivity;
import in.ecomexpress.sathi.utils.BitmapUtils;
import in.ecomexpress.sathi.utils.CommonUtils;
import in.ecomexpress.sathi.utils.Constants;
import in.ecomexpress.sathi.utils.GlobalConstant;
import in.ecomexpress.sathi.utils.Helper;
import in.ecomexpress.sathi.utils.ImageHandler;
import in.ecomexpress.sathi.utils.NetworkUtils;
import in.ecomexpress.sathi.utils.PreferenceUtils;

@AndroidEntryPoint
public class EDSSignatureActivity extends BaseActivity<ActivityEdsSignatureBinding, EDSSignatureViewModel> implements IEDSSignatureNavigator {

    String TAG = EDSSignatureActivity.class.getCanonicalName();
    @Inject
    EDSSignatureViewModel signatureViewModel;
    @Inject
    EdsCommit edsCommit;
    ActivityEdsSignatureBinding activityRvpSignatureBinding;
    ImageHandler imageHandler;
    EDSResponse edsResponseCommit;
    Boolean isImageCaptured = false;

    // Blur Image Recognition Work:-
    public static int imageCaptureCount = 0;
    String awb = "", composite_key = null;
    Bitmap emptyBitmap, well;
    public List<EDSActivityResponseWizard> edsActivityResponseWizards;
    private EDSResponse edsResponse;
    private int meterRange;
    private boolean consigneeProfiling = false;
    ImageView mImageView;
    Bitmap mBitmap;
    Gson gson = new Gson();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        activityRvpSignatureBinding = getViewDataBinding();
        signatureViewModel.setNavigator(this);
        signatureViewModel.getDataManager().setLoginPermission(false);
        if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB){
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        }
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(getApplicationContext(), R.color.eds));
        }
        Constants.LOCATION_ACCURACY = signatureViewModel.getDataManager().getUndeliverConsigneeRANGE();
        awb = String.valueOf(getIntent().getExtras().getLong("awb", 0));
        composite_key = String.valueOf(getIntent().getExtras().getString(Constants.COMPOSITE_KEY, ""));
        edsResponseCommit = gson.fromJson(getIntent().getStringExtra("edsResponse"), EDSResponse.class);
        edsActivityResponseWizards = getIntent().getParcelableArrayListExtra(getString(R.string.data));
        signatureViewModel.setedsCommit(edsResponseCommit);
        imageHandler = new ImageHandler(EDSSignatureActivity.this) {
            @Override
            public void onBitmapReceived(Bitmap bitmap, String imageUri, ImageView imgView, String imageName, String imageCode, int pos, boolean verifyImage){
                // Blur Image Recognition Using Laplacian Variance:-
                if(CommonUtils.checkImageIsBlurryOrNot(EDSSignatureActivity.this, "EDS", bitmap, imageCaptureCount, signatureViewModel.getDataManager())){
                    imageCaptureCount++;
                }
                else{
                    if(imgView != null)
                        isImageCaptured = true;
                    mImageView = imgView;
                    mBitmap = bitmap;
                    if(signatureViewModel.getDataManager().getEDSRealTimeSync().equalsIgnoreCase("true") && NetworkUtils.isNetworkConnected(EDSSignatureActivity.this)){
                        signatureViewModel.uploadImageServerImage(edsResponseCommit.getAwbNo() + "_" + edsResponseCommit.getDrsNo() + "_Image.png", imageUri, "Image", edsResponseCommit.getAwbNo(), edsResponseCommit.getDrsNo(), bitmap);
                    } else{
                        imgView.setImageBitmap(bitmap);
                        signatureViewModel.uploadAWSImage(imageUri, "Image", edsResponseCommit.getAwbNo() + "_" + edsResponseCommit.getDrsNo() + "_Image.png", false, 0);
                    }
                }
            }
        };
        Log.d(TAG, "onCreate: ");
        edsResponseCommit.setCompositeKey(String.valueOf(edsResponseCommit.getCompositeKey()));
        activityRvpSignatureBinding.consigneeAdd.setMovementMethod(new ScrollingMovementMethod());
        activityRvpSignatureBinding.consigneeName.setMovementMethod(new ScrollingMovementMethod());
        signatureViewModel.fetchEDSShipment(edsResponseCommit.getCompositeKey());
        signatureViewModel.getConsigneeProfiling();
    }

    public static Intent getStartIntent(Context context){
        return new Intent(context, EDSSignatureActivity.class);
    }

    @Override
    public EDSSignatureViewModel getViewModel(){
        return signatureViewModel;
    }

    @Override
    public int getBindingVariable(){
        return BR.viewModel;
    }

    @Override
    public int getLayoutId(){
        return R.layout.activity_eds_signature;
    }

    @Override
    public void onCaptureImage(){
        if(!CommonUtils.isAllPermissionAllow(this)){
            openSettingActivity();
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this,R.style.Base_ThemeOverlay_AppCompat_Dialog_Alert);
        new Thread(new Runnable() {
            @Override
            public void run(){
                try{
                    Thread.sleep(1000);
                } catch(Exception e){
                }
                //                intitialView = activityRvpSignatureBinding.signature.getBitmap();
            }
        }).start();
        String AlertText1 = "Attention : ";
        builder.setMessage(AlertText1 + getString(R.string.alert)).setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id){
                //do things
                imageHandler.captureImage(edsResponseCommit.getAwbNo() + "_image.png", activityRvpSignatureBinding.image, "Signature");
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public void saveSignature(){
        edsCommit.setOfd_customer_otp(Constants.PLAIN_OTP);
        edsCommit.setOfd_otp_verified(String.valueOf(Constants.OFD_OTP_VERIFIED));
        edsCommit.setCall_attempt_count(signatureViewModel.getDataManager().getEDSCallCount(awb + "EDS"));
        edsCommit.setMap_activity_count(signatureViewModel.getDataManager().getEDSMapCount(Long.parseLong(awb)));
        edsCommit.setTrying_reach(String.valueOf(signatureViewModel.getDataManager().getTryReachingCount(String.valueOf(awb+Constants.TRY_RECHING_COUNT))));
        edsCommit.setTechpark(String.valueOf(signatureViewModel.getDataManager().getSendSmsCount(String.valueOf(awb+Constants.TECH_PARK_COUNT))));
        Calendar calendar = Calendar.getInstance();
        int mDay = calendar.get(Calendar.DAY_OF_MONTH);
        int mMonth = calendar.get(Calendar.MONTH) + 1;
        if(signatureViewModel.loginDate().equalsIgnoreCase(String.valueOf(mDay)) && signatureViewModel.getDataManager().getLoginMonth() == mMonth){
            if(NetworkUtils.isNetworkConnected(EDSSignatureActivity.this)){
                if(Constants.IS_CASH_COLLECTION_ENABLE){
                    double value = Constants.EDS_CASH_COLLECTION + Double.parseDouble(PreferenceUtils.getSharedPreferences(getApplication().getApplicationContext()).getString("EDSCASHCOLLECTION", "0"));
                    PreferenceUtils.writePreferenceValue(EDSSignatureActivity.this, "EDSCASHCOLLECTION", String.valueOf(value));
                    saveBitmapInFile();
                } else{
                    // signatureViewModel.createCommitPacket(edsCommit, edsResponseCommit, edsActivityResponseWizards);
                    saveBitmapInFile();
                }
            } else{
                saveBitmapInFile();
            }
        } else{
            AlertDialog.Builder builder = new AlertDialog.Builder(this,R.style.Base_ThemeOverlay_AppCompat_Dialog_Alert);
            String AlertText1 = "Attention : ";
            builder.setMessage(AlertText1 + getString(R.string.commit_restriction_msg)).setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id){
                    dialog.cancel();
                    startActivityForResult(new Intent(android.provider.Settings.ACTION_DATE_SETTINGS), 0);
                }
            });
            AlertDialog alert = builder.create();
            alert.show();
        }
    }

    @Override
    public void submitErrorAlert(String message){
        showSnackbar(message);
    }

    @Override
    public void onbackclick(){
        super.onBackPressed();
        // showSnackbar(getString(R.string.cannot_go_back));
    }

    @Override
    public void onclear(){
        activityRvpSignatureBinding.signature.clear();
    }

    @Override
    public void onBack(){
        super.onBackPressed();
        // showSnackbar(getString(R.string.cannot_go_back));
    }

    @Override
    public void onHandleError(String errorResponse){
        showSnackbar(errorResponse);
    }

    @Override
    public void openSuccess(){
        openSuccessActivity();
    }

    @Override
    public void setConsigneeDistance(int meter){
        this.meterRange = meter;
    }

    @Override
    public void onEDSItemFetched(EDSResponse edsResponse){
        this.edsResponse = edsResponse;
        signatureViewModel.checkMeterRange(edsResponse);
    }

    @Override
    public void commiterror(String errorResponse){
        showSnackbar(errorResponse);
    }

    @Override
    protected void onResume(){
        super.onResume();
        new Thread() {
            public void run(){
                try{
                    sleep(1000);
                } catch(InterruptedException e){
                    //   SathiLogger.e(e.getMessage());
                    e.printStackTrace();
                }
            }
        }.start();
    }

    private void openSuccessActivity(){
        Helper.updateLocationWithData(EDSSignatureActivity.this, edsCommit.getAwb(), edsCommit.getStatus());
        Constants.SCANNED_DATA = "Not Found";
        Intent intent = EDSSuccessFailActivity.getStartIntent(this);
        intent.putExtra(Constants.INTENT_KEY, edsResponseCommit.getAwbNo());
        intent.putExtra("edsResponseCommit", gson.toJson(edsResponseCommit));
        intent.putExtra(Constants.DECIDENEXT, Constants.SUCCESS);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        imageHandler.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void setConsingeeProfiling(boolean enable){
        this.consigneeProfiling = enable;
    }

    @Override
    public void callCommit(String image_id, String image_key){
        try{
            signatureViewModel.createCommitPacketCashCollection(image_id, image_key, edsCommit, edsResponseCommit, edsActivityResponseWizards);
        } catch(Exception e){
            e.printStackTrace();
            signatureViewModel.createCommitPacketCashCollection(image_id, image_key, edsCommit, edsResponseCommit, edsActivityResponseWizards);
        }
    }

    @Override
    public void setBitmap(){
        mImageView.setImageBitmap(mBitmap);
    }

    private Boolean saveBitmapInFile(){
        try{
            File fileDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "/" + Constants.EcomExpress);
            if(!fileDir.exists())
                fileDir.mkdirs();
            File file = new File(fileDir, edsResponseCommit.getAwbNo() + "_" + edsResponseCommit.getDrsNo() + "_signature.png");
            if(!file.exists())
                file.createNewFile();
            try(FileOutputStream ostream = new FileOutputStream(file)){
                well = activityRvpSignatureBinding.signature.getBitmap();
                emptyBitmap = Bitmap.createBitmap(well.getWidth(), well.getHeight(), well.getConfig());
                if(well.sameAs(emptyBitmap)){
                    showSnackbar("Please place signature.");
                    ostream.close();
                    return false;
                } else{
                    if(signatureViewModel.getCounterDeliveryRange() < signatureViewModel.getDataManager().getDCRANGE()){
                        showSnackbar("Shipment cannot be marked delivered within the DC");
                        return false;
                    }
                    if(Constants.CONSIGNEE_PROFILE){
                        String dialog_message = getString(R.string.commitdialog);
                        String positiveButtonText = getString(R.string.yes);
                        if(consigneeProfiling && meterRange > signatureViewModel.getDataManager().getUndeliverConsigneeRANGE() && signatureViewModel.getDataManager().getConsigneeProfileValue().equalsIgnoreCase("W")){
                            dialog_message = "You are not attempting the shipment at Consignee’s location. Your current location = " + signatureViewModel.getDataManager().getCurrentLatitude() + ", " + signatureViewModel.getDataManager().getCurrentLongitude() + " You are " + meterRange + " meter away from consignee location, \nAre you sure you want to commit?";
                            //dialog_message = getString(R.string.commitdialog_meter_away);
                            positiveButtonText = getString(R.string.yes);
                        } else if((!consigneeProfiling) && meterRange > signatureViewModel.getDataManager().getUndeliverConsigneeRANGE() && signatureViewModel.getDataManager().getConsigneeProfileValue().equalsIgnoreCase("R")){
                            dialog_message = "You are not allowed to commit this shipment as you are not attempting at consignee location. your current location = " + signatureViewModel.getDataManager().getCurrentLatitude() + ", " + signatureViewModel.getDataManager().getCurrentLongitude() + "You are " + meterRange + " meter away from consignee location";
                            //dialog_message = getString(R.string.commitdialog_meter_away_mandatory);
                            positiveButtonText = getString(R.string.ok);
                        } else{
                            BitmapUtils.saveBitmap(file, well);
                            try{
                                if(NetworkUtils.isNetworkConnected(EDSSignatureActivity.this)){
                                    if(signatureViewModel.getDataManager().getEDSRealTimeSync().equalsIgnoreCase("true") && NetworkUtils.isNetworkConnected(EDSSignatureActivity.this)){
                                        if(Constants.IS_CASH_COLLECTION_ENABLE)
                                            signatureViewModel.uploadImageServer(edsResponseCommit.getAwbNo() + "_" + edsResponseCommit.getDrsNo() + "_signature.png", file.getAbsolutePath(), "signature", edsResponseCommit.getAwbNo(), edsResponseCommit.getDrsNo(), "", activityRvpSignatureBinding.signature.getBitmap());
                                        else{
                                            signatureViewModel.uploadAWSImage(file.getAbsolutePath(), "signature", edsResponseCommit.getAwbNo() + "_" + edsResponseCommit.getDrsNo() + "_signature.png", true, 0);
                                        }
                                    } else{
                                        signatureViewModel.uploadAWSImage(file.getAbsolutePath(), "signature", edsResponseCommit.getAwbNo() + "_" + edsResponseCommit.getDrsNo() + "_signature.png", true, 0);
                                    }
                                } else{
                                    signatureViewModel.uploadAWSImage(file.getAbsolutePath(), "signature", edsResponseCommit.getAwbNo() + "_" + edsResponseCommit.getDrsNo() + "_signature.png", true, 0);
                                }
                            } catch(Exception e){
                                RestApiErrorHandler restApiErrorHandler = new RestApiErrorHandler(e.getCause());
                                restApiErrorHandler.writeErrorLogs(0, e.getMessage());
                                e.printStackTrace();
                            }
                            return true;
                        }
                        AlertDialog.Builder builder = new AlertDialog.Builder(this,R.style.Base_ThemeOverlay_AppCompat_Dialog_Alert);
                        builder.setCancelable(false);
                        builder.setMessage(dialog_message);
                        if(meterRange > signatureViewModel.getDataManager().getUndeliverConsigneeRANGE()){
                            edsCommit.setLocation_verified(false);
                        } else{
                            edsCommit.setLocation_verified(true);
                        }
                        edsCommit.setLocation_verified(false);
                        builder.setPositiveButton(positiveButtonText, new DialogInterface.OnClickListener() {
                            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                            @Override
                            public void onClick(DialogInterface dialog, int which){
                                //if user pressed "yes", then he is allowed to exit from application
                                if(consigneeProfiling && meterRange > Constants.CONSIGNEE_PROFILING_METER_RANGE && signatureViewModel.getDataManager().getConsigneeProfileValue().equalsIgnoreCase("R")){
                                    dialog.cancel();
                                    return;
                                }
                                if(signatureViewModel.getDataManager().getConsigneeProfileValue().equalsIgnoreCase("W") || signatureViewModel.getDataManager().getConsigneeProfileValue().equalsIgnoreCase("N")){
                                    BitmapUtils.saveBitmap(file, well);
                                    if(NetworkUtils.isNetworkConnected(EDSSignatureActivity.this)){
                                        if(signatureViewModel.getDataManager().getEDSRealTimeSync().equalsIgnoreCase("true") && NetworkUtils.isNetworkConnected(EDSSignatureActivity.this)){
                                            if(Constants.IS_CASH_COLLECTION_ENABLE)
                                                signatureViewModel.uploadImageServer(edsResponseCommit.getAwbNo() + "_" + edsResponseCommit.getDrsNo() + "_signature.png", file.getAbsolutePath(), "signature", edsResponseCommit.getAwbNo(), edsResponseCommit.getDrsNo(), "", activityRvpSignatureBinding.signature.getBitmap());
                                            else{
                                                signatureViewModel.uploadAWSImage(file.getAbsolutePath(), "signature", edsResponseCommit.getAwbNo() + "_" + edsResponseCommit.getDrsNo() + "_signature.png", true, 0);
                                            }
                                        } else{
                                            signatureViewModel.uploadAWSImage(file.getAbsolutePath(), "signature", edsResponseCommit.getAwbNo() + "_" + edsResponseCommit.getDrsNo() + "_signature.png", true, GlobalConstant.ImageSyncStatus.IMAGE_SYNC_STATUS_NO);
                                        }
                                    } else{
                                        signatureViewModel.uploadAWSImage(file.getAbsolutePath(), "signature", edsResponseCommit.getAwbNo() + "_" + edsResponseCommit.getDrsNo() + "_signature.png", true, GlobalConstant.ImageSyncStatus.IMAGE_SYNC_STATUS_NO);
                                    }
                                } else{
                                    dialog.cancel();
                                }
                            }
                        });
                        if(!(consigneeProfiling && meterRange > signatureViewModel.getDataManager().getUndeliverConsigneeRANGE())){
                            builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which){
                                    dialog.cancel();
                                }
                            });
                        }
                        AlertDialog alert = builder.create();
                        alert.show();
                        return true;
                    } else{
                        edsCommit.setLocation_verified(meterRange <= signatureViewModel.getDataManager().getUndeliverConsigneeRANGE());
                        BitmapUtils.saveBitmap(file, well);
                        try{
                            if(NetworkUtils.isNetworkConnected(EDSSignatureActivity.this)){
                                if(signatureViewModel.getDataManager().getEDSRealTimeSync().equalsIgnoreCase("true") && NetworkUtils.isNetworkConnected(EDSSignatureActivity.this)){
                                    if(Constants.IS_CASH_COLLECTION_ENABLE)
                                        signatureViewModel.uploadImageServer(edsResponseCommit.getAwbNo() + "_" + edsResponseCommit.getDrsNo() + "_signature.png", file.getAbsolutePath(), "signature", edsResponseCommit.getAwbNo(), edsResponseCommit.getDrsNo(), "", activityRvpSignatureBinding.signature.getBitmap());
                                    else{
                                        signatureViewModel.uploadAWSImage(file.getAbsolutePath(), "signature", edsResponseCommit.getAwbNo() + "_" + edsResponseCommit.getDrsNo() + "_signature.png", true, 0);
                                    }
                                } else{
                                    signatureViewModel.uploadAWSImage(file.getAbsolutePath(), "signature", edsResponseCommit.getAwbNo() + "_" + edsResponseCommit.getDrsNo() + "_signature.png", true, GlobalConstant.ImageSyncStatus.IMAGE_SYNC_STATUS_NO);
                                }
                            } else{
                                signatureViewModel.uploadAWSImage(file.getAbsolutePath(), "signature", edsResponseCommit.getAwbNo() + "_" + edsResponseCommit.getDrsNo() + "_signature.png", true, GlobalConstant.ImageSyncStatus.IMAGE_SYNC_STATUS_NO);
                            }
                        } catch(Exception e){
                            RestApiErrorHandler restApiErrorHandler = new RestApiErrorHandler(e.getCause());
                            restApiErrorHandler.writeErrorLogs(0, e.getMessage());
                            e.printStackTrace();
                        }
                        return true;
                    }
                }
            }
        } catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        // showSnackbar(getString(R.string.cannot_go_back));
    }

    @Override
    protected void onPause(){
        super.onPause();
        well = activityRvpSignatureBinding.signature.getBitmap();
        if(well != null){
            Log.d(TAG, "onPause: " + well);
        } else{
            Log.d(TAG, "onPause: " + null);
        }
    }

    @Override
    protected void onStop(){
        super.onStop();
        well = activityRvpSignatureBinding.signature.getBitmap();
        if(well != null){
            Log.d(TAG, "onStop: " + well);
        } else{
            Log.d(TAG, "onStop: " + null);
        }
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        Log.d(TAG, "onDestroy: ");
    }
}
