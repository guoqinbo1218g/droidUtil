package com.github.lisicnu.libDroid.helper;

import android.os.SystemClock;

import com.github.lisicnu.libDroid.util.MiscUtils;
import com.github.lisicnu.log4android.LogManager;

import java.io.File;

/**
 * 包含搜索方法
 * <p/>
 * <p/>
 * Author: Eden Lee<p/>
 * Date: 2014/11/24 <p/>
 * Email: checkway@outlook.com <p/>
 * Version: 1.0 <p/>
 */
public final class FileScanner {

    static final String TAG = FileScanner.class.getSimpleName();
    private boolean stop = false;
    /**
     * 搜索时是否忽略文件后缀名的大小写
     */
    private boolean isFileIgnoreCase;
    private ISearch finder;
    private String[] srPath;
    private String[] srTypes;
    private boolean includeSubDir;
    private boolean isSearching = false;

    /**
     * 默认搜索后缀不区分大小写, 不包含自己文件夹
     */
    public FileScanner() {
        setFileExtIgnoreCase(false);
        setIncludeSubDir(false);
        srPath = new String[0];
        srTypes = new String[0];
    }

    public ISearch getFinder() {
        return finder;
    }

    public FileScanner setFinder(ISearch finder) {
        this.finder = finder;
        return this;
    }

    public String[] getSearchPath() {
        return srPath;
    }

    /**
     * 搜索指定目录
     *
     * @param srPath
     */
    public FileScanner setSearchPath(String... srPath) {
        if (srPath == null || srPath.length == 0) {
            this.srPath = new String[0];
        } else {
            this.srPath = srPath;
        }
        return this;
    }

    public String[] getSearchTypes() {
        return srTypes;
    }

    /**
     * 如果传入null 或者 空字符 数组, 表示各种文件全支持
     *
     * @param srTypes
     */
    public FileScanner setSearchTypes(String... srTypes) {
        if (srTypes == null || srTypes.length == 0) {
            this.srTypes = new String[0];
        } else {
            this.srTypes = srTypes;
        }
        return this;
    }

    /**
     * 返回是否搜索子目录
     *
     * @return
     */
    public boolean isIncludeSubDir() {
        return includeSubDir;
    }

    /**
     * 是否搜索子目录
     *
     * @param includeSubDir
     */
    public FileScanner setIncludeSubDir(boolean includeSubDir) {
        this.includeSubDir = includeSubDir;
        return this;
    }

    /**
     * 搜索的后缀名是否区分大小写
     *
     * @return
     */
    public boolean isFileExtIgnoreCase() {
        return isFileIgnoreCase;
    }

    /**
     * 搜索的后缀名是否区分大小写
     *
     * @param isFileIgnoreCase
     */
    public FileScanner setFileExtIgnoreCase(boolean isFileIgnoreCase) {
        this.isFileIgnoreCase = isFileIgnoreCase;
        return this;
    }

    private void getFile(File f) {

        if (stop)
            return;

        try {

            File[] files = f.listFiles();
            if (files == null) {
                return;
            }

            String tmpExt;
            for (File file : files) {
                if (stop)
                    return;

                if (file.isDirectory() && isIncludeSubDir()) {

                    getFile(file);

                } else {
                    if (isFileExtIgnoreCase()) {
                        tmpExt = file.getName().toLowerCase();
                    } else {
                        tmpExt = file.getName();
                    }

                    for (String type : srTypes) {

                        if (isFileExtIgnoreCase()) {
                            type = type.toLowerCase();
                        }

                        if (tmpExt.endsWith(type) && finder != null) {
                            finder.findFile(file);
                        }
                    }

                }
            }
        } catch (Exception e) {
            LogManager.e(TAG, e.toString());
        }

    }

    public boolean isSearching() {
        return isSearching;
    }

    /**
     * 开始搜索目录. 当前搜索会停止之前的搜索
     */
    public void startSearch() {

        stopSearch();

        stop = false;

        MiscUtils.getExecutor().execute(new Runnable() {
            @Override
            public void run() {
                isSearching = true;
                try {
                    if (finder != null) {
                        finder.start();
                    }

                    for (String aSrPath : srPath) {
                        getFile(new File(aSrPath));
                    }

                    if (!stop && finder != null) {
                        finder.finished();
                    }

                } finally {
                    isSearching = false;
                }

            }
        });
    }

    private void waitToFinish() {
        long startTime = SystemClock.elapsedRealtime();
        while (isSearching && (SystemClock.elapsedRealtime() - startTime < 3000)) {
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {

            }
        }
    }

    public void stopSearch() {
        stop = true;
        waitToFinish();
        isSearching = false;
    }

    public interface ISearch {
        /**
         * 搜索开始
         */
        void start();

        /**
         * 找到文件
         *
         * @param file 找到的文件
         */
        void findFile(File file);

        /**
         * 搜索结束时触发，当主动停止时不会触发。
         */
        void finished();
    }

}
