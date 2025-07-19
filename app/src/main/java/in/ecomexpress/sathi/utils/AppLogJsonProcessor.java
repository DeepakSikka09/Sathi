package in.ecomexpress.sathi.utils;

import android.content.Context;
import android.os.Build;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

import in.ecomexpress.sathi.BuildConfig;
import in.ecomexpress.sathi.SathiApplication;
public class AppLogJsonProcessor {


    final static String TAG = AppLogJsonProcessor.class.getSimpleName();
    final private static String filePathErrors = Logger.getHiddenDir("SathiCrashLogs.log");
    final private static String filePathEvents = Logger.getHiddenDir("SathiEventLogs.log");
    private static Context mContext;

    public enum LogType {
        ERROR, EVENT
    }

    public static void init(Context context) {
        mContext = context;
    }

    /**
     * @param logType
     * @param exception This exception in string format not in object of Exception class.
     * @param lat
     * @param lng
     * @param timeStamp
     * @param empCode
     */
    public static void appendErrorJSONObject(LogType logType, String exception, double lat, double lng, long timeStamp, String empCode) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                synchronized (AppLogJsonProcessor.class) {
                    String filePath = null;
                    switch (logType) {
                        case ERROR:
                            filePath = filePathErrors;
                            break;
                        case EVENT:
                            filePath = filePathEvents;
                            break;
                    }
                    JSONObject currentCrashReport = getCurrentCrashReport(filePath);
                    if (currentCrashReport == null) {
                        currentCrashReport = new JSONObject();
                        try {
                            //1. Emp Code
                            currentCrashReport.put("emp_code", empCode);
                            currentCrashReport.put("app_name", SathiApplication.APP_NAME);
                            currentCrashReport.put("app_version", BuildConfig.VERSION_NAME);

                            //2. Device Details
                            JSONObject jsonObjectDeviceDetails = new JSONObject();
                            jsonObjectDeviceDetails.put("name", getDeviceName());
                            jsonObjectDeviceDetails.put("model_no", getModelNumber());
                            jsonObjectDeviceDetails.put("manufacturer", getManufacturer());
                            jsonObjectDeviceDetails.put("os_version", getOSVersion());
                            jsonObjectDeviceDetails.put("kernal_version", getKernalVersion());
                            jsonObjectDeviceDetails.put("manufacturer_os_version", getManufacturerOSVersion());
                            currentCrashReport.put("device_details", jsonObjectDeviceDetails);

                            //3. Errors
                            JSONArray jsonArrayNode = new JSONArray();
                            switch (logType) {
                                case ERROR:
                                    currentCrashReport.put("errors", jsonArrayNode);
                                    break;
                                case EVENT:
                                    currentCrashReport.put("events", jsonArrayNode);
                                    break;
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }

                    try {
                        JSONArray jsonArrayErrors = null;
                        switch (logType) {
                            case ERROR:
                                jsonArrayErrors = currentCrashReport.getJSONArray("errors");
                                break;
                            case EVENT:
                                jsonArrayErrors = currentCrashReport.getJSONArray("events");
                                break;
                        }

                        //Create nested object of error for errors
                        JSONObject jsonObjectNode = new JSONObject();
                        jsonObjectNode.put("timestamp", timeStamp);
                        switch (logType) {
                            case ERROR:
                                jsonObjectNode.put("error_body", exception.trim());
                                break;
                            case EVENT:
                                jsonObjectNode.put("event_body", exception.trim());
                                break;
                        }

                        //create nested object of location for single error
                        JSONObject jsonObjectLocation = new JSONObject();
                        jsonObjectLocation.put("lat", lat);
                        jsonObjectLocation.put("lng", lng);

                        //put location object into error
                        jsonObjectNode.put("location", jsonObjectLocation);

                        //put nested object of error in errors array
                        jsonArrayErrors.put(jsonObjectNode);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Logger.d(TAG, "final Json error log file:\n" + currentCrashReport.toString());
                    switch (logType) {
                        case ERROR:
                            writeFile(filePathErrors, currentCrashReport);
                            break;
                        case EVENT:
                            writeFile(filePathEvents, currentCrashReport);
                            break;
                    }
                }
            }
        });
//                .start();

    }

    /**
     * @param logType
     * @param exception This exception in string format not in object of Exception class.
     * @param lat
     * @param lng
     * @param timeStamp
     * @param empCode
     */
    public static void appendSyncServiceLogs(LogType logType, String exception, double lat, double lng, long timeStamp, String empCode) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                synchronized (AppLogJsonProcessor.class) {
                    String filePath = null;
                    switch (logType) {
                        case ERROR:
                            filePath = filePathErrors;
                            break;
                        case EVENT:
                            filePath = filePathEvents;
                            break;
                    }
                    JSONObject currentCrashReport = getCurrentCrashReport(filePath);
                    if (currentCrashReport == null) {
                        currentCrashReport = new JSONObject();
                        try {
                            //1. Emp Code
                            currentCrashReport.put("emp_code", empCode);
                            currentCrashReport.put("app_name", SathiApplication.APP_NAME);
                            currentCrashReport.put("app_version", BuildConfig.VERSION_NAME);

                            //2. Device Details
                            JSONObject jsonObjectDeviceDetails = new JSONObject();
                            jsonObjectDeviceDetails.put("name", getDeviceName());
                            jsonObjectDeviceDetails.put("model_no", getModelNumber());
                            jsonObjectDeviceDetails.put("manufacturer", getManufacturer());
                            jsonObjectDeviceDetails.put("os_version", getOSVersion());
                            jsonObjectDeviceDetails.put("kernal_version", getKernalVersion());
                            jsonObjectDeviceDetails.put("manufacturer_os_version", getManufacturerOSVersion());
                            currentCrashReport.put("device_details", jsonObjectDeviceDetails);

                            //3. Errors
                            JSONArray jsonArrayNode = new JSONArray();
                            switch (logType) {
                                case ERROR:
                                    currentCrashReport.put("errors", jsonArrayNode);
                                    break;
                                case EVENT:
                                    currentCrashReport.put("events", jsonArrayNode);
                                    break;
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }

                    try {
                        JSONArray jsonArrayErrors = null;
                        switch (logType) {
                            case ERROR:
                                jsonArrayErrors = currentCrashReport.getJSONArray("errors");
                                break;
                            case EVENT:
                                jsonArrayErrors = currentCrashReport.getJSONArray("events");
                                break;
                        }

                        //Create nested object of error for errors
                        JSONObject jsonObjectNode = new JSONObject();
                        jsonObjectNode.put("timestamp", timeStamp);
                        switch (logType) {
                            case ERROR:
                                jsonObjectNode.put("error_body", exception.trim());
                                break;
                            case EVENT:
                                jsonObjectNode.put("event_body", exception.trim());
                                break;
                        }

                        //create nested object of location for single error
                        JSONObject jsonObjectLocation = new JSONObject();
                        jsonObjectLocation.put("lat", lat);
                        jsonObjectLocation.put("lng", lng);

                        //put location object into error
                        jsonObjectNode.put("location", jsonObjectLocation);

                        //put nested object of error in errors array
                        jsonArrayErrors.put(jsonObjectNode);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Logger.d(TAG, "final Json error log file:\n" + currentCrashReport.toString());
                    switch (logType) {
                        case ERROR:
                            writeFile(filePathErrors, currentCrashReport);
                            break;
                        case EVENT:
                            writeFile(filePathEvents, currentCrashReport);
                            break;
                    }
                }
            }
        })
                .start();

    }

    private static void writeFile(String filePath, JSONObject crashReport) {
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                file.createNewFile();
            }
            FileWriter fw = new FileWriter(file.getAbsolutePath());
            fw.write(crashReport.toString());
            fw.close();
        } catch (Exception e) {
            System.out.println(e);
        }
        System.out.println("Success...");

    }

    private static String getCrashLogFileContent() {
        try {

            BufferedReader br = new BufferedReader(new FileReader(filePathErrors));
            String st;
            StringBuilder stringBuilder = new StringBuilder();
            while ((st = br.readLine()) != null)
                stringBuilder.append(st);

            return stringBuilder.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    private static JSONObject getCurrentCrashReport(String filePath) {
        try {
            File file = new File(filePath);
            try {
                if (!file.exists()) {
                    file.createNewFile();
                }
                BufferedReader br = new BufferedReader(new FileReader(file));
                String st;
                StringBuilder stringBuilder = new StringBuilder();
                while ((st = br.readLine()) != null)
                    stringBuilder.append(st);

                return new JSONObject(stringBuilder.toString());
            } catch (Exception e) {
//                e.printStackTrace();
            }
        } catch (Exception e) {
//            e.printStackTrace();
        }
        return null;
    }

    private static String getDeviceName() {
        try {
            return Build.BRAND;
        } catch (Error noSuchFieldError) {
            return noSuchFieldError.getMessage();
        }

    }

    private static String getModelNumber() {

        try {
            return Build.MODEL;
        } catch (Error noSuchFieldError) {
            return noSuchFieldError.getMessage();
        }
    }

    private static String getManufacturer() {

        try {
            return Build.MANUFACTURER;
        } catch (Error noSuchFieldError) {
            return noSuchFieldError.getMessage();
        }
    }

    public static String getOSVersion() {
        try {
            return Build.VERSION.BASE_OS;
        } catch (Error noSuchFieldError) {
            return noSuchFieldError.getMessage();
        }
    }

    public static String getKernalVersion() {

        try {
            return Build.VERSION.CODENAME;
        } catch (Error noSuchFieldError) {
            return noSuchFieldError.getMessage();
        }
    }

    public static String getManufacturerOSVersion() {
        return "";
    }
}