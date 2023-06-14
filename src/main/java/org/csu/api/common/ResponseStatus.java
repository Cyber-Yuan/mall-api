package org.csu.api.common;

import lombok.Getter;

@Getter
public enum ResponseStatus {

    SUCCESS(0, "成功"),
    FAIL(1, "失败"),
    ARGUMENT_INVALID(10, "参数异常"),
    NEED_LOGIN(11, "用户未登录");

    private final Integer code;
    private final String description;

    ResponseStatus(Integer code, String description) {
        this.code = code;
        this.description = description;
    }

}
