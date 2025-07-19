package in.ecomexpress.sathi.ui.dummy.eds.eds_ekyc_niyo;

import androidx.databinding.ObservableField;

import android.app.Application;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.util.Calendar;

import javax.inject.Inject;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

import cz.msebera.android.httpclient.HttpException;
import dagger.hilt.android.lifecycle.HiltViewModel;
import in.ecomexpress.sathi.SathiApplication;
import in.ecomexpress.sathi.repo.IDataManager;
import in.ecomexpress.sathi.repo.remote.model.EncryptContactResponse;
import in.ecomexpress.sathi.repo.remote.model.GenerateTokenNiyo;
import in.ecomexpress.sathi.repo.remote.model.drs_list.edsnew.EDSActivityWizard;
import in.ecomexpress.sathi.repo.remote.model.masterdata.MasterActivityData;
import in.ecomexpress.sathi.ui.base.BaseViewModel;
import in.ecomexpress.sathi.utils.rx.ISchedulerProvider;
import io.reactivex.functions.Consumer;

@HiltViewModel
public class EdsEkycNiyoViewModel extends BaseViewModel<IEdsEkycNiyoFragmentNavigator> {
    public ObservableField<MasterActivityData> masterActivityData = new ObservableField<>();
    public ObservableField<EDSActivityWizard> edsActivityWizard = new ObservableField<>();

    @Inject
    public EdsEkycNiyoViewModel(IDataManager dataManager, ISchedulerProvider schedulerProvider, Application app) {
        super(dataManager, schedulerProvider,app);
    }
    public void ongetPid() {
        getNavigator().ongetPid();
    }
    public void setData(EDSActivityWizard edsActivityWizard, MasterActivityData masterActivityData) {
        this.edsActivityWizard.set(edsActivityWizard);
        this.masterActivityData.set(masterActivityData);
    }
    public void checkStatus( ) {
        //  showLoader();
        setIsLoading(true);
        try {

            final long timeStamp = Calendar.getInstance().getTimeInMillis();
            // IciciRequest iciciRequest = new IciciRequest(packet);
            //  Gson gson = new Gson();
            // IciciRequest iciciRequest = gson.fromJson(packet ,IciciRequest.class);
            // writeRestAPIRequst(timeStamp, packet);
            getCompositeDisposable().add(getDataManager()
                    .dogenerateniyotoken()
                    .doOnSuccess(new Consumer<GenerateTokenNiyo>() {
                        @Override
                        public void accept(GenerateTokenNiyo contatDecryption) {
                            writeRestAPIResponse(timeStamp, contatDecryption);
                            // Log.d("ICICI",packet);
                            //  Log.d("ICICI",urn);
                            //  Log.d("ICICI", iciciResponse.getStatusCode());
                            //  Log.d("ICICI", String.valueOf(iciciResponse.success));

                            Log.d("ICICI",  contatDecryption.getExpiry());
                            // hideLoader();


                        }
                    })
                    .subscribeOn(getSchedulerProvider().io())
                    .observeOn(getSchedulerProvider().ui())
                    .subscribe(new Consumer<GenerateTokenNiyo>() {
                        @Override
                        public void accept(GenerateTokenNiyo iciciResponse) {
                            setIsLoading(false);
                            getNavigator().getToken(iciciResponse);
                            // getNavigator().sendICICICheckStatusResponse(iciciResponse);
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            setIsLoading(false);
                            // hideLoader();
                            String error, myerror;

                        }
                    }));
        } catch (Exception e) {
            // hideLoader();
            setIsLoading(false);
            e.printStackTrace();
        }
    }
    public void getmobile_no(long awb ) {

        //  showLoader();
        // setIsLoading(true);
        try {

            final long timeStamp = Calendar.getInstance().getTimeInMillis();
            // IciciRequest iciciRequest = new IciciRequest(packet);
            //  Gson gson = new Gson();
            // IciciRequest iciciRequest = gson.fromJson(packet ,IciciRequest.class);
            // writeRestAPIRequst(timeStamp, packet);
            getCompositeDisposable().add(getDataManager()
                    .doencryptcontact(getDataManager().getAuthToken(),getDataManager().getEcomRegion(),awb)
                    .doOnSuccess(new Consumer<EncryptContactResponse>() {
                        @Override
                        public void accept(EncryptContactResponse contatDecryption) {
                            writeRestAPIResponse(timeStamp, contatDecryption);


                            Log.d("ICICIM", String.valueOf(contatDecryption.isStatus()));
                            // hideLoader();


                        }
                    })
                    .subscribeOn(getSchedulerProvider().io())
                    .observeOn(getSchedulerProvider().ui())
                    .subscribe(new Consumer<EncryptContactResponse>() {
                        @Override
                        public void accept(EncryptContactResponse encryptContactResponse) {
                            Log.d("ICICIM", String.valueOf(encryptContactResponse.isStatus()));
                            getNavigator().getMobile(encryptContactResponse);

                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            setIsLoading(false);
                            //  hideLoader();
                            Log.d("ICICI", "ERRRRRRRR");
                            //getNavigator().getMobile(((HttpException) throwable).code());
                            String error, myerror;


                        }
                    }));
        } catch (Exception e) {
            // hideLoader();edsActivityWizard
            e.printStackTrace();
        }
        // return mobile;
    }


//    public Response uploadBiometricData(RequestBody body, Context ctx,GenerateTokenNiyo res) {
//
//        try {
//        TrustManagerFactory trustManagerFactory = null;
//
//            trustManagerFactory = TrustManagerFactory.getInstance(
//                    TrustManagerFactory.getDefaultAlgorithm());
//
//        trustManagerFactory.init((KeyStore) null);
//        TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();
//        if (trustManagers.length != 1 || !(trustManagers[0] instanceof X509TrustManager)) {
//            throw new IllegalStateException("Unexpected default trust managers:"
//                    + Arrays.toString(trustManagers));
//        }
//        X509TrustManager trustManager = (X509TrustManager) trustManagers[0];
//        SSLContext sslContext = createCertificate(ctx.getResources().openRawResource(R.raw.cert1));
//        //SSLContext sslContext = SSLContext.getInstance("TLS");
//        sslContext.init(null, new TrustManager[] { trustManager }, null);
//        SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
//        OkHttpClient okHttpClient = new OkHttpClient().newBuilder().sslSocketFactory(sslSocketFactory, trustManager).addInterceptor(new PayloadIdfcEncryptNetworkInterceptor()).build();
//        //initSSL(context);
//        String url = "https://idfc-gateway.goniyo.com/global-sundry/v1/ecom/validate_ekyc";
//        Request okHttpRequest = new Request.Builder().url(url).cacheControl(CacheControl.FORCE_NETWORK).post(body).build();
//
//        Response response = null;
//        //                             String ss=new PayloadIdfcEncryptNetworkInterceptor().getResponseFromicicioap(request_payload,getActivity());
////
//           PayloadIdfcEncryptNetworkInterceptor pdc=new PayloadIdfcEncryptNetworkInterceptor();
//            PayloadEncPublicKey payloadEncPublicKey =  new PayloadEncPublicKey();
//            payloadEncPublicKey.setKey(res.getKey());
//            payloadEncPublicKey.setId(res.getId());
//            pdc.doSomething(payloadEncPublicKey);
//            response = okHttpClient.newCall(okHttpRequest).execute();
//            if (response.isSuccessful()) {
//                Response.Builder newResponse = response.newBuilder();
//                String contentType = response.header("Content-Type");
//                if (TextUtils.isEmpty(contentType)) contentType = "application/json";
////            InputStream cryptedStream = response.body().byteStream();
//                String responseStr = response.body().string();
//            }
////
////                        Log.d("OKHTTPPPP", ss);
//        System.out.println("Response code: " + response.code());
//
//
//
//        //  showLoader();
//        // setIsLoading(true);
////        try {
////
////            final long timeStamp = Calendar.getInstance().getTimeInMillis();
////            // IciciRequest iciciRequest = new IciciRequest(packet);
////            //  Gson gson = new Gson();
////            // IciciRequest iciciRequest = gson.fromJson(packet ,IciciRequest.class);
////            // writeRestAPIRequst(timeStamp, packet);
////            getCompositeDisposable().add(getDataManager()
////                    .doencryptcontact(getDataManager().getAuthToken(),awb)
////                    .doOnSuccess(new Consumer<EncryptContactResponse>() {
////                        @Override
////                        public void accept(EncryptContactResponse contatDecryption) {
////                            writeRestAPIResponse(timeStamp, contatDecryption);
////
////
////                            Log.d("ICICIM", String.valueOf(contatDecryption.isStatus()));
////                            // hideLoader();
////
////
////                        }
////                    })
////                    .subscribeOn(getSchedulerProvider().io())
////                    .observeOn(getSchedulerProvider().ui())
////                    .subscribe(new Consumer<EncryptContactResponse>() {
////                        @Override
////                        public void accept(EncryptContactResponse encryptContactResponse) {
////                            Log.d("ICICIM", String.valueOf(encryptContactResponse.isStatus()));
////                            getNavigator().getMobile(encryptContactResponse);
////
////                        }
////                    }, new Consumer<Throwable>() {
////                        @Override
////                        public void accept(Throwable throwable) throws Exception {
////                            setIsLoading(false);
////                            //  hideLoader();
////                            Log.d("ICICI", "ERRRRRRRR");
////                            String error, myerror;
////
////
////                        }
////                    }));
////        } catch (Exception e) {
////            // hideLoader();edsActivityWizard
////            e.printStackTrace();
////        }
//        // return mobile;
//        return response;
//        } catch (NoSuchAlgorithmException | KeyManagementException | KeyStoreException | CertificateException | IOException e) {
//            e.printStackTrace();
//        }
//        return null;
//    }

    private static SSLContext createCertificate(InputStream trustedCertificateIS) throws CertificateException, IOException, KeyStoreException, KeyManagementException, NoSuchAlgorithmException {

        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        Certificate ca;
        try {
            ca = cf.generateCertificate(trustedCertificateIS);
        } finally {
            trustedCertificateIS.close();
        }

        // creating a KeyStore containing our trusted CAs
        String keyStoreType = KeyStore.getDefaultType();
        KeyStore keyStore = KeyStore.getInstance(keyStoreType);
        keyStore.load(null, null);
        keyStore.setCertificateEntry("ca", ca);

        // creating a TrustManager that trusts the CAs in our KeyStore
        String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
        TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
        tmf.init(keyStore);

        // creating an SSLSocketFactory that uses our TrustManager
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, tmf.getTrustManagers(), null);
        return sslContext;

    }

}
