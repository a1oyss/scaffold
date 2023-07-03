package com.chaoxing.scaffold.common.encrypt.context;

import lombok.Data;

/**
 * @author SK
 * @since 2023/6/7
 */
@Data
public class EncryptContext {
    private String secret;
    private String privateKey;
    private String publicKey;
}
