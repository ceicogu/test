package com.qihao.toy.web.valve;

import com.alibaba.citrus.service.pipeline.PipelineContext;
import com.alibaba.citrus.service.pipeline.Valve;

public class MyValve implements Valve {

	public void invoke(PipelineContext pipelineContext) throws Exception {
        System.out.println("valve started.");
        pipelineContext.invokeNext(); // 调用后序 valves
        System.out.println("valve ended.");
	}

}
