package com.xlg.component.ks;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.RemovalListener;
import com.google.common.util.concurrent.JdkFutureAdapters;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListenableFutureTask;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;

/**
 * @author wangqingwei 
 * Created on 2021-07-20
 */
public class LoadingCacheStudy {


    private static final String key1 = "key1";

    private static final com.google.common.cache.LoadingCache<String, Long> cache = CacheBuilder.newBuilder()
            .maximumSize(100)
            .expireAfterWrite(10, TimeUnit.SECONDS)
            .removalListener((RemovalListener<String, Long>) notification -> {
                System.out.println("删除监听器: key, value" + notification.getKey() + "," + notification.getValue());
            })
            .build(new CacheLoader<String, Long>() {
                @Override
                public Long load(String key) throws Exception {
                    long curTime = System.currentTimeMillis();
                    System.out.println("curTime=" + curTime);
                    return curTime;
                }

                @Override
                public ListenableFuture<Long> reload(String key, Long oldValue) throws Exception {
                    System.out.println("刷新");
                    return super.reload(key, oldValue);
                }
            });

    private static final com.google.common.cache.LoadingCache<String, Long> notifyNoticeAndEmailCache = CacheBuilder.newBuilder() //
            .expireAfterWrite(
                    20, TimeUnit.SECONDS) // 缓存项在给定时间内没有被写访问(创建或覆盖)，则回收: 10s失效
            .maximumSize(10)
            .removalListener((RemovalListener<String, Long>) notification -> {
                System.out.println("删除监听器: key, value" + notification.getKey() + "," + notification.getValue());
            })
            .refreshAfterWrite(5, TimeUnit.SECONDS)
            .build(new AsyncReLoad<String, Long>() {
                @Override
                public Long load(String key) {
                    long curTime = System.currentTimeMillis();
                    System.out.println("curTime=" + curTime);
                    return curTime;
                }

                @Override
                public ListenableFuture<Long> reload(String key, Long oldValue) throws Exception {
                    System.out.println("异步线程, 执行");
                    return super.reload(key, oldValue);
                }
            });


    final static ListeningExecutorService service = MoreExecutors.listeningDecorator(Executors.newCachedThreadPool());
    private static final com.google.common.cache.LoadingCache<String, Long> graphs = CacheBuilder.newBuilder()
            .maximumSize(1000)
            .refreshAfterWrite(8, TimeUnit.SECONDS)
            .build(new CacheLoader<String, Long>() {
                @Override
                public Long load(String key) {
                    long timeMillis = System.currentTimeMillis();
                    System.out.println("load, time=" + timeMillis);
                    return timeMillis;
                }

                @Override
                public ListenableFuture<Long> reload(String key, Long oldValue) throws Exception {
                    // asynchronous!
                    ListenableFutureTask<Long> task =
                            ListenableFutureTask.create(new Callable<Long>() {
                                @Override
                                public Long call() throws Exception {
                                    long timeMillis = System.currentTimeMillis();
                                    System.out.println(Thread.currentThread().getName() + " reload, time= " + timeMillis);
                                    Thread.sleep(TimeUnit.SECONDS.toMillis(2));
                                    return timeMillis;
                                }
                            });
                    service.execute(task);
                    return task;
                }
            });

    public static com.google.common.cache.LoadingCache<String, Long> getGraphs() {
        return graphs;
    }

    @org.junit.Test
    public void BBB() {

    }


    public static void main(String[] args) throws ExecutionException, InterruptedException {

        getGraphs().asMap();

        new Thread(() -> {
            try {
                Thread.sleep(TimeUnit.SECONDS.toMillis(0));
                graphs.get(key1);
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }

        }).start();

        // 其实就是异步线程太快了， 以至于10s时有部分线程请求到了新值, 我们可以人为让异步线程放慢, 这时, 全部线程都会拿到旧值.
        new Thread(() -> {
            try {
                Thread.sleep(TimeUnit.SECONDS.toMillis(10));
                Long a1 = graphs.get(key1);
                System.out.println(Thread.currentThread().getName() + " a1 " + a1);
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }

        }).start();

        new Thread(() -> {
            try {
                Thread.sleep(TimeUnit.SECONDS.toMillis(12));
                Long a2 = graphs.get(key1);
                System.out.println(Thread.currentThread().getName() + " a2 " + a2);
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }

        }).start();

//        new Thread(() -> {
//            try {
//                Thread.sleep(TimeUnit.SECONDS.toMillis(12));
//                Long a3 = graphs.get(key1);
//                System.out.println(Thread.currentThread().getName() + " a3 " + a3);
//            } catch (InterruptedException | ExecutionException e) {
//                e.printStackTrace();
//            }
//
//        }).start();

        new Thread(() -> {
            try {
                Thread.sleep(TimeUnit.SECONDS.toMillis(15));
                Long a4 = graphs.get(key1);
                System.out.println(Thread.currentThread().getName() + " a4 " + a4);
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }

        }).start();
        //            String val = cache.get(key1);
        //            System.out.println(key1 + "， " + val);

//        Long aLong2 = cache.get(key1);
//        System.out.println("aLong2=" + aLong2);
//
//        Thread.sleep(TimeUnit.SECONDS.toMillis(2));
//
//        new Thread(() -> {
//            try {
//                Thread.sleep(TimeUnit.SECONDS.toMillis(2));
//                cache.refresh(key1);
//                System.out.println(1);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//
//        }).start();
//
//        new Thread(() -> {
//            try {
//                Thread.sleep(TimeUnit.SECONDS.toMillis(2));
//                cache.refresh(key1);
//                System.out.println("2");
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//
//        }).start();
//
//        Thread.sleep(TimeUnit.SECONDS.toMillis(2));
//
//        System.out.println("4s后。 ");
//        Long aLong3 = cache.get(key1);
//        System.out.println("aLong3=" + aLong3);
//
//        new Thread(() -> {
//            try {
//                Thread.sleep(TimeUnit.SECONDS.toMillis(12));
//                System.out.println("3s后。 ");
//                Long aLong1 = cache.get(key1);
//                System.out.println("aLong1=" + aLong1);
//
//            } catch (InterruptedException | ExecutionException e) {
//                e.printStackTrace();
//            }
//
//        }).start();
//
//        new Thread(() -> {
//            try {
//                Thread.sleep(TimeUnit.SECONDS.toMillis(12));
//                System.out.println("3s后。 ");
//                Long aLong1 = cache.get(key1);
//                System.out.println("aLong2=" + aLong1);
//
//            } catch (InterruptedException | ExecutionException e) {
//                e.printStackTrace();
//            }
//
//        }).start();


//        System.out.println(Thread.currentThread().getName() + ", " + Thread.currentThread().getState());
//        Long aLong = notifyNoticeAndEmailCache.get(key1);
//        System.out.println("aLong1=" + aLong);
//        Thread.sleep(TimeUnit.SECONDS.toMillis(2));
//        cache.refresh(key1);

//        new Thread(() -> {
//            try {
//                Thread.sleep(TimeUnit.SECONDS.toMillis(3));
//                System.out.println("3s后。 ");
//                Long aLong1 = notifyNoticeAndEmailCache.get(key1);
//                System.out.println("aLong1=" + aLong1);
//
//            } catch (InterruptedException | ExecutionException e) {
//                e.printStackTrace();
//            }
//
//        }).start();
//
//        new Thread(() -> {
//            try {
//                Thread.sleep(TimeUnit.SECONDS.toMillis(3));
//                System.out.println("3s后。 ");
//                Long aLong1 = notifyNoticeAndEmailCache.get(key1);
//                System.out.println("aLong2=" + aLong1);
//
//            } catch (InterruptedException | ExecutionException e) {
//                e.printStackTrace();
//            }
//        }).start();
//
//        new Thread(() -> {
//            try {
//                Thread.sleep(TimeUnit.SECONDS.toMillis(7));
//                notifyNoticeAndEmailCache.refresh(key1);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//
//        }).start();
//
//        new Thread(() -> {
//            try {
//                Thread.sleep(TimeUnit.SECONDS.toMillis(7));
//                System.out.println("7后。 ");
//                Long aLong1 = notifyNoticeAndEmailCache.get(key1);
//                System.out.println("aLong4=" + aLong1);
//            } catch (InterruptedException | ExecutionException e) {
//                e.printStackTrace();
//            }
//        }).start();
//
//
//        new Thread(() -> {
//            try {
//                Thread.sleep(TimeUnit.SECONDS.toMillis(14));
//                System.out.println("8后。 ");
//                Long aLong1 = notifyNoticeAndEmailCache.get(key1);
//                System.out.println("aLong4=" + aLong1);
//            } catch (InterruptedException | ExecutionException e) {
//                e.printStackTrace();
//            }
//        }).start();
    }

    public abstract static class AsyncReLoad<K, V> extends CacheLoader<K, V> {

        final static ListeningExecutorService service = MoreExecutors.listeningDecorator(Executors.newCachedThreadPool());
        final static ExecutorService ex = Executors.newCachedThreadPool();

        @Override
        public ListenableFuture<V> reload(K key, V oldValue) throws Exception {
            ListenableFutureTask<V> task = ListenableFutureTask.create(() -> this.load(key));
            System.out.println("刷新1111");
            ListenableFuture<?> listenable = JdkFutureAdapters.listenInPoolThread(ex.submit(task));
            return (ListenableFuture<V>) listenable;
        }
    }
}
