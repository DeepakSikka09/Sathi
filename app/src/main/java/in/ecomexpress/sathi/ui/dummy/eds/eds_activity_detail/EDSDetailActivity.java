package in.ecomexpress.sathi.ui.dummy.eds.eds_activity_detail;

import static in.ecomexpress.sathi.utils.Constants.INTENT_KEY_EDS_MASTER_LIST;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.viewpager.widget.ViewPager;

import com.google.gson.Gson;

import org.json.JSONObject;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;
import java.io.StringWriter;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import javax.inject.Inject;
import dagger.hilt.android.AndroidEntryPoint;
import fr.arnaudguyon.xmltojsonlib.XmlToJson;
import in.ecomexpress.sathi.BR;
import in.ecomexpress.sathi.R;
import in.ecomexpress.sathi.databinding.ActivityEdsDetailBinding;
import in.ecomexpress.sathi.repo.local.data.eds.EDSActivityResponseWizard;
import in.ecomexpress.sathi.repo.local.data.eds.EdsCommit;
import in.ecomexpress.sathi.repo.local.db.model.EdsWithActivityList;
import in.ecomexpress.sathi.repo.remote.model.drs_list.edsnew.EDSActivityWizard;
import in.ecomexpress.sathi.repo.remote.model.masterdata.MasterActivityData;
import in.ecomexpress.sathi.ui.base.BaseActivity;
import in.ecomexpress.sathi.ui.base.BaseFragment;
import in.ecomexpress.sathi.ui.drs.forward.details.ScannerActivity;
import in.ecomexpress.sathi.ui.dummy.eds.ac_document_list_collection.AcDocumentListCollectionFragment;
import in.ecomexpress.sathi.ui.dummy.eds.capture_image.CaptureImageFragment;
import in.ecomexpress.sathi.ui.dummy.eds.cash_collection.CashCollectionFragment;
import in.ecomexpress.sathi.ui.dummy.eds.document_list_collection.DocumentListCollectionFragment;
import in.ecomexpress.sathi.ui.dummy.eds.eds_bkyc_idfc.EdsBkycIdfcFragment;
import in.ecomexpress.sathi.ui.dummy.eds.eds_document_collection.DocumentCollectionFragment;
import in.ecomexpress.sathi.ui.dummy.eds.eds_document_verification.DocumentVerificationFragment;
import in.ecomexpress.sathi.ui.dummy.eds.eds_ekyc_XML.EdsEkycXMLFragment;
import in.ecomexpress.sathi.ui.dummy.eds.eds_ekyc_idfc.EdsEkycIdfcFragment;
import in.ecomexpress.sathi.ui.dummy.eds.eds_ekyc_niyo.EdsEkycNiyoFragment;
import in.ecomexpress.sathi.ui.dummy.eds.eds_ekyc_rbl.EdsRblFragment;
import in.ecomexpress.sathi.ui.dummy.eds.eds_opv.OpvFragment;
import in.ecomexpress.sathi.ui.dummy.eds.eds_res_opv.ResOpvFragment;
import in.ecomexpress.sathi.ui.dummy.eds.eds_scan.CaptureScanActivity;
import in.ecomexpress.sathi.ui.dummy.eds.eds_signature.EDSSignatureActivity;
import in.ecomexpress.sathi.ui.dummy.eds.eds_task_list.EdsTaskListActivity;
import in.ecomexpress.sathi.ui.dummy.eds.edsantwork.EdsEkycAntWorkFragment;
import in.ecomexpress.sathi.ui.dummy.eds.ekyc_freyo.EdsEkycFreyoFragment;
import in.ecomexpress.sathi.ui.dummy.eds.ekyc_paytm.EdsEkycPaytmFragment;
import in.ecomexpress.sathi.ui.dummy.eds.icic_standard.IciciEkycFragment_standard;
import in.ecomexpress.sathi.ui.dummy.eds.icici_ekyc.IciciEkycFragment;
import in.ecomexpress.sathi.ui.dummy.eds.paytm.PaytmFragment;
import in.ecomexpress.sathi.ui.dummy.eds.uid.DeviceInfo;
import in.ecomexpress.sathi.ui.dummy.eds.uid.Opts;
import in.ecomexpress.sathi.ui.dummy.eds.uid.Param;
import in.ecomexpress.sathi.ui.dummy.eds.uid.PidOptions;
import in.ecomexpress.sathi.ui.dummy.eds.undeilvered_eds.EDSUndeliveredActivity;
import in.ecomexpress.sathi.ui.dummy.eds.vodafone.VodafoneFragment;
import in.ecomexpress.sathi.utils.CommonUtils;
import in.ecomexpress.sathi.utils.Constants;
import in.ecomexpress.sathi.utils.DigitalCropImageHandler;
import in.ecomexpress.sathi.utils.GlobalConstant;
import in.ecomexpress.sathi.utils.ImageHandler;

@AndroidEntryPoint
public class EDSDetailActivity extends BaseActivity<ActivityEdsDetailBinding, EDSDetailViewModel> implements IEDSDetailNavigator, ViewPager.OnPageChangeListener {

    private static final String TAG = EDSDetailActivity.class.getSimpleName();
    public static EDSDetailActivity edsDetailActivity;
    private static final int CAMERA_SCANNER_CODE = 1002;
    private final int REQUEST_CODE_SCAN = 1101;
    public ImageHandler imageHandler;
    public DigitalCropImageHandler digitalCropImageHandler;
    public LinkedList<EDSActivityResponseWizard> edsActivityResponseWizards = new LinkedList<>();
    @Inject
    EDSDetailViewModel edsDetailViewModel;
    EDSDetailPagerAdapter edsDetailPagerAdapter;
    int activityNameCount = 0;
    // Blur Image Recognition Work:-
    public static int imageCaptureCount = 0;
    Fragment fragment;
    EdsWithActivityList edsWithActivityList;
    ArrayList<MasterActivityData> masterActivityData;
    ActivityData activityData;
    DocumentData documentData;
    Long awbNo;
    String order_id = "";
    int drs_no;
    LinkedHashSet<String> stageCountActivity = new LinkedHashSet<>();
    ArrayList<String> stagerunningCount = new ArrayList<>();
    List<String> runningCount = new ArrayList<>();
    int dccount = 0, dvcount = 0, acount = 0;
    int stageCount = 0;
    String getDrsApiKey = null, getDrsPstnKey = null, getDrsPin = null, composite_key = null;
    Bitmap quality_bitmap;
    ImageView qualilty_image;
    boolean reschedule_enable;
    ProgressDialog progress, progress1;
    ArrayList<Integer> arrayList_image_validation;
    private ActivityEdsDetailBinding activityEdsDetailBinding;
    private final LinkedHashMap<BaseFragment, Boolean> successFragment = new LinkedHashMap<>();
    private int count = 0;
    Bitmap mBitmap;
    ImageView pimgView;
    String front_image_uri = "", rear_image_uri = "";
    String front_image_code, rear_image_code;
    String front_image_name, rear_image_name;
    DeviceInfo info;
    String idc;
    String rdsId;
    String rdsVer;
    String dpId;
    String dc;
    String mi;
    String mc;
    List<String> value;
    JSONObject pidDataJson;
    private ArrayList<String> positions;
    private Serializer serializer = null;
    String spinner_code_value;
    private String card_type = "NONE";
    Gson gson = new Gson();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            edsDetailActivity = this;
            edsDetailViewModel.setNavigator(this);
            arrayList_image_validation = new ArrayList<>();
            activityEdsDetailBinding = getViewDataBinding();
            LocalBroadcastManager.getInstance(this).registerReceiver(listener, new IntentFilter("SET_SIZE"));
            activityEdsDetailBinding.qcViewPager.addOnPageChangeListener(this);

            setUp1();
            initilize();
            positions = new ArrayList<>();
            serializer = new Persister();
            awbNo = getIntent().getLongExtra(Constants.INTENT_KEY, 0);
            drs_no = (int) Constants.TEMP_DRSID;
            if (getIntent().getExtras().getString(Constants.ORDER_ID) != null) {
                order_id = getIntent().getExtras().getString(Constants.ORDER_ID);
            }
            getDrsApiKey = getIntent().getExtras().getString(Constants.DRS_API_KEY);
            getDrsPstnKey = getIntent().getExtras().getString(Constants.DRS_PSTN_KEY);
            reschedule_enable = getIntent().getBooleanExtra(Constants.RESCHEDULE_ENABLE, false);
            getDrsPin = getIntent().getExtras().getString(Constants.DRS_PIN);
            composite_key = getIntent().getExtras().getString(Constants.COMPOSITE_KEY);
            edsWithActivityList = gson.fromJson(getIntent().getStringExtra(Constants.INTENT_KEY_EDS_WITH_ACTIVITY), EdsWithActivityList.class);
            masterActivityData = getIntent().getParcelableArrayListExtra(INTENT_KEY_EDS_MASTER_LIST);
            edsDetailViewModel.setData(edsWithActivityList, masterActivityData);
            edsDetailPagerAdapter = new EDSDetailPagerAdapter(this.getSupportFragmentManager(),edsWithActivityList,masterActivityData,edsDetailViewModel);
            updateViewPager(edsWithActivityList, masterActivityData);
            activityEdsDetailBinding.consigneeAddTv.setMovementMethod(new ScrollingMovementMethod());
            activityEdsDetailBinding.consigneeName.setMovementMethod(new ScrollingMovementMethod());
            setUp();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setUp() {
        imageHandler = new ImageHandler(this) {
            @Override
            public void onBitmapReceived(Bitmap bitmap, String imageUri, ImageView imgView, String imageName, String imageCode, int pos, boolean verifyImage) {
                // Blur Image Recognition Using Laplacian Variance:-
                runOnUiThread(() -> {
                    try {
                        if (CommonUtils.checkImageIsBlurryOrNot(EDSDetailActivity.this, "EDS", bitmap, imageCaptureCount, edsDetailViewModel.getDataManager())) {
                            imageCaptureCount++;
                        } else {
                            pimgView = imgView;
                            mBitmap = bitmap;
                            fragment = edsDetailPagerAdapter.getItem(activityEdsDetailBinding.qcViewPager.getCurrentItem());
                            if (fragment instanceof DocumentVerificationFragment) {
                                quality_bitmap = bitmap;
                                qualilty_image = imgView;
                                DocumentVerificationFragment documentVerificationFragment = (DocumentVerificationFragment) edsDetailPagerAdapter.getRegisteredFragment(activityEdsDetailBinding.qcViewPager.getCurrentItem());
                                activityData = documentVerificationFragment;
                                documentData = documentVerificationFragment;
                                if (imageCode.equalsIgnoreCase("AADHAR_FRONT_IMAGE")) {
                                    imgView.setImageBitmap(bitmap);
                                    front_image_uri = imageUri;
                                    front_image_code = documentData.getFrontImageCode();
                                    front_image_name = imageName;
                                } else if (imageCode.equalsIgnoreCase("AADHAR_REAR_IMAGE")) {
                                    imgView.setImageBitmap(bitmap);
                                    rear_image_uri = imageUri;
                                    rear_image_code = documentData.getRearImageCode();
                                    rear_image_name = imageName;
                                } else {
                                    quality_bitmap = bitmap;
                                    qualilty_image = imgView;
                                    upLoadImage(imageName, imageUri, imageCode, awbNo, drs_no, edsWithActivityList.getEdsActivityWizards().get(activityEdsDetailBinding.qcViewPager.getCurrentItem()).code, bitmap, false, card_type);
                                }
                            } else if (fragment instanceof DocumentCollectionFragment) {
                                quality_bitmap = bitmap;
                                qualilty_image = imgView;
                                DocumentCollectionFragment documentCollectionFragment = (DocumentCollectionFragment) edsDetailPagerAdapter.getRegisteredFragment(activityEdsDetailBinding.qcViewPager.getCurrentItem());
                                documentCollectionFragment.showMessage(DocumentCollectionFragment.class.getSimpleName());
                                activityData = documentCollectionFragment;
                                documentData = documentCollectionFragment;
                                if (imageCode.equalsIgnoreCase("AADHAR_FRONT_IMAGE")) {
                                    imgView.setImageBitmap(bitmap);
                                    front_image_uri = imageUri;
                                    front_image_code = documentData.getFrontImageCode();
                                    front_image_name = imageName;
                                } else if (imageCode.equalsIgnoreCase("AADHAR_REAR_IMAGE")) {
                                    imgView.setImageBitmap(bitmap);
                                    rear_image_uri = imageUri;
                                    rear_image_code = documentData.getRearImageCode();
                                    rear_image_name = imageName;
                                } else {
                                    quality_bitmap = bitmap;
                                    qualilty_image = imgView;
                                    upLoadImage(imageName, imageUri, imageCode, awbNo, drs_no, edsWithActivityList.getEdsActivityWizards().get(activityEdsDetailBinding.qcViewPager.getCurrentItem()).code, bitmap, false, card_type);
                                }
                            } else if (fragment instanceof DocumentListCollectionFragment) {
                                DocumentListCollectionFragment documentListCollectionFragment = (DocumentListCollectionFragment) edsDetailPagerAdapter.getRegisteredFragment(activityEdsDetailBinding.qcViewPager.getCurrentItem());
                                documentListCollectionFragment.showMessage(DocumentListCollectionFragment.class.getSimpleName());
                                activityData = documentListCollectionFragment;
                                documentData = documentListCollectionFragment;
                                if (imageCode.equalsIgnoreCase("AADHAR_FRONT_IMAGE")) {
                                    imgView.setImageBitmap(bitmap);
                                    front_image_uri = imageUri;
                                    front_image_code = documentData.getFrontImageCode();
                                    front_image_name = imageName;
                                } else if (imageCode.equalsIgnoreCase("AADHAR_REAR_IMAGE")) {
                                    imgView.setImageBitmap(bitmap);
                                    rear_image_uri = imageUri;
                                    rear_image_code = documentData.getRearImageCode();
                                    rear_image_name = imageName;
                                } else {
                                    quality_bitmap = bitmap;
                                    qualilty_image = imgView;
                                    upLoadImage(imageName, imageUri, imageCode, awbNo, drs_no, edsWithActivityList.getEdsActivityWizards().get(activityEdsDetailBinding.qcViewPager.getCurrentItem()).code, bitmap, false, card_type);
                                }
                            } else if (fragment instanceof OpvFragment) {
                                quality_bitmap = bitmap;
                                qualilty_image = imgView;
                                imgView.setImageBitmap(bitmap);
                                OpvFragment opvFragment = (OpvFragment) edsDetailPagerAdapter.getRegisteredFragment(activityEdsDetailBinding.qcViewPager.getCurrentItem());
                                opvFragment.showMessage(OpvFragment.class.getSimpleName());
                                activityData = opvFragment;
                                upLoadImage(imageName, imageUri, imageCode, awbNo, drs_no, edsWithActivityList.getEdsActivityWizards().get(activityEdsDetailBinding.qcViewPager.getCurrentItem()).code, bitmap, false, card_type);
                            } else if (fragment instanceof ResOpvFragment) {
                                quality_bitmap = bitmap;
                                qualilty_image = imgView;
                                imgView.setImageBitmap(bitmap);
                                ResOpvFragment resOpvFragment = (ResOpvFragment) edsDetailPagerAdapter.getRegisteredFragment(activityEdsDetailBinding.qcViewPager.getCurrentItem());
                                resOpvFragment.showMessage(ResOpvFragment.class.getSimpleName());
                                activityData = resOpvFragment;
                                upLoadImage(imageName, imageUri, imageCode, awbNo, drs_no, edsWithActivityList.getEdsActivityWizards().get(activityEdsDetailBinding.qcViewPager.getCurrentItem()).code, bitmap, false, card_type);
                            } else if (fragment instanceof CaptureImageFragment) {
                                quality_bitmap = bitmap;
                                qualilty_image = imgView;
                                CaptureImageFragment captureImageFragment = (CaptureImageFragment) edsDetailPagerAdapter.getRegisteredFragment(activityEdsDetailBinding.qcViewPager.getCurrentItem());
                                captureImageFragment.showMessage(CaptureImageFragment.class.getSimpleName());
                                activityData = captureImageFragment;
                                documentData = captureImageFragment;
                                if (arrayList_image_validation.isEmpty()) {
                                    for (int i = 0; i < ((CaptureImageFragment) activityData).getViewModel().masterActivityData.get().imageSettings.max; i++) {
                                        arrayList_image_validation.add(0);
                                    }
                                }
                                if (imageCode.equalsIgnoreCase("AADHAR_FRONT_IMAGE")) {
                                    imgView.setImageBitmap(bitmap);
                                    front_image_uri = imageUri;
                                    front_image_code = documentData.getFrontImageCode();
                                    front_image_name = imageName;
                                } else if (imageCode.equalsIgnoreCase("AADHAR_REAR_IMAGE")) {
                                    imgView.setImageBitmap(bitmap);
                                    rear_image_uri = imageUri;
                                    rear_image_code = documentData.getRearImageCode();
                                    rear_image_name = imageName;
                                } else {
                                    quality_bitmap = bitmap;
                                    qualilty_image = imgView;
                                    if (verifyImage)
                                        upLoadImage(imageName, imageUri, imageCode, awbNo, drs_no, edsWithActivityList.getEdsActivityWizards().get(activityEdsDetailBinding.qcViewPager.getCurrentItem()).code, bitmap, true, card_type);
                                    else {
                                        if (edsDetailViewModel.getDataManager().getEDSRealTimeSync().equalsIgnoreCase("false")) {
                                            int image_tag_pos = (Integer) imgView.getTag();
                                            setBitmapNew(image_tag_pos, imgView);
                                            edsDetailViewModel.saveImage(imageName, imageUri, imageCode, 0);
                                        } else {
                                            int image_tag_pos = (Integer) imgView.getTag();
                                            edsDetailViewModel.setImagePosition(image_tag_pos);
                                            upLoadImage(imageName, imageUri, imageCode, awbNo, drs_no, edsWithActivityList.getEdsActivityWizards().get(activityEdsDetailBinding.qcViewPager.getCurrentItem()).code, bitmap, false, card_type);
                                        }
                                    }
                                }
                            } else if (fragment instanceof AcDocumentListCollectionFragment) {
                                quality_bitmap = bitmap;
                                qualilty_image = imgView;
                                AcDocumentListCollectionFragment acDocumentListCollectionFragment = (AcDocumentListCollectionFragment) edsDetailPagerAdapter.getRegisteredFragment(activityEdsDetailBinding.qcViewPager.getCurrentItem());
                                acDocumentListCollectionFragment.showMessage(AcDocumentListCollectionFragment.class.getSimpleName());
                                activityData = acDocumentListCollectionFragment;
                                documentData = acDocumentListCollectionFragment;
                                if (imageCode.equalsIgnoreCase("AADHAR_FRONT_IMAGE")) {
                                    imgView.setImageBitmap(bitmap);
                                    front_image_uri = imageUri;
                                    front_image_code = documentData.getFrontImageCode();
                                    front_image_name = imageName;
                                } else if (imageCode.equalsIgnoreCase("AADHAR_REAR_IMAGE")) {
                                    imgView.setImageBitmap(bitmap);
                                    rear_image_uri = imageUri;
                                    rear_image_code = documentData.getRearImageCode();
                                    rear_image_name = imageName;
                                } else {
                                    quality_bitmap = bitmap;
                                    qualilty_image = imgView;
                                    upLoadImage(imageName, imageUri, imageCode, awbNo, drs_no, edsWithActivityList.getEdsActivityWizards().get(activityEdsDetailBinding.qcViewPager.getCurrentItem()).code, bitmap, false, card_type);
                                }
                            }
                            activityData.validate(true);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        showSnackbar(e.getMessage());
                    }
                });
            }
        };
        activityEdsDetailBinding.qcViewPager.setAdapter(edsDetailPagerAdapter);
        activityEdsDetailBinding.qcViewPager.getCurrentItem();
    }

    private void setUp1() {
        digitalCropImageHandler = new DigitalCropImageHandler(this) {
            @Override
            public void onBitmapReceived(Bitmap bitmap, ImageView imgView, String imageName, String imageCode, String imageUri, boolean verifyImage) {
                runOnUiThread(() -> {
                    try {
                        pimgView = imgView;
                        mBitmap = bitmap;
                        fragment = edsDetailPagerAdapter.getItem(activityEdsDetailBinding.qcViewPager.getCurrentItem());
                        if (fragment instanceof DocumentVerificationFragment) {
                            quality_bitmap = bitmap;
                            qualilty_image = imgView;
                            DocumentVerificationFragment documentVerificationFragment = (DocumentVerificationFragment) edsDetailPagerAdapter.getRegisteredFragment(activityEdsDetailBinding.qcViewPager.getCurrentItem());
                            activityData = documentVerificationFragment;
                            documentData = documentVerificationFragment;
                            if (imageCode.equalsIgnoreCase("AADHAR_FRONT_IMAGE")) {
                                imgView.setImageBitmap(bitmap);
                                front_image_uri = imageUri;
                                front_image_code = documentData.getFrontImageCode();
                                front_image_name = imageName;
                            } else if (imageCode.equalsIgnoreCase("AADHAR_REAR_IMAGE")) {
                                imgView.setImageBitmap(bitmap);
                                rear_image_uri = imageUri;
                                rear_image_code = documentData.getRearImageCode();
                                rear_image_name = imageName;
                            } else {
                                quality_bitmap = bitmap;
                                qualilty_image = imgView;
                                upLoadImage(imageName, imageUri, imageCode, awbNo, drs_no, edsWithActivityList.getEdsActivityWizards().get(activityEdsDetailBinding.qcViewPager.getCurrentItem()).code, bitmap, false, card_type);
                            }
                        } else if (fragment instanceof DocumentCollectionFragment) {
                            quality_bitmap = bitmap;
                            qualilty_image = imgView;
                            DocumentCollectionFragment documentCollectionFragment = (DocumentCollectionFragment) edsDetailPagerAdapter.getRegisteredFragment(activityEdsDetailBinding.qcViewPager.getCurrentItem());
                            documentCollectionFragment.showMessage(DocumentCollectionFragment.class.getSimpleName());
                            activityData = documentCollectionFragment;
                            documentData = documentCollectionFragment;
                            if (imageCode.equalsIgnoreCase("AADHAR_FRONT_IMAGE")) {
                                imgView.setImageBitmap(bitmap);
                                front_image_uri = imageUri;
                                front_image_code = documentData.getFrontImageCode();
                                front_image_name = imageName;
                            } else if (imageCode.equalsIgnoreCase("AADHAR_REAR_IMAGE")) {
                                imgView.setImageBitmap(bitmap);
                                rear_image_uri = imageUri;
                                rear_image_code = documentData.getRearImageCode();
                                rear_image_name = imageName;
                            } else {
                                quality_bitmap = bitmap;
                                qualilty_image = imgView;
                                upLoadImage(imageName, imageUri, imageCode, awbNo, drs_no, edsWithActivityList.getEdsActivityWizards().get(activityEdsDetailBinding.qcViewPager.getCurrentItem()).code, bitmap, false, card_type);
                            }
                        } else if (fragment instanceof DocumentListCollectionFragment) {
                            DocumentListCollectionFragment documentListCollectionFragment = (DocumentListCollectionFragment) edsDetailPagerAdapter.getRegisteredFragment(activityEdsDetailBinding.qcViewPager.getCurrentItem());
                            documentListCollectionFragment.showMessage(DocumentListCollectionFragment.class.getSimpleName());
                            activityData = documentListCollectionFragment;
                            documentData = documentListCollectionFragment;
                            if (imageCode.equalsIgnoreCase("AADHAR_FRONT_IMAGE")) {
                                imgView.setImageBitmap(bitmap);
                                front_image_uri = imageUri;
                                front_image_code = documentData.getFrontImageCode();
                                front_image_name = imageName;
                            } else if (imageCode.equalsIgnoreCase("AADHAR_REAR_IMAGE")) {
                                imgView.setImageBitmap(bitmap);
                                rear_image_uri = imageUri;
                                rear_image_code = documentData.getRearImageCode();
                                rear_image_name = imageName;
                            } else {
                                quality_bitmap = bitmap;
                                qualilty_image = imgView;
                                upLoadImage(imageName, imageUri, imageCode, awbNo, drs_no, edsWithActivityList.getEdsActivityWizards().get(activityEdsDetailBinding.qcViewPager.getCurrentItem()).code, bitmap, false, card_type);
                            }
                        } else if (fragment instanceof OpvFragment) {
                            quality_bitmap = bitmap;
                            qualilty_image = imgView;
                            imgView.setImageBitmap(bitmap);
                            OpvFragment opvFragment = (OpvFragment) edsDetailPagerAdapter.getRegisteredFragment(activityEdsDetailBinding.qcViewPager.getCurrentItem());
                            opvFragment.showMessage(OpvFragment.class.getSimpleName());
                            activityData = opvFragment;
                            upLoadImage(imageName, imageUri, imageCode, awbNo, drs_no, edsWithActivityList.getEdsActivityWizards().get(activityEdsDetailBinding.qcViewPager.getCurrentItem()).code, bitmap, false, card_type);
                        } else if (fragment instanceof ResOpvFragment) {
                            quality_bitmap = bitmap;
                            qualilty_image = imgView;
                            imgView.setImageBitmap(bitmap);
                            ResOpvFragment resOpvFragment = (ResOpvFragment) edsDetailPagerAdapter.getRegisteredFragment(activityEdsDetailBinding.qcViewPager.getCurrentItem());
                            resOpvFragment.showMessage(ResOpvFragment.class.getSimpleName());
                            activityData = resOpvFragment;
                            upLoadImage(imageName, imageUri, imageCode, awbNo, drs_no, edsWithActivityList.getEdsActivityWizards().get(activityEdsDetailBinding.qcViewPager.getCurrentItem()).code, bitmap, false, card_type);
                        } else if (fragment instanceof CaptureImageFragment) {
                            quality_bitmap = bitmap;
                            qualilty_image = imgView;
                            CaptureImageFragment captureImageFragment = (CaptureImageFragment) edsDetailPagerAdapter.getRegisteredFragment(activityEdsDetailBinding.qcViewPager.getCurrentItem());
                            captureImageFragment.showMessage(CaptureImageFragment.class.getSimpleName());
                            activityData = captureImageFragment;
                            documentData = captureImageFragment;
                            if (arrayList_image_validation.isEmpty()) {
                                for (int i = 0; i < ((CaptureImageFragment) activityData).getViewModel().masterActivityData.get().imageSettings.max; i++) {
                                    arrayList_image_validation.add(0);
                                }
                            }
                            if (imageCode.equalsIgnoreCase("AADHAR_FRONT_IMAGE")) {
                                imgView.setImageBitmap(bitmap);
                                front_image_uri = imageUri;
                                front_image_code = documentData.getFrontImageCode();
                                front_image_name = imageName;
                            } else if (imageCode.equalsIgnoreCase("AADHAR_REAR_IMAGE")) {
                                imgView.setImageBitmap(bitmap);
                                rear_image_uri = imageUri;
                                rear_image_code = documentData.getRearImageCode();
                                rear_image_name = imageName;
                            } else {
                                quality_bitmap = bitmap;
                                qualilty_image = imgView;
                                if (verifyImage)
                                    upLoadImage(imageName, imageUri, imageCode, awbNo, drs_no, edsWithActivityList.getEdsActivityWizards().get(activityEdsDetailBinding.qcViewPager.getCurrentItem()).code, bitmap, true, card_type);
                                else {
                                    if (edsDetailViewModel.getDataManager().getEDSRealTimeSync().equalsIgnoreCase("false")) {
                                        int image_tag_pos = (Integer) imgView.getTag();
                                        setBitmapNew(image_tag_pos, imgView);
                                        edsDetailViewModel.saveImage(imageName, imageUri, imageCode, 0);
                                    } else {
                                        upLoadImage(imageName, imageUri, imageCode, awbNo, drs_no, edsWithActivityList.getEdsActivityWizards().get(activityEdsDetailBinding.qcViewPager.getCurrentItem()).code, bitmap, false, card_type);
                                    }
                                }
                            }
                        } else if (fragment instanceof AcDocumentListCollectionFragment) {
                            quality_bitmap = bitmap;
                            qualilty_image = imgView;
                            AcDocumentListCollectionFragment acDocumentListCollectionFragment = (AcDocumentListCollectionFragment) edsDetailPagerAdapter.getRegisteredFragment(activityEdsDetailBinding.qcViewPager.getCurrentItem());
                            acDocumentListCollectionFragment.showMessage(AcDocumentListCollectionFragment.class.getSimpleName());
                            activityData = acDocumentListCollectionFragment;
                            documentData = acDocumentListCollectionFragment;
                            if (imageCode.equalsIgnoreCase("AADHAR_FRONT_IMAGE")) {
                                imgView.setImageBitmap(bitmap);
                                front_image_uri = imageUri;
                                front_image_code = documentData.getFrontImageCode();
                                front_image_name = imageName;
                            } else if (imageCode.equalsIgnoreCase("AADHAR_REAR_IMAGE")) {
                                imgView.setImageBitmap(bitmap);
                                rear_image_uri = imageUri;
                                rear_image_code = documentData.getRearImageCode();
                                rear_image_name = imageName;
                            } else {
                                quality_bitmap = bitmap;
                                qualilty_image = imgView;
                                upLoadImage(imageName, imageUri, imageCode, awbNo, drs_no, edsWithActivityList.getEdsActivityWizards().get(activityEdsDetailBinding.qcViewPager.getCurrentItem()).code, bitmap, false, card_type);
                            }
                        }
                        activityData.validate(true);
                    } catch (Exception e) {
                        e.printStackTrace();
                        showSnackbar(e.getMessage());
                    }
                });
            }
        };
        activityEdsDetailBinding.qcViewPager.setAdapter(edsDetailPagerAdapter);
        activityEdsDetailBinding.qcViewPager.getCurrentItem();
    }

    private void upLoadImage(String imageName, String imageUri, String imageCode, long awbNo, int drs_no, String activity_code, Bitmap bitmap, boolean uddan_flag, String card_type) {
        if (uddan_flag) {
            edsDetailViewModel.upLoadAwsImage(imageName, imageUri, imageCode, awbNo, drs_no, activity_code, bitmap);
        } else {
            edsDetailViewModel.UploadImage(imageName, imageUri, imageCode, awbNo, drs_no, activity_code, bitmap, card_type);
        }
    }

    private void uploadAadharImages(String front_image_uri, String rear_image_uri, String front_image_code, String rear_image_code, String front_image_name, String rear_image_name) {
        edsDetailViewModel.uploadAadharImageServer(front_image_uri, rear_image_uri, front_image_code, rear_image_code, front_image_name, rear_image_name);
    }

    private void getStatusAadharImages() {
        edsDetailViewModel.getStatusAadharMasking();
    }

    public void initilize() {
        activityEdsDetailBinding.qcViewPager.setAdapter(edsDetailPagerAdapter);
        activityEdsDetailBinding.qcViewPager.getCurrentItem();
    }

    public void hideFooter() {
        activityEdsDetailBinding.lltFooter.setVisibility(View.GONE);
    }

    @Override
    public EDSDetailViewModel getViewModel() {
        return edsDetailViewModel;
    }

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_eds_detail;
    }

    public static Intent getStartIntent(Context context) {
        return new Intent(context, EDSDetailActivity.class);
    }

    private void updateViewPager(EdsWithActivityList edsWithActivityList, List<MasterActivityData> masterActivityData) {
        getActivityWiseCount(edsWithActivityList);
        getActivityName(edsWithActivityList, activityNameCount, "true");
        edsDetailPagerAdapter.edsWithActivityList = edsWithActivityList;
        edsDetailPagerAdapter.masterActivityData = masterActivityData;
        edsDetailPagerAdapter.notifyDataSetChanged();
    }

    private void getActivityWiseCount(EdsWithActivityList edsWithActivityList) {
        try {
            edsWithActivityList.setEdsActivityWizards(removeDuplicates(edsWithActivityList.getEdsActivityWizards()));
            for (EDSActivityWizard edsActivityWizard : edsWithActivityList.getEdsActivityWizards()) {
                if (edsActivityWizard.getCode().startsWith("DC")) {
                    stageCountActivity.add("DC");
                    dccount++;
                } else if (edsActivityWizard.getCode().startsWith("DV")) {
                    stageCountActivity.add("DV");
                    dvcount++;
                } else if (edsActivityWizard.getCode().startsWith("AC")) {
                    stageCountActivity.add("AC");
                    acount++;
                }
            }
            stagerunningCount.clear();
            stagerunningCount.addAll(stageCountActivity);
        } catch (Exception e) {
            e.printStackTrace();
            showSnackbar(e.getMessage());
        }
    }

    public List<EDSActivityWizard> removeDuplicates(List<EDSActivityWizard> list) {
        Set set = new TreeSet((o1, o2) -> {
            if (((EDSActivityWizard) o1).getActivityId().equalsIgnoreCase(((EDSActivityWizard) o2).getActivityId())) {
                return 0;
            }
            return 1;
        });
        set.addAll(list);
        return (List) new ArrayList(set);
    }

    private void getActivityName(EdsWithActivityList edsWithActivityList, int count, String flag) {
        try {
            if (edsWithActivityList.edsActivityWizards.get(count).getCode().startsWith("DC")) {
                if (flag.equalsIgnoreCase("true")) {
                    runningCount.add("DC");
                }
                Collections.frequency(runningCount, "DC");
                stageCount = stagerunningCount.indexOf("DC");
                edsDetailViewModel.setStageActivityCount(stageCount + 1);
                edsDetailViewModel.setActivityNameCount(Collections.frequency(runningCount, "DC"));
                edsDetailViewModel.setActivityTotalCount(dccount);
                edsDetailViewModel.setActivityName(GlobalConstant.EDSActivityName.DOCUMENT_COLLECTION);
            } else if (edsWithActivityList.edsActivityWizards.get(count).getCode().startsWith("DV")) {
                if (flag.equalsIgnoreCase("true")) {
                    runningCount.add("DV");
                }
                stageCount = stagerunningCount.indexOf("DV");
                edsDetailViewModel.setStageActivityCount(stageCount + 1);
                edsDetailViewModel.setActivityNameCount(Collections.frequency(runningCount, "DV"));
                edsDetailViewModel.setActivityTotalCount(dvcount);
                edsDetailViewModel.setActivityName(GlobalConstant.EDSActivityName.DOCUMENT_VERIFICATION);
            } else if (edsWithActivityList.edsActivityWizards.get(count).getCode().startsWith("AC")) {
                if (flag.equalsIgnoreCase("true")) {
                    runningCount.add("AC");
                }
                stageCount = stagerunningCount.indexOf("AC");
                edsDetailViewModel.setStageActivityCount(stageCount + 1);
                edsDetailViewModel.setActivityNameCount(Collections.frequency(runningCount, "AC"));
                edsDetailViewModel.setActivityTotalCount(acount);
                edsDetailViewModel.setActivityName(GlobalConstant.EDSActivityName.ACTIVITY);
            }
        } catch (Exception e) {
            e.printStackTrace();
            showSnackbar(e.getMessage());
        }
    }

    public void onBackPressed() {
        try {
            BaseFragment baseFragment = null;
            for (Map.Entry<BaseFragment, Boolean> entry : successFragment.entrySet()) {
                baseFragment = entry.getKey();
            }
            if (baseFragment != null) {
                successFragment.remove(baseFragment);
                if (activityNameCount >= 0) {
                    runningCount.remove(runningCount.size() - 1);
                    activityNameCount--;
                    getActivityName(edsWithActivityList, activityNameCount, "false");
                }
                if (edsActivityResponseWizards.getLast() != null)
                    edsActivityResponseWizards.remove(edsActivityResponseWizards.getLast());
            }
            int pos = showFragmentPosition(false);
            if (pos >= 0) {
                activityEdsDetailBinding.qcViewPager.setCurrentItem(pos);
            } else {
                super.onBackPressed();
                Intent intent = new Intent(EDSDetailActivity.this, EdsTaskListActivity.class);
                //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra(Constants.INTENT_KEY, awbNo);
                intent.putExtra(Constants.DRS_PSTN_KEY, getDrsPstnKey);
                intent.putExtra(Constants.DRS_API_KEY, getDrsApiKey);
                intent.putExtra(Constants.COMPOSITE_KEY, composite_key);
                intent.putExtra(Constants.RESCHEDULE_ENABLE, reschedule_enable);
                intent.putExtra(Constants.DRS_PIN, getDrsPin);
                startActivity(intent);
            }
        } catch (Exception e) {
            e.printStackTrace();
            showSnackbar(e.getMessage());
        }
    }

    @Override
    public void onBack() {
        try {
            BaseFragment baseFragment = null;
            for (Map.Entry<BaseFragment, Boolean> entry : successFragment.entrySet()) {
                baseFragment = entry.getKey();
            }
            if (baseFragment != null) {
                successFragment.remove(baseFragment);
                if (activityNameCount >= 0) {
                    // runningcount.remove(activityNameCount);
                    //  reversecount(edsWithActivityList, activityNameCount);
                    runningCount.remove(runningCount.size() - 1);
                    activityNameCount--;
                    getActivityName(edsWithActivityList, activityNameCount, "false");
                }
                if (edsActivityResponseWizards.getLast() != null)
                    edsActivityResponseWizards.remove(edsActivityResponseWizards.getLast());
            }
            int pos = showFragmentPosition(false);
            if (pos >= 0) {
                activityEdsDetailBinding.qcViewPager.setCurrentItem(pos);
            } else {
                super.onBackPressed();
                Intent intent = new Intent(EDSDetailActivity.this, EdsTaskListActivity.class);
                //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra(Constants.INTENT_KEY, awbNo);
                intent.putExtra(Constants.DRS_PSTN_KEY, getDrsPstnKey);
                intent.putExtra(Constants.DRS_API_KEY, getDrsApiKey);
                intent.putExtra(Constants.COMPOSITE_KEY, composite_key);
                intent.putExtra(Constants.RESCHEDULE_ENABLE, reschedule_enable);
                intent.putExtra(Constants.DRS_PIN, getDrsPin);
                startActivity(intent);
            }
        } catch (Exception e) {
            e.printStackTrace();
            showSnackbar(e.getMessage());
        }
    }

    @Override
    public void onNext(EdsCommit edsCommit) {
        try {
            fragment = edsDetailPagerAdapter.getItem(activityEdsDetailBinding.qcViewPager.getCurrentItem());
            if (fragment instanceof DocumentCollectionFragment) {
                DocumentCollectionFragment documentCollectionFragment = (DocumentCollectionFragment) edsDetailPagerAdapter.getRegisteredFragment(activityEdsDetailBinding.qcViewPager.getCurrentItem());
                activityData = documentCollectionFragment;
                documentCollectionFragment.showMessage(DocumentCollectionFragment.class.getSimpleName());
                if (edsWithActivityList.edsActivityWizards.get(activityEdsDetailBinding.qcViewPager.getCurrentItem()).wizardFlag.isMandate()) {
                    if (!activityData.validateData()) {
                        return;
                    }
                    getd();
                    activityData.getData(documentCollectionFragment);
                } else {
                    getd();
                    activityData.getData(documentCollectionFragment);
                }
            } else if (fragment instanceof EdsEkycPaytmFragment) {
                EdsEkycPaytmFragment edsEkycPaytmFragment = (EdsEkycPaytmFragment) edsDetailPagerAdapter.getRegisteredFragment(activityEdsDetailBinding.qcViewPager.getCurrentItem());
                activityData = edsEkycPaytmFragment;
                edsEkycPaytmFragment.showMessage(DocumentCollectionFragment.class.getSimpleName());
                if (edsWithActivityList.edsActivityWizards.get(activityEdsDetailBinding.qcViewPager.getCurrentItem()).wizardFlag.isMandate()) {
                    if (!activityData.validateData()) {
                        return;
                    }
                    getd();
                    activityData.getData(edsEkycPaytmFragment);
                } else {
                    getd();
                    activityData.getData(edsEkycPaytmFragment);
                }
            } else if (fragment instanceof DocumentVerificationFragment) {
                DocumentVerificationFragment documentVerificationFragment = (DocumentVerificationFragment) edsDetailPagerAdapter.getRegisteredFragment(activityEdsDetailBinding.qcViewPager.getCurrentItem());
                documentVerificationFragment.showMessage(DocumentVerificationFragment.class.getSimpleName());
                activityData = documentVerificationFragment;
                if (edsWithActivityList.edsActivityWizards.get(activityEdsDetailBinding.qcViewPager.getCurrentItem()).wizardFlag.isMandate()) {
                    if (!activityData.validateData()) {
                        return;
                    }
                    getd();
                    activityData.getData(documentVerificationFragment);
                } else {
                    getd();
                    activityData.getData(documentVerificationFragment);
                }
            } else if (fragment instanceof CashCollectionFragment) {
                CashCollectionFragment cashCollectionFragment = (CashCollectionFragment) edsDetailPagerAdapter.getRegisteredFragment(activityEdsDetailBinding.qcViewPager.getCurrentItem());
                cashCollectionFragment.showMessage(CashCollectionFragment.class.getSimpleName());
                activityData = cashCollectionFragment;
                if (edsWithActivityList.edsActivityWizards.get(activityEdsDetailBinding.qcViewPager.getCurrentItem()).wizardFlag.isMandate()) {
                    if (!activityData.validateData()) {
                        return;
                    }
                    getd();
                    activityData.getData(cashCollectionFragment);
                } else {
                    getd();
                    activityData.getData(cashCollectionFragment);
                }
            } else if (fragment instanceof DocumentListCollectionFragment) {
                DocumentListCollectionFragment documentListCollectionFragment = (DocumentListCollectionFragment) edsDetailPagerAdapter.getRegisteredFragment(activityEdsDetailBinding.qcViewPager.getCurrentItem());
                documentListCollectionFragment.showMessage(DocumentListCollectionFragment.class.getSimpleName());
                activityData = documentListCollectionFragment;
                if (edsWithActivityList.edsActivityWizards.get(activityEdsDetailBinding.qcViewPager.getCurrentItem()).wizardFlag.isMandate()) {
                    if (spinner_code_value != null && spinner_code_value.equalsIgnoreCase("AADHAR")) {
                        if (!edsDetailViewModel.getDataManager().getAadharStatus()) {
                            showSnackbar("Aadhar Images are not uploaded or Aadhar status is pending.");
                            return;
                        }
                    } else {
                        if (!activityData.validateData()) {
                            return;
                        }
                    }
                    getd();
                    activityData.getData(documentListCollectionFragment);
                } else {
                    getd();
                    activityData.getData(documentListCollectionFragment);
                }
            } else if (fragment instanceof OpvFragment) {
                OpvFragment opvFragment = (OpvFragment) edsDetailPagerAdapter.getRegisteredFragment(activityEdsDetailBinding.qcViewPager.getCurrentItem());
                opvFragment.showMessage(OpvFragment.class.getSimpleName());
                activityData = opvFragment;
                if (edsWithActivityList.edsActivityWizards.get(activityEdsDetailBinding.qcViewPager.getCurrentItem()).wizardFlag.isMandate()) {
                    if (!activityData.validateData()) {
                        return;
                    }
                    getd();
                    activityData.getData(opvFragment);
                } else {
                    getd();
                    activityData.getData(opvFragment);
                }
            } else if (fragment instanceof EdsEkycAntWorkFragment) {
                EdsEkycAntWorkFragment edsEkycAntWorkFragment = (EdsEkycAntWorkFragment) edsDetailPagerAdapter.getRegisteredFragment(activityEdsDetailBinding.qcViewPager.getCurrentItem());
                edsEkycAntWorkFragment.showMessage(OpvFragment.class.getSimpleName());
                activityData = edsEkycAntWorkFragment;
                if (edsWithActivityList.edsActivityWizards.get(activityEdsDetailBinding.qcViewPager.getCurrentItem()).wizardFlag.isMandate()) {
                    if (!activityData.validateData()) {
                        return;
                    }
                    getd();
                    activityData.getData(edsEkycAntWorkFragment);
                } else {
                    getd();
                    activityData.getData(edsEkycAntWorkFragment);
                }
            } else if (fragment instanceof ResOpvFragment) {
                ResOpvFragment resOpvFragment = (ResOpvFragment) edsDetailPagerAdapter.getRegisteredFragment(activityEdsDetailBinding.qcViewPager.getCurrentItem());
                activityData = resOpvFragment;
                if (edsWithActivityList.edsActivityWizards.get(activityEdsDetailBinding.qcViewPager.getCurrentItem()).wizardFlag.isMandate()) {
                    if (!activityData.validateData()) {
                        return;
                    }
                    getd();
                    activityData.getData(resOpvFragment);
                } else {
                    getd();
                    activityData.getData(resOpvFragment);
                }
            } else if (fragment instanceof VodafoneFragment) {
                VodafoneFragment vodafoneFragment = (VodafoneFragment) edsDetailPagerAdapter.getRegisteredFragment(activityEdsDetailBinding.qcViewPager.getCurrentItem());
                vodafoneFragment.showMessage(DocumentListCollectionFragment.class.getSimpleName());
                activityData = vodafoneFragment;
                if (edsWithActivityList.edsActivityWizards.get(activityEdsDetailBinding.qcViewPager.getCurrentItem()).wizardFlag.isMandate()) {
                    if (!activityData.validateData()) {
                        return;
                    }
                    getd();
                    activityData.getData(vodafoneFragment);
                } else {
                    getd();
                    activityData.getData(vodafoneFragment);
                }
            } else if (fragment instanceof EdsRblFragment) {
                EdsRblFragment edsRblFragment = (EdsRblFragment) edsDetailPagerAdapter.getRegisteredFragment(activityEdsDetailBinding.qcViewPager.getCurrentItem());
                activityData = edsRblFragment;
                if (edsWithActivityList.edsActivityWizards.get(activityEdsDetailBinding.qcViewPager.getCurrentItem()).wizardFlag.isMandate()) {
                    if (!activityData.validateData()) {
                        showSnackbar("Please complete kyc first.");
                        return;
                    }
                    getd();
                    activityData.getData(edsRblFragment);
                } else {
                    getd();
                    activityData.getData(edsRblFragment);
                }
            } else if (fragment instanceof CaptureImageFragment) {
                CaptureImageFragment captureImageFragment = (CaptureImageFragment) edsDetailPagerAdapter.getRegisteredFragment(activityEdsDetailBinding.qcViewPager.getCurrentItem());
                captureImageFragment.showMessage(CaptureImageFragment.class.getSimpleName());
                activityData = captureImageFragment;
                if (edsWithActivityList.edsActivityWizards.get(activityEdsDetailBinding.qcViewPager.getCurrentItem()).wizardFlag.isMandate()) {
                    if (!activityData.validateData()) {
                        return;
                    }
                    getd();
                    activityData.getData(captureImageFragment);
                } else {
                    getd();
                    activityData.getData(captureImageFragment);
                }
            } else if (fragment instanceof AcDocumentListCollectionFragment) {
                AcDocumentListCollectionFragment acDocumentListCollectionFragment = (AcDocumentListCollectionFragment) edsDetailPagerAdapter.getRegisteredFragment(activityEdsDetailBinding.qcViewPager.getCurrentItem());
                acDocumentListCollectionFragment.showMessage(AcDocumentListCollectionFragment.class.getSimpleName());
                activityData = acDocumentListCollectionFragment;
                if (edsWithActivityList.edsActivityWizards.get(activityEdsDetailBinding.qcViewPager.getCurrentItem()).wizardFlag.isMandate()) {
                    if (spinner_code_value != null && spinner_code_value.equalsIgnoreCase("AADHAR")) {
                        if (!edsDetailViewModel.getDataManager().getAadharStatus()) {
                            showSnackbar("Aadhar Images are not uploaded or Aadhar status is pending.");
                            return;
                        }
                    } else {
                        if (!activityData.validateData()) {
                            return;
                        }
                    }
                    getd();
                    activityData.getData(acDocumentListCollectionFragment);
                } else {
                    getd();
                    activityData.getData(acDocumentListCollectionFragment);
                }
            } else if (fragment instanceof IciciEkycFragment) {
                IciciEkycFragment iciciEkycFragment = (IciciEkycFragment) edsDetailPagerAdapter.getRegisteredFragment(activityEdsDetailBinding.qcViewPager.getCurrentItem());
                activityData = iciciEkycFragment;
                if (edsWithActivityList.edsActivityWizards.get(activityEdsDetailBinding.qcViewPager.getCurrentItem()).wizardFlag.isMandate()) {
                    if (!activityData.validateData()) {
                        return;
                    }
                    getd();
                    activityData.getData(iciciEkycFragment);
                } else {
                    getd();
                    activityData.getData(iciciEkycFragment);
                }
            } else if (fragment instanceof IciciEkycFragment_standard) {
                IciciEkycFragment_standard iciciEkycFragment = (IciciEkycFragment_standard) edsDetailPagerAdapter.getRegisteredFragment(activityEdsDetailBinding.qcViewPager.getCurrentItem());
                activityData = iciciEkycFragment;
                if (!activityData.validateData()) {
                    return;
                }
                getd();
                activityData.getData(iciciEkycFragment);
            } else if (fragment instanceof EdsEkycXMLFragment) {
                EdsEkycXMLFragment EdsEkycXMLFragment = (EdsEkycXMLFragment) edsDetailPagerAdapter.getRegisteredFragment(activityEdsDetailBinding.qcViewPager.getCurrentItem());
                activityData = EdsEkycXMLFragment;
                if (edsWithActivityList.edsActivityWizards.get(activityEdsDetailBinding.qcViewPager.getCurrentItem()).wizardFlag.isMandate()) {
                    if (!activityData.validateData()) {
                        return;
                    }
                    getd();
                    activityData.getData(EdsEkycXMLFragment);
                } else {
                    getd();
                    activityData.getData(EdsEkycXMLFragment);
                }
            } else if (fragment instanceof EdsBkycIdfcFragment) {
                EdsBkycIdfcFragment edsBkycIdfcFragment = (EdsBkycIdfcFragment) edsDetailPagerAdapter.getRegisteredFragment(activityEdsDetailBinding.qcViewPager.getCurrentItem());
                activityData = edsBkycIdfcFragment;
                if (!activityData.validateData()) {
                    showSnackbar("Please complete kyc first.");
                    return;
                }
                getd();
                activityData.getData(edsBkycIdfcFragment);
            } else if (fragment instanceof EdsEkycIdfcFragment) {
                EdsEkycIdfcFragment edsEkycIdfcFragment = (EdsEkycIdfcFragment) edsDetailPagerAdapter.getRegisteredFragment(activityEdsDetailBinding.qcViewPager.getCurrentItem());
                activityData = edsEkycIdfcFragment;
                if (!activityData.validateData()) {
                    showSnackbar("Please complete kyc first.");
                    return;
                }
                getd();
                activityData.getData(edsEkycIdfcFragment);
            } else if (fragment instanceof EdsEkycNiyoFragment) {
                EdsEkycNiyoFragment edsEkycNiyoFragment = (EdsEkycNiyoFragment) edsDetailPagerAdapter.getRegisteredFragment(activityEdsDetailBinding.qcViewPager.getCurrentItem());
                activityData = edsEkycNiyoFragment;
                if (!activityData.validateData()) {
                    showSnackbar("Please complete kyc first.");
                    return;
                }
                getd();
                activityData.getData(edsEkycNiyoFragment);
            } else if (fragment instanceof PaytmFragment) {
                PaytmFragment paytmFragment = (PaytmFragment) edsDetailPagerAdapter.getRegisteredFragment(activityEdsDetailBinding.qcViewPager.getCurrentItem());
                activityData = paytmFragment;
                if (!activityData.validateData()) {
                    // showSnackbar("Please complete kyc first.");
                    return;
                }
                getd();
                activityData.getData(paytmFragment);
            } else if (fragment instanceof EdsEkycFreyoFragment) {
                EdsEkycFreyoFragment edsEkycFreyoFragment = (EdsEkycFreyoFragment) edsDetailPagerAdapter.getRegisteredFragment(activityEdsDetailBinding.qcViewPager.getCurrentItem());
                activityData = edsEkycFreyoFragment;
                if (!activityData.validateData()) {
                    showSnackbar("Please complete kyc first.");
                    return;
                }
                getd();
                activityData.getData(edsEkycFreyoFragment);
            }
            if (masterActivityData != null && masterActivityData.size() > 0 && masterActivityData.size() > count) {
                int pos = showFragmentPosition(true);
                if (pos > 0 && masterActivityData.size() - 1 > activityEdsDetailBinding.qcViewPager.getCurrentItem()) {
                    activityEdsDetailBinding.qcViewPager.setCurrentItem(pos);
                    if (fragment instanceof OpvFragment) {
                        edsDetailPagerAdapter.notifyDataSetChanged();
                    }
                    // setCount();
                } else {
                    proceedToNextActivity();
                }
            } else {
                proceedToNextActivity();
            }
        } catch (Exception e) {
            e.printStackTrace();
            showSnackbar(e.getMessage());
        }
    }

    @Override
    public void onCancel(EdsCommit edsCommit){
        AlertDialog.Builder builder = new AlertDialog.Builder(this,R.style.Base_ThemeOverlay_AppCompat_Dialog_Alert);
        String AlertText1 = "Attention : ";
        builder.setMessage(AlertText1 + getString(R.string.cancel_alert)).setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                onCancelFragment();
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public void scanMantra() {
        Intent intent = new Intent();
        intent.setAction("in.gov.uidai.rdservice.fp.INFO");
        startActivityForResult(intent, 1);
    }

    @Override
    public void showErrorMessage(boolean status) {
        if (status)
            showSnackbar(getString(R.string.http_500_msg));
        else
            showSnackbar(getString(R.string.server_down_msg));
    }

    @Override
    public void sendScanResult(String scannedData) {
        try {
            sendData(scannedData);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onHandleError(String errorResponse) {
        showSnackbar(errorResponse);
    }

    @Override
    public void errorMgs(String eMessage) {
        showSnackbar(eMessage);
    }

    @Override
    public void showProgressDelay(){
        if(!isFinishing()){
            try{
                progress1 = new ProgressDialog(this,android.R.style.Theme_Material_Light_Dialog);
                progress1.setMessage("We are verifying this image to server. Please wait...");
                progress1.setCancelable(false);
                progress1.show();
                //        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                //        activityEdsDetailBinding.progress.setVisibility(View.VISIBLE);
                //        activityEdsDetailBinding.tvProgressTime.setVisibility(View.VISIBLE);
                //        activityEdsDetailBinding.progress.setClickable(false);
                edsDetailViewModel.countDown();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onProgressTimer(int seconds) {
        activityEdsDetailBinding.tvProgressTime.setText(seconds + "");
    }

    @Override
    public void onProgressFinish() {
        //        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        //        activityEdsDetailBinding.progress.setVisibility(View.GONE);
        //        activityEdsDetailBinding.tvProgressTime.setVisibility(View.GONE);
        try {
            if (progress != null && progress.isShowing()) {
                progress.dismiss();
                progress = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        try {
            if (progress != null && progress.isShowing()) {
                progress.dismiss();
                progress = null;
            }
            if (progress1 != null && progress1.isShowing()) {
                progress1.dismiss();
                progress1 = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onProgressFinishCount() {
        try {
            edsDetailViewModel.getImageResult();
            if (progress1 != null && progress1.isShowing()) {
                progress1.dismiss();
                progress1 = null;
            }
        } catch (Exception e) {
            if (progress1 != null && progress1.isShowing()) {
                progress1.dismiss();
                progress1 = null;
            }
        }
    }

    @Override
    public void setBitmap() {
        try {
            activityData.setImageValidation();
            qualilty_image.setImageBitmap(mBitmap);
            //if (pimgView.getDrawable() != null && pimgView.getDrawable().getConstantState() == getResources().getDrawable(R.drawable.cam).getConstantState()) {
            Intent i = new Intent("SEND_IMAGE_VALIDATION");
            arrayList_image_validation.add(edsDetailViewModel.getImagePosition(), 1);
            i.putIntegerArrayListExtra("image_validation", arrayList_image_validation);
            LocalBroadcastManager.getInstance(this).sendBroadcast(i);
            //qualilty_image.setImageBitmap(quality_bitmap);
            //            } else {
            //                Intent i = new Intent("SEND_IMAGE_VALIDATION");
            //                i.putIntegerArrayListExtra("image_validation", arrayList_image_validation);
            //                LocalBroadcastManager.getInstance(this).sendBroadcast(i);
            //                // qualilty_image.setImageBitmap(quality_bitmap);
            //            }
        } catch (Exception e) {
            //Toast.makeText(EDSDetailActivity.this, "Image corrupted.Try Again", Toast.LENGTH_LONG).show();
        }
    }

    public void setBitmapNew(int image_pos, ImageView imgView) {
        try {
            activityData.setImageValidation();
            imgView.setImageBitmap(mBitmap);
            Intent i = new Intent("SEND_IMAGE_VALIDATION");
            arrayList_image_validation.set(image_pos, 1);
            i.putIntegerArrayListExtra("image_validation", arrayList_image_validation);
            LocalBroadcastManager.getInstance(this).sendBroadcast(i);
          /*  if (imgView.getDrawable() != null && imgView.getDrawable().getConstantState() == getResources().getDrawable(R.drawable.cam).getConstantState()) {
                Intent i = new Intent("SEND_IMAGE_VALIDATION");
                arrayList_image_validation.set(image_pos, 1);
                i.putIntegerArrayListExtra("image_validation", arrayList_image_validation);
                LocalBroadcastManager.getInstance(this).sendBroadcast(i);
            } else {
                Intent i = new Intent("SEND_IMAGE_VALIDATION");
                i.putIntegerArrayListExtra("image_validation", arrayList_image_validation);
                LocalBroadcastManager.getInstance(this).sendBroadcast(i);
                // imgView.setImageBitmap(mBitmap);
            }*/
        } catch (Exception e) {
            Toast.makeText(EDSDetailActivity.this, "Image corrupted.Try Again", Toast.LENGTH_LONG).show();
        }
        // qualilty_image.setImageBitmap(quality_bitmap);
    }

    @Override
    public void removeBitmap() {
        activityData.validate(false);
        try {
            arrayList_image_validation.set(edsDetailViewModel.getImagePosition(), 0);
            qualilty_image.setImageResource(R.drawable.cam);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onProgressFinishCall() {
        try {
            if (progress != null && progress.isShowing()) {
                progress.dismiss();
                progress = null;
            }
            //            progress1.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setBitmapOnImageId() {
        qualilty_image.setImageBitmap(quality_bitmap);
    }

    @Override
    public void showProgress(){
        if(!isFinishing()){
            try{
                progress = new ProgressDialog(this,android.R.style.Theme_Material_Light_Dialog);
                progress.setMessage("Uploading the image. Please wait...");
                progress.setCancelable(false);
                progress.show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public Activity getContextProvider() {
        return this;
    }

    @Override
    public void setAadharToCameraImages() {
        documentData.setAadharToCamera();
    }

    @Override
    public void showSuccessMessage(String msg) {
        showSuccessMessage(msg);
    }

    @Override
    public void setAadharURIToBlank() {
        front_image_uri = "";
        rear_image_uri = "";
    }

    @Override
    public void uploadEdsImages(String imageName, String imageUri, String imageCode, long awbNo, int drs_no, String activity_code, Bitmap bitmap, boolean udan_flag) {
        if (udan_flag) {
            edsDetailViewModel.upLoadAwsImage(imageName, imageUri, imageCode, awbNo, drs_no, activity_code, bitmap);
        } else {
            edsDetailViewModel.UploadImage(imageName, imageUri, imageCode, awbNo, drs_no, activity_code, bitmap, "NONE");
        }
    }

    private String sendData(String scannedData) {
        return scannedData;
    }

    private void onCancelFragment() {
        try {
            fragment = edsDetailPagerAdapter.getItem(activityEdsDetailBinding.qcViewPager.getCurrentItem());
            Intent intent;
            if (fragment instanceof DocumentCollectionFragment) {
                DocumentCollectionFragment documentCollectionFragment = (DocumentCollectionFragment) edsDetailPagerAdapter.getRegisteredFragment(activityEdsDetailBinding.qcViewPager.getCurrentItem());
                activityData = documentCollectionFragment;
                if (activityData.validateCancelData()) {
                    intent = new Intent(this, EDSUndeliveredActivity.class);
                    intent.putParcelableArrayListExtra(getString(R.string.data), new ArrayList<>(edsActivityResponseWizards));
                    intent.putExtra("edsResponse", gson.toJson(edsDetailViewModel.edsWithActivityList.get().edsResponse));
                    intent.putExtra(Constants.RESCHEDULE_ENABLE, reschedule_enable);
                    intent.putExtra("awb", awbNo);
                    intent.putExtra(Constants.COMPOSITE_KEY, composite_key);
                    intent.putExtra("navigator", "activity");
                    startActivity(intent);
                } else {
                    //Toast.makeText(EDSDetailActivity.this, "You Cannot Cancel because something is missing i.e Wrong selection or No Option Selected.", Toast.LENGTH_LONG).show();
                    showSnackbar("You Cannot Cancel because something is missing i.e Wrong selection or No Option Selected.");
                }
            } else if (fragment instanceof DocumentVerificationFragment) {
                DocumentVerificationFragment documentVerificationFragment = (DocumentVerificationFragment) edsDetailPagerAdapter.getRegisteredFragment(activityEdsDetailBinding.qcViewPager.getCurrentItem());
                documentVerificationFragment.showMessage(DocumentVerificationFragment.class.getSimpleName());
                activityData = documentVerificationFragment;
                if (activityData.validateCancelData()) {
                    intent = new Intent(this, EDSUndeliveredActivity.class);
                    intent.putParcelableArrayListExtra(getString(R.string.data), new ArrayList<>(edsActivityResponseWizards));
                    intent.putExtra("edsResponse", gson.toJson(edsDetailViewModel.edsWithActivityList.get().edsResponse));
                    intent.putExtra(Constants.RESCHEDULE_ENABLE, reschedule_enable);
                    intent.putExtra("awb", awbNo);
                    intent.putExtra(Constants.COMPOSITE_KEY, composite_key);
                    intent.putExtra("navigator", "activity");
                    startActivity(intent);
                } else {
                    // Toast.makeText(EDSDetailActivity.this, "You Cannot Select Cancel.Because you have selected certain item which you are collecting..", Toast.LENGTH_LONG).show();
                    showSnackbar("You Cannot Select Cancel.Because you have selected certain item which you are collecting..");
                }
            } else if (fragment instanceof CashCollectionFragment) {
                CashCollectionFragment cashCollectionFragment = (CashCollectionFragment) edsDetailPagerAdapter.getRegisteredFragment(activityEdsDetailBinding.qcViewPager.getCurrentItem());
                cashCollectionFragment.showMessage(CashCollectionFragment.class.getSimpleName());
                activityData = cashCollectionFragment;
                if (activityData.validateCancelData()) {
                    intent = new Intent(this, EDSUndeliveredActivity.class);
                    intent.putParcelableArrayListExtra(getString(R.string.data), new ArrayList<>(edsActivityResponseWizards));
                    intent.putExtra("edsResponse", gson.toJson(edsDetailViewModel.edsWithActivityList.get().edsResponse));
                    intent.putExtra(Constants.RESCHEDULE_ENABLE, reschedule_enable);
                    intent.putExtra("awb", awbNo);
                    intent.putExtra(Constants.COMPOSITE_KEY, composite_key);
                    intent.putExtra("navigator", "activity");
                    startActivity(intent);
                } else {
                    // Toast.makeText(EDSDetailActivity.this, "please Select any Option..", Toast.LENGTH_LONG).show();
                    showSnackbar("You Cannot Cancel because something is missing i.e Wrong selection or No Option Selected.");
                }
            } else if (fragment instanceof DocumentListCollectionFragment) {
                DocumentListCollectionFragment documentListCollectionFragment = (DocumentListCollectionFragment) edsDetailPagerAdapter.getRegisteredFragment(activityEdsDetailBinding.qcViewPager.getCurrentItem());
                documentListCollectionFragment.showMessage(DocumentListCollectionFragment.class.getSimpleName());
                activityData = documentListCollectionFragment;
                if (activityData.validateCancelData()) {
                    intent = new Intent(this, EDSUndeliveredActivity.class);
                    intent.putParcelableArrayListExtra(getString(R.string.data), new ArrayList<>(edsActivityResponseWizards));
                    intent.putExtra("edsResponse", gson.toJson(edsDetailViewModel.edsWithActivityList.get().edsResponse));
                    intent.putExtra(Constants.RESCHEDULE_ENABLE, reschedule_enable);
                    intent.putExtra(Constants.COMPOSITE_KEY, composite_key);
                    intent.putExtra("awb", awbNo);
                    intent.putExtra("navigator", "activity");
                    startActivity(intent);
                } else {
                    // Toast.makeText(EDSDetailActivity.this, "You Cannot Select Cancel.Because you have selected certain item which you are collecting..", Toast.LENGTH_LONG).show();
                    showSnackbar("You Cannot Select Cancel.Because you have selected certain item which you are collecting..");
                }
            } else if (fragment instanceof AcDocumentListCollectionFragment) {
                AcDocumentListCollectionFragment acDocumentListCollectionFragment = (AcDocumentListCollectionFragment) edsDetailPagerAdapter.getRegisteredFragment(activityEdsDetailBinding.qcViewPager.getCurrentItem());
                acDocumentListCollectionFragment.showMessage(AcDocumentListCollectionFragment.class.getSimpleName());
                activityData = acDocumentListCollectionFragment;
                if (activityData.validateCancelData()) {
                    intent = new Intent(this, EDSUndeliveredActivity.class);
                    intent.putParcelableArrayListExtra(getString(R.string.data), new ArrayList<>(edsActivityResponseWizards));
                    intent.putExtra("edsResponse", gson.toJson(edsDetailViewModel.edsWithActivityList.get().edsResponse));
                    intent.putExtra(Constants.RESCHEDULE_ENABLE, reschedule_enable);
                    intent.putExtra(Constants.COMPOSITE_KEY, composite_key);
                    intent.putExtra("awb", awbNo);
                    intent.putExtra("navigator", "activity");
                    startActivity(intent);
                } else {
                    // Toast.makeText(EDSDetailActivity.this, "You Cannot Select Cancel.Because you have selected certain item which you are collecting..", Toast.LENGTH_LONG).show();
                    showSnackbar("You Cannot Select Cancel.Because you have selected certain item which you are collecting..");
                }
            } else if (fragment instanceof OpvFragment) {
                OpvFragment opvFragment = (OpvFragment) edsDetailPagerAdapter.getRegisteredFragment(activityEdsDetailBinding.qcViewPager.getCurrentItem());
                opvFragment.showMessage(OpvFragment.class.getSimpleName());
                activityData = opvFragment;
                intent = new Intent(this, EDSUndeliveredActivity.class);
                intent.putParcelableArrayListExtra(getString(R.string.data), new ArrayList<>(edsActivityResponseWizards));
                intent.putExtra("edsResponse", gson.toJson(edsDetailViewModel.edsWithActivityList.get().edsResponse));
                intent.putExtra(Constants.RESCHEDULE_ENABLE, reschedule_enable);
                intent.putExtra(Constants.COMPOSITE_KEY, composite_key);
                intent.putExtra("awb", awbNo);
                intent.putExtra("navigator", "activity");
                startActivity(intent);
            } else if (fragment instanceof EdsEkycAntWorkFragment) {
                EdsEkycAntWorkFragment edsEkycAntWorkFragment = (EdsEkycAntWorkFragment) edsDetailPagerAdapter.getRegisteredFragment(activityEdsDetailBinding.qcViewPager.getCurrentItem());
                edsEkycAntWorkFragment.showMessage(OpvFragment.class.getSimpleName());
                activityData = edsEkycAntWorkFragment;
                intent = new Intent(this, EDSUndeliveredActivity.class);
                intent.putParcelableArrayListExtra(getString(R.string.data), new ArrayList<>(edsActivityResponseWizards));
                intent.putExtra("edsResponse", gson.toJson(edsDetailViewModel.edsWithActivityList.get().edsResponse));
                intent.putExtra(Constants.RESCHEDULE_ENABLE, reschedule_enable);
                intent.putExtra(Constants.COMPOSITE_KEY, composite_key);
                intent.putExtra("awb", awbNo);
                intent.putExtra("navigator", "activity");
                startActivity(intent);
            } else if (fragment instanceof ResOpvFragment) {
                ResOpvFragment resOpvFragment = (ResOpvFragment) edsDetailPagerAdapter.getRegisteredFragment(activityEdsDetailBinding.qcViewPager.getCurrentItem());
                resOpvFragment.showMessage(ResOpvFragment.class.getSimpleName());
                activityData = resOpvFragment;
                intent = new Intent(this, EDSUndeliveredActivity.class);
                intent.putParcelableArrayListExtra(getString(R.string.data), new ArrayList<>(edsActivityResponseWizards));
                intent.putExtra("edsResponse", gson.toJson(edsDetailViewModel.edsWithActivityList.get().edsResponse));
                intent.putExtra(Constants.RESCHEDULE_ENABLE, reschedule_enable);
                intent.putExtra(Constants.COMPOSITE_KEY, composite_key);
                intent.putExtra("awb", awbNo);
                intent.putExtra("navigator", "activity");
                startActivity(intent);
            } else if (fragment instanceof CaptureImageFragment) {
                CaptureImageFragment captureImageFragment = (CaptureImageFragment) edsDetailPagerAdapter.getRegisteredFragment(activityEdsDetailBinding.qcViewPager.getCurrentItem());
                captureImageFragment.showMessage(CaptureImageFragment.class.getSimpleName());
                activityData = captureImageFragment;
                intent = new Intent(this, EDSUndeliveredActivity.class);
                intent.putParcelableArrayListExtra(getString(R.string.data), new ArrayList<>(edsActivityResponseWizards));
                intent.putExtra("edsResponse", gson.toJson(edsDetailViewModel.edsWithActivityList.get().edsResponse));
                intent.putExtra(Constants.RESCHEDULE_ENABLE, reschedule_enable);
                intent.putExtra(Constants.COMPOSITE_KEY, composite_key);
                intent.putExtra("awb", awbNo);
                intent.putExtra("navigator", "activity");
                startActivity(intent);
            } else if (fragment instanceof VodafoneFragment) {
                VodafoneFragment vodafoneFragment = (VodafoneFragment) edsDetailPagerAdapter.getRegisteredFragment(activityEdsDetailBinding.qcViewPager.getCurrentItem());
                vodafoneFragment.showMessage(VodafoneFragment.class.getSimpleName());
                activityData = vodafoneFragment;
                if (activityData.validateCancelData()) {
                    intent = new Intent(this, EDSUndeliveredActivity.class);
                    intent.putParcelableArrayListExtra(getString(R.string.data), new ArrayList<>(edsActivityResponseWizards));
                    intent.putExtra("edsResponse", gson.toJson(edsDetailViewModel.edsWithActivityList.get().edsResponse));
                    intent.putExtra(Constants.RESCHEDULE_ENABLE, reschedule_enable);
                    intent.putExtra(Constants.COMPOSITE_KEY, composite_key);
                    intent.putExtra("awb", awbNo);
                    intent.putExtra("navigator", "activity");
                    startActivity(intent);
                } else {
                    showSnackbar("You Cannot Select Cancel.Because you have Completed your KYC through Vodafone App..");
                    // Toast.makeText(EDSDetailActivity.this, "You Cannot Select Cancel.Because you have Completed your KYC through Vodafone App..", Toast.LENGTH_LONG).show();
                }
            } else if (fragment instanceof EdsEkycXMLFragment) {
                EdsEkycXMLFragment edsEkycXMLFragment = (EdsEkycXMLFragment) edsDetailPagerAdapter.getRegisteredFragment(activityEdsDetailBinding.qcViewPager.getCurrentItem());
                edsEkycXMLFragment.showMessage(EdsEkycXMLFragment.class.getSimpleName());
                activityData = edsEkycXMLFragment;
                if (activityData.validateCancelData()) {
                    intent = new Intent(this, EDSUndeliveredActivity.class);
                    intent.putParcelableArrayListExtra(getString(R.string.data), new ArrayList<>(edsActivityResponseWizards));
                    intent.putExtra("edsResponse", gson.toJson(edsDetailViewModel.edsWithActivityList.get().edsResponse));
                    intent.putExtra(Constants.RESCHEDULE_ENABLE, reschedule_enable);
                    intent.putExtra(Constants.COMPOSITE_KEY, composite_key);
                    intent.putExtra("awb", awbNo);
                    intent.putExtra(Constants.COMPOSITE_KEY, composite_key);
                    intent.putExtra("navigator", "activity");
                    startActivity(intent);
                } else {
                    showSnackbar("You Cannot Select Cancel.Because you have Completed your KYC through Vodafone App..");
                    // Toast.makeText(EDSDetailActivity.this, "You Cannot Select Cancel.Because you have Completed your KYC through Vodafone App..", Toast.LENGTH_LONG).show();
                }
            } else if (fragment instanceof EdsRblFragment) {
                EdsRblFragment edsRblFragment = (EdsRblFragment) edsDetailPagerAdapter.getRegisteredFragment(activityEdsDetailBinding.qcViewPager.getCurrentItem());
                activityData = edsRblFragment;
                intent = new Intent(this, EDSUndeliveredActivity.class);
                intent.putParcelableArrayListExtra(getString(R.string.data), new ArrayList<>(edsActivityResponseWizards));
                intent.putExtra("edsResponse", gson.toJson(edsDetailViewModel.edsWithActivityList.get().edsResponse));
                intent.putExtra(Constants.RESCHEDULE_ENABLE, reschedule_enable);
                intent.putExtra(Constants.COMPOSITE_KEY, composite_key);
                intent.putExtra("awb", awbNo);
                intent.putExtra(Constants.COMPOSITE_KEY, composite_key);
                intent.putExtra("navigator", "activity");
                startActivity(intent);
            } else if (fragment instanceof IciciEkycFragment) {
                IciciEkycFragment iciciEkycFragment = (IciciEkycFragment) edsDetailPagerAdapter.getRegisteredFragment(activityEdsDetailBinding.qcViewPager.getCurrentItem());
                iciciEkycFragment.showMessage(IciciEkycFragment.class.getSimpleName());
                activityData = iciciEkycFragment;
                if (activityData.validateCancelData()) {
                    intent = new Intent(this, EDSUndeliveredActivity.class);
                    intent.putParcelableArrayListExtra(getString(R.string.data), new ArrayList<>(edsActivityResponseWizards));
                    intent.putExtra("edsResponse", gson.toJson(edsDetailViewModel.edsWithActivityList.get().edsResponse));
                    intent.putExtra(Constants.RESCHEDULE_ENABLE, reschedule_enable);
                    intent.putExtra("awb", awbNo);
                    intent.putExtra(Constants.COMPOSITE_KEY, composite_key);
                    intent.putExtra("navigator", "activity");
                    startActivity(intent);
                } else {
                    showSnackbar("You Cannot Select Cancel.Because you have Completed your KYC ..");
                    // Toast.makeText(EDSDetailActivity.this, "You Cannot Select Cancel.Because you have Completed your KYC through Vodafone App..", Toast.LENGTH_LONG).show();
                }
            } else if (fragment instanceof IciciEkycFragment_standard) {
                IciciEkycFragment_standard iciciEkycFragment = (IciciEkycFragment_standard) edsDetailPagerAdapter.getRegisteredFragment(activityEdsDetailBinding.qcViewPager.getCurrentItem());
                iciciEkycFragment.showMessage(IciciEkycFragment_standard.class.getSimpleName());
                activityData = iciciEkycFragment;
                if (activityData.validateCancelData()) {
                    intent = new Intent(this, EDSUndeliveredActivity.class);
                    intent.putParcelableArrayListExtra(getString(R.string.data), new ArrayList<>(edsActivityResponseWizards));
                    intent.putExtra("edsResponse", gson.toJson(edsDetailViewModel.edsWithActivityList.get().edsResponse));
                    intent.putExtra(Constants.RESCHEDULE_ENABLE, reschedule_enable);
                    intent.putExtra("awb", awbNo);
                    intent.putExtra(Constants.COMPOSITE_KEY, composite_key);
                    intent.putExtra("navigator", "activity");
                    startActivity(intent);
                } else {
                    showSnackbar("You Cannot Select Cancel.Because you have Completed your KYC ..");
                    // Toast.makeText(EDSDetailActivity.this, "You Cannot Select Cancel.Because you have Completed your KYC through Vodafone App..", Toast.LENGTH_LONG).show();
                }
            } else if (fragment instanceof EdsBkycIdfcFragment) {
                EdsBkycIdfcFragment edsBkycIdfcFragment = (EdsBkycIdfcFragment) edsDetailPagerAdapter.getRegisteredFragment(activityEdsDetailBinding.qcViewPager.getCurrentItem());
                edsBkycIdfcFragment.showMessage(EdsBkycIdfcFragment.class.getSimpleName());
                activityData = edsBkycIdfcFragment;
                if (activityData.validateCancelData()) {
                    intent = new Intent(this, EDSUndeliveredActivity.class);
                    intent.putParcelableArrayListExtra(getString(R.string.data), new ArrayList<>(edsActivityResponseWizards));
                    intent.putExtra("edsResponse", gson.toJson(edsDetailViewModel.edsWithActivityList.get().edsResponse));
                    intent.putExtra(Constants.RESCHEDULE_ENABLE, reschedule_enable);
                    intent.putExtra("awb", awbNo);
                    intent.putExtra(Constants.COMPOSITE_KEY, composite_key);
                    intent.putExtra("navigator", "activity");
                    startActivity(intent);
                } else {
                    showSnackbar("You Cannot Select Cancel.Because you have Completed your KYC ..");
                    // Toast.makeText(EDSDetailActivity.this, "You Cannot Select Cancel.Because you have Completed your KYC through Vodafone App..", Toast.LENGTH_LONG).show();
                }
            } else if (fragment instanceof EdsEkycIdfcFragment) {
                EdsEkycIdfcFragment edsEkycIdfcFragment = (EdsEkycIdfcFragment) edsDetailPagerAdapter.getRegisteredFragment(activityEdsDetailBinding.qcViewPager.getCurrentItem());
                edsEkycIdfcFragment.showMessage(EdsBkycIdfcFragment.class.getSimpleName());
                activityData = edsEkycIdfcFragment;
                if (activityData.validateCancelData()) {
                    intent = new Intent(this, EDSUndeliveredActivity.class);
                    intent.putParcelableArrayListExtra(getString(R.string.data), new ArrayList<>(edsActivityResponseWizards));
                    intent.putExtra("edsResponse", gson.toJson(edsDetailViewModel.edsWithActivityList.get().edsResponse));
                    intent.putExtra(Constants.RESCHEDULE_ENABLE, reschedule_enable);
                    intent.putExtra("awb", awbNo);
                    intent.putExtra(Constants.COMPOSITE_KEY, composite_key);
                    intent.putExtra("navigator", "activity");
                    startActivity(intent);
                } else {
                    showSnackbar("You Cannot Select Cancel.Because you have Completed your KYC ..");
                    // Toast.makeText(EDSDetailActivity.this, "You Cannot Select Cancel.Because you have Completed your KYC through Vodafone App..", Toast.LENGTH_LONG).show();
                }
            } else if (fragment instanceof EdsEkycNiyoFragment) {
                EdsEkycNiyoFragment edsEkycNiyoFragment = (EdsEkycNiyoFragment) edsDetailPagerAdapter.getRegisteredFragment(activityEdsDetailBinding.qcViewPager.getCurrentItem());
                edsEkycNiyoFragment.showMessage(EdsEkycNiyoFragment.class.getSimpleName());
                activityData = edsEkycNiyoFragment;
                if (activityData.validateCancelData()) {
                    intent = new Intent(this, EDSUndeliveredActivity.class);
                    intent.putParcelableArrayListExtra(getString(R.string.data), new ArrayList<>(edsActivityResponseWizards));
                    intent.putExtra("edsResponse", gson.toJson(edsDetailViewModel.edsWithActivityList.get().edsResponse));
                    intent.putExtra(Constants.RESCHEDULE_ENABLE, reschedule_enable);
                    intent.putExtra("awb", awbNo);
                    intent.putExtra(Constants.COMPOSITE_KEY, composite_key);
                    intent.putExtra("navigator", "activity");
                    startActivity(intent);
                } else {
                    showSnackbar("You Cannot Select Cancel.Because you have Completed your KYC ..");
                    // Toast.makeText(EDSDetailActivity.this, "You Cannot Select Cancel.Because you have Completed your KYC through Vodafone App..", Toast.LENGTH_LONG).show();
                }
            } else if (fragment instanceof PaytmFragment) {
                PaytmFragment paytmFragment = (PaytmFragment) edsDetailPagerAdapter.getRegisteredFragment(activityEdsDetailBinding.qcViewPager.getCurrentItem());
                paytmFragment.showMessage(EdsEkycNiyoFragment.class.getSimpleName());
                activityData = paytmFragment;
                if (activityData.validateCancelData()) {
                    intent = new Intent(this, EDSUndeliveredActivity.class);
                    intent.putParcelableArrayListExtra(getString(R.string.data), new ArrayList<>(edsActivityResponseWizards));
                    intent.putExtra("edsResponse", gson.toJson(edsDetailViewModel.edsWithActivityList.get().edsResponse));
                    intent.putExtra(Constants.RESCHEDULE_ENABLE, reschedule_enable);
                    intent.putExtra("awb", awbNo);
                    intent.putExtra(Constants.COMPOSITE_KEY, composite_key);
                    intent.putExtra("navigator", "activity");
                    startActivity(intent);
                } else if (!activityData.validateCancelData()) {
                    intent = new Intent(this, EDSUndeliveredActivity.class);
                    intent.putParcelableArrayListExtra(getString(R.string.data), new ArrayList<>(edsActivityResponseWizards));
                    intent.putExtra("edsResponse", gson.toJson(edsDetailViewModel.edsWithActivityList.get().edsResponse));
                    intent.putExtra(Constants.RESCHEDULE_ENABLE, reschedule_enable);
                    intent.putExtra("awb", awbNo);
                    intent.putExtra("is_already_kyced", true);
                    intent.putExtra(Constants.COMPOSITE_KEY, composite_key);
                    intent.putExtra("navigator", "activity");
                    startActivity(intent);
                } else if (fragment instanceof EdsEkycPaytmFragment) {
                    EdsEkycPaytmFragment edsEkycPaytmFragment = (EdsEkycPaytmFragment) edsDetailPagerAdapter.getRegisteredFragment(activityEdsDetailBinding.qcViewPager.getCurrentItem());
                    edsEkycPaytmFragment.showMessage(EdsEkycPaytmFragment.class.getSimpleName());
                    activityData = edsEkycPaytmFragment;
                    intent = new Intent(this, EDSUndeliveredActivity.class);
                    intent.putParcelableArrayListExtra(getString(R.string.data), new ArrayList<>(edsActivityResponseWizards));
                    intent.putExtra("edsResponse", gson.toJson(edsDetailViewModel.edsWithActivityList.get().edsResponse));
                    intent.putExtra(Constants.RESCHEDULE_ENABLE, reschedule_enable);
                    intent.putExtra("awb", awbNo);
                    intent.putExtra(Constants.COMPOSITE_KEY, composite_key);
                    intent.putExtra("navigator", "activity");
                    startActivity(intent);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            showSnackbar(e.getMessage());
        }
    }

    private void getd() {
        activityNameCount++;
        if (edsWithActivityList.edsActivityWizards.size() > activityNameCount)
            getActivityName(edsWithActivityList, activityNameCount, "true");
    }

    public void getFragmentData(boolean status, EDSActivityResponseWizard edsActivityResponseWizard, BaseFragment fragment) {
        Log.d(TAG, "getFragmentData: " + edsActivityResponseWizard.toString());
        edsActivityResponseWizards.add(edsActivityResponseWizard);
        successFragment.put(fragment, status);
    }

    private void proceedToNextActivity() {
        try {
            Intent intent = null;
            if (edsWithActivityList.edsResponse.getShipmentDetail().getFlag().isIdDcEnabled()) {
                //scan
                intent = new Intent(this, CaptureScanActivity.class);
            } else {
                //signature
                intent = new Intent(this, EDSSignatureActivity.class);
            }
            intent.putParcelableArrayListExtra(getString(R.string.data), new ArrayList<>(edsActivityResponseWizards));
            intent.putExtra("edsResponse", gson.toJson(edsDetailViewModel.edsWithActivityList.get().edsResponse));
            intent.putExtra(Constants.COMPOSITE_KEY, composite_key);
            intent.putExtra("awb", awbNo);
            startActivity(intent);
            //        finish();
        } catch (Exception e) {
            e.printStackTrace();
            showSnackbar(e.getMessage());
        }
    }

    private int showFragmentPosition(boolean isNext) {
        if (isNext) {
            if (edsDetailViewModel.edsWithActivityList.get() != null && edsDetailViewModel.edsWithActivityList.get().edsActivityWizards != null && edsDetailViewModel.edsWithActivityList.get().edsActivityWizards.size() > count) {
                return ++count;
            } else {
                return -1;
            }
        } else {
            if (count > 0) {
                return --count;
            } else {
                return count = -1;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (requestCode == REQUEST_CODE_SCAN) {
                handleScanResult(data);
            } else if (resultCode == RESULT_OK) {
                if (requestCode == CAMERA_SCANNER_CODE) {
                    String path = data.getStringExtra("croped_path");
                    Bitmap bitmap = BitmapFactory.decodeFile(path);
                    digitalCropImageHandler.sendImage(bitmap, path);
                } else if (requestCode == 1) {
                    try {
                        if (data != null) {
                            value = new ArrayList<>();
                            String result = data.getStringExtra("DEVICE_INFO");
                            if (result != null) {
                                info = serializer.read(DeviceInfo.class, result);
                                rdsId = info.rdsId;
                                rdsId = info.rdsId;
                                dpId = info.dpId;
                                dc = info.dc;
                                mi = info.mi;
                                mc = info.mc;
                                rdsVer = info.mc;
                                List<Param> data1 = info.add_info.params;
                                for (int i = 0; i < data1.size(); i++) {
                                    value.add(data1.get(i).value);
                                }
                                idc = value.get(0);
                            }
                            if (mi.isEmpty()) {
                                // new Helper().showAlert(Icici_Ekyc.this, "Device is not Attached Properly", "Alert");
                                // Toast.makeText(getApplication(), "", Toast.LENGTH_LONG).show();
                            } else {
                                if (dpId.equalsIgnoreCase("MANTRA.MSIPL"))
                                    getcapture();
                                else {
                                    //                                getPopupAlert();
                                }
                            }
                        }
                    } catch (Exception e) {
                        Log.e("Error", "Error while deserialze device info", e);
                    }
                } else if (requestCode == 2) {
                    try {
                        pidDataJson = new JSONObject();
                        if (data != null) {
                            String result_resp = data.getStringExtra("PID_DATA");
                            XmlToJson xmlToJson = new XmlToJson.Builder(result_resp).build();
                            //    Log.e("json", xmlToJson.toFormattedString());
                            pidDataJson = new JSONObject(xmlToJson.toFormattedString());
                            JSONObject jobj_piddata = pidDataJson.getJSONObject("PidData").getJSONObject("Resp");
                            // JSONObject jobj_resp = jobj_piddata.getJSONObject("Resp");
                            // JSONObject jobj_resp = jobj_piddata.getJSONObject("Resp");
                            if (jobj_piddata.getString("errCode").equalsIgnoreCase("0")) {
                                fragment = edsDetailPagerAdapter.getItem(activityEdsDetailBinding.qcViewPager.getCurrentItem());
                                // EdsEkycXMLFragment edsEkycXMLFragment = (EdsEkycXMLFragment) edsDetailPagerAdapter.getRegisteredFragment(activityEdsDetailBinding.qcViewPager.getCurrentItem());
                                if (fragment instanceof EdsEkycXMLFragment) {
                                    EdsEkycXMLFragment edsEkycXMLFragment = (EdsEkycXMLFragment) edsDetailPagerAdapter.getRegisteredFragment(activityEdsDetailBinding.qcViewPager.getCurrentItem());
                                    edsEkycXMLFragment.sendData(result_resp);
                                }
                                if (fragment instanceof IciciEkycFragment) {
                                    IciciEkycFragment iciciEkycFragment = (IciciEkycFragment) edsDetailPagerAdapter.getRegisteredFragment(activityEdsDetailBinding.qcViewPager.getCurrentItem());
                                    iciciEkycFragment.sendData(pidDataJson.toString());
                                }
                                if (fragment instanceof IciciEkycFragment_standard) {
                                    IciciEkycFragment_standard iciciEkycFragment_standard = (IciciEkycFragment_standard) edsDetailPagerAdapter.getRegisteredFragment(activityEdsDetailBinding.qcViewPager.getCurrentItem());
                                    iciciEkycFragment_standard.sendData(pidDataJson.toString());
                                }
                                if (fragment instanceof EdsRblFragment) {
                                    EdsRblFragment edsRblFragment = (EdsRblFragment) edsDetailPagerAdapter.getRegisteredFragment(activityEdsDetailBinding.qcViewPager.getCurrentItem());
                                    edsRblFragment.sendData(pidDataJson.toString());
                                }
                                if (fragment instanceof EdsBkycIdfcFragment) {
                                    EdsBkycIdfcFragment edsBkycIdfcFragment = (EdsBkycIdfcFragment) edsDetailPagerAdapter.getRegisteredFragment(activityEdsDetailBinding.qcViewPager.getCurrentItem());
                                    edsBkycIdfcFragment.sendData(pidDataJson.toString());//
                                    //  (pid//DataJson.toString());
                                }
                                if (fragment instanceof EdsEkycIdfcFragment) {
                                    EdsEkycIdfcFragment edsEkycIdfcFragment = (EdsEkycIdfcFragment) edsDetailPagerAdapter.getRegisteredFragment(activityEdsDetailBinding.qcViewPager.getCurrentItem());
                                    edsEkycIdfcFragment.sendData(String.valueOf(result_resp), String.valueOf(awbNo), order_id);
                                }
                                if (fragment instanceof EdsEkycNiyoFragment) {
                                    EdsEkycNiyoFragment edsEkycNiyoFragment = (EdsEkycNiyoFragment) edsDetailPagerAdapter.getRegisteredFragment(activityEdsDetailBinding.qcViewPager.getCurrentItem());
                                    edsEkycNiyoFragment.sendData(pidDataJson.toString());
                                }
                                if (fragment instanceof EdsEkycAntWorkFragment) {
                                    EdsEkycAntWorkFragment edsEkycAntWorkFragment = (EdsEkycAntWorkFragment) edsDetailPagerAdapter.getRegisteredFragment(activityEdsDetailBinding.qcViewPager.getCurrentItem());
                                    edsEkycAntWorkFragment.sendData(result_resp, String.valueOf(awbNo), order_id);
                                }
                                if (fragment instanceof EdsEkycFreyoFragment) {
                                    EdsEkycFreyoFragment edsEkycFreyoFragment = (EdsEkycFreyoFragment) edsDetailPagerAdapter.getRegisteredFragment(activityEdsDetailBinding.qcViewPager.getCurrentItem());
                                    edsEkycFreyoFragment.sendData(pidDataJson.toString(), String.valueOf(awbNo), order_id);
                                }
                            } else {
                                showSnackbar(jobj_piddata.getString("errInfo"));
                            }
                        }
                    } catch (Exception e) {
                        Log.e("Error", "Error while deserialze pid data", e);
                    }
                    } else {
                        imageHandler.onActivityResult(requestCode, resultCode, data);
                    }
                    super.onActivityResult(requestCode, resultCode, data);
                } else {
                    imageHandler.onActivityResult(requestCode, resultCode, data);
                }
        } catch (Exception e) {
            e.printStackTrace();
            showSnackbar(e.getMessage());
        }
    }

    private void handleScanResult(Intent data) {
        if (data != null) {
            Constants.SCANNED_DATA = data.getStringExtra(ScannerActivity.SCANNED_CODE);
        }
    }

    public void scanBarcode() {
        Intent intent = new Intent(EDSDetailActivity.this, ScannerActivity.class);
        startActivityForResult(intent, REQUEST_CODE_SCAN);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        Log.e("onPageScrolled", position + "");
        fragment = edsDetailPagerAdapter.getItem(activityEdsDetailBinding.qcViewPager.getCurrentItem());
        if (fragment instanceof DocumentListCollectionFragment) {
            documentData = (DocumentListCollectionFragment) edsDetailPagerAdapter.getRegisteredFragment(activityEdsDetailBinding.qcViewPager.getCurrentItem());
            documentData.setDetail(position, edsWithActivityList.edsActivityWizards.get(position));
        }
    }

    @Override
    public void onPageSelected(int position) {
        arrayList_image_validation.clear();
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    public void getcapture() {
        try {
            fragment = edsDetailPagerAdapter.getItem(activityEdsDetailBinding.qcViewPager.getCurrentItem());
            String pidOption;
            if (fragment instanceof EdsEkycNiyoFragment) {
                pidOption = getPIDOptionniyo();
            } else if (fragment instanceof EdsBkycIdfcFragment) {
                pidOption = getPIDOptionsidfc();
            } else if (fragment instanceof EdsEkycXMLFragment) {
                pidOption = getPIDOptionsFincare();
            } else if (fragment instanceof EdsEkycFreyoFragment) {
                pidOption = getPIDOptionsFreyo();
            } else if (fragment instanceof EdsEkycIdfcFragment) {
                pidOption = getPIDOptionsIDFC();
            } else if (fragment instanceof IciciEkycFragment_standard) {
                pidOption = getPIDOptionsICICIStandard();
            } else {
                pidOption = getPIDOptions();
            }
            if (pidOption != null) {
                //  Log.e("PidOptions", pidOption);
                Intent intent2 = new Intent();
                intent2.setAction("in.gov.uidai.rdservice.fp.CAPTURE");
                intent2.putExtra("PID_OPTIONS", pidOption);
                startActivityForResult(intent2, 2);
            }
        } catch (Exception e) {
            //   Log.e("Error", e.toString());
        }
    }

    private String getPIDOptionsFincare() {
        try {
            int fingerCount = 1;
            int fingerFormat;
            fingerFormat = Constants.IS_ICICI_FINKARE;
            String pidVer = "2.0";
            String timeOut = "10000";
            String posh = "UNKNOWN";
            if (!positions.isEmpty()) {
                posh = positions.toString().replace("[", "").replace("]", "").replaceAll("[\\s+]", "");
            }
            Opts opts = new Opts();
            opts.fCount = String.valueOf(fingerCount);
            opts.fType = edsWithActivityList.edsActivityWizards.get(activityEdsDetailBinding.qcViewPager.getCurrentItem()).getQuestionFormFields().getFtype();
            opts.iCount = "0";
            opts.iType = "0";
            opts.pCount = "0";
            opts.pType = "0";
            opts.format = String.valueOf(fingerFormat);
            opts.pidVer = pidVer;
            opts.timeout = timeOut;
            opts.posh = posh;
            opts.env = "P";
            opts.wadh = getWADH("2.5FYNNN");
            PidOptions pidOptions = new PidOptions();
            pidOptions.ver = "2.0";
            pidOptions.Opts = opts;
            Serializer serializer = new Persister();
            StringWriter writer = new StringWriter();
            serializer.write(pidOptions, writer);
            return writer.toString();
        } catch (Exception e) {
            //    Log.e("Error", e.toString());
        }
        return null;
    }

    private String getPIDOptionniyo() {
        try {

            Constants.TEMP_FYPE_NIYO = edsWithActivityList.edsActivityWizards.get(activityEdsDetailBinding.qcViewPager.getCurrentItem()).getQuestionFormFields().getFtype();

            int fingerCount = 1;
            int fingerFormat = 0;
            String pidVer = "2.0";
            String timeOut = "10000";
            String posh = "UNKNOWN";
            if (!positions.isEmpty()) {
                posh = positions.toString().replace("[", "").replace("]", "").replaceAll("[\\s+]", "");
            }
            Opts opts = new Opts();
            opts.fCount = String.valueOf(fingerCount);
            opts.fType = edsWithActivityList.edsActivityWizards.get(activityEdsDetailBinding.qcViewPager.getCurrentItem()).getQuestionFormFields().getFtype();
            opts.iCount = "0";
            opts.iType = "0";
            opts.pCount = "0";
            opts.pType = "0";
            opts.format = String.valueOf(fingerFormat);
            opts.pidVer = pidVer;
            opts.timeout = timeOut;
            opts.posh = posh;
            opts.env = "P";
            if (edsDetailViewModel.getDataManager().getLiveTrackingSpeed() == 120) {
                opts.wadh = getWADH("2.5FYNNY");
            } else {
                opts.wadh = getWADH("2.5FYNNN");
            }
            PidOptions pidOptions = new PidOptions();
            pidOptions.ver = "2.0";
            pidOptions.Opts = opts;
            Serializer serializer = new Persister();
            StringWriter writer = new StringWriter();
            serializer.write(pidOptions, writer);
            return writer.toString();
        } catch (Exception e) {
            //    Log.e("Error", e.toString());
        }
        return null;
    }

    private String getPIDOptionsidfc() {
        try {
            int fingerCount = 1;
            int fingerFormat;
            fingerFormat = 1;
            String pidVer = "2.0";
            String timeOut = "10000";
            String posh = "UNKNOWN";
            if (!positions.isEmpty()) {
                posh = positions.toString().replace("[", "").replace("]", "").replaceAll("[\\s+]", "");
            }
            Opts opts = new Opts();
            opts.fCount = String.valueOf(fingerCount);
            opts.fType = edsWithActivityList.edsActivityWizards.get(activityEdsDetailBinding.qcViewPager.getCurrentItem()).getQuestionFormFields().getFtype();
            opts.iCount = "0";
            opts.iType = "0";
            opts.pCount = "0";
            opts.pType = "0";
            opts.format = String.valueOf(fingerFormat);
            opts.pidVer = pidVer;
            opts.timeout = timeOut;
            opts.posh = posh;
            opts.env = "P";
            PidOptions pidOptions = new PidOptions();
            pidOptions.ver = "2.0";
            pidOptions.Opts = opts;
            Serializer serializer = new Persister();
            StringWriter writer = new StringWriter();
            serializer.write(pidOptions, writer);
            return writer.toString();
        } catch (Exception e) {
            //    Log.e("Error", e.toString());
        }
        return null;
    }

    private String getPIDOptionsFreyo() {
        try {
            Constants.TEMP_FYPE_NIYO = edsWithActivityList.edsActivityWizards.get(activityEdsDetailBinding.qcViewPager.getCurrentItem()).getQuestionFormFields().getFtype();
            int fingerCount = 1;
            int fingerFormat;
            fingerFormat = Constants.IS_ICICI_FINKARE;
            String pidVer = "2.0";
            String timeOut = "10000";
            String posh = "UNKNOWN";
            if (!positions.isEmpty()) {
                posh = positions.toString().replace("[", "").replace("]", "").replaceAll("[\\s+]", "");
            }
            Opts opts = new Opts();
            opts.fCount = String.valueOf(fingerCount);
            opts.fType = edsWithActivityList.edsActivityWizards.get(activityEdsDetailBinding.qcViewPager.getCurrentItem()).getQuestionFormFields().getFtype();
            opts.iCount = "0";
            opts.iType = "0";
            opts.pCount = "0";
            opts.pType = "0";
            opts.format = String.valueOf(fingerFormat);
            opts.pidVer = pidVer;
            opts.timeout = timeOut;
            opts.posh = posh;
            opts.env = "P";
            opts.wadh = getWADH("2.5FYNNY");
            PidOptions pidOptions = new PidOptions();
            pidOptions.ver = "2.0";
            pidOptions.Opts = opts;
            Serializer serializer = new Persister();
            StringWriter writer = new StringWriter();
            serializer.write(pidOptions, writer);
            return writer.toString();
        } catch (Exception e) {
            //    Log.e("Error", e.toString());
        }
        return null;
    }

    private String getPIDOptionsICICIStandard() {
        try {
            Constants.TEMP_FYPE_NIYO = edsWithActivityList.edsActivityWizards.get(activityEdsDetailBinding.qcViewPager.getCurrentItem()).getQuestionFormFields().getFtype();
            int fingerCount = 1;
            int fingerFormat;
            fingerFormat = Constants.IS_ICICI_FINKARE;
            String pidVer = "2.0";
            String timeOut = "10000";
            String posh = "UNKNOWN";
            if (!positions.isEmpty()) {
                posh = positions.toString().replace("[", "").replace("]", "").replaceAll("[\\s+]", "");
            }
            Opts opts = new Opts();
            opts.fCount = String.valueOf(fingerCount);
            opts.fType = edsWithActivityList.edsActivityWizards.get(activityEdsDetailBinding.qcViewPager.getCurrentItem()).getQuestionFormFields().getFtype();
            opts.iCount = "0";
            opts.iType = "0";
            opts.pCount = "0";
            opts.pType = "0";
            opts.format = String.valueOf(fingerFormat);
            opts.pidVer = pidVer;
            opts.timeout = timeOut;
            opts.posh = posh;
            opts.env = "P";
            opts.wadh = edsWithActivityList.edsActivityWizards.get(activityEdsDetailBinding.qcViewPager.getCurrentItem()).getQuestionFormFields().getWadh_value();
            //opts.wadh = "E0jzJ/P8UopUHAieZn8CKqS4WPMi5ZSYXgfnlfkWjrc";
            PidOptions pidOptions = new PidOptions();
            pidOptions.ver = "2.0";
            pidOptions.Opts = opts;
            Serializer serializer = new Persister();
            StringWriter writer = new StringWriter();
            serializer.write(pidOptions, writer);
            return writer.toString();
        } catch (Exception e) {
            //    Log.e("Error", e.toString());
        }
        return null;
    }

    private String getPIDOptionsIDFC() {
        try {
            int fingerCount = 1;
            int fingerType = 0;
            int fingerFormat = 0;
            fingerFormat = Constants.IS_ICICI_FINKARE;
            String pidVer = "2.0";
            String timeOut = "10000";
            String posh = "UNKNOWN";
            if (positions.size() > 0) {
                posh = positions.toString().replace("[", "").replace("]", "").replaceAll("[\\s+]", "");
            }
            Opts opts = new Opts();
            opts.fCount = String.valueOf(fingerCount);
            opts.fType = edsWithActivityList.edsActivityWizards.get(activityEdsDetailBinding.qcViewPager.getCurrentItem()).getQuestionFormFields().getFtype();
            opts.iCount = "0";
            opts.iType = "0";
            opts.pCount = "0";
            opts.pType = "0";
            opts.format = String.valueOf(fingerFormat);
            opts.pidVer = pidVer;
            opts.timeout = timeOut;
            opts.posh = posh;
            opts.env = "P";
            opts.wadh = getWADH("2.5FYNNY");
            //opts.wadh = "E0jzJ/P8UopUHAieZn8CKqS4WPMi5ZSYXgfnlfkWjrc";
            PidOptions pidOptions = new PidOptions();
            pidOptions.ver = "2.0";
            pidOptions.Opts = opts;
            Serializer serializer = new Persister();
            StringWriter writer = new StringWriter();
            serializer.write(pidOptions, writer);
            return writer.toString();
        } catch (Exception e) {
            //    Log.e("Error", e.toString());
        }
        return null;
    }

    private String getPIDOptions() {
        try {
            Constants.TEMP_FYPE_NIYO = edsWithActivityList.edsActivityWizards.get(activityEdsDetailBinding.qcViewPager.getCurrentItem()).getQuestionFormFields().getFtype();
            int fingerCount = 1;
            int fingerType = 0;
            int fingerFormat = 0;
            fingerFormat = Constants.IS_ICICI_FINKARE;
            String pidVer = "2.0";
            String timeOut = "10000";
            String posh = "UNKNOWN";
            if (positions.size() > 0) {
                posh = positions.toString().replace("[", "").replace("]", "").replaceAll("[\\s+]", "");
            }
            Opts opts = new Opts();
            opts.fCount = String.valueOf(fingerCount);
            opts.fType = edsWithActivityList.edsActivityWizards.get(activityEdsDetailBinding.qcViewPager.getCurrentItem()).getQuestionFormFields().getFtype();
            opts.iCount = "0";
            opts.iType = "0";
            opts.pCount = "0";
            opts.pType = "0";
            opts.format = String.valueOf(fingerFormat);
            opts.pidVer = pidVer;
            opts.timeout = timeOut;
            opts.posh = posh;
            opts.env = "P";
            opts.wadh = getWADH("2.5FYNNN");
            //opts.wadh = "E0jzJ/P8UopUHAieZn8CKqS4WPMi5ZSYXgfnlfkWjrc";
            PidOptions pidOptions = new PidOptions();
            pidOptions.ver = "2.0";
            pidOptions.Opts = opts;
            Serializer serializer = new Persister();
            StringWriter writer = new StringWriter();
            serializer.write(pidOptions, writer);
            return writer.toString();
        } catch (Exception e) {
            //    Log.e("Error", e.toString());
        }
        return null;
    }

    private String getCurrentTimeStamp() {
        Date date = new java.util.Date();
        return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").format(date);
    }

    private String getWADH(String s) throws Exception {
        MessageDigest digest = null;
        digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(s.getBytes("UTF-8"));
        return Base64.encodeToString(hash, Base64.NO_WRAP);
    }

    private final BroadcastReceiver listener = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int size = intent.getIntExtra("SET_SIZE", 0);
        }
    };

    @Override
    protected void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(listener);
        super.onDestroy();
    }

    public void uploadAAdharImage() {
        if (!front_image_uri.equalsIgnoreCase("")) {
            if (!rear_image_uri.equalsIgnoreCase("")) {
                uploadAadharImages(front_image_uri, rear_image_uri, front_image_code, rear_image_code, front_image_name, rear_image_name);
            } else {
                showSnackbar(getResources().getString(R.string.aadhar_back_image));
            }
        } else {
            showSnackbar(getResources().getString(R.string.aadhar_front_image));
        }
    }

    public void getHDFCMaskingStatus() {
        getStatusAadharImages();
    }

    public void setSpinnerCodeValue(String spinner_value) {
        spinner_code_value = spinner_value;
    }

    public String getOrderId() {
        if (order_id != null) {
            return order_id;
        } else {
            return "";
        }
    }

    public String getAwbNo() {
        if (awbNo != null) {
            return String.valueOf(awbNo);
        } else {
            return "0";
        }
    }

    public void callSignatureScreen() {
        proceedToNextActivity();
    }

    public void cancelRbLScreen() {
        Intent intent = new Intent(this, EDSUndeliveredActivity.class);
        intent.putParcelableArrayListExtra(getString(R.string.data), new ArrayList<>(edsActivityResponseWizards));
        intent.putExtra("edsResponse", gson.toJson(edsDetailViewModel.edsWithActivityList.get().edsResponse));
        intent.putExtra(Constants.RESCHEDULE_ENABLE, reschedule_enable);
        intent.putExtra(Constants.COMPOSITE_KEY, composite_key);
        intent.putExtra("awb", awbNo);
        intent.putExtra(Constants.COMPOSITE_KEY, composite_key);
        intent.putExtra("navigator", "activity");
        startActivity(intent);
    }


    public void setCardType(String codes) {
        this.card_type = codes;
    }
}

