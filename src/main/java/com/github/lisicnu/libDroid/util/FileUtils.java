package com.github.lisicnu.libDroid.util;

import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;

/**
 * <p/>
 * <p/>
 * Author: Eden Lee<p/>
 * Date: 2014/11/24 <p/>
 * Email: checkway@outlook.com <p/>
 * Version: 1.0 <p/>
 */
public final class FileUtils {

    public static final int CREATE_NEW_FILE_SUCCESS = 0;
    public static final int CREATE_NEW_FILE_FAILED = -1;
    public static final int CREATE_NEW_FILE_NO_FREESPACE = 1;
    private static final String TAG = FileUtils.class.getSimpleName();

    /**
     * delete file.
     *
     * @param path
     */
    public static void delete(String path) {
        delete(path, false);
    }

    /**
     * delete file or folder.
     *
     * @param path
     * @param deleteSubDir default value false.
     */
    public static void delete(String path, boolean deleteSubDir) {
        delete(new File(path), deleteSubDir);
    }

    /**
     * @param file
     * @param deleteSubDir
     * @param ignoreCase   this param must use with fileExt
     * @param fileExt      file extension to delete.
     */
    public static void delete(File file, boolean deleteSubDir, boolean ignoreCase, String...
            fileExt) {

        if (file.exists()) {
            if (file.isFile()) {
                if (isTargetValid(file, ignoreCase, fileExt))
                    file.delete();

            } else if (file.isDirectory()) {
                if (!deleteSubDir) {
                    return;
                }

                File files[] = file.listFiles();
                if (files != null && files.length != 0) {
                    for (int i = 0; i < files.length; i++) {
                        delete(files[i], true, ignoreCase, fileExt);
                    }
                }

                file.delete();
            } else {
                if (isTargetValid(file, ignoreCase, fileExt)) {
                    file.delete();
                }
            }
        }
    }

    /**
     * 是否是符合特殊的文件格式, 如果 fi 或者 fileExt 是null, 空, 将会直接返回TRUE.
     *
     * @param fi
     * @param ignoreCase
     * @param fileExt
     * @return
     */
    private static boolean isTargetValid(File fi, boolean ignoreCase, String... fileExt) {
        if (fi == null || fileExt == null || fileExt.length == 0)
            return true;

        String ext = getExtension(fi);

        for (String str : fileExt) {
            if (ignoreCase ? ext.equalsIgnoreCase(str) : ext.equals(str))
                return true;
        }

        return false;
    }

    /**
     * delete file or folder.
     *
     * @param file
     * @param deleteSubDir
     */
    public static void delete(File file, boolean deleteSubDir) {
        delete(file, deleteSubDir, false);
    }

    /**
     * remove [`~!@#$%^&*+=|{}':;',\\[\\].<>/?~！ @#￥%……&*——+|{}【】‘；：”“’。，、？]
     * 两个括号君不移除
     *
     * @param tmp
     * @return
     */
    public static String removeInvalidSeprator(String tmp) {
        if (tmp != null && !tmp.isEmpty()) {
            return tmp.replaceAll("[`~!@#$%^&*+=|{}':;',\\[\\]<>/?~！ @#￥%……&*——+|{}【】‘；：”“’。，、？]",
                    "_").replaceAll("__", "_");
        }
        return tmp;
    }

    public static String getFileNameWithoutExtension(String fileName) {

        if (fileName == null)
            return null;
        if (fileName.isEmpty())
            return "";

        String result = fileName;
        int idx = fileName.lastIndexOf(File.separator);
        if (idx != -1) {
            result = result.substring(idx + 1);
        }
        idx = result.lastIndexOf(".");
        if (idx != -1) {
            result = result.substring(0, idx);
        }
        return result;
    }

    /**
     * 获取路径中的文件名, 如果有后缀, 则包含后缀
     *
     * @param fileName
     * @return
     */
    public static String getFileName(String fileName) {

        if (fileName == null)
            return null;
        if (fileName.isEmpty())
            return "";

        String result = fileName;
        int idx = fileName.lastIndexOf(File.separator);
        if (idx != -1) {
            result = result.substring(idx + 1);
        }
        return result;
    }

    public static String getExtension(File file) {
        if (file == null)
            return null;

        return getExtension(file.getPath());
    }

    /**
     * 获取文件后缀名, 包含 .
     *
     * @param fileName
     * @return
     */
    public static String getExtension(String fileName) {
        if (fileName == null)
            return null;
        if (fileName.isEmpty())
            return "";

        int idx = fileName.lastIndexOf(".");
        if (idx != -1) {
            return fileName.substring(idx);
        } else {
            return "";
        }
    }

    /**
     * @param file
     * @param fileSize
     * @return 0 创建成功, 并且分配文件大小成功, -1 创建失败, 1 创建成功, 但是分配文件大小失败.
     */
    public static int createNewFile(File file, long fileSize) {
        if (fileSize < 0) {
            Log.e(TAG, "createNewFile invalid fileSize=" + fileSize);
            return CREATE_NEW_FILE_FAILED;
        }

        int retVal = CREATE_NEW_FILE_SUCCESS;

        try {
            String absName = file.getAbsolutePath();
            int idx = absName.lastIndexOf(File.separatorChar);
            if (idx != -1) {
                File tmp = new File(absName.substring(0, idx));
                tmp.mkdirs();
                if (tmp.exists() && tmp.getFreeSpace() < fileSize) {
                    retVal = CREATE_NEW_FILE_NO_FREESPACE;
                    return retVal;
                }
                tmp = null;
            }

            long t1 = System.currentTimeMillis();

            long createdSize = 0;
            RandomAccessFile rnd = null;
            FileChannel fc = null;

            try {
                file.createNewFile();
                rnd = new RandomAccessFile(file, "rw");
                fc = rnd.getChannel();
                long size = 4 * 1024 * 1024; // 4M
                while (true) {
                    if (createdSize > fileSize) {
                        createdSize = fileSize;
                    }

                    if (createdSize + size > fileSize) {
                        size = fileSize - createdSize;
                    }

                    fc.map(FileChannel.MapMode.READ_WRITE, createdSize, size);

                    if (createdSize >= fileSize)
                        break;

                    createdSize += size;

                    Thread.sleep(40); // 防止创建文件太大, 导致磁盘卡死, 100ms
                }
            } catch (Exception e) {
                Log.e(TAG, "FileChannal to create new file failed: ", e);
                if (file.length() < fileSize) {
                    try {
                        file.createNewFile();
                    } catch (Exception xe) {
                    }
                }
            } finally {
                if (fc != null)
                    fc.close();
                if (rnd != null) {
                    try {
                        rnd.close();
                    } catch (Exception e2) {
                    }
                }
            }

            boolean ret = file.exists();
            if (ret) {
                ret = file.length() == fileSize;
                if (ret) {
                    retVal = CREATE_NEW_FILE_SUCCESS;
                } else {
                    retVal = CREATE_NEW_FILE_NO_FREESPACE;
                    file.delete();
                }
            } else {
                retVal = CREATE_NEW_FILE_FAILED;
            }
        } catch (Exception e) {
            Log.e(TAG, new StringBuilder("create file error.").append(file.getAbsolutePath())
                    .append(": ").append(e.toString()).toString());

            retVal = CREATE_NEW_FILE_FAILED;
        }
        return retVal;
    }

    public static boolean copy(String srcFile, String dstFile) {
        return copy(new File(srcFile), new File(dstFile));
    }

    public static boolean copy(String srcFile, File dstFile) {
        return copy(new File(srcFile), dstFile);
    }

    public static boolean copy(File srcFile, String dstFile) {
        return copy(srcFile, new File(dstFile));
    }

    public static boolean copy(File srcFile, File dstFile) {
        if (srcFile == null || dstFile == null || !srcFile.exists()) {
            return false;
        }

        InputStream in = null;
        try {
            in = new FileInputStream(srcFile);
            return copy(in, dstFile);
        } catch (FileNotFoundException e) {
            return false;
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            in = null;
        }
    }

    public static boolean createFolder(String folder) {
        if (StringUtils.isNullOrEmpty(folder))
            return false;

        try {
            File fi = new File(folder);
            if (!fi.exists())
                fi.mkdirs();
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public static boolean createFolder(File folder) {
        if (folder == null)
            return false;

        try {
            if (!folder.exists())
                folder.mkdirs();
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     * 拷贝文件, 不关闭流
     *
     * @param in
     * @param dstFile
     * @return
     */
    public static boolean copy(InputStream in, File dstFile) {
        if (in == null || dstFile == null) {
            return false;
        }

        OutputStream out = null;
        boolean result = false;

        try {
            createFolder(dstFile.getParentFile());

            out = new FileOutputStream(dstFile);

            byte[] buffer = new byte[10240];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            buffer = null;
            result = true;
        } catch (IOException e) {
            Log.e(TAG, "", e);
        } finally {
            if (out != null) {
                try {
                    out.flush();
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            out = null;
        }
        return result;
    }
}
