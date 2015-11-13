package com.ucpaas.im.api.executor;

public interface HttpMethodExecutor {
	public abstract String post(String conType,String authorization, String url, String content);
	public abstract String get(String conType,String authorization, String url, String content);
}
