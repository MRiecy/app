package com.jianjia.medicinevendingmachine.dataStore.remoterepository;

import androidx.annotation.NonNull;

public class PrintInfo {
    private int type;
    private PrintText printText;
    private PrintColumnContents printColumnContents;
    private PrintBarcode printBarcode;
    private printQr mPrintQr;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public PrintText getPrintText() {
        return printText;
    }

    public void setPrintText(PrintText printText) {
        this.printText = printText;
    }

    public PrintColumnContents getPrintColumnContents() {
        return printColumnContents;
    }

    public void setPrintColumnContents(PrintColumnContents printColumnContents) {
        this.printColumnContents = printColumnContents;
    }

    public PrintBarcode getPrintBarcode() {
        return printBarcode;
    }

    public void setPrintBarcode(PrintBarcode printBarcode) {
        this.printBarcode = printBarcode;
    }

    public printQr getPrintQr() {
        return mPrintQr;
    }

    public void setPrintQr(printQr printQr) {
        mPrintQr = printQr;
    }


    public static class PrintText {
        private String text;
        private int align_Type;
        private int char_Zoom_Num;
        private int line_spac;

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public int getAlign_Type() {
            return align_Type;
        }

        public void setAlign_Type(int align_Type) {
            this.align_Type = align_Type;
        }

        public int getChar_Zoom_Num() {
            return char_Zoom_Num;
        }

        public void setChar_Zoom_Num(int char_Zoom_Num) {
            this.char_Zoom_Num = char_Zoom_Num;
        }

        public int getLine_spac() {
            return line_spac;
        }

        public void setLine_spac(int line_spac) {
            this.line_spac = line_spac;
        }

        @NonNull
        @Override
        public String toString() {
            return "PrintText{" +
                    "text='" + text + '\'' +
                    ", align_Type=" + align_Type +
                    ", char_Zoom_Num=" + char_Zoom_Num +
                    ", line_spac=" + line_spac +
                    '}';
        }
    }

    public static class PrintColumnContents {
        private String content1;
        private int interval1;
        private String content2;
        private int interval2;
        private String content3;
        private int interval3;
        private String content4;
        private int interval4;

        public String getContent1() {
            return content1;
        }

        public void setContent1(String content1) {
            this.content1 = content1;
        }

        public int getInterval1() {
            return interval1;
        }

        public void setInterval1(int interval1) {
            this.interval1 = interval1;
        }

        public String getContent2() {
            return content2;
        }

        public void setContent2(String content2) {
            this.content2 = content2;
        }

        public int getInterval2() {
            return interval2;
        }

        public void setInterval2(int interval2) {
            this.interval2 = interval2;
        }

        public String getContent3() {
            return content3;
        }

        public void setContent3(String content3) {
            this.content3 = content3;
        }

        public int getInterval3() {
            return interval3;
        }

        public void setInterval3(int interval3) {
            this.interval3 = interval3;
        }

        public String getContent4() {
            return content4;
        }

        public void setContent4(String content4) {
            this.content4 = content4;
        }

        public int getInterval4() {
            return interval4;
        }

        public void setInterval4(int interval4) {
            this.interval4 = interval4;
        }

        @NonNull
        @Override
        public String toString() {
            return "PrintColumnContents{" +
                    "content1='" + content1 + '\'' +
                    ", interval1=" + interval1 +
                    ", content2='" + content2 + '\'' +
                    ", interval2=" + interval2 +
                    ", content3='" + content3 + '\'' +
                    ", interval3=" + interval3 +
                    ", content4='" + content4 + '\'' +
                    ", interval4=" + interval4 +
                    '}';
        }
    }

    public static class PrintBarcode {
        private String str;
        private int br_width;
        private int br_height;
        private int br_type;
        private int nHriFontPosition;
        private int align;

        public String getStr() {
            return str;
        }

        public void setStr(String str) {
            this.str = str;
        }

        public int getBr_width() {
            return br_width;
        }

        public void setBr_width(int br_width) {
            this.br_width = br_width;
        }

        public int getBr_height() {
            return br_height;
        }

        public void setBr_height(int br_height) {
            this.br_height = br_height;
        }

        public int getBr_type() {
            return br_type;
        }

        public void setBr_type(int br_type) {
            this.br_type = br_type;
        }

        public int getnHriFontPosition() {
            return nHriFontPosition;
        }

        public void setnHriFontPosition(int nHriFontPosition) {
            this.nHriFontPosition = nHriFontPosition;
        }

        public int getAlign() {
            return align;
        }

        public void setAlign(int align) {
            this.align = align;
        }

        @NonNull
        @Override
        public String toString() {
            return "PrintBarcode{" +
                    "str='" + str + '\'' +
                    ", br_width=" + br_width +
                    ", br_height=" + br_height +
                    ", br_type=" + br_type +
                    ", nHriFontPosition=" + nHriFontPosition +
                    ", align=" + align +
                    '}';
        }
    }

    public static class printQr {
        private String strqrcode;
        private int qr_width;
        private int align;

        public String getStrqrcode() {
            return strqrcode;
        }

        public void setStrqrcode(String strqrcode) {
            this.strqrcode = strqrcode;
        }

        public int getQr_width() {
            return qr_width;
        }

        public void setQr_width(int qr_width) {
            this.qr_width = qr_width;
        }

        public int getAlign() {
            return align;
        }

        public void setAlign(int align) {
            this.align = align;
        }

        @NonNull
        @Override
        public String toString() {
            return "printQr{" +
                    "strqrcode='" + strqrcode + '\'' +
                    ", qr_width=" + qr_width +
                    ", align=" + align +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "PrintInfo{" +
                "type=" + type +
                ", printText=" + printText +
                ", printColumnContents=" + printColumnContents +
                ", printBarcode=" + printBarcode +
                ", mPrintQr=" + mPrintQr +
                '}';
    }
}
