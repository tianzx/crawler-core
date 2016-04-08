package org.crawler.core.model;

import org.apache.log4j.Logger;
import org.crawler.core.service.FileDownloadEngineer;
import org.crawler.core.utils.FileDownloadConfig;

public class FileDownloader {

	private Logger logger = Logger.getLogger(FileDownloader.class);

	private FileDownloadEngineer engineer;

	private FileDownloadConfig config;

	private FileDownloader() {
		logger.info("FileDownloader initing");
	}

	private static class FileDownloaderInstance {
		private static final FileDownloader instance = new FileDownloader();
	}

	public static FileDownloader getInstance() {
		return FileDownloaderInstance.instance;
	}

	public synchronized void init(FileDownloadConfig config) {

		if (null == config) {
			throw new IllegalArgumentException(
					"FileDownload is not init rightly");
		}
		if (null == this.config) {
			logger.info("init downloader");
			engineer = new FileDownloadEngineer(config);
			this.config = config;
		} else {
			logger.info("FileDownloader has been inited");
		}
	}

	public boolean download(String url, String filename) {

		return engineer.download(url, filename);
	}

	public boolean isInited() {
		return this.config != null;
	}

	public void destroy() {
		if (engineer != null) {
			engineer.close();
			engineer = null;
		}
	}
}
