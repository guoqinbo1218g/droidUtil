package com.github.lisicnu.libDroid.helper;

import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.Properties;

/**
 * <p/>
 * <p/>
 * Author: Eden Lee<p/>
 * Date: 2014/11/24 <p/>
 * Email: checkway@outlook.com <p/>
 * Version: 1.0 <p/>
 */
public final class INIHelper {
    final static String TAG = INIHelper.class.getSimpleName();
    File file = null;
    private Properties ini = new Properties();

    public INIHelper(String fileName) {
        file = new File(fileName);

        try {
            if (file.isFile() && !file.exists())
                file.createNewFile();
        } catch (Exception e) {
            Log.e(TAG, "", e);
        }

        try {
            ini.load(new FileInputStream(file));
        } catch (IOException e) {
            Log.e(TAG, "", e);
        }

    }

    /**
     * @param key
     * @return if key not exist , return null.
     */
    public String getKey(String key) {
        if (!ini.containsKey(key)) {
            return null;
        }
        return ini.get(key).toString();
    }

    /**
     * Add or modify
     */
    public void setKey(String key, String value) {
        ini.put(key, value);
    }

    /**
     * save the file.
     */
    public void save() {
        FileWriter fw = null;
        BufferedWriter bw = null;
        try {
            fw = new FileWriter(file);
            bw = new BufferedWriter(fw);
            for (Iterator<Object> iterator = ini.keySet().iterator(); iterator.hasNext(); ) {
                String key = iterator.next().toString();
                bw.write(key + "=" + getKey(key));
                bw.newLine();
            }
            bw.close();
            bw = null;
            fw.close();
            fw = null;
        } catch (Exception ex) {
            Log.e(TAG, "", ex);
        } finally {
            if (bw != null) {
                try {
                    bw.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                bw = null;
            }
            if (fw != null) {
                try {
                    fw.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                fw = null;
            }

        }
    }
}
