package org.crawler.core.service;

import java.io.File;
import java.util.concurrent.CountDownLatch;

import org.apache.log4j.Logger;


public class DownloadWorker extends Worker{

	private Logger logger = Logger.getLogger(DownloadWorker.class);
	
	private int id;
	private String url;
	private File file;
	private long thread_download_len;
	
	private CountDownLatch latch;
	
	private HttpRequestImpl httpRequestImpl;
	
	public DownloadWorker(int id,String url,long thread_download_len,File file,HttpRequestImpl httpRequestImpl,CountDownLatch latch) {
		this.id = id;
		this.url = url;
		this.thread_download_len = thread_download_len;
		this.file = file;
		this.httpRequestImpl = httpRequestImpl;
		this.latch = latch;
	}
	@Override
	public void run() {
		long start = id * thread_download_len;
		long end = id * thread_download_len + thread_download_len -1;
		logger.info("线程:" + id +" 开始下载 url:"+url+ ",range:" + start + "-" + end);
		boolean result = false;
		httpRequestImpl.downloadPartFile(id, url, file, start, end);
		if(null!=listener) {
			listener.notify(id,url,start,end,result,"");
		}
		latch.countDown();
	}

}
