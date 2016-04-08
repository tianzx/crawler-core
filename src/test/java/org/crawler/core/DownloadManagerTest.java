package org.crawler.core;

import java.io.File;

import org.crawler.core.model.FileDownloader;
import org.crawler.core.utils.FileDownloadConfig;

public class DownloadManagerTest {

public static void main(String[] args) {
		
		String url = "http://dldir1.qq.com/qqfile/qq/QQ7.9/16621/QQ7.9.exe";
		String filename = "QQ7.9.exe";
		
		FileDownloader fileDownloader = FileDownloader.getInstance();
		FileDownloadConfig configuration = FileDownloadConfig
				.custom()
				.coreThreadNum(5)
				.maxRetryCount(3)
				.build();
		fileDownloader.init(configuration);
		
		boolean success = fileDownloader.download(url, filename);
		System.out.println("download result:" + success);
		
		//关闭线程池
		fileDownloader.destroy();
	}
}
