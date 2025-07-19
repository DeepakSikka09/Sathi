/*
package in.ecomexpress.sathi.di.builder;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import in.ecomexpress.sathi.ui.auth.changepassword.ChangePasswordDialogProvider;
import in.ecomexpress.sathi.ui.auth.forget.ForgotDialogProvider;
import in.ecomexpress.sathi.ui.auth.login.LoginActivity;
import in.ecomexpress.sathi.ui.auth.login.LoginActivityModule;
import in.ecomexpress.sathi.ui.auth.verifyOtpLoginScreen.LoginVerifyOtpActivity;
import in.ecomexpress.sathi.ui.auth.verifyOtpLoginScreen.LoginVerifyOtpModule;
import in.ecomexpress.sathi.ui.dashboard.refer.ReferFriendActivity;
import in.ecomexpress.sathi.ui.dashboard.refer.ReferModule;
import in.ecomexpress.sathi.ui.drs.rvp.rvp_qc_details.RvpQCDetailActivity;
import in.ecomexpress.sathi.ui.drs.rvp.rvp_qc_details.RvpQcDetailsModule;
import in.ecomexpress.sathi.ui.drs.rvp.rvp_qc_details.RvpQcViewModel;
import in.ecomexpress.sathi.ui.side_drawer.adm.ADMActivity;
import in.ecomexpress.sathi.ui.side_drawer.adm.ADMActivityModule;
import in.ecomexpress.sathi.ui.dashboard.attendance.activity.AttendanceActivity;
import in.ecomexpress.sathi.ui.dashboard.attendance.activity.AttendanceActivity1;
import in.ecomexpress.sathi.ui.dashboard.attendance.activity.AttendanceActivityModule;
import in.ecomexpress.sathi.ui.dashboard.attendance.custom_dialog.CustomdialogProvider;
import in.ecomexpress.sathi.ui.dashboard.attendance.days_attendance_status_dialog.DaysAttendanceStatusDialogProvider;
import in.ecomexpress.sathi.ui.dashboard.campaign.CampaignActivity;
import in.ecomexpress.sathi.ui.dashboard.campaign.CampaignModule;
import in.ecomexpress.sathi.ui.side_drawer.dc_location_updation.DCLocationActivity;
import in.ecomexpress.sathi.ui.side_drawer.dc_location_updation.DCLocationActivityModule;
import in.ecomexpress.sathi.ui.dashboard.drs.map.googlemap.GMapFragmentProvider;
import in.ecomexpress.sathi.ui.dashboard.drs.map.googlemap.info_window.MapInfoDialogProvider;
import in.ecomexpress.sathi.ui.dashboard.fe_earned.EarnedActivityModule;
import in.ecomexpress.sathi.ui.dashboard.fe_earned.Earned_Activity;
import in.ecomexpress.sathi.ui.dashboard.fuel.FuelReimburseActivity;
import in.ecomexpress.sathi.ui.dashboard.fuel.FuelReimburseActivityModule;
import in.ecomexpress.sathi.ui.dashboard.landing.DashboardActivity;
import in.ecomexpress.sathi.ui.dashboard.landing.DashboardActivityModule;
import in.ecomexpress.sathi.ui.dashboard.mapview.MapActivity;
import in.ecomexpress.sathi.ui.dashboard.mapview.MapModule;
import in.ecomexpress.sathi.ui.side_drawer.pendingHistory.PendingHistoryActivity;
import in.ecomexpress.sathi.ui.side_drawer.pendingHistory.PendingHistoryActivityModule;
import in.ecomexpress.sathi.ui.side_drawer.pendingHistory.PendingHistoryDetailActivity;
import in.ecomexpress.sathi.ui.side_drawer.pendingHistory.PendingHistoryDetailActivityModule;
import in.ecomexpress.sathi.ui.dashboard.performance.PerformanceActivity;
import in.ecomexpress.sathi.ui.dashboard.performance.PerformanceActivityModule;
import in.ecomexpress.sathi.ui.side_drawer.profile.ProfileActivity;
import in.ecomexpress.sathi.ui.side_drawer.profile.ProfileActivityModule;
import in.ecomexpress.sathi.ui.side_drawer.drawer_main.SideDrawerActivity;
import in.ecomexpress.sathi.ui.side_drawer.drawer_main.SideDrawerModule;
import in.ecomexpress.sathi.ui.dashboard.starttrip.StartTripActivity;
import in.ecomexpress.sathi.ui.dashboard.starttrip.StartTripDialogModule;
import in.ecomexpress.sathi.ui.dashboard.stoptrip.StopTripActivity;
import in.ecomexpress.sathi.ui.dashboard.stoptrip.StopTripDailogModule;
import in.ecomexpress.sathi.ui.dashboard.switchnumber.SwitchNumberDialogProvider;
import in.ecomexpress.sathi.ui.dashboard.training.TrainingActivity;
import in.ecomexpress.sathi.ui.dashboard.training.TrainingActivityModule;
import in.ecomexpress.sathi.ui.dashboard.unattempted_shipments.UnattemptedShipmentActivity;
import in.ecomexpress.sathi.ui.dashboard.unattempted_shipments.UnattemptedShipmentModule;
import in.ecomexpress.sathi.ui.drs.forward.bpid.ScanBPIDActivity;
import in.ecomexpress.sathi.ui.drs.forward.bpid.ScanBPIDActivityModule;
import in.ecomexpress.sathi.ui.drs.forward.details.ForwardDetailActivity;
import in.ecomexpress.sathi.ui.drs.forward.details.ForwardDetailActivityModule;
import in.ecomexpress.sathi.ui.drs.forward.disputeDailog.DisputeDialogProvider;
import in.ecomexpress.sathi.ui.drs.forward.fill_awb.AwbNumberBPIDDialogProvider;
import in.ecomexpress.sathi.ui.drs.forward.fill_awb.AwbNumberDialogProvider;
import in.ecomexpress.sathi.ui.drs.forward.mps.MPSActivityModule;
import in.ecomexpress.sathi.ui.drs.forward.mps.MPSScanActivity;
import in.ecomexpress.sathi.ui.drs.forward.obd.activity.FwdOBDCompleteActivity;
import in.ecomexpress.sathi.ui.drs.forward.obd.activity.FwdOBDProductDetailActivity;
import in.ecomexpress.sathi.ui.drs.forward.obd.activity.FwdOBDQcFailActivity;
import in.ecomexpress.sathi.ui.drs.forward.obd.activity.FwdOBDQcPassActivity;
import in.ecomexpress.sathi.ui.drs.forward.obd.activity.FwdOBDScannerActivity;
import in.ecomexpress.sathi.ui.drs.forward.obd.activity.FwdOBDStartOTPActivity;
import in.ecomexpress.sathi.ui.drs.forward.obd.activity.FwdOBDStopOTPActivity;
import in.ecomexpress.sathi.ui.drs.forward.obd.module.FwdOBDCompleteModule;
import in.ecomexpress.sathi.ui.drs.forward.obd.module.FwdOBDProductDetailModule;
import in.ecomexpress.sathi.ui.drs.forward.obd.module.FwdOBDQcFailModule;
import in.ecomexpress.sathi.ui.drs.forward.obd.module.OBDFragmentProvider;
import in.ecomexpress.sathi.ui.drs.forward.obd.module.OBDQcPassModule;
import in.ecomexpress.sathi.ui.drs.forward.obd.module.OBDScanModule;
import in.ecomexpress.sathi.ui.drs.forward.obd.module.OBDStartOTPModule;
import in.ecomexpress.sathi.ui.drs.forward.obd.module.OBDStopOTPModule;
import in.ecomexpress.sathi.ui.drs.forward.otherNumber.OtherNumberDialogProvider;
import in.ecomexpress.sathi.ui.drs.forward.shipmentearndialog.ShipmentEarnDialogProvider;
import in.ecomexpress.sathi.ui.drs.forward.signature.SignatureActivity;
import in.ecomexpress.sathi.ui.drs.forward.signature.SignatureActivityModule;
import in.ecomexpress.sathi.ui.drs.forward.success.FWDSuccessActivity;
import in.ecomexpress.sathi.ui.drs.forward.success.FWDSuccessActivityModule;
import in.ecomexpress.sathi.ui.drs.forward.undelivered_fwd.UndeliveredActivity;
import in.ecomexpress.sathi.ui.drs.forward.undelivered_fwd.UndeliveredActivityModule;
import in.ecomexpress.sathi.ui.drs.forward.undelivered_fwd.UndeliveredBPIDActivity;
import in.ecomexpress.sathi.ui.drs.rts.rts_main_list.RTSListActivity;
import in.ecomexpress.sathi.ui.drs.rts.rts_main_list.RTSListActivityModule;
import in.ecomexpress.sathi.ui.drs.rts.rts_scan_and_deliver.RTSScanActivity;
import in.ecomexpress.sathi.ui.drs.rts.rts_scan_and_deliver.RTSScanActivityModule;
import in.ecomexpress.sathi.ui.drs.rts.rts_signature.RTSSignatureActivity;
import in.ecomexpress.sathi.ui.drs.rts.rts_signature.RTSSignatureActivityModule;
import in.ecomexpress.sathi.ui.drs.rts.rts_success.RTSSuccessActivity;
import in.ecomexpress.sathi.ui.drs.rts.rts_success.RTSSuccessActivityModule;
import in.ecomexpress.sathi.ui.drs.rvp.awbscan.CaptureScanActivity;
import in.ecomexpress.sathi.ui.drs.rvp.awbscan.CaptureScanActivityModule;
import in.ecomexpress.sathi.ui.drs.rvp.qc_failure_list.RvpQcFailedActivity;
import in.ecomexpress.sathi.ui.drs.rvp.qc_failure_list.RvpQcFailedActivityModule;
import in.ecomexpress.sathi.ui.drs.rvp.rvp_qc_details.RvpQcDataDetailsActivity;
import in.ecomexpress.sathi.ui.drs.rvp.rvp_qc_details.RvpQcDataDetailsModule;
import in.ecomexpress.sathi.ui.drs.rvp.rvp_qc_details.qc_check.QcCheckFragmentProvider;
import in.ecomexpress.sathi.ui.drs.rvp.rvp_qc_details.qc_check_image.QcCheckImageFragmentProvider;
import in.ecomexpress.sathi.ui.drs.rvp.rvp_qc_details.qc_input.QcInputFragmentProvider;
import in.ecomexpress.sathi.ui.drs.rvp.rvp_qc_list.RvpQcListActivity;
import in.ecomexpress.sathi.ui.drs.rvp.rvp_qc_list.RvpQcListActivityModule;
import in.ecomexpress.sathi.ui.drs.rvp.rvp_secure_activity.RVPSecureDeliveryActivity;
import in.ecomexpress.sathi.ui.drs.rvp.rvp_secure_activity.RVPSecureDeliveryActivityModule;
import in.ecomexpress.sathi.ui.drs.rvp.rvp_secure_activity.RVPSecureDeliveryMapProvider;
import in.ecomexpress.sathi.ui.drs.rvp.signature.RVPSignatureActivity;
import in.ecomexpress.sathi.ui.drs.rvp.signature.RVPSignatureActivityModule;
import in.ecomexpress.sathi.ui.drs.rvp.success.RVPSuccessActivity;
import in.ecomexpress.sathi.ui.drs.rvp.success.RVPSuccessActivityModule;
import in.ecomexpress.sathi.ui.drs.rvp.undelivered.RVPUndeliveredActivity;
import in.ecomexpress.sathi.ui.drs.rvp.undelivered.RVPUndeliveredActivityModule;
import in.ecomexpress.sathi.ui.drs.secure_delivery.SecureDeliveryActivity;
import in.ecomexpress.sathi.ui.drs.secure_delivery.SecureDeliveryActivityModule;
import in.ecomexpress.sathi.ui.drs.sms.SMSDialogProvider;
import in.ecomexpress.sathi.ui.drs.todolist.ToDoListActivity;
import in.ecomexpress.sathi.ui.drs.todolist.ToDoListModule;
import in.ecomexpress.sathi.ui.dummy.eds.ac_document_list_collection.AcDocumentListFragmentProvider;
import in.ecomexpress.sathi.ui.dummy.eds.capture_image.CaptureImageFragmentProvider;
import in.ecomexpress.sathi.ui.dummy.eds.cash_collection.CashCollectionFragmentProvider;
import in.ecomexpress.sathi.ui.dummy.eds.document_list_collection.DocumentListFragmentProvider;
import in.ecomexpress.sathi.ui.dummy.eds.dummy.DummyFragmentProvider;
import in.ecomexpress.sathi.ui.dummy.eds.eds_activity_detail.EDSDetailActivity;
import in.ecomexpress.sathi.ui.dummy.eds.eds_activity_detail.EDSDetailModule;
import in.ecomexpress.sathi.ui.dummy.eds.eds_amazon.AmazonFragmentProvider;
import in.ecomexpress.sathi.ui.dummy.eds.eds_bkyc_idfc.EdsBkycIdfcFragmentProvider;
import in.ecomexpress.sathi.ui.dummy.eds.eds_document_collection.DocumentCollectionFragmentProvider;
import in.ecomexpress.sathi.ui.dummy.eds.eds_document_verification.DocumentVerificationFragmentProvider;
import in.ecomexpress.sathi.ui.dummy.eds.eds_ekyc_XML.EdsEkycFragmentProvider;
import in.ecomexpress.sathi.ui.dummy.eds.eds_ekyc_hdfc.EdsEkycHdfcFragmentProvider;
import in.ecomexpress.sathi.ui.dummy.eds.eds_ekyc_idfc.EdsEkycIdfcFragmentProvider;
import in.ecomexpress.sathi.ui.dummy.eds.eds_ekyc_niyo.EdsEkycNiyoFragmentProvider;
import in.ecomexpress.sathi.ui.dummy.eds.eds_ekyc_rbl.EdsRblFragmentProvider;
import in.ecomexpress.sathi.ui.dummy.eds.eds_hdfc_masking.HDFCMaskingFragmentProvider;
import in.ecomexpress.sathi.ui.dummy.eds.eds_opv.OpvFragmentProvider;
import in.ecomexpress.sathi.ui.dummy.eds.eds_otp.EdsOtpActivity;
import in.ecomexpress.sathi.ui.dummy.eds.eds_otp.EdsOtpModule;
import in.ecomexpress.sathi.ui.dummy.eds.eds_res_opv.ResOpvFragmentProvider;
import in.ecomexpress.sathi.ui.dummy.eds.eds_signature.EDSSignatureActivity;
import in.ecomexpress.sathi.ui.dummy.eds.eds_signature.EDSSignatureActivityModule;
import in.ecomexpress.sathi.ui.dummy.eds.eds_success_fail.EDSSuccessFailActivity;
import in.ecomexpress.sathi.ui.dummy.eds.eds_success_fail.EDSSuccessFailActivityModule;
import in.ecomexpress.sathi.ui.dummy.eds.eds_task_list.EdsTaskListActivity;
import in.ecomexpress.sathi.ui.dummy.eds.eds_task_list.EdsTaskListActivityModule;
import in.ecomexpress.sathi.ui.dummy.eds.edsantwork.EdsEkycAntworkFragmentProvider;
import in.ecomexpress.sathi.ui.dummy.eds.ekyc_freyo.EdsEkycFreyoFragmentProvider;
import in.ecomexpress.sathi.ui.dummy.eds.ekyc_paytm.EdsEkycPaytmFragmentProvider;
import in.ecomexpress.sathi.ui.dummy.eds.icic_standard.IciciEkycFragmentStandardProvider;
import in.ecomexpress.sathi.ui.dummy.eds.icici_ekyc.IciciEkycFragmentProvider;
import in.ecomexpress.sathi.ui.dummy.eds.paytm.PaytmfragmentProvider;
import in.ecomexpress.sathi.ui.dummy.eds.undeilvered_eds.EDSUndeliveredActivity;
import in.ecomexpress.sathi.ui.dummy.eds.undeilvered_eds.UndeliveredModule;
import in.ecomexpress.sathi.ui.dummy.eds.vodafone.VodafoneFragmentProvider;
import in.ecomexpress.sathi.utils.cameraView.CameraSelfieActivity;
import in.ecomexpress.sathi.utils.cameraView.CameraXActivity;
import in.ecomexpress.sathi.utils.cameraView.CameraXModule;

public abstract class ActivityBuilder {

  */
/*  @ContributesAndroidInjector(modules = {LoginActivityModule.class, ForgotDialogProvider.class, ChangePasswordDialogProvider.class})
    abstract LoginActivity bindLoginActivity();

    @ContributesAndroidInjector(modules = {LoginVerifyOtpModule.class})
    abstract LoginVerifyOtpActivity bindLoginVerifyOtpActivity();

    @ContributesAndroidInjector(modules = EdsOtpModule.class)
    abstract EdsOtpActivity bindEdsOtpActivity();

    @ContributesAndroidInjector(modules = {DashboardActivityModule.class, ChangePasswordDialogProvider.class, SwitchNumberDialogProvider.class})
    abstract DashboardActivity bindDashboardActivity();

    @ContributesAndroidInjector(modules = {ProfileActivityModule.class, ChangePasswordDialogProvider.class})
    abstract ProfileActivity bindProfileActivity();

    @ContributesAndroidInjector(modules = {ToDoListModule.class, SwitchNumberDialogProvider.class, GMapFragmentProvider.class, MapInfoDialogProvider.class, SMSDialogProvider.class})
    abstract ToDoListActivity bindToDoListActivity();

    @ContributesAndroidInjector(modules = {ForwardDetailActivityModule.class, OtherNumberDialogProvider.class, DisputeDialogProvider.class})
    abstract ForwardDetailActivity bindForwardDetailActivity();

    @ContributesAndroidInjector(modules = SecureDeliveryActivityModule.class)
    abstract SecureDeliveryActivity bindSecureDeliveryActivity();

    @ContributesAndroidInjector(modules = {ScanBPIDActivityModule.class, AwbNumberBPIDDialogProvider.class})
    abstract ScanBPIDActivity bindScanBPIDActivity();

    @ContributesAndroidInjector(modules = {RTSScanActivityModule.class})
    abstract RTSScanActivity binRtsScanActivity();

    @ContributesAndroidInjector(modules = {OBDQcPassModule.class, OBDFragmentProvider.class})
    abstract FwdOBDQcPassActivity bindFwdOBDQcPassActivity();

    @ContributesAndroidInjector(modules = {FwdOBDQcFailModule.class})
    abstract FwdOBDQcFailActivity bindFwdOBDQcFailActivity();

    @ContributesAndroidInjector(modules = UndeliveredActivityModule.class)
    abstract UndeliveredBPIDActivity bindUndeliveredBPIDActivity();

    @ContributesAndroidInjector(modules = {SignatureActivityModule.class, AwbNumberDialogProvider.class})
    abstract SignatureActivity bindSignatureActivity();

    @ContributesAndroidInjector(modules = {UndeliveredActivityModule.class, AwbNumberDialogProvider.class})
    abstract UndeliveredActivity bindUndeliveredActivity();

    @ContributesAndroidInjector(modules = RvpQcListActivityModule.class)
    abstract RvpQcListActivity bindRvpQcListActivity();

    @ContributesAndroidInjector(modules = FuelReimburseActivityModule.class)
    abstract FuelReimburseActivity bindFuelReimburseActivity();

    @ContributesAndroidInjector(modules = RVPSignatureActivityModule.class)
    abstract RVPSignatureActivity bindRVPSignatureActivity();

    @ContributesAndroidInjector(modules = RVPUndeliveredActivityModule.class)
    abstract RVPUndeliveredActivity bindRVPRVPUndeliveredActivity();

    @ContributesAndroidInjector(modules = CaptureScanActivityModule.class)
    abstract CaptureScanActivity bindCaptureScanActivity();

    @ContributesAndroidInjector(modules = {RvpQcDataDetailsModule.class, QcCheckFragmentProvider.class, QcInputFragmentProvider.class, QcCheckImageFragmentProvider.class})
    abstract RvpQcDataDetailsActivity bindRvpQcDataDetailsActivity();

    @ContributesAndroidInjector(modules = RvpQcFailedActivityModule.class)
    abstract RvpQcFailedActivity bindRVPQcFailedActivity();

    @ContributesAndroidInjector(modules = EdsTaskListActivityModule.class)
    abstract EdsTaskListActivity bindEdsTaskListActivity();

    @ContributesAndroidInjector(modules = {EDSDetailModule.class, AcDocumentListFragmentProvider.class, CaptureImageFragmentProvider.class, ResOpvFragmentProvider.class, OpvFragmentProvider.class, VodafoneFragmentProvider.class, DocumentListFragmentProvider.class, CashCollectionFragmentProvider.class, DummyFragmentProvider.class, DocumentCollectionFragmentProvider.class, EdsEkycFragmentProvider.class, DocumentVerificationFragmentProvider.class, IciciEkycFragmentProvider.class, IciciEkycFragmentStandardProvider.class, AmazonFragmentProvider.class, HDFCMaskingFragmentProvider.class, EdsBkycIdfcFragmentProvider.class, EdsEkycIdfcFragmentProvider.class, EdsEkycHdfcFragmentProvider.class, EdsRblFragmentProvider.class, PaytmfragmentProvider.class, EdsEkycNiyoFragmentProvider.class, EdsEkycPaytmFragmentProvider.class, EdsEkycFreyoFragmentProvider.class, EdsEkycAntworkFragmentProvider.class})
    abstract EDSDetailActivity bindEDSDetailActivity();

    @ContributesAndroidInjector(modules = UndeliveredModule.class)
    abstract EDSUndeliveredActivity bindEDSUndeliveredActivity();

    @ContributesAndroidInjector(modules = RTSListActivityModule.class)
    abstract RTSListActivity bindRTSListActivity();

    @ContributesAndroidInjector(modules = RTSSignatureActivityModule.class)
    abstract RTSSignatureActivity bindRTSSignatureActivity();

    @ContributesAndroidInjector(modules = {RTSSuccessActivityModule.class, ShipmentEarnDialogProvider.class})
    abstract RTSSuccessActivity bindRTSSuccessActivity();

    @ContributesAndroidInjector(modules = {RVPSuccessActivityModule.class, ShipmentEarnDialogProvider.class})
    abstract RVPSuccessActivity bindRVPSuccessActivity();

    @ContributesAndroidInjector(modules = {FWDSuccessActivityModule.class, ShipmentEarnDialogProvider.class})
    abstract FWDSuccessActivity bindFWDSuccessActivity();

    @ContributesAndroidInjector(modules = EDSSignatureActivityModule.class)
    abstract EDSSignatureActivity bindEDSSignatureActivity();

    @ContributesAndroidInjector(modules = {SideDrawerModule.class, SwitchNumberDialogProvider.class, ChangePasswordDialogProvider.class})
    abstract SideDrawerActivity bindSideDrawerActivity();

    @ContributesAndroidInjector(modules = {EDSSuccessFailActivityModule.class, ShipmentEarnDialogProvider.class})
    abstract EDSSuccessFailActivity bindEDSSuccessFailActivity();

    @ContributesAndroidInjector(modules = in.ecomexpress.sathi.ui.dummy.eds.eds_scan.CaptureScanActivityModule.class)
    abstract in.ecomexpress.sathi.ui.dummy.eds.eds_scan.CaptureScanActivity bindEdsCaptureScanActivity();

    @ContributesAndroidInjector(modules = {AttendanceActivityModule.class, CustomdialogProvider.class, DaysAttendanceStatusDialogProvider.class})
    abstract AttendanceActivity bindAttendanceActivity();

    @ContributesAndroidInjector(modules = {AttendanceActivityModule.class, CustomdialogProvider.class, DaysAttendanceStatusDialogProvider.class})
    abstract AttendanceActivity1 bindAttendanceActivity1();

    @ContributesAndroidInjector(modules = PerformanceActivityModule.class)
    abstract PerformanceActivity bindPerformanceActivity();

    @ContributesAndroidInjector(modules = MPSActivityModule.class)
    abstract MPSScanActivity bindMpsScanActivity();

    @ContributesAndroidInjector(modules = DCLocationActivityModule.class)
    abstract DCLocationActivity bindDCLocationActivity();

    @ContributesAndroidInjector(modules = PendingHistoryActivityModule.class)
    abstract PendingHistoryActivity bindPendingHistoryActivity();

    @ContributesAndroidInjector(modules = EarnedActivityModule.class)
    abstract Earned_Activity bindEarned_activity();

    @ContributesAndroidInjector(modules = ADMActivityModule.class)
    abstract ADMActivity bindAdmActivity();

    @ContributesAndroidInjector(modules = TrainingActivityModule.class)
    abstract TrainingActivity trainingActivity();

    @ContributesAndroidInjector(modules = PendingHistoryDetailActivityModule.class)
    abstract PendingHistoryDetailActivity pendingHistoryDetailActivity();


    @ContributesAndroidInjector(modules = {RVPSecureDeliveryActivityModule.class, RVPSecureDeliveryMapProvider.class})
    abstract RVPSecureDeliveryActivity rvpSecureDeliveryActivity();

    @ContributesAndroidInjector(modules = OBDStartOTPModule.class)
    abstract FwdOBDStartOTPActivity myObdOtpActivity();

    @ContributesAndroidInjector(modules = OBDStopOTPModule.class)
    abstract FwdOBDStopOTPActivity myObdOtpVerifyActivity();

    @ContributesAndroidInjector(modules = OBDScanModule.class)
    abstract FwdOBDScannerActivity fwdObdScannerActivity();

    @ContributesAndroidInjector(modules = FwdOBDProductDetailModule.class)
    abstract FwdOBDProductDetailActivity fwdOBDProductDetailActivity();

    @ContributesAndroidInjector(modules = FwdOBDCompleteModule.class)
    abstract FwdOBDCompleteActivity fwdOBDCompleteActivity();

    @ContributesAndroidInjector(modules = MapModule.class)
    abstract MapActivity dashBoardMapActivity();

    @ContributesAndroidInjector(modules = StartTripDialogModule.class)
    abstract StartTripActivity bindStartTripActivity();

    @ContributesAndroidInjector(modules = StopTripDailogModule.class)
    abstract StopTripActivity bindStopTripActivity();

    @ContributesAndroidInjector(modules = CameraXModule.class)
    abstract CameraXActivity bindCameraFilterActivity();

    @ContributesAndroidInjector(modules = UnattemptedShipmentModule.class)
    abstract UnattemptedShipmentActivity bindUnattemptedShipmentActivity();

    @ContributesAndroidInjector(modules = CameraXModule.class)
    abstract CameraSelfieActivity bindSelfieFilterActivity();

    @ContributesAndroidInjector(modules = CampaignModule.class)
    abstract CampaignActivity campaignActivity();

    @ContributesAndroidInjector(modules = ReferModule.class)
    abstract ReferFriendActivity referFriendActivity();*//*


    @ContributesAndroidInjector(modules = RvpQcDetailsModule.class)
    abstract RvpQCDetailActivity rvpQCDetailActivity();


}*/
