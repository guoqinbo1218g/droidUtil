package com.github.lisicnu.libDroid.helper;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.webkit.URLUtil;

import com.github.lisicnu.libDroid.util.MiscUtils;
import com.github.lisicnu.libDroid.util.StringUtils;
import com.github.lisicnu.log4android.LogManager;
import com.github.lisicnu.libDroid.util.BitmapUtils;
import com.github.lisicnu.libDroid.util.FileUtils;
import com.github.lisicnu.libDroid.util.URLUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Deprecated()
@SuppressWarnings("use com.nostra13.universalimageloader to instead.")
/**
 * max cache size is 8M.<br/>
 * <strong>use Android-Universal-Image-Loader to instead.
 *
 * @see <a href="https://github.com/nostra13/Android-Universal-Image-Loader">Android-Universal-Image-Loader</a>
 * </strong>
 * <p/>
 * <p/>
 * Author: Eden Lee<p/>
 * Date: 2014/11/24 <p/>
 * Email: checkway@outlook.com <p/>
 * Version: 1.0 <p/>
 */
public final class BitmapCacheLoader {

    private static final String TAG = BitmapCacheLoader.class.getSimpleName();
    private static BitmapCacheLoader _instance = new BitmapCacheLoader();
    /**
     * 保存所有的 正在下载和已下载的状态列表
     */
    private final LinkedList<QueueHolder> downloadQueue = new LinkedList<QueueHolder>();
    private final Object locker = new Object();
    private LruMemoryCache imageCache = new LruMemoryCache(8 * 1024 * 1024);

    /**
     * 最大同时加载个数.
     */
    private int maxLoading = 3;

    public static BitmapCacheLoader getInstance() {
        return _instance;
    }

    private static String fixTempPath(String tmpDirectory) {
        if (tmpDirectory == null || tmpDirectory.isEmpty()) {
            File fi = new File("mnt/sdcard/cache/");
            fi.mkdirs();
            return fi.getPath();
        }
        return tmpDirectory;
    }

    public void clearCache(String fileName) {
        if (imageCache != null && !StringUtils.isNullOrEmpty(fileName)) {
            imageCache.remove(fileName);
        }
    }

    /**
     * clear cache bitmaps.
     */
    public void clearCaches() {
        if (imageCache != null) {
            imageCache.clear();
        }
    }

    public Bitmap loadFile(String fileName, String tmpDirectory, ImageCallBack imageCallBack,
                           boolean canRemove, int reqWidth, int reqHeight) {

        if (fileName == null || fileName.isEmpty())
            return null;

        // STEP 1.
        Bitmap tmpBMP = loadFromCache(fileName, reqWidth, reqHeight);
        if (tmpBMP != null)
            return tmpBMP;

        // STEP 2.
        File file = new File(fileName);
        if (file.isDirectory())
            return null;

        if (file.isFile()) {
            if (file.exists()) {
                Bitmap tmpBmp = BitmapUtils.load(fileName, reqWidth, reqHeight);
                if (tmpBmp != null)
                    imageCache.put(fileName, tmpBmp);
                return tmpBmp;
            } else {
                return null;
            }
        }

        // STEP 3.
        tmpDirectory = fixTempPath(tmpDirectory);
        File cacheDir = new File(tmpDirectory);
        if (!cacheDir.exists()) {
            cacheDir.mkdirs();
        }

        tmpBMP = loadFromTempFolder(fileName, cacheDir, reqWidth, reqHeight);
        if (tmpBMP != null)
            return tmpBMP;

        // STEP 4.
        if (!URLUtil.isValidUrl(fileName)) {
            // LogManager.i(TAG, "File not found and isn't a valid URL." +
            // fileName);
            return null;
        }

        // add to download queue
        addToDownloadQueue(fileName, tmpDirectory, imageCallBack, canRemove, reqWidth, reqHeight);

        return null;
    }

    /**
     * Load Steps:<br/>
     * 1. load from caches. <br/>
     * 2. if file not exist in caches, then load it from local storage.<br/>
     * 3. if local storage not exist, then load it from temporary directory.<br/>
     * 4. if file not exist in temporary directory. then download it.<br/>
     * 5. after download, then callback will be added.<br/>
     *
     * @param fileName      file name , local or URL 可以是带路径名, 也可以不带路径名. 当带路径名时, 先去查找指定目录..
     *                      然后再找傳入的臨時目錄.
     * @param tmpDirectory  临时目录, 下载之后的保存目录. 默认路径: mnt/sdcard/cache
     * @param imageCallBack 下载完成时的毁掉
     * @param canRemove     是否可以移除. 当等待的个数超过最大下载数目时, 当前条目是否可以清除.
     * @return the bitmap loaded. if from network, this will return null,
     * return the result from callback
     */
    public Bitmap loadFile(String fileName, String tmpDirectory, ImageCallBack imageCallBack,
                           boolean canRemove) {

        return loadFile(fileName, tmpDirectory, imageCallBack, canRemove, -1, -1);
    }

    private synchronized void addToDownloadQueue(String fileName, String tmpDirectory,
                                                 ImageCallBack imageCallBack, boolean canRemove, int reqWidth, int reqHeight) {

        QueueHolder holder = null;
        synchronized (locker) {
            for (QueueHolder tmp : downloadQueue) {
                if (tmp != null && tmp.imageUrl.equals(fileName)) {
                    holder = tmp;
                }
            }
        }
        if (holder == null) {
            holder = new QueueHolder();
            synchronized (locker) {
                downloadQueue.add(holder);
            }
        }

        holder.addCallBack(imageCallBack);
        holder.imageUrl = fileName;
        holder.removeAble = canRemove;
        holder.reqHeight = reqHeight;
        holder.reqWidth = reqWidth;
        holder.tmpDirectory = tmpDirectory;

        synchronized (locker) {
            int count = downloadQueue.size() - getMaxLoading();
            if (count > 0) {
                for (int j = 0; j < count; j++) {
                    QueueHolder tmp = downloadQueue.get(j);
                    if (tmp != null && tmp.removeAble) {
                        tmp.stopLoading();
                        downloadQueue.remove(j);
                    }
                }
            }
        }
        holder.startLoadingImage();
    }

    private Bitmap loadFromTempFolder(String fileName, File cacheDir, int width, int height) {

        String bitmapName = FileUtils.removeInvalidSeprator(fileName.substring(fileName
                .lastIndexOf(File.separator) + 1));

        File[] cacheFiles = cacheDir.listFiles();
        if (cacheFiles != null) {

            File fi = null;
            for (File cacheFile : cacheFiles) {
                if (cacheFile.exists() && bitmapName.equals(cacheFile.getName())) {
                    if (cacheFile.length() == 0) {
                        cacheFile.delete();
                    } else {
                        fi = cacheFile;
                    }
                    break;
                }
            }

            if (fi != null) {
                if (width > 0 && height > 0)
                    return BitmapUtils.load(fi.getAbsolutePath(), width, height);

                return BitmapFactory.decodeFile(fi.getAbsolutePath(), getDefaultBmpOption());
            }
        }
        return null;
    }

    private Bitmap loadFromCache(String fileName, int reqWidth, int reqHeight) {
        if (imageCache.get(fileName) == null) {
            Bitmap tmpBmp = BitmapUtils.load(fileName, reqWidth, reqHeight);
            if (!TextUtils.isEmpty(fileName) && tmpBmp != null)
                imageCache.put(fileName, tmpBmp);
            return tmpBmp;
        }
        return imageCache.get(fileName);
    }

    public int getMaxLoading() {
        return maxLoading;
    }

    /**
     * 设置能够同时下载的最大个数.
     *
     * @param maxLoadingCount
     * @return
     */
    public BitmapCacheLoader setMaxLoading(int maxLoadingCount) {
        maxLoading = maxLoadingCount;
        return this;
    }

    /**
     * 强制移除所有等待下载的item
     */
    public void forceRemoveItems() {
        synchronized (locker) {
            for (int j = 0; j < downloadQueue.size() - getMaxLoading(); j++) {
                QueueHolder tmp = downloadQueue.get(j);
                if (tmp != null) {
                    tmp.stopLoading();
                    downloadQueue.remove(j);
                }
            }
        }
    }

    private void downloadCallback(String url, Bitmap tmp) {
        try {
            List<ImageCallBack> callBacks = null;

            synchronized (locker) {
                for (QueueHolder holder : downloadQueue) {
                    if (holder != null && holder.imageUrl.equals(url)) {
                        callBacks = holder.callbacks;
                        break;
                    }
                }
            }

            if (callBacks != null) {
                for (ImageCallBack imageCallBack : callBacks) {
                    if (imageCallBack != null) {
                        imageCallBack.imageLoad(url, tmp);
                    }
                }
            }
        } catch (Exception e) {
        }
    }

    private BitmapFactory.Options getDefaultBmpOption() {
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inPreferredConfig = Bitmap.Config.RGB_565;
        opt.inPurgeable = true;
        opt.inInputShareable = true;
        opt.inSampleSize = 2;
        return opt;
    }

    public static interface ImageCallBack {
        /**
         * 图片的加载结果提示, 如果加载失败, 则 bitmap 返回 null.
         *
         * @param imageUrl 加载图片的地址
         * @param bitmap   图片加载的结果, 如果失败, 则返回 null.
         */
        public void imageLoad(String imageUrl, Bitmap bitmap);
    }

    private class QueueHolder {

        boolean removeAble = true;
        String imageUrl;
        List<ImageCallBack> callbacks;
        boolean loadFinished = false;
        boolean stopLoading = false;
        boolean isLoading = false;
        HttpURLConnection http = null;
        int reqWidth, reqHeight;
        private String tmpDirectory;

        QueueHolder() {
            callbacks = new ArrayList<ImageCallBack>();
        }

        void addCallBack(ImageCallBack callback) {
            if (callbacks.contains(callback))
                return;

            callbacks.add(callback);
        }

        /**
         * execute loading image after 300 milliseconds.
         */
        void startLoadingImage() {
            if (loadFinished || isLoading)
                return;

            MiscUtils.getExecutor().execute(new LoadingRunnable());
        }

        void stopLoading() {
            stopLoading = true;

            if (http != null) {
                http.disconnect();
                http = null;
            }
        }


        class LoadingRunnable implements Runnable {

            @Override
            public void run() {

                LogManager.d(TAG, "Loading Runnable starting....." + imageUrl);
                isLoading = true;
                InputStream bitmapIs = null;
                FileOutputStream fos = null;
                Bitmap bitmap = null;

                try {

                    Thread.sleep(500);
                    if (stopLoading)
                        return;

                    http = URLUtils.getNormalCon(imageUrl);
//                    http.setRequestProperty("Connection", "Keep-Alive");
                    http.setConnectTimeout(5000);
                    http.setReadTimeout(10000);
                    http.connect();

                    if (stopLoading) {
                        return;
                    }

                    bitmapIs = http.getInputStream();

                    if (reqWidth > 0 && reqHeight > 0)
                        bitmap = BitmapUtils.load(bitmapIs, reqWidth, reqHeight);
                    else
                        bitmap = BitmapFactory.decodeStream(bitmapIs, null, getDefaultBmpOption());

                    if (bitmap != null && imageUrl != null)
                        imageCache.put(imageUrl, bitmap);

                    tmpDirectory = fixTempPath(tmpDirectory);

                    File dir = new File(tmpDirectory);
                    if (!dir.exists()) {
                        dir.mkdirs();
                    }

                    String tmp = FileUtils.removeInvalidSeprator(URLUtils.analysisFileName(imageUrl));

                    File bitmapFile = new File(tmpDirectory, tmp);

                    if (!bitmapFile.exists()) {
                        try {
                            bitmapFile.createNewFile();
                        } catch (IOException e) {
                            LogManager.e(TAG, e);
                        }
                    }

                    if (stopLoading) {
                        return;
                    }

                    if (bitmap != null) {
                        fos = new FileOutputStream(bitmapFile);
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                        fos.close();
                        fos = null;
                    }
                } catch (Exception e) {
                    LogManager.e(TAG, e);
                } finally {
                    if (bitmapIs != null) {
                        try {
                            bitmapIs.close();
                        } catch (IOException e) {
                            LogManager.e(TAG, e);
                        }
                        bitmapIs = null;
                    }
                    if (http != null) {
                        http.disconnect();
                        http = null;
                    }
                    if (fos != null) {
                        try {
                            fos.close();
                        } catch (IOException e) {
                            LogManager.e(TAG, e);
                        }
                        fos = null;
                    }

                    loadFinished = true;
                    isLoading = false;

                    downloadCallback(imageUrl, bitmap);

                    synchronized (locker) {
                        for (QueueHolder tmp : downloadQueue) {
                            if (tmp != null && tmp.imageUrl.equals(imageUrl)) {
                                downloadQueue.remove(tmp);
                                break;
                            }
                        }
                    }
                }
            }
        }

    }

    class LruMemoryCache {

        private final LinkedHashMap<String, Bitmap> map;

        private final int maxSize;
        /**
         * Size of this cache in bytes
         */
        private int size;

        /**
         * @param maxSize Maximum sum of the sizes of the Bitmaps in this cache, bytes.
         */
        public LruMemoryCache(int maxSize) {
            if (maxSize <= 0) {
                throw new IllegalArgumentException("maxSize <= 0");
            }
            this.maxSize = maxSize;
            this.map = new LinkedHashMap<String, Bitmap>(0, 0.75f, true);
        }

        /**
         * Returns the Bitmap for {@code key} if it exists in the cache. If a Bitmap was returned, it is moved to the head
         * of the queue. This returns null if a Bitmap is not cached.
         */
        public final Bitmap get(String key) {
            if (key == null) {
                throw new NullPointerException("key == null");
            }

            synchronized (this) {
                return map.get(key);
            }
        }

        /**
         * Caches {@code Bitmap} for {@code key}. The Bitmap is moved to the head of the queue.
         */
        public final boolean put(String key, Bitmap value) {
            if (key == null || value == null) {
                throw new NullPointerException("key == null || value == null");
            }

            synchronized (this) {
                size += sizeOf(key, value);
                Bitmap previous = map.put(key, value);
                if (previous != null) {
                    size -= sizeOf(key, previous);
                    previous.recycle();
                }
            }

            trimToSize(maxSize);
            return true;
        }

        /**
         * Remove the eldest entries until the total of remaining entries is at or below the requested size.
         *
         * @param maxSize the maximum size of the cache before returning. May be -1 to evict even 0-sized elements.
         */
        private void trimToSize(int maxSize) {
            while (true) {
                String key;
                Bitmap value;
                synchronized (this) {
                    if (size < 0 || (map.isEmpty() && size != 0)) {
                        throw new IllegalStateException(getClass().getName() + ".sizeOf() is reporting inconsistent results!");
                    }

                    if (size <= maxSize || map.isEmpty()) {
                        break;
                    }

                    Map.Entry<String, Bitmap> toEvict = map.entrySet().iterator().next();
                    if (toEvict == null) {
                        break;
                    }
                    key = toEvict.getKey();
                    value = toEvict.getValue();
                    map.remove(key);
                    size -= sizeOf(key, value);
                    value.recycle();
                }
            }
        }

        /**
         * Removes the entry for {@code key} if it exists.
         */
        public final void remove(String key) {
            if (key == null) {
                throw new NullPointerException("key == null");
            }

            synchronized (this) {
                Bitmap previous = map.remove(key);
                if (previous != null) {
                    size -= sizeOf(key, previous);
                    previous.recycle();
                }
            }
        }

        public Collection<String> keys() {
            synchronized (this) {
                return new HashSet<String>(map.keySet());
            }
        }

        public void clear() {
            trimToSize(-1); // -1 will evict 0-sized elements
        }

        /**
         * Returns the size {@code Bitmap} in bytes.
         * <p/>
         * An entry's size must not change while it is in the cache.
         */
        private int sizeOf(String key, Bitmap value) {
            return value.getRowBytes() * value.getHeight();
        }

        public synchronized final String toString() {
            return String.format("LruCache[maxSize=%d]", maxSize);
        }
    }
}
