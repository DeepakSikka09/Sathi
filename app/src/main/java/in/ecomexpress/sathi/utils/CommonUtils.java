package in.ecomexpress.sathi.utils;

import static android.content.Context.TELEPHONY_SERVICE;
import static in.ecomexpress.sathi.utils.Constants.Button;
import static in.ecomexpress.sathi.utils.Constants.Input;
import static in.ecomexpress.sathi.utils.Constants.permissions;
import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.CallLog;
import android.provider.Settings;
import android.telephony.PhoneNumberUtils;
import android.telephony.TelephonyManager;
import android.text.format.Formatter;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.analytics.FirebaseAnalytics;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Random;
import java.util.StringTokenizer;
import java.util.TimeZone;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import in.ecomexpress.sathi.R;
import in.ecomexpress.sathi.SathiApplication;
import in.ecomexpress.sathi.repo.IDataManager;
import in.ecomexpress.sathi.repo.remote.model.drs_list.rts.new_rts.FlagsMap;
import in.ecomexpress.sathi.ui.base.BaseActivity;
import in.ecomexpress.sathi.ui.dashboard.global_activity.MultiSpaceDialogActivity;

public final class CommonUtils {

    private static final int REQUEST_PHONE_CALL = 1;
    private static SecretKeySpec secretKey;
    private static final ExecutorService executor = Executors.newSingleThreadExecutor();

    public static String millisecondToAmPm(long milliSecond) {
        GregorianCalendar cal = new GregorianCalendar();
        StringBuilder sBuf = new StringBuilder(8);
        cal.setTime(new Date(milliSecond));
        int hour = cal.get(java.util.Calendar.HOUR);
        if (hour == 0) {
            hour = 12;
        }
        if (hour < 10) {
            sBuf.append(" ");
        }
        sBuf.append(hour);
        sBuf.append(":");
        int minute = cal.get(java.util.Calendar.MINUTE);
        if (minute < 10) {
            sBuf.append("0");
        }
        sBuf.append(minute);
        sBuf.append(" ");
        sBuf.append(cal.get(java.util.Calendar.AM_PM) == java.util.Calendar.AM ? "AM" : "PM");
        return (sBuf.toString());
    }

    public static int splitDecimal(double number) {
        int integer = (int) number;
        double decimal = (100 * number - 100 * integer);
        return (int) decimal;
    }

    public static boolean startCallIntent(String number, Context context, Activity mContext) {
        try {
            TelephonyManager tMgr = (TelephonyManager) context.getSystemService(TELEPHONY_SERVICE);
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
            int simState = tMgr.getSimState();
            if (simState == TelephonyManager.SIM_STATE_ABSENT) {
                Toast.makeText(context, "No Sim Found!!", Toast.LENGTH_SHORT).show();
                return false;
            }
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(mContext, new String[]{Manifest.permission.CALL_PHONE}, REQUEST_PHONE_CALL);
            } else {
                Constants.call_intent_number = number;
                Intent intent1 = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + number));
                intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent1);
            }
        } catch (NullPointerException e) {
            Logger.e("CommonUtils", String.valueOf(e));
        }
        return true;
    }

    public static String toTitleCase(String str) {
        if (str == null) {
            return null;
        }
        boolean space = true;
        StringBuilder builder = new StringBuilder(str);
        final int len = builder.length();
        for (int i = 0; i < len; ++i) {
            char c = builder.charAt(i);
            if (space) {
                if (!Character.isWhitespace(c)) {
                    builder.setCharAt(i, Character.toTitleCase(c));
                    space = false;
                }
            } else if (Character.isWhitespace(c)) {
                space = true;
            } else {
                builder.setCharAt(i, Character.toLowerCase(c));
            }
        }
        return builder.toString();
    }

    public static ProgressDialog showLoadingDialog(Context context) {
        ProgressDialog progressDialog = new ProgressDialog(context, android.R.style.Theme_Material_Light_Dialog);
        progressDialog.show();
        if (progressDialog.getWindow() != null) {
            progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        progressDialog.setContentView(R.layout.progress_dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        return progressDialog;
    }

    public static String loadJSONFromAsset(Context context, String jsonFileName) {
        byte[] buffer;
        String strStreamData = null;
        AssetManager manager = context.getAssets();
        try (InputStream inputStream = manager.open(jsonFileName)) {
            int size = inputStream.available();
            buffer = new byte[size];
            inputStream.read(buffer);
            inputStream.close();
            strStreamData = new String(buffer, StandardCharsets.UTF_8);
        } catch (Exception e) {
            Logger.e("CommonUtils", String.valueOf(e));
        } finally {
            return strStreamData;
        }
    }

    public static String getDate(long milliSeconds, String dateFormat) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
        java.util.Calendar calendar = java.util.Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }

    public static String getTimeStamp() {
        return new SimpleDateFormat(Constants.TIMESTAMP_FORMAT, Locale.US).format(new Date());
    }

    public static String getLocalIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface networkInterface = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddress = networkInterface.getInetAddresses(); enumIpAddress.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddress.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        return Formatter.formatIpAddress(inetAddress.hashCode());
                    }
                }
            }
        } catch (SocketException ex) {
            Logger.e("CommonUtils", String.valueOf(ex));
        }
        return null;
    }

    public static boolean isAppInstalled(String uri, Context context) {
        PackageManager pm = context.getApplicationContext().getPackageManager();
        boolean appInstalled;
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            appInstalled = true;
        } catch (PackageManager.NameNotFoundException e) {
            appInstalled = false;
        }
        return appInstalled;
    }

    public static String imeiFullStars(String mobileNo) {
        return mobileNo.replaceAll("^07", "*");
    }

    @SuppressLint("HardwareIds")
    public static String getImei(Context context) {
        String imei;
        try {
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                return "123";
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                imei = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
            } else {
                imei = telephonyManager.getDeviceId();
            }
            if (imei == null || imei.isEmpty()) {
                imei = "123456789";
            }
        } catch (Exception e) {
            imei = "12345678";
        }
        return imei;
    }

    public static boolean isStringMatch(String newstring, String oldstring) {
        if (newstring == null || newstring.isEmpty()) {
            return false;
        }
        if (oldstring == null || oldstring.isEmpty()) {
            return false;
        }
        return newstring.equals(oldstring);
    }

    public synchronized static String nullToEmpty(String value) {
        if (value == null) {
            return "";
        } else {
            return value.trim();
        }
    }

    public static boolean isAllPermissionAllow(BaseActivity baseActivity) {
        for (String permission : permissions) {
            if (!baseActivity.hasPermission(permission)) {
                return false;
            }
        }
        return true;
    }

    public static String decrypt(String strToDecrypt, String secret) {
        try {
            setKey(secret);
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            return new String(cipher.doFinal(Base64.getDecoder().decode(strToDecrypt)));
        } catch (Exception e) {
            System.out.println("Error while decrypting: " + e);
        }
        return null;
    }

    public static void setKey(String myKey) {
        MessageDigest sha;
        try {
            byte[] key = myKey.getBytes(StandardCharsets.UTF_8);
            sha = MessageDigest.getInstance("SHA-256");
            key = sha.digest(key);
            key = Arrays.copyOf(key, 16);
            secretKey = new SecretKeySpec(key, "AES");
        } catch (NoSuchAlgorithmException e) {
            Logger.e("CommonUtils", String.valueOf(e));
        }
    }

    public static boolean turnGPSOn(Context context) {
        LocationManager mLocationManager = (LocationManager) context.getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        boolean isGPSOn = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!isGPSOn) {
            Intent callGPSSettingIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            callGPSSettingIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(callGPSSettingIntent);
            return false;
        } else {
            return true;
        }
    }

    public static void copy(File src, File dst) throws IOException {
        try (InputStream in = new FileInputStream(src)) {
            try (OutputStream out = new FileOutputStream(dst)) {
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
            }
        }
    }

    public static boolean checkMultiSpace(Context context, IDataManager mDataManager) {
        boolean isNotMulitSpace = true;
        new StringTokenizer(mDataManager.getMultiSpaceApps(), ",");
        String[] arr_fakeApplications = mDataManager.getMultiSpaceApps().split(",");
        for (String packageName : arr_fakeApplications) {
            boolean isPackageInstalled = isPackageInstalled(packageName, context.getPackageManager());
            if (isPackageInstalled) {
                Intent i = new Intent(context, MultiSpaceDialogActivity.class);
                i.putExtra("FlagType", "MultiSpace");
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(i);
                isNotMulitSpace = false;
                break;
            } else {
                if (MultiSpaceDialogActivity.finishActivityFlag != null) {
                    MultiSpaceDialogActivity.finishActivityFlag.finish();
                }
            }
        }
        return isNotMulitSpace;
    }

    private static boolean isPackageInstalled(String packageName, PackageManager packageManager) {
        try {
            packageManager.getPackageInfo(packageName, 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    public static double calculateLaplacianVariance(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        // Convert to grayscale:-
        int[] pixels = new int[width * height];
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
        double[][] gray = new double[width][height];
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                int pixel = pixels[i * width + j];
                int r = (pixel >> 16) & 0xFF; // For Red Pixel
                int g = (pixel >> 8) & 0xFF; // For Green Pixel
                int b = (pixel) & 0xFF; // For Green Blue
                /*
                * Standard Formula for GreyScale Conversion:- Gray = 0.299×R + 0.587×G + 0.114×B
                * These weights are based on human eye sensitivity:👀 Human eyes are
                    1. Most sensitive to Green (G) → 0.587
                    2. Less sensitive to Red (R) → 0.299
                    3. Least sensitive to Blue (B) → 0.114
                * */
                gray[j][i] = 0.299 * r + 0.587 * g + 0.114 * b;
            }
        }

        // Apply Laplacian filter & Calculate Variance:-
        double sum = 0, sumOfSquares = 0;
        int count = 0;
        for (int i = 1; i < height - 1; i++) {
            for (int j = 1; j < width - 1; j++) {
                double laplacian = Math.abs(4 * gray[j][i] - gray[j - 1][i] - gray[j + 1][i] - gray[j][i - 1] - gray[j][i + 1]);
                sum += laplacian;
                sumOfSquares += laplacian * laplacian;
                count++;
            }
        }

        // Calculating Variance:- variance = Σ(x - mean)² / N
        double mean = sum / count;
        return (sumOfSquares / count) - (mean * mean);
    }

    public static LinkedHashMap<String, String> convertStringToLinkedHashMap(String inputString) {
        LinkedHashMap<String, String> linkedHashMap = new LinkedHashMap<>();
        String[] words = inputString.split(",");
        for (int i = 0; i < words.length; i++) {
            linkedHashMap.put("key" + (i + 1), words[i].trim());
        }
        return linkedHashMap;
    }

    public static boolean checkImageIsBlurryOrNot(Activity context, String moduleName, Bitmap bitmap, int imageCaptureCount, IDataManager dataManager) {
        if ((convertStringToLinkedHashMap(dataManager.getBlurImageType())).containsValue(moduleName)) {
            double varianceValue = calculateLaplacianVariance(bitmap);
            if (varianceValue < 150 && imageCaptureCount < 3) {
                Snackbar snackbar = Snackbar.make(context.findViewById(android.R.id.content), R.string.clicked_image_is_not_clear_click_it_again, Snackbar.LENGTH_LONG);
                View view = snackbar.getView();
                view.setBackgroundColor(context.getResources().getColor(R.color.red_ecom));
                TextView tv = view.findViewById(R.id.snackbar_text);
                tv.setTextColor(Color.WHITE);
                snackbar.show();
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

    public static String getWhatsAppRemarkTemplate(String FE_Name, String FE_Number, String awb_no, String brand_name) {
        return "Hi, I am " + FE_Name + ", your Ecom Express agent.\n"
                + "I tried reaching you for your shipment with AWB " + awb_no + " from " + brand_name + " but unable to locate your address.\n"
                + "To assist in fulfilling your order,\n" + "1️⃣ Please share the 📍 location on the same chat\n"
                + "2️⃣ Please share alternate 📳 contact number for better coordination\n"
                + "In case of any issues, please contact me.\n"
                + FE_Name + "\n" + FE_Number + "\n"
                + "Looking forward to a great experience! 🤝🏻\n"
                + "Ecom Express";
    }

    public static void sendSMSViaWhatsApp(Context applicationContext, Activity activity, String smsNumber, String whatsAppRemarkTemplate) throws UnsupportedEncodingException {
        String url = "https://api.whatsapp.com/send?phone=+91" + smsNumber + "&text=" + whatsAppRemarkTemplate + URLEncoder.encode("", "UTF-8");
        if (whatsappInstalledOrNot("com.whatsapp.w4b", applicationContext)) {
            try {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_VIEW)
                        .setPackage("com.whatsapp.w4b")
                        .setData(Uri.parse(url))
                        .putExtra("jid", PhoneNumberUtils.stripSeparators(smsNumber) + "@s.whatsapp.net");
                applicationContext.startActivity(sendIntent);
            } catch (Exception e) {
                Logger.e("CommonUtils", String.valueOf(e));
            }
        } else if (whatsappInstalledOrNot("com.whatsapp", applicationContext)) {
            try {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_VIEW)
                        .setPackage("com.whatsapp")
                        .setData(Uri.parse(url))
                        .putExtra("jid", PhoneNumberUtils.stripSeparators(smsNumber) + "@b.whatsapp.net");
                applicationContext.startActivity(sendIntent);
            } catch (Exception e) {
                Logger.e("CommonUtils", String.valueOf(e));
            }
        } else {
            sendSmsViaNumber(activity, smsNumber, whatsAppRemarkTemplate);
        }
    }

    private static boolean whatsappInstalledOrNot(String uri, Context applicationContext) {
        PackageManager pm = applicationContext.getPackageManager();
        boolean app_installed;
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            app_installed = true;
        } catch (PackageManager.NameNotFoundException e) {
            app_installed = false;
        }
        return app_installed;
    }

    public static void sendSmsViaNumber(Activity activity, String smsNumber, String whatsAppRemarkTemplate) {
        if (smsNumber.length() == 10) {
            smsNumber = "+91" + smsNumber;
        }
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("smsto:" + smsNumber));
        intent.putExtra("sms_body", whatsAppRemarkTemplate);
        activity.startActivity(intent);
    }

    public static AlertDialog.Builder showDialogBoxForWhatsApp(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.Base_ThemeOverlay_AppCompat_Dialog_Alert);
        builder.setMessage(R.string.tried_reaching);
        return builder;
    }

    public static String randomStringSelector(String input) {
        String[] messages = input.split(",");
        Random randomGenerator = new Random();
        int randomIndex = randomGenerator.nextInt(messages.length);
        return messages[randomIndex];
    }

    public static boolean isDeveloperModeEnabled(Context context) {
        int developerModeEnabled = Settings.Global.getInt(context.getContentResolver(), Settings.Global.DEVELOPMENT_SETTINGS_ENABLED, 0);
        return developerModeEnabled != 0;
        // return false;
    }

    public static boolean checkDeveloperMode(Context context) {
        boolean isDeveloperModeEnable = false;
        if (isDeveloperModeEnabled(context)) {
            Intent developerModeIntent = new Intent(context, MultiSpaceDialogActivity.class);
            developerModeIntent.putExtra("FlagType", "DeveloperModeEnable");
            developerModeIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(developerModeIntent);
            isDeveloperModeEnable = true;
        } else {
            if (MultiSpaceDialogActivity.finishActivityFlag != null) {
                MultiSpaceDialogActivity.finishActivityFlag.finish();
            }
        }
        return isDeveloperModeEnable;
    }

    public static boolean isValidPatterForRVPFlyerScan(String input) {
        if (input == null || input.isEmpty()) {
            return false;
        }
        Pattern pattern = Pattern.compile("^(?=.*[a-zA-Z])(?=.*[0-9])[a-zA-Z0-9]+$");
        Matcher matcher = pattern.matcher(input);
        return matcher.matches();
    }

    public static boolean containsSpecialCharacterOrBlank(String input) {
        if (input == null || input.trim().isEmpty()) {
            return true;
        }
        return !input.matches("[a-zA-Z0-9]+");
    }

    // Delete call logs using content resolver:-

    public static void deleteNumberFromCallLogsAsync(String number, Context context) {
        executor.execute(() -> deleteNumberFromCallLogs(number, context));
    }

    public static void deleteNumberFromCallLogs(String number, Context context) {
        try {
            String searchNumber = number.contains(",") ? extractPrimaryNumber(number) : number;
            Uri callUri = CallLog.Calls.CONTENT_URI;
            Cursor cursor = context.getContentResolver().query(callUri, null, CallLog.Calls.NUMBER + " LIKE ?", new String[]{searchNumber + "%"}, null);

            if (cursor != null) {
                int idColumnIndex = cursor.getColumnIndex(CallLog.Calls._ID);
                if (idColumnIndex != -1 && cursor.moveToFirst()) {
                    do {
                        String id = cursor.getString(idColumnIndex);
                        context.getContentResolver().delete(callUri, CallLog.Calls._ID + "=?", new String[]{id});
                    } while (cursor.moveToNext());
                }
                cursor.close();
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    private static String extractPrimaryNumber(String formattedNumber) {
        int commaIndex = formattedNumber.indexOf(',');
        if (commaIndex != -1) {
            return formattedNumber.substring(0, commaIndex);
        } else {
            return formattedNumber;
        }
    }

    // Checking image count for particular awb:-
    public static int capturedImageCount(long awb) {
        int imageCount = 0;
        if (SathiApplication.rtsCapturedImage1.get(awb) == null || SathiApplication.rtsCapturedImage2.get(awb) == null) {
            return 0;
        }
        if (Boolean.TRUE.equals(SathiApplication.rtsCapturedImage1.get(awb))) {
            imageCount++;
        }
        if (Boolean.TRUE.equals(SathiApplication.rtsCapturedImage2.get(awb))) {
            imageCount++;
        }
        return imageCount;
    }

    // Check value of getFlagsMap and getRET_DEL_IMAGE, it can be null, true, false or blank string. Return the the value true if condition meet.
    public static boolean getRtsDeliveredImagesValue(FlagsMap flagsMap) {
        boolean rtsDeliveredImage;
        if (flagsMap == null || flagsMap.getRET_DEL_IMAGE() == null || flagsMap.getRET_DEL_IMAGE().isEmpty() || flagsMap.getRET_DEL_IMAGE().equalsIgnoreCase("false")) {
            rtsDeliveredImage = false;
        } else {
            rtsDeliveredImage = flagsMap.getRET_DEL_IMAGE().equalsIgnoreCase("true");
        }
        return rtsDeliveredImage;
    }

    // Show custom snackbar:-
    public static void showCustomSnackbar(View view, String message, Context context) {
        Snackbar snackbar = Snackbar.make(view, message, Snackbar.LENGTH_SHORT);
        View snackbarView = snackbar.getView();
        snackbarView.setMinimumHeight(20);
        snackbarView.setBackgroundColor(ContextCompat.getColor(context, R.color.red_ecom));
        TextView textView = snackbarView.findViewById(com.google.android.material.R.id.snackbar_text);
        textView.setTextSize(13);
        textView.setTextColor(Color.WHITE);
        textView.setTypeface(textView.getTypeface(), Typeface.BOLD);
        snackbar.show();
    }

    public static boolean isEmpty(@Nullable String value) {
        return (value == null && value.trim().isEmpty());
    }

    @SuppressLint("SimpleDateFormat")
    public static int convertMillisToDate(String millis) {
        try {
            long timestamp = Long.parseLong(millis);
            Date date = new Date(timestamp);
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd");
            dateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Kolkata"));
            return Integer.parseInt(dateFormat.format(date));
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    @SuppressLint("SimpleDateFormat")
    public static int convertMillisToMonth(String millis) {
        try {
            long timestamp = Long.parseLong(millis);
            Date date = new Date(timestamp);
            SimpleDateFormat monthFormat = new SimpleDateFormat("M");
            monthFormat.setTimeZone(TimeZone.getTimeZone("Asia/Kolkata"));
            return Integer.parseInt(monthFormat.format(date));
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    public static boolean verifyVehicleNumber(String vehicleNumber) {
        boolean flag = false;
        try {
            if (vehicleNumber.toUpperCase(Locale.ENGLISH) != null) {
                Pattern pattern = Pattern.compile(Constants.VEHICLE_REGEX);
                flag = pattern.matcher(vehicleNumber.toUpperCase(Locale.ENGLISH)).matches();
            }
        } catch (Exception e) {
            Logger.e("exp", String.valueOf(e));
        }
        return !flag;
    }

    // Get shortname from the full name:-
    public static String getShortName(String fullName) {
        if (fullName == null || fullName.trim().isEmpty()) {
            return "";
        }
        String[] nameParts = fullName.trim().split("\\s+");
        char firstNameInitial = nameParts[0].charAt(0);
        char lastNameInitial = nameParts[nameParts.length - 1].charAt(0);
        return (Character.toUpperCase(firstNameInitial) + "" + Character.toUpperCase(lastNameInitial));
    }

    // Google Analytics For Screen Time:-
    public static void logScreenNameInGoogleAnalytics(String screenName, Context context) {
        FirebaseAnalytics firebaseAnalytics = FirebaseAnalytics.getInstance(context);
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.SCREEN_NAME, screenName);
        firebaseAnalytics.logEvent(screenName, bundle);
    }

    // Google Analytics For Button Time:-
    public static void logButtonEventInGoogleAnalytics(String screenName, String buttonName, String value, Context context) {
        FirebaseAnalytics firebaseAnalytics = FirebaseAnalytics.getInstance(context);
        Bundle bundle = new Bundle();
        bundle.putString(Button, buttonName);
        if (!value.isEmpty()) {
            bundle.putString(Input, value);
        }
        firebaseAnalytics.logEvent(screenName, bundle);
    }

    // Apply transition to move to next activity.
    public static void applyTransitionToOpenActivity(Activity activity) {
        activity.overridePendingTransition(R.anim.activity_slide_in_left, R.anim.activity_slide_out_right);
    }

    // Apply transition to back from the activity.
    public static void applyTransitionToBackFromActivity(Activity activity) {
        activity.overridePendingTransition(R.anim.activity_slide_in_right, R.anim.activity_slide_out_left);
    }

    public static String imeiTailStars(String imei, int count) {
        if (imei.isEmpty() && imei.length() < count) {
            return "No Data Defined";
        } else {
            String headString = imei.substring(0, imei.length() - count);
            String tailString = imei.substring(imei.length() - count).replaceAll("(?s).", "*");
            return headString + tailString;
        }
    }

    public static String getTrailStarredString(String imei, int count) {
        if (!imei.isEmpty() && imei.length() >= count) {
            return imei.substring(imei.trim().length() - count, imei.trim().length());
        }
        return "0";
    }

    public static String imeiHeadStars(String imei, int count) {
        if (imei.isEmpty() && imei.length() < count) {
            return "No Data Defined";
        } else {
            String headString = imei.substring(0, count).replaceAll("(?s).", "*");
            String tailString = imei.substring(count);
            return headString + tailString;
        }
    }

    public static String getHeadStarredString(String imei, int count) {
        if (!imei.isEmpty() && imei.length() >= count) {
            return imei.substring(0, count);
        }
        return "0";
    }

    public static String imeiFirstLastFourVisible(String imei) {
        if (imei.length() >= 8) {
            String firstFour = imei.substring(0, 4);
            String lastFour = imei.substring(imei.length() - 4);
            int numStars = imei.length() - 8;
            String stars = String.join("", Collections.nCopies(numStars, "*"));
            return firstFour + stars + lastFour;
        } else if (imei.length() >= 4) {
            String frontTwo = imei.substring(0, 2);
            String backTwo = imei.substring(imei.length() - 2);
            int numStars = imei.length() - 4;
            String stars = String.join("", Collections.nCopies(numStars, "*"));
            return frontTwo + stars + backTwo;
        } else {
            return imei;
        }
    }

    public static Bitmap convertPathtoBitmap(String imagePathWithWaterMark) {
        return BitmapFactory.decodeFile(imagePathWithWaterMark);
    }

    public static String getTimeStampToDate(long timeStamp){
        Date date = new Date(timeStamp);
        DateFormat formattedDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:");
        return formattedDate.format(date);
    }
}