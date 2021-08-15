package com.mashibing.zookeeper;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;
import org.apache.zookeeper.proto.WatcherEvent;

/**
 * @author hugangquan
 * @date 2021/08/15 10:50
 */
public class ZookeeperMain {

    public static void main(String[] args) throws Exception {

        //建立连接
        ZooKeeper zooKeeper = new ZooKeeper("192.168.25.60:2181,192.168.25.61:2181,192.168.25.62:2181", 10000, new Watcher() {
            public void process(WatchedEvent event) {
                String path = event.getPath();
                Event.KeeperState state = event.getState();
                Event.EventType type = event.getType();
                WatcherEvent wrapper = event.getWrapper();

                System.out.println("path:" + path + ",state:" + state + ",type:" + type + ",wrapper:" + wrapper);

                switch (state) {

                    case Unknown:
                        break;
                    case Disconnected:
                        System.out.println("与服务器断开连接");
                        break;
                    case NoSyncConnected:
                        break;
                    case SyncConnected:
                        System.out.println("与服务器同步连接");
                        break;
                    case AuthFailed:
                        System.out.println("与服务器认证失败");
                        break;
                    case ConnectedReadOnly:
                        System.out.println("与服务器建立只读连接");
                        break;
                    case SaslAuthenticated:
                        break;
                    case Expired:
                        System.out.println("连接已过期");
                        break;
                    case Closed:
                        System.out.println("连接关闭");
                        break;
                }
            }
        });

        //zooKeeper.close();

        //创建节点
        //OPEN_ACL_UNSAFE 创建不安全的节点
        String path = zooKeeper.create("/ooxx", "helloworld".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
        System.out.println(path);

        ///ooxx0000000006
        zooKeeper.getData(path, new Watcher() {
            public void process(WatchedEvent event) {
                String path1 = event.getPath();
                Event.KeeperState state = event.getState();
                Event.EventType type = event.getType();
                WatcherEvent wrapper = event.getWrapper();
                System.out.println("path:" + path1 + ",state:" + state + ",type:" + type + ",wrapper:" + wrapper);
            }
        },new AsyncCallback.DataCallback(){

            public void processResult(int rc, String path, Object ctx, byte[] data, Stat stat) {
                System.out.println("rc:"+rc+",path:"+path+",ctx:"+ctx+",data:"+new String(data)+",stat:"+stat);
            }
        },new ZookeeperMain());

        //修改节点数据
        zooKeeper.setData(path,"haha".getBytes(),-1);

        //删除节点
        //zooKeeper.delete(path,-1);


        System.in.read();

    }

}
