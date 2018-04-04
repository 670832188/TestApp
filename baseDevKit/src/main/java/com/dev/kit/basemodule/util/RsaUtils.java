package com.dev.kit.basemodule.util;

import android.text.TextUtils;
import android.util.Base64;

import com.dev.kit.basemodule.netRequest.Configs.Config;

import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.Signature;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;

/**
 * rsa工具类
 * Created by cuiyan on 2015/7/22.
 */

/**
 * 处理RSA加密/解密，签名/验签
 * 每次加密的字节数不能超过(密钥字节数 - 8)
 */
public final class RsaUtils {
    private static String RSA = "RSA";


    /**
     * 随机获得密钥对
     */
    public static KeyPair generateRSAKeyPair(int keyLength) {
        try {
            KeyPairGenerator kpg = KeyPairGenerator.getInstance(RSA);
            kpg.initialize(keyLength);
            return kpg.genKeyPair();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 用公钥加密
     *
     * @param data      待加密数据
     * @param publicKey 公钥
     * @return 加密后的byte型数据
     */
    public static byte[] encryptByPublicKey(byte[] data, RSAPublicKey publicKey) throws Exception {
        Cipher cipher = Cipher.getInstance(RSA);
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        return cipher.doFinal(data);
    }

    /**
     * 用私钥解密
     *
     * @param encryptedData 加密的byte数据
     * @param privateKey    私钥
     */
    public static byte[] decryptByPrivateKey(byte[] encryptedData, RSAPrivateKey privateKey) throws Exception {
        Cipher cipher = Cipher.getInstance(RSA);
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        return cipher.doFinal(encryptedData);
    }

    /**
     * 用私钥加密
     *
     * @param data       待加密数据
     * @param privateKey 私钥
     */
    public static byte[] encryptByPrivateKey(byte[] data, RSAPrivateKey privateKey) throws Exception {

        Cipher cipher = Cipher.getInstance(RSA);
        cipher.init(Cipher.ENCRYPT_MODE, privateKey);
        return cipher.doFinal(data);
    }

    /**
     * 用公钥解密
     *
     * @param encryptedData 加密的byte数据
     * @param publicKey     公钥
     */
    public static byte[] decryptByPublicKey(byte[] encryptedData, RSAPublicKey publicKey) throws Exception {
        Cipher cipher = Cipher.getInstance(RSA);
        cipher.init(Cipher.DECRYPT_MODE, publicKey);
        return cipher.doFinal(encryptedData);
    }

    /**
     * 从字符串中加载公钥
     *
     * @throws Exception 加载公钥时产生的异常
     */
    public static RSAPublicKey loadPublicKey(String publicKeyString) throws Exception {
        byte[] buffer = Base64.decode(publicKeyString, Base64.NO_WRAP);
        KeyFactory keyFactory = KeyFactory.getInstance(RSA);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(buffer);
        return (RSAPublicKey) keyFactory.generatePublic(keySpec);
    }

    /**
     * 从字符串中加载私钥
     * 加载时使用的是PKCS8EncodedKeySpec（PKCS#8编码的Key指令）。
     */
    public static RSAPrivateKey loadPrivateKey(String privateKeyStr) throws Exception {
        byte[] buffer = Base64.decode(privateKeyStr, Base64.NO_WRAP);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(buffer);
        KeyFactory keyFactory = KeyFactory.getInstance(RSA);
        return (RSAPrivateKey) keyFactory.generatePrivate(keySpec);
    }


    /**
     * 用私钥对信息生成数字签名
     *
     * @param signContent 待签名数据
     * @param privateKey  //私钥
     * @return Base64加密字符串
     */
    public static String sign(String signContent, RSAPrivateKey privateKey, String charset) throws Exception {
        //用私钥对信息生成数字签名
        Signature signature = Signature.getInstance(Config.SIGN_ALGORITHMS);
        signature.initSign(privateKey);
        if (!TextUtils.isEmpty(charset)) {
            signature.update(signContent.getBytes(charset));
        } else {
            signature.update(signContent.getBytes());
        }
        return Base64.encodeToString(signature.sign(), Base64.NO_WRAP);
    }

    /**
     * 用私钥对信息生成数字签名
     *
     * @param signContent   待签名数据
     * @param privateKeyStr 私钥字符串
     * @return Base64加密字符串
     */
    public static String sign(String signContent, String privateKeyStr, String charset) throws Exception {
        //用私钥对信息生成数字签名
        RSAPrivateKey privateKey = loadPrivateKey(privateKeyStr);
        Signature signature = Signature.getInstance(Config.SIGN_ALGORITHMS);
        signature.initSign(privateKey);
        if (!TextUtils.isEmpty(charset)) {
            signature.update(signContent.getBytes(charset));
        } else {
            signature.update(signContent.getBytes());
        }
        return Base64.encodeToString(signature.sign(), Base64.NO_WRAP);
    }

    /**
     * 公钥校验数字签名
     *
     * @param signContent 待签名的字符串
     * @param publicKey   公钥
     * @param signedStr   Base64加密后的数字签名
     */
    public static boolean verify(String signContent, RSAPublicKey publicKey, String signedStr, String charset) throws Exception {
        Signature signature = Signature.getInstance(Config.SIGN_ALGORITHMS);
        signature.initVerify(publicKey);
        if (!TextUtils.isEmpty(charset)) {
            signature.update(signContent.getBytes(charset));
        } else {
            signature.update(signContent.getBytes());
        }
        return signature.verify(Base64.decode(signedStr, Base64.NO_WRAP));
    }

    /**
     * 公钥校验数字签名
     *
     * @param signContent  待签名字符串
     * @param publicKeyStr 公钥字符串
     * @param signedStr    Base64加密后的数字签名
     */
    public static boolean verify(String signContent, String publicKeyStr, String signedStr, String charset) throws Exception {
        RSAPublicKey publicKey = loadPublicKey(publicKeyStr);
        Signature signature = Signature.getInstance(Config.SIGN_ALGORITHMS);
        signature.initVerify(publicKey);
        if (!TextUtils.isEmpty(charset)) {
            signature.update(signContent.getBytes(charset));
        } else {
            signature.update(signContent.getBytes());
        }
        return signature.verify(Base64.decode(signedStr, Base64.NO_WRAP));
    }
}
