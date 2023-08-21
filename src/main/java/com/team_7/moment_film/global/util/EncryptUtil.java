package com.team_7.moment_film.global.util;

import com.amazonaws.services.kms.AWSKMS;
import com.amazonaws.services.kms.model.DecryptRequest;
import com.amazonaws.services.kms.model.DecryptResult;
import com.amazonaws.services.kms.model.EncryptRequest;
import com.amazonaws.services.kms.model.EncryptResult;
import com.team_7.moment_film.global.config.KmsConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.security.GeneralSecurityException;
import java.util.Base64;

@Slf4j(topic = "EncryptUtil")
@Component
@RequiredArgsConstructor
public class EncryptUtil {
    @Autowired
    private KmsConfig kmsConfig;

    @Value("${aws.kms.keyId}")
    private String keyId;

    // AES256 알고리즘으로 평문 데이터 암호화
    public String encrypt(String plainText) throws GeneralSecurityException, IOException {
        // AWS KMS Client 생성
        AWSKMS kmsClient = kmsConfig.kmsClient();

        // 데이터 암호화
        EncryptRequest encryptRequest = new EncryptRequest();
        encryptRequest.setKeyId(keyId);
        encryptRequest.setPlaintext(ByteBuffer.wrap(plainText.getBytes("UTF-8")));
        EncryptResult encryptResult = kmsClient.encrypt(encryptRequest);
        byte[] cipherText = encryptResult.getCiphertextBlob().array();

        // 암호화된 데이터를 Base64로 인코딩
        String cipherTextBase64 = Base64.getEncoder().encodeToString(cipherText);

        return cipherTextBase64;
    }

    // AES256 알고리즘으로 암호화된 테이터 복호화
    public String decrypt(String cipherTextBase64) throws GeneralSecurityException, IOException {
        // 암호화된 후 인코딩 된 데이터를 Base64로 디코딩
        byte[] cipherText = Base64.getDecoder().decode(cipherTextBase64);

        // 데이터 복호화
        DecryptRequest decryptRequest = new DecryptRequest();
        decryptRequest.setKeyId(keyId);
        decryptRequest.setCiphertextBlob(ByteBuffer.wrap(cipherText));
        DecryptResult decryptResult = kmsConfig.kmsClient().decrypt(decryptRequest);
        byte[] plainText = decryptResult.getPlaintext().array();

        // 복호화 된 데이터(byte 배열)을 String(UTF-8)로 변환
        String plainTextUtf8 = new String(plainText, "UTF-8");

        return plainTextUtf8;
    }
}
