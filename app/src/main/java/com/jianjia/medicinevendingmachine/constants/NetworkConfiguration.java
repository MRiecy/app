package com.jianjia.medicinevendingmachine.constants;

public class NetworkConfiguration {
    private NetworkConfiguration() {
    }

    //java正式
    public static final int PORT = 32112;
    public static final String URL = "newtsf.mnuonet.com";//java测试
    public static final String LOAD_INFO_URL = "http://data.mnuonet.cn/service/GetDeviceConfigInfo";
    public static final String UP_FILE_URL = "http://data.mnuonet.cn/service/DeviceLogReceive";
    public static final String UP_PICTURE_URL = "http://data.mnuonet.cn/service/DeviceCaptureReceive";
    //  public static final String GET_DEVICE_EXTENDED_INFORMATION_URL = "http://data.mnuonet.cn/service/queryAdvert";
    public static final String GET_DEVICE_NEW_EXTENDED_INFORMATION_URL = "http://data.mnuonet.cn/service/advert";
    public static final String GET_DEVICE_NewAPP_INFO_URL = "http://data.mnuonet.cn/service/apk";
    public static final String UP_DATA_INFO_URL = "http://data.mnuonet.cn/service/SyncDeviceLocation";
    //  public static final String ADVERT_FILE_URL = "http://file.mnuonet.cn";
   // public static final String ADVERT_FILE_URL = "http://file.healthy-home.com.cn";
    public static final String ADVERT_FILE_URL = "http://file.jiyaowang.com.cn";
    //public static final String QR_CODE_GM_URL = "http://mall.mnuonet.com/goods?no=";
   // public static final String QR_CODE_GM_URL = "http://mall.healthy-home.com.cn/goods?no=";
    public static final String QR_CODE_GM_URL = "https://yiyao.jiyaowang.com.cn/api/getewm.aspx?";
}
