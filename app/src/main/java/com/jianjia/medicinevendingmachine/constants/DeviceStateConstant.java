package com.jianjia.medicinevendingmachine.constants;

public class DeviceStateConstant {
    private DeviceStateConstant() {
    }

    public static final int DEVICE_NORMAL = 0;
    public static final int DEVICE_PROCESSING = 1;
    public static final int DEVICE_NO_ORDER = 2;
    public static final int DEVICE_ORDER_ERROR = 3;
    public static final int DEVICE_OUTING_GOODS = 4;
    public static final int DEVICE_OUT_GOODS_FAIL = 5;
    public static final int DEVICE_OUT_GOODS_PART_FAIL = 6;
    public static final int DEVICE_OUT_GOODS_SUCCESSFUL = 7;
}