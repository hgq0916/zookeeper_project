package com.mashibing.zookeeper.utils;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * 分布式锁
 * @author hugangquan
 * @date 2021/08/16 10:56
 */
public class ZkLock implements Watcher, AsyncCallback.Children2Callback, AsyncCallback.StatCallback, AsyncCallback.StringCallback {

    private String threadName;

    private static final String path = "/lock";

    private String pathName;

    private ZooKeeper zk;

    private CountDownLatch countDownLatch;

    public String getThreadName() {
        return threadName;
    }

    public void setThreadName(String threadName) {
        this.threadName = threadName;
    }

    public ZooKeeper getZk() {
        return zk;
    }

    public void setZk(ZooKeeper zk) {
        this.zk = zk;
    }

    /**
     * 尝试加锁
     */
    public void tryLock() {
        try {
            countDownLatch = new CountDownLatch(1);
            zk.create(path, threadName.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL,this,"abc");
            //等待获取锁
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    /**
     * 释放锁
     */
    public void releaseLock() {
        try{
            zk.delete(pathName,-1);
            System.out.println(threadName+pathName+"节点删除成功");
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (KeeperException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void process(WatchedEvent event) {
        System.out.println("监控前一个节点删除:"+event);
        switch (event.getType()){
            case None:
                break;
            case NodeCreated:
                break;
            case NodeDeleted:
                //监控path的下级节点
                System.out.println(threadName+pathName+"监控到"+event.getPath()+"节点删除成功");
                zk.getChildren("/",false,this,"abc");
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

    @Override
    public void processResult(int rc, String path, Object ctx, List<String> children, Stat stat) {

        Collections.sort(children);

        int index = children.indexOf(pathName.substring(1));
        if(index == 0){
            //第一个，获取锁成功
            try {
                zk.setData("/",threadName.getBytes(),-1);
            } catch (KeeperException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            countDownLatch.countDown();
        }else {
            //监控前一个节点的删除
            String preNode = children.get(index - 1);
            System.out.println(threadName+"等待/"+preNode+"节点删除...");
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            zk.exists("/"+preNode,this,this,"abc");
        }
    }


    @Override
    public void processResult(int rc, String path, Object ctx, Stat stat) {
        System.out.println("监控前一个节点的状态:"+threadName+",path:"+path+",stat:"+stat);
        if(stat == null){
            zk.getChildren("/",true,this,"abc");
        }else {
            zk.exists(path,this,this,"abc");
        }
    }

    @Override
    public void processResult(int rc, String path, Object ctx, String name) {
        if(name != null){
            pathName = name;
            System.out.println(threadName+"----"+pathName);
            //监控path的下级节点
            zk.getChildren("/",false,this,"abc");
        }
    }
}
