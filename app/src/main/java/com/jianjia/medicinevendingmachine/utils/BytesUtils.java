package com.jianjia.medicinevendingmachine.utils;

import android.annotation.SuppressLint;
import android.util.Base64;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.nio.charset.StandardCharsets;

/**
 * 字节处理类
 */
public class BytesUtils {
    private BytesUtils() {
    }

    /**
     * 小字节序
     * 将int数值转换为占四个字节的byte数组，本方法适用于(低位在前，高位在后)的顺序。 和bytesToInt（）配套使用
     *
     * @param value 要转换的int值
     * @return byte数组
     */
    @NonNull
    public static byte[] intToByteS(int value) {
        byte[] src = new byte[4];
        src[0] = (byte) (value & 0xFF);
        src[1] = (byte) ((value >> 8) & 0xFF);
        src[2] = (byte) ((value >> 16) & 0xFF);
        src[3] = (byte) ((value >> 24) & 0xFF);
        return src;
    }

    /**
     * 大字节序
     * <p>
     * 将int数值转换为占四个字节的byte数组，本方法适用于(高位在前，低位在后)的顺序。  和bytesToInt2（）配套使用
     */
    @NonNull
    public static byte[] intToBytesB(int value) {
        byte[] src = new byte[4];
        src[0] = (byte) ((value >> 24) & 0xFF);
        src[1] = (byte) ((value >> 16) & 0xFF);
        src[2] = (byte) ((value >> 8) & 0xFF);
        src[3] = (byte) (value & 0xFF);
        return src;
    }


    /**
     * 小字节序
     * byte数组中取int数值，本方法适用于(低位在前，高位在后)的顺序，和和intToBytes（）配套使用
     *
     * @param src byte数组
     * @return int数值
     */
    public static int bytesToIntS(byte[] src) {
        int value;
        value = (src[0] & 0xFF)
                | ((src[1] & 0xFF) << 8)
                | ((src[2] & 0xFF) << 16)
                | ((src[3] & 0xFF) << 24);
        return value;
    }

    /**
     * 大字节序
     * byte数组中取int数值，本方法适用于(低位在后，高位在前)的顺序。和intToBytes2（）配套使用
     */
    public static int bytesToIntB(byte[] src) {
        int value;
        value = ((src[0] & 0xFF) << 24)
                | ((src[1] & 0xFF) << 16)
                | ((src[2] & 0xFF) << 8)
                | (src[3] & 0xFF);
        return value;
    }

    /**
     * 生成打印16进制日志所需的字符串
     *
     * @param src 数据源
     * @return 字符串给日志使用
     */
    @Nullable
    public static String bytesToHexString(@Nullable byte[] src) {
        StringBuilder stringBuilder = new StringBuilder();
        if (src == null || src.length <= 0) {
            return null;
        }
        for (byte b : src) {
            int v = b & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }

    private final static char[] mChars = "0123456789ABCDEF".toCharArray();

    @NonNull
    public static String str2HexStr(@NonNull String str) {
        StringBuilder sb = new StringBuilder();
        byte[] bs = str.getBytes();

        for (byte b : bs) {
            sb.append(mChars[(b & 0xFF) >> 4]);
            sb.append(mChars[b & 0x0F]);
        }
        return sb.toString().trim();
    }

    public static String getBase64(String str) {
        String result = "";
        if( str != null) {
            result = new String(Base64.encode(str.getBytes(StandardCharsets.UTF_8), Base64.NO_WRAP), StandardCharsets.UTF_8);
        }
        return result;
    }

    @NonNull
    public static String SubAndBase64Decode(@NonNull String bodyStr, int startIndex, int endIndex) {
        int bodyLen = Integer.parseInt(bodyStr.substring(startIndex, endIndex));
        String bodyStr1 = bodyStr.substring(endIndex, endIndex + bodyLen);
        String bodyStr2 = bodyStr1.replace("%2B", "+");
        return new String(Base64.decode(bodyStr2, Base64.DEFAULT));
    }

    @SuppressLint("NewApi")
    @NonNull
    public static String SubAndBase64Decode(@NonNull String bodyStr) {
        String bodyStr1 = bodyStr.replace(" ", "+");
        return new String(Base64.decode(bodyStr1, Base64.DEFAULT), StandardCharsets.UTF_8);
    }
}
