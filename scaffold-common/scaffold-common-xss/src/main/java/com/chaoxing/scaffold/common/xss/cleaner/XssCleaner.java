package com.chaoxing.scaffold.common.xss.cleaner;

/**
 * 清理html中的危险字符
 * @author SK
 * @date 2023/5/30
 */
public interface XssCleaner {
    /**
     * 清理有 Xss 风险的文本
     * @param html 原 html
     * @return 清理后的 html
     */
    String clean(String html);
}
