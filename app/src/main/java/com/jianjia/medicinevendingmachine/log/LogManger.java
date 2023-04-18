package com.jianjia.medicinevendingmachine.log;

import android.content.Context;
import android.os.Environment;

import com.elvishew.xlog.LogConfiguration;
import com.elvishew.xlog.XLog;
import com.elvishew.xlog.flattener.PatternFlattener;
import com.elvishew.xlog.printer.AndroidPrinter;
import com.elvishew.xlog.printer.Printer;
import com.elvishew.xlog.printer.file.FilePrinter;
import com.elvishew.xlog.printer.file.backup.NeverBackupStrategy;
import com.elvishew.xlog.printer.file.clean.FileLastModifiedCleanStrategy;
import com.jianjia.medicinevendingmachine.constants.FilePathConstant;

import javax.inject.Inject;

import dagger.hilt.android.qualifiers.ApplicationContext;

public class LogManger {
    private static final String TAG = "LogManger";
    private static final long MAX_TIME = 432000000L;
    private Context mContext;

    @Inject
    public LogManger(@ApplicationContext Context context) {
        this.mContext = context;
    }

    public void init() {
        LogConfiguration config = new LogConfiguration.Builder().build();
        Printer androidPrinter = new AndroidPrinter();
        String path = mContext.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).getPath()+"/log";
        Printer filePrinter = new FilePrinter
                .Builder(FilePathConstant.LOG_FILE_PATH)
                .fileNameGenerator(new MyDateFileNamesGenerator())
                .backupStrategy(new NeverBackupStrategy())
                .cleanStrategy(new FileLastModifiedCleanStrategy(MAX_TIME))
                .flattener(new PatternFlattener("{d} {l} {t} {m}"))
                .build();
        XLog.init(config, androidPrinter, filePrinter);
    }
}
