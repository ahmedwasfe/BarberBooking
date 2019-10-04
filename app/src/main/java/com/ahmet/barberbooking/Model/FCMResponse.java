package com.ahmet.barberbooking.Model;

import java.util.List;

public class FCMResponse {

    private long multicastId;
    private int success;
    private int failure;
    private int canonicalIds;
    private List<Result> mListResult;

    public FCMResponse() {}

    public long getMulticastId() {
        return multicastId;
    }

    public void setMulticastId(long multicastId) {
        this.multicastId = multicastId;
    }

    public int getSuccess() {
        return success;
    }

    public void setSuccess(int success) {
        this.success = success;
    }

    public int getFailure() {
        return failure;
    }

    public void setFailure(int failure) {
        this.failure = failure;
    }

    public int getCanonicalIds() {
        return canonicalIds;
    }

    public void setCanonicalIds(int canonicalIds) {
        this.canonicalIds = canonicalIds;
    }

    public List<Result> getmListResult() {
        return mListResult;
    }

    public void setmListResult(List<Result> mListResult) {
        this.mListResult = mListResult;
    }
}

class Result{

    private String messageId;

    public Result() {}

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }
}
