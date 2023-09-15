package com.caox.message;

public enum ResponseEnum {
    SUCCESS(200, "请求成功"),
    FAIL(500, "请求失败");
    
    private int statusCode;
    private String msg;
    
    ResponseEnum(int statusCode, String msg) {
        this.statusCode = statusCode;
        this.msg = msg;
    }

    public int getStatusCode() {
        return statusCode;
    }
    
    public String getMsg() {
        return msg;
    }
}
