package com.jianjia.medicinevendingmachine.dataStore.remoterepository;

public class SocketCmdCode {
    private SocketCmdCode() {
    }

    /**
     * 通讯协议发送包头
     */
    public final static byte[] CMD_CODE_SEND_HEAD = {(byte) 0xFF, (byte) 0xAB};
    /**
     * 通讯协议收到包头
     */
    public final static byte[] CMD_CODE_RED_HEAD = {(byte) 0xFF, (byte) 0xBA};
    /**
     * 通讯协议包尾
     */
    public final static byte[] CMD_CODE_END = {(byte) 0xFF};
    /**
     * 通讯包长占用字节长度
     */
    public final static int PACKAGE_BYTE_LEN = 4;
    /**
     * 设备类型：药柜
     */
    public final static byte[] DEV_TYPE_MEDICINE_CABINET = {0x01};
    /**
     * 设备类型：彩票机
     */
    public final static byte[] DEV_TYPE_LOTTERY_MACHINE = {0x02};
    /**
     * 设备类型：酒柜
     */
    public final static byte[] DEV_TYPE_WINE_CABINET = {0x03};

    //-------------------------------------以下是设备端主动请求服务端-------------------------------------------------------//
    /**
     * 通讯协议命令码：签到
     */

    public final static byte[] CMD_CODE_SIGN_IN = {0x00, 0x01};
    /**
     * 通讯协议命令码：心跳
     */
    public final static byte[] CMD_CODE_HEARTBEAT = {0x00, 0x02};
    /**
     * 通讯协议命令码：获取广告
     */
    public final static byte[] CMD_CODE_ADVERTISING = {0x00, 0x05};
    /**
     * 通讯协议命令码：出货
     */
    public final static byte[] CMD_CODE_OUT_GOODS = {0x00, 0x0A};
    /**
     * 通讯协议命令码：出货结果
     */
    public final static byte[] CMD_CODE_OUT_GOODS_RESULT = {0x00, 0x0B};
    /**
     * 通讯协议命令码：获取机柜版本号
     */
    public final static byte[] CMD_CODE_GET_APP_VERSION = {0x00, 0x0C};
    /**
     * 通讯协议命令码：设备同步时间请求
     */
    public final static byte[] CMD_CODE_SYN_TIME = {0x00, 0x12};
    /**
     * 通讯协议命令码：配置机柜信息提交请求
     */
    public final static byte[] CMD_CODE_SET_CONFIG_INFO = {0x00, 0x13};
    /**
     * 通讯协议命令码：获取机柜信息请求
     */
    public final static byte[] CMD_CODE_GET_CONFIG_INFO = {0x00, 0x14};
    /**
     * 通讯协议命令码：发送机柜故障
     */
    public final static byte[] CMD_CODE_EQUIPMENT_FAILURE = {0x00, 0x15};
    /**
     * 通讯协议命令码：温湿度 TemperatureAndHumidity
     */
    public final static byte[] CMD_CODE_DEVICE_TEMPERATURE_HUMIDITY = {0x00, 0x16};
    /**
     * 通讯协议命令码：设备信息上传
     */
    public final static byte[] CMD_CODE_UP_DEVICE_MESSAGE = {0x00, 0x17};
    //-------------------------------------以下是服务端主动推送给设备-------------------------------------------------------//
    /**
     * 通讯协议命令码：2.0推板归位
     */
    public final static byte[] CMD_CODE_BOARD_REVERT = {(byte) 0xA0, 0x01};
    /**
     * 通讯协议命令码：发起屏幕截屏
     */
    public final static byte[] CMD_CODE_SCREEN_CAPTURE = {(byte) 0xA0, 0x02};
    /**
     * 通讯协议命令码：发起监控截图
     */
    public final static byte[] CMD_CODE_MONITOR_CAPTURE = {(byte) 0xA0, 0x03};
    /**
     * 通讯协议命令码：通知下载广告程序
     */
    public final static byte[] CMD_CODE_ADVERT_DOWNLOAD = {(byte) 0xA0, 0x04};
    /**
     * 通讯协议命令码：通知下载轮播图
     */
    public final static byte[] CMD_CODE_BANNER_DOWNLOAD = {(byte) 0xA0, 0x05};
    /**
     * 通讯协议命令码：设备重启
     */
    public final static byte[] CMD_CODE_RESTART_SYSTEM = {(byte) 0xA0, 0x06};
    /**
     * 通讯协议命令码：设备关机
     */
    public final static byte[] CMD_CODE_SHUTDOWN = {(byte) 0xA0, 0x07};
    /**
     * 通讯协议命令码：发起异常检测
     */
    public final static byte[] CMD_CODE_FAULT_CHECK = {(byte) 0xA0, 0x08};
    /**
     * 通讯协议命令码：打印请求给设备(原33)
     */
    public final static byte[] CMD_CODE_DEVICE_PRINT_TICKET = {(byte) 0xA0, 0x09};
    /**
     * 通讯协议命令码：获取设备发送日志通知
     */
    public final static byte[] CMD_CODE_DOWNLOAD_DEVICE_LOGS = {(byte) 0xA0, 0x0A};
    /**
     * 通讯协议命令码：语音本文推送
     */
    public final static byte[] CMD_CODE_DEVICE_MESSAGE_PUSH = {(byte) 0xA0, 0x0B};
    /**
     * 通讯协议命令码：通知下载Apk固件
     */
    public final static byte[] CMD_CODE_DEVICE_DOWNLOAD_APK = {(byte) 0xA0, 0x0C};

    /**
     * 通讯协议命令码：通知更新配置信息
     */
    public final static byte[] CMD_CODE_DEVICE_SYNC_CONFIG_INFO = {(byte) 0xA0, 0x0D};

    /**
     * 通讯协议命令码：通知开启货柜门
     */
    public final static byte[] CMD_CODE_DEVICE_OPEN_DOOR = {(byte) 0xA0, 0x0E};

    /**
     * 通讯协议命令码：调整设备音量
     */
    public final static byte[] CMD_CODE_CHANGE_SOUND = {(byte) 0xA0, 0x0F};

    /**
     * 通讯协议命令码：温湿度显示
     */
    public final static byte[] CMD_CODE_TEMPERATURE_HUMIDITY_STATE = {(byte) 0xA0, 0x10};

}
