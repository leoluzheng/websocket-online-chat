package com.wdg.chatonlinewebsocket.utils;

/**
 * ClassName: Result
 * Description:
 * date: 2019/11/29 11:42
 *
 * @author WuDG(1490727316)
 * @version 0.1
 * @since JDK 1.8
 */

import lombok.Data;

/**
 * 输出结果的封装
 * 只要get不要set,进行更好的封装
 *
 * @param <T>
 */
@SuppressWarnings("all")
@Data
public class Result<T> {
    private int code;
    private String msg;
    private T data;

    private Result(T data) {
        this.code = 200;
        this.msg = "success";
        this.data = data;
    }

    private Result(ResultEnum mg) {
        if (mg == null) {
            return;
        }
        this.code = mg.getCode();
        this.msg = mg.getMsg();
    }

    /**
     * 成功时
     *
     * @param <T>
     * @return
     */
    public static <T> Result<T> success(T data) {
        return new Result<T>(data);
    }

    /**
     * 失败
     *
     * @param <T>
     * @return
     */
    public static <T> Result<T> fail(ResultEnum mg) {
        return new Result<T>(mg);
    }
}