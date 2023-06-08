package com.chaoxing.scaffold.common.core.entity;

import com.chaoxing.scaffold.common.core.enums.ResultEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(title = "统一返回体")
public class Result<T> {
    @Schema(title = "状态码")
    private Integer code;
    @Schema(title = "消息")
    private String msg;
    @Schema(title = "返回数据")
    private T data;

    public Result(Integer code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public static <T> Result<T> success() {
        return success(null);
    }

    public static <T> Result<T> success(T data) {
        return success(ResultEnum.SUCCESS.getMsg(), data);
    }

    public static <T> Result<T> success(String msg, T data) {
        return new Result<>(ResultEnum.SUCCESS.getCode(), msg, data);
    }

    public static <T> Result<T> failed() {
        return failed(ResultEnum.SERVER_ERROR.getMsg());
    }

    public static <T> Result<T> failed(String msg) {
        return failed(ResultEnum.SERVER_ERROR, null);
    }

    public static <T> Result<T> failed(ResultEnum resultEnum, String msg) {
        return new Result<>(resultEnum.getCode(), msg, null);
    }

    public static <T> Result<T> unauthorized(T data) {
        return new Result<>(ResultEnum.UNAUTHORIZED.getCode(), "未登录或登陆信息已过期", data);
    }
}
