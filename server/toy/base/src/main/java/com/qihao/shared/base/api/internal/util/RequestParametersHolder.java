package com.qihao.shared.base.api.internal.util;

public class RequestParametersHolder {
    private GeneralHashMap protocalMustParams;
    private GeneralHashMap protocalOptParams;
    private GeneralHashMap applicationParams;

    public GeneralHashMap getProtocalMustParams() {
        return protocalMustParams;
    }

    public void setProtocalMustParams(GeneralHashMap protocalMustParams) {
        this.protocalMustParams = protocalMustParams;
    }

    public GeneralHashMap getProtocalOptParams() {
        return protocalOptParams;
    }

    public void setProtocalOptParams(GeneralHashMap protocalOptParams) {
        this.protocalOptParams = protocalOptParams;
    }

    public GeneralHashMap getApplicationParams() {
        return applicationParams;
    }

    public void setApplicationParams(GeneralHashMap applicationParams) {
        this.applicationParams = applicationParams;
    }

}
