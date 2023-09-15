package com.caox.service.Impl;

import com.caox.annotation.ServiceImplementation;
import com.caox.pojo.Student;
import com.caox.service.ServiceTwo;

@ServiceImplementation
public class ServiceTwoImpl implements ServiceTwo {
    @Override
    public Student write(Student student) {
        
        return student;
    }
}
