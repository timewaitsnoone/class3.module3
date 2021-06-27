package com.lagou.loadbalance;

import org.apache.dubbo.common.URL;
import org.apache.dubbo.rpc.Invocation;
import org.apache.dubbo.rpc.Invoker;
import org.apache.dubbo.rpc.RpcException;
import org.apache.dubbo.rpc.cluster.LoadBalance;

import java.util.List;

public class OnlyFirstLoadBalancer implements LoadBalance {
    @Override
    public <T> Invoker<T> select(List<Invoker<T>> list, URL url, Invocation invocation) throws RpcException {
        return list.stream().sorted((l1, l2) -> {
            final int ipCompare = l1.getUrl().getIp().compareTo(l2.getUrl().getIp());
            if (ipCompare == 0) {
                return Integer.compare(l1.getUrl().getPort(), l2.getUrl().getPort());
            } else {
                return ipCompare;
            }
        }).findFirst().get();
    }
}
