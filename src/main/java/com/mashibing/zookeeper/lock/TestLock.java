package com.mashibing.zookeeper.lock;

import com.mashibing.zookeeper.utils.ZKUtils;
import com.mashibing.zookeeper.utils.ZkLock;
import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * 分布式锁
 * @author hugangquan
 * @date 2021/08/16 10:38
 */
public class TestLock {


    @Test
    public void testLock() throws Exception {

        CountDownLatch countDownLatch = new CountDownLatch(100);

        for(int i=0;i<100;i++){
            new Thread(()->{

                ZkLock zkLock = new ZkLock();
                zkLock.setZk(ZKUtils.getZK("/testLock",2000));
                zkLock.setThreadName(Thread.currentThread().getName());
                //加锁
                zkLock.tryLock();
                try{
                    //业务逻辑
                    System.out.println(Thread.currentThread().getName()+"业务逻辑处理...");
                    //TimeUnit.MILLISECONDS.sleep(1000);
                }catch (Exception e){
                }finally {
                    //释放锁
                    zkLock.releaseLock();
                    countDownLatch.countDown();
                }

            }).start();
        }

        countDownLatch.await();
        System.out.println("所有线程结束");
    }

}
