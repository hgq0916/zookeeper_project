package com.mashibing.zookeeper.discovery;

import com.mashibing.zookeeper.utils.ZKUtils;
import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * zk注册发现
 * @author hugangquan
 * @date 2021/08/16 08:58
 */
public class ZkConfig {

    private ZooKeeper zk = null;

    @Before
    public void getZk(){
        zk = ZKUtils.getZK("/testConfig");
    }

    @After
    public void destory(){
        try {
            zk.close();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testConfig1() throws Exception {
        zk.exists("/AppConf", new Watcher() {
            public void process(WatchedEvent event) {
                Event.EventType type = event.getType();
                switch (type){

                    case None:
                        break;
                    case NodeCreated:
                        zk.getData("/AppConf", true, new AsyncCallback.DataCallback() {
                            public void processResult(int rc, String path, Object ctx, byte[] data, Stat stat) {
                                if (data != null) {
                                    System.out.println("AppConf节点配置:" + new String(data));
                                }
                            }
                        }, "abc");
                        break;
                    case NodeDeleted:
                        break;
                    case NodeDataChanged:
                        break;
                    case NodeChildrenChanged:
                        break;
                    case DataWatchRemoved:
                        break;
                    case ChildWatchRemoved:
                        break;
                    case PersistentWatchRemoved:
                        break;
                }
            }
        }, new AsyncCallback.StatCallback() {

            public void processResult(int rc, String path, Object ctx, Stat stat) {
                if (stat != null) {
                    zk.getData("/AppConf", true, new DataCallback() {
                        public void processResult(int rc, String path, Object ctx, byte[] data, Stat stat) {
                            if (data != null) {
                                System.out.println("AppConf节点配置:" + new String(data));
                            }
                        }
                    }, "abc");
                }
            }
        }, "abc");

        while (true){

        }

    }

    @Test
    public void testConfig2() throws Exception {

        String path = "/AppConf";

        ZkCallBack zkCallBack = new ZkCallBack();
        zkCallBack.setZk(zk);
        zkCallBack.setPath(path);
        Config conf = new Config();
        zkCallBack.setConf(conf);

        zk.exists(path,zkCallBack,zkCallBack , "abc");
        zkCallBack.await();

        while (true){
            if(conf.getConf().equals("")){
                System.out.println(path+"丢失了...");
                zkCallBack.await();
            }else {
                System.out.println(path+":"+conf.getConf());
            }

            TimeUnit.MILLISECONDS.sleep(1000);
        }
    }

    @Test
    public void test2(){
        CountDownLatch countDownLatch = new CountDownLatch(1);
        countDownLatch.countDown();
        countDownLatch.countDown();
        countDownLatch.countDown();
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("执行完成");
    }

}
