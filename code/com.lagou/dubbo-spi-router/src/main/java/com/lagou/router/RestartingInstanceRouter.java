package com.lagou.router;

import org.apache.dubbo.common.URL;
import org.apache.dubbo.rpc.Invocation;
import org.apache.dubbo.rpc.Invoker;
import org.apache.dubbo.rpc.RpcException;
import org.apache.dubbo.rpc.cluster.Router;

import java.util.List;
import java.util.stream.Collectors;

public class RestartingInstanceRouter implements Router {

    private final ReadyRestartInstances instances;
    private final URL url;

    public RestartingInstanceRouter(URL url) {
        this.url = url;
        this.instances = ReadyRestartInstances.create();
    }

    @Override
    public URL getUrl() {
        return url;
    }

    @Override
    public <T> List<Invoker<T>> route(List<Invoker<T>> list, URL url, Invocation invocation) throws RpcException {
        return list.stream().filter(i -> !instances.hasRestartingInstance(i.getUrl().getParameter("remote.application"), i.getUrl().getIp(),i.getUrl().getPort())).collect(Collectors.toList());

    }

    @Override
    public boolean isRuntime() {
        return false;
    }

    @Override
    public boolean isForce() {
        return false;
    }

    @Override
    public int getPriority() {
        return 0;
    }
}
