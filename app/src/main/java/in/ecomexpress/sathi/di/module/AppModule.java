package in.ecomexpress.sathi.di.module;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;
import androidx.room.Room;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import javax.inject.Singleton;
import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;
import in.ecomexpress.sathi.di.ApplicationInfo;
import in.ecomexpress.sathi.di.DatabaseInfo;
import in.ecomexpress.sathi.di.PreferenceInfo;
import in.ecomexpress.sathi.di.RestApiInfo;
import in.ecomexpress.sathi.repo.DataManager;
import in.ecomexpress.sathi.repo.IDataManager;
import in.ecomexpress.sathi.repo.local.data.eds.EdsCommit;
import in.ecomexpress.sathi.repo.local.data.fwd.ForwardCommit;
import in.ecomexpress.sathi.repo.local.data.rts.RTSCommit;
import in.ecomexpress.sathi.repo.local.data.rvp.RvpCommit;
import in.ecomexpress.sathi.repo.local.db.DBHelper;
import in.ecomexpress.sathi.repo.local.db.IDBHelper;
import in.ecomexpress.sathi.repo.local.db.db_utils.SathiDatabase;
import in.ecomexpress.sathi.repo.local.db.model.EdsWithActivityList;
import in.ecomexpress.sathi.repo.local.pref.IPreferenceHelper;
import in.ecomexpress.sathi.repo.local.pref.PreferenceHelper;
import in.ecomexpress.sathi.repo.local.storage.StorageHelper;
import in.ecomexpress.sathi.repo.remote.IRestApiHelper;
import in.ecomexpress.sathi.repo.remote.RestApiHelper;
import in.ecomexpress.sathi.repo.remote.RetrofitService;
import in.ecomexpress.sathi.repo.remote.model.DeviceDetails;
import in.ecomexpress.sathi.repo.remote.model.drs_list.edsnew.EDSActivityWizard;
import in.ecomexpress.sathi.repo.remote.model.masterdata.MasterActivityData;
import in.ecomexpress.sathi.ui.auth.changepassword.ChangePasswordViewModel;
import in.ecomexpress.sathi.ui.auth.forget.ForgetViewModel;
import in.ecomexpress.sathi.ui.auth.login.LoginViewModel;
import in.ecomexpress.sathi.ui.auth.verifyOtpLoginScreen.LoginVerifyOtpViewModel;
import in.ecomexpress.sathi.ui.dashboard.attendance.activity.AttendanceViewModel;
import in.ecomexpress.sathi.ui.dashboard.attendance.activity.CalendarDaysAdapter;
import in.ecomexpress.sathi.ui.dashboard.attendance.activity.MonthStatusAdapter;
import in.ecomexpress.sathi.ui.dashboard.attendance.attendance_calendar_fragment.AttendanceCalendarFragmentViewModel;
import in.ecomexpress.sathi.ui.dashboard.attendance.custom_dialog.CustomDialogAdapter;
import in.ecomexpress.sathi.ui.dashboard.attendance.custom_dialog.CustumDialogViewModel;
import in.ecomexpress.sathi.ui.dashboard.attendance.days_attendance_status_dialog.DaysAttendanceStausViewModel;
import in.ecomexpress.sathi.ui.dashboard.campaign.CampaignViewModel;
import in.ecomexpress.sathi.ui.dashboard.drs.map.googlemap.GViewModel;
import in.ecomexpress.sathi.ui.dashboard.drs.map.googlemap.info_window.MapInfoViewModel;
import in.ecomexpress.sathi.ui.dashboard.fe_earned.EarnedViewModel;
import in.ecomexpress.sathi.ui.dashboard.fuel.FuelReimburseViewModel;
import in.ecomexpress.sathi.ui.dashboard.fuel.FuelReimbursementAdapter;
import in.ecomexpress.sathi.ui.dashboard.landing.DashboardViewModel;
import in.ecomexpress.sathi.ui.dashboard.mapview.MapViewmodel;
import in.ecomexpress.sathi.ui.dashboard.performance.PerformanceViewModel;
import in.ecomexpress.sathi.ui.dashboard.refer.ReferFriendViewModel;
import in.ecomexpress.sathi.ui.dashboard.starttrip.StartTripViewModel;
import in.ecomexpress.sathi.ui.dashboard.stoptrip.StopTripViewModel;
import in.ecomexpress.sathi.ui.dashboard.switchnumber.SwitchNumberListAdapter;
import in.ecomexpress.sathi.ui.dashboard.switchnumber.SwitchNumberViewModel;
import in.ecomexpress.sathi.ui.dashboard.training.TrainingViewModel;
import in.ecomexpress.sathi.ui.dashboard.unattempted_shipments.UnattemptedShipmentAdapter;
import in.ecomexpress.sathi.ui.dashboard.unattempted_shipments.UnattemptedShipmentViewmodel;
import in.ecomexpress.sathi.ui.drs.forward.bpid.ScanBPIDViewModel;
import in.ecomexpress.sathi.ui.drs.forward.details.ForwardDetailViewModel;
import in.ecomexpress.sathi.ui.drs.forward.disputeDailog.DisputeDialogViewModel;
import in.ecomexpress.sathi.ui.drs.forward.fill_awb.AwbPopupDialogViewModel;
import in.ecomexpress.sathi.ui.drs.forward.mps.MPSScanViewModel;
import in.ecomexpress.sathi.ui.drs.forward.mps.ScannerItemAdapter;
import in.ecomexpress.sathi.ui.drs.forward.obd.viewmodel.FwdOBDCompleteViewModel;
import in.ecomexpress.sathi.ui.drs.forward.obd.viewmodel.FwdOBDProductDetailViewModel;
import in.ecomexpress.sathi.ui.drs.forward.obd.viewmodel.FwdOBDQcFailViewModel;
import in.ecomexpress.sathi.ui.drs.forward.obd.viewmodel.FwdOBDcCPassViewModel;
import in.ecomexpress.sathi.ui.drs.forward.obd.viewmodel.OBDScannerViewModel;
import in.ecomexpress.sathi.ui.drs.forward.obd.viewmodel.OBDStartOTPViewModel;
import in.ecomexpress.sathi.ui.drs.forward.obd.viewmodel.OBDStopOTPViewModel;
import in.ecomexpress.sathi.ui.drs.forward.otherNumber.OtherNumberDialogViewModel;
import in.ecomexpress.sathi.ui.drs.forward.shipmentearndialog.ShipmentEarnDialogViewModel;
import in.ecomexpress.sathi.ui.drs.forward.signature.SignatureViewModel;
import in.ecomexpress.sathi.ui.drs.forward.success.FWDSuccessViewModel;
import in.ecomexpress.sathi.ui.drs.forward.undelivered_fwd.UndeliveredViewModel;
import in.ecomexpress.sathi.ui.drs.rts.rts_main_list.RTSListActivityViewModel;
import in.ecomexpress.sathi.ui.drs.rts.rts_main_list.RTSShipmentListAdapter;
import in.ecomexpress.sathi.ui.drs.rts.rts_scan_and_deliver.RTSScanActivityViewModel;
import in.ecomexpress.sathi.ui.drs.rts.rts_signature.RTSSignatureViewModel;
import in.ecomexpress.sathi.ui.drs.rts.rts_success.RTSSuccessViewModel;
import in.ecomexpress.sathi.ui.drs.rvp.awbscan.CaptureScanViewModel;
import in.ecomexpress.sathi.ui.drs.rvp.qc_failure_list.QcFailAdapter;
import in.ecomexpress.sathi.ui.drs.rvp.qc_failure_list.RVPQcFailureViewModel;
import in.ecomexpress.sathi.ui.drs.rvp.rvp_qc_details.RvpQcDataDetailsViewModel;
import in.ecomexpress.sathi.ui.drs.rvp.rvp_qc_list.RvpQcListViewModel;
import in.ecomexpress.sathi.ui.drs.rvp.rvp_secure_activity.RVPSecureDeliveryViewModel;
import in.ecomexpress.sathi.ui.drs.rvp.signature.RVPSignatureViewModel;
import in.ecomexpress.sathi.ui.drs.rvp.success.RVPSuccessViewModel;
import in.ecomexpress.sathi.ui.drs.rvp.undelivered.RVPUndeliveredViewModel;
import in.ecomexpress.sathi.ui.drs.secure_delivery.SecureDeliveryViewModel;
import in.ecomexpress.sathi.ui.drs.sms.SMSViewModel;
import in.ecomexpress.sathi.ui.drs.todolist.DRSListAdapter;
import in.ecomexpress.sathi.ui.drs.todolist.ToDoListViewModel;
import in.ecomexpress.sathi.ui.dummy.eds.ac_document_list_collection.AcDocumentListCollectionAdapter;
import in.ecomexpress.sathi.ui.dummy.eds.ac_document_list_collection.AcDocumentListViewModel;
import in.ecomexpress.sathi.ui.dummy.eds.capture_image.CaptureImageAdapter;
import in.ecomexpress.sathi.ui.dummy.eds.capture_image.CaptureImageViewModel;
import in.ecomexpress.sathi.ui.dummy.eds.cash_collection.CashCollectionViewModel;
import in.ecomexpress.sathi.ui.dummy.eds.document_list_collection.DocumentListCollectionAdapter;
import in.ecomexpress.sathi.ui.dummy.eds.document_list_collection.DocumentListViewModel;
import in.ecomexpress.sathi.ui.dummy.eds.dummy.DummyViewModel;
import in.ecomexpress.sathi.ui.dummy.eds.eds_activity_detail.EDSDetailActivity;
import in.ecomexpress.sathi.ui.dummy.eds.eds_activity_detail.EDSDetailPagerAdapter;
import in.ecomexpress.sathi.ui.dummy.eds.eds_activity_detail.EDSDetailViewModel;
import in.ecomexpress.sathi.ui.dummy.eds.eds_amazon.AmazonFragmentViewModel;
import in.ecomexpress.sathi.ui.dummy.eds.eds_bkyc_idfc.EdsBkycIdfcViewModel;
import in.ecomexpress.sathi.ui.dummy.eds.eds_document_collection.DocumentCollectionAdapter;
import in.ecomexpress.sathi.ui.dummy.eds.eds_document_collection.DocumentCollectionViewModel;
import in.ecomexpress.sathi.ui.dummy.eds.eds_document_verification.DocumentVerificationAdapter;
import in.ecomexpress.sathi.ui.dummy.eds.eds_document_verification.DocumentVerificationViewModel;
import in.ecomexpress.sathi.ui.dummy.eds.eds_ekyc_XML.EdsEkycXMLViewModel;
import in.ecomexpress.sathi.ui.dummy.eds.eds_ekyc_hdfc.EdsEkycHdfcViewModel;
import in.ecomexpress.sathi.ui.dummy.eds.eds_ekyc_idfc.EdsEkycIdfcViewModel;
import in.ecomexpress.sathi.ui.dummy.eds.eds_ekyc_niyo.EdsEkycNiyoViewModel;
import in.ecomexpress.sathi.ui.dummy.eds.eds_ekyc_rbl.EdsRblViewModel;
import in.ecomexpress.sathi.ui.dummy.eds.eds_hdfc_masking.HDFCMaskingFragmentViewModel;
import in.ecomexpress.sathi.ui.dummy.eds.eds_opv.OpvFragmentViewModel;
import in.ecomexpress.sathi.ui.dummy.eds.eds_otp.EdsOtpViewModel;
import in.ecomexpress.sathi.ui.dummy.eds.eds_res_opv.ResOpvFragmentViewModel;
import in.ecomexpress.sathi.ui.dummy.eds.eds_signature.EDSSignatureViewModel;
import in.ecomexpress.sathi.ui.dummy.eds.eds_success_fail.EDSSuccessFailViewModel;
import in.ecomexpress.sathi.ui.dummy.eds.eds_task_list.EdsTaskListActivityModel;
import in.ecomexpress.sathi.ui.dummy.eds.edsantwork.EdsEkycAntWorkViewModel;
import in.ecomexpress.sathi.ui.dummy.eds.ekyc_freyo.EdsEkycFreyoViewModel;
import in.ecomexpress.sathi.ui.dummy.eds.ekyc_paytm.EdsEkycPaytmViewModel;
import in.ecomexpress.sathi.ui.dummy.eds.icic_standard.IciciStandardViewModel;
import in.ecomexpress.sathi.ui.dummy.eds.icici_ekyc.IciciViewModel;
import in.ecomexpress.sathi.ui.dummy.eds.paytm.PaytmViewModel;
import in.ecomexpress.sathi.ui.dummy.eds.vodafone.VodafoneViewModel;
import in.ecomexpress.sathi.ui.side_drawer.adm.ADMViewModel;
import in.ecomexpress.sathi.ui.side_drawer.dc_location_updation.DCLocationViewModel;
import in.ecomexpress.sathi.ui.side_drawer.drawer_main.SideDrawerViewModel;
import in.ecomexpress.sathi.ui.side_drawer.pendingHistory.PendingHistoryAdapter;
import in.ecomexpress.sathi.ui.side_drawer.pendingHistory.PendingHistoryDetailAdapter;
import in.ecomexpress.sathi.ui.side_drawer.pendingHistory.PendingHistoryDetailViewModel;
import in.ecomexpress.sathi.ui.side_drawer.pendingHistory.PendingHistoryViewModel;
import in.ecomexpress.sathi.ui.side_drawer.profile.ProfileViewModel;
import in.ecomexpress.sathi.utils.CommonUtils;
import in.ecomexpress.sathi.utils.Constants;
import in.ecomexpress.sathi.utils.rx.ISchedulerProvider;
import in.ecomexpress.sathi.utils.rx.SchedulerProvider;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.jackson.JacksonConverterFactory;

@InstallIn(SingletonComponent.class)
@Module
public class AppModule {

    /**
     * Provide context context.
     *
     * @param application the application
     * @return the context
     */
    @Provides
    @Singleton
    Context provideContext(Application application) {
        return application;
    }

    /**
     * Provide scheduler provider scheduler provider.
     *
     * @return the scheduler provider
     */
    @Provides
    ISchedulerProvider provideSchedulerProvider() {
        return new SchedulerProvider();
    }

    /**
     * Provide app database sathi database.
     *
     * @param dbName     the db name
     * @param dbPassword the db password
     * @param context    the context
     * @return the sathi database
     */
    @Provides
    @Singleton
    SathiDatabase provideAppDatabase(@DatabaseInfo("Name") String dbName, @DatabaseInfo("Password") String dbPassword, Context context) {
        return Room.databaseBuilder(context, SathiDatabase.class, dbName)
                //        .openHelperFactory(safeHelperFactory)
                .fallbackToDestructiveMigration()
                .build();
    }

    /**
     * Provide database name string.
     *
     * @return the string
     */
    @Provides
    @DatabaseInfo("Name")
    String provideDatabaseName() {
        return Constants.DB_NAME;
    }

    /**
     * Provide database key string.
     *
     * @return the string
     */
    @Provides
    @DatabaseInfo("Password")
    String provideDatabaseKey() {
        return Constants.DB_PASS;
    }

    /**
     * Provide preference name string.
     *
     * @return the string
     */
    @Provides
    @PreferenceInfo
    String providePreferenceName() {
        return Constants.PREF_NAME;
    }

    /**
     * Provide package name string.
     *
     * @param context the context
     * @return the string
     */
    @Provides
    @ApplicationInfo("PackageName")
    String providePackageName(Context context) {
        return context.getPackageName();
    }

    /**
     * Provide app version name string.
     *
     * @return the string
     */
    @Provides
    @ApplicationInfo("VersionName")
    String provideAppVersionName() {
        return Constants.VERSION_NAME;
    }

    /**
     * Provide app version code integer.
     *
     * @return the integer
     */
    @Provides
    @ApplicationInfo("VersionCode")
    Integer provideAppVersionCode() {
        return Constants.VERSION_CODE;
    }

    /**
     * Provide encryption key string.
     *
     * @return the string
     */
    @Provides
    @ApplicationInfo("EncryptionKey")
    String provideEncryptionKey() {
        return Constants.ENCRYPTION_KEY;
    }

    /**
     * Provide api key string.
     *
     * @return the string
     */
    @Provides
    @RestApiInfo("ApiKey")
    String provideApiKey() {
        return Constants.API_KEY;
    }


    /**
     * Provide api url string.
     *
     * @return the string
     */
    @Provides
    @RestApiInfo("ServerUrl")
    String provideApiUrl() {
        return Constants.SERVER_URL;
    }

    /**
     * Provide data manager data manager.
     *
     * @param dataHelper the data helper
     * @return the data manager
     */
    @Provides
    @Singleton
    IDataManager provideDataManager(DataManager dataHelper) {
        return dataHelper;
    }


    /**
     * Provide database helper idb helper.
     *
     * @param databaseHelper the database helper
     * @return the idb helper
     */
    @Provides
    @Singleton
    IDBHelper provideDatabaseHelper(DBHelper databaseHelper) {
        return databaseHelper;
    }


    /**
     * Provide preference helper preference helper.
     *
     * @param preferenceHelper the preference helper
     * @return the preference helper
     */

    @Provides
    @Singleton
    IPreferenceHelper providePreferenceHelper(PreferenceHelper preferenceHelper) {
        return preferenceHelper;

    }

    /**
     * Provide rest api helper rest api helper.
     *
     * @param restApiHelper the rest api helper
     * @return the rest api helper
     */
    @Provides
    @Singleton
    IRestApiHelper provideRestApiHelper(RestApiHelper restApiHelper, StorageHelper storageHelper) {
//        if (BuildConfig.BUILD_TYPE.equalsIgnoreCase("release")) {
        //  return restApiHelper;
//        }
//        if (BuildConfig.DEBUG) {
        // return storageHelper;
//        } else {

        return restApiHelper;
//        }
    }

    @SuppressLint({"MissingPermission", "HardwareIds"})
    @Provides
    @Singleton
    DeviceDetails provideDeviceDetails(Context context) {
        DeviceDetails deviceDetails = new DeviceDetails();
        deviceDetails.setIpAddress(CommonUtils.getLocalIpAddress());
        deviceDetails.setIsOtgEnabled(context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_USB_HOST));
        deviceDetails.setManufacturer(Build.MANUFACTURER);
        deviceDetails.setModelNumber(Build.MODEL);
        deviceDetails.setSdkVersion(Build.VERSION.RELEASE);
        deviceDetails.setSdkVersionCode(Build.VERSION.SDK_INT);
        return deviceDetails;

    }


    @Provides
    OkHttpClient provideOkHttpClient() {
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
            @Override
            public void log(String message) {
                int maxLogSize = 4000;
                for (int i = 0; i <= message.length() / maxLogSize; i++) {
                    int start = i * maxLogSize;
                    int end = (i + 1) * maxLogSize;
                    end = end > message.length() ? message.length() : end;
                    Log.i("okhttp", message.substring(start, end));
                }
            }
        });
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        return new OkHttpClient().newBuilder().readTimeout(150, TimeUnit.SECONDS)
                .writeTimeout(150, TimeUnit.SECONDS)
                .connectTimeout(150, TimeUnit.SECONDS).addInterceptor(httpLoggingInterceptor).build();

    }

    @Provides
    Retrofit provideRetrofit(@RestApiInfo("ServerUrl") String url, OkHttpClient okHttpClient) {
        return new Retrofit.Builder()
            .baseUrl(url)
            .client(provideOkHttpClient())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(JacksonConverterFactory.create())
            .build();
    }

    @Provides
    RetrofitService provideRetrofitService(Retrofit retrofit) {
        return retrofit.create(RetrofitService.class);
    }

    @Provides
    ChangePasswordViewModel provideAboutViewModel(IDataManager dataManager, ISchedulerProvider schedulerProvider, Application application) {
        return new ChangePasswordViewModel(dataManager, schedulerProvider, application);
    }

    @Provides
    ForgetViewModel provideForgetViewModel(IDataManager dataManager, ISchedulerProvider schedulerProvider, Application application) {
        return new ForgetViewModel(dataManager, schedulerProvider, application);
    }

    @Provides
    LoginViewModel provideLoginViewModel(IDataManager dataManager, ISchedulerProvider schedulerProvider, Application sathiApplication) {
        return new LoginViewModel(dataManager, schedulerProvider, sathiApplication);
    }

    @Provides
    LoginVerifyOtpViewModel provideLoginVerifyOtpViewModel(IDataManager dataManager, ISchedulerProvider schedulerProvider, Application sathiApplication) {
        return new LoginVerifyOtpViewModel(dataManager, schedulerProvider, sathiApplication);
    }

    @Provides
    AttendanceViewModel provideAttendanceViewModel(IDataManager dataManager, ISchedulerProvider schedulerProvider, Application sathiApplication) {
        return new AttendanceViewModel(dataManager, schedulerProvider, sathiApplication);
    }

    @Provides
    MonthStatusAdapter provideMonthStatusAdapter() {
        return new MonthStatusAdapter(new ArrayList<>());
    }

    @Provides
    CalendarDaysAdapter provideDaysAdapter() {
        return new CalendarDaysAdapter(new ArrayList<>());
    }

    @Provides
    DaysAttendanceStausViewModel provideDaysAttendanceStausViewModel(IDataManager dataManager, ISchedulerProvider
            schedulerProvider, Application application) {
        return new DaysAttendanceStausViewModel(dataManager, schedulerProvider, application);
    }

    @Provides
    CampaignViewModel providerCampaignViewModel(IDataManager dataManager, ISchedulerProvider schedulerProvider, Application sathiApplication) {
        return new CampaignViewModel(dataManager, schedulerProvider, sathiApplication);
    }

    @Provides
    ScanBPIDViewModel provideScanBPIDViewModel(IDataManager dataManager, ISchedulerProvider schedulerProvider, Application sathiApplication) {
        return new ScanBPIDViewModel(dataManager, schedulerProvider, sathiApplication);
    }

    @Provides
    AttendanceCalendarFragmentViewModel provideAttendanceCalendarFragmentViewModel(IDataManager dataManager, ISchedulerProvider schedulerProvider, Application application) {
        return new AttendanceCalendarFragmentViewModel(dataManager, schedulerProvider, application);
    }

    @Provides
    EDSActivityWizard provideEDSActivityWizard() {
        return new EDSActivityWizard();
    }

    @Provides
    MasterActivityData provideMasterActivityData() {
        return new MasterActivityData();
    }


    @Provides
    CustumDialogViewModel provideCustumDialogViewModel(IDataManager dataManager, ISchedulerProvider
            schedulerProvider, Application application) {
        return new CustumDialogViewModel(dataManager, schedulerProvider, application);
    }

    @Provides
    CustomDialogAdapter provideAdapter() {
        return new CustomDialogAdapter(new ArrayList<>());
    }

    @Provides
    public MapInfoViewModel proviMapInfoViewModel(IDataManager dataManager, ISchedulerProvider schedulerProvider, Application application) {
        return new MapInfoViewModel(dataManager, schedulerProvider, application);
    }

    @Provides
    GViewModel provideGViewModel(IDataManager dataManager, ISchedulerProvider schedulerProvider, Application application) {
        return new GViewModel(dataManager, schedulerProvider, application);
    }

    @Provides
    DRSListAdapter provideDrsListAdapter() {
        return new DRSListAdapter();
    }

    @Provides
    EarnedViewModel provideEarnedViewModel(IDataManager dataManager, ISchedulerProvider schedulerProvider, Application sathiApplication) {
        return new EarnedViewModel(dataManager, schedulerProvider, sathiApplication);
    }

    @Provides
    FuelReimburseViewModel provideFuelReimburseViewModel(IDataManager dataManager, ISchedulerProvider schedulerProvider, Application application) {
        return new FuelReimburseViewModel(dataManager, schedulerProvider, application);
    }

    @Provides
    FuelReimbursementAdapter provideFuelReimbursementAdapter() {
        return new FuelReimbursementAdapter(new ArrayList<>());
    }

    @Provides
    DashboardViewModel provideDashboardViewModel(IDataManager dataManager, ISchedulerProvider schedulerProvider, Application sathiApplication) {
        return new DashboardViewModel(dataManager, schedulerProvider, sathiApplication);
    }

    @Provides
    MapViewmodel providerDashBoardMapViewModel(IDataManager dataManager, ISchedulerProvider schedulerProvider, Application sathiApplication) {
        return new MapViewmodel(dataManager, schedulerProvider, sathiApplication);
    }

    @Provides
    PerformanceViewModel providePerformancefViewModel(IDataManager dataManager,
                                                      ISchedulerProvider schedulerProvider, Application sathiApplication) {
        return new PerformanceViewModel(dataManager, schedulerProvider, sathiApplication);
    }

    @Provides
    ReferFriendViewModel providerWebviewReferFriendViewModel(IDataManager dataManager, ISchedulerProvider schedulerProvider, Application sathiApplication) {
        return new ReferFriendViewModel(dataManager, schedulerProvider, sathiApplication);
    }

    @Provides
    StartTripViewModel provideStartTripViewModel(IDataManager dataManager, ISchedulerProvider schedulerProvider, Application application) {
        return new StartTripViewModel(dataManager, schedulerProvider, application);
    }

    @Provides
    StopTripViewModel provideStopTripViewModel(IDataManager dataManager, ISchedulerProvider
            schedulerProvider, Application application) {
        return new StopTripViewModel(dataManager, schedulerProvider, application);
    }

    @Provides
    SwitchNumberViewModel provideSwitchNumberViewModel(IDataManager dataManager, ISchedulerProvider schedulerProvider, Application sathiApplication) {
        return new SwitchNumberViewModel(dataManager, schedulerProvider, sathiApplication);
    }

    @Provides
    SwitchNumberListAdapter provideSwitchNumberListAdapter() {
        return new SwitchNumberListAdapter(new ArrayList<>());
    }

    @Provides
    TrainingViewModel providTrainingViewModel(IDataManager dataManager,
                                              ISchedulerProvider schedulerProvider, Application sathiApplication) {
        return new TrainingViewModel(dataManager, schedulerProvider, sathiApplication);
    }

    @Provides
    UnattemptedShipmentViewmodel provideUnAttemptedShipmentViewModel(IDataManager dataManager, ISchedulerProvider schedulerProvider, Application sathiApplication) {
        return new UnattemptedShipmentViewmodel(dataManager, schedulerProvider, sathiApplication);
    }

    @Provides
    UnattemptedShipmentAdapter provideUnattemptedShipmentAdapter() {
        return new UnattemptedShipmentAdapter();
    }

    @Provides
    ForwardDetailViewModel provideForwardDetailViewModel(IDataManager dataManager, ISchedulerProvider schedulerProvider, Application sathiApplication) {
        return new ForwardDetailViewModel(dataManager, schedulerProvider, sathiApplication);
    }

    @Provides
    ForwardCommit provideForwardCommit() {
        return new ForwardCommit();
    }


    @Provides
    DisputeDialogViewModel provideDisputeDialogViewModel(IDataManager dataManager,
                                                         ISchedulerProvider schedulerProvider, Application sathiApplication) {
        return new DisputeDialogViewModel(dataManager, schedulerProvider, sathiApplication);
    }

    @Provides
    AwbPopupDialogViewModel provideAwbPopupDialogViewModel(IDataManager dataManager, ISchedulerProvider schedulerProvider, Application sathiApplication) {
        return new AwbPopupDialogViewModel(dataManager, schedulerProvider, sathiApplication);
    }

    @Provides
    public MPSScanViewModel provideMPSScanViewModel(IDataManager dataManager,
                                                    ISchedulerProvider schedulerProvider, Application sathiApplication) {
        return new MPSScanViewModel(dataManager, schedulerProvider, sathiApplication);
    }


    @Provides
    ScannerItemAdapter provideScannerItemAdapter() {
        return new ScannerItemAdapter(new ArrayList<>());
    }

    @Provides
    FwdOBDCompleteViewModel provideFwdOBDCompleteViewModel(IDataManager dataManager,
                                                           ISchedulerProvider schedulerProvider, Application sathiApplication) {
        return new FwdOBDCompleteViewModel(dataManager, schedulerProvider, sathiApplication);
    }

    @Provides
    FwdOBDProductDetailViewModel provideFwdOBDProductDetailViewModel(IDataManager dataManager,
                                                                     ISchedulerProvider schedulerProvider, Application sathiApplication) {
        return new FwdOBDProductDetailViewModel(dataManager, schedulerProvider, sathiApplication);
    }


    @Provides
    FwdOBDQcFailViewModel provideFwdOBDQcFailViewModel(IDataManager dataManager, ISchedulerProvider schedulerProvider, Application sathiApplication) {
        return new FwdOBDQcFailViewModel(dataManager, schedulerProvider, sathiApplication);
    }

    @Provides
    FwdOBDcCPassViewModel provideQCAndCapturedSharedViewModel(IDataManager dataManager, ISchedulerProvider schedulerProvider, Application sathiApplication) {
        return new FwdOBDcCPassViewModel(dataManager, schedulerProvider, sathiApplication);
    }

    @Provides
    OBDScannerViewModel obdScannerViewModel(IDataManager dataManager, ISchedulerProvider schedulerProvider, Application sathiApplication) {
        return new OBDScannerViewModel(dataManager, schedulerProvider, sathiApplication);
    }

    @Provides
    OBDStartOTPViewModel provideObdOtpViewModel(IDataManager dataManager, ISchedulerProvider schedulerProvider, Application sathiApplication) {
        return new OBDStartOTPViewModel(dataManager, schedulerProvider, sathiApplication);
    }


    @Provides
    OBDStopOTPViewModel provideObdOtpVerifyViewModel(IDataManager dataManager, ISchedulerProvider schedulerProvider, Application sathiApplication) {
        return new OBDStopOTPViewModel(dataManager, schedulerProvider, sathiApplication);
    }

    @Provides
    OtherNumberDialogViewModel provideOtherNumberDialogViewModel(IDataManager dataManager,
                                                                 ISchedulerProvider schedulerProvider, Application sathiApplication) {
        return new OtherNumberDialogViewModel(dataManager, schedulerProvider, sathiApplication);
    }

    @Provides
    ShipmentEarnDialogViewModel provideShipmentEarnDialogViewModel(IDataManager dataManager, ISchedulerProvider
            schedulerProvider, Application sathiApplication) {
        return new ShipmentEarnDialogViewModel(dataManager, schedulerProvider, sathiApplication);
    }

    @Provides
    SignatureViewModel provideSignatureViewModel(IDataManager dataManager,
                                                 ISchedulerProvider schedulerProvider, Application sathiApplication) {
        return new SignatureViewModel(dataManager, schedulerProvider, sathiApplication);
    }

    @Provides
    FWDSuccessViewModel providesFWDSuccessViewModel(IDataManager dataManager,
                                                    ISchedulerProvider schedulerProvider, Application sathiApplication) {
        return new FWDSuccessViewModel(dataManager, schedulerProvider, sathiApplication);
    }

    @Provides
    UndeliveredViewModel provideUndeliveredViewModel(IDataManager dataManager,
                                                     ISchedulerProvider schedulerProvider, Application sathiApplication) {
        return new UndeliveredViewModel(dataManager, schedulerProvider, sathiApplication);
    }

    @Provides
    RTSListActivityViewModel providesRTSListActivityViewModel(IDataManager dataManager, ISchedulerProvider schedulerProvider, Application sathiApplication) {
        return new RTSListActivityViewModel(dataManager, schedulerProvider, sathiApplication);
    }

    @Provides
    RTSShipmentListAdapter provideRTSShipmentListAdapter() {
        return new RTSShipmentListAdapter(new ArrayList<>(), true);
    }

    @Provides
    RTSScanActivityViewModel provideRTSScanViewModel(IDataManager dataManager, ISchedulerProvider schedulerProvider, Application sathiApplication) {
        return new RTSScanActivityViewModel(dataManager, schedulerProvider, sathiApplication);
    }

    @Provides
    RTSSignatureViewModel providesRTSSignatureViewModel(IDataManager dataManager, ISchedulerProvider iSchedulerProvider, Application sathiApplication) {
        return new RTSSignatureViewModel(dataManager, iSchedulerProvider, sathiApplication);
    }

    @Provides
    RTSSuccessViewModel provideRTSSuccessViewModel(IDataManager dataManager, ISchedulerProvider schedulerProvider, Application sathiApplication) {
        return new RTSSuccessViewModel(dataManager, schedulerProvider, sathiApplication);
    }

    @Provides
    RTSCommit provideRTSCommit() {
        return new RTSCommit();
    }

    @Provides
    public CaptureScanViewModel provideCaptureScanViewModel(IDataManager dataManager,
                                                            ISchedulerProvider schedulerProvider, Application sathiApplication) {
        return new CaptureScanViewModel(dataManager, schedulerProvider, sathiApplication);
    }

    @Provides
    public in.ecomexpress.sathi.ui.dummy.eds.eds_scan.CaptureScanViewModel provideCaptureEdsScanViewModel(IDataManager dataManager,
                                                                                                       ISchedulerProvider schedulerProvider, Application sathiApplication) {
        return new in.ecomexpress.sathi.ui.dummy.eds.eds_scan.CaptureScanViewModel(dataManager, schedulerProvider, sathiApplication);
    }

    @Provides
    public EDSSuccessFailViewModel provideEDSSuccessFailViewModel(IDataManager dataManager,
                                                                  ISchedulerProvider schedulerProvider, Application sathiApplication) {
        return new EDSSuccessFailViewModel(dataManager, schedulerProvider, sathiApplication);
    }

    @Provides
    public EdsEkycIdfcViewModel provideEdsEkycIdfcViewViewModel(IDataManager dataManager,
                                                               ISchedulerProvider schedulerProvider, Application sathiApplication) {
        return new EdsEkycIdfcViewModel(dataManager, schedulerProvider, sathiApplication);
    }

    @Provides
    RVPQcFailureViewModel provideRVPQcFailureViewModel(IDataManager dataManager,
                                                       ISchedulerProvider schedulerProvider, Application sathiApplication) {
        return new RVPQcFailureViewModel(dataManager, schedulerProvider, sathiApplication);
    }

    @Provides
    QcFailAdapter provideQcFailAdapter() {
        return new QcFailAdapter(new ArrayList<>());
    }


    @Provides
    RvpQcDataDetailsViewModel provideRvpQcDataDetailsViewModel(IDataManager dataManager,
                                                               ISchedulerProvider schedulerProvider, Application sathiApplication) {
        return new RvpQcDataDetailsViewModel(dataManager, schedulerProvider, sathiApplication);
    }


    @Provides
    RvpQcListViewModel provideRvpQcListViewModel(IDataManager dataManager,
                                                 ISchedulerProvider schedulerProvider, Application sathiApplication) {
        return new RvpQcListViewModel(dataManager, schedulerProvider, sathiApplication);
    }

    @Provides
    RVPSecureDeliveryViewModel providerrvpsecuredeliveryactivity(IDataManager dataManager,
                                                                 ISchedulerProvider schedulerProvider, Application sathiApplication) {
        return new RVPSecureDeliveryViewModel(dataManager, schedulerProvider, sathiApplication);
    }


    @Provides
    RVPSignatureViewModel providesSignatureViewModel(IDataManager dataManager,
                                                     ISchedulerProvider iSchedulerProvider, Application sathiApplication) {
        return new RVPSignatureViewModel(dataManager, iSchedulerProvider, sathiApplication);
    }



    @Provides
    RVPSuccessViewModel providesRVPSuccessViewModel(IDataManager dataManager,
                                                    ISchedulerProvider schedulerProvider, Application sathiApplication) {
        return new RVPSuccessViewModel(dataManager, schedulerProvider, sathiApplication);
    }

    @Provides
    RVPUndeliveredViewModel provideRVPUndeliveredViewModel(IDataManager dataManager,
                                                           ISchedulerProvider schedulerProvider, Application sathiApplication) {
        return new RVPUndeliveredViewModel(dataManager, schedulerProvider, sathiApplication);
    }

    @Provides
    SecureDeliveryViewModel provideSecureDeliveryViewModel(IDataManager dataManager,
                                                           ISchedulerProvider schedulerProvider, Application sathiApplication) {
        return new SecureDeliveryViewModel(dataManager, schedulerProvider, sathiApplication);
    }

    @Provides
    RvpCommit provideRvpCommit() {
        return new RvpCommit();
    }

    @Provides
    EdsCommit provideEdsCommit() {
        return new EdsCommit();
    }

    @Provides
    SMSViewModel provideRateUsViewModel(IDataManager dataManager, ISchedulerProvider
            schedulerProvider, Application application) {
        return new SMSViewModel(dataManager, schedulerProvider, application);
    }

    @Provides
    ToDoListViewModel provideToDoListViewModel(IDataManager iDataManager, ISchedulerProvider iSchedulerProvider, Application application) {
        return new ToDoListViewModel(iDataManager, iSchedulerProvider, application);
    }

    @Provides
    ADMViewModel provideAdmViewModel(IDataManager dataManager, ISchedulerProvider schedulerProvider, Application application) {
        return new ADMViewModel(dataManager, schedulerProvider, application);
    }

    @Provides
    DCLocationViewModel provideDCLocationViewModel(IDataManager dataManager, ISchedulerProvider schedulerProvider, Application application) {
        return new DCLocationViewModel(dataManager, schedulerProvider, application);
    }

    @Provides
    SideDrawerViewModel provideSideDrawerViewModel(IDataManager dataManager, ISchedulerProvider schedulerProvider, Application application) {
        return new SideDrawerViewModel(dataManager, schedulerProvider, application);
    }

    @Provides
    PendingHistoryViewModel providePendingHistoryViewModel(IDataManager dataManager, ISchedulerProvider schedulerProvider, Application application) {
        return new PendingHistoryViewModel(dataManager, schedulerProvider, application);
    }

    @Provides
    public PendingHistoryAdapter providePendingHistoryAdapter() {
        return new PendingHistoryAdapter(new ArrayList<>());
    }


    @Provides
    PendingHistoryDetailViewModel providePendingHistoryDetailViewModel(IDataManager dataManager, ISchedulerProvider schedulerProvider, Application application) {
        return new PendingHistoryDetailViewModel(dataManager, schedulerProvider, application);
    }

    @Provides
    public PendingHistoryDetailAdapter providePendingHistoryDetailAdapter() {
        return new PendingHistoryDetailAdapter(new ArrayList<>());
    }

    @Provides
    ProfileViewModel provideProfileViewModel(IDataManager dataManager,
                                             ISchedulerProvider schedulerProvider, Application application) {
        return new ProfileViewModel(dataManager, schedulerProvider, application);
    }

    @Provides
    AcDocumentListViewModel provideAcDocumentListViewModel(IDataManager dataManager,
                                                           ISchedulerProvider schedulerProvider, Application sathiApplication) {
        return new AcDocumentListViewModel(dataManager, schedulerProvider, sathiApplication);
    }

    @Provides
    AcDocumentListCollectionAdapter provideAcDocumentListCollectionAdapter() {
        return new AcDocumentListCollectionAdapter(new ArrayList<Boolean>(), new ArrayList<Boolean>());
    }

    @Provides
    CaptureImageViewModel provideCaptureImageViewModel(IDataManager dataManager,
                                                       ISchedulerProvider schedulerProvider, Application sathiApplication) {
        return new CaptureImageViewModel(dataManager, schedulerProvider, sathiApplication);
    }

    @Provides
    CaptureImageAdapter provideCaptureImageAdapter() {
        return new CaptureImageAdapter(new ArrayList<Boolean>(), new ArrayList<Boolean>());
    }

    @Provides
    CashCollectionViewModel provideCashCollectionViewModel(IDataManager dataManager,
                                                           ISchedulerProvider schedulerProvider, Application sathiApplication) {
        return new CashCollectionViewModel(dataManager, schedulerProvider, sathiApplication);
    }

    @Provides
    DocumentListViewModel provideDocumentListViewModel(IDataManager dataManager,
                                                       ISchedulerProvider schedulerProvider, Application sathiApplication) {
        return new DocumentListViewModel(dataManager, schedulerProvider, sathiApplication);
    }

    @Provides
    DocumentListCollectionAdapter provideDocumentListCollectionAdapter() {
        return new DocumentListCollectionAdapter(new ArrayList<Boolean>(), new ArrayList<Boolean>());
    }

    @Provides
    DummyViewModel provideDummyViewModel(IDataManager dataManager,
                                         ISchedulerProvider schedulerProvider, Application sathiApplication) {
        return new DummyViewModel(dataManager, schedulerProvider, sathiApplication);
    }

    @Provides
    EDSDetailViewModel provideEDSDetailViewModel(IDataManager dataManager,
                                                 ISchedulerProvider schedulerProvider, Application sathiApplication) {
        return new EDSDetailViewModel(dataManager, schedulerProvider, sathiApplication);
    }

    @Provides
    EDSDetailPagerAdapter provideEDSDetailPagerAdapter(EDSDetailActivity activity, IDataManager dataManager,
                                                       ISchedulerProvider schedulerProvider, Application sathiApplication) {
        return new EDSDetailPagerAdapter(activity.getSupportFragmentManager(), new EdsWithActivityList(), new ArrayList<MasterActivityData>(), new EDSDetailViewModel(dataManager, schedulerProvider, sathiApplication));
    }

    @Provides
    AmazonFragmentViewModel provideAmazonFragmentViewModel(IDataManager dataManager, ISchedulerProvider schedulerProvider, Application sathiApplication) {
        return new AmazonFragmentViewModel(dataManager, schedulerProvider, sathiApplication);
    }

    @Provides
    EdsBkycIdfcViewModel provideEdsEkycIdfcModel(IDataManager dataManager,
                                                 ISchedulerProvider schedulerProvider, Application sathiApplication) {
        return new EdsBkycIdfcViewModel(dataManager, schedulerProvider, sathiApplication);
    }

    @Provides
    DocumentCollectionViewModel provideDocumentCollectionViewModel(IDataManager dataManager,
                                                                   ISchedulerProvider schedulerProvider, Application sathiApplication) {
        return new DocumentCollectionViewModel(dataManager, schedulerProvider, sathiApplication);
    }

    @Provides
    DocumentCollectionAdapter provideDocumentCollectionAdapter() {
        return new DocumentCollectionAdapter(new ArrayList<Boolean>(), new ArrayList<Boolean>());
    }

    @Provides
    DocumentVerificationViewModel provideDocumentVerificationViewModel(IDataManager dataManager,
                                                                       ISchedulerProvider schedulerProvider, Application sathiApplication) {
        return new DocumentVerificationViewModel(dataManager, schedulerProvider, sathiApplication);
    }

    @Provides
    DocumentVerificationAdapter provideDocumentVerificationAdapter() {
        return new DocumentVerificationAdapter(new ArrayList<Boolean>(), new ArrayList<Boolean>());
    }

    @Provides
    EdsEkycHdfcViewModel provideEdsEkycHdfcModel(IDataManager dataManager,
                                                 ISchedulerProvider schedulerProvider, Application sathiApplication) {
        return new EdsEkycHdfcViewModel(dataManager, schedulerProvider, sathiApplication);
    }

    @Provides
    EdsEkycNiyoViewModel provideEdsEkycNiyoModel(IDataManager dataManager,
                                                 ISchedulerProvider schedulerProvider, Application app) {
        return new EdsEkycNiyoViewModel(dataManager, schedulerProvider, app);
    }

    @Provides
    EdsRblViewModel provideEdsRblModel(IDataManager dataManager,
                                       ISchedulerProvider schedulerProvider, Application sathiApplication) {
        return new EdsRblViewModel(dataManager, schedulerProvider, sathiApplication);
    }

    @Provides
    EdsEkycXMLViewModel provideEdsEkycXMLModel(IDataManager dataManager,
                                               ISchedulerProvider schedulerProvider, Application sathiApplication) {
        return new EdsEkycXMLViewModel(dataManager, schedulerProvider, sathiApplication);
    }

    @Provides
    HDFCMaskingFragmentViewModel provideHDFCMaskingFragmentViewModel(IDataManager dataManager, ISchedulerProvider schedulerProvider, Application sathiApplication) {
        return new HDFCMaskingFragmentViewModel(dataManager, schedulerProvider, sathiApplication);
    }

    @Provides
    OpvFragmentViewModel provideOpvFragmentViewModel(IDataManager dataManager,
                                                     ISchedulerProvider schedulerProvider, Application sathiApplication) {
        return new OpvFragmentViewModel(dataManager, schedulerProvider, sathiApplication);
    }


    @Provides
    EdsOtpViewModel providesEdsOtpViewModel(IDataManager dataManager,
                                            ISchedulerProvider iSchedulerProvider, Application sathiApplication) {
        return new EdsOtpViewModel(dataManager, iSchedulerProvider, sathiApplication);
    }

    @Provides
    ResOpvFragmentViewModel provideResOpvFragmentViewModel(IDataManager dataManager,
                                                           ISchedulerProvider schedulerProvider, Application sathiApplication) {
        return new ResOpvFragmentViewModel(dataManager, schedulerProvider, sathiApplication);
    }


    @Provides
    EDSSignatureViewModel providesEDSSignatureViewModel(IDataManager dataManager,
                                                        ISchedulerProvider iSchedulerProvider, Application sathiApplication) {
        return new EDSSignatureViewModel(dataManager, iSchedulerProvider, sathiApplication);
    }

    @Provides
    EdsTaskListActivityModel provideEdsTaskListActivityModel(IDataManager dataManager,
                                                             ISchedulerProvider schedulerProvider, Application sathiApplication) {
        return new EdsTaskListActivityModel(dataManager, schedulerProvider ,sathiApplication);
    }

    @Provides
    EdsEkycAntWorkViewModel edsEkycAntWorkViewModel(IDataManager dataManager,
                                                    ISchedulerProvider schedulerProvider, Application app) {
        return new EdsEkycAntWorkViewModel(dataManager, schedulerProvider,app);
    }

    @Provides
    EdsEkycFreyoViewModel provideEdsEkycFreyoViewModel(IDataManager dataManager,
                                                       ISchedulerProvider schedulerProvider, Application app) {
        return new EdsEkycFreyoViewModel(dataManager, schedulerProvider,app);
    }

    @Provides
    EdsEkycPaytmViewModel provideEdsEkycPaytmViewModel(IDataManager dataManager,
                                                       ISchedulerProvider schedulerProvider, Application sathiApplication) {
        return new EdsEkycPaytmViewModel(dataManager, schedulerProvider ,sathiApplication);
    }

    @Provides
    IciciStandardViewModel provideIciciStandardViewModel(IDataManager dataManager,
                                                    ISchedulerProvider schedulerProvider , Application sathiApplication) {
        return new IciciStandardViewModel(dataManager, schedulerProvider, sathiApplication);
    }

    @Provides
    IciciViewModel provideIciciViewModel(IDataManager dataManager,
                                            ISchedulerProvider schedulerProvider , Application sathiApplication) {
        return new IciciViewModel(dataManager, schedulerProvider, sathiApplication);
    }

    @Provides
    PaytmViewModel providePaytmViewModel(IDataManager dataManager,
                                         ISchedulerProvider schedulerProvider, Application app) {
        return new PaytmViewModel(dataManager, schedulerProvider,app);
    }

    @Provides
    in.ecomexpress.sathi.ui.dummy.eds.undeilvered_eds.UndeliveredViewModel providesUndeliveredViewModel(IDataManager dataManager,
                                                                                                        ISchedulerProvider iSchedulerProvider, Application sathiApplication) {
        return new in.ecomexpress.sathi.ui.dummy.eds.undeilvered_eds.UndeliveredViewModel(dataManager, iSchedulerProvider ,sathiApplication);
    }

    @Provides
    VodafoneViewModel provideVodafoneViewModel(IDataManager dataManager,
                                               ISchedulerProvider schedulerProvider, Application sathiApplication) {
        return new VodafoneViewModel(dataManager, schedulerProvider ,sathiApplication);
    }
}