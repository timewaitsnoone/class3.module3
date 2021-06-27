package com.lagou.sercvice.impl;

import com.lagou.service.HelloService;
import org.apache.dubbo.config.annotation.Service;

import java.util.Random;

@Service(filter = "TPMonitor")
public class HelloServiceImpl implements HelloService {
    @Override
    public String sayHello(String name,int timeToWait ) {
        try {
            Thread.sleep(timeToWait);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return "Hello1 "+name;
    }

    @Override
    public String sayHello(String name) {
        try {
            Random random = new Random();
            Thread.sleep(random.nextInt(100));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return "Hello1 random delay : "+name;
    }
}
