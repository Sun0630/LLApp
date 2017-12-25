package com.umeng.soexample.task;

import android.util.Log;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

/**
 * FutureTask 用法
 * 这是一个可以取消的异步计算，该类提供了Future的基本实现，具有启动和取消运算，查询运算是否结束，
 * 并且检查返回计算的结果，该结果只能在运行完成之后才能获取到，如果程序没有运行结束，则`get()`将会阻塞。
 * 程序运行结束之后，无法重新启动或者是取消程序（除非调用`runAndReset`方法）
 * Created by LiuLei on 2017/7/18.
 */

public class ThreadTask {

    /**
     * ExecutorService
     */
    private static ExecutorService mExecutor = Executors.newSingleThreadExecutor();

    private static class WorkTask implements Callable<Integer> {

        @Override
        public Integer call() throws Exception {
            //我们这里通过使用线程 sleep来模拟耗时的操作，以后我们所有的耗时操作都在该方法里面执行了
            Thread.sleep(5000);
            //将执行的结果返回出去
            return 1000;
        }
    }

    public static void executeTask() {
        //创建一个worktask，并且当作参数传入到FutureTask中。
        WorkTask workTask = new WorkTask();
        /**
         * FutureTask则是一个RunnableFuture<V>，即实现了Runnbale又实现了Futrue<V>这两个接口，
         * 另外它还可以包装Runnable(实际上会转换为Callable)和Callable
         * <V>，所以一般来讲是一个符合体了，它可以通过Thread包装来直接执行，也可以提交给ExecuteService来执行
         * ，并且还可以通过v get()返回执行结果，在线程体没有执行完成的时候，主线程一直阻塞等待，执行完则直接返回结果。
         */
        FutureTask<Integer> futureTask = new FutureTask<Integer>(workTask) {
            @Override
            protected void done() {
                try {
                    //该方法回调意思线程运行结束回调的，然后获取call方法中返回的结果
                    int result = 0;
                    try {
                        result = get();//获取上面call()的返回值    如果call()是耗时操作，则get()将会阻塞
                        Log.i("LOH", "result..." + result);
                        Thread.currentThread().getName();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        //1、将FutureTask作为参数传入到Thread函数中执行。
        Thread thread = new Thread(futureTask);
        //启动线程执行任务
        thread.start();

//        //2、或者提交futureTask到ExecutorService
//        mExecutor.submit(futureTask);


    }

    public static void testTask() {
        /**
         * 提交runnable则没有返回值, future没有数据
         */
        Future<?> result = mExecutor.submit(() -> {
            //TODO
        });
        try {
            System.out.println("future result from runnable : " + result.get());
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        /**
         * 提交Callable, 有返回值, future中能够获取返回值
         */
        Future<Integer> result2 = mExecutor.submit(() -> 11111);
        try {
            System.out.println("future result from callable : " + result2.get());
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

    }

    public static void cancelTask() {

    }

}
