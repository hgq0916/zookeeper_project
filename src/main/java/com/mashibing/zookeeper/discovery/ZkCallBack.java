package com.mashibing.zookeeper.discovery;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.util.concurrent.CountDownLatch;

/**
 * @author hugangquan
 * @date 2021/08/16 09:19
 */
public class ZkCallBack implements Watcher, AsyncCallback.DataCallback, AsyncCallback.StatCallback {

    private ZooKeeper zk;

    private Config conf;

    private String path;

    private CountDownLatch countDownLatch = new CountDownLatch(1);

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Config getConf() {
        return conf;
    }

    public void setConf(Config conf) {
        this.conf = conf;
    }

    public ZooKeeper getZk() {
        return zk;
    }

    public void setZk(ZooKeeper zk) {
        this.zk = zk;
    }

    public void await() {
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void processResult(int rc, String path, Object ctx, byte[] data, Stat stat) {
        if (data != null) {
            conf.setConf(new String(data));
            countDownLatch.countDown();
        }
    }

    public void processResult(int rc, String path, Object ctx, Stat stat) {
        if (stat != null) {
            zk.getData(path, this,this, "abc");
        }
    }

    public void process(WatchedEvent event) {
        Event.EventType type = event.getType();
        switch (type){

            case None:
                break;
            case NodeCreated:
                System.out.println("节点创建了");
                zk.getData(path, this,this, "abc");
                break;
            case NodeDeleted:
                //异常处理
                try {
                    conf.setConf("");
                    zk.exists(path,this);
                    countDownLatch = new CountDownLatch(1);
                } catch (KeeperException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                break;
            case NodeDataChanged:
                zk.getData(path, this,this, "abc");
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



}
