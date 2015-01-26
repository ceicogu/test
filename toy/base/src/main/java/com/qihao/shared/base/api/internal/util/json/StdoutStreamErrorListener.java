package com.qihao.shared.base.api.internal.util.json;

public class StdoutStreamErrorListener extends BufferErrorListener {

    public void end() {
        System.out.print(buffer.toString());
    }
}
