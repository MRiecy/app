package com.jianjia.medicinevendingmachine.manger;

import android.content.Context;
import android.util.Log;

import com.elvishew.xlog.XLog;
import com.jianjia.medicinevendingmachine.dataStore.remoterepository.PrintInfo;
import com.tim.serialportlib.OnDataListener;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.qualifiers.ApplicationContext;

public class DeviceManger {
    private final String TAG = "DeviceManger";
    Printer printer;
    QR qr;
    Device device;
    DeviceSystemManger deviceSystemManger;
    Context context;
    private boolean isInit = false;

    @Inject
    public DeviceManger(@ApplicationContext Context context, DeviceSystemManger deviceSystemManger, Device device, Printer printer, QR qr) {
        Log.i(TAG, "初始化");
        this.context = context;
        this.deviceSystemManger = deviceSystemManger;
        this.device = device;
        this.printer = printer;
        this.qr = qr;
    }

    public void init() {
        deviceSystemManger.init(() -> {
            XLog.tag(TAG).i("系统服务连接成功");
            isInit = true;
            deviceSystemManger.hideNavBar(true);
            deviceSystemManger.hideStatusBar(false);
            deviceSystemManger.daemon(context.getPackageName(), 0);
            deviceSystemManger.selfStart(context.getPackageName());
        });
        device.init();
        printer.init();
        /*qr.init(context);
        qr.startScan();*/
    }

    public void initLog() {
        deviceSystemManger.startLog();
    }

    public void shutdown() {
        if (isInit) {
            XLog.tag(TAG).i("关机");
            deviceSystemManger.shutdown();
        } else {
            XLog.tag(TAG).i("系统服务未连接");
        }
    }

    public void reboot() {
        if (isInit) {
            XLog.tag(TAG).i("重启");
            deviceSystemManger.reboot();
        } else {
            XLog.tag(TAG).i("系统服务未连接");
        }
    }

    public boolean takeScreenshot(String path) {
        if (isInit) {
            XLog.tag(TAG).i("截屏：" + path);
            return deviceSystemManger.takeScreenshot(path);
        } else {
            XLog.tag(TAG).i("系统服务未连接");
            return false;
        }
    }

    public boolean silentInstallApk(String apkPath) {
        if (isInit) {
            XLog.tag(TAG).i("静默安装程序：" + apkPath);
            return deviceSystemManger.silentInstallApk(apkPath);
        } else {
            XLog.tag(TAG).i("系统服务未连接");
            return false;
        }
    }

    public boolean installApk(String apkPath) {
        XLog.tag(TAG).i("静默安装程序：" + apkPath);
        return deviceSystemManger.installApk(apkPath);
    }

    public boolean unInstallApk(String packageName) {
        if (isInit) {
            XLog.tag(TAG).i("静默卸载程序：" + packageName);
            return deviceSystemManger.unInstallApk(packageName);
        } else {
            XLog.tag(TAG).i("系统服务未连接");
            return false;
        }
    }

    public void changeVolume(int volume) {
        XLog.tag(TAG).i("调整音量");
        deviceSystemManger.changeVolume(volume);
    }

    public void shoppingGoods(double IRC, double iCC, int type) {
        XLog.tag(TAG).i("出货：" + IRC + "," + iCC + "," + type);
        if (device.isOpenDevice()) {
            device.outGoods(IRC, iCC, type);
        } else {
            XLog.tag(TAG).i("串口未打开");
        }
    }

    public void getTemperatureAndHumidity() {
        XLog.tag(TAG).i("获取温湿度");
        if (device.isOpenDevice()) {
            device.getTemperatureAndHumidity();
        } else {
            XLog.tag(TAG).i("串口未打开");
        }
    }

    public void addDeviceDataListener(OnDataListener onDataListener) {
        device.addListener(onDataListener);
    }

    public int printTicks(List<PrintInfo> printInfoList) {
        if (printer.isConnect()) {
            XLog.tag(TAG).i("打印机连接");
            int status = printer.getStatus();
            if ((status & 0x400) > 0 || (status & 0x08) > 0) {
                XLog.tag(TAG).i("缺纸 ");
                return DeviceConstants.PRINTER_STATE_NO_PAPER;
            } else if (status == -1) {
                XLog.tag(TAG).i("打印机数据故障");
            } else if ((status & 0x4) > 0) {
                XLog.tag(TAG).i("打印机故障");
                return DeviceConstants.PRINTER_STAT_ERROR;
            } else {
                for (PrintInfo printInfo : printInfoList) {
                    int type = printInfo.getType();
                    if (type == 1) {
                        PrintInfo.PrintText printText = printInfo.getPrintText();
                        String text = printText.getText();
                        int align_type = printText.getAlign_Type();
                        int char_zoom_num = printText.getChar_Zoom_Num() - 1;
                        int line_spac = printText.getLine_spac() + 10;
                        printer.setAlignMode(align_type);
                        printer.setLineSpace(line_spac);
                        printer.setCharSize(char_zoom_num, char_zoom_num);
                        printer.printString(text);
                    } else if (type == 2) {
                        PrintInfo.PrintBarcode printBarcode = printInfo.getPrintBarcode();
                        int br_width = printBarcode.getBr_width();
                        int br_height = printBarcode.getBr_height();
                        String str = printBarcode.getStr();
                        int align = printBarcode.getAlign();
                        printer.setAlignMode(align);
                        printer.printBarCode(br_width, br_height, str);
                    } else if (type == 3) {
                        PrintInfo.printQr printQr = printInfo.getPrintQr();
                        int qr_width = printQr.getQr_width();
                        int align = printQr.getAlign();
                        String strQrcode = printQr.getStrqrcode();
                        printer.setAlignMode(align);
                        printer.printQRCode(strQrcode, qr_width, false);
                    } else if (type == 6) {
                        PrintInfo.PrintColumnContents printColumnContents = printInfo.getPrintColumnContents();
                        printer.setCharSize(0, 0);
                        printer.setAlignMode(1);
                        String text1 = printColumnContents.getContent1();
                        printer.printString(text1 + "\t");
                        String text2 = printColumnContents.getContent2();
                        printer.printString(text2 + "\t");
                        String text3 = printColumnContents.getContent3();
                        printer.printString(text3 + "\t");
                        String text4 = printColumnContents.getContent4();
                        printer.printString(text4);
                    }
                    printer.printFeed();
                }
                printer.fullCutPaper();
            }
        } else {
            XLog.tag(TAG).i("打印机未连接");
            return DeviceConstants.PRINTER_STATE_DISCONNECT;
        }
        return 0;
    }

    public void addScanListener() {
        qr.addDataListener(code -> XLog.tag(TAG).i("扫码结果：" + code));
    }

    public void closeDevice() {
        if (device.isOpenDevice()) {
            device.closeDevice();
        }
        if (printer.isConnect()) {
            printer.close();
        }
        if (deviceSystemManger != null && isInit) {
            deviceSystemManger.closeSystemManger();
        }
    }
}
