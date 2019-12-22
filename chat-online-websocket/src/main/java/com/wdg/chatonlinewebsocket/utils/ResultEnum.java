package com.wdg.chatonlinewebsocket.utils;

/**
 * ClassName: ResultEnum
 * Description:
 * date: 2019/11/29 11:42
 *
 * @author WuDG(1490727316)
 * @version 0.1
 * @since JDK 1.8
 */

/**
 * 只要get不要set,进行更好的封装
 */
@SuppressWarnings("all")
public class ResultEnum {

    private int code;
    private String msg;

    //通用的异常
    public static ResultEnum SUCCESS = new ResultEnum(0, "success");

    public static ResultEnum SERVER_ERROR = new ResultEnum(50000, "服务端异常");
    public static ResultEnum PASSWORD_EMPTY = new ResultEnum(50001, "密码不能为空");
    public static ResultEnum MOBILE_EMPTY = new ResultEnum(50002, "手机号不能为空");
    public static ResultEnum MOBILE_ERROR = new ResultEnum(50003, "手机号格式错误");
    public static ResultEnum NO_USER = new ResultEnum(50004, "用户不存在");
    public static ResultEnum PASSWORD_ERROR = new ResultEnum(50005, "密码错误");
    public static ResultEnum NAME_BLANK = new ResultEnum(50005, "名字不能为空");

    public ResultEnum fillArgs(Object... args) {
        int code = this.code;
        String message = String.format(this.msg, args);
        return new ResultEnum(code, message);
    }

    private ResultEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }


    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    //注意需要重写toString 方法,不然到前端页面是一个对象的地址....
    @Override
    public String toString() {
        return "ResultEnum{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                '}';
    }

}