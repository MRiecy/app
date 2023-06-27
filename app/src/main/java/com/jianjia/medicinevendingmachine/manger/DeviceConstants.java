package com.jianjia.medicinevendingmachine.manger;

public class DeviceConstants {
    private DeviceConstants() {
    }

    public static final String SERIAL_PORT_PATH = "/dev/ttyS1";
    public static final int BAUD_RATE = 115200;
    public static final int PRINTER_STATE_DISCONNECT = 1000;
    public static final int PRINTER_STATE_NO_PAPER = 1001;
    public static final int PRINTER_STAT_ERROR = 1002;
}
