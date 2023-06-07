package org.csu.api.common;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;

import java.util.Objects;

@Getter
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class CommonResponse <T> {

    private Integer code;
    private T data;
    private String message;

    private CommonResponse(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    private CommonResponse(Integer code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    @JsonIgnore
    public boolean isSuccess() {
        return Objects.equals(this.code, ResponseStatus.SUCCESS.getCode());
    }

    public static <T> CommonResponse<T> createForSuccess() {
        return new CommonResponse<>(ResponseStatus.SUCCESS.getCode(), "SUCCESS");
    }

    public static <T> CommonResponse<T> createForSuccess(T data) {
        return new CommonResponse<>(ResponseStatus.SUCCESS.getCode(), "SUCCESS", data);
    }

    public static <T> CommonResponse<T> createForSuccess(String message, T data) {
        return new CommonResponse<>(ResponseStatus.SUCCESS.getCode(), message, data);
    }

    public static <T> CommonResponse<T> createForErrorMessage(String message) {
        return new CommonResponse<>(ResponseStatus.FAIL.getCode(), message);
    }

    public static <T> CommonResponse<T> createForErrorMessage(int code, String message) {
        return new CommonResponse<>(code, message);
    }

    public static <T> CommonResponse<T> createForArgumentErrorMessage(String message) {
        return new CommonResponse<>(ResponseStatus.ARGUMENT_INVALID.getCode(), message);
    }
}
