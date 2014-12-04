package com.github.lisicnu.libDroid.helper;

import com.github.lisicnu.libDroid.util.MiscUtils;
import com.github.lisicnu.log4android.LogManager;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import de.innosystec.unrar.Archive;
import de.innosystec.unrar.rarfile.FileHeader;

/**
 * <p/>
 * <p/>
 * Author: Eden Lee<p/>
 * Date: 2014/11/24 <p/>
 * Email: checkway@outlook.com <p/>
 * Version: 1.0 <p/>
 */
public final class Decompression {

    final static String TAG = Decompression.class.getSimpleName();

    /**
     * unrar .rar files.
     *
     * @param files if not need unrar files, pass null
     */
    public static synchronized boolean unrar(String sourceRar, String destDir, List<String> files)
            throws Exception {
        Archive a = null;
        boolean ret = false;
        LogManager.i(TAG, "start");
        FileOutputStream fos = null;
        try {
            // a = new Archive(new File(sourceRar), null, true);
            // 构建测解压缩类
            a = new Archive(new File(sourceRar));
            FileHeader fh = a.nextFileHeader();
            while (fh != null) {
                if (!fh.isDirectory()) {
                    // 1 根据不同的操作系统拿到相应的 destDirName 和 destFileName
                    String compressFileName = fh.getFileNameW().trim();
                    if (!MiscUtils.findChinese(compressFileName)) {
                        compressFileName = fh.getFileNameString().trim();
                    }

                    String destFileName = "";
                    String destDirName = "";
                    // 非windows系统
                    if (File.separator.equals("/")) {
                        destFileName = destDir + compressFileName.replaceAll("\\\\", "/");
                        destDirName = destFileName.substring(0, destFileName.lastIndexOf("/"));
                        // windows系统
                    } else {
                        destFileName = destDir + compressFileName.replaceAll("/", "\\\\");
                        destDirName = destFileName.substring(0, destFileName.lastIndexOf("\\"));
                    }
                    // 2创建文件夹
                    File dir = new File(destDirName);
                    if (!dir.exists() || !dir.isDirectory()) {
                        dir.mkdirs();
                    }
                    // 3解压缩文件
                    fos = new FileOutputStream(new File(destFileName));
                    a.extractFile(fh, fos);
                    fos.close();
                    fos = null;
                    if (files != null) {
                        files.add(destFileName);
                    }
                }
                fh = a.nextFileHeader();
            }
            a.close();
            a = null;
            ret = true;
            LogManager.i(TAG, "success: " + sourceRar);
        } catch (Exception e) {
            LogManager.e(TAG, "error:" + e.toString());
            throw e;
        } finally {

            if (fos != null) {
                try {
                    fos.close();
                    fos = null;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (a != null) {
                try {
                    a.close();
                    a = null;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return ret;
    }

    /**
     * with "."
     *
     * @param fileName
     * @return 如果出错将返回null, 如果里面没有文件,将返回空
     */
    public static String getFirstFileSuffix(String fileName) {
        String suffix = "";
        try {

            ZipFile zipFile = new ZipFile(fileName);
            Enumeration<?> zipEnum = zipFile.entries();

            if (zipEnum.hasMoreElements()) {
                String tmp = ((ZipEntry) zipEnum.nextElement()).getName();
                int idx = tmp.lastIndexOf(".");
                if (idx >= 0) {
                    suffix = tmp.substring(idx);
                }
            }
        } catch (Exception e) {
            LogManager.e(TAG + "-GetExt:", e);
            suffix = null;
        }
        return suffix;
    }

    /**
     * decompression first file.
     *
     * @param fileName
     * @param destName
     * @return
     * @throws Exception
     */
    public static boolean deCompressFirst(String fileName, String destName) throws Exception {

        if (fileName == null || fileName.isEmpty())
            throw new NullPointerException("decompress file can't be null.");

        int idx = fileName.lastIndexOf(".");
        if (idx == -1) {
            throw new Exception("decompress target must be a file with extension.");
        }
        String type = fileName.substring(idx + 1).toLowerCase();
        // if (type.equals("zip")) {
        // result = unZipFiles(fileName, destDir, files);
        // } else if (type.equals("rar")) {
        // result = unrar(sourceFile, destDir, files);
        // } else {
        // throw new Exception("只支持zip和rar格式的压缩包！");
        // }
        boolean result;
        if (type.equals("rar")) {
            result = unrarFirst(fileName, destName);
        } else if (type.equals("zip")) {
            result = unZipFirst(fileName, destName);
        } else {
            throw new Exception("Only supported for rar and zip files.");
        }
        return result;
    }

    /**
     * unrar first file.
     *
     * @param sourceRar
     * @param destName
     * @return
     * @throws Exception
     */
    public static synchronized boolean unrarFirst(String sourceRar, String destName)
            throws Exception {
        Archive a = null;
        boolean ret = false;
        LogManager.e(TAG, "start");
        FileOutputStream fos = null;

        String destDir = destName.substring(0, destName.lastIndexOf(File.separator) + 1);
        try {
            new File(destDir).mkdirs();

            // a = new Archive(new File(sourceRar), null, true);
            // 构建测解压缩类
            a = new Archive(new File(sourceRar));
            FileHeader fh = a.nextFileHeader();
            ArrayList<String> files = new ArrayList<String>();
            while (fh != null) {
                if (!fh.isDirectory()) {
                    // 1 根据不同的操作系统拿到相应的 destDirName 和 destFileName
                    String compressFileName = fh.getFileNameW().trim();
                    if (!MiscUtils.findChinese(compressFileName)) {
                        compressFileName = fh.getFileNameString().trim();
                    }

                    String destFileName = "";
                    String destDirName = "";
                    // 非windows系统
                    if (File.separator.equals("/")) {
                        destFileName = destDir + compressFileName.replaceAll("\\\\", "/");
                        destDirName = destFileName.substring(0, destFileName.lastIndexOf("/"));
                        // windows系统
                    } else {
                        destFileName = destDir + compressFileName.replaceAll("/", "\\\\");
                        destDirName = destFileName.substring(0, destFileName.lastIndexOf("\\"));
                    }
                    // 2创建文件夹
                    File dir = new File(destDirName);
                    if (!dir.exists() || !dir.isDirectory()) {
                        dir.mkdirs();
                    }
                    // 3解压缩文件
                    fos = new FileOutputStream(new File(destFileName));
                    a.extractFile(fh, fos);
                    fos.close();
                    fos = null;
                    if (files != null) {
                        files.add(destFileName);
                    }
                }
                fh = a.nextFileHeader();
            }
            a.close();
            a = null;
            ret = true;

            LogManager.e(TAG + "-RAR", "success: " + sourceRar);
            if (files.size() > 0) {
                new File(files.get(0)).renameTo(new File(destName));
            }
        } catch (Exception e) {
            LogManager.e(TAG, "error:" + e.toString());
            throw e;
        } finally {

            if (fos != null) {
                try {
                    fos.close();
                    fos = null;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (a != null) {
                try {
                    a.close();
                    a = null;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return ret;
    }

    /**
     * unzip first file.
     *
     * @param zipFile
     * @param destName
     * @return
     */
    public static synchronized boolean unZipFirst(String zipFile, String destName) {

        boolean ret = false;
        try {

            ZipFile zip = new ZipFile(zipFile);
            Enumeration<?> zipEnum = zip.entries();
            if (zipEnum.hasMoreElements()) {
                ZipEntry entry = (ZipEntry) zipEnum.nextElement();
                InputStream in = zip.getInputStream(entry);

                new File(destName).createNewFile();

                OutputStream out = new FileOutputStream(destName);
                byte buffer[] = new byte[1024 * 2];
                int realLength;
                while ((realLength = in.read(buffer)) > 0) {
                    out.write(buffer, 0, realLength);
                }

                try {
                    in.close();
                    in = null;
                } catch (Exception e) {
                    // TODO: handle exception
                }
                try {
                    out.close();
                    out = null;
                } catch (Exception e) {
                    // TODO: handle exception
                }

            }

            ret = true;
        } catch (Exception e) {
            LogManager.e(TAG + "-unZIP", e.toString());
        }
        return ret;
    }

    /**
     * 如果名字为null或者空, 或者传入的是一个目录, 或者文件不存在或者 文件格式不是zip, rar , 或者打开文件失败, 将返回null.
     * Note: 中文可能返回乱码.
     *
     * @param fileName
     * @return
     */
    public static List<String> getFileList(String fileName) {

        // if (true) {
        // try {
        //
        // ZipFile zipFile = new ZipFile(fileName);
        // Enumeration zipEnum = zipFile.entries();
        //
        // while (zipEnum.hasMoreElements()) {
        // ZipEntry entry = (ZipEntry) zipEnum.nextElement();
        //
        // String str = new String(entry.getName().getBytes("gb2312"),
        // "8859_1");
        // System.out.println(str);
        // }
        //
        // } catch (Exception e) {
        // LogManager.e(TAG + "-testGetFileList", e);
        // }
        //
        // return null;
        // }

        if (fileName == null || fileName.isEmpty())
            return null;

        File file = new File(fileName);
        if (file.isDirectory() || !file.exists())
            return null;

        fileName = fileName.toLowerCase();
        if (!fileName.endsWith(".zip") && !fileName.endsWith(".rar")) {
            return null;
        }
        Archive a = null;
        FileOutputStream fos = null;
        List<String> files = null;
        ZipInputStream zis = null;
        try {
            String dir = fileName.substring(0, fileName.lastIndexOf(File.separator) + 1);
            files = new ArrayList<String>();

            if (fileName.endsWith(".zip")) {
                zis = new ZipInputStream(new BufferedInputStream(new FileInputStream(file)));

                ZipEntry entry;
                while ((entry = zis.getNextEntry()) != null) {
                    if (!entry.isDirectory()) {
                        files.add(dir + File.separator + entry.getName());
                    }
                }
            } else if (fileName.endsWith(".rar")) {

                a = new Archive(file);
                FileHeader fh = a.nextFileHeader();
                while (fh != null) {
                    if (!fh.isDirectory()) {
                        // 1 根据不同的操作系统拿到相应的 destDirName 和 destFileName
                        String compressFileName = fh.getFileNameW().trim();

                        if (!MiscUtils.findChinese(compressFileName)) {
                            compressFileName = fh.getFileNameString().trim();
                        }

                        String destFileName = "";
                        // 非windows系统
                        if (File.separator.equals("/")) {
                            destFileName = dir + compressFileName.replaceAll("\\\\", "/");
                            // windows系统
                        } else {
                            destFileName = dir + compressFileName.replaceAll("/", "\\\\");
                        }

                        if (files != null) {
                            files.add(destFileName);
                        }
                    }
                    fh = a.nextFileHeader();
                }
                a.close();
                a = null;
            }
        } catch (Exception e) {
            LogManager.e(TAG + "-GetFiles", e);
            return null;
        } finally {
            if (zis != null) {
                try {
                    zis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                zis = null;
            }
            if (fos != null) {
                try {
                    fos.close();
                    fos = null;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (a != null) {
                try {
                    a.close();
                    a = null;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return files;

    }

    /**
     * used java.util.zip<br/>
     * to unzip files.
     *
     * @param zipFile    file has been compressed.
     * @param destFolder target decompression folder.
     * @param files      file list has been decompressioned.
     * @return
     */
    public static synchronized boolean unZipFiles(String zipFile, String destFolder,
                                                  List<String> files) {

        boolean ret = false;
        try {

            BufferedOutputStream dest = null;
            ZipInputStream zis = new ZipInputStream(new BufferedInputStream(new FileInputStream(
                    zipFile)));
            int BUFFER = 2048;
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                LogManager.d(TAG, "Extracting: " + entry.getName());
                int count;
                byte data[] = new byte[BUFFER];

                if (entry.isDirectory()) {
                    new File(destFolder + "/" + entry.getName()).mkdirs();
                    continue;
                } else {
                    int di = entry.getName().lastIndexOf('/');
                    if (di != -1) {
                        new File(destFolder + "/" + entry.getName().substring(0, di)).mkdirs();
                    }
                }

                FileOutputStream fos = new FileOutputStream(destFolder + "/" + entry.getName());
                dest = new BufferedOutputStream(fos);

                while ((count = zis.read(data)) != -1) {
                    dest.write(data, 0, count);
                }

                dest.flush();
                dest.close();
                if (files != null) {
                    files.add(destFolder + "/" + entry.getName());
                }
            }
            ret = true;
        } catch (Exception e) {
            LogManager.e(TAG + "-unZIP", e);
        }
        return ret;
    }

    /**
     * 解压缩
     */
    public static boolean deCompress(String sourceFile, String destDir, List<String> files)
            throws Exception {
        // 保证文件夹路径最后是"/"或者"\"

        char lastChar = destDir.charAt(destDir.length() - 1);
        if (lastChar != '/' && lastChar != '\\') {
            destDir += File.separator;
        }
        boolean result = false;
        // 根据类型，进行相应的解压缩
        String type = sourceFile.substring(sourceFile.lastIndexOf(".") + 1);
        if (type.equalsIgnoreCase("zip")) {
            result = unZipFiles(sourceFile, destDir, files);
        } else if (type.equalsIgnoreCase("rar")) {
            result = unrar(sourceFile, destDir, files);
        } else {
            throw new Exception("只支持zip和rar格式的压缩包！");
        }
        return result;
    }
}
