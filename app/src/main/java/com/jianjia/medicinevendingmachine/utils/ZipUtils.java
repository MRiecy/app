package com.jianjia.medicinevendingmachine.utils;

import com.elvishew.xlog.XLog;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

public class ZipUtils {
    private static final String TAG = "ZipUtils";

    private ZipUtils() {
    }

    public static boolean zipFile(String srcFilePath, String zipPath, boolean isKeepDirStructure) {
        if (srcFilePath == null || "".equals(srcFilePath) || zipPath == null || "".equals(zipPath)) {
            XLog.tag(TAG).i( "zipFile源文件或压缩文件path为空");
            return false;
        }
        XLog.tag(TAG).i( "zipFile源文件path：" + srcFilePath);
        File srcFile = new File(srcFilePath);
        if (srcFile.exists() && srcFile.length() > 0) {
            String outFilePath;
            if (zipPath.endsWith(File.separator)) {
                outFilePath = zipPath.substring(0, zipPath.lastIndexOf(File.separator)) + ".zip";
            } else if (!zipPath.endsWith(".zip")) {
                outFilePath = zipPath + ".zip";
            } else {
                outFilePath = zipPath;
            }
            XLog.tag(TAG).i("zipFile压缩文件path：" + outFilePath);
            ZipOutputStream zipOutputStream = null;
            try {
                zipOutputStream = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(outFilePath)));
                return compressFile(srcFile, zipOutputStream, isKeepDirStructure);
            } catch (FileNotFoundException e) {
                XLog.tag(TAG).i("zipFileFileNotFoundException错误：" + e.getMessage());
            } catch (IOException ex) {
                XLog.tag(TAG).i("zipFileIOException错误：" + ex.getMessage());
            } finally {
                try {
                    if (zipOutputStream != null) {
                        zipOutputStream.close();
                    }
                } catch (IOException ex) {
                    XLog.tag(TAG).i( " zipOutputStream.close()错误：" + ex.getMessage());
                }
            }
        } else {
            XLog.tag(TAG).i( "无有效文件");
        }
        return false;
    }

    private static boolean compressFile(File srcFile, ZipOutputStream zipOutputStream, boolean isKeepDirStructure) throws IOException {
        if (srcFile.isFile()) {
            FileInputStream lFileInputStream = new FileInputStream(srcFile);
            zipOutputStream.putNextEntry(new ZipEntry(srcFile.getName()));
            byte[] bytes = new byte[1024];
            while (true) {
                int lRead = lFileInputStream.read(bytes);
                if (lRead != -1) {
                    zipOutputStream.write(bytes, 0, lRead);
                } else {
                    zipOutputStream.flush();
                    lFileInputStream.close();
                    break;
                }
            }
        } else if (srcFile.isDirectory()) {
            compressDir(srcFile, srcFile.getName(), zipOutputStream, isKeepDirStructure);
        }
        zipOutputStream.closeEntry();
        zipOutputStream.close();
        XLog.tag(TAG).i("文件全部压缩成功");
        return true;
    }

    private static void compressDir(File srcFile, String fileName, ZipOutputStream zipOutputStream,
                                    boolean isKeepDirStructure) throws IOException {
        File[] lFiles = srcFile.listFiles();
        if (lFiles != null && lFiles.length > 0) {
            for (File lFile : lFiles) {
                if (lFile.isFile()) {
                    FileInputStream lFileInputStream = new FileInputStream(lFile);
                    if (isKeepDirStructure) {
                        zipOutputStream.putNextEntry(new ZipEntry(fileName + "/" + lFile.getName()));
                    } else {
                        zipOutputStream.putNextEntry(new ZipEntry(lFile.getName()));
                    }
                    byte[] bytes = new byte[1024];
                    while (true) {
                        int lRead = lFileInputStream.read(bytes);
                        if (lRead != -1) {
                            zipOutputStream.write(bytes, 0, lRead);
                        } else {
                            zipOutputStream.flush();
                            lFileInputStream.close();
                            break;
                        }
                    }
                } else {
                    String filePath = lFile.getName();
                    if (isKeepDirStructure) {
                        filePath = fileName + File.separator + lFile.getName();
                    }
                    compressDir(lFile, filePath, zipOutputStream, isKeepDirStructure);
                }
            }
        }
    }

    public static boolean unZipFile(String zipFilePath, String targetFilePath) {
        if (zipFilePath == null || "".equals(zipFilePath) || targetFilePath == null || "".equals(targetFilePath)) {
            return false;
        }
        XLog.tag(TAG).i("解压文件地址：" + zipFilePath + "解压地址：" + targetFilePath);
        String unZipFilePath = targetFilePath;
        if (!targetFilePath.endsWith(File.separator)) {
            unZipFilePath = targetFilePath + "/";
        }
        try {
            File file = new File(zipFilePath);
            if (file.exists() && file.length() > 0) {
                File outFileDic = new File(targetFilePath);
                if (!outFileDic.exists()) {
                    outFileDic.mkdirs();
                }
                ZipFile zipFile = new ZipFile(zipFilePath);
                Enumeration<? extends ZipEntry> lEntries = zipFile.entries();
                while (lEntries.hasMoreElements()) {
                    ZipEntry lZipEntry = lEntries.nextElement();
                    if (lZipEntry.isDirectory()) {
                        File lFile = new File(unZipFilePath + lZipEntry.getName());
                        if (!lFile.exists()) {
                            lFile.mkdirs();
                        }
                    } else {
                        BufferedInputStream lBufferedInputStream = new BufferedInputStream(zipFile.getInputStream(lZipEntry));
                        File outFile = new File(unZipFilePath + lZipEntry.getName());
                        if (!outFile.exists()) {
                            outFile.createNewFile();
                        }
                        BufferedOutputStream lBufferedOutputStream = new BufferedOutputStream(new FileOutputStream(outFile));
                        byte[] bytes = new byte[1024];
                        int len;
                        while ((len = lBufferedInputStream.read(bytes)) != -1) {
                            lBufferedOutputStream.write(bytes, 0, len);
                        }
                        lBufferedOutputStream.flush();
                        lBufferedOutputStream.close();
                        lBufferedInputStream.close();
                    }
                }
                zipFile.close();
                XLog.tag(TAG).i("解压成功");
                return true;
            } else {
                XLog.tag(TAG).i("压缩文件不存在");
            }
        } catch (IOException e) {
            XLog.tag(TAG).i("解压失败：" + e.getMessage());
        }
        return false;
    }
}
