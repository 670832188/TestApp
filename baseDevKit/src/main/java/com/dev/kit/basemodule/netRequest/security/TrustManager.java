package com.dev.kit.basemodule.netRequest.security;

import android.app.Application;
import android.content.Context;
import androidx.annotation.NonNull;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

/**
 * Author：cuiyan on 16/6/8 17:29
 * Email：670832188@qq.com
 */
public class TrustManager {
    private static Context context;
    private static TrustManager instance;

    public static void init(@NonNull Application application) {
        TrustManager.context = application.getApplicationContext();
    }

    public synchronized static TrustManager getInstance() throws NullPointerException {
        if (instance == null) {
            if (context == null) {
                throw new NullPointerException("TrustManager has not been initialized, please call TrustManager.init(context) method to initialize");
            }
            synchronized (TrustManager.class) {
                if (instance == null) {
                    instance = new TrustManager();
                }
            }
        }
        return instance;
    }

    // 处理自签名证书
    private javax.net.ssl.TrustManager[] getWrappedTrustManagers(javax.net.ssl.TrustManager[] trustManagers) {

        final X509TrustManager originalTrustManager = (X509TrustManager) trustManagers[0];

        return new javax.net.ssl.TrustManager[]{
                new X509TrustManager() {
                    @Override
                    public X509Certificate[] getAcceptedIssuers() {
                        return originalTrustManager.getAcceptedIssuers();
                    }

                    @Override
                    public void checkClientTrusted(X509Certificate[] certs, String authType) {
                        try {
                            originalTrustManager.checkClientTrusted(certs, authType);
                        } catch (CertificateException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void checkServerTrusted(X509Certificate[] certs, String authType) {
                        try {
                            originalTrustManager.checkServerTrusted(certs, authType);
                        } catch (CertificateException e) {
                            e.printStackTrace();
                        }
                    }
                }
        };
    }

    public SSLSocketFactory getSSLSocketFactory(String keyStoreType, int keystoreResId)
            throws CertificateException, KeyStoreException, IOException, NoSuchAlgorithmException, KeyManagementException {
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        InputStream caInput = context.getResources().openRawResource(keystoreResId);
        Certificate ca = cf.generateCertificate(caInput);
        caInput.close();
        if (keyStoreType == null || keyStoreType.length() == 0) {
            keyStoreType = KeyStore.getDefaultType();
        }
        KeyStore keyStore = KeyStore.getInstance(keyStoreType);
        keyStore.load(null, null);
        keyStore.setCertificateEntry("ca", ca);
        String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
        TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
        tmf.init(keyStore);
        javax.net.ssl.TrustManager[] wrappedTrustManagers = getWrappedTrustManagers(tmf.getTrustManagers());
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, wrappedTrustManagers, null);
        return sslContext.getSocketFactory();
    }
}