package in.ecomexpress.sathi.repo.local.pref;

import android.net.Uri;
import java.util.Set;
import in.ecomexpress.sathi.repo.DataManager;
import in.ecomexpress.sathi.repo.IDataManager;
import in.ecomexpress.sathi.repo.remote.model.login.LoginResponse;

public interface IPreferenceHelper {

    int getCurrentUserLoggedInMode();
    void setCurrentUserLoggedInMode(DataManager.LoggedInMode currentUserLoggedInMode);

    String getAuthToken();
    void setAuthToken(String authToken);

    void setCurrentTimeForDelay(long time);
    long getCurrentTimeForDelay();

    int getTypeId();
    void setTypeId(int pos);

    int getVehicleTypeId();
    void setVehicleTypeId(int pos);

    int getProgressiveTimer();
    void setProgressiveTimer(int timer);

    int getSameDayReassignTimer();
    void setSameDayReassignTimer(int count);

    String getServiceCenter();
    void setServiceCenter(String serviceCenter);

    void setDownloadAPKIsInProcess(long b);
    String getName();
    void setName(String name);

    String getDesignation();
    void setDesignation(String designation);

    String getMobile();
    void setMobile(String mobile);

    String getVodaOrderNo();
    void setVodaOrderNo(String orderNo);

    Long getLocationType();
    void setLocationType(Long locationType);

    String getLocationCode();
    void setLocationCode(String locationCode);

    String getCode();
    void setCode(String code);

    String getAuthPinCode();
    void setAuthPinCode(String authPinCode);

    boolean clearPrefrence();

    boolean getIsUserValided();
    void setIsUserValided(Boolean isUserValided);

    String getPhotoUrl();
    void setPhotoUrl(String photoUrl);

    String getForwardReasonCodeFlag();
    void setForwardReasonCodeFlag(String flag);

    String getRTSReasonCodeFlag();
    void setRTSReasonCodeFlag(String flag);

    void setRtsInputResendFlag(String flag);
    String getRtsInputResendFlag();

    String getRVPReasonCodeFlag();
    void setRVPReasonCodeFlag(String flag);

    String getEDSReasonCodeFlag();
    void setEDSReasonCodeFlag(String flag);

    String getTripId();
    void setTripId(String tripId);

    String getVehicleType();
    void setVehicleType(String vehicleType);

    String getTypeOfVehicle();
    void setTypeOfVehicle(String typeOfVehicle);

    String getRouteName();
    void setRouteName(String routeName);

    long getStartTripMeterReading();
    void setStartTripMeterReading(long reading);

    long getStopTripMeterReading();
    void setStopTripMeterReading(long reading);

    long getActualMeterReading();
    void setActualMeterReading(Long actualMeterReading);

    String getCallITExecutiveNo();
    void setCallITExecutiveNo(String number);

    void increaseNotificationCounter();
    int getNotificationCounter();

    String getPstnFormat();
    void setPstnFormat(String format);

    double getCurrentLatitude();
    void setCurrentLatitude(double latitude);

    double getCurrentLongitude();
    void setCurrentLongitude(double longitude);

    double getDCLatitude();
    double getDCLongitude();

    long isDownloadAPKIsInProcess();

    void updateDCDetails(LoginResponse.DcLocationAddress dcLocationAddress);

    void updateUserLoggedInState(IDataManager.LoggedInMode loggedInMode);

    void saveBottomText(String text);

    String getBottomText();

    String getWebLinkUrl();

    void setWebLinkUrl(String webLinkUrl);

    float getLIVETRACKINGLATLNGACCURACYl();

    void setLIVETRACKINGLATLNGACCURACY(String livetrackinglatlngaccuracy);

    void setStartStopTripMeterReadingDiff(int readingDiff);

    String getTripGeofencing();

    void setTripGeofencing(String tripGeofencing);

    int getStartStopMeterReadingDiff();

    int getSelectedDRSView();

    void setSelectedDRSView(int index);

    int getSelectedSorting();

    void setSelectedSorting(int index);

    String getSOSNumbers();

    void setSOSNumbers(String sosNumbers);

    String getSOSSMSTemplate();

    void setSOSSMSTemplate(String template);

    boolean getFEReachedDC();

    void setFEReachedDC(boolean b);

    int getFWDUnattemptedReasonCode();

    void setFWDUnattemptedReasonCode(int reasonCode);

    int getRVPUnattemptedReasonCode();

    void setRVPUnattemptedReasonCode(int reasonCode);

    int getRTSUnattemptedReasonCode();

    void setRTSUnattemptedReasonCode(int reasonCode);

    int getEDSUnattemptedReasonCode();

    void setEDSUnattemptedReasonCode(int reasonCode);

    int getImageQualityId();

    void setImageQualityId(int image_id);

    void setConsigneeProfiling(boolean enable);

    boolean getConsigneeProfiling();

    boolean getRootDeviceDisabled();

    void setRoodDeviceDisabled(boolean disabled);

    void setActivityCode(String activityCode);

    String getActivityCode();

    void saveEDSRealTimeSync(String  status);

    String getEDSRealTimeSync();

    void saveRVPRealTimeSync(String  status);

    String getRVPRealTimeSync();

    void setEmp_code(String emp_code);

    String getEmp_code();

    void setDcLatitude(String latitude);

    String getDcLatitude();

    void setDcLongitude(String longitude);

    String getDcLongitude();

    void setConsigneeProfileValue(String value);

    String getConsigneeProfileValue();

    void setStopTripDate(long current_date);

    long getStopTripDate();

    int getDCRANGE();

    void setDCRANGE(int range);

    int getREQUEST_RESPONSE_TIME();

    void setREQUEST_RESPONSE_TIME(int time);

    int getREQUEST_RESPONSE_COUNT();

    void setREQUEST_RESPONSE_COUNT(int count);

    boolean isDCLocationUpdateAllowed();

    void SetisDCLocationUpdateAllowed(boolean status);

    void setPreviousTripStatus(Boolean previousTripStatus);

    Boolean getPreviosTripStatus();

    void savePrivateKey(String privateKey);
    String getPrivateKey();

    long getEndTripTime();

    void setEndTripTime(long end_trip_time);

    long getEndTripKm();

    void setEndTripKm(long end_trip_km);

    int getMaxDailyDiffForStartTrip();

    void setMaxDailyDiffForStartTrip(String configValue);

    int getMaxTripRunForStopTrip();

    void setMaxTripRunForStopTrip(String configValue);

     void setRescheduleAttemptTimes(int rescheduleAttemptTimes);

     int getRescheduleAttemptTimes();

    void setDlightSuccessEncrptedOTP(String encrypted_otp);

    String getDlightSuccessEncrptedOTP();

    void setDlightSuccessEncrptedOTPType(String otp_type);

    String getDlightSuccessEncrptedOTPType();

    void setInternetApiAvailable(String flag);

    String getInternetApiAvailable();

    void setENABLEDIRECTDIAL(String flag);

    String getENABLEDIRECTDIAL();

    void setENABLERTSEMAIL(String flag);

    String getENABLERTSEMAIL();

    void setSAMEDAYRESCHEDULE(String flag);

    String getSAMEDAYRESCHEDULE();

    void setRTSIMAGE(String flag);

    String getRTSIMAGE();

    void setDEFAULTSTATISTICS(String flag);

    String getDEFAULTSTATISTICS();

    void setDuplicateCashReceipt(String configValue);

    String getDuplicateCashReceipt();

    void setAmazonOTPStatus(String b);

    void setAmazonOTPTiming(long timeInMillis);

    String getAmazonOTPStatus();

    long getAmazonOTPTiming();

    void setPinBOTPStatus(String b);

    void setAmazonOTPValue(String otp_value);

    void setAmazonPinbValue(String pinb_value);

    void setAmazonList(String amazonList);

    String getAmazonList();

    String getAmazonOTPValue();

    String getAmazonPinbValue();

    void setPinBOTPTimming(long timeInMillis);

    String getPinBOTPStatus();

    long   getPinBOTPTimming();

    void setSyncDelay(long configValue);

    long getSyncDelay();

    void setLoginDate(String date);

    String getLoginDate();

    void setIsScanAwb(boolean isScanAwb);

    void setDcUndeliverStatus(boolean isScanAwb);

    boolean getIsScanAwb();

    boolean getDcUndeliverStatus();

    void setLiveTrackingTripIdForApi(String live_tracking_trip_id);

    String getLiveTrackingTripIdForApi();


    void setAdharConsent(String aadhaar_consent);

    String getAdharConsent();

    void setMAP_DRIVING_MODE(String mapDrivingMode);

    String getMAP_DRIVING_MODE();

    void setLiveTrackingTripId(String live_tracking_trip_id);

    String getLiveTrackingTripId();

    void setLiveTrackingMaxFileSize(int parseInt);

    int getLiveTrackingMaxFileSize();

    void setAdharMessage(String configValue);

    String getAdharMessage();

    void setRVPAWBWords(String configValue);

    String getRVPAWBWords();

    void setRVP_UD_FLYER(String flyerValue);

    String getRVP_UD_FLYER();

    void setAadharFrontImage(String front_image_id);

    void setAadharRearImage(String rear_image_id);

    String getAadharFrontImage();

    String getAadharRearImage();

    void setAadharStatusInterval(String configValue);

    String getAadharStatusInterval();

    void setAadharStatus(boolean b);

    boolean getAadharStatus();

    void setAadharStatusCode(int i);

    int getAadharStatusCode();

    void setStopTrackingAlertFlag(String s);

    String getStopTrackingAlertFlag();

    void setEdsActivityCodes(Set<String> all_edsactivity_codes);

    Set<String> getEdsActivityCodes();

    void setDRSTimeStap(long time_stamp);

    long getDRSTimeStamp();

    void setLatLngLimit(String configValue);

    String getLatLngLimit();

    void setLoginMonth(int mMonth);

    int getLoginMonth();

    void setAadharFrontImageName(String aadhar_front_image);

    void setAadharRearImageName(String aadhar_rear_image);

    void setUndeliverCount(int configValue);

    int getUndeliverCount();

    void setShipperId(int shipper_id);

    int getShipperId();

    void setDirectionDistance(double distance);

    void setDirectionTotalDistance(double distance);

    double getDirectionDistance();

    double getDirectionTotalDistance();

    void setUndeliverConsigneeRANGE(int parseInt);

    int getUndeliverConsigneeRANGE();

    void setIsSignatureImageMandatory(String configValue);

    String getIsSignatureImageMandatory();

    void setIsCallBridgeCheckStatus(String configValue);

    String getIsCallBridgeCheckStatus();

    void setEDISPUTE(String configValue);

    String getEDispute();

    void setCovidConset(boolean configValue);
    boolean getCovidConset();


    void setCovidUrl(String covid_image_url);
    String getCovidUrl();


    void setEcomRegion(String ecom_dlv_region);
    String getEcomRegion();

    void setMessageClicked(String awb , boolean val);
    void setCardClicked(String awb ,    boolean val);

    boolean getMessageClicked(String awb);
    boolean getCardClicked(String awb);

    void setMessageCount(String awb ,int ecode_status_clicked_times_message_link);
    int getMessageCount(String awb);

    int getCardCount(String awb);
    void setCardCount(String awb , int i);

    void setMaxEDSFailAttempt(int configValue);
    int getMaxEDSFailAttempt();

    void setLocationCount(int count);
    int getLocationCount();

    void setLiveTrackingSpeed(String configValue);
    double getLiveTrackingSpeed();

    void setLiveTrackingAccuracy(String configValue);
    int getLiveTrackingAccuracy();

    void setLiveTrackingDisplacement(String configValue);
    float getLiveTrackingDisplacement();

    void setLiveTrackingInterval(String configValue);
    long getLiveTrackingInterval();

    void setLiveTrackingMINSpeed(String configValue);
    double getLiveTrackingMINSpeed();

    void setIsAdmEmp(boolean flag);
    boolean getIsAdmEmp();

    void setFWDPrice(float price);
    float getFWDPrice();

    void setPPDPrice(float price);
    float getPPDPrice();

    void setCODPrice(float price);
    float getCODPrice();

    void setRQCPrice(float price);
    float getRQCPrice();

    void setEDSPrice(float price);
    float getEDSPrice();

    void setRVPPrice(float price);
    float getRVPPrice();

    void setRTSPrice(float price);
    float getRTSPrice();

    void setCurrentTripAmount(String amount);

    String getCurrentTripAmount();

    void setDRSId(long id);
    long getDRSId();

    void setIsQRCodeScanned(boolean b);
    boolean getIsQRCodeScanned();

    String getCallStatusApiInterval();
    void setCallStatusApiInterval(String interval);

    boolean getDirectUndeliver();
    void setDirectUndeliver(boolean b);

    boolean isCallStatusAPIRecursion();
    void setCallAPIRecursion(boolean b);

    long getRequestRsponseTime();
    void setRequestResponseTime(long time);

    void setEnableDPEmployee(boolean parseLong);
    boolean getEnableDPEmployee();

    void setCounterDelivery(boolean b);
    boolean isCounterDelivery();

    void setCodStatusInterval(long parseBoolean);
    long getCodStatusInterval();

    void setCodStatusIntervalStatusFraction(int parseBoolean);
    int getCodeStatusIntervalFraction();

    void setRescheduleMaxAttempts(int parseInt);
    int getRescheduleMaxAttempts();

    void setRescheduleMaxDaysAllow(int parseInt);
    int getRescheduleMaxDaysAllow();

    boolean isUseCamscannerPrintReceipt();
    void setIsUseCamscannerPrintReceipt(boolean val);

    boolean isUseCamscannerDispute();
    void setIsUseCamscannerDispute(boolean val);

    boolean isUseCamscannerTrip();
    void setIsUseCamscannerTrip(boolean val);

    void setSathiLogApiCallInterval(long parseLong);
    long getSathiLogApiCallInterval();

    void setDistance(int parseInt);
    int getDistance();

    void setLiveTrackingCalculatedDistance(float distance);
    float getLiveTrackingCalculatedDistance();

    void setLiveTrackingCalculatedDistanceWithSpeed(float distance);
    float getLiveTrackingCalculatedDistanceWithSpeed();

    void isRecentRemoved(boolean b);
    boolean getIsRecentRemoved();

    void setLoginPermission(boolean b);
    boolean getLoginPermission();

    void setPaymentType(String shipper_id);
    String getPaymentType();

    void setOfflineFwd(boolean parseBoolean);
    boolean getofflineFwd();

    void setLiveTrackingCount(int size);
    int getLiveTrackingCount();

    void setADMUpdated(boolean b);
    boolean isADMUpdated();

    void setAdharPositiveButton(String configValue);
    void setAdharNegativeButton(String configValue);

    String getAdharPositiveButton();
    String getAdharNegativeButton();

    void setDisableResendOtpButtonDuration(long configValue);
    long getDisableResendOtpButtonDuration();

    void setStartTripTime(long currentTimeMillis);
    long getStartTripTime();

    void setSycningBlokingStatus(boolean b);
    boolean getSynckingBlockingStatus();

    void setFilterCount(int filterCount);
    int getFilterCount();

    void setESPSchemeTerms(String terms);

    void setESPReferCode(String refer_code);

    String getESPSchemeTerms();
    String getESPReferCode();

    void setLocationAccuracy(float accuracy);
    float getLocationAccuracy();

    void setPSTNType(String cb_calling_type);
    String getPSTNType();

    void setIsMasterDataAvailable(boolean b);
    boolean isMasterDataAvailable();

    void setFakeApplicatons(String configValue);
    String getFakeApplications();

    void setIsCallAlreadyDone(Boolean aBoolean);
    boolean getIsCallAlreadyDone();

    void setKiranaUser(String configValue);
    String getKiranaUser();

    void setVCallpopup(boolean parseBoolean);
    boolean getVCallPopup();

    void setImageUri(Uri selectedPhotoPath);
    String getImageUri();

    void setStartTrip(boolean b);
    boolean getStatTrip();

    void setForwardCallCount(String awb, int forward_call_count);
    void setRVPCallCount(String awb, int rvp_call_count);
    void setEDSCallCount(String awb, int eds_call_count);
    void setRTSCallCount(String awb, int rts_call_count);

    boolean getCallClicked(String awb);
    void setCallClicked(String awb, boolean isCallClicked);

    int getForwardCallCount(String awb);
    int getRVPCallCount(String awb);
    int getEDSCallCount(String awb);
    int getRTSCallCount(String awb);

    void setForwardMapCount(long awb ,int forward_map_count);
    void setRTSMapCount(long awb ,int rts_map_count);
    void setRVPMapCount(long awb ,int rvp_map_count);
    void setEDSMapCount(long awb ,int eds_map_count);

    int getForwardMapCount(long awb);
    int getRVPMapCount(long awb);
    int getEDSMapCount(long awb);
    int getRTSMapCount(long awb);

    void setStartQCLat(double currentLatitude);
    void setStartQCLng(double currentLongitude);

    String getStartQCLat();
    String getStartQCLng();

    void setOFDOTPVerifiedStatus(String s);
    String getOFDOTPVerifiedStatus();

    void setSKIPOTPREVRQC(String configValue);
    String getSKIPOTPREVRQC();

    void setRVPSecureOTPVerified(String s);
    String getRVPSecureOTPVerified();

    void setUndeliverReasonCode(String select_reason_code_rts);
    String getUndeliverReasonCode();

    void setRVPRQCBarcodeScan(String configValue);
    String getRVPRQCBarcodeScan();

    void setSMSThroughWhatsapp(boolean parseBoolean);
    void setTechparkWhatsapp(String parseBoolean);

    void setTriedReachyouWhatsapp(String parseBoolean);
    boolean getSMSThroughWhatsapp();
    String getTechparkWhatsapp();
    String getTriedReachyouWhatsapp();

    void setTryReachingCount(String awb ,int count);
    int getTryReachingCount(String awb);

    void setSendSmsCount(String awb ,int count);
    int getSendSmsCount(String awb);

    boolean getDPUserBarcodeFlag();
    void setDPUserBarcodeFlag(boolean dpUserFlag);

    void setMultiSpaceApps(String configValue);
    String getMultiSpaceApps();

    void setFeedbackMessage(String feedbackMessage);
    String getFeedbackMessage();

    // Blur Image Work:-
    void setBlurImageType(String blurImageType);
    String getBlurImageType();

    void setSathiAttendanceFeatureEnable(Boolean sathiAttendanceFeatureEnable);
    Boolean getSathiAttendanceFeatureEnable();

    void setBPMismatch(Boolean bpMismatch);
    Boolean getBPMismatch();

    void setUDBPCode(String udbpCode);
    String getUDBPCode();

    void setOBDREFUSED(String obdRefused);
    String getOBDREFUSED();

    void setOBDQCFAIL(String obdQcFail);
    String getOBDQCFAIL();


    void setHideCamera(Boolean hideCamera);
    Boolean getHideCamera();

    void setFWD_UD_RD_OTPVerfied(String awb, boolean b);
    boolean getFWD_UD_RD_OTPVerfied(String awb);

    void setFWDRessign(boolean parseBoolean);
    boolean getFWDRessign();

    void setAddressQualityScore(String configValue);
    String getAddressQualityScore();

    void setImageManualFlyer(Boolean aBoolean);
    boolean getImageManulaFlyer();

    void setSKIP_CANC_OTP_RVP(String configValue);
    String getSKIP_CANC_OTP_RVP();

    void setMasterDataSync(boolean masterDataSync);
    boolean getMasterDataSync();

    void setESP_EARNING_VISIBILITY(boolean espEarningVisibility);
    boolean getESP_EARNING_VISIBILITY();

    void setCheckAttendanceLoginStatus(boolean status);
    boolean getCheckAttendanceLoginStatus();

    void setLoginServerTimeStamp(String timeStamp);
    String getLoginServerTimeStamp();

    void setScreenStatus(Boolean status);
    boolean getScreenStatus();

    boolean getCampaignStatus();

    void setCampaignStatus(Boolean status);

    void setDistanceAPIEnabled(Boolean isDistanceAPIEnabled);
    Boolean getDistanceAPIEnabled();

}
