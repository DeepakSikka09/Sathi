package in.ecomexpress.sathi.ui.dummy.eds.eds_ekyc_XML;

import androidx.databinding.ObservableField;

import android.app.Application;
import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.net.ssl.HttpsURLConnection;

import dagger.hilt.android.lifecycle.HiltViewModel;
import in.ecomexpress.sathi.SathiApplication;
import in.ecomexpress.sathi.repo.IDataManager;
import in.ecomexpress.sathi.repo.remote.model.drs_list.edsnew.EDSActivityWizard;
import in.ecomexpress.sathi.repo.remote.model.ekyc_xml_response.ekycXMLResponse;
import in.ecomexpress.sathi.repo.remote.model.masterdata.MasterActivityData;
import in.ecomexpress.sathi.ui.base.BaseViewModel;
import in.ecomexpress.sathi.utils.rx.ISchedulerProvider;
import io.reactivex.functions.Consumer;

@HiltViewModel
public class EdsEkycXMLViewModel extends BaseViewModel<IEdsEkycXMLFragmentNavigator> {
    public ObservableField<MasterActivityData> masterActivityData = new ObservableField<>();
    public ObservableField<EDSActivityWizard> edsActivityWizard = new ObservableField<>();
    public final ObservableField<String> inputData = new ObservableField<>("");

    @Inject
    public EdsEkycXMLViewModel(IDataManager dataManager, ISchedulerProvider schedulerProvider , Application sathiApplication) {
        super(dataManager, schedulerProvider ,sathiApplication);
    }
    public void setCheckStatus(String checkStatus) {
        this.inputData.set(checkStatus);
    }

    public void ongetPid() {
        getNavigator().ongetPid();
    }
    public void validateurn() {
        getNavigator().validateurn();
    }
    public void validateAdhaar() {
        getNavigator().validateAdhaar();
    }
    public void setData(EDSActivityWizard edsActivityWizard, MasterActivityData masterActivityData) {
        this.edsActivityWizard.set(edsActivityWizard);
        this.masterActivityData.set(masterActivityData);
    }

    public void login(String packet, HashMap<String, String> webheader) {
        try {
            setIsLoading(true);
            final long timeStamp = Calendar.getInstance().getTimeInMillis();
            writeRestAPIRequst(timeStamp, packet);
            getCompositeDisposable().add(getDataManager()
                    .doEkycXMLApiCall(getDataManager().getEcomRegion(),packet, webheader)
                    .doOnSuccess(new Consumer<ekycXMLResponse>() {
                        @Override
                        public void accept(ekycXMLResponse iciciResponse) {
                            writeRestAPIResponse(timeStamp, iciciResponse);
                            // Log.d("ICICI",iciciResponse.)
                            // getNavigator().sendICICIResponse(iciciResponse);
                        }
                    })
                    .subscribeOn(getSchedulerProvider().io())
                    .observeOn(getSchedulerProvider().ui())
                    .subscribe(new Consumer<ekycXMLResponse>() {
                        @Override
                        public void accept(ekycXMLResponse iciciResponse) {
                            Log.e("iciciResponse", iciciResponse + "");

                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            EdsEkycXMLViewModel.this.setIsLoading(false);
                            String error, myerror;

                        }
                    }));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getResponseFromJsonURL1(String xml_pid, HashMap<String, String> webHeader,String url) {
        String server_response = null;
        //  DefaultHttpClient httpClient = null;
        //if (CommonUtility.isNotEmpty(url)) {
        try {
            /************** For getting response from HTTP URL start ***************/
            //    KeyStore keyStore = KeyStore.getInstance("PKCS12");
//            final Resources resources =  mContext.getResources();
//            InputStream in = resources.openRawResource(R.raw.ecomexpress);
            //   FileInputStream   fis = new FileInputStream(certificateFile);
//            HttpParams httpParameters = new BasicHttpParams();
//            httpClient = new DefaultHttpClient(httpParameters);
//            HttpGet httpGet = new HttpGet(Constants.UNDELIVERED_REASON);
//            HttpResponse httpResponse = httpClient.execute(httpGet);
//             outputStream = new ByteArrayOutputHelper.getAuthToken(mContext)Stream();
//            httpResponse.getEntity().writeTo(outputStream);

//            byte[] decoded = Base64.decodeBase64(certificate_pass.getBytes());
//            String output = new String(decoded);
//            URL urlCertificate = new URL(certificate_url + Helper.getAuthToken(mContext));
//            HttpURLConnection conn = (HttpURLConnection) urlCertificate.openConnection();
//            conn.setRequestMethod("GET");
//            // read the response
//            InputStream in = new BufferedInputStream(conn.getInputStream());
//            keyStore.load(in, new String(Base64.decodeBase64(certificate_pass.getBytes())).toCharArray());
//
//            KeyManagerFactory kmf = KeyManagerFactory.getInstance("X509");
//            kmf.init(keyStore, new String(Base64.decodeBase64(certificate_pass.getBytes())).toCharArray());
//            KeyManager[] keyManagers = kmf.getKeyManagers();
//            SSLContext sslContext = SSLContext.getInstance("SSL");
//            sslContext.init(keyManagers, null, null);
//            String authString = username + ":" + password;
//            byte[] authEncBytes = Base64.encodeBase64(authString.getBytes());
//            String authStringEnc = new String(authEncBytes);
//            URIBuilder ub = new URIBuilder(url);
//            ub.addParameter("client_id", client_id);
//            ub.addParameter("client_secret", clientpass);
//            String urlparam = ub.toString();


            URL object = new URL(url);
            //System.out.println("Base64 encoded auth string: " + authStringEnc);
            //String requestParams = "client_id=7fe3a73a-2e28-4bf9-b0e4-9e5ecdaf9a55&client_secret=B2sK6pM2eY1sR8nI0uL6pD6yN2vB8pS0iS6fF5lQ6gP5wF3pL6";
            HttpsURLConnection connection = (HttpsURLConnection) object
                    .openConnection();
//            if (connection instanceof HttpsURLConnection) {
//                ((HttpsURLConnection) connection)
//                        .setSSLSocketFactory(sslContext.getSocketFactory());
//            }
            connection.setRequestMethod("POST");
            connection.setRequestProperty("content-type", "application/xml");
            // connection.setRequestProperty("Authorization", "Basic " + authStringEnc);
            for (Map.Entry<String, String> entry : webHeader.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                connection.setRequestProperty(key, value);
            }
            connection.setDoOutput(true);
            DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
            wr.writeBytes(xml_pid);
            wr.flush();
            wr.close();
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                server_response = readStream(connection.getInputStream());
            } else {
                server_response = connection.getResponseMessage();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return server_response;

        }
        //}
        return server_response;
    }


    public static String readStream(InputStream in) {
        BufferedReader reader = null;
        StringBuffer response = new StringBuffer();
        try {
            reader = new BufferedReader(new InputStreamReader(in));
            String line = "";
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return response.toString();
    }

}
