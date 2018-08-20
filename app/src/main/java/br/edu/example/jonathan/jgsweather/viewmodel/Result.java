package br.edu.example.jonathan.jgsweather.viewmodel;

import android.support.annotation.Nullable;

public class Result<Data> {

    private SourceType mSourceType;

    private MessageLevel mMessageLevel = MessageLevel.DEFAULT;

    private boolean mSuccess;

    private String mMessage;

    private Data mData;

    public boolean isSuccess() {
        return mSuccess;
    }

    public void setSuccess(boolean success) {
        mSuccess = success;
    }

    public String getMessage() {
        return mMessage;
    }

    public void setMessage(String message) {
        mMessage = mMessage;
    }

    @Nullable
    public Data getData() {
        return mData;
    }

    public void setData(@Nullable Data data) {
        this.mData = data;
    }

    public void setSourceNetwork() {
        mSourceType = SourceType.NETWORK;
    }

    public void setSourceDatabase() {
        mSourceType = SourceType.DATABASE;
    }

    public void setMessageLevelWarning() {
        mMessageLevel = MessageLevel.WARNING;
    }

    public void setMessageLevelError() {
        mMessageLevel = MessageLevel.ERROR;
    }

    public boolean isError() {
        return mMessageLevel == MessageLevel.ERROR;
    }

    public boolean isWarning() {
        return mMessageLevel == MessageLevel.WARNING;
    }

    public boolean isNetworkSource() {
        return SourceType.NETWORK.equals(mSourceType);
    }

    public boolean isDatabaseSource() {
        return SourceType.DATABASE.equals(mSourceType);
    }

    public boolean hasData() {
        return mData != null;
    }

    private enum SourceType {

        NETWORK, DATABASE

    }

    private enum MessageLevel {

        DEFAULT, WARNING, ERROR

    }

}
