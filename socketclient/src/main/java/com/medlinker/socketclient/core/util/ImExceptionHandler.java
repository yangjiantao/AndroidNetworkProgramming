package com.medlinker.socketclient.core.util;

import android.content.Context;
import android.os.Environment;

import com.qpdstudio.logger.util.LogUtil;


import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Properties;

import com.medlinker.socketclient.core.ImServiceHelper;

public class ImExceptionHandler implements Thread.UncaughtExceptionHandler {
    private static final String TAG = ImExceptionHandler.class.getSimpleName();
    /** 系统默认的UncaughtException处理类 */
//    private Thread.UncaughtExceptionHandler mDefaultHandler;
    /** ExceptionHandler实例 */
    private static ImExceptionHandler mInstance;
    /** 程序的Context对象 */
    private Context mContext;

    // 使用Properties来保存设备的信息和错误堆栈信息
    private Properties mCrashProperties = new Properties();
    private static final String VERSION_NAME = "version_name";
    private static final String VERSION_CODE = "version_code";
    private static final String STACK_TRACE = "stack_trace";
    /** 错误报告文件的扩展名 */
    private static final String CRASH_REPORTER_EXTENSION = ".cr";

    private ImExceptionHandler() {
    }

    public static synchronized ImExceptionHandler getInstance() {
        if (mInstance == null) {
            mInstance = new ImExceptionHandler();
        }
        return mInstance;
    }

    /**
     * 初始化，获取系统默认的异常处理器，设置ExceptionHandler为程序的默认处理器
     * @param context 应用上下文
     */
    public void initialize(Context context) {
        mContext = context.getApplicationContext();
//        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        // crash统计打点
        if (ex != null) {
            ex.printStackTrace();
            // 保存错误报告文件
            saveCrashInfoToFile(ex);
        }
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        LogUtil.i(TAG, "uncaughtException crash recovering");
        ImServiceHelper.getInstance(mContext).reConnect();
    }

    /**
     * 保存错误信息到文件中
     * @param ex 异常信息
     * @return 异常收集文件名称
     */
    private String saveCrashInfoToFile(Throwable ex) {
        Writer info = new StringWriter();
        PrintWriter printWriter = new PrintWriter(info);
        ex.printStackTrace(printWriter);

        Throwable cause = ex.getCause();
        while (cause != null) {
            cause.printStackTrace(printWriter);
            cause = cause.getCause();
        }
        String result = info.toString();
        printWriter.close();
        mCrashProperties.put(STACK_TRACE, result);
        try {
            long timestamp = System.currentTimeMillis();
            final SimpleDateFormat FORMAT = new SimpleDateFormat("yyyy-MM-dd___HH:mm:ss", Locale.CHINA);
            String fileName = "crash-" + FORMAT.format(new Date(timestamp)) + CRASH_REPORTER_EXTENSION;
            File cacheFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath().concat(File.separator).concat("medlinker/log/".concat(fileName)));
            FileOutputStream trace = new FileOutputStream(cacheFile);
			mCrashProperties.store(trace, "trace");
            trace.flush();
            trace.close();
            LogUtil.i(TAG, "crash log write success filepath = %s", cacheFile.getAbsolutePath());
            return fileName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
