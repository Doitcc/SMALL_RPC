package com.caox.service.Impl;

import com.caox.annotation.ServiceImplementation;
import com.caox.service.ServiceOne;

@ServiceImplementation
public class ServiceOneImpl implements ServiceOne {
    @Override
    public String hello(String data) {
        
        return "成功返回的数据："+data;
    }

    @Override
    public String hi(String data) {
        
        return "hi方法返回的数据"+data;
    }


}
