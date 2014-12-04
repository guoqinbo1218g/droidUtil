package com.github.lisicnu.libDroid.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <p/>
 * <p/>
 * Author: Eden Lee<p/>
 * Date: 2014/11/24 <p/>
 * Email: checkway@outlook.com <p/>
 * Version: 1.0 <p/>
 */
public final class MiscUtils {

    private static class PriorityThreadFactory implements ThreadFactory {

        private static final AtomicInteger poolNumber = new AtomicInteger(1);

        private final ThreadGroup group;
        private final AtomicInteger threadNumber = new AtomicInteger(1);
        private final String namePrefix;
        private final int threadPriority;

        PriorityThreadFactory(int threadPriority) {
            if (threadPriority < Thread.MIN_PRIORITY || threadPriority > Thread.MAX_PRIORITY) {
                this.threadPriority = Thread.NORM_PRIORITY;
            } else {
                this.threadPriority = threadPriority;
            }
            SecurityManager s = System.getSecurityManager();
            group = (s != null) ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
            namePrefix = "pool-" + poolNumber.getAndIncrement() + "-thread-";
        }

        public Thread newThread(Runnable r) {
            Thread t = new Thread(group, r, namePrefix + threadNumber.getAndIncrement(), 0);
            if (t.isDaemon())
                t.setDaemon(false);
            t.setPriority(threadPriority);
            return t;
        }
    }

    static {
        pattern = Pattern.compile("[\\u4E00-\\u9FA5\\uF900-\\uFA2D]");
        executor = new ThreadPoolExecutor(0, Integer.MAX_VALUE, 30L, TimeUnit.SECONDS,
                new SynchronousQueue<Runnable>(), new PriorityThreadFactory(3),
                new ThreadPoolExecutor.DiscardOldestPolicy());
    }

    static Pattern pattern;

    /**
     * 查询是否含有中文字符
     *
     * @param str
     * @return
     */
    public static boolean findChinese(String str) {
        Matcher m = pattern.matcher(str);
        return m.find();
    }

    private static ExecutorService executor;

    /**
     * Thread priority is 3. Max thread number is 32, if thread is idle, will keep 30s.
     *
     * @return
     */
    public static ExecutorService getExecutor() {
        return executor;
    }

    public static String getSizeText(long size) {

        float kb = size / 1024f;
        float mb = kb / 1024;
        float gb = mb / 1024;

        if (((int) gb) > 0) {
            return String.format("%.2f Gb", gb);
        }
        if (((int) mb > 0)) {
            return String.format("%.2f Mb", mb);
        }
        if (((int) kb > 0)) {
            return String.format("%.0f Kb", kb);
        }
        return size + " byte";
    }
}
