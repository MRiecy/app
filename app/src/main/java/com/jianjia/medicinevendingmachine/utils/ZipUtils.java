package com.jianjia.medicinevendingmachine.utils;

import android.util.Log;

import androidx.annotation.NonNull;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class ZipUtils {
    private static final String TAG = "ZipUtils";

    private ZipUtils() {
    }

    public static boolean zipFile(@NonNull String srcFilePath, @NonNull String zipPath, boolean isKeepDirStructure) {
        if (srcFilePath == null || ("").equals(srcFilePath) || zipPath == null || ("").equals(zipPath)) {
            Log.i(TAG, "zipFile源文件或压缩文件path为空");
            return false;
        }
        Log.i(TAG, "zipFile源文件path：" + srcFilePath);
        File srcFile = new File(srcFilePath);
        ZipOutputStream zipOutputStream = null;
        boolean isValid = checkFileIsValid(srcFile);
        Log.i(TAG, "是否为有效文件：" + isValid);
        if (isValid) {
            String outFileName;
            if (zipPath.endsWith(File.separator)) {
                outFileName = zipPath.substring(0, zipPath.lastIndexOf(File.separator)) + ".zip";
            } else if (!zipPath.endsWith(".zip")) {
                outFileName = zipPath + ".zip";
            } else {
                outFileName = zipPath;
            }
            Log.i(TAG, "zipFile压缩文件path：" + outFileName);
            try {
                zipOutputStream = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(outFileName)));
                return compress(srcFile, zipOutputStream, srcFile.getName(), isKeepDirStructure);
            } catch (FileNotFoundException e) {
                Log.i(TAG, "zipFileFileNotFoundException错误：" + e.getMessage());
            } catch (IOException ex) {
                Log.i(TAG, "zipFileIOException错误：" + ex.getMessage());
            } finally {
                if (zipOutputStream != null) {
                    try {
                        zipOutputStream.close();
                    } catch (IOException ex) {
                        Log.i(TAG, " zipOutputStream.close()错误：" + ex.getMessage());
                    }
                }
            }
        } else {
            Log.i(TAG, "无有效文件");
        }
        return false;
    }

    private static boolean checkFileIsValid(File file) {
        ArrayList<String> lFileList = new ArrayList<>();
        if (!file.exists()) {
            return false;
        } else if (file.isFile() && file.length() <= 0) {
            return false;
        } else if (file.isDirectory()) {
            List<String> lDirectoryFile = getDirectoryFile(file, lFileList);
            return !lDirectoryFile.isEmpty();
        }
        return true;
    }

    private static List<String> getDirectoryFile(File file, List<String> fileList) {
        File[] lFiles = file.listFiles();
        if (lFiles != null) {
            for (File lFile : lFiles) {
                if (lFile.isDirectory()) {
                    getDirectoryFile(lFile, fileList);
                } else {
                    if (lFile.length() > 0) {
                        fileList.add(lFile.getName());
                    }
                }
            }
        }
        return fileList;
    }

    public static boolean zipFiles(List<String> srcFilePathList, String zipPath, boolean isKeepDirStructure) {
        if (zipPath != null && !zipPath.equals("") && srcFilePathList != null && !srcFilePathList.isEmpty()) {
            ZipOutputStream zipOutputStream = null;
            String outFileName;
            if (!zipPath.endsWith(".zip")) {
                outFileName = zipPath + ".zip";
            } else if (zipPath.endsWith(File.separator)) {
                outFileName = zipPath.replace(File.separator, "") + ".zip";
            } else {
                outFileName = zipPath;
            }
            Log.i(TAG, "zipFiles源文件path：" + srcFilePathList.toString());
            Log.i(TAG, "zipFiles压缩文件path：" + outFileName);
            try {
                zipOutputStream = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(outFileName)));
                for (int i = 0; i < srcFilePathList.size(); i++) {
                    File srcFile = new File(srcFilePathList.get(i));
                    Log.i(TAG, "srcFileName是：" + srcFile.getName());
                    if (checkFileIsValid(srcFile)) {
                        compress(srcFile, zipOutputStream, srcFile.getName(), isKeepDirStructure);
                    } else {
                        Log.i(TAG, "源文件不存在或为空");
                    }
                }
                return true;
            } catch (FileNotFoundException e) {
                Log.i(TAG, "错误：" + e.getMessage());
            } catch (IOException ex) {
                Log.i(TAG, "错误：" + ex.getMessage());
            } finally {
                if (zipOutputStream != null) {
                    try {
                        zipOutputStream.finish();
                        zipOutputStream.close();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }
        return false;
    }

    private static boolean compress(File srcFile, ZipOutputStream zipOutputStream, String
            fileName, boolean isKeepDirStructure) throws IOException {
        Log.i(TAG, "文件压缩:" + srcFile.getName() + " " + fileName);
        FileInputStream lFileInputStream = null;
        try {
            if (srcFile.isFile() && srcFile.length() > 0) {
                lFileInputStream = new FileInputStream(srcFile);
                zipOutputStream.putNextEntry(new ZipEntry(fileName));
                byte[] bytes = new byte[1024];
                int len;
                while ((len = lFileInputStream.read(bytes)) != -1) {
                    zipOutputStream.write(bytes, 0, len);
                }
            } else {
                File[] lFiles = srcFile.listFiles();
                if (lFiles != null && lFiles.length > 0) {
                    for (File lFile : lFiles) {
                        String filePath = lFile.getName();
                        if (isKeepDirStructure) {
                            filePath = fileName + File.separator + lFile.getName();
                        }
                        compress(lFile, zipOutputStream, filePath, isKeepDirStructure);
                    }
                }
            }
            zipOutputStream.flush();
            zipOutputStream.closeEntry();
            Log.i(TAG, "文件压缩成功");
            return true;
        } catch (FileNotFoundException e) {
            Log.i(TAG, "compressStream错误：" + e.getMessage());
        } finally {
            if (lFileInputStream != null) {
                try {
                    lFileInputStream.close();
                } catch (IOException e) {
                    Log.i(TAG, "compressCloseStream错误：" + e.getMessage());
                }
            }
        }
        return false;
    }

    public static boolean unZipFile(String zipFilePath, String targetFilePath) {
        if (zipFilePath == null || zipFilePath.equals("") || targetFilePath == null || targetFilePath.equals("")) {
            return false;
        }
        Log.i(TAG, "解压文件地址：" + zipFilePath + "解压地址：" + targetFilePath);
        String unZipFilePath = targetFilePath;
        if (!targetFilePath.endsWith("/")) {
            unZipFilePath = targetFilePath + "/";
        }
        BufferedInputStream lBufferedInputStream = null;
        BufferedOutputStream lBufferedOutputStream = null;
        ZipFile zip = null;
        File zipFile = new File(zipFilePath);
        if (zipFile.exists() && zipFile.length() > 0) {
            try {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                    Log.i(TAG, "解压开始");
                    zip = new ZipFile(zipFile, Charset.forName("gbk"));
                    Enumeration<? extends ZipEntry> lEntries = zip.entries();
                    while (lEntries.hasMoreElements()) {
                        ZipEntry entry = (ZipEntry) lEntries.nextElement();
                        if (entry.isDirectory()) {
                            String name = entry.getName();
                            Log.i(TAG, "ZipEntryName:" + name);
                            File lFile = new File(unZipFilePath + name);
                            if (!lFile.exists()) {
                                lFile.mkdirs();
                            }
                        } else {
                            lBufferedInputStream = new BufferedInputStream(zip.getInputStream(entry));
                            lBufferedOutputStream =
                                    new BufferedOutputStream(new FileOutputStream(unZipFilePath + entry.getName()), 1024);
                            byte[] lBytes = new byte[1024];
                            int len;
                            while ((len = lBufferedInputStream.read(lBytes)) != -1) {
                                lBufferedOutputStream.write(lBytes, 0, len);
                            }
                        }
                    }
                    lBufferedOutputStream.flush();
                    Log.i(TAG, "解压成功");
                    return true;
                } else {
                    ZipInputStream lZipInputStream = new ZipInputStream(new FileInputStream(zipFile));
                    lBufferedInputStream = new BufferedInputStream(lZipInputStream);
                    ZipEntry lZipEntry;
                    while ((lZipEntry = lZipInputStream.getNextEntry()) != null) {
                        if (lZipEntry.isDirectory()) {
                            String name = lZipEntry.getName();
                            File lFile = new File(unZipFilePath + name);
                            if (!lFile.exists()) {
                                lFile.mkdirs();
                            }
                        } else {
                            lBufferedOutputStream =
                                    new BufferedOutputStream(new FileOutputStream(unZipFilePath + lZipEntry.getName()), 1024);
                            byte[] lBytes = new byte[1024];
                            int len;
                            while ((len = lBufferedInputStream.read(lBytes)) != -1) {
                                lBufferedOutputStream.write(lBytes, 0, len);
                            }
                        }
                    }
                    lBufferedOutputStream.flush();
                    Log.i(TAG, "解压成功");
                    return true;
                }
            } catch (FileNotFoundException e) {
                Log.i(TAG, "错误：" + e.getMessage());
            } catch (IOException e) {
                Log.i(TAG, "错误：" + e.getMessage());
            } finally {
                if (lBufferedInputStream != null) {
                    try {
                        lBufferedInputStream.close();
                    } catch (IOException e) {
                        Log.i(TAG, "错误：" + e.getMessage());
                    }
                }
                if (lBufferedOutputStream != null) {
                    try {
                        lBufferedOutputStream.close();
                    } catch (IOException e) {
                        Log.i(TAG, "错误：" + e.getMessage());
                    }
                }
                if (zip != null) {
                    try {
                        zip.close();
                    } catch (IOException e) {
                        Log.i(TAG, "错误：" + e.getMessage());
                    }
                }
            }
        }
        return false;
    }
}
