package com.jianjia.medicinevendingmachine.dataStore.remoterepository;

import androidx.annotation.NonNull;

import com.xuhao.didi.core.iocore.interfaces.ISendable;

/**
 * 发送数据的实体类
 */
public class Data implements ISendable {
    private String[] content;
    private final byte[] cmdCode;

    /**
     * 传入的指令
     *
     * @param cmdCode 传入的指令
     */
    public Data(byte[] cmdCode) {
        this.cmdCode = cmdCode;
    }

    /**
     * 传入指令和信息
     *
     * @param cmdCode 指令
     * @param content 信息
     */
    public Data(byte[] cmdCode, String[] content) {
        this.cmdCode = cmdCode;
        this.content = content;
    }

    @NonNull
    @Override
    public byte[] parse() {
        //根据服务器的解析规则,构建byte数组
        if (content != null && content.length > 0) {
            return DataOperation.GetSocketSendData(cmdCode, content);
        } else {
            return DataOperation.GetSocketSendData(cmdCode);
        }
    }
}
