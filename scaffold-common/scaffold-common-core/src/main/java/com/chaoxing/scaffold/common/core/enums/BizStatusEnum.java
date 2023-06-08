package com.chaoxing.scaffold.common.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author SK
 * @date 2023/5/9
 */
@Getter
@AllArgsConstructor
public enum BizStatusEnum {
    FAIL(0,"失败"),
    SUCCESS(1,"成功");
    private final Integer code;
    private final String desc;
}
