package com.jianjia.medicinevendingmachine.constants;

public class NetworkConfiguration {
    private NetworkConfiguration() {
    }

    public static final int PORT = 32112;
    public static final String URL = "newtsf.mnuonet.com";//java测试
    public static final String LOAD_INFO_URL = "http://data.jiyaowang.com.cn/service/GetDeviceConfigInfo";
    public static final String UP_FILE_URL = "http://data.jiyaowang.com.cn/service/DeviceLogReceive";
    public static final String UP_PICTURE_URL = "http://data.jiyaowang.com.cn/service/DeviceCaptureReceive";
    public static final String GET_DEVICE_NEW_EXTENDED_INFORMATION_URL = "http://data.jiyaowang.com.cn/service/advert";
    public static final String GET_DEVICE_NewAPP_INFO_URL = "http://data.jiyaowang.com.cn/service/apk";
    public static final String UP_DATA_INFO_URL = "http://data.jiyaowang.com.cn/service/SyncDeviceLocation";
    public static final String ADVERT_FILE_URL = "http://file.jiyaowang.com.cn";
    public static final String QR_CODE_GM_URL = "https://yiyao.jiyaowang.com.cn/api/getewm.aspx?";
}
