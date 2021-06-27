package com.lagou;

import com.lagou.bean.ComsumerComponet;
import org.apache.dubbo.common.threadpool.support.fixed.FixedThreadPool;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.apache.dubbo.rpc.RpcContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.util.concurrent.*;

public class AnnotationConsumerMain {
    public static void main(String[] args) throws Exception {
        System.out.println("-------------");
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(ConsumerConfiguration.class);
        context.start();
        // 获取消费者组件
        ComsumerComponet service = context.getBean(ComsumerComponet.class);
//        while (true) {
//            System.in.read();
//            String hello = service.sayHello("world");
//            System.out.println("result:" + hello);
//
//        }
        ThreadPoolExecutor poolExecutor = new ThreadPoolExecutor(50, 200,
                1000L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(
                ));
        while(true){
            poolExecutor.submit(()->{
                String hello = service.sayHello("world");
//                System.out.println("Thread : "+ Thread.currentThread().getId()+ " result : " + hello);
            });
        }
    }


    @Configuration
    @PropertySource("classpath:/dubbo-consumer.properties")
    @ComponentScan(basePackages = "com.lagou.bean")
    @EnableDubbo
    static class ConsumerConfiguration {

    }
}
