package com.chaoxing.scaffold.common.encrypt.algorithm;

/**
 * @author SK
 * @since 2023/6/7
 */
public interface Algorithm {
    String encrypt(String value);

    String decrypt(String value);
}
