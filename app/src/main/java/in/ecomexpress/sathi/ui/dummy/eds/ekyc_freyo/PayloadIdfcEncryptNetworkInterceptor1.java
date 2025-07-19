package in.ecomexpress.sathi.ui.dummy.eds.ekyc_freyo;

import android.util.Base64;
import android.util.Log;

import java.io.IOException;
import java.security.GeneralSecurityException;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSink;
import okio.Okio;

public class    PayloadIdfcEncryptNetworkInterceptor1 implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        // PayloadEncPublicKey payloadEncPublicKey = PayloadIdfcEncKeyManager.getInstance(NiyobankingLauncher.getInstance().application()).getPayloadEncPublicKey();
        PayloadEncPublicKey payloadEncPublicKey = new PayloadEncPublicKey();// PayloadIdfcEncKeyManager.getInstance(NiyobankingLauncher.getInstance().application()).getPayloadEncPublicKey();
        payloadEncPublicKey.setId("8e9ec305-7027-4509-8e75-de53860383d6");
        payloadEncPublicKey.setKey("-----BEGIN PUBLIC KEY-----MIIBIzANBgkqhkiG9w0BAQEFAAOCARAAMIIBCwKCAQIAtWm+xOAvpy7JE+VRMXfuKgn2Wfkro+NIrEHKWVIZoS9q6HHUYCff5Sr/cWOIvGXla7m9mYgyo2zF6lud3vYYnrA3H/xoapeiNqqbt4O/YlmZb9oRrLOzgjTw94QyQdJPKvtNCaynJuqZsf3GQ8DUYNofFJn2vhtQyuWbEx0TQrHMK8YmwUaQvinxopTV7739uVjqkAuFUeUjWt1z4x9eA2v7p/p80jI2eWlHgY7mcp5xTI8pHtRh1JrJI7hiiJxmySBQS18PtNxyK38keFs3JQ7AkM7uuEk44oai9i+lgFzCm7ZjUCBPec547AOFNJr9rKmABbHTXa4UVQPuhPQ3avUCAwEAAQ==-----END PUBLIC KEY-----");
        Response proceed = null;
        if (payloadEncPublicKey == null) {
            return chain.proceed(chain.request());
        }

        Request original = chain.request();

        if (original.body() != null && original.body().contentLength() > 50000) {
            // Disable Enc above 50KB of request body
            return chain.proceed(chain.request());
        }

        if (original.url().toString().contains("v2/keys")) {
            return chain.proceed(chain.request());
        }

        try {
            byte[] randomSymmetricKey = CryptoUtils.generateKey(256);
            byte[] iv = CryptoUtils.generateIv();
            //Request request=new Request.Builder()
            Request.Builder builder = original.newBuilder()
                    .addHeader("x-correlation-id", "testecom")
                    .addHeader("x-api-key", "w7pd2xy7WwRdh381Lssm3StDhrU8DlBf")
                    .addHeader("Content-Type", "application/json")
                    //.addHeader("connection","close")
                    .method(original.method(), original.body());
            Request modifiedRequest = builder.build();

//                Request.Builder builder = original.newBuilder()
//                        .header("x-key-id", payloadEncPublicKey.id)
//                        .header("x-custom-iv", new String(Base64.encode(iv, Base64.NO_WRAP), "UTF-8"))
//                        .header("x-custom-key", CryptoUtils.encryptRSA(payloadEncPublicKey.getKey(), randomSymmetricKey))
//                        .header("x-crypto-version", "v2")
//                        .method(original.method(), original.body() != null ? encryptAES(original.body(), randomSymmetricKey, iv) : null);
            //  Request modifiedRequest = builder.build();
              proceed = chain.proceed(modifiedRequest);
            String ss = proceed.body().string();
            Log.d("INTER_NIYO", ss);
            return proceed;

        } catch (Exception e) {
            e.printStackTrace();
            Log.e("PayloadIntercepter", e.getMessage() == null ? "ApiModule Encryption Error" : e.getMessage());
        }
        return proceed;
    }

    private RequestBody encryptAES(final RequestBody body, final byte[] key, final byte[] iv) {
        return new RequestBody() {
            @Override
            public MediaType contentType() {
                return body.contentType();
            }

            @Override
            public long contentLength() {
                return -1; // We don't know the compressed length in advance!
            }

            @Override
            public void writeTo(BufferedSink sink) throws IOException {
                try {

                    Buffer bufferedSink = new Buffer();
                    body.writeTo(bufferedSink);
                    byte[] bodyBytes = bufferedSink.readByteArray();

                    byte[] bytes = CryptoUtils.encryptAES(key, bodyBytes, iv);
                    String encryptedData = new String(Base64.encode(bytes, Base64.NO_WRAP), "UTF-8");
                    BufferedSink gzipSink = Okio.buffer(sink);
                    gzipSink.writeUtf8(encryptedData);
                    gzipSink.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
    }

    private ResponseBody decryptAES(final ResponseBody body, final byte[] key, final byte[] iv) {
        if (body == null) {
            return body;
        }
        try {
            byte[] bytes = body.bytes();
            if (bytes.length <= 0) {
                return body;
            }

            byte[] decryptAesBytes = CryptoUtils.decryptAes(key, Base64.decode(bytes, Base64.NO_WRAP), iv);
            String decrypt = new String(decryptAesBytes, "UTF-8");
            Buffer bufferedSink = new Buffer();
            bufferedSink.write(decrypt.getBytes());
            return ResponseBody.create(body.contentType(), body.contentLength(), bufferedSink);
        } catch (IOException | GeneralSecurityException e) {
            e.printStackTrace();
        }
        return null;
    }

}