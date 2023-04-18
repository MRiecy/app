package com.jianjia.medicinevendingmachine.log;

import com.elvishew.xlog.printer.file.naming.FileNameGenerator;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;


public class MyDateFileNamesGenerator implements FileNameGenerator {

    ThreadLocal<SimpleDateFormat> mLocalDateFormat = new ThreadLocal<>() {
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        }
    };

    @Override
    public boolean isFileNameChangeable() {
        return true;
    }

    /**
     * Generate a file name which represent a specific date.
     */
    @Override
    public String generateFileName(int logLevel, long timestamp) {
        SimpleDateFormat sdf = mLocalDateFormat.get();
        assert sdf != null;
        sdf.setTimeZone(TimeZone.getDefault());
        return sdf.format(new Date(timestamp)) + ".txt";
    }
}
