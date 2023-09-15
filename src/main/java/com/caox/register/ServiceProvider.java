package com.caox.register;

public interface ServiceProvider {
    void addService(String serviceName,String serviceinterfaceName);
    
    String getService(String serviceinterfaceName);
}
