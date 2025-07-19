package in.ecomexpress.sathi.ui.dummy.eds.edsantwork;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.databinding.ObservableField;

import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.util.Arrays;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import dagger.hilt.android.lifecycle.HiltViewModel;
import in.ecomexpress.sathi.R;
import in.ecomexpress.sathi.repo.IDataManager;
import in.ecomexpress.sathi.repo.remote.model.EncryptContactResponse;
import in.ecomexpress.sathi.repo.remote.model.GenerateTokenNiyo;
import in.ecomexpress.sathi.repo.remote.model.antwork.BioMatricResponse;
import in.ecomexpress.sathi.repo.remote.model.antwork.BiomatricRequest;
import in.ecomexpress.sathi.repo.remote.model.antwork.WadhResponse;
import in.ecomexpress.sathi.repo.remote.model.antwork.WathRequest;
import in.ecomexpress.sathi.repo.remote.model.drs_list.edsnew.EDSActivityWizard;
import in.ecomexpress.sathi.repo.remote.model.masterdata.MasterActivityData;
import in.ecomexpress.sathi.ui.base.BaseViewModel;
import in.ecomexpress.sathi.ui.dummy.eds.ekyc_freyo.IEdsEkycFreyoFragmentNavigator;
import in.ecomexpress.sathi.utils.rx.ISchedulerProvider;
import io.reactivex.functions.Consumer;
import okhttp3.OkHttpClient;

@HiltViewModel
public class EdsEkycAntWorkViewModel extends BaseViewModel<IEdsEkycAntWorkFragmentNavigator> {
    public ObservableField<MasterActivityData> masterActivityData = new ObservableField<>();
    public ObservableField<EDSActivityWizard> edsActivityWizard = new ObservableField<>();

    @Inject
    public EdsEkycAntWorkViewModel(IDataManager dataManager, ISchedulerProvider schedulerProvider, Application app){
        super(dataManager, schedulerProvider, app);
    }

    public void onActivateSensor(){
        getNavigator().onActivateSensor();
    }

    public void setData(EDSActivityWizard edsActivityWizard, MasterActivityData masterActivityData){
        this.edsActivityWizard.set(edsActivityWizard);
        this.masterActivityData.set(masterActivityData);
    }

    public void checkStatus(){
        //  showLoader();
        setIsLoading(true);
        try{
            final long timeStamp = Calendar.getInstance().getTimeInMillis();
            getCompositeDisposable().add(getDataManager().dogenerateniyotoken().doOnSuccess(new Consumer<GenerateTokenNiyo>() {
                @Override
                public void accept(GenerateTokenNiyo contatDecryption){
                    writeRestAPIResponse(timeStamp, contatDecryption);
                }
            }).subscribeOn(getSchedulerProvider().io()).observeOn(getSchedulerProvider().ui()).subscribe(new Consumer<GenerateTokenNiyo>() {
                @Override
                public void accept(GenerateTokenNiyo iciciResponse){
                    setIsLoading(false);
                    getNavigator().getToken(iciciResponse);
                    // getNavigator().sendICICICheckStatusResponse(iciciResponse);
                }
            }, new Consumer<Throwable>() {
                @Override
                public void accept(Throwable throwable) throws Exception{
                    setIsLoading(false);
                    // hideLoader();
                    String error, myerror;
                }
            }));
        } catch(Exception e){
            // hideLoader();
            setIsLoading(false);
            e.printStackTrace();
        }
    }

    public void getWadhValue(String basic, String token_url , WathRequest wathRequest){
        try{
            getCompositeDisposable().add(getDataManager().doGetWadhValueAntWork(basic,getDataManager().getEcomRegion(),token_url , wathRequest).doOnSuccess(new Consumer<WadhResponse>() {
                @Override
                public void accept(WadhResponse contatDecryption){
                    Log.e("resopns" ,contatDecryption+"");
                }
            }).subscribeOn(getSchedulerProvider().io()).observeOn(getSchedulerProvider().ui()).subscribe(new Consumer<WadhResponse>() {
                @Override
                public void accept(WadhResponse wadhResponse){
                    getNavigator().sendBiomatricData(wadhResponse);
                }
            }, new Consumer<Throwable>() {
                @Override
                public void accept(Throwable throwable) throws Exception{
                    setIsLoading(false);

                }
            }));
        } catch(Exception e){
            e.printStackTrace();
        }
    }


    public void sendBiomaticData(String basic, String token_url , BiomatricRequest biomatricRequest){
        try{
            getCompositeDisposable().add(getDataManager().doGetBioMatricData(basic,getDataManager().getEcomRegion(),token_url , biomatricRequest).doOnSuccess(new Consumer<BioMatricResponse>() {
                @Override
                public void accept(BioMatricResponse bioMatricResponse){
                    Log.e("resopns" ,bioMatricResponse+"");
                }
            }).subscribeOn(getSchedulerProvider().io()).observeOn(getSchedulerProvider().ui())
                    .subscribe(new Consumer<BioMatricResponse>() {
                @Override
                public void accept(BioMatricResponse bioMatricResponse){
                    getNavigator().bioMatricResult(bioMatricResponse);
                }
            }, new Consumer<Throwable>() {
                @Override
                public void accept(Throwable throwable) throws Exception{
                    setIsLoading(false);
                }
            }));
        } catch(Exception e){
            e.printStackTrace();
        }
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
    //    private static SSLContext createCertificate(InputStream trustedCertificateIS) throws CertificateException, IOException, KeyStoreException, KeyManagementException, NoSuchAlgorithmException {
    //
    //        CertificateFactory cf = CertificateFactory.getInstance("X.509");
    //        Certificate ca;
    //        try {
    //            ca = cf.generateCertificate(trustedCertificateIS);
    //        } finally {
    //            trustedCertificateIS.close();
    //        }
    //
    //        // creating a KeyStore containing our trusted CAs
    //        String keyStoreType = KeyStore.getDefaultType();
    //        KeyStore keyStore = KeyStore.getInstance(keyStoreType);
    //        keyStore.load(null, null);
    //        keyStore.setCertificateEntry("ca", ca);
    //
    //        // creating a TrustManager that trusts the CAs in our KeyStore
    //        String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
    //        TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
    //        tmf.init(keyStore);
    //
    //        // creating an SSLSocketFactory that uses our TrustManager
    //        SSLContext sslContext = SSLContext.getInstance("TLS");
    //        sslContext.init(null, tmf.getTrustManagers(), null);
    //        return sslContext;
    //
    //    }
    // my code starts from here

    private static void initSSL(Context context){
        SSLContext sslContext = null;
        try{
            sslContext = createCertificate(context.getResources().openRawResource(R.raw.ecomdemo));
        } catch(CertificateException | IOException | KeyStoreException | KeyManagementException | NoSuchAlgorithmException e){
            e.printStackTrace();
        }
        if(sslContext != null){
            OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder().readTimeout(5, TimeUnit.SECONDS);
            httpClientBuilder.sslSocketFactory(sslContext.getSocketFactory(), systemDefaultTrustManager());
        }
    }

    private static SSLContext createCertificate(InputStream trustedCertificateIS) throws CertificateException, IOException, KeyStoreException, KeyManagementException, NoSuchAlgorithmException{
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        Certificate ca;
        try{
            ca = cf.generateCertificate(trustedCertificateIS);
        } finally{
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

    private static X509TrustManager systemDefaultTrustManager(){
        try{
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init((KeyStore) null);
            TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();
            if(trustManagers.length != 1 || !(trustManagers[0] instanceof X509TrustManager)){
                throw new IllegalStateException("Unexpected default trust managers:" + Arrays.toString(trustManagers));
            }
            return (X509TrustManager) trustManagers[0];
        } catch(GeneralSecurityException e){
            throw new AssertionError(); // The system has no TLS. Just give up.
        }
    }
}
