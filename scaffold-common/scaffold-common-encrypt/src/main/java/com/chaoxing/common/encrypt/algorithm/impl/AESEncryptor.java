package com.chaoxing.common.encrypt.algorithm.impl;


import cn.hutool.core.util.HexUtil;
import cn.hutool.crypto.Mode;
import cn.hutool.crypto.Padding;
import cn.hutool.crypto.symmetric.AES;
import com.chaoxing.common.encrypt.algorithm.Algorithm;
import com.chaoxing.common.encrypt.context.EncryptContext;
import com.chaoxing.common.encrypt.context.EncryptContextHolder;

import java.nio.charset.StandardCharsets;

public class AESEncryptor implements Algorithm {
    @Override
    public String encrypt(String value) {
        EncryptContext context = EncryptContextHolder.getContext();
        String secret = context.getSecret();

        AES aes = new AES(Mode.ECB, Padding.PKCS5Padding,secret.getBytes(StandardCharsets.UTF_8));
        return aes.encryptHex(value.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public String decrypt(String value) {
        EncryptContext context = EncryptContextHolder.getContext();
        String secret = context.getSecret();

        AES aes = new AES(Mode.ECB, Padding.PKCS5Padding,secret.getBytes(StandardCharsets.UTF_8));
        return aes.decryptStr(HexUtil.decodeHex(value));
    }
}
