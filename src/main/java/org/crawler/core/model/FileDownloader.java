package org.crawler.core.model;

import org.apache.log4j.Logger;

public class FileDownloader {

	private Logger logger = Logger.getLogger(FileDownloader.class);
	
	private FileDownloader(){
		logger.info("FileDownloader initing");
	}
	
	private static class FileDownloaderInstance {
		private static final FileDownloader instance = new FileDownloader();
	}
	
	public static FileDownloader getInstance() {
		return FileDownloaderInstance.instance;
	}
	
	
}

