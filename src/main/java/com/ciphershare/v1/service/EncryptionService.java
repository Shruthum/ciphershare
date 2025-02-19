package com.ciphershare.v1.service;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EncryptionService {

    @Autowired
    private EnvService envService;
    private final String Algo = "AES";
    private String key = envService.getsecretKey();

    public byte[] encrypt(byte[] data) throws Exception {

        byte[] byte_key = key.getBytes();
        SecretKey secretKey = new SecretKeySpec(byte_key, Algo);
        Cipher cipher = Cipher.getInstance(Algo);

        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        return cipher.doFinal(data);
    }

    public byte[] decrypt(byte[] encryptedData) throws Exception {

        SecretKey secretKey = new SecretKeySpec(key.getBytes(), Algo);
        Cipher cipher = Cipher.getInstance(Algo);
        cipher.init(Cipher.DECRYPT_MODE, secretKey);

        return cipher.doFinal(encryptedData);
    }
}
