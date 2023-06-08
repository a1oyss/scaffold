package com.chaoxing.common.encrypt.context;

import org.springframework.util.Assert;

/**
 * @author SK
 * @since 2023/6/7
 */
public class EncryptContextHolder {
    private static final ThreadLocal<EncryptContext> contextHolder = new ThreadLocal<>();

    public static void clearContext() {
        contextHolder.remove();
    }

    public static EncryptContext getContext() {
        EncryptContext ctx = contextHolder.get();
        if (ctx == null) {
            ctx = createEmptyContext();
            contextHolder.set(ctx);
        }
        return ctx;
    }

    public static void setContext(EncryptContext context) {
        Assert.notNull(context, "Only non-null SecurityContext instances are permitted");
        contextHolder.set(context);
    }

    public static EncryptContext createEmptyContext() {
        return new EncryptContext();
    }
}
