package org.crawler.core.service;

public abstract class Worker implements Runnable{

	protected DownloadListener listener;
	
	public void addListener(DownloadListener listener){
		this.listener = listener;
	}
	public interface DownloadListener {
		public void notify(int threadId,String url,long start,long end,boolean result,String msg);
	}
}
