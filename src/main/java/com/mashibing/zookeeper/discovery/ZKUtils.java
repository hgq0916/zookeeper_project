package com.mashibing.zookeeper.discovery;

import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;

/**
 * zookeeper工具类
 * @author hugangquan
 * @date 2021/08/16 08:51
 */
public class ZKUtils {

    private static final String connectString = "192.168.25.60:2181,192.168.25.61:2181,192.168.25.62:2181,192.168.25.63:2181";


    /**
     * 获取zk
     * @return
     */
    public static ZooKeeper getZK(){
        return getZK("");
    }

    /**
     *
     * 获取zk
     * @param path 默认路径
     * @return
     */
    public static ZooKeeper getZK(String path){
        ZooKeeper zk = null;
        try {
            DefaultWatch defaultWatch = new DefaultWatch();
            zk = new ZooKeeper(connectString+path,2000,defaultWatch);
            defaultWatch.await();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return zk;
    }

}
