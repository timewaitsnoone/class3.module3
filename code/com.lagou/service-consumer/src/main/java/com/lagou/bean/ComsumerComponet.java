package com.lagou.bean;

import com.lagou.service.HelloService;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.stereotype.Component;

@Component
public class ComsumerComponet {
    @Reference(loadbalance = "roundrobin",async = false,mock = "force:return null")//roundrobin onlyFirst
    private HelloService  helloService;
    public String  sayHello(String name){
        return  helloService.sayHello(name);
    }

//    @Override
//    public void run() {
//        helloService.sayHello("Hello ");
//    }
}
