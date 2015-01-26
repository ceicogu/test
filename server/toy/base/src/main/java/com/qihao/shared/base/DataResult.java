package com.qihao.shared.base;

public class DataResult<T> extends SimpleResult {

    private static final long serialVersionUID = 1L;
    private T                 data;

    /**
     * @return the data
     */
    public T getData() {
        return data;
    }

    /**
     * @param data the data to set
     */
    public void setData(T data) {
        this.data = data;
    }

}
