package com.lagou.filter;

import org.apache.dubbo.common.constants.CommonConstants;
import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.rpc.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Activate(group = {CommonConstants.PROVIDER})
public class TPMonitorFilter implements Filter ,Runnable {
    private static final Logger LOGGER= LoggerFactory.getLogger(TPMonitorFilter.class);
    private final List<long[]> spendTimeList=new ArrayList<>();
    public TPMonitorFilter() {
        Executors.newSingleThreadScheduledExecutor().scheduleWithFixedDelay(this, 1, 5, TimeUnit.SECONDS);
    }
    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        long startTime=System.currentTimeMillis();
        try{
            return invoker.invoke(invocation);
        }
        finally {
            long currentTimeMillis = System.currentTimeMillis();
            Long spendTime=(currentTimeMillis-startTime);
//            System.out.println("invoke time "+spendTime+ "ms");
            spendTimeList.add(new long[]{currentTimeMillis, spendTime});
        }

    }

    @Override
    public void run() {
        int allRequestTme=0;
        long tp90=-1;
        long tp99=-1;
        List<Long> lastMin=new ArrayList<>();
        if(spendTimeList.isEmpty()) {
            return;
        }
        synchronized(this) {
            for(int i=spendTimeList.size()-1;i>=0;i--){
                if(spendTimeList.get(i)[0]<System.currentTimeMillis()-60*1000){
                    break;
                }
                lastMin.add(spendTimeList.get(i)[1]);
            }
            Collections.sort(lastMin);
            if(!lastMin.isEmpty()){
                tp90=lastMin.get((int) (lastMin.size()*0.9));
                tp99=lastMin.get((int) (lastMin.size()*0.99-1));
            }

            LOGGER.info("===>>>累计调用 : {} 次. 最近一分钟调用 : {} 次. 最近一分钟TP90 : {} .最近一分钟TP99 : {}",spendTimeList.size(),lastMin.size(),tp90,tp99);
        }

    }
}
