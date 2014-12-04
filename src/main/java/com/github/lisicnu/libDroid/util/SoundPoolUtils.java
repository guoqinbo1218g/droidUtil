package com.github.lisicnu.libDroid.util;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;

import com.github.lisicnu.log4android.LogManager;

import java.util.HashMap;
import java.util.Iterator;

/**
 * <p/>
 * <p/>
 * Author: Eden Lee<p/>
 * Date: 2014/11/24 <p/>
 * Email: checkway@outlook.com <p/>
 * Version: 1.0 <p/>
 */
public final class SoundPoolUtils {

    final static String TAG = "SoundPool";

    static SoundPoolUtils pool = new SoundPoolUtils();
    final Object locker = new Object();
    int lastSoundId;
    private SoundPool soundPool;
    private HashMap<Integer, Integer> soundMap;

    private SoundPoolUtils() {
        init();
    }

    /**
     * @return return max = 256 streams.
     */
    public static synchronized SoundPoolUtils getInstance() {
        if (pool == null) {
            pool = new SoundPoolUtils();
        }
        return pool;
    }

    /**
     * release all resource. after called this method, if want to use again,
     * call {@link #init()} first.
     */
    public void recycle() {
        try {
            recycleSoundMap();
            if (soundPool != null) {
                soundPool.release();
                soundPool = null;
            }

        } catch (Exception ex) {
            LogManager.e(getClass().getName(), ex.toString());
        }
    }

    private void recycleSoundMap() {
        if (soundMap != null && soundMap.size() > 0) {
            synchronized (locker) {
                for (Iterator<Integer> ite = soundMap.keySet().iterator(); ite.hasNext(); ) {
                    if (soundPool != null)
                        soundPool.unload(soundMap.get(ite.next()));
                }
                soundMap.clear();
            }
        }
    }

    /**
     * 初始化音效播放器
     */
    private void init() {
        if (soundPool == null)
            soundPool = new SoundPool(256, AudioManager.STREAM_MUSIC, 100);

        synchronized (locker) {
            if (soundMap != null) {
                soundMap.clear();
            } else {
                soundMap = new HashMap<Integer, Integer>();
            }
        }
    }

    public void playSound(int resId) {
        Integer soundId = soundMap.get(resId);
        if (soundId != null) {
            soundPool.pause(lastSoundId);
            soundPool.play(soundId, 1, 1, 1, 0, 1);
            lastSoundId = soundId;
        } else {
            LogManager.e(TAG, "Play sound: resource not found in soundMap");
        }
    }

    /**
     * After call this method, this will unload all of resource has been settled
     * before.<br/>
     * <p/>
     * <b>Note:</b>This may takes a long time.
     *
     * @param resources
     */
    public void updateSound(Context context, int... resources) {
        updateSound(context, true, resources);
    }

    private void updateSound(Context context, boolean clearBefore, int... resources) {
        if (clearBefore) {
            recycleSoundMap();
        }
        synchronized (locker) {
            for (int resource : resources) {
                soundMap.put(resource, soundPool.load(context, resource, 1));
            }
        }
    }

    /**
     * <b>Note:</b>This may takes a long time.
     *
     * @param resources
     */
    public void addSound(Context context, int... resources) {
        updateSound(context, false, resources);
    }

}
