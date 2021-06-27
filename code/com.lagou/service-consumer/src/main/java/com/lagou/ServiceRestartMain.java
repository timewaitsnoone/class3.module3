package com.lagou;

import com.lagou.router.ReadyRestartInstances;

public class ServiceRestartMain {
    public static void main(String[] args) throws Exception {
        ReadyRestartInstances.create().addRestartingInstance("service-provider","192.168.50.133",20881);
        ReadyRestartInstances.create().removeRestartingInstance("service-provider","192.168.50.133",20881);
    }
}
