package com.medlinker.socketclient.core.util;

/**
 * 默认重试5次。
 * Created by jiantao on 2017/3/8.
 */

public class DefaultRetryPolicy implements RetryPolicy {

    /**
     * The current retry count.
     */
    private int mCurrentRetryCount;

    /**
     * The maximum number of attempts.
     */
    private final int mMaxNumRetries;

    /**
     * The default number of retries
     */
    public static final int DEFAULT_MAX_RETRIES = 5;


    /**
     * Constructs a new retry policy using the default timeouts.
     */
    public DefaultRetryPolicy() {
        this(DEFAULT_MAX_RETRIES);
    }

    /**
     * Constructs a new retry policy.
     *
     * @param maxNumRetries     The maximum number of retries.
     */
    public DefaultRetryPolicy( int maxNumRetries) {
        mMaxNumRetries = maxNumRetries;
    }
    @Override
    public int getCurrentRetryCount() {
        return mCurrentRetryCount;
    }

    @Override
    public void retry(Exception ex) throws Exception {
        mCurrentRetryCount++;
        if (!hasAttemptRemaining()) {
            throw ex;
        }
    }

    /**
     * 重置当前重试次数
     */
    @Override
    public void reset(){
        this.mCurrentRetryCount = 0;
    }
    /**
     * Returns true if this policy has attempts remaining, false otherwise.
     */
    protected boolean hasAttemptRemaining() {
        return mCurrentRetryCount <= mMaxNumRetries;
    }
}
