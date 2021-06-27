package com.lagou.router;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.dubbo.common.utils.CollectionUtils;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ReadyRestartInstances implements PathChildrenCacheListener {
    private static final Logger LOOGER= LoggerFactory.getLogger(ReadyRestartInstances.class);
    private static final String LISTEN_PATHS="/lagou/dubbo/restart/instances";

    private final CuratorFramework zkClient;
    private volatile Set<String> restartInstance=new HashSet<>();

    private ReadyRestartInstances(CuratorFramework zkClient) {
        this.zkClient = zkClient;
    }

    public static ReadyRestartInstances create(){
        final CuratorFramework zookeeperClient=ZookeeperClients.client();
        try{
            final Stat stat=zookeeperClient.checkExists().forPath(LISTEN_PATHS);
            if(stat==null){
                zookeeperClient.create().creatingParentsIfNeeded().forPath(LISTEN_PATHS);
            }
        }catch(Exception e){
            e.printStackTrace();
            LOOGER.error("something error when create "+LISTEN_PATHS+"  : "+e.getMessage());
        }
        final ReadyRestartInstances instances=new ReadyRestartInstances(zookeeperClient);
        PathChildrenCache pathChildrenCache = new PathChildrenCache(zookeeperClient, LISTEN_PATHS, false);
        pathChildrenCache.getListenable().addListener(instances);
        try {
            pathChildrenCache.start();
        } catch (Exception e) {
            e.printStackTrace();
            LOOGER.error("add listener faild : "+e.getMessage());
        }
        return instances;
    }

    private String buildApplicationAndInstanceString(String applicationName,String host,Integer port){
        return applicationName+"_"+host+"_"+port;
    }

    public void addRestartingInstance(String application,String host,Integer port) throws Exception {
        if(zkClient.checkExists().forPath(LISTEN_PATHS+"/"+buildApplicationAndInstanceString(application,host,port))==null){
            zkClient.create().creatingParentsIfNeeded().forPath(LISTEN_PATHS+"/"+buildApplicationAndInstanceString(application,host,port));
        }
    }

    public void removeRestartingInstance(String application,String host,Integer port) throws Exception {
        zkClient.delete().forPath(LISTEN_PATHS+"/"+buildApplicationAndInstanceString(application, host,port));
    }

    public boolean hasRestartingInstance(String application,String host,Integer port){
        return restartInstance.contains(buildApplicationAndInstanceString(application, host,port));
    }

    @Override
    public void childEvent(CuratorFramework curatorFramework, PathChildrenCacheEvent pathChildrenCacheEvent) throws Exception {
        final List<String> restartInstances=zkClient.getChildren().forPath(LISTEN_PATHS);

        if(CollectionUtils.isEmpty(restartInstances)){
            this.restartInstance= Collections.emptySet();
        }else{
            this.restartInstance=new HashSet<>(restartInstances) ;
        }

    }
}
