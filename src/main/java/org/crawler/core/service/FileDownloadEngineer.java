package org.crawler.core.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.BitSet;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;
import org.crawler.core.service.Worker.DownloadListener;
import org.crawler.core.utils.FileDownloadConfig;

public class FileDownloadEngineer {

	private Logger logger = Logger.getLogger(FileDownloadEngineer.class);
	
	private FileDownloadConfig config;
	private ExecutorService pool;
	private int coreThreadNum;
	private HttpRequestImpl httpRequestImpl;
	private File downloadDestinationDir;
	
	public FileDownloadEngineer(FileDownloadConfig config) {
		this.config = config;
		this.coreThreadNum = config.getCoreThreadNum();
		this.httpRequestImpl = new HttpRequestImpl(this.config);
		this.pool =  Executors.newFixedThreadPool(this.config.getCoreThreadNum());
		
		this.downloadDestinationDir = this.config.getDownloadDestinationDir();
		if(!this.downloadDestinationDir.exists()){
			this.downloadDestinationDir.mkdirs();
		}
	}

	public boolean download(String url, String filename) {
		long start_time = System.currentTimeMillis();
//		logger.info("開啓時間:"+start_time);
		logger.info("開始下載 .....");
		long total_file_len = httpRequestImpl.getFileSize(url);
		if(total_file_len<1) {
			logger.warn("獲取文件大小失敗"+url+",filename"+filename);
			return false;
		}
		final BitSet downloadIndicatorBitSet = new BitSet(coreThreadNum);
		File file = null;
		
		file = new File(downloadDestinationDir,filename);
		try {
			RandomAccessFile raf = new RandomAccessFile(file, "rws");
			raf.setLength(total_file_len);
			raf.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		CountDownLatch latch = new CountDownLatch(coreThreadNum);//两个工人的协作  
		long thread_download_len = (total_file_len + coreThreadNum - 1) / coreThreadNum;	
		int i = 0;
		for(;i<coreThreadNum;i++) {
			DownloadWorker worker = new DownloadWorker(i,url,thread_download_len,file,httpRequestImpl,latch);
			worker.addListener(new DownloadListener() {
				
				@Override
				public void notify(int threadId, String url, long start,
						long end, boolean result, String msg) {
					modifyState(downloadIndicatorBitSet, threadId);
				}
			});
			pool.execute(worker);
		}
		try {
			latch.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		logger.info("下载结束,url:"+url+",耗时:"+((System.currentTimeMillis()-start_time)/1000)+"(s)");
		return downloadIndicatorBitSet.cardinality()==coreThreadNum;
	}
	
	private synchronized void modifyState(BitSet bitSet, int index){
		bitSet.set(index);
	}
	
	public void close() {
		if(httpRequestImpl!=null){
			httpRequestImpl.close();
			httpRequestImpl = null;
		}
		if(pool!=null){
			pool.shutdown();
			pool = null;
		}
		
	}
	
}
