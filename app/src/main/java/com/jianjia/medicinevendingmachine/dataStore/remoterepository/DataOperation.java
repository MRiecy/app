package com.jianjia.medicinevendingmachine.dataStore.remoterepository;

import static com.jianjia.medicinevendingmachine.dataStore.remoterepository.SocketCmdCode.CMD_CODE_END;
import static com.jianjia.medicinevendingmachine.dataStore.remoterepository.SocketCmdCode.CMD_CODE_RED_HEAD;
import static com.jianjia.medicinevendingmachine.dataStore.remoterepository.SocketCmdCode.CMD_CODE_SEND_HEAD;
import static com.jianjia.medicinevendingmachine.dataStore.remoterepository.SocketCmdCode.DEV_TYPE_MEDICINE_CABINET;
import static com.jianjia.medicinevendingmachine.dataStore.remoterepository.SocketCmdCode.PACKAGE_BYTE_LEN;

import androidx.annotation.NonNull;

import com.elvishew.xlog.XLog;
import com.jianjia.medicinevendingmachine.utils.BytesUtils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * 协议规则
 */
public class DataOperation {
    private static final String TAG = "DataOperation";
    /**
     * 设备类型
     */
    private static final byte[] devType = DEV_TYPE_MEDICINE_CABINET;

    private DataOperation() {
    }

    /**
     * 获取组合的发送数据
     * 指令，不携带数据
     * 报文格式：包头（2字节）+包长（4字节）+设备类型（1字节）+命令码（2字节）+参数数据（变长）+结尾符号（1字节）
     *
     * @param cmdCode 命令码
     * @return 自定义协议结构数据
     */
    @NonNull
    public static byte[] GetSocketSendData(@NonNull byte[] cmdCode) {
        //根据服务器的解析规则,构建byte数组
        int dataLen = CMD_CODE_SEND_HEAD.length + PACKAGE_BYTE_LEN + devType.length + cmdCode.length + CMD_CODE_END.length;
        ByteBuffer bb = ByteBuffer.allocate(dataLen);
        bb.order(ByteOrder.BIG_ENDIAN);
        bb.put(CMD_CODE_SEND_HEAD);
        bb.put(BytesUtils.intToBytesB(dataLen));
        bb.put(devType);
        bb.put(cmdCode);
        bb.put(CMD_CODE_END);
        return bb.array();
    }

    /**
     * 获取组合的发送数据
     * 报文格式：包头（2字节）+包长（4字节）+设备类型（1字节）+命令码（2字节）+参数数据（变长）+结尾符号（1字节）
     *
     * @param cmdCode 命令码
     * @param cmdBody 数据内容
     * @return 自定义协议结构数据
     */
    @NonNull
    static byte[] GetSocketSendData(@NonNull byte[] cmdCode, @NonNull String[] cmdBody) {
        //根据服务器的解析规则,构建byte数组
        int bodyLen = 0;
        for (String s : cmdBody) {
            bodyLen += s.length();
        }
        ByteBuffer body = ByteBuffer.allocate(bodyLen);
        for (String s : cmdBody) {
            body.put(s.getBytes());
        }
        int dataLen = CMD_CODE_SEND_HEAD.length + PACKAGE_BYTE_LEN + devType.length + cmdCode.length + body.array().length + CMD_CODE_END.length;
        ByteBuffer bb = ByteBuffer.allocate(dataLen);
        bb.order(ByteOrder.BIG_ENDIAN);
        bb.put(CMD_CODE_SEND_HEAD);
        bb.put(BytesUtils.intToBytesB(dataLen));
        bb.put(devType);
        bb.put(cmdCode);
        bb.put(body.array());
        bb.put(CMD_CODE_END);
        return bb.array();
    }

    /**
     * 对收到的数据包进行验证
     *
     * @param data 需要验证的数据
     * @return true 验证通过 false 数据不合法
     */
    public static boolean DisposeSocketRedData(@NonNull byte[] data) {
        //验证包头包尾
        if (data[0] == CMD_CODE_RED_HEAD[0] && data[1] == CMD_CODE_RED_HEAD[1] && data[data.length - 1] == CMD_CODE_END[0]) {
            byte[] len = new byte[PACKAGE_BYTE_LEN];
            System.arraycopy(data, CMD_CODE_RED_HEAD.length, len, 0, len.length);
            //验证包长
            if (BytesUtils.bytesToIntB(len) == data.length) {
                //验证设备码
                if (data[6] == devType[0]) {
                    return true;
                } else {
                     XLog.tag(TAG).i( "DisposeSocketRedData: 数据设备验证失败");
                }
            } else {
                 XLog.tag(TAG).i( "DisposeSocketRedData: 数据包长验证失败");
            }
        } else {
             XLog.tag(TAG).i( "DisposeSocketRedData: 数据包验证失败");
        }
        return false;
    }
}
