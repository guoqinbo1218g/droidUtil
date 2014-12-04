package com.github.lisicnu.libDroid.util;

import android.media.MediaPlayer;

import com.github.lisicnu.log4android.LogManager;

import java.io.FileInputStream;
import java.io.IOException;

/**
 * <p/>
 * <p/>
 * Author: Eden Lee<p/>
 * Date: 2014/11/24 <p/>
 * Email: checkway@outlook.com <p/>
 * Version: 1.0 <p/>
 */
public final class MediaPlayerUtils {
    private final static String TAG = "MediaPlayerUtils";
    private static MediaPlayer player;

    public static void stopPlay() {
        try {
            if (player == null)
                return;

            if (player.isPlaying())
                player.stop();

        } catch (Exception e) {
            LogManager.e(TAG, e.toString());
        }
    }

    /**
     * 播放音频文件
     *
     * @param fileName
     * @param sync     是否同步播放, 如果同步播放则会等到播放结束时再返回, 反之则调用之后就直接返回
     */
    public static void playSound(String fileName, boolean sync) {
        playSound(fileName, 0, -1, sync);
    }

    /**
     * 播放文件中的部分音频
     *
     * @param fileName
     * @param offset
     * @param length   -1 means play all.
     * @param sync     是否同步播放, 如果同步播放则会等到播放结束时再返回, 反之则调用之后就直接返回
     */
    public static void playSound(String fileName, long offset, long length,
                                 boolean sync) {

        FileInputStream fis = null;
        try {
            if (player == null) {
                player = new MediaPlayer();
            }

            player.reset();
            fis = new FileInputStream(fileName);
            int available = fis.available();

            if (available < offset)
                return;

            if (length == -1 || length + offset > available) {
                length = available - offset;
            }

            player.setDataSource(fis.getFD(), offset, length);
            player.prepare();
            player.start();

            if (sync) {
                while (player.isPlaying()) {
                    Thread.sleep(30);
                }
            }

        } catch (Exception e) {
            LogManager.e(TAG, e.toString());
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    public static void recyle() {
        if (player != null) {
            player.stop();
            player.release();
            player = null;
        }
    }
}
