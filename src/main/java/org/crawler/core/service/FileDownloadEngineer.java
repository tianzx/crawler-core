package org.crawler.core.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.BitSet;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;
import org.crawler.core.utils.FileDownloadConfig;

public class FileDownloadEngineer {

	private Logger logger = Logger.getLogger(FileDownloadEngineer.class);
	
	private FileDownloadConfig config;
	private Executors pool;
	private int coreThreadNum;
	private HttpRequestImpl httpRequestImpl;
	private File downloadDestinationDir;
	
	public FileDownloadEngineer(FileDownloadConfig config) {
		this.config = config;
		this.coreThreadNum = config.getCoreThreadNum();
		this.httpRequestImpl = new HttpRequestImpl(this.config);
		this.pool = (Executors) Executors.newFixedThreadPool(this.config.getCoreThreadNum());
		
		this.downloadDestinationDir = this.config.getDownloadDestinationDir();
		if(!this.downloadDestinationDir.exists()){
			this.downloadDestinationDir.mkdirs();
		}
	}

	public boolean download(String url, String filename) {
		long start_time = System.currentTimeMillis();
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
		int i = 0;
		for(;i<coreThreadNum;i++) {
			
		}
		return false;
	}

	public void close() {
		
	}
	
}
