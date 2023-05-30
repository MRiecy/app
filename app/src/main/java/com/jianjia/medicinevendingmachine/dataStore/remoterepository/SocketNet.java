package com.jianjia.medicinevendingmachine.dataStore.remoterepository;

import android.util.Log;

import androidx.annotation.NonNull;

import com.elvishew.xlog.XLog;
import com.jianjia.medicinevendingmachine.utils.BytesUtils;
import com.xuhao.didi.core.iocore.interfaces.IPulseSendable;
import com.xuhao.didi.core.protocol.IReaderProtocol;
import com.xuhao.didi.socket.client.sdk.OkSocket;
import com.xuhao.didi.socket.client.sdk.client.ConnectionInfo;
import com.xuhao.didi.socket.client.sdk.client.OkSocketOptions;
import com.xuhao.didi.socket.client.sdk.client.action.SocketActionAdapter;
import com.xuhao.didi.socket.client.sdk.client.connection.IConnectionManager;

import java.nio.ByteOrder;

import javax.inject.Inject;

public class SocketNet {
    private static final String TAG = "SocketNet";
    private IConnectionManager manager;
    private SocketActionAdapter mSocketActionAdapter;

    @Inject
    public SocketNet() {
        Log.i(TAG, "初始化");
    }

    @NonNull
    public SocketNet setConnectConfiguration(@NonNull String hostAddress, int port) {
        XLog.tag(TAG).i("中转平台IP地址为：" + hostAddress + " " + port);
        ConnectionInfo info = new ConnectionInfo(hostAddress, port);
     /*   SLog.setIsDebug(true);
        OkSocketOptions.setIsDebug(true);*/
        OkSocketOptions lOkSocketOptions = new OkSocketOptions.Builder()
                .setPulseFrequency(10 * 1000)//心跳发送间隔时间
                .setPulseFeedLoseTimes(5)//心跳最大丢失次数
                .setConnectTimeoutSecond(10)
                .setIOThreadMode(OkSocketOptions.IOThreadMode.DUPLEX)
                .setReaderProtocol(new IReaderProtocol() {
                    @Override
                    public int getHeaderLength() {
                        return 9;
                    }

                    @Override
                    public int getBodyLength(@NonNull byte[] header, ByteOrder byteOrder) {
                        byte[] len = new byte[4];
                        System.arraycopy(header, 2, len, 0, len.length);
                        return BytesUtils.bytesToIntB(len) - 9;
                    }
                }).build();
        manager = OkSocket.open(info).option(lOkSocketOptions);
        return this;
    }

    @NonNull
    public SocketNet addListener(SocketActionAdapter socketActionAdapter) {
        this.mSocketActionAdapter = socketActionAdapter;
        if (manager != null) {
            manager.registerReceiver(socketActionAdapter);
        } else {
            XLog.tag(TAG).i("manger为空");
        }
        return this;
    }

    @NonNull
    public SocketNet connect() {
        if (manager != null) {
            manager.connect();
        } else {
            XLog.tag(TAG).i("manger为空");
        }
        return this;
    }

    /***
     * 发送数据
     * @param cmdCode 命令码
     * @param content 数据内容
     */
    public void sendData(byte[] cmdCode, String[] content) {
        if (manager != null) {
            manager.send(new Data(cmdCode, content));
        }
    }

    /***
     * 发送数据
     * @param cmdCode 命令码
     */
    public void sendData(byte[] cmdCode) {
        if (manager != null) {
            manager.send(new Data(cmdCode));
        }
    }

    /**
     * 开启心跳
     */
    public void startHeartbeat() {
        if (manager != null) {
            manager.getPulseManager()
                    .setPulseSendable((IPulseSendable) () -> DataOperation.GetSocketSendData(SocketCmdCode.CMD_CODE_HEARTBEAT))//只需要设置一次,下一次可以直接调用pulse()
                    .pulse();//开始心跳,开始心跳后,心跳管理器会自动进行心跳触发
        }
    }

    /**
     * 开启心跳
     */
    public void triggerHeartbeat() {
        if (manager != null) {
            manager.getPulseManager()
                    .setPulseSendable((IPulseSendable) () -> DataOperation.GetSocketSendData(SocketCmdCode.CMD_CODE_HEARTBEAT))//只需要设置一次,下一次可以直接调用pulse()
                    .trigger();//心跳管理器触发一次心跳
        }
    }

    /**
     * 心跳喂狗
     */
    public void heartFeed() {
        if (manager != null) {
            manager.getPulseManager().feed();
        }
    }

    public void closeConnect() {
        if (manager != null) {
            manager.unRegisterReceiver(mSocketActionAdapter);
            manager.disconnect();
        }
    }
}
